package babysteps.core;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Disjoint union of two values represented by {@code Left} or {@code Right}.
 *
 * <p>{@code Left} values may be {@code null}, while {@code Right} values may be {@code null}. The
 * API treats {@code Right} as the "success" side when bridging to {@link Result} or {@link Option}.
 *
 * <p>{@code Either} is a symmetric sum type. If you want APIs that assume a success/error workflow,
 * use {@link Result} instead. {@code Result} offers helpers like unwrap/orElse/recover that align
 * with error handling.
 *
 * @param <L> left value type
 * @param <R> right value type
 */
public sealed interface Either<L, R> permits Either.Left, Either.Right {
  /**
   * Creates a {@code Left} value.
   *
   * @param value the left value, possibly {@code null}
   * @param <L> left value type
   * @param <R> right value type
   * @return a {@code Left}
   */
  static <L, R> @NonNull Either<L, R> left(@Nullable L value) {
    return new Left<>(value);
  }

  /**
   * Creates a {@code Right} value.
   *
   * @param value the right value, possibly {@code null}
   * @param <L> left value type
   * @param <R> right value type
   * @return a {@code Right}
   */
  static <L, R> @NonNull Either<L, R> right(@Nullable R value) {
    return new Right<>(value);
  }

  /**
   * Converts a {@link Result} into an {@code Either} by treating {@code Ok} as {@code Right}.
   *
   * @param result result to convert
   * @param <L> left value type
   * @param <R> right value type
   * @return {@code Right} for {@code Ok}, otherwise {@code Left}
   * @throws NullPointerException if {@code result} is {@code null}
   */
  static <L, R> @NonNull Either<L, R> fromResult(@NonNull Result<? extends R, ? extends L> result) {
    Objects.requireNonNull(result, "result");
    if (result.isOk()) {
      return right(result.unwrap());
    }
    return left(result.unwrapErr());
  }

  /**
   * @return {@code true} when this is {@code Right}
   */
  boolean isRight();

  /**
   * @return {@code true} when this is {@code Left}
   */
  default boolean isLeft() {
    return !isRight();
  }

  /**
   * Returns the left value.
   *
   * @return the left value, possibly {@code null}
   * @throws IllegalStateException if this is {@code Right}
   */
  @Nullable L unwrapLeft();

  /**
   * Returns the right value.
   *
   * @return the right value, possibly {@code null}
   * @throws IllegalStateException if this is {@code Left}
   */
  @Nullable R unwrapRight();

  /**
   * Returns the right value or {@code fallback} when this is {@code Left}.
   *
   * @param fallback fallback right value
   * @return the right value or {@code fallback}, possibly {@code null}
   */
  default @Nullable R unwrapRightOr(@Nullable R fallback) {
    return isRight() ? unwrapRight() : fallback;
  }

  /**
   * Returns the right value or a supplied fallback when this is {@code Left}.
   *
   * @param fallback fallback supplier
   * @return the right value or the supplier result, possibly {@code null}
   * @throws NullPointerException if {@code fallback} is {@code null}
   */
  default @Nullable R unwrapRightOrElse(@NonNull Supplier<? extends @Nullable R> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    return isRight() ? unwrapRight() : fallback.get();
  }

  /**
   * Returns the left value or {@code fallback} when this is {@code Right}.
   *
   * @param fallback fallback left value
   * @return the left value or {@code fallback}
   */
  default @Nullable L unwrapLeftOr(@Nullable L fallback) {
    return isLeft() ? unwrapLeft() : fallback;
  }

  /**
   * Returns the left value or a supplied fallback when this is {@code Right}.
   *
   * @param fallback fallback supplier
   * @return the left value or the supplier result
   * @throws NullPointerException if {@code fallback} is {@code null}
   */
  default @Nullable L unwrapLeftOrElse(@NonNull Supplier<? extends @Nullable L> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    return isLeft() ? unwrapLeft() : fallback.get();
  }

