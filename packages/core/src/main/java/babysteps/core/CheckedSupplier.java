package babysteps.core;

/**
 * Supplier that allows checked exceptions.
 *
 * @param <T> supplied value type
 */
@FunctionalInterface
public interface CheckedSupplier<T> {
  T get() throws Exception;
}
