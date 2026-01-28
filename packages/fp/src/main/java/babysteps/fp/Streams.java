package babysteps.fp;

import babysteps.core.Option;
import babysteps.core.Try;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Utility helpers for working with {@link Stream}. */
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

  /**
   * Splits a stream into consecutive chunks of the given size.
   *
   * <p>The last chunk may be smaller when the stream does not divide evenly.
   *
   * @param stream source stream
   * @param size chunk size
   * @param <T> element type
   * @return stream of chunks
   * @throws IllegalArgumentException if {@code size} is not positive
   * @throws NullPointerException if {@code stream} is {@code null}
   */
  public static <T> @NonNull Stream<List<T>> chunked(
      @NonNull Stream<? extends @Nullable T> stream, int size) {
    Objects.requireNonNull(stream, "stream");
    if (size <= 0) {
      throw new IllegalArgumentException("size must be positive");
    }
    final var iterator = stream.iterator();
    final var chunkIterator =
        new Iterator<List<T>>() {
          @Override
          public boolean hasNext() {
            return iterator.hasNext();
          }

          @Override
          public List<T> next() {
            final var values = new ArrayList<T>(size);
            while (iterator.hasNext() && values.size() < size) {
              values.add(iterator.next());
            }
            return Collections.unmodifiableList(values);
          }
        };
    return streamFromIterator(chunkIterator);
  }

  /**
   * Creates sliding windows of the given size with step {@code 1} and no partial windows.
   *
   * @param stream source stream
   * @param size window size
   * @param <T> element type
   * @return stream of windows
   * @throws IllegalArgumentException if {@code size} is not positive
   * @throws NullPointerException if {@code stream} is {@code null}
   */
  public static <T> @NonNull Stream<List<T>> windowed(
      @NonNull Stream<? extends @Nullable T> stream, int size) {
    return windowed(stream, size, 1, false);
  }

  /**
   * Creates sliding windows of the given size and step, without partial windows.
   *
   * @param stream source stream
   * @param size window size
   * @param step steps between windows
   * @param <T> element type
   * @return stream of windows
   * @throws IllegalArgumentException if {@code size} or {@code step} is not positive
   * @throws NullPointerException if {@code stream} is {@code null}
   */
  public static <T> @NonNull Stream<List<T>> windowed(
      @NonNull Stream<? extends @Nullable T> stream, int size, int step) {
    return windowed(stream, size, step, false);
  }

  /**
   * Creates sliding windows of the given size and step.
   *
   * @param stream source stream
   * @param size window size
   * @param step steps between windows
   * @param partialWindows whether to include trailing partial windows
   * @param <T> element type
   * @return stream of windows
   * @throws IllegalArgumentException if {@code size} or {@code step} is not positive
   * @throws NullPointerException if {@code stream} is {@code null}
   */
  public static <T> @NonNull Stream<List<T>> windowed(
      @NonNull Stream<? extends @Nullable T> stream, int size, int step, boolean partialWindows) {
    Objects.requireNonNull(stream, "stream");
    if (size <= 0) {
      throw new IllegalArgumentException("size must be positive");
    }
    if (step <= 0) {
      throw new IllegalArgumentException("step must be positive");
    }
    final var iterator = stream.iterator();
    final var buffer = new ArrayDeque<T>(size);
    final var windowIterator =
        new Iterator<List<T>>() {
          private boolean initialized;

          private void fillToSize() {
            while (buffer.size() < size && iterator.hasNext()) {
              buffer.addLast(iterator.next());
            }
          }

          @Override
          public boolean hasNext() {
            if (!initialized) {
              fillToSize();
              initialized = true;
            }
            if (buffer.isEmpty()) {
              return false;
            }
            if (buffer.size() == size) {
              return true;
            }
            return partialWindows;
          }

          @Override
          public List<T> next() {
            final var window = Collections.unmodifiableList(new ArrayList<>(buffer));
            var toDrop = step;
            while (toDrop > 0 && !buffer.isEmpty()) {
              buffer.removeFirst();
              toDrop--;
            }
            fillToSize();
            return window;
          }
        };
    return streamFromIterator(windowIterator);
  }

  /**
   * Enumerates values with a zero-based index.
   *
   * @param stream source stream
   * @param <T> element type
   * @return stream of index/value pairs
   * @throws NullPointerException if {@code stream} is {@code null}
   */
  public static <T> @NonNull Stream<Tuple2<Integer, T>> indexed(
      @NonNull Stream<? extends @Nullable T> stream) {
    Objects.requireNonNull(stream, "stream");
    final var iterator = stream.iterator();
    final var indexedIterator =
        new Iterator<Tuple2<Integer, T>>() {
          private int index;

          @Override
          public boolean hasNext() {
            return iterator.hasNext();
          }

          @Override
          public Tuple2<Integer, T> next() {
            return Tuple2.of(index++, iterator.next());
          }
        };
    return streamFromIterator(indexedIterator);
  }

  /**
   * Takes values while the predicate holds, including the first failing element.
   *
   * @param stream source stream
   * @param predicate predicate to test
   * @param <T> element type
   * @return stream that includes the first failing element
   * @throws NullPointerException if {@code stream} or {@code predicate} is {@code null}
   */
  public static <T> @NonNull Stream<T> takeWhileInclusive(
      @NonNull Stream<? extends @Nullable T> stream,
      @NonNull Predicate<? super @Nullable T> predicate) {
    Objects.requireNonNull(stream, "stream");
    Objects.requireNonNull(predicate, "predicate");
    final var iterator = stream.iterator();
    final var spliterator =
        new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
          private boolean stopped;

          @Override
          public boolean tryAdvance(Consumer<? super T> action) {
            Objects.requireNonNull(action, "action");
            if (stopped) {
              return false;
            }
            if (!iterator.hasNext()) {
              stopped = true;
              return false;
            }
            final var value = iterator.next();
            action.accept(value);
            if (!predicate.test(value)) {
              stopped = true;
            }
            return true;
          }
        };
    return StreamSupport.stream(spliterator, false);
  }

  /**
   * Drops values while the predicate holds, also dropping the first failing element.
   *
   * @param stream source stream
   * @param predicate predicate to test
   * @param <T> element type
   * @return stream with the first failing element removed
   * @throws NullPointerException if {@code stream} or {@code predicate} is {@code null}
   */
  public static <T> @NonNull Stream<T> dropWhileInclusive(
      @NonNull Stream<@Nullable T> stream,
      @NonNull Predicate<? super @Nullable T> predicate) {
    Objects.requireNonNull(stream, "stream");
    Objects.requireNonNull(predicate, "predicate");
    return stream.dropWhile(predicate).skip(1);
  }

  /**
   * Filters distinct values by a key extractor.
   *
   * @param stream source stream
   * @param keyExtractor key extractor
   * @param <T> element type
   * @param <K> key type
   * @return stream with distinct keys
   * @throws NullPointerException if {@code stream} or {@code keyExtractor} is {@code null}
   */
  public static <T, K> @NonNull Stream<T> distinctBy(
      @NonNull Stream<@Nullable T> stream,
      @NonNull Function<? super @Nullable T, ? extends @Nullable K> keyExtractor) {
    Objects.requireNonNull(stream, "stream");
    Objects.requireNonNull(keyExtractor, "keyExtractor");
    final var seen = new HashSet<@Nullable K>();
    return stream.sequential().filter(value -> seen.add(keyExtractor.apply(value)));
  }

  /**
   * Maps values to {@link Try}, capturing exceptions from the mapper.
   *
   * @param stream source stream
   * @param mapper mapper function
   * @param <T> input type
   * @param <R> output type
   * @return stream of {@link Try}
   * @throws NullPointerException if {@code stream} or {@code mapper} is {@code null}
   */
  public static <T, R> @NonNull Stream<Try<R>> mapCatching(
      @NonNull Stream<? extends @Nullable T> stream,
      @NonNull Function<? super @Nullable T, ? extends R> mapper) {
    Objects.requireNonNull(stream, "stream");
    Objects.requireNonNull(mapper, "mapper");
    return stream.map(value -> Try.of(() -> mapper.apply(value)));
  }

  /**
   * Filters values with a predicate, capturing exceptions into {@link Try}.
   *
   * <p>When the predicate returns {@code true}, the value is emitted as {@link Try.Success}. When
   * the predicate returns {@code false}, the value is dropped. When the predicate throws, a {@link
   * Try.Failure} is emitted.
   *
   * @param stream source stream
   * @param predicate predicate to test
   * @param <T> element type
   * @return stream of {@link Try} for accepted values and failures
   * @throws NullPointerException if {@code stream} or {@code predicate} is {@code null}
   */
  public static <T> @NonNull Stream<Try<T>> filterCatching(
      @NonNull Stream<? extends @Nullable T> stream,
      @NonNull Predicate<? super @Nullable T> predicate) {
    Objects.requireNonNull(stream, "stream");
    Objects.requireNonNull(predicate, "predicate");
    return stream.flatMap(
        value -> {
          try {
            if (predicate.test(value)) {
              return Stream.of(Try.success(value));
            }
            return Stream.empty();
          } catch (Exception exception) {
            return Stream.of(Try.failure(exception));
          }
        });
  }

  private static <T> @NonNull Stream<T> streamFromIterator(@NonNull Iterator<T> iterator) {
    final var spliterator = Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED);
    return StreamSupport.stream(spliterator, false);
  }
}
