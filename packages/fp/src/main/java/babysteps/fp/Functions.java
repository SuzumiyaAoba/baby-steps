package babysteps.fp;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;

public final class Functions {
  private Functions() {}

  private static final Object NULL_KEY = new Object();
  private static final Object NULL_VALUE = new Object();

  public static <A, B, C> Function<A, C> compose(
      @NonNull Function<? super B, ? extends C> after,
      @NonNull Function<? super A, ? extends B> before) {
    Objects.requireNonNull(after, "after");
    Objects.requireNonNull(before, "before");
    return value -> after.apply(before.apply(value));
  }

  public static <A, B, C> Function<A, C> pipe(
      @NonNull Function<? super A, ? extends B> before,
      @NonNull Function<? super B, ? extends C> after) {
    Objects.requireNonNull(before, "before");
    Objects.requireNonNull(after, "after");
    return value -> after.apply(before.apply(value));
  }

  public static <A, B, C, R> Function<A, Function<B, R>> curry(
      @NonNull BiFunction<? super A, ? super B, ? extends R> fn) {
    Objects.requireNonNull(fn, "fn");
    return a -> b -> fn.apply(a, b);
  }

  public static <A, B, R> Function<Tuple2<A, B>, R> tupled(
      @NonNull BiFunction<? super A, ? super B, ? extends R> fn) {
    Objects.requireNonNull(fn, "fn");
    return tuple -> fn.apply(tuple.first(), tuple.second());
  }

  public static <A, B, R> BiFunction<A, B, R> untupled(
      @NonNull Function<? super Tuple2<A, B>, ? extends R> fn) {
    Objects.requireNonNull(fn, "fn");
    return (a, b) -> fn.apply(new Tuple2<>(a, b));
  }

  public static <A, B, R> BiFunction<B, A, R> flip(
      @NonNull BiFunction<? super A, ? super B, ? extends R> fn) {
    Objects.requireNonNull(fn, "fn");
    return (b, a) -> fn.apply(a, b);
  }

  public static <A, B, R> Function<B, R> partial(
      @NonNull BiFunction<? super A, ? super B, ? extends R> fn, A value) {
    Objects.requireNonNull(fn, "fn");
    return b -> fn.apply(value, b);
  }

  public static <A, R> Function<A, R> memoize(@NonNull Function<? super A, ? extends R> fn) {
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

  public static <A, B, R> BiFunction<A, B, R> memoize(
      @NonNull BiFunction<? super A, ? super B, ? extends R> fn) {
    Objects.requireNonNull(fn, "fn");
    final var cache = new ConcurrentHashMap<Tuple2<A, B>, Object>();
    return (a, b) -> {
      final var key = new Tuple2<>(a, b);
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