  /**
   * Returns this {@code Right}, or {@code fallback} when this is {@code Left}.
   *
   * @param fallback the fallback {@code Either}
   * @return this either for {@code Right}, otherwise {@code fallback}
   * @throws NullPointerException if {@code fallback} is {@code null}
   */
  default Either<L, R> orElse(@NonNull Either<? extends L, ? extends R> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    if (isRight()) {
      return this;
    }
    @SuppressWarnings("unchecked")
    final var other = (Either<L, R>) fallback;
    return other;
  }

  /**
   * Returns this {@code Right}, or a supplied {@code Either} when this is {@code Left}.
   *
   * @param fallback the supplier for the fallback either
   * @return this either for {@code Right}, otherwise the supplied either
   * @throws NullPointerException if {@code fallback} or its result is {@code null}
   */
  default Either<L, R> orElseGet(
      @NonNull Supplier<? extends Either<? extends L, ? extends R>> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    if (isRight()) {
      return this;
    }
    @SuppressWarnings("unchecked")
    final var other = (Either<L, R>) Objects.requireNonNull(fallback.get(), "result");
    return other;
  }

  /**
   * Returns the right value or throws a supplied exception when this is {@code Left}.
   *
   * @param exceptionSupplier supplier for exception
   * @param <X> exception type
   * @return the right value, possibly {@code null}
   * @throws X if this either is {@code Left}
   * @throws NullPointerException if {@code exceptionSupplier} is {@code null}
   */
  default <X extends Throwable> @Nullable R orElseThrow(
      @NonNull Supplier<? extends X> exceptionSupplier) throws X {
    Objects.requireNonNull(exceptionSupplier, "exceptionSupplier");
    if (isLeft()) {
      final var exception = Objects.requireNonNull(exceptionSupplier.get(), "exception");
      throw exception;
    }
    return unwrapRight();
  }

  /**
   * Maps the right value.
   *
   * @param mapper mapper applied to the right value
   * @param <U> mapped right type
   * @return mapped {@code Right}, or the same {@code Left}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <U> @NonNull Either<L, U> map(
      @NonNull Function<? super @Nullable R, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isRight()) {
      return right(mapper.apply(unwrapRight()));
    }
    return left(unwrapLeft());
  }

  /**
   * Maps the right value to another {@code Either}, flattening the result.
   *
   * @param mapper mapper applied to the right value
   * @param <U> mapped right type
   * @return mapped {@code Either}, or the same {@code Left}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default <U> @NonNull Either<L, U> flatMap(
      @NonNull Function<? super @Nullable R, ? extends Either<? extends L, ? extends U>> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isLeft()) {
      return left(unwrapLeft());
    }
    @SuppressWarnings("unchecked")
    final var mapped = (Either<L, U>) mapper.apply(unwrapRight());
    return Objects.requireNonNull(mapped, "mapped");
  }

  /**
   * Maps the left value.
   *
   * @param mapper mapper applied to the left value
   * @param <U> mapped left type
   * @return mapped {@code Left}, or the same {@code Right}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <U> @NonNull Either<U, R> mapLeft(
      @NonNull Function<? super @Nullable L, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isLeft()) {
      return left(mapper.apply(unwrapLeft()));
    }
    return right(unwrapRight());
  }

  /**
   * Maps the left value to another {@code Either}, flattening the result.
   *
   * @param mapper mapper applied to the left value
   * @param <U> mapped left type
   * @return mapped {@code Either}, or the same {@code Right}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default <U> @NonNull Either<U, R> flatMapLeft(
      @NonNull Function<? super @Nullable L, ? extends Either<? extends U, ? extends R>> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isRight()) {
      return right(unwrapRight());
    }
    @SuppressWarnings("unchecked")
    final var mapped = (Either<U, R>) mapper.apply(unwrapLeft());
    return Objects.requireNonNull(mapped, "mapped");
  }

  /**
   * Maps the right value.
   *
   * @param mapper mapper applied to the right value
   * @param <U> mapped right type
   * @return mapped {@code Right}, or the same {@code Left}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <U> @NonNull Either<L, U> mapRight(
      @NonNull Function<? super @Nullable R, ? extends U> mapper) {
    return map(mapper);
  }

  /**
   * Maps both left and right values.
   *
   * @param leftMapper mapper applied to the left value
   * @param rightMapper mapper applied to the right value
   * @param <U> mapped left type
   * @param <V> mapped right type
   * @return mapped {@code Either}
   * @throws NullPointerException if {@code leftMapper} or {@code rightMapper} is {@code null}
   */
  default <U, V> @NonNull Either<U, V> mapBoth(
      @NonNull Function<? super @Nullable L, ? extends U> leftMapper,
      @NonNull Function<? super @Nullable R, ? extends V> rightMapper) {
    Objects.requireNonNull(leftMapper, "leftMapper");
    Objects.requireNonNull(rightMapper, "rightMapper");
    if (isRight()) {
      return right(rightMapper.apply(unwrapRight()));
    }
    return left(leftMapper.apply(unwrapLeft()));
  }

