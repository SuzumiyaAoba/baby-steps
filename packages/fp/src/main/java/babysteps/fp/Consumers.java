package babysteps.fp;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Utility consumers for composition and tap-style usage. */
public final class Consumers {
  private Consumers() {}

  /**
   * Combine two consumers into one, invoking them in order.
   *
   * @param first first consumer
   * @param second second consumer
   * @param <T> input type
   * @return combined consumer
   * @throws NullPointerException if {@code first} or {@code second} is {@code null}
   */
  public static <T> @NonNull Consumer<@Nullable T> tee(
      @NonNull Consumer<? super @Nullable T> first, @NonNull Consumer<? super @Nullable T> second) {
    Objects.requireNonNull(first, "first");
    Objects.requireNonNull(second, "second");
    return value -> {
      first.accept(value);
      second.accept(value);
    };
  }

  /**
   * Compose two consumers into one, invoking them in order.
   *
   * @param first first consumer
   * @param second second consumer
   * @param <T> input type
   * @return composed consumer
   * @throws NullPointerException if {@code first} or {@code second} is {@code null}
   */
  public static <T> @NonNull Consumer<@Nullable T> compose(
      @NonNull Consumer<? super @Nullable T> first, @NonNull Consumer<? super @Nullable T> second) {
    return tee(first, second);
  }

  /**
   * Combine multiple consumers into one, invoking them in order.
   *
   * @param first first consumer
   * @param second second consumer
   * @param rest remaining consumers
   * @param <T> input type
   * @return combined consumer
   * @throws NullPointerException if any consumer is {@code null}
   */
  @SafeVarargs
  public static <T> @NonNull Consumer<@Nullable T> teeAll(
      @NonNull Consumer<? super @Nullable T> first,
      @NonNull Consumer<? super @Nullable T> second,
      @NonNull Consumer<? super @Nullable T>... rest) {
    Objects.requireNonNull(first, "first");
    Objects.requireNonNull(second, "second");
    Objects.requireNonNull(rest, "rest");
    return value -> {
      first.accept(value);
      second.accept(value);
      for (final var consumer : rest) {
        Objects.requireNonNull(consumer, "consumer");
        consumer.accept(value);
      }
    };
  }

  /**
   * Returns a no-op consumer.
   *
   * @param <T> input type
   * @return consumer that does nothing
   */
  public static <T> @NonNull Consumer<@Nullable T> noop() {
    return value -> {};
  }

  /**
   * Perform a side effect and return the original value.
   *
   * @param action side-effecting consumer
   * @param <T> input type
   * @return function that returns the input after invoking {@code action}
   * @throws NullPointerException if {@code action} is {@code null}
   */
  public static <T> @NonNull Function<@Nullable T, @Nullable T> tap(
      @NonNull Consumer<? super @Nullable T> action) {
    Objects.requireNonNull(action, "action");
    return value -> {
      action.accept(value);
      return value;
    };
  }
}
