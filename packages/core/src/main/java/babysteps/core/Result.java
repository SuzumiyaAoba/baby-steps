package babysteps.core;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A container that represents either a success value ({@code Ok}) or an error value ({@code Err}).
 *
 * <p>{@code T} may be {@code null} when using {@link #ok(Object)}. Errors must be non-null.
 *
 * @param <T> the success value type, possibly nullable
 * @param <E> the error value type, non-null
 */
public sealed interface Result<T, E> permits Result.Ok, Result.Err {
  /**
   * Creates an {@code Ok} result with the given value.
   *
   * @param value the success value, possibly {@code null}
   * @return an {@code Ok} result
   */
  static <T, E> Result<T, E> ok(@Nullable T value) {
    return new Ok<>(value);
  }

  /**
   * Creates an {@code Err} result with the given error.
   *
   * @param error the non-null error value
   * @return an {@code Err} result
   * @throws NullPointerException if {@code error} is {@code null}
   */
  static <T, E> Result<T, E> err(@NonNull E error) {
    return new Err<>(Objects.requireNonNull(error, "error"));
  }

  /**
   * Returns {@code true} if this result is {@code Ok}.
   *
   * @return {@code true} for {@code Ok}, {@code false} for {@code Err}
   */
  boolean isOk();

  /**
   * Returns {@code true} if this result is {@code Err}.
   *
   * @return {@code true} for {@code Err}, {@code false} for {@code Ok}
   */
  default boolean isErr() {
    return !isOk();
  }

  /**
   * Returns the success value.
   *
   * @return the success value, possibly {@code null}
   * @throws IllegalStateException if this result is {@code Err}
   */
  @Nullable T unwrap();

  /**
   * Returns the error value.
   *
   * @return the non-null error value
   * @throws IllegalStateException if this result is {@code Ok}
   */
  @NonNull E unwrapErr();

  /**
   * Returns the success value or the provided fallback when this is {@code Err}.
   *
   * @param fallback the value to return for {@code Err}, possibly {@code null}
   * @return the success value or {@code fallback}
   */
  default @Nullable T unwrapOr(@Nullable T fallback) {
    return isOk() ? unwrap() : fallback;
  }

  /**
   * Returns the success value or the result of {@code fallback} when this is {@code Err}.
   *
   * @param fallback the supplier used for {@code Err}
   * @return the success value or the supplier result, possibly {@code null}
   * @throws NullPointerException if {@code fallback} is {@code null}
   */
  default @Nullable T unwrapOrElse(@NonNull Supplier<? extends @Nullable T> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    return isOk() ? unwrap() : fallback.get();
  }

  /**
   * Returns the success value or throws an exception mapped from the error.
   *
   * @param mapper the mapper used to convert the error into an exception
   * @param <X> the exception type
   * @return the success value, possibly {@code null}
   * @throws X if this result is {@code Err}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default <X extends Throwable> @Nullable T unwrapOrThrow(
      @NonNull Function<? super E, ? extends X> mapper) throws X {
    Objects.requireNonNull(mapper, "mapper");
    if (isErr()) {
      throw Objects.requireNonNull(mapper.apply(unwrapErr()), "exception");
    }
    return unwrap();
  }

  /**
   * Returns the success value or throws {@link IllegalStateException} with the given message.
   *
   * @param message the exception message
   * @return the success value, possibly {@code null}
   * @throws IllegalStateException if this result is {@code Err}
   * @throws NullPointerException if {@code message} is {@code null}
   */
  default @Nullable T expect(@NonNull String message) {
    Objects.requireNonNull(message, "message");
    if (isErr()) {
      throw new IllegalStateException(message);
    }
    return unwrap();
  }

  /**
   * Returns this {@code Ok}, or {@code fallback} when this is {@code Err}.
   *
   * @param fallback the fallback {@code Result}
   * @return this result for {@code Ok}, otherwise {@code fallback}
   * @throws NullPointerException if {@code fallback} is {@code null}
   */
  default Result<T, E> orElse(@NonNull Result<? extends T, E> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    if (isOk()) {
      return this;
    }
    @SuppressWarnings("unchecked")
    final var other = (Result<T, E>) fallback;
    return other;
  }

  /**
   * Returns this {@code Ok}, or a supplied {@code Result} when this is {@code Err}.
   *
   * @param fallback the supplier for the fallback result
   * @return this result for {@code Ok}, otherwise the supplied result
   * @throws NullPointerException if {@code fallback} or its result is {@code null}
   */
  default Result<T, E> orElseGet(@NonNull Supplier<? extends Result<? extends T, E>> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    if (isOk()) {
      return this;
    }
    @SuppressWarnings("unchecked")
    final var other = (Result<T, E>) Objects.requireNonNull(fallback.get(), "result");
    return other;
  }

  /**
   * Maps the success value while preserving errors.
   *
   * @param mapper the mapper applied to the success value
   * @return a mapped {@code Ok}, or the same {@code Err}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <U> Result<U, E> map(@NonNull Function<? super @Nullable T, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isErr()) {
      return err(unwrapErr());
    }
    return ok(mapper.apply(unwrap()));
  }

  /**
   * Fold the result into a non-{@code Result} value by handling both cases.
   *
   * @param ifErr handler for the error case
   * @param ifOk handler for the success case
   * @param <U> result type
   * @return result of the chosen handler
   * @throws NullPointerException if {@code ifErr} or {@code ifOk} is {@code null}
   */
  default <U> @Nullable U fold(
      @NonNull Function<? super E, ? extends U> ifErr,
      @NonNull Function<? super @Nullable T, ? extends U> ifOk) {
    Objects.requireNonNull(ifErr, "ifErr");
    Objects.requireNonNull(ifOk, "ifOk");
    return isOk() ? ifOk.apply(unwrap()) : ifErr.apply(unwrapErr());
  }

  /**
   * Recovers from an error by mapping it to a success value.
   *
   * @param mapper the mapper applied to the error value
   * @return a recovered {@code Ok} or the original {@code Ok}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default Result<T, E> recover(@NonNull Function<? super E, ? extends @Nullable T> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      return this;
    }
    return ok(mapper.apply(unwrapErr()));
  }

  /**
   * Returns the mapped success value or {@code fallback} for {@code Err}.
   *
   * @param fallback the value to return for {@code Err}, possibly {@code null}
   * @param mapper the mapper applied to the success value
   * @param <U> the mapped value type
   * @return the mapped value or {@code fallback}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <U> @Nullable U mapOr(
      @Nullable U fallback, @NonNull Function<? super @Nullable T, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      return mapper.apply(unwrap());
    }
    return fallback;
  }

  /**
   * Returns the mapped success value or the supplied fallback for {@code Err}.
   *
   * @param fallback the supplier used for {@code Err}
   * @param mapper the mapper applied to the success value
   * @param <U> the mapped value type
   * @return the mapped value or the supplied fallback, possibly {@code null}
   * @throws NullPointerException if {@code fallback} or {@code mapper} is {@code null}
   */
  default <U> @Nullable U mapOrElse(
      @NonNull Supplier<? extends @Nullable U> fallback,
      @NonNull Function<? super @Nullable T, ? extends U> mapper) {
    Objects.requireNonNull(fallback, "fallback");
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      return mapper.apply(unwrap());
    }
    return fallback.get();
  }

  /**
   * Maps the error value while preserving success values.
   *
   * @param mapper the mapper applied to the error value
   * @return a mapped {@code Err}, or the same {@code Ok}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <F> Result<T, F> mapErr(@NonNull Function<? super E, ? extends F> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      return ok(unwrap());
    }
    return err(mapper.apply(unwrapErr()));
  }

  /**
   * Returns {@code true} if this is {@code Ok} and the value equals {@code other}.
   *
   * @param other the value to compare with the success value
   * @return {@code true} when {@code Ok} and values are equal
   */
  default boolean contains(@Nullable T other) {
    return isOk() && Objects.equals(unwrap(), other);
  }

  /**
   * Returns {@code other} if this is {@code Ok}, otherwise the same {@code Err}.
   *
   * @param other the next result
   * @param <U> the success value type of {@code other}
   * @return {@code other} for {@code Ok}, otherwise the same {@code Err}
   * @throws NullPointerException if {@code other} is {@code null}
   */
  default <U> Result<U, E> and(@NonNull Result<? extends U, E> other) {
    Objects.requireNonNull(other, "other");
    if (isErr()) {
      return err(unwrapErr());
    }
    @SuppressWarnings("unchecked")
    final var next = (Result<U, E>) other;
    return next;
  }

  /**
   * Returns the mapped {@code Result} for {@code Ok}, otherwise the same {@code Err}.
   *
   * @param mapper the mapper applied to the success value
   * @param <U> the success value type of the mapped result
   * @return the mapped result for {@code Ok}, otherwise the same {@code Err}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default <U> Result<U, E> andThen(
      @NonNull Function<? super @Nullable T, ? extends Result<? extends U, E>> mapper) {
    return flatMap(mapper);
  }

  /**
   * Maps the success value to another {@code Result}, flattening the result.
   *
   * @param mapper the mapper applied to the success value
   * @return the mapped {@code Result}, or the same {@code Err}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default <U> Result<U, E> flatMap(
      @NonNull Function<? super @Nullable T, ? extends Result<? extends U, E>> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isErr()) {
      return err(unwrapErr());
    }
    @SuppressWarnings("unchecked")
    final var mapped = (Result<U, E>) mapper.apply(unwrap());
    return Objects.requireNonNull(mapped, "mapped");
  }

  /**
   * Returns {@code true} if this is {@code Ok} and the value matches {@code predicate}.
   *
   * @param predicate the predicate applied to the success value
   * @return {@code true} when {@code Ok} and predicate returns {@code true}
   * @throws NullPointerException if {@code predicate} is {@code null}
   */
  default boolean isOkAnd(@NonNull Predicate<? super @Nullable T> predicate) {
    Objects.requireNonNull(predicate, "predicate");
    return isOk() && predicate.test(unwrap());
  }

  /**
   * Returns {@code true} if this is {@code Err} and the error matches {@code predicate}.
   *
   * @param predicate the predicate applied to the error value
   * @return {@code true} when {@code Err} and predicate returns {@code true}
   * @throws NullPointerException if {@code predicate} is {@code null}
   */
  default boolean isErrAnd(@NonNull Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "predicate");
    return isErr() && predicate.test(unwrapErr());
  }

  /**
   * Returns the success value as {@link Optional}.
   *
   * @return {@link Optional#empty()} for {@code Err}, otherwise the success value
   */
  default Optional<@Nullable T> ok() {
    if (isOk()) {
      return Optional.ofNullable(unwrap());
    }
    return Optional.empty();
  }

  /**
   * Returns the success value as {@link Option}.
   *
   * @return {@code Some} for {@code Ok}, otherwise {@code None}
   */
  default Option<@NonNull T> toOption() {
    if (isOk()) {
      return Option.ofNullable(unwrap());
    }
    return Option.none();
  }

  /**
   * Returns the error value as {@link Optional}.
   *
   * @return {@link Optional#empty()} for {@code Ok}, otherwise the error value
   */
  default Optional<@NonNull E> err() {
    if (isErr()) {
      return Optional.of(unwrapErr());
    }
    return Optional.empty();
  }

  /**
   * A successful result.
   *
   * @param value the success value, possibly {@code null}
   */
  record Ok<T, E>(@Nullable T value) implements Result<T, E> {
    @Override
    public boolean isOk() {
      return true;
    }

    @Override
    public @Nullable T unwrap() {
      return value;
    }

    @Override
    public @NonNull E unwrapErr() {
      throw new IllegalStateException("Result is Ok");
    }
  }

  /**
   * An error result.
   *
   * @param error the non-null error value
   */
  record Err<T, E>(@NonNull E error) implements Result<T, E> {
    public Err {
      Objects.requireNonNull(error, "error");
    }

    @Override
    public boolean isOk() {
      return false;
    }

    @Override
    public @Nullable T unwrap() {
      throw new IllegalStateException("Result is Err");
    }

    @Override
    public @NonNull E unwrapErr() {
      return error;
    }
  }
}