  /**
   * Fold the {@code Either} into a non-{@code Either} value by handling both cases.
   *
   * @param ifLeft handler for the left case
   * @param ifRight handler for the right case
   * @param <U> result type
   * @return result of the chosen handler
   * @throws NullPointerException if {@code ifLeft} or {@code ifRight} is {@code null}
   */
  default <U> @Nullable U fold(
      @NonNull Function<? super @Nullable L, ? extends U> ifLeft,
      @NonNull Function<? super @Nullable R, ? extends U> ifRight) {
    Objects.requireNonNull(ifLeft, "ifLeft");
    Objects.requireNonNull(ifRight, "ifRight");
    return isRight() ? ifRight.apply(unwrapRight()) : ifLeft.apply(unwrapLeft());
  }

  /**
   * Fold the {@code Either} into a value by supplying handlers for both cases.
   *
   * @param ifLeft supplier for the left case
   * @param ifRight supplier for the right case
   * @param <U> result type
   * @return result of the chosen supplier
   * @throws NullPointerException if {@code ifLeft} or {@code ifRight} is {@code null}
   */
  default <U> @Nullable U fold(
      @NonNull Supplier<? extends U> ifLeft, @NonNull Supplier<? extends U> ifRight) {
    Objects.requireNonNull(ifLeft, "ifLeft");
    Objects.requireNonNull(ifRight, "ifRight");
    return isRight() ? ifRight.get() : ifLeft.get();
  }

  /**
   * Perform a side effect for {@code Right} without changing the result.
   *
   * @param action consumer for the right value
   * @return this either
   * @throws NullPointerException if {@code action} is {@code null}
   */
  default Either<L, R> peek(@NonNull Consumer<? super @Nullable R> action) {
    Objects.requireNonNull(action, "action");
    if (isRight()) {
      action.accept(unwrapRight());
    }
    return this;
  }

  /**
   * Perform a side effect for {@code Left} without changing the result.
   *
   * @param action consumer for the left value
   * @return this either
   * @throws NullPointerException if {@code action} is {@code null}
   */
  default Either<L, R> peekLeft(@NonNull Consumer<? super @Nullable L> action) {
    Objects.requireNonNull(action, "action");
    if (isLeft()) {
      action.accept(unwrapLeft());
    }
    return this;
  }

  /**
   * Perform a side effect for {@code Right} without changing the result.
   *
   * @param action consumer for the right value
   * @return this either
   * @throws NullPointerException if {@code action} is {@code null}
   */
  default Either<L, R> peekRight(@NonNull Consumer<? super @Nullable R> action) {
    return peek(action);
  }

  /**
   * Perform a side effect for either case without changing the result.
   *
   * @param onLeft consumer for the left value
   * @param onRight consumer for the right value
   * @return this either
   * @throws NullPointerException if {@code onLeft} or {@code onRight} is {@code null}
   */
  default Either<L, R> tapBoth(
      @NonNull Consumer<? super @Nullable L> onLeft,
      @NonNull Consumer<? super @Nullable R> onRight) {
    Objects.requireNonNull(onLeft, "onLeft");
    Objects.requireNonNull(onRight, "onRight");
    if (isRight()) {
      onRight.accept(unwrapRight());
    } else {
      onLeft.accept(unwrapLeft());
    }
    return this;
  }

