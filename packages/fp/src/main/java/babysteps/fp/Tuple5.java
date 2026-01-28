package babysteps.fp;

import java.util.function.Function;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Simple tuple of five values.
 *
 * @param first first value
 * @param second second value
 * @param third third value
 * @param fourth fourth value
 * @param fifth fifth value
 * @param <A> first value type
 * @param <B> second value type
 * @param <C> third value type
 * @param <D> fourth value type
 * @param <E> fifth value type
 */
public record Tuple5<A, B, C, D, E>(
    @Nullable A first,
    @Nullable B second,
    @Nullable C third,
    @Nullable D fourth,
    @Nullable E fifth) {
  /**
   * Creates a {@link Tuple5} from the given values.
   *
   * @param first first value
   * @param second second value
   * @param third third value
   * @param fourth fourth value
   * @param fifth fifth value
   * @param <A> first value type
   * @param <B> second value type
   * @param <C> third value type
   * @param <D> fourth value type
   * @param <E> fifth value type
   * @return a tuple holding the provided values
   */
  public static <A, B, C, D, E>
      @NonNull Tuple5<@Nullable A, @Nullable B, @Nullable C, @Nullable D, @Nullable E> of(
          @Nullable A first,
          @Nullable B second,
          @Nullable C third,
          @Nullable D fourth,
          @Nullable E fifth) {
    return new Tuple5<>(first, second, third, fourth, fifth);
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
   * Returns the fifth element.
   *
   * @return the fifth element
   */
  public @Nullable E _5() {
    return fifth;
  }

  /**
   * Map the first element.
   *
   * @param mapper mapper for the first element
   * @param <F> mapped first element type
   * @return a tuple with the mapped first element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <F> @NonNull Tuple5<@Nullable F, @Nullable B, @Nullable C, @Nullable D, @Nullable E>
      mapFirst(@NonNull Function<? super @Nullable A, ? extends @Nullable F> mapper) {
    return new Tuple5<>(mapper.apply(first), second, third, fourth, fifth);
  }

  /**
   * Map the second element.
   *
   * @param mapper mapper for the second element
   * @param <F> mapped second element type
   * @return a tuple with the mapped second element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <F> @NonNull Tuple5<@Nullable A, @Nullable F, @Nullable C, @Nullable D, @Nullable E>
      mapSecond(@NonNull Function<? super @Nullable B, ? extends @Nullable F> mapper) {
    return new Tuple5<>(first, mapper.apply(second), third, fourth, fifth);
  }

  /**
   * Map the third element.
   *
   * @param mapper mapper for the third element
   * @param <F> mapped third element type
   * @return a tuple with the mapped third element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <F> @NonNull Tuple5<@Nullable A, @Nullable B, @Nullable F, @Nullable D, @Nullable E>
      mapThird(@NonNull Function<? super @Nullable C, ? extends @Nullable F> mapper) {
    return new Tuple5<>(first, second, mapper.apply(third), fourth, fifth);
  }

  /**
   * Map the fourth element.
   *
   * @param mapper mapper for the fourth element
   * @param <F> mapped fourth element type
   * @return a tuple with the mapped fourth element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <F> @NonNull Tuple5<@Nullable A, @Nullable B, @Nullable C, @Nullable F, @Nullable E>
      mapFourth(@NonNull Function<? super @Nullable D, ? extends @Nullable F> mapper) {
    return new Tuple5<>(first, second, third, mapper.apply(fourth), fifth);
  }

  /**
   * Map the fifth element.
   *
   * @param mapper mapper for the fifth element
   * @param <F> mapped fifth element type
   * @return a tuple with the mapped fifth element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <F> @NonNull Tuple5<@Nullable A, @Nullable B, @Nullable C, @Nullable D, @Nullable F>
      mapFifth(@NonNull Function<? super @Nullable E, ? extends @Nullable F> mapper) {
    return new Tuple5<>(first, second, third, fourth, mapper.apply(fifth));
  }

  /**
   * Map the first two elements.
   *
   * @param firstMapper mapper for the first element
   * @param secondMapper mapper for the second element
   * @param <F> mapped first element type
   * @param <G> mapped second element type
   * @return a tuple with the first two elements mapped
   * @throws NullPointerException if {@code firstMapper} or {@code secondMapper} is {@code null}
   */
  public <F, G>
      @NonNull Tuple5<@Nullable F, @Nullable G, @Nullable C, @Nullable D, @Nullable E> bimap(
          @NonNull Function<? super @Nullable A, ? extends @Nullable F> firstMapper,
          @NonNull Function<? super @Nullable B, ? extends @Nullable G> secondMapper) {
    return new Tuple5<>(firstMapper.apply(first), secondMapper.apply(second), third, fourth, fifth);
  }

  /**
   * Map the first three elements.
   *
   * @param firstMapper mapper for the first element
   * @param secondMapper mapper for the second element
   * @param thirdMapper mapper for the third element
   * @param <F> mapped first element type
   * @param <G> mapped second element type
   * @param <H> mapped third element type
   * @return a tuple with the first three elements mapped
   * @throws NullPointerException if any mapper is {@code null}
   */
  public <F, G, H>
      @NonNull Tuple5<@Nullable F, @Nullable G, @Nullable H, @Nullable D, @Nullable E> trimap(
          @NonNull Function<? super @Nullable A, ? extends @Nullable F> firstMapper,
          @NonNull Function<? super @Nullable B, ? extends @Nullable G> secondMapper,
          @NonNull Function<? super @Nullable C, ? extends @Nullable H> thirdMapper) {
    return new Tuple5<>(
        firstMapper.apply(first),
        secondMapper.apply(second),
        thirdMapper.apply(third),
        fourth,
        fifth);
  }

  /**
   * Map all elements.
   *
   * @param firstMapper mapper for the first element
   * @param secondMapper mapper for the second element
   * @param thirdMapper mapper for the third element
   * @param fourthMapper mapper for the fourth element
   * @param fifthMapper mapper for the fifth element
   * @param <F> mapped first element type
   * @param <G> mapped second element type
   * @param <H> mapped third element type
   * @param <I> mapped fourth element type
   * @param <J> mapped fifth element type
   * @return a tuple with all elements mapped
   * @throws NullPointerException if any mapper is {@code null}
   */
  public <F, G, H, I, J>
      @NonNull Tuple5<@Nullable F, @Nullable G, @Nullable H, @Nullable I, @Nullable J> quintmap(
          @NonNull Function<? super @Nullable A, ? extends @Nullable F> firstMapper,
          @NonNull Function<? super @Nullable B, ? extends @Nullable G> secondMapper,
          @NonNull Function<? super @Nullable C, ? extends @Nullable H> thirdMapper,
          @NonNull Function<? super @Nullable D, ? extends @Nullable I> fourthMapper,
          @NonNull Function<? super @Nullable E, ? extends @Nullable J> fifthMapper) {
    return new Tuple5<>(
        firstMapper.apply(first),
        secondMapper.apply(second),
        thirdMapper.apply(third),
        fourthMapper.apply(fourth),
        fifthMapper.apply(fifth));
  }
}
