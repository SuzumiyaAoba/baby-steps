package babysteps.core;

import java.util.concurrent.atomic.AtomicBoolean;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class TryTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void isSuccess_withSuccess_expectedTrue() {
    // Arrange
    final var sut = Try.success("value");

    // Act
    final var result = sut.isSuccess();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void isFailure_withSuccess_expectedFalse() {
    // Arrange
    final var sut = Try.success("value");

    // Act
    final var result = sut.isFailure();

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void isSuccess_withFailure_expectedFalse() {
    // Arrange
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var result = sut.isSuccess();

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void isFailure_withFailure_expectedTrue() {
    // Arrange
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var result = sut.isFailure();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void get_withSuccess_expectedValue() {
    // Arrange
    final var sut = Try.success("value");

    // Act
    final var result = sut.get();

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void get_withFailure_expectedException() {
    // Arrange
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var action = (ThrowingCallable) sut::get;

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Try is Failure");
  }

  @Test
  void getCause_withFailure_expectedCause() {
    // Arrange
    final var cause = new IllegalStateException("boom");
    final var sut = Try.failure(cause);

    // Act
    final var result = sut.getCause();

    // Assert
    softly.assertThat(result).isSameAs(cause);
  }

  @Test
  void getCause_withSuccess_expectedException() {
    // Arrange
    final var sut = Try.success("value");

    // Act
    final var action = (ThrowingCallable) sut::getCause;

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Try is Success");
  }

  @Test
  void of_withSuccess_expectedSuccess() {
    // Arrange
    // Act
    final var result = Try.of(() -> "value");

    // Assert
    softly.assertThat(result.get()).isEqualTo("value");
  }

  @Test
  void of_withException_expectedFailure() {
    // Arrange
    final var cause = new Exception("boom");

    // Act
    final var result =
        Try.of(
            () -> {
              throw cause;
            });

    // Assert
    softly.assertThat(result.isFailure()).isTrue();
    softly.assertThat(result.getCause()).isSameAs(cause);
  }

  @Test
  void getOrElse_withSuccess_expectedValue() {
    // Arrange
    final var sut = Try.success("value");

    // Act
    final var result = sut.getOrElse("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void getOrElse_withFailure_expectedFallback() {
    // Arrange
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var result = sut.getOrElse("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void getOrElseGet_withSuccess_expectedValueAndSupplierNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Try.success("value");

    // Act
    final var result =
        sut.getOrElseGet(
            () -> {
              called.set(true);
              return "fallback";
            });

    // Assert
    softly.assertThat(result).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void getOrElseGet_withFailure_expectedFallback() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var result =
        sut.getOrElseGet(
            () -> {
              called.set(true);
              return "fallback";
            });

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
    softly.assertThat(called).isTrue();
  }

  @Test
  void orElseThrow_withSuccess_expectedValueAndSupplierNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Try.success("value");

    // Act
    final var result =
        sut.orElseThrow(
            () -> {
              called.set(true);
              return new IllegalStateException("boom");
            });

    // Assert
    softly.assertThat(result).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void orElseThrow_withFailure_expectedException() {
    // Arrange
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var action = (ThrowingCallable) () -> sut.orElseThrow(() -> new RuntimeException("nope"));

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(RuntimeException.class).hasMessage("nope");
  }

  @Test
  void map_withSuccess_expectedMappedValue() {
    // Arrange
    final var sut = Try.success("value");

    // Act
    final var result = sut.map(String::length);

    // Assert
    softly.assertThat(result.get()).isEqualTo(5);
  }

  @Test
  void map_withFailure_expectedFailureAndMapperNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Try.<String>failure(new IllegalStateException("boom"));

    // Act
    final var result =
        sut.map(
            value -> {
              called.set(true);
              return value.length();
            });

    // Assert
    softly.assertThat(result.isFailure()).isTrue();
    softly.assertThat(result.getCause().getMessage()).isEqualTo("boom");
    softly.assertThat(called).isFalse();
  }

  @Test
  void map_withThrowingMapper_expectedFailure() {
    // Arrange
    final var sut = Try.success("value");

    // Act
    final var result =
        sut.map(
            value -> {
              throw new IllegalArgumentException("mapped");
            });

    // Assert
    softly.assertThat(result.isFailure()).isTrue();
    softly
        .assertThat(result.getCause())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("mapped");
  }

  @Test
  void fold_withSuccess_expectedSuccessHandler() {
    // Arrange
    final var sut = Try.success("value");

    // Act
    final var result = sut.fold(Throwable::getMessage, value -> value + "!");

    // Assert
    softly.assertThat(result).isEqualTo("value!");
  }

  @Test
  void fold_withFailure_expectedFailureHandler() {
    // Arrange
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var result = sut.fold(Throwable::getMessage, value -> "unused");

    // Assert
    softly.assertThat(result).isEqualTo("boom");
  }

  @Test
  void peek_withSuccess_expectedActionCalledAndSameInstance() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Try.success("value");

    // Act
    final var result =
        sut.peek(
            value -> {
              called.set(true);
              softly.assertThat(value).isEqualTo("value");
            });

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(called).isTrue();
  }

  @Test
  void peek_withFailure_expectedActionNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var result = sut.peek(value -> called.set(true));

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(called).isFalse();
  }

  @Test
  void peekFailure_withFailure_expectedActionCalledAndSameInstance() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var result =
        sut.peekFailure(
            error -> {
              called.set(true);
              softly.assertThat(error).isInstanceOf(IllegalStateException.class);
            });

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(called).isTrue();
  }

  @Test
  void peekFailure_withSuccess_expectedActionNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Try.success("value");

    // Act
    final var result = sut.peekFailure(error -> called.set(true));

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(called).isFalse();
  }

  @Test
  void flatMap_withSuccess_expectedMappedValue() {
    // Arrange
    final var sut = Try.success("value");

    // Act
    final var result = sut.flatMap(value -> Try.success(value + "!"));

    // Assert
    softly.assertThat(result.get()).isEqualTo("value!");
  }

  @Test
  void flatMap_withFailure_expectedFailureAndMapperNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Try.<String>failure(new IllegalStateException("boom"));

    // Act
    final var result =
        sut.flatMap(
            value -> {
              called.set(true);
              return Try.success(value + "!");
            });

    // Assert
    softly.assertThat(result.isFailure()).isTrue();
    softly.assertThat(result.getCause().getMessage()).isEqualTo("boom");
    softly.assertThat(called).isFalse();
  }

  @Test
  void flatMap_withThrowingMapper_expectedFailure() {
    // Arrange
    final var sut = Try.success("value");

    // Act
    final var result =
        sut.flatMap(
            value -> {
              throw new IllegalArgumentException("mapped");
            });

    // Assert
    softly.assertThat(result.isFailure()).isTrue();
    softly
        .assertThat(result.getCause())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("mapped");
  }

  @Test
  void recover_withFailure_expectedSuccess() {
    // Arrange
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var result = sut.recover(error -> "recovered");

    // Assert
    softly.assertThat(result.get()).isEqualTo("recovered");
  }

  @Test
  void recover_withFailureAndThrowingMapper_expectedFailure() {
    // Arrange
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var result =
        sut.recover(
            error -> {
              throw new IllegalArgumentException("mapped");
            });

    // Assert
    softly.assertThat(result.isFailure()).isTrue();
    softly
        .assertThat(result.getCause())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("mapped");
  }

  @Test
  void recover_withSuccess_expectedSuccessAndMapperNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Try.success("value");

    // Act
    final var result =
        sut.recover(
            error -> {
              called.set(true);
              return "recovered";
            });

    // Assert
    softly.assertThat(result.get()).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void recoverWith_withFailure_expectedSuccess() {
    // Arrange
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var result = sut.recoverWith(error -> Try.success("recovered"));

    // Assert
    softly.assertThat(result.get()).isEqualTo("recovered");
  }

  @Test
  void recoverWith_withFailureAndThrowingMapper_expectedFailure() {
    // Arrange
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var result =
        sut.recoverWith(
            error -> {
              throw new IllegalArgumentException("mapped");
            });

    // Assert
    softly.assertThat(result.isFailure()).isTrue();
    softly
        .assertThat(result.getCause())
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("mapped");
  }

  @Test
  void recoverWith_withSuccess_expectedSuccessAndMapperNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Try.success("value");

    // Act
    final var result =
        sut.recoverWith(
            error -> {
              called.set(true);
              return Try.success("recovered");
            });

    // Assert
    softly.assertThat(result.get()).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void toResult_withSuccess_expectedOk() {
    // Arrange
    final var sut = Try.success("value");

    // Act
    final var result = sut.toResult();

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("value");
  }

  @Test
  void toResult_withFailure_expectedErr() {
    // Arrange
    final var cause = new IllegalStateException("boom");
    final var sut = Try.failure(cause);

    // Act
    final var result = sut.toResult();

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).isSameAs(cause);
  }

  @Test
  void toOption_withSuccess_expectedSome() {
    // Arrange
    final var sut = Try.success("value");

    // Act
    final var result = sut.toOption();

    // Assert
    softly.assertThat(result.isPresent()).isTrue();
    softly.assertThat(result.get()).isEqualTo("value");
  }

  @Test
  void toOption_withFailure_expectedNone() {
    // Arrange
    final var sut = Try.failure(new IllegalStateException("boom"));

    // Act
    final var result = sut.toOption();

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void failure_withNullError_expectedException() {
    // Arrange
    // Act
    final var action = (ThrowingCallable) () -> Try.failure(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class).hasMessage("error");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void of_withNullSupplier_expectedException() {
    // Arrange
    // Act
    final var action = (ThrowingCallable) () -> Try.of(null);

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("supplier");
  }
}
