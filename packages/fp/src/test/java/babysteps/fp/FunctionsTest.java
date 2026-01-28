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
  void compose_expectedApplyFunctions() {
    // Arrange
    final var fn =
        Functions.compose((Integer value) -> value * 2, (String value) -> value.length());

    // Act
    final var result = fn.apply("abcd");

    // Assert
    softly.assertThat(result).isEqualTo(8);
  }

  @Test
  void pipe_expectedApplyFunctions() {
    // Arrange
    final var fn = Functions.pipe((String value) -> value.length(), (Integer value) -> value * 2);

    // Act
    final var result = fn.apply("abcd");

    // Assert
    softly.assertThat(result).isEqualTo(8);
  }

  @Test
  void curry_expectedApplyBiFunction() {
    // Arrange
    final var fn = Functions.curry((String left, String right) -> left + right);

    // Act
    final var result = fn.apply("a").apply("b");

    // Assert
    softly.assertThat(result).isEqualTo("ab");
  }

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
  void memoize_withFunction_repeatedInput_expectedSingleEvaluation() {
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

    // Assert
    softly.assertThat(first).isEqualTo(5);
    softly.assertThat(calls.get()).isEqualTo(1);
  }

  @Test
  void memoize_withFunction_sameInputAgain_expectedCachedValue() {
    // Arrange
    final var calls = new AtomicInteger(0);
    final var fn =
        Functions.memoize(
            (String value) -> {
              calls.incrementAndGet();
              return value.length();
            });

    fn.apply("value");

    // Act
    final var result = fn.apply("value");

    // Assert
    softly.assertThat(result).isEqualTo(5);
    softly.assertThat(calls.get()).isEqualTo(1);
  }

  @Test
  void memoize_withFunction_nullInput_expectedCachedNullValue() {
    // Arrange
    final var calls = new AtomicInteger(0);
    final var fn =
        Functions.memoize(
            value -> {
              calls.incrementAndGet();
              return null;
            });

    fn.apply(null);

    // Act
    final var result = fn.apply(null);

    // Assert
    softly.assertThat(result).isNull();
    softly.assertThat(calls.get()).isEqualTo(1);
  }

  @Test
  void memoize_withFunction_differentInput_expectedNewEvaluation() {
    // Arrange
    final var calls = new AtomicInteger(0);
    final var fn =
        Functions.memoize(
            (String value) -> {
              calls.incrementAndGet();
              return value.length();
            });

    fn.apply("value");

    // Act
    final var result = fn.apply("other");

    // Assert
    softly.assertThat(result).isEqualTo(5);
    softly.assertThat(calls.get()).isEqualTo(2);
  }

  @Test
  void memoize_withBiFunction_repeatedInput_expectedSingleEvaluation() {
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

    // Assert
    softly.assertThat(first).isEqualTo("ab");
    softly.assertThat(calls.get()).isEqualTo(1);
  }

  @Test
  void memoize_withBiFunction_sameInputAgain_expectedCachedValue() {
    // Arrange
    final var calls = new AtomicInteger(0);
    final var fn =
        Functions.memoize(
            (String left, String right) -> {
              calls.incrementAndGet();
              return left + right;
            });

    fn.apply("a", "b");

    // Act
    final var result = fn.apply("a", "b");

    // Assert
    softly.assertThat(result).isEqualTo("ab");
    softly.assertThat(calls.get()).isEqualTo(1);
  }

  @Test
  void memoize_withBiFunction_nullInput_expectedCachedNullValue() {
    // Arrange
    final var calls = new AtomicInteger(0);
    final var fn =
        Functions.memoize(
            (String left, String right) -> {
              calls.incrementAndGet();
              return null;
            });

    fn.apply(null, null);

    // Act
    final var result = fn.apply(null, null);

    // Assert
    softly.assertThat(result).isNull();
    softly.assertThat(calls.get()).isEqualTo(1);
  }

  @Test
  void memoize_withBiFunction_differentInput_expectedNewEvaluation() {
    // Arrange
    final var calls = new AtomicInteger(0);
    final var fn =
        Functions.memoize(
            (String left, String right) -> {
              calls.incrementAndGet();
              return left + right;
            });

    fn.apply("a", "b");

    // Act
    final var result = fn.apply("a", "c");

    // Assert
    softly.assertThat(result).isEqualTo("ac");
    softly.assertThat(calls.get()).isEqualTo(2);
  }

}