  /**
   * Swaps {@code Left} and {@code Right}.
   *
   * @return swapped {@code Either}
   */
  default @NonNull Either<R, L> swap() {
    if (isRight()) {
      return left(unwrapRight());
    }
    return right(unwrapLeft());
  }

  /**
   * Returns the left value as {@link Optional}.
   *
   * @return {@link Optional#empty()} for {@code Right}, otherwise the left value
   */
  default Optional<L> left() {
    if (isLeft()) {
      return Optional.ofNullable(unwrapLeft());
    }
    return Optional.empty();
  }

  /**
   * Returns the right value as {@link Optional}.
   *
   * @return {@link Optional#empty()} for {@code Left}, otherwise the right value
   */
  default Optional<R> right() {
    if (isRight()) {
      return Optional.ofNullable(unwrapRight());
    }
    return Optional.empty();
  }

  /**
   * Returns the right value as {@link Option}.
   *
   * @return {@code Some} for {@code Right}, otherwise {@code None}
   */
  default Option<R> toOption() {
    if (isRight()) {
      return Option.ofNullable(unwrapRight());
    }
    return Option.none();
  }

  /**
   * Converts this {@code Either} to {@link Result} by treating {@code Right} as success.
   *
   * @return {@code Result.ok} for {@code Right}, otherwise {@code Result.err}
   */
  default Result<R, L> toResult() {
    if (isRight()) {
      return Result.ok(unwrapRight());
    }
    return Result.err(unwrapLeft());
  }

  /**
   * Converts this {@code Either} to a {@link Try} by mapping {@code Left} to {@link Throwable}.
   *
   * @param mapper mapper to convert left into {@link Throwable}
   * @return {@code Try.success} for {@code Right}, otherwise {@code Try.failure}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default Try<R> toTry(@NonNull Function<? super @Nullable L, ? extends Throwable> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isRight()) {
      return Try.success(unwrapRight());
    }
    final var throwable = Objects.requireNonNull(mapper.apply(unwrapLeft()), "throwable");
    return Try.failure(throwable);
  }

  /**
   * Returns {@code true} if this is {@code Right} and the value equals {@code other}.
   *
   * @param other the value to compare with the right value
   * @return {@code true} when {@code Right} and values are equal
   */
  default boolean contains(@Nullable R other) {
    return isRight() && Objects.equals(unwrapRight(), other);
  }

  /**
   * Returns {@code true} if this is {@code Left} and the value equals {@code other}.
   *
   * @param other the value to compare with the left value
   * @return {@code true} when {@code Left} and values are equal
   */
  default boolean containsLeft(@Nullable L other) {
    return isLeft() && Objects.equals(unwrapLeft(), other);
  }

  /**
   * Returns {@code true} if this is {@code Right} and the value matches {@code predicate}.
   *
   * @param predicate predicate applied to the right value
   * @return {@code true} when {@code Right} and predicate returns {@code true}
   * @throws NullPointerException if {@code predicate} is {@code null}
   */
  default boolean isRightAnd(@NonNull Predicate<? super @Nullable R> predicate) {
    Objects.requireNonNull(predicate, "predicate");
    return isRight() && predicate.test(unwrapRight());
  }

  /**
   * Returns {@code true} if this is {@code Left} and the value matches {@code predicate}.
   *
   * @param predicate predicate applied to the left value
   * @return {@code true} when {@code Left} and predicate returns {@code true}
   * @throws NullPointerException if {@code predicate} is {@code null}
   */
  default boolean isLeftAnd(@NonNull Predicate<? super @Nullable L> predicate) {
    Objects.requireNonNull(predicate, "predicate");
    return isLeft() && predicate.test(unwrapLeft());
  }

