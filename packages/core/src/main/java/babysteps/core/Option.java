package babysteps.core;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Optional value container inspired by functional programming (Maybe/Option).
 *
 * <p>Technical background: Option models the presence or absence of a value. {@link Some} may hold
 * {@code null} values, while {@link #ofNullable(Object)} maps {@code null} to {@link None}. The API
 * favors total functions and encourages handling of empty cases at the boundary.
 *
 * <p>The design is intentionally small and predictable:
 *
 * <ul>
 *   <li>Presence is represented by {@link Some}; absence by {@link None}.
 *   <li>Mapping preserves emptiness; flatMap enables chaining of Option-returning functions.
 *   <li>Use {@link #fold(Supplier, Function)} or {@link #getOrElse(Object)} to exit the monadic
 *       flow.
 * </ul>
 *
 * <p>Sample usage:
 *
 * <pre>{@code
 * Option<String> name = Option.ofNullable(user.name());
 *
 * String greeting = name
 *     .map(String::trim)
 *     .filter(n -> !n.isEmpty())
 *     .map(n -> "Hello, " + n + "!")
 *     .getOrElse("Hello, guest!");
 * }</pre>
 *
 * <p>Composing with dependent lookups:
 *
 * <pre>{@code
 * Option<User> user = findUserById(id);
 * Option<Email> email = user
 *     .flatMap(User::primaryContact)
 *     .flatMap(Contact::email);
 *
 * email.peek(e -> audit("email found: " + e.value()));
 * }</pre>
 *
 * @param <T> value type
 */
public sealed interface Option<T> permits Option.Some, Option.None {
  /**
   * Create a {@link Some} containing a value.
   *
   * @param value value to wrap, possibly {@code null}
   * @param <T> value type
   * @return Option with a present value
   * @throws NullPointerException when {@code value} is null
   */
  static <T> @NonNull Option<T> some(@Nullable T value) {
    return new Some<>(value);
  }

  /**
   * Create a {@link None}.
   *
   * @param <T> value type
   * @return empty Option
   */
  static <T> @NonNull Option<T> none() {
    return None.instance();
  }

  /**
   * Create {@link Some} when non-null, otherwise {@link None}.
   *
   * @param value possibly-null value
   * @param <T> value type
   * @return Option of value
   */
  static <T> @NonNull Option<T> ofNullable(@Nullable T value) {
    return value == null ? none() : some(value);
  }

  /**
   * Convert {@link Optional} to {@link Option}.
   *
   * @param optional Optional to convert
   * @param <T> value type
   * @return Option of optional
   */
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  static <T> @NonNull Option<T> fromOptional(@NonNull Optional<T> optional) {
    final var nonNullOptional = Objects.requireNonNull(optional, "optional");
    return nonNullOptional.map(Option::some).orElseGet(Option::none);
  }

  /**
   * @return true when value is present
   */
  boolean isPresent();

  /**
   * @return true when value is absent
   */
  default boolean isEmpty() {
    return !isPresent();
  }

  /**
   * Get the value or throw if empty.
   *
   * @return present value, possibly {@code null}
   * @throws NoSuchElementException when empty
   */
  @Nullable T get();

  /**
   * Get the value or a fallback when empty.
   *
   * @param fallback fallback value
   * @return present value or fallback
   */
  default @Nullable T getOrElse(@Nullable T fallback) {
    return isPresent() ? get() : fallback;
  }

  /**
   * Get the value or supply a fallback when empty.
   *
   * @param supplier supplier for fallback value
   * @return present value or supplied fallback
   */
  default @Nullable T getOrElseGet(@NonNull Supplier<? extends @Nullable T> supplier) {
    Objects.requireNonNull(supplier, "supplier");
    return isPresent() ? get() : supplier.get();
  }

  /**
   * Return this Option when present, otherwise return the provided fallback.
   *
   * @param fallback alternative Option
   * @return this or fallback
   */
  default @NonNull Option<T> orElse(@NonNull Option<? extends T> fallback) {
    Objects.requireNonNull(fallback, "fallback");
    if (isPresent()) {
      return this;
    }
    @SuppressWarnings("unchecked")
    final var casted = (Option<T>) fallback;
    return casted;
  }

  /**
   * Return this Option when present, otherwise use a supplier for the fallback.
   *
   * @param supplier supplier for alternative Option
   * @return this or supplied fallback
   */
  default @NonNull Option<T> orElseGet(@NonNull Supplier<? extends Option<? extends T>> supplier) {
    Objects.requireNonNull(supplier, "supplier");
    if (isPresent()) {
      return this;
    }
    @SuppressWarnings("unchecked")
    final var supplied = (Option<T>) supplier.get();
    return Objects.requireNonNull(supplied, "supplied");
  }

  /**
   * Return this Option when present, otherwise use a supplier for the fallback.
   *
   * @param supplier supplier for alternative Option
   * @return this or supplied fallback
   * @throws NullPointerException if {@code supplier} or its result is {@code null}
   */
  default @NonNull Option<T> or(@NonNull Supplier<? extends Option<? extends T>> supplier) {
    return orElseGet(supplier);
  }

  /**
   * Return the value when present or throw a supplied exception when empty.
   *
   * @param exceptionSupplier supplier for exception
   * @param <X> exception type
   * @return present value
   * @throws X when empty
   */
  default <X extends Throwable> @Nullable T orElseThrow(
      @NonNull Supplier<? extends X> exceptionSupplier) throws X {
    Objects.requireNonNull(exceptionSupplier, "exceptionSupplier");
    if (isPresent()) {
      return get();
    }
    throw exceptionSupplier.get();
  }

  /**
   * Return the value when present or throw {@link NoSuchElementException} when empty.
   *
   * @return present value
   * @throws NoSuchElementException when empty
   */
  default @Nullable T orElseThrow() {
    return get();
  }

  /**
   * Fold the Option into a non-optional value by providing handlers for both cases.
   *
   * @param ifEmpty handler for empty case
   * @param ifPresent handler for present case
   * @param <U> result type
   * @return result of the chosen handler
   */
  default <U> @Nullable U fold(
      @NonNull Supplier<? extends U> ifEmpty,
      @NonNull Function<? super @Nullable T, ? extends U> ifPresent) {
    Objects.requireNonNull(ifEmpty, "ifEmpty");
    Objects.requireNonNull(ifPresent, "ifPresent");
    return isPresent() ? ifPresent.apply(get()) : ifEmpty.get();
  }

  /**
   * Keep the value only when predicate matches; otherwise empty.
   *
   * @param predicate predicate to test
   * @return filtered Option
   */
  default @NonNull Option<T> filter(@NonNull Predicate<? super @Nullable T> predicate) {
    Objects.requireNonNull(predicate, "predicate");
    if (isEmpty()) {
      return this;
    }
    return predicate.test(get()) ? this : none();
  }

  /**
   * Keep the value only when predicate does NOT match; otherwise empty.
   *
   * @param predicate predicate to test
   * @return filtered Option
   */
  default @NonNull Option<T> filterNot(@NonNull Predicate<? super @Nullable T> predicate) {
    Objects.requireNonNull(predicate, "predicate");
    return filter(predicate.negate());
  }

  /**
   * Transform the value when present.
   *
   * @param mapper mapping function
   * @param <U> result type
   * @return mapped Option
   */
  default <U> @NonNull Option<U> map(@NonNull Function<? super @Nullable T, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isEmpty()) {
      return none();
    }
    return Option.ofNullable(mapper.apply(get()));
  }

  /**
   * Map the value or return a fallback when empty.
   *
   * @param mapper mapping function
   * @param fallback fallback value
   * @param <U> mapped value type
   * @return mapped value or fallback
   * @throws NullPointerException if {@code mapper} is {@code null}
   */
  default <U> @Nullable U mapOr(
      @Nullable U fallback, @NonNull Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    return isPresent() ? mapper.apply(get()) : fallback;
  }

  /**
   * Map the value or return a supplied fallback when empty.
   *
   * @param mapper mapping function
   * @param fallback supplier for fallback value
   * @param <U> mapped value type
   * @return mapped value or supplied fallback
   * @throws NullPointerException if {@code mapper} or {@code fallback} is {@code null}
   */
  default <U> @Nullable U mapOrElse(
      @NonNull Supplier<? extends U> fallback, @NonNull Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(fallback, "fallback");
    Objects.requireNonNull(mapper, "mapper");
    return isPresent() ? mapper.apply(get()) : fallback.get();
  }

  /**
   * Transform the value with a function that already returns an Option.
   *
   * @param mapper mapping function
   * @param <U> result type
   * @return mapped Option
   */
  default <U> @NonNull Option<U> flatMap(
      @NonNull Function<? super @Nullable T, ? extends Option<? extends U>> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isEmpty()) {
      return none();
    }
    @SuppressWarnings("unchecked")
    final var mapped = (Option<U>) mapper.apply(get());
    return Objects.requireNonNull(mapped, "mapped");
  }

  /**
   * Perform a side-effect when present without changing the Option.
   *
   * @param action action to perform
   * @return this Option
   */
  default @NonNull Option<T> peek(@NonNull Consumer<? super @Nullable T> action) {
    Objects.requireNonNull(action, "action");
    if (isPresent()) {
      action.accept(get());
    }
    return this;
  }

  /**
   * Perform an action if present.
   *
   * @param action action to perform
   */
  default void ifPresent(@NonNull Consumer<? super @Nullable T> action) {
    Objects.requireNonNull(action, "action");
    if (isPresent()) {
      action.accept(get());
    }
  }

  /**
   * Perform an action if present, otherwise run the empty action.
   *
   * @param action action to perform when present
   * @param emptyAction action to perform when empty
   */
  default void ifPresentOrElse(
      @NonNull Consumer<? super @Nullable T> action, @NonNull Runnable emptyAction) {
    Objects.requireNonNull(action, "action");
    Objects.requireNonNull(emptyAction, "emptyAction");
    if (isPresent()) {
      action.accept(get());
    } else {
      emptyAction.run();
    }
  }

  /**
   * Test a predicate against the value when present.
   *
   * @param predicate predicate to test
   * @return true when present and predicate matches
   */
  default boolean exists(@NonNull Predicate<? super @Nullable T> predicate) {
    Objects.requireNonNull(predicate, "predicate");
    return isPresent() && predicate.test(get());
  }

  /**
   * Test a predicate against the value when present, or true when empty.
   *
   * @param predicate predicate to test
   * @return true when empty or predicate matches
   */
  default boolean forAll(@NonNull Predicate<? super @Nullable T> predicate) {
    Objects.requireNonNull(predicate, "predicate");
    return isEmpty() || predicate.test(get());
  }

  /**
   * @param value value to compare
   * @return true when present and equal to the provided value
   */
  default boolean contains(@Nullable T value) {
    return isPresent() && Objects.equals(get(), value);
  }

  /**
   * Convert to {@link Result} using a supplier for the error when empty.
   *
   * @param ifEmpty error supplier
   * @param <E> error type
   * @return Result wrapping the value or error
   */
  default <E> @NonNull Result<T, E> toResult(@NonNull Supplier<? extends E> ifEmpty) {
    Objects.requireNonNull(ifEmpty, "ifEmpty");
    return isPresent() ? Result.ok(get()) : Result.err(ifEmpty.get());
  }

  /**
   * Convert to a {@link Stream} with 0 or 1 element.
   *
   * @return Stream of the value when present
   */
  default @NonNull Stream<T> stream() {
    return isPresent() ? Stream.of(get()) : Stream.empty();
  }

  /**
   * Convert to {@link Optional} for interop with standard APIs.
   *
   * <p>{@link Some} holding {@code null} is converted to {@link Optional#empty()}.
   *
   * @return Optional view
   */
  default @NonNull Optional<T> toOptional() {
    return isPresent() ? Optional.ofNullable(get()) : Optional.empty();
  }

  /**
   * Convert {@code Some(null)} to {@link None}; keeps other values unchanged.
   *
   * @return {@code None} when this is {@code Some(null)}, otherwise this Option
   */
  default @NonNull Option<T> normalize() {
    if (isPresent() && get() == null) {
      return none();
    }
    return this;
  }

  /**
   * Present case.
   *
   * @param <T> value type, possibly nullable
   */
  record Some<T>(@Nullable T value) implements Option<T> {

    @Override
    public boolean isPresent() {
      return true;
    }

    @Override
    public @Nullable T get() {
      return value;
    }

    @Override
    public @NonNull String toString() {
      return "Some(" + value + ")";
    }
  }

  /**
   * Empty case.
   *
   * @param <T> value type
   */
  final class None<T> implements Option<T> {
    private static final None<?> INSTANCE = new None<>();

    private None() {}

    @SuppressWarnings("unchecked")
    static <T> None<T> instance() {
      return (None<T>) INSTANCE;
    }

    @Override
    public boolean isPresent() {
      return false;
    }

    @Override
    public @NonNull T get() {
      throw new NoSuchElementException("Option is empty");
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof None<?>;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    @Override
    public @NonNull String toString() {
      return "None";
    }
  }
}
