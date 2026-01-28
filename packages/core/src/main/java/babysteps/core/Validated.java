package babysteps.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A validation container that accumulates errors instead of short-circuiting.
 *
 * <p>{@code Ok} carries a successful value. {@code Err} carries a list of errors. When combining
 * validations, errors from both sides are accumulated. This is useful for validating independent
 * fields where you want to report all failures at once.
 *
 * <p>{@code T} may be {@code null} when using {@link #ok(Object)}. Error elements may be {@code
 * null} when using {@link #err(Object)} or {@link #errs(Collection)}.
 *
 * @param <T> success value type, possibly nullable
 * @param <E> error element type, possibly nullable
 */
public sealed interface Validated<T, E> permits Validated.Ok, Validated.Err {
  /**
   * Creates an {@code Ok} validation with the given value.
   *
   * @param value the success value, possibly {@code null}
   * @return an {@code Ok} validation
   */
  static <T, E> @NonNull Validated<T, E> ok(@Nullable T value) {
    return new Ok<>(value);
  }

  /**
   * Creates an {@code Err} validation with a single error.
   *
   * @param error error value, possibly {@code null}
   * @return an {@code Err} validation containing one error
   */
  static <T, E> @NonNull Validated<T, E> err(@Nullable E error) {
    final var errors = new ArrayList<@Nullable E>(1);
    errors.add(error);
    return new Err<>(errors);
  }

  /**
   * Creates an {@code Err} validation with the given non-empty errors.
   *
   * @param errors non-empty error list
   * @return an {@code Err} validation containing the given errors
   * @throws NullPointerException if {@code errors} is {@code null}
   */
  static <T, E> @NonNull Validated<T, E> errs(@NonNull NonEmptyList<? extends @Nullable E> errors) {
    Objects.requireNonNull(errors, "errors");
    @SuppressWarnings("unchecked")
    final var list = (List<@Nullable E>) errors.toList();
    return errList(list);
  }

  /**
   * Creates an {@code Err} validation with the given errors.
   *
   * @param errors errors to store
   * @return an {@code Err} validation containing the given errors
   * @throws NullPointerException if {@code errors} is {@code null}
   */
  static <T, E> @NonNull Validated<T, E> errs(@NonNull Collection<? extends @Nullable E> errors) {
    Objects.requireNonNull(errors, "errors");
    return new Err<>(errors);
  }

  /**
   * Converts a {@link Result} into {@code Validated} by treating {@code Ok} as success.
   *
   * @param result result to convert
   * @param <T> success value type
   * @param <E> error value type
   * @return {@code Ok} for {@code Result.ok}, otherwise {@code Err} with a single error
   * @throws NullPointerException if {@code result} is {@code null}
   */
  static <T, E> @NonNull Validated<T, E> fromResult(
      @NonNull Result<? extends T, ? extends E> result) {
    Objects.requireNonNull(result, "result");
    if (result.isOk()) {
      return ok(result.unwrap());
    }
    return err(result.unwrapErr());
  }

  /**
   * Converts an {@link Either} into {@code Validated} by treating {@code Right} as success.
   *
   * @param either either to convert
   * @param <T> success value type
   * @param <E> error value type
   * @return {@code Ok} for {@code Right}, otherwise {@code Err} with a single error
   * @throws NullPointerException if {@code either} is {@code null}
   */
  static <T, E> @NonNull Validated<T, E> fromEither(
      @NonNull Either<? extends E, ? extends T> either) {
    Objects.requireNonNull(either, "either");
    if (either.isRight()) {
      return ok(either.unwrapRight());
    }
    return err(either.unwrapLeft());
  }

  /**
   * Converts an {@link Option} into {@code Validated} by treating {@code Some} as success.
   *
   * @param option option to convert
   * @param ifEmpty supplier for the error when the option is empty
   * @param <T> success value type
   * @param <E> error value type
   * @return {@code Ok} for {@code Some}, otherwise {@code Err} with a single error
   * @throws NullPointerException if {@code option}, {@code ifEmpty}, or its result is {@code null}
   */
  static <T, E> @NonNull Validated<T, E> fromOption(
      @NonNull Option<? extends T> option, @NonNull Supplier<? extends E> ifEmpty) {
    Objects.requireNonNull(option, "option");
    Objects.requireNonNull(ifEmpty, "ifEmpty");
    if (option.isPresent()) {
      return ok(option.get());
    }
    return err(Objects.requireNonNull(ifEmpty.get(), "error"));
  }

  /**
   * Converts an {@link Optional} into {@code Validated} by treating presence as success.
   *
   * @param optional optional to convert
   * @param ifEmpty supplier for the error when the optional is empty
   * @param <T> success value type
   * @param <E> error value type
   * @return {@code Ok} for present optional, otherwise {@code Err} with a single error
   * @throws NullPointerException if {@code optional}, {@code ifEmpty}, or its result is {@code
   *     null}
   */
  static <T, E> @NonNull Validated<T, E> fromOptional(
      @NonNull Optional<? extends T> optional, @NonNull Supplier<? extends E> ifEmpty) {
    Objects.requireNonNull(optional, "optional");
    Objects.requireNonNull(ifEmpty, "ifEmpty");
    if (optional.isPresent()) {
      return ok(optional.get());
    }
    return err(Objects.requireNonNull(ifEmpty.get(), "error"));
  }

  /**
   * @return {@code true} if this is {@code Ok}
   */
  boolean isOk();

  /**
   * @return {@code true} if this is {@code Err}
   */
  default boolean isErr() {
    return !isOk();
  }

  /**
   * Returns the success value.
   *
   * @return the success value, possibly {@code null}
   * @throws IllegalStateException if this is {@code Err}
   */
  @Nullable T unwrap();

  /**
   * Returns the accumulated errors.
   *
   * @return unmodifiable list of errors
   * @throws IllegalStateException if this is {@code Ok}
   */
  @NonNull List<@Nullable E> unwrapErrs();

  /**
   * Returns the success value or the provided fallback when this is {@code Err}.
   *
   * @param fallback fallback value, possibly {@code null}
   * @return the success value or {@code fallback}
   */
  default @Nullable T unwrapOr(@Nullable T fallback) {
    return isOk() ? unwrap() : fallback;
  }

  /**
   * Returns the success value or the result of {@code fallback} when this is {@code Err}.
   *
   * @param fallback supplier used for {@code Err}
   * @return the success value or the supplier result
   * @throws NullPointerException if {@code fallback} is {@code null}
   */
  default @Nullable T unwrapOrElse(@NonNull Supplier<? extends @Nullable T> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    return isOk() ? unwrap() : fallback.get();
  }

  /**
   * Returns the success value or throws an exception mapped from the error list.
   *
   * @param mapper mapper used to convert the error list into an exception
   * @param <X> exception type
   * @return the success value, possibly {@code null}
   * @throws X if this validation is {@code Err}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default <X extends Throwable> @Nullable T unwrapOrThrow(
      @NonNull Function<? super @NonNull List<@Nullable E>, ? extends X> mapper) throws X {
    Objects.requireNonNull(mapper, "mapper");
    if (isErr()) {
      throw Objects.requireNonNull(mapper.apply(unwrapErrs()), "exception");
    }
    return unwrap();
  }

  /**
   * Returns the success value or throws {@link IllegalStateException} with the given message.
   *
   * @param message exception message
   * @return the success value, possibly {@code null}
   * @throws IllegalStateException if this is {@code Err}
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
   * Returns the accumulated errors or throws {@link IllegalStateException} with the given message.
   *
   * @param message exception message
   * @return unmodifiable list of errors
   * @throws IllegalStateException if this is {@code Ok}
   * @throws NullPointerException if {@code message} is {@code null}
   */
  default @NonNull List<@Nullable E> expectErrs(@NonNull String message) {
    Objects.requireNonNull(message, "message");
    if (isOk()) {
      throw new IllegalStateException(message);
    }
    return unwrapErrs();
  }

  /**
   * Maps the success value while preserving errors.
   *
   * @param mapper mapper for the success value
   * @param <U> mapped success type
   * @return mapped {@code Ok}, or the same {@code Err}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <U> @NonNull Validated<U, E> map(
      @NonNull Function<? super @Nullable T, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isErr()) {
      @SuppressWarnings("unchecked")
      final var self = (Validated<U, E>) this;
      return self;
    }
    return ok(mapper.apply(unwrap()));
  }

  /**
   * Maps errors while preserving successes.
   *
   * @param mapper mapper for error elements
   * @param <F> mapped error type
   * @return mapped {@code Err} with an unmodifiable error list, or the same {@code Ok}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <F> @NonNull Validated<T, F> mapErr(
      @NonNull Function<? super @Nullable E, ? extends F> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      @SuppressWarnings("unchecked")
      final var self = (Validated<T, F>) this;
      return self;
    }
    final var mapped = new ArrayList<@Nullable F>();
    for (final var error : unwrapErrs()) {
      mapped.add(mapper.apply(error));
    }
    return new Err<>(mapped);
  }

  /**
   * Folds this validation into a single value.
   *
   * @param ifErr handler for the error list
   * @param ifOk handler for the success value
   * @param <U> result type
   * @return result of the chosen handler
   * @throws NullPointerException if {@code ifErr} or {@code ifOk} is {@code null}
   */
  default <U> @Nullable U fold(
      @NonNull Function<? super @NonNull List<@Nullable E>, ? extends U> ifErr,
      @NonNull Function<? super @Nullable T, ? extends U> ifOk) {
    Objects.requireNonNull(ifErr, "ifErr");
    Objects.requireNonNull(ifOk, "ifOk");
    if (isOk()) {
      return ifOk.apply(unwrap());
    }
    return ifErr.apply(unwrapErrs());
  }

  /**
   * Converts this {@code Validated} to a {@link Result}, keeping accumulated errors as a list.
   *
   * @return {@code Result.ok} for {@code Ok}, otherwise {@code Result.err} of error list
   */
  default Result<T, List<@Nullable E>> toResult() {
    if (isOk()) {
      return Result.ok(unwrap());
    }
    return Result.err(unwrapErrs());
  }

  /**
   * Converts this {@code Validated} to {@link Either} by treating {@code Ok} as {@code Right}.
   *
   * @return {@code Either.right} for {@code Ok}, otherwise {@code Either.left} of error list
   */
  default Either<List<@Nullable E>, T> toEither() {
    if (isOk()) {
      return Either.right(unwrap());
    }
    return Either.left(unwrapErrs());
  }

  /**
   * Converts this {@code Validated} to {@link Option}.
   *
   * @return {@link Option#some} for {@code Ok}, otherwise {@link Option#none}
   */
  default Option<T> toOption() {
    if (isOk()) {
      return Option.ofNullable(unwrap());
    }
    return Option.none();
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
   * Returns the errors as {@link Optional}.
   *
   * @return {@link Optional#empty()} for {@code Ok}, otherwise the error list
   */
  default Optional<List<@Nullable E>> errs() {
    if (isErr()) {
      return Optional.of(unwrapErrs());
    }
    return Optional.empty();
  }

  /**
   * Returns this {@code Ok}, or {@code fallback} when this is {@code Err}.
   *
   * @param fallback the fallback validation
   * @return this validation for {@code Ok}, otherwise {@code fallback}
   * @throws NullPointerException if {@code fallback} is {@code null}
   */
  default Validated<T, E> orElse(@NonNull Validated<? extends T, E> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    if (isOk()) {
      return this;
    }
    @SuppressWarnings("unchecked")
    final var other = (Validated<T, E>) fallback;
    return other;
  }

  /**
   * Returns this {@code Ok}, or a supplied {@code Validated} when this is {@code Err}.
   *
   * @param fallback the supplier for the fallback validation
   * @return this validation for {@code Ok}, otherwise the supplied validation
   * @throws NullPointerException if {@code fallback} or its result is {@code null}
   */
  default Validated<T, E> orElseGet(
      @NonNull Supplier<? extends Validated<? extends T, E>> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    if (isOk()) {
      return this;
    }
    @SuppressWarnings("unchecked")
    final var other = (Validated<T, E>) Objects.requireNonNull(fallback.get(), "validation");
    return other;
  }

  /**
   * Applies a validated function to this validated value, accumulating errors.
   *
   * @param function validated function
   * @param <U> mapped success type
   * @return applied validation with accumulated errors
   * @throws NullPointerException if {@code function} is {@code null}
   */
  default <U> @NonNull Validated<U, E> ap(
      @NonNull Validated<? extends Function<? super @Nullable T, ? extends U>, E> function) {
    Objects.requireNonNull(function, "function");
    return function.zip(this, (mapper, value) -> mapper.apply(value));
  }

  /**
   * Adds context to {@code Err} by prepending the supplied error.
   *
   * @param context supplier for the context error
   * @return validation with prepended context error when {@code Err}
   * @throws NullPointerException if {@code context} is {@code null}
   */
  default Validated<T, E> withContext(@NonNull Supplier<? extends @Nullable E> context) {
    Objects.requireNonNull(context, "context");
    if (isOk()) {
      return this;
    }
    final var errors = new ArrayList<@Nullable E>(unwrapErrs().size() + 1);
    errors.add(context.get());
    errors.addAll(unwrapErrs());
    return errList(errors);
  }

  /**
   * Combines two {@code Ok} values with {@code combiner}, accumulating errors otherwise.
   *
   * @param other the other validation
   * @param combiner combiner for success values
   * @param <U> other success value type
   * @param <V> combined success value type
   * @return combined {@code Ok}, or accumulated {@code Err}
   * @throws NullPointerException if {@code other} or {@code combiner} is {@code null}
   */
  default <U, V> @NonNull Validated<V, E> zip(
      @NonNull Validated<? extends U, E> other,
      @NonNull BiFunction<? super @Nullable T, ? super @Nullable U, ? extends V> combiner) {
    Objects.requireNonNull(other, "other");
    Objects.requireNonNull(combiner, "combiner");
    if (isOk() && other.isOk()) {
      return ok(combiner.apply(unwrap(), other.unwrap()));
    }
    if (isErr() && other.isErr()) {
      return errList(mergeErrors(unwrapErrs(), other.unwrapErrs()));
    }
    if (isErr()) {
      @SuppressWarnings("unchecked")
      final var self = (Validated<V, E>) this;
      return self;
    }
    @SuppressWarnings("unchecked")
    final var otherErr = (Validated<V, E>) other;
    return otherErr;
  }

  /**
   * Combines two {@code Ok} values with {@code combiner}, accumulating errors otherwise.
   *
   * @param other the other validation
   * @param combiner combiner for success values
   * @return combined {@code Ok}, or accumulated {@code Err}
   * @throws NullPointerException if {@code other} or {@code combiner} is {@code null}
   */
  default @NonNull Validated<T, E> combine(
      @NonNull Validated<? extends T, E> other,
      @NonNull BiFunction<? super @Nullable T, ? super @Nullable T, ? extends T> combiner) {
    Objects.requireNonNull(other, "other");
    Objects.requireNonNull(combiner, "combiner");
    return zip(other, combiner);
  }

  /**
   * Maps the error list while preserving successes.
   *
   * @param mapper mapper for error lists
   * @param <F> mapped error type
   * @return mapped {@code Err} with an unmodifiable error list, or the same {@code Ok}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default <F> @NonNull Validated<T, F> mapErrs(
      @NonNull Function<? super @NonNull List<@Nullable E>, ? extends List<@Nullable F>> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      @SuppressWarnings("unchecked")
      final var self = (Validated<T, F>) this;
      return self;
    }
    final var mapped = Objects.requireNonNull(mapper.apply(unwrapErrs()), "errors");
    return new Err<>(mapped);
  }

  /**
   * Performs a side effect for {@code Ok} without changing the result.
   *
   * @param action consumer for the success value
   * @return this validation
   * @throws NullPointerException if {@code action} is {@code null}
   */
  default Validated<T, E> peek(@NonNull Consumer<? super @Nullable T> action) {
    Objects.requireNonNull(action, "action");
    if (isOk()) {
      action.accept(unwrap());
    }
    return this;
  }

  /**
   * Performs a side effect for {@code Err} without changing the result.
   *
   * @param action consumer for the error list
   * @return this validation
   * @throws NullPointerException if {@code action} is {@code null}
   */
  default Validated<T, E> peekErrs(@NonNull Consumer<? super @NonNull List<@Nullable E>> action) {
    Objects.requireNonNull(action, "action");
    if (isErr()) {
      action.accept(unwrapErrs());
    }
    return this;
  }

  /**
   * Performs a side effect on the current case without changing the result.
   *
   * @param onErrs consumer for the error list
   * @param onOk consumer for the success value
   * @return this validation
   * @throws NullPointerException if {@code onErrs} or {@code onOk} is {@code null}
   */
  default Validated<T, E> peekBoth(
      @NonNull Consumer<? super @NonNull List<@Nullable E>> onErrs,
      @NonNull Consumer<? super @Nullable T> onOk) {
    Objects.requireNonNull(onErrs, "onErrs");
    Objects.requireNonNull(onOk, "onOk");
    if (isOk()) {
      onOk.accept(unwrap());
    } else {
      onErrs.accept(unwrapErrs());
    }
    return this;
  }

  /**
   * Returns {@code true} if this is {@code Ok} and the value equals {@code other}.
   *
   * @param other value to compare with the success value
   * @return {@code true} when {@code Ok} and values are equal
   */
  default boolean contains(@Nullable T other) {
    return isOk() && Objects.equals(unwrap(), other);
  }

  /**
   * Returns {@code true} if this is {@code Err} and the error list contains {@code other}.
   *
   * @param other value to compare with error elements
   * @return {@code true} when {@code Err} and errors contain the value
   */
  default boolean containsErr(@Nullable E other) {
    return isErr() && unwrapErrs().contains(other);
  }

  /**
   * Partition validations into success values and accumulated error values.
   *
   * @param validations validations to partition
   * @param <T> success value type
   * @param <E> error value type
   * @return partitioned validations with unmodifiable lists
   * @throws NullPointerException if {@code validations} or any element is {@code null}
   */
  static <T, E> @NonNull Partition<T, E> partition(
      @NonNull Collection<? extends Validated<? extends T, ? extends E>> validations) {
    Objects.requireNonNull(validations, "validations");
    final var oks = new ArrayList<T>();
    final var errs = new ArrayList<@Nullable E>();
    for (final var validation : validations) {
      Objects.requireNonNull(validation, "validation");
      if (validation.isOk()) {
        oks.add(validation.unwrap());
      } else {
        errs.addAll(validation.unwrapErrs());
      }
    }
    return new Partition<>(unmodifiableCopy(oks), unmodifiableCopy(errs));
  }

  /**
   * Converts a collection of validations into a validation of list, accumulating errors.
   *
   * @param validations validations to sequence
   * @param <T> success value type
   * @param <E> error value type
   * @return sequenced validation with an unmodifiable success list or error list
   * @throws NullPointerException if {@code validations} or any element is {@code null}
   */
  static <T, E> @NonNull Validated<List<T>, E> sequence(
      @NonNull Collection<? extends Validated<? extends T, E>> validations) {
    Objects.requireNonNull(validations, "validations");
    final var values = new ArrayList<T>();
    final var errors = new ArrayList<@Nullable E>();
    for (final var validation : validations) {
      Objects.requireNonNull(validation, "validation");
      if (validation.isOk()) {
        values.add(validation.unwrap());
      } else {
        errors.addAll(validation.unwrapErrs());
      }
    }
    if (errors.isEmpty()) {
      return ok(unmodifiableCopy(values));
    }
    return errList(errors);
  }

  /**
   * Applies {@code mapper} to each value and accumulates errors across results.
   *
   * @param values values to validate
   * @param mapper mapper producing validations
   * @param <T> input value type
   * @param <U> success value type
   * @param <E> error value type
   * @return traversed validation with an unmodifiable success list or error list
   * @throws NullPointerException if {@code values}, {@code mapper}, or any mapped validation is
   *     {@code null}
   */
  static <T, U, E> @NonNull Validated<List<U>, E> traverse(
      @NonNull Collection<? extends T> values,
      @NonNull Function<? super T, ? extends Validated<? extends U, E>> mapper) {
    Objects.requireNonNull(values, "values");
    Objects.requireNonNull(mapper, "mapper");
    final var results = new ArrayList<U>();
    final var errors = new ArrayList<@Nullable E>();
    for (final var value : values) {
      final var validation = Objects.requireNonNull(mapper.apply(value), "validation");
      if (validation.isOk()) {
        results.add(validation.unwrap());
      } else {
        errors.addAll(validation.unwrapErrs());
      }
    }
    if (errors.isEmpty()) {
      return ok(unmodifiableCopy(results));
    }
    return errList(errors);
  }

  /**
   * Combines all {@code Ok} values using {@code combiner}, accumulating errors.
   *
   * @param validations validations to combine
   * @param combiner combiner for success values
   * @param <T> success value type
   * @param <E> error value type
   * @return combined validation with an unmodifiable error list; returns {@code Ok(null)} when
   *     empty and error-free
   * @throws NullPointerException if {@code validations} or {@code combiner} is {@code null}
   */
  static <T, E> @NonNull Validated<T, E> combineAll(
      @NonNull Collection<? extends Validated<? extends T, E>> validations,
      @NonNull BiFunction<? super @Nullable T, ? super @Nullable T, ? extends T> combiner) {
    Objects.requireNonNull(validations, "validations");
    Objects.requireNonNull(combiner, "combiner");
    final var errors = new ArrayList<@Nullable E>();
    var hasValue = false;
    @Nullable T combined = null;
    for (final var validation : validations) {
      Objects.requireNonNull(validation, "validation");
      if (validation.isOk()) {
        if (!hasValue) {
          combined = validation.unwrap();
          hasValue = true;
        } else {
          combined = combiner.apply(combined, validation.unwrap());
        }
      } else {
        errors.addAll(validation.unwrapErrs());
      }
    }
    if (!errors.isEmpty()) {
      return errList(errors);
    }
    return ok(combined);
  }

  private static <T, E> @NonNull Validated<T, E> errList(@NonNull List<@Nullable E> errors) {
    return new Err<>(errors);
  }

  private static <T> @NonNull List<@Nullable T> unmodifiableCopy(
      @NonNull Collection<? extends @Nullable T> values) {
    return Collections.unmodifiableList(new ArrayList<>(values));
  }

  private static <E> @NonNull List<@Nullable E> mergeErrors(
      @NonNull List<@Nullable E> first, @NonNull List<@Nullable E> second) {
    final var merged = new ArrayList<@Nullable E>(first.size() + second.size());
    merged.addAll(first);
    merged.addAll(second);
    return merged;
  }

  /**
   * Holder for partitioned validations.
   *
   * @param oks unmodifiable success values
   * @param errs unmodifiable error values
   * @param <T> success value type
   * @param <E> error value type
   */
  record Partition<T, E>(@NonNull List<T> oks, @NonNull List<@Nullable E> errs) {}

  /**
   * A successful validation.
   *
   * @param value the success value, possibly {@code null}
   */
  record Ok<T, E>(@Nullable T value) implements Validated<T, E> {
    /**
     * Returns {@code true} for {@code Ok}.
     *
     * @return {@code true}
     */
    @Override
    public boolean isOk() {
      return true;
    }

    /**
     * Returns the success value.
     *
     * @return the success value, possibly {@code null}
     */
    @Override
    public @Nullable T unwrap() {
      return value;
    }

    /**
     * Throws because this is {@code Ok}.
     *
     * @return never returns normally
     * @throws IllegalStateException always
     */
    @Override
    public @NonNull List<@Nullable E> unwrapErrs() {
      throw new IllegalStateException("Validated is Ok");
    }
  }

  /**
   * A failed validation with accumulated errors.
   *
   * @param errors the unmodifiable errors list
   */
  record Err<T, E>(@NonNull List<@Nullable E> errors) implements Validated<T, E> {
    public Err {
      Objects.requireNonNull(errors, "errors");
      errors = Collections.unmodifiableList(errors);
    }

    Err(@NonNull Collection<? extends @Nullable E> errors) {
      this(new ArrayList<>(Objects.requireNonNull(errors, "errors")));
    }

    /**
     * Returns {@code false} for {@code Err}.
     *
     * @return {@code false}
     */
    @Override
    public boolean isOk() {
      return false;
    }

    /**
     * Throws because this is {@code Err}.
     *
     * @return never returns normally
     * @throws IllegalStateException always
     */
    @Override
    public @Nullable T unwrap() {
      throw new IllegalStateException("Validated is Err");
    }

    /**
     * Returns the accumulated errors.
     *
     * @return unmodifiable list of errors
     */
    @Override
    public @NonNull List<@Nullable E> unwrapErrs() {
      return errors;
    }
  }
}