  /**
   * Returns the mapped right value or {@code fallback} for {@code Left}.
   *
   * @param fallback the value to return for {@code Left}, possibly {@code null}
   * @param mapper the mapper applied to the right value
   * @param <U> the mapped value type
   * @return the mapped value or {@code fallback}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <U> @Nullable U mapRightOr(
      @Nullable U fallback, @NonNull Function<? super @Nullable R, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isRight()) {
      return mapper.apply(unwrapRight());
    }
    return fallback;
  }

  /**
   * Returns the mapped left value or {@code fallback} for {@code Right}.
   *
   * @param fallback the value to return for {@code Right}, possibly {@code null}
   * @param mapper the mapper applied to the left value
   * @param <U> the mapped value type
   * @return the mapped value or {@code fallback}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <U> @Nullable U mapLeftOr(
      @Nullable U fallback, @NonNull Function<? super @Nullable L, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isLeft()) {
      return mapper.apply(unwrapLeft());
    }
    return fallback;
  }

  /**
   * Returns the mapped right value or the supplied fallback for {@code Left}.
   *
   * @param fallback the supplier used for {@code Left}
   * @param mapper the mapper applied to the right value
   * @param <U> the mapped value type
   * @return the mapped value or the supplied fallback, possibly {@code null}
   * @throws NullPointerException if {@code fallback} or {@code mapper} is {@code null}
   */
  default <U> @Nullable U mapRightOrElse(
      @NonNull Supplier<? extends @Nullable U> fallback,
      @NonNull Function<? super @Nullable R, ? extends U> mapper) {
    Objects.requireNonNull(fallback, "fallback");
    Objects.requireNonNull(mapper, "mapper");
    if (isRight()) {
      return mapper.apply(unwrapRight());
    }
    return fallback.get();
  }

  /**
   * Returns the mapped left value or the supplied fallback for {@code Right}.
   *
   * @param fallback the supplier used for {@code Right}
   * @param mapper the mapper applied to the left value
   * @param <U> the mapped value type
   * @return the mapped value or the supplied fallback, possibly {@code null}
   * @throws NullPointerException if {@code fallback} or {@code mapper} is {@code null}
   */
  default <U> @Nullable U mapLeftOrElse(
      @NonNull Supplier<? extends @Nullable U> fallback,
      @NonNull Function<? super @Nullable L, ? extends U> mapper) {
    Objects.requireNonNull(fallback, "fallback");
    Objects.requireNonNull(mapper, "mapper");
    if (isLeft()) {
      return mapper.apply(unwrapLeft());
    }
    return fallback.get();
  }

  /**
   * Flattens a nested {@code Either} when this is {@code Right}.
   *
   * <p>Uses {@link #unwrapRight()} and casts the right value to {@code Either<L, U>}.
   *
   * @param <U> inner right value type
   * @return the flattened {@code Either}
   * @throws ClassCastException if the right value is not an {@code Either}
   * @throws NullPointerException if this is {@code Right} with {@code null}
   */
  @SuppressWarnings("unchecked")
  default <U> @NonNull Either<L, U> flatten() {
    if (isLeft()) {
      return left(unwrapLeft());
    }
    final var nested = (Either<L, U>) Objects.requireNonNull(unwrapRight(), "value");
    return Objects.requireNonNull(nested, "nested");
  }

  /**
   * A left value.
   *
   * @param value the left value, possibly {@code null}
   */
  record Left<L, R>(@Nullable L value) implements Either<L, R> {

    @Override
    public boolean isRight() {
      return false;
    }

    @Override
    public @Nullable L unwrapLeft() {
      return value;
    }

    @Override
    public @Nullable R unwrapRight() {
      throw new IllegalStateException("Either is Left");
    }
  }

  /**
   * A right value.
   *
   * @param value the right value, possibly {@code null}
   */
  record Right<L, R>(@Nullable R value) implements Either<L, R> {
    @Override
    public boolean isRight() {
      return true;
    }

    @Override
    public @Nullable L unwrapLeft() {
      throw new IllegalStateException("Either is Right");
    }

    @Override
    public @Nullable R unwrapRight() {
      return value;
    }
  }
}
