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

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Unit;
  }

  @Override
  public int hashCode() {
    return 0;
  }
}
