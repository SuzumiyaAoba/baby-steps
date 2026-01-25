package babysteps.fp;

/**
 * Simple tuple of two values.
 *
 * @param first first value
 * @param second second value
 * @param <A> first value type
 * @param <B> second value type
 */
public record Tuple2<A, B>(A first, B second) {
  public static <A, B> Tuple2<A, B> of(A first, B second) {
    return new Tuple2<>(first, second);
  }
}
