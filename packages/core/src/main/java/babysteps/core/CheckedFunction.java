package babysteps.core;

/**
 * Function that allows checked exceptions.
 *
 * @param <T> input type
 * @param <R> output type
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {
  R apply(T value) throws Exception;
}
