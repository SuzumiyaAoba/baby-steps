package babysteps.fp;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Simple tuple of two values.
 *
 * @param first first value
 * @param second second value
 * @param <A> first value type
 * @param <B> second value type
 */
public record Tuple2<A, B>(@Nullable A first, @Nullable B second) {
  /**
   * Creates a {@link Tuple2} from the given values.
   *
   * @param first first value
   * @param second second value
   * @param <A> first value type
   * @param <B> second value type
   * @return a tuple holding the provided values
   */
  public static <A, B> @NonNull Tuple2<@Nullable A, @Nullable B> of(
      @Nullable A first, @Nullable B second) {
    return new Tuple2<>(first, second);
  }
}
