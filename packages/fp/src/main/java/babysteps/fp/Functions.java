package babysteps.fp;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Utility functions for working with functional interfaces. */
public final class Functions {
  private Functions() {}

  private static final Object NULL_KEY = new Object();
  private static final Object NULL_VALUE = new Object();

  /**
   * Compose two functions with {@code after(before(x))} order.
   *
   * @param after the function applied second
   * @param before the function applied first
   * @param <A> input type
   * @param <B> intermediate type
   * @param <C> output type
   * @return composed function
   * @throws NullPointerException if {@code after} or {@code before} is {@code null}
   */
  public static <A, B, C> @NonNull Function<@Nullable A, @Nullable C> compose(
      @NonNull Function<? super @Nullable B, ? extends @Nullable C> after,
      @NonNull Function<? super @Nullable A, ? extends @Nullable B> before) {
    Objects.requireNonNull(after, "after");
    Objects.requireNonNull(before, "before");
    return value -> after.apply(before.apply(value));
  }

  /**
   * Pipe two functions with {@code after(before(x))} order.
   *
   * @param before the function applied first
   * @param after the function applied second
   * @param <A> input type
   * @param <B> intermediate type
   * @param <C> output type
   * @return piped function
   * @throws NullPointerException if {@code before} or {@code after} is {@code null}
   */
  public static <A, B, C> @NonNull Function<@Nullable A, @Nullable C> pipe(
      @NonNull Function<? super @Nullable A, ? extends @Nullable B> before,
      @NonNull Function<? super @Nullable B, ? extends @Nullable C> after) {
    Objects.requireNonNull(before, "before");
    Objects.requireNonNull(after, "after");
    return value -> after.apply(before.apply(value));
  }

  /**
   * Curry a {@link BiFunction} into a function that returns another function.
   *
   * @param fn the function to curry
   * @param <A> first input type
   * @param <B> second input type
   * @param <R> output type
   * @return curried function
   * @throws NullPointerException if {@code fn} is {@code null}
   */
  public static <A, B, R> @NonNull Function<@Nullable A, Function<@Nullable B, @Nullable R>> curry(
      @NonNull BiFunction<? super @Nullable A, ? super @Nullable B, ? extends @Nullable R> fn) {
    Objects.requireNonNull(fn, "fn");
    return a -> b -> fn.apply(a, b);
  }

  /**
   * Convert a {@link BiFunction} to a function that accepts a {@link Tuple2}.
   *
   * @param fn the function to tuple
   * @param <A> first input type
   * @param <B> second input type
   * @param <R> output type
   * @return tupled function
   * @throws NullPointerException if {@code fn} is {@code null}
   */
  public static <A, B, R> @NonNull Function<Tuple2<@Nullable A, @Nullable B>, @Nullable R> tupled(
      @NonNull BiFunction<? super @Nullable A, ? super @Nullable B, ? extends @Nullable R> fn) {
    Objects.requireNonNull(fn, "fn");
    return tuple -> fn.apply(tuple.first(), tuple.second());
  }

  /**
   * Convert a function that accepts a {@link Tuple2} into a {@link BiFunction}.
   *
   * @param fn the function to untuple
   * @param <A> first input type
   * @param <B> second input type
   * @param <R> output type
   * @return untupled function
   * @throws NullPointerException if {@code fn} is {@code null}
   */
  public static <A, B, R> @NonNull BiFunction<@Nullable A, @Nullable B, @Nullable R> untupled(
      @NonNull Function<? super Tuple2<@Nullable A, @Nullable B>, ? extends @Nullable R> fn) {
    Objects.requireNonNull(fn, "fn");
    return (a, b) -> fn.apply(new Tuple2<>(a, b));
  }

  /**
   * Flip the arguments of a {@link BiFunction}.
   *
   * @param fn the function to flip
   * @param <A> first input type
   * @param <B> second input type
   * @param <R> output type
   * @return flipped function
   * @throws NullPointerException if {@code fn} is {@code null}
   */
  public static <A, B, R> @NonNull BiFunction<@Nullable B, @Nullable A, @Nullable R> flip(
      @NonNull BiFunction<? super @Nullable A, ? super @Nullable B, ? extends @Nullable R> fn) {
    Objects.requireNonNull(fn, "fn");
    return (b, a) -> fn.apply(a, b);
  }

  /**
   * Partially apply the first argument of a {@link BiFunction}.
   *
   * @param fn the function to partially apply
   * @param value the fixed first argument
   * @param <A> first input type
   * @param <B> second input type
   * @param <R> output type
   * @return partially applied function
   * @throws NullPointerException if {@code fn} is {@code null}
   */
  public static <A, B, R> @NonNull Function<@Nullable B, @Nullable R> partial(
      @NonNull BiFunction<? super @Nullable A, ? super @Nullable B, ? extends @Nullable R> fn,
      @Nullable A value) {
    Objects.requireNonNull(fn, "fn");
    return b -> fn.apply(value, b);
  }

  /**
   * Memoize a function, caching results by input value (including {@code null}).
   *
   * @param fn the function to memoize
   * @param <A> input type
   * @param <R> output type
   * @return memoized function
   * @throws NullPointerException if {@code fn} is {@code null}
   */
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

  /**
   * Memoize a {@link BiFunction}, caching results by input pair (including {@code null} values).
   *
   * @param fn the function to memoize
   * @param <A> first input type
   * @param <B> second input type
   * @param <R> output type
   * @return memoized function
   * @throws NullPointerException if {@code fn} is {@code null}
   */
  public static <A, B, R> @NonNull BiFunction<@Nullable A, @Nullable B, @Nullable R> memoize(
      @NonNull BiFunction<? super @Nullable A, ? super @Nullable B, ? extends @Nullable R> fn) {
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
