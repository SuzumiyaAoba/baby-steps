package babysteps.core;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A container that captures exceptions from computations, yielding either {@code Success} or {@code
 * Failure}.
 *
 * <p>{@code T} may be {@code null} when using {@link #success(Object)} or {@link
 * #of(CheckedSupplier)}. Failures always carry a non-null {@link Throwable}.
 *
 * @param <T> success value type, possibly nullable
 */
public sealed interface Try<T> permits Try.Success, Try.Failure {
  /**
   * Executes a supplier and captures any thrown {@link Exception} as {@code Failure}.
   *
   * @param supplier the checked supplier
   * @param <T> success value type
   * @return a {@code Success} for normal completion, otherwise {@code Failure}
   * @throws NullPointerException if {@code supplier} is {@code null}
   */
  static <T> @NonNull Try<T> of(@NonNull CheckedSupplier<? extends T> supplier) {
    Objects.requireNonNull(supplier, "supplier");
    try {
      return success(supplier.get());
    } catch (Exception exception) {
      return failure(exception);
    }
  }

  /**
   * Creates a {@code Success} result.
   *
   * @param value the success value, possibly {@code null}
   * @param <T> success value type
   * @return a {@code Success}
   */
  static <T> @NonNull Try<T> success(@Nullable T value) {
    return new Success<>(value);
  }

  /**
   * Creates a {@code Failure} result.
   *
   * @param error the non-null error
   * @param <T> success value type
   * @return a {@code Failure}
   * @throws NullPointerException if {@code error} is {@code null}
   */
  static <T> @NonNull Try<T> failure(@NonNull Throwable error) {
    return new Failure<>(Objects.requireNonNull(error, "error"));
  }

  /**
   * @return {@code true} when this is {@code Success}
   */
  boolean isSuccess();

  /**
   * @return {@code true} when this is {@code Failure}
   */
  default boolean isFailure() {
    return !isSuccess();
  }

  /**
   * Returns the success value.
   *
   * @return the success value, possibly {@code null}
   * @throws IllegalStateException if this is {@code Failure}
   */
  @Nullable T get();

  /**
   * Returns the failure cause.
   *
   * @return the non-null failure cause
   * @throws IllegalStateException if this is {@code Success}
   */
  @NonNull Throwable getCause();

  /**
   * Returns the success value or {@code fallback} if this is {@code Failure}.
   *
   * @param fallback fallback value, possibly {@code null}
   * @return the success value or {@code fallback}
   */
  default @Nullable T getOrElse(@Nullable T fallback) {
    return isSuccess() ? get() : fallback;
  }

  /**
   * Returns the success value or a supplied fallback if this is {@code Failure}.
   *
   * @param fallback fallback supplier
   * @return the success value or supplied fallback, possibly {@code null}
   * @throws NullPointerException if {@code fallback} is {@code null}
   */
  default @Nullable T getOrElseGet(@NonNull Supplier<? extends @Nullable T> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    return isSuccess() ? get() : fallback.get();
  }

  /**
   * Maps the success value, capturing mapper exceptions as {@code Failure}.
   *
   * @param mapper mapper for the success value
   * @param <U> mapped value type
   * @return mapped {@code Success} or {@code Failure}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <U> @NonNull Try<U> map(@NonNull Function<? super @Nullable T, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isFailure()) {
      return failure(getCause());
    }
    try {
      return success(mapper.apply(get()));
    } catch (Exception exception) {
      return failure(exception);
    }
  }

  /**
   * Flat-maps the success value, capturing mapper exceptions as {@code Failure}.
   *
   * @param mapper mapper for the success value
   * @param <U> mapped value type
   * @return mapped {@code Try} or {@code Failure}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default <U> @NonNull Try<U> flatMap(
      @NonNull Function<? super @Nullable T, ? extends Try<? extends U>> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isFailure()) {
      return failure(getCause());
    }
    try {
      @SuppressWarnings("unchecked")
      final var mapped = (Try<U>) mapper.apply(get());
      return Objects.requireNonNull(mapped, "mapped");
    } catch (Exception exception) {
      return failure(exception);
    }
  }

  /**
   * Recovers from failure by mapping the error into a success value.
   *
   * @param mapper mapper for the failure cause
   * @return a recovered {@code Success} or the original {@code Success}
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default @NonNull Try<T> recover(
      @NonNull Function<? super Throwable, ? extends @Nullable T> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isSuccess()) {
      return this;
    }
    try {
      return success(mapper.apply(getCause()));
    } catch (Exception exception) {
      return failure(exception);
    }
  }

  /**
   * Recovers from failure by mapping the error into another {@code Try}.
   *
   * @param mapper mapper for the failure cause
   * @return a recovered {@code Try} or the original {@code Success}
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  default @NonNull Try<T> recoverWith(
      @NonNull Function<? super Throwable, ? extends Try<? extends @Nullable T>> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isSuccess()) {
      return this;
    }
    try {
      @SuppressWarnings("unchecked")
      final var mapped = (Try<T>) mapper.apply(getCause());
      return Objects.requireNonNull(mapped, "mapped");
    } catch (Exception exception) {
      return failure(exception);
    }
  }

  /**
   * Converts this {@code Try} to a {@link Result}.
   *
   * @return {@code Result.ok} for {@code Success}, otherwise {@code Result.err}
   */
  default @NonNull Result<T, @NonNull Throwable> toResult() {
    if (isSuccess()) {
      return Result.ok(get());
    }
    return Result.err(getCause());
  }

  /**
   * Converts this {@code Try} to an {@link Option}.
   *
   * @return {@code Some} for {@code Success}, otherwise {@code None}
   */
  default @NonNull Option<@NonNull T> toOption() {
    if (isSuccess()) {
      return Option.ofNullable(get());
    }
    return Option.none();
  }

  /**
   * Successful case.
   *
   * @param <T> success value type
   */
  record Success<T>(@Nullable T value) implements Try<T> {
    @Override
    public boolean isSuccess() {
      return true;
    }

    @Override
    public @Nullable T get() {
      return value;
    }

    @Override
    public @NonNull Throwable getCause() {
      throw new IllegalStateException("Try is Success");
    }
  }

  /**
   * Failure case.
   *
   * @param <T> success value type
   */
  record Failure<T>(@NonNull Throwable error) implements Try<T> {
    public Failure {
      Objects.requireNonNull(error, "error");
    }

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    public @Nullable T get() {
      throw new IllegalStateException("Try is Failure");
    }

    @Override
    public @NonNull Throwable getCause() {
      return error;
    }
  }

  /**
   * Supplier that allows checked exceptions.
   *
   * @param <T> supplied value type
   */
  @FunctionalInterface
  interface CheckedSupplier<T> {
    T get() throws Exception;
  }
}
