package babysteps.fp;

import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class FunctionsTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void tupled_expectedApplyBiFunction() {
    // Arrange
    final var fn = Functions.tupled((String left, String right) -> left + right);

    // Act
    final var result = fn.apply(new Tuple2<>("a", "b"));

    // Assert
    softly.assertThat(result).isEqualTo("ab");
  }

  @Test
  void untupled_expectedApplyFunction() {
    // Arrange
    final var fn =
        Functions.untupled((Tuple2<String, String> tuple) -> tuple.first() + tuple.second());

    // Act
    final var result = fn.apply("a", "b");

    // Assert
    softly.assertThat(result).isEqualTo("ab");
  }

  @Test
  void flip_expectedSwapArguments() {
    // Arrange
    final var fn = Functions.flip((String left, String right) -> left + right);

    // Act
    final var result = fn.apply("b", "a");

    // Assert
    softly.assertThat(result).isEqualTo("ab");
  }

  @Test
  void partial_expectedFixFirstArgument() {
    // Arrange
    final var fn = Functions.partial((String left, String right) -> left + right, "a");

    // Act
    final var result = fn.apply("b");

    // Assert
    softly.assertThat(result).isEqualTo("ab");
  }

  @Test
  void memoize_withFunction_expectedCacheByInput() {
    // Arrange
    final var calls = new AtomicInteger(0);
    final var fn =
        Functions.memoize(
            (String value) -> {
              calls.incrementAndGet();
              return value.length();
            });

    // Act
    final var first = fn.apply("value");
    final var second = fn.apply("value");
    final var third = fn.apply("other");

    // Assert
    softly.assertThat(first).isEqualTo(5);
    softly.assertThat(second).isEqualTo(5);
    softly.assertThat(third).isEqualTo(5);
    softly.assertThat(calls.get()).isEqualTo(2);
  }

  @Test
  void memoize_withBiFunction_expectedCacheByTuple() {
    // Arrange
    final var calls = new AtomicInteger(0);
    final var fn =
        Functions.memoize(
            (String left, String right) -> {
              calls.incrementAndGet();
              return left + right;
            });

    // Act
    final var first = fn.apply("a", "b");
    final var second = fn.apply("a", "b");
    final var third = fn.apply("a", "c");

    // Assert
    softly.assertThat(first).isEqualTo("ab");
    softly.assertThat(second).isEqualTo("ab");
    softly.assertThat(third).isEqualTo("ac");
    softly.assertThat(calls.get()).isEqualTo(2);
  }
}
