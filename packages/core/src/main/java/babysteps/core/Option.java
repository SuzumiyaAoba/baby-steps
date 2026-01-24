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
 * <p>Technical background: Option models the presence or absence of a value without using {@code
 * null}. This makes absence explicit in the type system and enables safe composition through {@link
 * #map(Function)} and {@link #flatMap(Function)}. The API favors total functions and encourages
 * handling of empty cases at the boundary.
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
   * Create a {@link Some} containing a non-null value.
   *
   * @param value value to wrap (must be non-null)
   * @param <T> value type
   * @return Option with a present value
   * @throws NullPointerException when {@code value} is null
   */
  static <T> Option<T> some(@NonNull T value) {
    return new Some<>(Objects.requireNonNull(value, "value"));
  }

  /**
   * Create a {@link None}.
   *
   * @param <T> value type
   * @return empty Option
   */
  static <T> Option<T> none() {
    return None.instance();
  }

  /**
   * Create {@link Some} when non-null, otherwise {@link None}.
   *
   * @param value possibly-null value
   * @param <T> value type
   * @return Option of value
   */
  static <T> Option<T> ofNullable(@Nullable T value) {
    return value == null ? none() : some(value);
  }

  /**
   * Convert {@link Optional} to {@link Option}.
   *
   * @param optional Optional to convert
   * @param <T> value type
   * @return Option of optional
   */
  static <T> Option<T> fromOptional(@NonNull Optional<? extends T> optional) {
    Objects.requireNonNull(optional, "optional");
    @SuppressWarnings("unchecked")
    final var result = (Option<T>) optional.map(Option::some).orElseGet(Option::none);
    return result;
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
   * @return present value
   * @throws NoSuchElementException when empty
   */
  @NonNull T get();

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
  default @Nullable T getOrElseGet(@NonNull Supplier<? extends T> supplier) {
    Objects.requireNonNull(supplier, "supplier");
    return isPresent() ? get() : supplier.get();
  }

  /**
   * Return this Option when present, otherwise return the provided fallback.
   *
   * @param fallback alternative Option
   * @return this or fallback
   */
  default Option<T> orElse(@NonNull Option<? extends T> fallback) {
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
  default Option<T> orElseGet(
      @NonNull Supplier<? extends Option<? extends T>> supplier) {
    Objects.requireNonNull(supplier, "supplier");
    if (isPresent()) {
      return this;
    }
    @SuppressWarnings("unchecked")
    final var supplied = (Option<T>) supplier.get();
    return Objects.requireNonNull(supplied, "supplied");
  }

  /**
   * Return the value when present or throw a supplied exception when empty.
   *
   * @param exceptionSupplier supplier for exception
   * @param <X> exception type
   * @return present value
   * @throws X when empty
   */
  default <X extends Throwable> @NonNull T orElseThrow(
      @NonNull Supplier<? extends X> exceptionSupplier) throws X {
    Objects.requireNonNull(exceptionSupplier, "exceptionSupplier");
    if (isPresent()) {
      return get();
    }
    throw exceptionSupplier.get();
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
      @NonNull Function<? super T, ? extends U> ifPresent) {
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
  default Option<T> filter(@NonNull Predicate<? super T> predicate) {
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
  default Option<T> filterNot(@NonNull Predicate<? super T> predicate) {
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
  default <U> Option<U> map(@NonNull Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper, "mapper");
    if (isEmpty()) {
      return none();
    }
    return Option.ofNullable(mapper.apply(get()));
  }

  /**
   * Transform the value with a function that already returns an Option.
   *
   * @param mapper mapping function
   * @param <U> result type
   * @return mapped Option
   */
  default <U> Option<U> flatMap(
      @NonNull Function<? super T, ? extends Option<? extends U>> mapper) {
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
  default Option<T> peek(@NonNull Consumer<? super T> action) {
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
  default void ifPresent(@NonNull Consumer<? super T> action) {
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
      @NonNull Consumer<? super T> action, @NonNull Runnable emptyAction) {
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
  default boolean exists(@NonNull Predicate<? super T> predicate) {
    Objects.requireNonNull(predicate, "predicate");
    return isPresent() && predicate.test(get());
  }

  /**
   * Test a predicate against the value when present, or true when empty.
   *
   * @param predicate predicate to test
   * @return true when empty or predicate matches
   */
  default boolean forAll(@NonNull Predicate<? super T> predicate) {
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
  default <E> Result<T, E> toResult(@NonNull Supplier<? extends E> ifEmpty) {
    Objects.requireNonNull(ifEmpty, "ifEmpty");
    return isPresent() ? Result.ok(get()) : Result.err(ifEmpty.get());
  }

  /**
   * Convert to a {@link Stream} with 0 or 1 element.
   *
   * @return Stream of the value when present
   */
  default Stream<T> stream() {
    return isPresent() ? Stream.of(get()) : Stream.empty();
  }

  /**
   * Convert to {@link Optional} for interop with standard APIs.
   *
   * @return Optional view
   */
  default Optional<T> toOptional() {
    return isPresent() ? Optional.of(get()) : Optional.empty();
  }

  /**
   * Present case.
   *
   * @param <T> value type
   */
  record Some<T>(@NonNull T value) implements Option<T> {
    public Some {
      Objects.requireNonNull(value, "value");
    }

    @Override
    public boolean isPresent() {
      return true;
    }

    @Override
    public @NonNull T get() {
      return value;
    }

    @Override
    public String toString() {
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
    public String toString() {
      return "None";
    }
  }
}
