package babysteps.fp;

import java.util.function.Function;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Simple tuple of three values.
 *
 * @param first first value
 * @param second second value
 * @param third third value
 * @param <A> first value type
 * @param <B> second value type
 * @param <C> third value type
 */
public record Tuple3<A, B, C>(@Nullable A first, @Nullable B second, @Nullable C third) {
  /**
   * Creates a {@link Tuple3} from the given values.
   *
   * @param first first value
   * @param second second value
   * @param third third value
   * @param <A> first value type
   * @param <B> second value type
   * @param <C> third value type
   * @return a tuple holding the provided values
   */
  public static <A, B, C> @NonNull Tuple3<@Nullable A, @Nullable B, @Nullable C> of(
      @Nullable A first, @Nullable B second, @Nullable C third) {
    return new Tuple3<>(first, second, third);
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
   * Map the first element.
   *
   * @param mapper mapper for the first element
   * @param <D> mapped first element type
   * @return a tuple with the mapped first element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <D> @NonNull Tuple3<@Nullable D, @Nullable B, @Nullable C> mapFirst(
      @NonNull Function<? super @Nullable A, ? extends @Nullable D> mapper) {
    return new Tuple3<>(mapper.apply(first), second, third);
  }

  /**
   * Map the second element.
   *
   * @param mapper mapper for the second element
   * @param <D> mapped second element type
   * @return a tuple with the mapped second element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <D> @NonNull Tuple3<@Nullable A, @Nullable D, @Nullable C> mapSecond(
      @NonNull Function<? super @Nullable B, ? extends @Nullable D> mapper) {
    return new Tuple3<>(first, mapper.apply(second), third);
  }

  /**
   * Map the third element.
   *
   * @param mapper mapper for the third element
   * @param <D> mapped third element type
   * @return a tuple with the mapped third element
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <D> @NonNull Tuple3<@Nullable A, @Nullable B, @Nullable D> mapThird(
      @NonNull Function<? super @Nullable C, ? extends @Nullable D> mapper) {
    return new Tuple3<>(first, second, mapper.apply(third));
  }

  /**
   * Map the first two elements.
   *
   * @param firstMapper mapper for the first element
   * @param secondMapper mapper for the second element
   * @param <D> mapped first element type
   * @param <E> mapped second element type
   * @return a tuple with the first two elements mapped
   * @throws NullPointerException if {@code firstMapper} or {@code secondMapper} is {@code null}
   */
  public <D, E> @NonNull Tuple3<@Nullable D, @Nullable E, @Nullable C> bimap(
      @NonNull Function<? super @Nullable A, ? extends @Nullable D> firstMapper,
      @NonNull Function<? super @Nullable B, ? extends @Nullable E> secondMapper) {
    return new Tuple3<>(firstMapper.apply(first), secondMapper.apply(second), third);
  }

  /**
   * Map all elements.
   *
   * @param firstMapper mapper for the first element
   * @param secondMapper mapper for the second element
   * @param thirdMapper mapper for the third element
   * @param <D> mapped first element type
   * @param <E> mapped second element type
   * @param <F> mapped third element type
   * @return a tuple with all elements mapped
   * @throws NullPointerException if any mapper is {@code null}
   */
  public <D, E, F> @NonNull Tuple3<@Nullable D, @Nullable E, @Nullable F> trimap(
      @NonNull Function<? super @Nullable A, ? extends @Nullable D> firstMapper,
      @NonNull Function<? super @Nullable B, ? extends @Nullable E> secondMapper,
      @NonNull Function<? super @Nullable C, ? extends @Nullable F> thirdMapper) {
    return new Tuple3<>(
        firstMapper.apply(first), secondMapper.apply(second), thirdMapper.apply(third));
  }
}
