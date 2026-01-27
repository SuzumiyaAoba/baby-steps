package babysteps.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
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
   * @return list of errors
   * @throws IllegalStateException if this is {@code Ok}
   */
  @NonNull List<@Nullable E> unwrapErrs();

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
      return errList(unwrapErrs());
    }
    return ok(mapper.apply(unwrap()));
  }

  /**
   * Maps errors while preserving successes.
   *
   * @param mapper mapper for error elements
   * @param <F> mapped error type
   * @return mapped {@code Err}, or the same {@code Ok}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <F> @NonNull Validated<T, F> mapErr(
      @NonNull Function<? super @Nullable E, ? extends F> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      return ok(unwrap());
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
      return errList(unwrapErrs());
    }
    return errList(other.unwrapErrs());
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

  private static <T, E> @NonNull Validated<T, E> errList(@NonNull List<@Nullable E> errors) {
    return new Err<>(errors);
  }

  private static <E> @NonNull List<@Nullable E> mergeErrors(
      @NonNull List<@Nullable E> first, @NonNull List<@Nullable E> second) {
    final var merged = new ArrayList<@Nullable E>(first.size() + second.size());
    merged.addAll(first);
    merged.addAll(second);
    return Collections.unmodifiableList(merged);
  }

  /**
   * A successful validation.
   *
   * @param value the success value, possibly {@code null}
   */
  record Ok<T, E>(@Nullable T value) implements Validated<T, E> {
    @Override
    public boolean isOk() {
      return true;
    }

    @Override
    public @Nullable T unwrap() {
      return value;
    }

    @Override
    public @NonNull List<@Nullable E> unwrapErrs() {
      throw new IllegalStateException("Validated is Ok");
    }
  }

  /**
   * A failed validation with accumulated errors.
   *
   * @param errors the errors list
   */
  record Err<T, E>(@NonNull List<@Nullable E> errors) implements Validated<T, E> {
    public Err {
      Objects.requireNonNull(errors, "errors");
      errors = Collections.unmodifiableList(new ArrayList<>(errors));
    }

    Err(@NonNull Collection<? extends @Nullable E> errors) {
      this(new ArrayList<>(Objects.requireNonNull(errors, "errors")));
    }

    @Override
    public boolean isOk() {
      return false;
    }

    @Override
    public @Nullable T unwrap() {
      throw new IllegalStateException("Validated is Err");
    }

    @Override
    public @NonNull List<@Nullable E> unwrapErrs() {
      return errors;
    }
  }
}
