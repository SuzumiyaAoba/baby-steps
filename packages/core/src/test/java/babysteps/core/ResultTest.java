package babysteps.core;

import java.util.concurrent.atomic.AtomicBoolean;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class ResultTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void isOk_withOk_expectedTrue() {
    // Arrange
    final var sut = Result.ok("value");

    // Act
    final var result = sut.isOk();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void isErr_withOk_expectedFalse() {
    // Arrange
    final var sut = Result.ok("value");

    // Act
    final var result = sut.isErr();

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void isOk_withErr_expectedFalse() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.isOk();

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void isErr_withErr_expectedTrue() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.isErr();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void unwrap_withOk_expectedValue() {
    // Arrange
    final var sut = Result.ok("value");

    // Act
    final var result = sut.unwrap();

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void unwrapErr_withOk_expectedException() {
    // Arrange
    final var sut = Result.ok("value");

    // Act
    final var action = (ThrowingCallable) sut::unwrapErr;

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Result is Ok");
  }

  @Test
  void unwrap_withErr_expectedException() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var action = (ThrowingCallable) sut::unwrap;

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Result is Err");
  }

  @Test
  void unwrapErr_withErr_expectedValue() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.unwrapErr();

    // Assert
    softly.assertThat(result).isEqualTo("error");
  }

  @Test
  void unwrapOr_withOk_expectedValue() {
    // Arrange
    final var sut = Result.ok("value");

    // Act
    final var result = sut.unwrapOr("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void unwrapOr_withErr_expectedFallback() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.unwrapOr("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void map_withOk_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.map(String::length);

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo(5);
  }

  @Test
  void map_withErr_expectedErrAndMapperNotCalled() {
    // Arrange
    final var mapped = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.map(
            value -> {
              mapped.set(true);
              return value.length();
            });

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).isEqualTo("error");
    softly.assertThat(mapped).isFalse();
  }

  @Test
  void mapErr_withErr_expectedMappedError() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.mapErr(String::length);

    // Assert
    softly.assertThat(result.unwrapErr()).isEqualTo(5);
  }

  @Test
  void mapErr_withOk_expectedOkAndMapperNotCalled() {
    // Arrange
    final var mapped = new AtomicBoolean(false);
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result =
        sut.mapErr(
            value -> {
              mapped.set(true);
              return value.length();
            });

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("value");
    softly.assertThat(mapped).isFalse();
  }

  @Test
  void flatMap_withOk_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.flatMap(value -> Result.ok(value + "!"));

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("value!");
  }

  @Test
  void flatMap_withErr_expectedErrAndMapperNotCalled() {
    // Arrange
    final var mapped = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.flatMap(
            value -> {
              mapped.set(true);
              return Result.ok(value + "!");
            });

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).isEqualTo("error");
    softly.assertThat(mapped).isFalse();
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void err_withNull_expectedException() {
    // Arrange
    // Act
    final var action = (ThrowingCallable) () -> Result.err(null);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("error");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void map_withNullMapper_expectedException() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var action = (ThrowingCallable) () -> sut.map(null);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("mapper");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void mapErr_withNullMapper_expectedException() {
    // Arrange
    final var sut = Result.err("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.mapErr(null);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("mapper");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void flatMap_withNullMapper_expectedException() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var action = (ThrowingCallable) () -> sut.flatMap(null);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("mapper");
  }

  @Test
  void flatMap_withNullMapped_expectedException() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var action = (ThrowingCallable) () -> sut.flatMap(value -> null);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("mapped");
  }
}
