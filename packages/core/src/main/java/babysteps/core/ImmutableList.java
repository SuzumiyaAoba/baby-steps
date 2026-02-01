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
 * Immutable list wrapper that allows empty lists while preserving null elements.
 *
 * <p>All list views returned by this class are unmodifiable. Mutation methods create new immutable
 * instances.
 *
 * @param <T> element type, possibly nullable
 */
public final class ImmutableList<T> implements Iterable<@Nullable T> {
  private static final ImmutableList<?> EMPTY = new ImmutableList<>(Collections.emptyList(), true);

  private final @NonNull List<@Nullable T> values;

  private ImmutableList(@NonNull List<@Nullable T> values, boolean alreadyOwned) {
    Objects.requireNonNull(values, "values");
    this.values = alreadyOwned ? Collections.unmodifiableList(values) : unmodifiableCopy(values);
  }

  /**
   * Returns an empty immutable list.
   *
   * @param <T> element type
   * @return empty list
   */
  public static <T> @NonNull ImmutableList<T> empty() {
    @SuppressWarnings("unchecked")
    final var casted = (ImmutableList<T>) EMPTY;
    return casted;
  }

  /**
   * Creates an {@link ImmutableList} from values.
   *
   * @param values values to wrap
   * @param <T> element type
   * @return immutable list containing the provided values
   * @throws NullPointerException if {@code values} is {@code null}
   */
  @SafeVarargs
  public static <T> @NonNull ImmutableList<T> of(@Nullable T... values) {
    Objects.requireNonNull(values, "values");
    if (values.length == 0) {
      return empty();
    }
    final var list = new ArrayList<@Nullable T>(values.length);
    Collections.addAll(list, values);
    return new ImmutableList<>(list, true);
  }

  /**
   * Creates an {@link ImmutableList} from a list.
   *
   * @param values source list
   * @param <T> element type
   * @return immutable list of provided values
   * @throws NullPointerException if {@code values} is {@code null}
   */
  public static <T> @NonNull ImmutableList<T> fromList(
      @NonNull List<? extends @Nullable T> values) {
    Objects.requireNonNull(values, "values");
    if (values.isEmpty()) {
      return empty();
    }
    final var list = new ArrayList<@Nullable T>(values);
    return new ImmutableList<>(list, true);
  }

  /**
   * Creates an {@link ImmutableList} from an iterable.
   *
   * @param values source iterable
   * @param <T> element type
   * @return immutable list of provided values
   * @throws NullPointerException if {@code values} is {@code null}
   */
  public static <T> @NonNull ImmutableList<T> fromIterable(
      @NonNull Iterable<? extends @Nullable T> values) {
    Objects.requireNonNull(values, "values");
    final var iterator = values.iterator();
    if (!iterator.hasNext()) {
      return empty();
    }
    final var list = new ArrayList<@Nullable T>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    return new ImmutableList<>(list, true);
  }

  /**
   * Returns true if the list is empty.
   *
   * @return true when the list has no elements
   */
  public boolean isEmpty() {
    return values.isEmpty();
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
   * Returns the first element as an {@link Option}.
   *
   * @return {@link Option#some(Object)} when non-empty, otherwise {@link Option#none()}
   */
  public @NonNull Option<T> headOption() {
    if (values.isEmpty()) {
      return Option.none();
    }
    return Option.some(values.get(0));
  }

  /**
   * Returns all elements except the first.
   *
   * @return immutable list of tail elements
   */
  public @NonNull ImmutableList<T> tail() {
    if (values.size() <= 1) {
      return empty();
    }
    return new ImmutableList<>(new ArrayList<>(values.subList(1, values.size())), true);
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
   * Returns a {@link NonEmptyList} when this list has elements.
   *
   * @return {@link Option#some(Object)} for non-empty lists, otherwise {@link Option#none()}
   */
  public @NonNull Option<NonEmptyList<T>> toNonEmptyList() {
    return NonEmptyList.fromList(values);
  }

  /**
   * Appends a value to the end of this list.
   *
   * @param value value to append, possibly {@code null}
   * @return new immutable list with the appended value
   */
  public @NonNull ImmutableList<T> append(@Nullable T value) {
    final var list = new ArrayList<@Nullable T>(values.size() + 1);
    list.addAll(values);
    list.add(value);
    return new ImmutableList<>(list, true);
  }

  /**
   * Prepends a value to the start of this list.
   *
   * @param value value to prepend, possibly {@code null}
   * @return new immutable list with the prepended value
   */
  public @NonNull ImmutableList<T> prepend(@Nullable T value) {
    final var list = new ArrayList<@Nullable T>(values.size() + 1);
    list.add(value);
    list.addAll(values);
    return new ImmutableList<>(list, true);
  }

  /**
   * Concatenates another {@link ImmutableList} to this list.
   *
   * @param other other list to append
   * @return new immutable list with all elements
   * @throws NullPointerException if {@code other} is {@code null}
   */
  public @NonNull ImmutableList<T> concat(@NonNull ImmutableList<? extends T> other) {
    Objects.requireNonNull(other, "other");
    if (other.isEmpty()) {
      return this;
    }
    final var list = new ArrayList<@Nullable T>(values.size() + other.size());
    list.addAll(values);
    for (final var value : other) {
      list.add(value);
    }
    return new ImmutableList<>(list, true);
  }

  /**
   * Maps each element to another value.
   *
   * @param mapper mapper to apply
   * @param <U> mapped element type
   * @return immutable list of mapped values
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  public <U> @NonNull ImmutableList<U> map(
      @NonNull Function<? super @Nullable T, ? extends @Nullable U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (values.isEmpty()) {
      return empty();
    }
    final var list = new ArrayList<@Nullable U>(values.size());
    for (final var value : values) {
      list.add(mapper.apply(value));
    }
    return new ImmutableList<>(list, true);
  }

  /**
   * Maps each element to another immutable list and flattens the result.
   *
   * @param mapper mapper to apply
   * @param <U> mapped element type
   * @return flattened immutable list
   * @throws NullPointerException if {@code mapper} or its result is {@code null}
   */
  public <U> @NonNull ImmutableList<U> flatMap(
      @NonNull Function<? super @Nullable T, ? extends ImmutableList<? extends @Nullable U>>
          mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (values.isEmpty()) {
      return empty();
    }
    final var list = new ArrayList<@Nullable U>();
    for (final var value : values) {
      final var mapped = Objects.requireNonNull(mapper.apply(value), "mapped");
      for (final var inner : mapped) {
        list.add(inner);
      }
    }
    return new ImmutableList<>(list, true);
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
    if (!(other instanceof ImmutableList<?> that)) {
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
    return "ImmutableList" + values;
  }

  private static <T> @NonNull List<@Nullable T> unmodifiableCopy(
      @NonNull Collection<? extends @Nullable T> values) {
    return Collections.unmodifiableList(new ArrayList<>(values));
  }
}
