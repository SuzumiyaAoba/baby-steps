package babysteps.fp;

import java.util.function.Function;
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

  /**
   * Returns the first element.
   *
   * @return the first element
   */
  public @Nullable A _1() {
    return first;
  }

  /**
   * Returns the second element.
   *
   * @return the second element
   */
  public @Nullable B _2() {
    return second;
  }

  /**
   * Swap the tuple elements.
   *
   * @return a swapped tuple
   */
  public @NonNull Tuple2<@Nullable B, @Nullable A> swap() {
    return new Tuple2<>(second, first);
  }

  /**
   * Map the first element.
   *
   * @param mapper mapper for the first element
   * @param <C> mapped first element type
   * @return a tuple with the mapped first element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <C> @NonNull Tuple2<@Nullable C, @Nullable B> mapFirst(
      @NonNull Function<? super @Nullable A, ? extends @Nullable C> mapper) {
    return new Tuple2<>(mapper.apply(first), second);
  }

  /**
   * Map the second element.
   *
   * @param mapper mapper for the second element
   * @param <D> mapped second element type
   * @return a tuple with the mapped second element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <D> @NonNull Tuple2<@Nullable A, @Nullable D> mapSecond(
      @NonNull Function<? super @Nullable B, ? extends @Nullable D> mapper) {
    return new Tuple2<>(first, mapper.apply(second));
  }

  /**
   * Map both elements.
   *
   * @param firstMapper mapper for the first element
   * @param secondMapper mapper for the second element
   * @param <C> mapped first element type
   * @param <D> mapped second element type
   * @return a tuple with both elements mapped
   * @throws NullPointerException if {@code firstMapper} or {@code secondMapper} is {@code null}
   */
  public <C, D> @NonNull Tuple2<@Nullable C, @Nullable D> bimap(
      @NonNull Function<? super @Nullable A, ? extends @Nullable C> firstMapper,
      @NonNull Function<? super @Nullable B, ? extends @Nullable D> secondMapper) {
    return new Tuple2<>(firstMapper.apply(first), secondMapper.apply(second));
  }
}
