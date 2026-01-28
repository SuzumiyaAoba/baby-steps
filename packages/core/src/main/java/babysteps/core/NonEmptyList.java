package babysteps.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Immutable list that always contains at least one element.
 *
 * <p>Unlike {@link java.util.List}, this type guarantees non-emptiness at the type level. The
 * contained values may be {@code null}, but the list itself is never empty.
 *
 * <p>Construction APIs return an {@link Option} to avoid throwing for empty inputs. All list views
 * returned by this class are unmodifiable.
 *
 * @param <T> element type, possibly nullable
 */
public final class NonEmptyList<T> implements Iterable<@Nullable T> {
  private final @NonNull List<@Nullable T> values;

  private NonEmptyList(@NonNull List<@Nullable T> values, boolean alreadyOwned) {
    Objects.requireNonNull(values, "values");
    if (values.isEmpty()) {
      throw new IllegalArgumentException("NonEmptyList requires at least one element");
    }
    this.values = alreadyOwned ? Collections.unmodifiableList(values) : unmodifiableCopy(values);
  }

  /**
   * Creates a {@link NonEmptyList} from a head value and optional tail values.
   *
   * @param first first element, possibly {@code null}
   * @param rest remaining elements, possibly {@code null}
   * @param <T> element type
   * @return non-empty list containing the provided values
   * @throws NullPointerException if {@code rest} is {@code null}
   */
  @SafeVarargs
  public static <T> @NonNull NonEmptyList<T> of(@Nullable T first, @NonNull T... rest) {
    Objects.requireNonNull(rest, "rest");
    final var list = new ArrayList<@Nullable T>(rest.length + 1);
    list.add(first);
    Collections.addAll(list, rest);
    return new NonEmptyList<>(list, true);
  }

  /**
   * Creates a {@link NonEmptyList} from a list when non-empty.
   *
   * @param values source list
   * @param <T> element type
   * @return {@link Option#some(Object)} for non-empty lists, otherwise {@link Option#none()}
   * @throws NullPointerException if {@code values} is {@code null}
   */
  public static <T> @NonNull Option<NonEmptyList<T>> fromList(
      @NonNull List<? extends @Nullable T> values) {
    Objects.requireNonNull(values, "values");
    if (values.isEmpty()) {
      return Option.none();
    }
    final var copy = new ArrayList<@Nullable T>(values);
    return Option.some(new NonEmptyList<>(copy, true));
  }

  /**
   * Creates a {@link NonEmptyList} from an iterable when it has at least one element.
   *
   * @param values source iterable
   * @param <T> element type
   * @return {@link Option#some(Object)} for non-empty iterables, otherwise {@link Option#none()}
   * @throws NullPointerException if {@code values} is {@code null}
   */
  public static <T> @NonNull Option<NonEmptyList<T>> fromIterable(
      @NonNull Iterable<? extends @Nullable T> values) {
    Objects.requireNonNull(values, "values");
    final var iterator = values.iterator();
    if (!iterator.hasNext()) {
      return Option.none();
    }
    final var list = new ArrayList<@Nullable T>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    return Option.some(new NonEmptyList<>(list, true));
  }

  /**
   * Returns the first element.
   *
   * @return the first element, possibly {@code null}
   */
  public @Nullable T head() {
    return values.get(0);
  }

  /**
   * Returns all elements except the first.
   *
   * @return unmodifiable list of tail elements
   */
  public @NonNull List<@Nullable T> tail() {
    if (values.size() == 1) {
      return Collections.emptyList();
    }
    return values.subList(1, values.size());
  }

  /**
   * Returns the number of elements.
   *
   * @return size of the list
   */
  public int size() {
    return values.size();
  }

  /**
   * Returns an unmodifiable view of this list.
   *
   * @return unmodifiable list of elements
   */
  public @NonNull List<@Nullable T> toList() {
    return values;
  }

  /**
   * Appends a value to the end of this list.
   *
   * @param value value to append, possibly {@code null}
   * @return new non-empty list with the appended value
   */
  public @NonNull NonEmptyList<T> append(@Nullable T value) {
    final var list = new ArrayList<@Nullable T>(values.size() + 1);
    list.addAll(values);
    list.add(value);
    return new NonEmptyList<>(list, true);
  }

  /**
   * Prepends a value to the start of this list.
   *
   * @param value value to prepend, possibly {@code null}
   * @return new non-empty list with the prepended value
   */
  public @NonNull NonEmptyList<T> prepend(@Nullable T value) {
    final var list = new ArrayList<@Nullable T>(values.size() + 1);
    list.add(value);
    list.addAll(values);
    return new NonEmptyList<>(list, true);
  }

  /**
   * Concatenates another {@link NonEmptyList} to this list.
   *
   * @param other other list to append
   * @return new non-empty list with all elements
   * @throws NullPointerException if {@code other} is {@code null}
   */
  public @NonNull NonEmptyList<T> concat(@NonNull NonEmptyList<? extends T> other) {
    Objects.requireNonNull(other, "other");
    final var list = new ArrayList<@Nullable T>(values.size() + other.size());
    list.addAll(values);
    for (final var value : other) {
      list.add(value);
    }
    return new NonEmptyList<>(list, true);
  }

  /**
   * Maps each element to another value.
   *
   * @param mapper mapper to apply
   * @param <U> mapped element type
   * @return non-empty list of mapped values
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <U> @NonNull NonEmptyList<U> map(
      @NonNull Function<? super @Nullable T, ? extends @Nullable U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    final var list = new ArrayList<@Nullable U>(values.size());
    for (final var value : values) {
      list.add(mapper.apply(value));
    }
    return new NonEmptyList<>(list, true);
  }

  /**
   * Maps each element to another non-empty list and flattens the result.
   *
   * @param mapper mapper to apply
   * @param <U> mapped element type
   * @return flattened non-empty list
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  public <U> @NonNull NonEmptyList<U> flatMap(
      @NonNull Function<? super @Nullable T, ? extends NonEmptyList<? extends @Nullable U>>
          mapper) {
    Objects.requireNonNull(mapper, "mapper");
    final var list = new ArrayList<@Nullable U>();
    for (final var value : values) {
      final var mapped = Objects.requireNonNull(mapper.apply(value), "mapped");
      for (final var inner : mapped) {
        list.add(inner);
      }
    }
    return new NonEmptyList<>(list, true);
  }

  /**
   * Folds the list into a single value by applying {@code folder} left-to-right.
   *
   * @param initial initial accumulator value, possibly {@code null}
   * @param folder folding function
   * @param <U> accumulator type
   * @return folded result
   * @throws NullPointerException if {@code folder} is {@code null}
   */
  public <U> @Nullable U fold(
      @Nullable U initial,
      @NonNull BiFunction<? super @Nullable U, ? super @Nullable T, ? extends U> folder) {
    Objects.requireNonNull(folder, "folder");
    final var accumulator = new java.util.concurrent.atomic.AtomicReference<@Nullable U>(initial);
    for (final var value : values) {
      accumulator.set(folder.apply(accumulator.get(), value));
    }
    return accumulator.get();
  }

  @Override
  public @NonNull Iterator<@Nullable T> iterator() {
    return values.iterator();
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof NonEmptyList<?> that)) {
      return false;
    }
    return values.equals(that.values);
  }

  @Override
  public int hashCode() {
    return values.hashCode();
  }

  @Override
  public String toString() {
    return "NonEmptyList" + values;
  }

  private static <T> @NonNull List<@Nullable T> unmodifiableCopy(
      @NonNull Collection<? extends @Nullable T> values) {
    return Collections.unmodifiableList(new ArrayList<>(values));
  }
}
