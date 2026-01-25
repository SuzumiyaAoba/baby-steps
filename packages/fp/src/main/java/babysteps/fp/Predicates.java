package babysteps.fp;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Utility predicates for composition. */
public final class Predicates {
  private Predicates() {}

  /**
   * Combine predicates with logical AND.
   *
   * @param left left predicate
   * @param right right predicate
   * @param <T> input type
   * @return combined predicate
   * @throws NullPointerException if {@code left} or {@code right} is {@code null}
   */
  public static <T> @NonNull Predicate<@Nullable T> and(
      @NonNull Predicate<? super @Nullable T> left, @NonNull Predicate<? super @Nullable T> right) {
    Objects.requireNonNull(left, "left");
    Objects.requireNonNull(right, "right");
    return value -> left.test(value) && right.test(value);
  }

  /**
   * Combine predicates with logical OR.
   *
   * @param left left predicate
   * @param right right predicate
   * @param <T> input type
   * @return combined predicate
   * @throws NullPointerException if {@code left} or {@code right} is {@code null}
   */
  public static <T> @NonNull Predicate<@Nullable T> or(
      @NonNull Predicate<? super @Nullable T> left, @NonNull Predicate<? super @Nullable T> right) {
    Objects.requireNonNull(left, "left");
    Objects.requireNonNull(right, "right");
    return value -> left.test(value) || right.test(value);
  }

  /**
   * Negate a predicate.
   *
   * @param predicate predicate to negate
   * @param <T> input type
   * @return negated predicate
   * @throws NullPointerException if {@code predicate} is {@code null}
   */
  public static <T> @NonNull Predicate<@Nullable T> not(
      @NonNull Predicate<? super @Nullable T> predicate) {
    Objects.requireNonNull(predicate, "predicate");
    return value -> !predicate.test(value);
  }

  /**
   * Returns a predicate that checks for {@code null}.
   *
   * @param <T> input type
   * @return predicate that returns {@code true} when input is {@code null}
   */
  public static <T> @NonNull Predicate<@Nullable T> isNull() {
    return Objects::isNull;
  }

  /**
   * Returns a predicate that checks for non-null.
   *
   * @param <T> input type
   * @return predicate that returns {@code true} when input is not {@code null}
   */
  public static <T> @NonNull Predicate<@Nullable T> nonNull() {
    return Objects::nonNull;
  }

  /**
   * Returns a predicate that checks for equality with the given value.
   *
   * @param expected expected value, possibly {@code null}
   * @param <T> input type
   * @return predicate that returns {@code true} when input equals {@code expected}
   */
  public static <T> @NonNull Predicate<@Nullable T> isEqual(@Nullable T expected) {
    return value -> Objects.equals(value, expected);
  }

  /**
   * Returns a predicate that always returns {@code true}.
   *
   * @param <T> input type
   * @return predicate that always returns {@code true}
   */
  public static <T> @NonNull Predicate<@Nullable T> alwaysTrue() {
    return value -> true;
  }

  /**
   * Returns a predicate that always returns {@code false}.
   *
   * @param <T> input type
   * @return predicate that always returns {@code false}
   */
  public static <T> @NonNull Predicate<@Nullable T> alwaysFalse() {
    return value -> false;
  }

  /**
   * Returns a predicate that checks membership in the given collection.
   *
   * @param values collection to check
   * @param <T> input type
   * @return predicate that returns {@code true} when input is contained in {@code values}
   * @throws NullPointerException if {@code values} is {@code null}
   */
  public static <T> @NonNull Predicate<@Nullable T> in(@NonNull Collection<? extends T> values) {
    Objects.requireNonNull(values, "values");
    return values::contains;
  }

  /**
   * Returns a predicate that checks non-membership in the given collection.
   *
   * @param values collection to check
   * @param <T> input type
   * @return predicate that returns {@code true} when input is not contained in {@code values}
   * @throws NullPointerException if {@code values} is {@code null}
   */
  public static <T> @NonNull Predicate<@Nullable T> notIn(@NonNull Collection<? extends T> values) {
    Objects.requireNonNull(values, "values");
    return value -> !values.contains(value);
  }
}
