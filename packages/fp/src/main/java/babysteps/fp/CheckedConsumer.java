package babysteps.fp;

/**
 * Consumer that allows checked exceptions.
 *
 * @param <T> input type
 */
@FunctionalInterface
public interface CheckedConsumer<T> {
  void accept(T value) throws Exception;
}
