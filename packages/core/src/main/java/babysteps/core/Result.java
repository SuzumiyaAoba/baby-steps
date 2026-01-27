package babysteps.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A container that represents either a success value ({@code Ok}) or an error value ({@code Err}).
 *
 * <p>{@code T} may be {@code null} when using {@link #ok(Object)}. Errors may be {@code null} when
 * using {@link #err(Object)}.
 *
 * <p>Use {@link Either} when you need a symmetric sum type where neither side implies success or
 * failure. {@code Result} is intentionally biased toward a success/error workflow and provides
 * convenience APIs (unwrap/orElse/recover) tailored to that intent.
 *
 * @param <T> the success value type, possibly nullable
 * @param <E> the error value type, possibly nullable
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
   * @param error the error value, possibly {@code null}
   * @return an {@code Err} result
   */
  static <T, E> Result<T, E> err(@Nullable E error) {
    return new Err<>(error);
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
   * @return the error value, possibly {@code null}
   * @throws IllegalStateException if this result is {@code Ok}
   */
  @Nullable E unwrapErr();

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
      @NonNull Function<? super @Nullable E, ? extends X> mapper) throws X {
    Objects.requireNonNull(mapper, "mapper");
    if (isErr()) {
      throw Objects.requireNonNull(mapper.apply(unwrapErr()), "exception");
    }
    return unwrap();
  }

  /**
   * Returns the success value or throws a supplied exception when this is {@code Err}.
   *
   * @param exceptionSupplier supplier for exception
   * @param <X> exception type
   * @return the success value, possibly {@code null}
   * @throws X if this result is {@code Err}
   * @throws NullPointerException if {@code exceptionSupplier} is {@code null}
   */
  default <X extends Throwable> @Nullable T orElseThrow(
      @NonNull Supplier<? extends X> exceptionSupplier) throws X {
    Objects.requireNonNull(exceptionSupplier, "exceptionSupplier");
    if (isErr()) {
      final var exception = Objects.requireNonNull(exceptionSupplier.get(), "exception");
      throw exception;
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
   * Returns the error value or throws {@link IllegalStateException} with the given message.
   *
   * @param message the exception message
   * @return the error value
   * @throws IllegalStateException if this result is {@code Ok}
   * @throws NullPointerException if {@code message} is {@code null}
   */
  default @Nullable E expectErr(@NonNull String message) {
    Objects.requireNonNull(message, "message");
    if (isOk()) {
      throw new IllegalStateException(message);
    }
    return unwrapErr();
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
      @NonNull Function<? super @Nullable E, ? extends U> ifErr,
      @NonNull Function<? super @Nullable T, ? extends U> ifOk) {
    Objects.requireNonNull(ifErr, "ifErr");
    Objects.requireNonNull(ifOk, "ifOk");
    return isOk() ? ifOk.apply(unwrap()) : ifErr.apply(unwrapErr());
  }

  /**
   * Fold the result into a value by supplying handlers for both cases.
   *
   * @param ifErr supplier for the error case
   * @param ifOk supplier for the success case
   * @param <U> result type
   * @return result of the chosen supplier
   * @throws NullPointerException if {@code ifErr} or {@code ifOk} is {@code null}
   */
  default <U> @Nullable U fold(
      @NonNull Supplier<? extends U> ifErr, @NonNull Supplier<? extends U> ifOk) {
    Objects.requireNonNull(ifErr, "ifErr");
    Objects.requireNonNull(ifOk, "ifOk");
    return isOk() ? ifOk.get() : ifErr.get();
  }

  /**
   * Recovers from an error by mapping it to a success value.
   *
   * @param mapper the mapper applied to the error value
   * @return a recovered {@code Ok} or the original {@code Ok}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default Result<T, E> recover(
      @NonNull Function<? super @Nullable E, ? extends @Nullable T> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      return this;
    }
    return ok(mapper.apply(unwrapErr()));
  }

  /**
   * Recovers from an error by mapping it to another {@code Result}.
   *
   * @param mapper the mapper applied to the error value
   * @return a recovered {@code Result} or the original {@code Ok}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default Result<T, E> recoverWith(
      @NonNull Function<? super @Nullable E, ? extends Result<? extends T, E>> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      return this;
    }
    @SuppressWarnings("unchecked")
    final var mapped = (Result<T, E>) mapper.apply(unwrapErr());
    return Objects.requireNonNull(mapped, "mapped");
  }

  /**
   * Recovers from an error by mapping it to another {@code Result} with a new error type.
   *
   * @param mapper the mapper applied to the error value
   * @param <F> new error type
   * @return a recovered {@code Result} or the original {@code Ok}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default <F> Result<T, F> recoverWithErr(
      @NonNull Function<? super @Nullable E, ? extends Result<? extends T, F>> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      return ok(unwrap());
    }
    @SuppressWarnings("unchecked")
    final var mapped = (Result<T, F>) mapper.apply(unwrapErr());
    return Objects.requireNonNull(mapped, "mapped");
  }

  /**
   * Swaps success and error values.
   *
   * @return a swapped result
   * @throws NullPointerException if this is {@code Ok} with a {@code null} value
   */
  default Result<E, T> swap() {
    if (isOk()) {
      return err(Objects.requireNonNull(unwrap(), "value"));
    }
    return ok(unwrapErr());
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
   * Returns the mapped error value or {@code fallback} for {@code Ok}.
   *
   * @param fallback the value to return for {@code Ok}, possibly {@code null}
   * @param mapper the mapper applied to the error value
   * @param <F> the mapped error type
   * @return the mapped error value or {@code fallback}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <F> @Nullable F mapErrOr(
      @Nullable F fallback, @NonNull Function<? super @Nullable E, ? extends F> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isErr()) {
      return mapper.apply(unwrapErr());
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
   * Returns the mapped error value or the supplied fallback for {@code Ok}.
   *
   * @param fallback the supplier used for {@code Ok}
   * @param mapper the mapper applied to the error value
   * @param <F> the mapped error type
   * @return the mapped error value or the supplied fallback
   * @throws NullPointerException if {@code fallback} or {@code mapper} is {@code null}
   */
  default <F> @Nullable F mapErrOrElse(
      @NonNull Supplier<? extends @Nullable F> fallback,
      @NonNull Function<? super @Nullable E, ? extends F> mapper) {
    Objects.requireNonNull(fallback, "fallback");
    Objects.requireNonNull(mapper, "mapper");
    if (isErr()) {
      return mapper.apply(unwrapErr());
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
  default <F> Result<T, F> mapErr(@NonNull Function<? super @Nullable E, ? extends F> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      return ok(unwrap());
    }
    return err(mapper.apply(unwrapErr()));
  }

  /**
   * Maps both success and error values.
   *
   * @param okMapper mapper applied to the success value
   * @param errMapper mapper applied to the error value
   * @param <U> mapped success type
   * @param <F> mapped error type
   * @return mapped {@code Result}
   * @throws NullPointerException if {@code okMapper} or {@code errMapper} is {@code null}
   */
  default <U, F> Result<U, F> mapBoth(
      @NonNull Function<? super @Nullable T, ? extends U> okMapper,
      @NonNull Function<? super @Nullable E, ? extends F> errMapper) {
    Objects.requireNonNull(okMapper, "okMapper");
    Objects.requireNonNull(errMapper, "errMapper");
    if (isOk()) {
      return ok(okMapper.apply(unwrap()));
    }
    return err(errMapper.apply(unwrapErr()));
  }

  /**
   * Perform a side effect for {@code Ok} without changing the result.
   *
   * @param action consumer for the success value
   * @return this result
   * @throws NullPointerException if {@code action} is {@code null}
   */
  default Result<T, E> tap(@NonNull Consumer<? super @Nullable T> action) {
    Objects.requireNonNull(action, "action");
    if (isOk()) {
      action.accept(unwrap());
    }
    return this;
  }

  /**
   * Perform a side effect for {@code Err} without changing the result.
   *
   * @param action consumer for the error value
   * @return this result
   * @throws NullPointerException if {@code action} is {@code null}
   */
  default Result<T, E> tapErr(@NonNull Consumer<? super @Nullable E> action) {
    Objects.requireNonNull(action, "action");
    if (isErr()) {
      action.accept(unwrapErr());
    }
    return this;
  }

  /**
   * Perform a side effect for either case without changing the result.
   *
   * @param onOk consumer for the success value
   * @param onErr consumer for the error value
   * @return this result
   * @throws NullPointerException if {@code onOk} or {@code onErr} is {@code null}
   */
  default Result<T, E> tapBoth(
      @NonNull Consumer<? super @Nullable T> onOk, @NonNull Consumer<? super @Nullable E> onErr) {
    Objects.requireNonNull(onOk, "onOk");
    Objects.requireNonNull(onErr, "onErr");
    if (isOk()) {
      onOk.accept(unwrap());
    } else {
      onErr.accept(unwrapErr());
    }
    return this;
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
   * Returns {@code true} if this is {@code Err} and the error equals {@code other}.
   *
   * @param other the value to compare with the error
   * @return {@code true} when {@code Err} and values are equal
   */
  default boolean containsErr(@Nullable E other) {
    return isErr() && Objects.equals(unwrapErr(), other);
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
   * Flattens a nested {@code Result} when this is {@code Ok}.
   *
   * @param <U> inner success value type
   * @return the flattened {@code Result}
   * @throws NullPointerException if this is {@code Ok} with {@code null} or not a {@code Result}
   */
  @SuppressWarnings("unchecked")
  default <U> Result<U, E> flatten() {
    if (isErr()) {
      return err(unwrapErr());
    }
    final var nested = (Result<U, E>) Objects.requireNonNull(unwrap(), "value");
    return Objects.requireNonNull(nested, "nested");
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
  default boolean isErrAnd(@NonNull Predicate<? super @Nullable E> predicate) {
    Objects.requireNonNull(predicate, "predicate");
    return isErr() && predicate.test(unwrapErr());
  }

  /**
   * Returns the success value as {@link Optional}.
   *
   * @return {@link Optional#empty()} for {@code Err}, otherwise the success value
   */
  default Optional<T> ok() {
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
  default Option<T> toOption() {
    if (isOk()) {
      return Option.ofNullable(unwrap());
    }
    return Option.none();
  }

  /**
   * Converts this {@code Result} to a {@link Try} by mapping errors to {@link Throwable}.
   *
   * @param mapper mapper to convert error into {@link Throwable}
   * @return {@code Try.success} for {@code Ok}, otherwise {@code Try.failure}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default Try<T> toTry(@NonNull Function<? super @Nullable E, ? extends Throwable> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      return Try.success(unwrap());
    }
    final var throwable = Objects.requireNonNull(mapper.apply(unwrapErr()), "throwable");
    return Try.failure(throwable);
  }

  /**
   * Returns the error value as {@link Optional}.
   *
   * @return {@link Optional#empty()} for {@code Ok}, otherwise the error value
   */
  default Optional<E> err() {
    if (isErr()) {
      return Optional.ofNullable(unwrapErr());
    }
    return Optional.empty();
  }

  /**
   * Returns the error value or {@code fallback} when this is {@code Ok}.
   *
   * @param fallback fallback error value
   * @return the error value or {@code fallback}
   */
  default @Nullable E unwrapErrOr(@Nullable E fallback) {
    return isErr() ? unwrapErr() : fallback;
  }

  /**
   * Returns the error value or a supplied fallback when this is {@code Ok}.
   *
   * @param fallback fallback supplier
   * @return the error value or the supplier result
   * @throws NullPointerException if {@code fallback} is {@code null}
   */
  default @Nullable E unwrapErrOrElse(@NonNull Supplier<? extends @Nullable E> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    return isErr() ? unwrapErr() : fallback.get();
  }

  /**
   * Combine two {@code Ok} values using {@code combiner}, otherwise return the first {@code Err}.
   *
   * @param other the other result
   * @param combiner combiner for success values
   * @param <U> other success value type
   * @param <V> combined success value type
   * @return combined {@code Ok}, or an {@code Err}
   * @throws NullPointerException if {@code other} or {@code combiner} is {@code null}
   */
  default <U, V> Result<V, E> zip(
      @NonNull Result<? extends U, E> other,
      @NonNull BiFunction<? super @Nullable T, ? super @Nullable U, ? extends V> combiner) {
    Objects.requireNonNull(other, "other");
    Objects.requireNonNull(combiner, "combiner");
    if (isErr()) {
      return err(unwrapErr());
    }
    if (other.isErr()) {
      return err(other.unwrapErr());
    }
    return ok(combiner.apply(unwrap(), other.unwrap()));
  }

  /**
   * Combine two {@code Ok} values using {@code combiner}, otherwise return the first {@code Err}.
   *
   * @param other the other result
   * @param combiner combiner for success values
   * @return combined {@code Ok}, or an {@code Err}
   * @throws NullPointerException if {@code other} or {@code combiner} is {@code null}
   */
  default Result<T, E> combine(
      @NonNull Result<? extends T, E> other,
      @NonNull BiFunction<? super @Nullable T, ? super @Nullable T, ? extends T> combiner) {
    Objects.requireNonNull(other, "other");
    Objects.requireNonNull(combiner, "combiner");
    if (isErr()) {
      return err(unwrapErr());
    }
    if (other.isErr()) {
      return err(other.unwrapErr());
    }
    return ok(combiner.apply(unwrap(), other.unwrap()));
  }

  /**
   * Maps errors to another {@code Result} while preserving successes.
   *
   * @param mapper mapper for the error value
   * @param <F> new error type
   * @return mapped {@code Result}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default <F> Result<T, F> andThenErr(
      @NonNull Function<? super @Nullable E, ? extends Result<? extends T, F>> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      return ok(unwrap());
    }
    @SuppressWarnings("unchecked")
    final var mapped = (Result<T, F>) mapper.apply(unwrapErr());
    return Objects.requireNonNull(mapped, "mapped");
  }

  /**
   * Partition results into successes and errors.
   *
   * @param results results to partition
   * @param <T> success value type
   * @param <E> error value type
   * @return partitioned results
   * @throws NullPointerException if {@code results} or any element is {@code null}
   */
  static <T, E> @NonNull Partition<T, E> partition(
      @NonNull Collection<? extends Result<? extends T, ? extends E>> results) {
    Objects.requireNonNull(results, "results");
    final var oks = new ArrayList<T>();
    final var errs = new ArrayList<@Nullable E>();
    for (final var result : results) {
      Objects.requireNonNull(result, "result");
      if (result.isOk()) {
        oks.add(result.unwrap());
      } else {
        errs.add(result.unwrapErr());
      }
    }
    return new Partition<>(List.copyOf(oks), List.copyOf(errs));
  }

  /**
   * Converts a collection of results into a result of list, failing on the first error.
   *
   * @param results results to sequence
   * @param <T> success value type
   * @param <E> error value type
   * @return sequenced result
   * @throws NullPointerException if {@code results} or any element is {@code null}
   */
  static <T, E> @NonNull Result<List<T>, E> sequence(
      @NonNull Collection<? extends Result<? extends T, E>> results) {
    Objects.requireNonNull(results, "results");
    final var values = new ArrayList<T>();
    for (final var result : results) {
      Objects.requireNonNull(result, "result");
      if (result.isErr()) {
        return err(result.unwrapErr());
      }
      values.add(result.unwrap());
    }
    return ok(List.copyOf(values));
  }

  /**
   * Maps values to results and collects them, failing on the first error.
   *
   * @param values values to traverse
   * @param mapper mapper to results
   * @param <T> input type
   * @param <U> success value type
   * @param <E> error value type
   * @return traversed result
   * @throws NullPointerException if {@code values}, {@code mapper}, or mapped result is {@code
   *     null}
   */
  static <T, U, E> @NonNull Result<List<U>, E> traverse(
      @NonNull Collection<? extends T> values,
      @NonNull Function<? super T, ? extends Result<? extends U, E>> mapper) {
    Objects.requireNonNull(values, "values");
    Objects.requireNonNull(mapper, "mapper");
    final var results = new ArrayList<U>();
    for (final var value : values) {
      final var result = Objects.requireNonNull(mapper.apply(value), "result");
      if (result.isErr()) {
        return err(result.unwrapErr());
      }
      results.add(result.unwrap());
    }
    return ok(List.copyOf(results));
  }

  /**
   * Holder for partitioned results.
   *
   * @param oks success values
   * @param errs error values
   * @param <T> success value type
   * @param <E> error value type
   */
  record Partition<T, E>(@NonNull List<T> oks, @NonNull List<@Nullable E> errs) {}

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
    public @Nullable E unwrapErr() {
      throw new IllegalStateException("Result is Ok");
    }
  }

  /**
   * An error result.
   *
   * @param error the error value, possibly {@code null}
   */
  record Err<T, E>(@Nullable E error) implements Result<T, E> {

    @Override
    public boolean isOk() {
      return false;
    }

    @Override
    public @Nullable T unwrap() {
      throw new IllegalStateException("Result is Err");
    }

    @Override
    public @Nullable E unwrapErr() {
      return error;
    }
  }
}
