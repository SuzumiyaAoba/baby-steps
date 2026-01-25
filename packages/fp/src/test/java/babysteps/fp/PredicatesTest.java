package babysteps.fp;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class PredicatesTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void and_withBothTrue_expectedTrue() {
    // Arrange
    final Predicate<String> left = value -> value.startsWith("a");
    final Predicate<String> right = value -> value.endsWith("z");
    final var sut = Predicates.and(left, right);

    // Act
    final var result = sut.test("abz");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void and_withLeftFalse_expectedFalse() {
    // Arrange
    final Predicate<String> left = value -> value.startsWith("a");
    final Predicate<String> right = value -> value.endsWith("z");
    final var sut = Predicates.and(left, right);

    // Act
    final var result = sut.test("bbz");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void and_withRightFalse_expectedFalse() {
    // Arrange
    final Predicate<String> left = value -> value.startsWith("a");
    final Predicate<String> right = value -> value.endsWith("z");
    final var sut = Predicates.and(left, right);

    // Act
    final var result = sut.test("abc");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void and_withLeftFalse_expectedRightNotCalled() {
    // Arrange
    final Predicate<String> left = value -> false;
    final var called = new AtomicBoolean(false);
    final Predicate<String> right =
        value -> {
          called.set(true);
          return true;
        };
    final var sut = Predicates.and(left, right);

    // Act
    final var result = sut.test("value");

    // Assert
    softly.assertThat(result).isFalse();
    softly.assertThat(called).isFalse();
  }

  @Test
  void or_withBothFalse_expectedFalse() {
    // Arrange
    final Predicate<String> left = value -> value.startsWith("a");
    final Predicate<String> right = value -> value.endsWith("z");
    final var sut = Predicates.or(left, right);

    // Act
    final var result = sut.test("bbb");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void or_withLeftTrue_expectedTrue() {
    // Arrange
    final Predicate<String> left = value -> value.startsWith("a");
    final Predicate<String> right = value -> value.endsWith("z");
    final var sut = Predicates.or(left, right);

    // Act
    final var result = sut.test("abc");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void or_withRightTrue_expectedTrue() {
    // Arrange
    final Predicate<String> left = value -> value.startsWith("a");
    final Predicate<String> right = value -> value.endsWith("z");
    final var sut = Predicates.or(left, right);

    // Act
    final var result = sut.test("bbz");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void or_withLeftTrue_expectedRightNotCalled() {
    // Arrange
    final Predicate<String> left = value -> true;
    final var called = new AtomicBoolean(false);
    final Predicate<String> right =
        value -> {
          called.set(true);
          return true;
        };
    final var sut = Predicates.or(left, right);

    // Act
    final var result = sut.test("value");

    // Assert
    softly.assertThat(result).isTrue();
    softly.assertThat(called).isFalse();
  }

  @Test
  void not_withTrue_expectedFalse() {
    // Arrange
    final Predicate<String> predicate = value -> value.startsWith("a");
    final var sut = Predicates.not(predicate);

    // Act
    final var result = sut.test("abc");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void not_withFalse_expectedTrue() {
    // Arrange
    final Predicate<String> predicate = value -> value.startsWith("a");
    final var sut = Predicates.not(predicate);

    // Act
    final var result = sut.test("bbb");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void isNull_withNull_expectedTrue() {
    // Arrange
    final var sut = Predicates.<String>isNull();

    // Act
    final var result = sut.test(null);

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void isNull_withValue_expectedFalse() {
    // Arrange
    final var sut = Predicates.<String>isNull();

    // Act
    final var result = sut.test("value");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void nonNull_withNull_expectedFalse() {
    // Arrange
    final var sut = Predicates.<String>nonNull();

    // Act
    final var result = sut.test(null);

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void nonNull_withValue_expectedTrue() {
    // Arrange
    final var sut = Predicates.<String>nonNull();

    // Act
    final var result = sut.test("value");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void isEqual_withSameValue_expectedTrue() {
    // Arrange
    final var sut = Predicates.isEqual("value");

    // Act
    final var result = sut.test("value");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void isEqual_withDifferentValue_expectedFalse() {
    // Arrange
    final var sut = Predicates.isEqual("value");

    // Act
    final var result = sut.test("other");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void isEqual_withNullExpected_expectedTrue() {
    // Arrange
    final var sut = Predicates.isEqual(null);

    // Act
    final var result = sut.test(null);

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void alwaysTrue_expectedTrue() {
    // Arrange
    final var sut = Predicates.<String>alwaysTrue();

    // Act
    final var result = sut.test("value");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void alwaysFalse_expectedFalse() {
    // Arrange
    final var sut = Predicates.<String>alwaysFalse();

    // Act
    final var result = sut.test("value");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void in_withContainedValue_expectedTrue() {
    // Arrange
    final var values = List.of("a", "b");
    final var sut = Predicates.in(values);

    // Act
    final var result = sut.test("a");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void in_withMissingValue_expectedFalse() {
    // Arrange
    final var values = List.of("a", "b");
    final var sut = Predicates.in(values);

    // Act
    final var result = sut.test("c");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void notIn_withContainedValue_expectedFalse() {
    // Arrange
    final var values = List.of("a", "b");
    final var sut = Predicates.notIn(values);

    // Act
    final var result = sut.test("a");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void notIn_withMissingValue_expectedTrue() {
    // Arrange
    final var values = List.of("a", "b");
    final var sut = Predicates.notIn(values);

    // Act
    final var result = sut.test("c");

    // Assert
    softly.assertThat(result).isTrue();
  }
}
