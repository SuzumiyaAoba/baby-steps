package babysteps.fp;

import java.util.function.Function;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Simple tuple of four values.
 *
 * @param first first value
 * @param second second value
 * @param third third value
 * @param fourth fourth value
 * @param <A> first value type
 * @param <B> second value type
 * @param <C> third value type
 * @param <D> fourth value type
 */
public record Tuple4<A, B, C, D>(
    @Nullable A first, @Nullable B second, @Nullable C third, @Nullable D fourth) {
  /**
   * Creates a {@link Tuple4} from the given values.
   *
   * @param first first value
   * @param second second value
   * @param third third value
   * @param fourth fourth value
   * @param <A> first value type
   * @param <B> second value type
   * @param <C> third value type
   * @param <D> fourth value type
   * @return a tuple holding the provided values
   */
  public static <A, B, C, D> @NonNull Tuple4<@Nullable A, @Nullable B, @Nullable C, @Nullable D> of(
      @Nullable A first, @Nullable B second, @Nullable C third, @Nullable D fourth) {
    return new Tuple4<>(first, second, third, fourth);
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
   * Returns the third element.
   *
   * @return the third element
   */
  public @Nullable C _3() {
    return third;
  }

  /**
   * Returns the fourth element.
   *
   * @return the fourth element
   */
  public @Nullable D _4() {
    return fourth;
  }

  /**
   * Map the first element.
   *
   * @param mapper mapper for the first element
   * @param <E> mapped first element type
   * @return a tuple with the mapped first element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <E> @NonNull Tuple4<@Nullable E, @Nullable B, @Nullable C, @Nullable D> mapFirst(
      @NonNull Function<? super @Nullable A, ? extends @Nullable E> mapper) {
    return new Tuple4<>(mapper.apply(first), second, third, fourth);
  }

  /**
   * Map the second element.
   *
   * @param mapper mapper for the second element
   * @param <E> mapped second element type
   * @return a tuple with the mapped second element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <E> @NonNull Tuple4<@Nullable A, @Nullable E, @Nullable C, @Nullable D> mapSecond(
      @NonNull Function<? super @Nullable B, ? extends @Nullable E> mapper) {
    return new Tuple4<>(first, mapper.apply(second), third, fourth);
  }

  /**
   * Map the third element.
   *
   * @param mapper mapper for the third element
   * @param <E> mapped third element type
   * @return a tuple with the mapped third element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <E> @NonNull Tuple4<@Nullable A, @Nullable B, @Nullable E, @Nullable D> mapThird(
      @NonNull Function<? super @Nullable C, ? extends @Nullable E> mapper) {
    return new Tuple4<>(first, second, mapper.apply(third), fourth);
  }

  /**
   * Map the fourth element.
   *
   * @param mapper mapper for the fourth element
   * @param <E> mapped fourth element type
   * @return a tuple with the mapped fourth element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <E> @NonNull Tuple4<@Nullable A, @Nullable B, @Nullable C, @Nullable E> mapFourth(
      @NonNull Function<? super @Nullable D, ? extends @Nullable E> mapper) {
    return new Tuple4<>(first, second, third, mapper.apply(fourth));
  }

  /**
   * Map the first two elements.
   *
   * @param firstMapper mapper for the first element
   * @param secondMapper mapper for the second element
   * @param <E> mapped first element type
   * @param <F> mapped second element type
   * @return a tuple with the first two elements mapped
   * @throws NullPointerException if {@code firstMapper} or {@code secondMapper} is {@code null}
   */
  public <E, F> @NonNull Tuple4<@Nullable E, @Nullable F, @Nullable C, @Nullable D> bimap(
      @NonNull Function<? super @Nullable A, ? extends @Nullable E> firstMapper,
      @NonNull Function<? super @Nullable B, ? extends @Nullable F> secondMapper) {
    return new Tuple4<>(firstMapper.apply(first), secondMapper.apply(second), third, fourth);
  }

  /**
   * Map the first three elements.
   *
   * @param firstMapper mapper for the first element
   * @param secondMapper mapper for the second element
   * @param thirdMapper mapper for the third element
   * @param <E> mapped first element type
   * @param <F> mapped second element type
   * @param <G> mapped third element type
   * @return a tuple with the first three elements mapped
   * @throws NullPointerException if any mapper is {@code null}
   */
  public <E, F, G> @NonNull Tuple4<@Nullable E, @Nullable F, @Nullable G, @Nullable D> trimap(
      @NonNull Function<? super @Nullable A, ? extends @Nullable E> firstMapper,
      @NonNull Function<? super @Nullable B, ? extends @Nullable F> secondMapper,
      @NonNull Function<? super @Nullable C, ? extends @Nullable G> thirdMapper) {
    return new Tuple4<>(
        firstMapper.apply(first), secondMapper.apply(second), thirdMapper.apply(third), fourth);
  }

  /**
   * Map all elements.
   *
   * @param firstMapper mapper for the first element
   * @param secondMapper mapper for the second element
   * @param thirdMapper mapper for the third element
   * @param fourthMapper mapper for the fourth element
   * @param <E> mapped first element type
   * @param <F> mapped second element type
   * @param <G> mapped third element type
   * @param <H> mapped fourth element type
   * @return a tuple with all elements mapped
   * @throws NullPointerException if any mapper is {@code null}
   */
  public <E, F, G, H> @NonNull Tuple4<@Nullable E, @Nullable F, @Nullable G, @Nullable H> quadmap(
      @NonNull Function<? super @Nullable A, ? extends @Nullable E> firstMapper,
      @NonNull Function<? super @Nullable B, ? extends @Nullable F> secondMapper,
      @NonNull Function<? super @Nullable C, ? extends @Nullable G> thirdMapper,
      @NonNull Function<? super @Nullable D, ? extends @Nullable H> fourthMapper) {
    return new Tuple4<>(
        firstMapper.apply(first),
        secondMapper.apply(second),
        thirdMapper.apply(third),
        fourthMapper.apply(fourth));
  }
}
