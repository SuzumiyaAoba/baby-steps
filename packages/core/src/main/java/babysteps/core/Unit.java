package babysteps.core;

import org.jspecify.annotations.NonNull;

/** Singleton type to model {@code void} in functional pipelines. */
public final class Unit {
  private static final Unit INSTANCE = new Unit();

  private Unit() {}

  /**
   * @return the singleton {@code Unit} value
   */
  public static @NonNull Unit instance() {
    return INSTANCE;
  }

  @Override
  public @NonNull String toString() {
    return "Unit";
  }

  /**
   * Returns {@code true} for any other {@code Unit} instance.
   *
   * @param obj object to compare
   * @return {@code true} when {@code obj} is a {@code Unit}
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Unit;
  }

  /**
   * Returns a constant hash code for all {@code Unit} instances.
   *
   * @return hash code
   */
  @Override
  public int hashCode() {
    return 0;
  }
}
