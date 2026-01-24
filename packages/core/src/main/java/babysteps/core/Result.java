package babysteps.core;

import java.util.Objects;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public sealed interface Result<T, E> permits Result.Ok, Result.Err {
  static <T, E> Result<T, E> ok(@Nullable T value) {
    return new Ok<>(value);
  }

  static <T, E> Result<T, E> err(@NonNull E error) {
    return new Err<>(Objects.requireNonNull(error, "error"));
  }

  boolean isOk();

  default boolean isErr() {
    return !isOk();
  }

  @Nullable T unwrap();

  @NonNull E unwrapErr();

  default @Nullable T unwrapOr(@Nullable T fallback) {
    return isOk() ? unwrap() : fallback;
  }

  default <U> Result<U, E> map(@NonNull Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isErr()) {
      return err(unwrapErr());
    }
    return ok(mapper.apply(unwrap()));
  }

  default <F> Result<T, F> mapErr(@NonNull Function<? super E, ? extends F> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isOk()) {
      return ok(unwrap());
    }
    return err(mapper.apply(unwrapErr()));
  }

  default <U> Result<U, E> flatMap(
      @NonNull Function<? super T, ? extends Result<? extends U, E>> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isErr()) {
      return err(unwrapErr());
    }
    @SuppressWarnings("unchecked")
    final var mapped = (Result<U, E>) mapper.apply(unwrap());
    return Objects.requireNonNull(mapped, "mapped");
  }

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
