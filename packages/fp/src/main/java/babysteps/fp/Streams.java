package babysteps.fp;

import babysteps.core.Option;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Utility helpers for working with {@link Stream} terminals. */
public final class Streams {
  private Streams() {}

  /**
   * Returns the first element of the stream when present.
   *
   * @param stream source stream
   * @param <T> element type
   * @return {@link Option#some(Object)} when the stream has an element, otherwise {@link
   *     Option#none()}
   * @throws NullPointerException if {@code stream} is {@code null}
   */
  public static <T> @NonNull Option<T> first(@NonNull Stream<? extends @Nullable T> stream) {
    Objects.requireNonNull(stream, "stream");
    final var iterator = stream.iterator();
    if (!iterator.hasNext()) {
      return Option.none();
    }
    return Option.some(iterator.next());
  }

  /**
   * Returns the last element of the stream when present.
   *
   * @param stream source stream
   * @param <T> element type
   * @return {@link Option#some(Object)} when the stream has an element, otherwise {@link
   *     Option#none()}
   * @throws NullPointerException if {@code stream} is {@code null}
   */
  public static <T> @NonNull Option<T> last(@NonNull Stream<? extends @Nullable T> stream) {
    Objects.requireNonNull(stream, "stream");
    final var iterator = stream.iterator();
    if (!iterator.hasNext()) {
      return Option.none();
    }
    @Nullable T last = iterator.next();
    while (iterator.hasNext()) {
      last = iterator.next();
    }
    return Option.some(last);
  }

  /**
   * Returns the only element of the stream when exactly one element is present.
   *
   * @param stream source stream
   * @param <T> element type
   * @return {@link Option#some(Object)} when the stream has exactly one element, otherwise {@link
   *     Option#none()}
   * @throws NullPointerException if {@code stream} is {@code null}
   */
  public static <T> @NonNull Option<T> single(@NonNull Stream<? extends @Nullable T> stream) {
    Objects.requireNonNull(stream, "stream");
    final var iterator = stream.iterator();
    if (!iterator.hasNext()) {
      return Option.none();
    }
    final var value = iterator.next();
    if (iterator.hasNext()) {
      return Option.none();
    }
    return Option.some(value);
  }
}
