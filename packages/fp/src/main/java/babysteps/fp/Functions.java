package babysteps.fp;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class Functions {
  private Functions() {}

  private static final Object NULL_KEY = new Object();
  private static final Object NULL_VALUE = new Object();

  public static <A, B, C> @NonNull Function<@Nullable A, @Nullable C> compose(
      @NonNull Function<? super @Nullable B, ? extends @Nullable C> after,
      @NonNull Function<? super @Nullable A, ? extends @Nullable B> before) {
    Objects.requireNonNull(after, "after");
    Objects.requireNonNull(before, "before");
    return value -> after.apply(before.apply(value));
  }

  public static <A, B, C> @NonNull Function<@Nullable A, @Nullable C> pipe(
      @NonNull Function<? super @Nullable A, ? extends @Nullable B> before,
      @NonNull Function<? super @Nullable B, ? extends @Nullable C> after) {
    Objects.requireNonNull(before, "before");
    Objects.requireNonNull(after, "after");
    return value -> after.apply(before.apply(value));
  }

  public static <A, B, R>
      @NonNull Function<@Nullable A, Function<@Nullable B, @Nullable R>> curry(
          @NonNull BiFunction<? super @Nullable A, ? super @Nullable B, ? extends @Nullable R> fn) {
    Objects.requireNonNull(fn, "fn");
    return a -> b -> fn.apply(a, b);
  }

  public static <A, B, R>
      @NonNull Function<Tuple2<@Nullable A, @Nullable B>, @Nullable R> tupled(
          @NonNull BiFunction<? super @Nullable A, ? super @Nullable B, ? extends @Nullable R> fn) {
    Objects.requireNonNull(fn, "fn");
    return tuple -> fn.apply(tuple.first(), tuple.second());
  }

  public static <A, B, R>
      @NonNull BiFunction<@Nullable A, @Nullable B, @Nullable R> untupled(
          @NonNull
              Function<? super Tuple2<@Nullable A, @Nullable B>, ? extends @Nullable R> fn) {
    Objects.requireNonNull(fn, "fn");
    return (a, b) -> fn.apply(new Tuple2<>(a, b));
  }

  public static <A, B, R> @NonNull BiFunction<@Nullable B, @Nullable A, @Nullable R> flip(
      @NonNull BiFunction<? super @Nullable A, ? super @Nullable B, ? extends @Nullable R> fn) {
    Objects.requireNonNull(fn, "fn");
    return (b, a) -> fn.apply(a, b);
  }

  public static <A, B, R> @NonNull Function<@Nullable B, @Nullable R> partial(
      @NonNull BiFunction<? super @Nullable A, ? super @Nullable B, ? extends @Nullable R> fn,
      @Nullable A value) {
    Objects.requireNonNull(fn, "fn");
    return b -> fn.apply(value, b);
  }

  public static <A, R> @NonNull Function<@Nullable A, @Nullable R> memoize(
      @NonNull Function<? super @Nullable A, ? extends @Nullable R> fn) {
    Objects.requireNonNull(fn, "fn");
    final var cache = new ConcurrentHashMap<Object, Object>();
    return value -> {
      final var key = value == null ? NULL_KEY : value;
      final var cached =
          cache.computeIfAbsent(
              key,
              ignored -> {
                final var result = fn.apply(value);
                return result == null ? NULL_VALUE : result;
              });
      @SuppressWarnings("unchecked")
      final var result = (R) cached;
      return result == NULL_VALUE ? null : result;
    };
  }

  public static <A, B, R>
      @NonNull BiFunction<@Nullable A, @Nullable B, @Nullable R> memoize(
          @NonNull
              BiFunction<? super @Nullable A, ? super @Nullable B, ? extends @Nullable R> fn) {
    Objects.requireNonNull(fn, "fn");
    final var cache = new ConcurrentHashMap<Tuple2<@Nullable A, @Nullable B>, Object>();
    return (a, b) -> {
      final var key = new Tuple2<@Nullable A, @Nullable B>(a, b);
      final var cached =
          cache.computeIfAbsent(
              key,
              ignored -> {
                final var result = fn.apply(a, b);
                return result == null ? NULL_VALUE : result;
              });
      @SuppressWarnings("unchecked")
      final var result = (R) cached;
      return result == NULL_VALUE ? null : result;
    };
  }
}
