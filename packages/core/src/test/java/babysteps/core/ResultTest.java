package babysteps.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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
    softly
        .assertThatThrownBy(action)
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
    softly
        .assertThatThrownBy(action)
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
  void unwrapOrDefault_withOk_expectedValue() {
    // Arrange
    final var sut = Result.ok("value");

    // Act
    final var result = sut.unwrapOrDefault("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void unwrapOrDefault_withErr_expectedFallback() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.unwrapOrDefault("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void unwrapOrElse_withOk_expectedValueAndSupplierNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.ok("value");

    // Act
    final var result =
        sut.unwrapOrElse(
            () -> {
              called.set(true);
              return "fallback";
            });

    // Assert
    softly.assertThat(result).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void unwrapOrElse_withErr_expectedFallback() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.unwrapOrElse(
            () -> {
              called.set(true);
              return "fallback";
            });

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
    softly.assertThat(called).isTrue();
  }

  @Test
  void unwrapOrElseGet_withOk_expectedValueAndSupplierNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.ok("value");

    // Act
    final var result =
        sut.unwrapOrElseGet(
            () -> {
              called.set(true);
              return "fallback";
            });

    // Assert
    softly.assertThat(result).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void unwrapOrElseGet_withErr_expectedFallback() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.unwrapOrElseGet(
            () -> {
              called.set(true);
              return "fallback";
            });

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
    softly.assertThat(called).isTrue();
  }

  @Test
  void unwrapOrThrow_withOk_expectedValueAndMapperNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.ok("value");

    // Act
    final var result =
        sut.unwrapOrThrow(
            error -> {
              called.set(true);
              return new IllegalArgumentException(String.valueOf(error));
            });

    // Assert
    softly.assertThat(result).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void unwrapOrThrow_withErr_expectedException() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var action =
        (ThrowingCallable)
            () -> sut.unwrapOrThrow(error -> new IllegalArgumentException("mapped-" + error));

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("mapped-error");
  }

  @Test
  void orElseThrow_withOk_expectedValueAndSupplierNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.ok("value");

    // Act
    final var result =
        sut.orElseThrow(
            () -> {
              called.set(true);
              return new IllegalStateException("missing");
            });

    // Assert
    softly.assertThat(result).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void orElseThrow_withErr_expectedException() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.orElseThrow(IllegalStateException::new);

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("error");
  }

  @Test
  void expect_withOk_expectedValue() {
    // Arrange
    final var sut = Result.ok("value");

    // Act
    final var result = sut.expect("expected");

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void expect_withErr_expectedException() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.expect("expected");

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("expected");
  }

  @Test
  void expectErr_withErr_expectedValue() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.expectErr("expected");

    // Assert
    softly.assertThat(result).isEqualTo("error");
  }

  @Test
  void expectErr_withOk_expectedException() {
    // Arrange
    final var sut = Result.ok("value");

    // Act
    final var action = (ThrowingCallable) () -> sut.expectErr("expected");

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("expected");
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
  void fold_withOk_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.fold(error -> "err:" + error, value -> "ok:" + value);

    // Assert
    softly.assertThat(result).isEqualTo("ok:value");
  }

  @Test
  void fold_withErr_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.fold(error -> "err:" + error, value -> "ok:" + value);

    // Assert
    softly.assertThat(result).isEqualTo("err:error");
  }

  @Test
  void match_withOk_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.match(error -> "err:" + error, value -> "ok:" + value);

    // Assert
    softly.assertThat(result).isEqualTo("ok:value");
  }

  @Test
  void match_withErr_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.match(error -> "err:" + error, value -> "ok:" + value);

    // Assert
    softly.assertThat(result).isEqualTo("err:error");
  }

  @Test
  void match_withSuppliersOk_expectedOkSupplier() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result =
        sut.match(
            () -> {
              called.set(true);
              return "err";
            },
            () -> "ok");

    // Assert
    softly.assertThat(result).isEqualTo("ok");
    softly.assertThat(called).isFalse();
  }

  @Test
  void match_withSuppliersErr_expectedErrSupplier() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.match(
            () -> "err",
            () -> {
              called.set(true);
              return "ok";
            });

    // Assert
    softly.assertThat(result).isEqualTo("err");
    softly.assertThat(called).isFalse();
  }

  @Test
  void fold_withSuppliersOk_expectedOkSupplier() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result =
        sut.fold(
            () -> {
              called.set(true);
              return "err";
            },
            () -> "ok");

    // Assert
    softly.assertThat(result).isEqualTo("ok");
    softly.assertThat(called).isFalse();
  }

  @Test
  void fold_withSuppliersErr_expectedErrSupplier() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.fold(
            () -> "err",
            () -> {
              called.set(true);
              return "ok";
            });

    // Assert
    softly.assertThat(result).isEqualTo("err");
    softly.assertThat(called).isFalse();
  }

  @Test
  void recover_withErr_expectedOk() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.recover(error -> "recovered:" + error);

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("recovered:error");
  }

  @Test
  void recover_withOk_expectedOkAndMapperNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result =
        sut.recover(
            error -> {
              called.set(true);
              return "recovered:" + error;
            });

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void recoverWith_withErr_expectedRecovered() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.recoverWith(error -> Result.ok("recovered:" + error));

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("recovered:error");
  }

  @Test
  void recoverWith_withOk_expectedOkAndMapperNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result =
        sut.recoverWith(
            error -> {
              called.set(true);
              return Result.ok("recovered:" + error);
            });

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void recoverWithErr_withErr_expectedRecovered() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.recoverWithErr(error -> Result.ok("recovered:" + error));

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("recovered:error");
  }

  @Test
  void recoverWithErr_withOk_expectedOk() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.recoverWithErr(error -> Result.err("mapped"));

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("value");
  }

  @Test
  void swap_withOk_expectedErr() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.swap();

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).isEqualTo("value");
  }

  @Test
  void swap_withErr_expectedOk() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.swap();

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("error");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void swap_withOkNull_expectedException() {
    // Arrange
    final var sut = Result.<String, String>ok(null);

    // Act
    final var action = (ThrowingCallable) sut::swap;

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("value");
  }

  @Test
  void map_withNullValue_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>ok(null);

    // Act
    final var result = sut.map(value -> value == null ? "empty" : value);

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("empty");
  }

  @Test
  void mapOr_withOk_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.mapOr("fallback", String::length);

    // Assert
    softly.assertThat(result).isEqualTo(5);
  }

  @Test
  void mapOr_withErr_expectedFallback() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.mapOr("fallback", String::length);

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void mapOrElse_withOk_expectedMappedValue() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result =
        sut.mapOrElse(
            () -> {
              called.set(true);
              return 0;
            },
            String::length);

    // Assert
    softly.assertThat(result).isEqualTo(5);
    softly.assertThat(called).isFalse();
  }

  @Test
  void mapOrElse_withErr_expectedFallback() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.mapOrElse(
            () -> {
              called.set(true);
              return 0;
            },
            String::length);

    // Assert
    softly.assertThat(result).isEqualTo(0);
    softly.assertThat(called).isTrue();
  }

  @Test
  void mapOkOr_withOk_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.mapOkOr("fallback", String::length);

    // Assert
    softly.assertThat(result).isEqualTo(5);
  }

  @Test
  void mapOkOr_withErr_expectedFallback() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.mapOkOr("fallback", String::length);

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void mapErrOr_withErr_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.mapErrOr(0, String::length);

    // Assert
    softly.assertThat(result).isEqualTo(5);
  }

  @Test
  void mapErrOr_withOk_expectedFallback() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.mapErrOr(0, String::length);

    // Assert
    softly.assertThat(result).isEqualTo(0);
  }

  @Test
  void mapErrOrElse_withErr_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.mapErrOrElse(() -> 0, String::length);

    // Assert
    softly.assertThat(result).isEqualTo(5);
  }

  @Test
  void mapErrOrElse_withOk_expectedFallback() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.mapErrOrElse(() -> 0, String::length);

    // Assert
    softly.assertThat(result).isEqualTo(0);
  }

  @Test
  void mapBoth_withOk_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.mapBoth(String::length, String::length);

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo(5);
  }

  @Test
  void mapBoth_withErr_expectedMappedError() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.mapBoth(String::length, String::length);

    // Assert
    softly.assertThat(result.unwrapErr()).isEqualTo(5);
  }

  @Test
  void tap_withOk_expectedConsumerInvocation() {
    // Arrange
    final var seen = new AtomicReference<String>();
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.tap(seen::set);

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(seen.get()).isEqualTo("value");
  }

  @Test
  void tap_withErr_expectedConsumerNotCalled() {
    // Arrange
    final var seen = new AtomicReference<String>();
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.tap(seen::set);

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(seen.get()).isNull();
  }

  @Test
  void tapErr_withErr_expectedConsumerInvocation() {
    // Arrange
    final var seen = new AtomicReference<String>();
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.tapErr(seen::set);

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(seen.get()).isEqualTo("error");
  }

  @Test
  void tapErr_withOk_expectedConsumerNotCalled() {
    // Arrange
    final var seen = new AtomicReference<String>();
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.tapErr(seen::set);

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(seen.get()).isNull();
  }

  @Test
  void inspect_withOk_expectedConsumerInvocation() {
    // Arrange
    final var seen = new AtomicReference<String>();
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.inspect(seen::set);

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(seen.get()).isEqualTo("value");
  }

  @Test
  void inspectErr_withErr_expectedConsumerInvocation() {
    // Arrange
    final var seen = new AtomicReference<String>();
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.inspectErr(seen::set);

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(seen.get()).isEqualTo("error");
  }

  @Test
  void tapBoth_withOk_expectedOkConsumerInvocation() {
    // Arrange
    final var okSeen = new AtomicReference<String>();
    final var errSeen = new AtomicReference<String>();
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.tapBoth(okSeen::set, errSeen::set);

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(okSeen.get()).isEqualTo("value");
    softly.assertThat(errSeen.get()).isNull();
  }

  @Test
  void tapBoth_withErr_expectedErrConsumerInvocation() {
    // Arrange
    final var okSeen = new AtomicReference<String>();
    final var errSeen = new AtomicReference<String>();
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.tapBoth(okSeen::set, errSeen::set);

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(okSeen.get()).isNull();
    softly.assertThat(errSeen.get()).isEqualTo("error");
  }

  @Test
  void onOk_withOk_expectedConsumerInvocation() {
    // Arrange
    final var seen = new AtomicReference<String>();
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.onOk(seen::set);

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(seen.get()).isEqualTo("value");
  }

  @Test
  void onErr_withErr_expectedConsumerInvocation() {
    // Arrange
    final var seen = new AtomicReference<String>();
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.onErr(seen::set);

    // Assert
    softly.assertThat(result).isSameAs(sut);
    softly.assertThat(seen.get()).isEqualTo("error");
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
  void mapOk_withOk_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.mapOk(String::length);

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo(5);
  }

  @Test
  void mapOk_withErr_expectedErrAndMapperNotCalled() {
    // Arrange
    final var mapped = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.mapOk(
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
  void andThenErr_withErr_expectedMappedResult() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.andThenErr(error -> Result.err("mapped-" + error));

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).isEqualTo("mapped-error");
  }

  @Test
  void andThenErr_withOk_expectedOk() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.andThenErr(error -> Result.err("mapped"));

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("value");
  }

  @Test
  void orElse_withOk_expectedOriginal() {
    // Arrange
    final var sut = Result.<String, String>ok("value");
    final var fallback = Result.<String, String>ok("fallback");

    // Act
    final var result = sut.orElse(fallback);

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("value");
  }

  @Test
  void or_withErr_expectedFallback() {
    // Arrange
    final var sut = Result.<String, String>err("error");
    final var fallback = Result.<String, String>ok("fallback");

    // Act
    final var result = sut.or(fallback);

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("fallback");
  }

  @Test
  void or_withOk_expectedOriginal() {
    // Arrange
    final var sut = Result.<String, String>ok("value");
    final var fallback = Result.<String, String>ok("fallback");

    // Act
    final var result = sut.or(fallback);

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("value");
  }

  @Test
  void orElse_withErr_expectedFallback() {
    // Arrange
    final var sut = Result.<String, String>err("error");
    final var fallback = Result.<String, String>ok("fallback");

    // Act
    final var result = sut.orElse(fallback);

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("fallback");
  }

  @Test
  void orElseGet_withOk_expectedOriginalAndSupplierNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result =
        sut.orElseGet(
            () -> {
              called.set(true);
              return Result.ok("fallback");
            });

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void orElseGet_withErr_expectedFallback() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.orElseGet(
            () -> {
              called.set(true);
              return Result.ok("fallback");
            });

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("fallback");
    softly.assertThat(called).isTrue();
  }

  @Test
  void and_withOk_expectedNext() {
    // Arrange
    final var sut = Result.<String, String>ok("value");
    final var next = Result.<Integer, String>ok(1);

    // Act
    final var result = sut.and(next);

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo(1);
  }

  @Test
  void and_withErr_expectedErr() {
    // Arrange
    final var sut = Result.<String, String>err("error");
    final var next = Result.<Integer, String>ok(1);

    // Act
    final var result = sut.and(next);

    // Assert
    softly.assertThat(result.unwrapErr()).isEqualTo("error");
  }

  @Test
  void andThen_withOk_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.andThen(value -> Result.ok(value + "!"));

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("value!");
  }

  @Test
  void andThen_withErr_expectedErrAndMapperNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.andThen(
            value -> {
              called.set(true);
              return Result.ok(value + "!");
            });

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).isEqualTo("error");
    softly.assertThat(called).isFalse();
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
  void flatMap_withNullValue_expectedMappedValue() {
    // Arrange
    final var sut = Result.<String, String>ok(null);

    // Act
    final var result = sut.flatMap(value -> Result.ok(value == null ? "empty" : value));

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("empty");
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
  void flatten_withOkOk_expectedOk() {
    // Arrange
    final var sut = Result.<Result<String, String>, String>ok(Result.ok("value"));

    // Act
    final var result = sut.flatten();

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("value");
  }

  @Test
  void flatten_withOkErr_expectedErr() {
    // Arrange
    final var sut = Result.<Result<String, String>, String>ok(Result.err("error"));

    // Act
    final var result = sut.flatten();

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).isEqualTo("error");
  }

  @Test
  void flatten_withErr_expectedErr() {
    // Arrange
    final var sut = Result.<Result<String, String>, String>err("error");

    // Act
    final var result = sut.flatten();

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).isEqualTo("error");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void flatten_withOkNull_expectedException() {
    // Arrange
    final var sut = Result.<Result<String, String>, String>ok(null);

    // Act
    final var action = (ThrowingCallable) sut::flatten;

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("value");
  }

  @Test
  void isOkAnd_withOk_expectedTrue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.isOkAnd(value -> value.startsWith("val"));

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void isOkAnd_withOkFailingPredicate_expectedFalse() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result =
        sut.isOkAnd(
            value -> {
              called.set(true);
              return value.startsWith("nope");
            });

    // Assert
    softly.assertThat(result).isFalse();
    softly.assertThat(called).isTrue();
  }

  @Test
  void isOkAnd_withErr_expectedFalseAndPredicateNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.isOkAnd(
            value -> {
              called.set(true);
              return value.startsWith("val");
            });

    // Assert
    softly.assertThat(result).isFalse();
    softly.assertThat(called).isFalse();
  }

  @Test
  void isErrAnd_withErr_expectedTrue() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.isErrAnd(value -> value.startsWith("err"));

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void isErrAnd_withErrFailingPredicate_expectedFalse() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.isErrAnd(
            value -> {
              called.set(true);
              return value.startsWith("nope");
            });

    // Assert
    softly.assertThat(result).isFalse();
    softly.assertThat(called).isTrue();
  }

  @Test
  void isErrAnd_withOk_expectedFalseAndPredicateNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result =
        sut.isErrAnd(
            value -> {
              called.set(true);
              return value.startsWith("err");
            });

    // Assert
    softly.assertThat(result).isFalse();
    softly.assertThat(called).isFalse();
  }

  @Test
  void ok_withOk_expectedOptionalPresent() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.ok();

    // Assert
    softly.assertThat(result).isPresent();
    softly.assertThat(result.get()).isEqualTo("value");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void ok_withNull_expectedOptionalEmpty() {
    // Arrange
    final var sut = Result.<String, String>ok(null);

    // Act
    final var result = sut.ok();

    // Assert
    softly.assertThat(result).isEmpty();
  }

  @Test
  void ok_withErr_expectedOptionalEmpty() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.ok();

    // Assert
    softly.assertThat(result).isEmpty();
  }

  @Test
  void asOptional_withOk_expectedOptionalPresent() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.asOptional();

    // Assert
    softly.assertThat(result).isPresent();
    softly.assertThat(result.get()).isEqualTo("value");
  }

  @Test
  void toOption_withOk_expectedSome() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.toOption();

    // Assert
    softly.assertThat(result.isPresent()).isTrue();
    softly.assertThat(result.get()).isEqualTo("value");
  }

  @Test
  void toOption_withOkNull_expectedNone() {
    // Arrange
    final var sut = Result.<String, String>ok(null);

    // Act
    final var result = sut.toOption();

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void toOption_withErr_expectedNone() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.toOption();

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void asOption_withOk_expectedSome() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.asOption();

    // Assert
    softly.assertThat(result.isPresent()).isTrue();
    softly.assertThat(result.get()).isEqualTo("value");
  }

  @Test
  void toTry_withOk_expectedSuccess() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.toTry(RuntimeException::new);

    // Assert
    softly.assertThat(result.isSuccess()).isTrue();
    softly.assertThat(result.get()).isEqualTo("value");
  }

  @Test
  void toTry_withErr_expectedFailure() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.toTry(IllegalStateException::new);

    // Assert
    softly.assertThat(result.isFailure()).isTrue();
    softly.assertThat(result.getCause())
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("error");
  }

  @Test
  void contains_withOk_expectedTrue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.contains("value");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void containsErr_withErr_expectedTrue() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.containsErr("error");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void containsErr_withErrNonMatching_expectedFalse() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.containsErr("other");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void containsErr_withOk_expectedFalse() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.containsErr("error");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void zip_withOk_expectedCombinedValue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");
    final var other = Result.<Integer, String>ok(2);

    // Act
    final var result = sut.zip(other, (left, right) -> left + right);

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("value2");
  }

  @Test
  void zip_withErr_expectedErr() {
    // Arrange
    final var sut = Result.<String, String>err("error");
    final var other = Result.<Integer, String>ok(2);

    // Act
    final var result = sut.zip(other, (left, right) -> left + right);

    // Assert
    softly.assertThat(result.unwrapErr()).isEqualTo("error");
  }

  @Test
  void zip_withOtherErr_expectedErr() {
    // Arrange
    final var sut = Result.<String, String>ok("value");
    final var other = Result.<Integer, String>err("other");

    // Act
    final var result = sut.zip(other, (left, right) -> left + right);

    // Assert
    softly.assertThat(result.unwrapErr()).isEqualTo("other");
  }

  @Test
  void zipWith_withOk_expectedCombinedValue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");
    final var other = Result.<Integer, String>ok(2);

    // Act
    final var result = sut.zipWith(other, (left, right) -> left + right);

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("value2");
  }

  @Test
  void combine_withOk_expectedCombinedValue() {
    // Arrange
    final var sut = Result.<String, String>ok("a");
    final var other = Result.<String, String>ok("b");

    // Act
    final var result = sut.combine(other, (left, right) -> left + right);

    // Assert
    softly.assertThat(result.unwrap()).isEqualTo("ab");
  }

  @Test
  void combine_withErr_expectedErr() {
    // Arrange
    final var sut = Result.<String, String>err("error");
    final var other = Result.<String, String>ok("b");

    // Act
    final var result = sut.combine(other, (left, right) -> left + right);

    // Assert
    softly.assertThat(result.unwrapErr()).isEqualTo("error");
  }

  @Test
  void contains_withOkNonMatching_expectedFalse() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.contains("other");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void contains_withErr_expectedFalse() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.contains("value");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void err_withErr_expectedOptionalPresent() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.err();

    // Assert
    softly.assertThat(result).isPresent();
    softly.assertThat(result.get()).isEqualTo("error");
  }

  @Test
  void err_withOk_expectedOptionalEmpty() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.err();

    // Assert
    softly.assertThat(result).isEmpty();
  }

  @Test
  void unwrapErrOr_withErr_expectedError() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var result = sut.unwrapErrOr("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("error");
  }

  @Test
  void unwrapErrOr_withOk_expectedFallback() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.unwrapErrOr("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void unwrapErrOrElse_withErr_expectedError() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>err("error");

    // Act
    final var result =
        sut.unwrapErrOrElse(
            () -> {
              called.set(true);
              return "fallback";
            });

    // Assert
    softly.assertThat(result).isEqualTo("error");
    softly.assertThat(called).isFalse();
  }

  @Test
  void unwrapErrOrElse_withOk_expectedFallback() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result =
        sut.unwrapErrOrElse(
            () -> {
              called.set(true);
              return "fallback";
            });

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
    softly.assertThat(called).isTrue();
  }

  @Test
  void partition_withMixed_expectedPartitionedLists() {
    // Arrange
    final var first = Result.<String, String>ok("a");
    final var second = Result.<String, String>err("b");

    // Act
    final var result = Result.partition(List.of(first, second));

    // Assert
    softly.assertThat(result.oks()).containsExactly("a");
    softly.assertThat(result.errs()).containsExactly("b");
  }

  @Test
  void sequence_withAllOk_expectedOkList() {
    // Arrange
    final var first = Result.<String, String>ok("a");
    final var second = Result.<String, String>ok("b");

    // Act
    final var result = Result.sequence(List.of(first, second));

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).containsExactly("a", "b");
  }

  @Test
  void sequence_withErr_expectedErr() {
    // Arrange
    final var first = Result.<String, String>ok("a");
    final var second = Result.<String, String>err("b");

    // Act
    final var result = Result.sequence(List.of(first, second));

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).isEqualTo("b");
  }

  @Test
  void traverse_withAllOk_expectedOkList() {
    // Arrange
    final var values = List.of("a", "b");

    // Act
    final var result = Result.traverse(values, value -> Result.ok(value + "!"));

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).containsExactly("a!", "b!");
  }

  @Test
  void traverse_withErr_expectedErr() {
    // Arrange
    final var values = List.of("b");

    // Act
    final var result = Result.traverse(values, value -> Result.err("error"));

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).isEqualTo("error");
  }

  @Test
  void okEquality_expectedTrue() {
    // Arrange
    final var left = Result.<String, String>ok("value");
    final var right = Result.<String, String>ok("value");

    // Act
    final var result = left.equals(right);

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void errEquality_expectedTrue() {
    // Arrange
    final var left = Result.<String, String>err("error");
    final var right = Result.<String, String>err("error");

    // Act
    final var result = left.equals(right);

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void okHashCode_expectedMatch() {
    // Arrange
    final var left = Result.<String, String>ok("value");
    final var right = Result.<String, String>ok("value");

    // Act
    final var result = left.hashCode() == right.hashCode();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void errHashCode_expectedMatch() {
    // Arrange
    final var left = Result.<String, String>err("error");
    final var right = Result.<String, String>err("error");

    // Act
    final var result = left.hashCode() == right.hashCode();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void err_withNull_expectedException() {
    // Arrange
    // Act
    final var action = (ThrowingCallable) () -> Result.err(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class).hasMessage("error");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void map_withNullMapper_expectedException() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var action = (ThrowingCallable) () -> sut.map(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class).hasMessage("mapper");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void mapErr_withNullMapper_expectedException() {
    // Arrange
    final var sut = Result.err("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.mapErr(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class).hasMessage("mapper");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void recoverWith_withNullMapper_expectedException() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.recoverWith(null);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("mapper");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void toTry_withNullMapper_expectedException() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.toTry(null);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("mapper");
  }

  @Test
  void toTry_withNullThrowable_expectedException() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.toTry(error -> null);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("throwable");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void tap_withNullAction_expectedException() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var action = (ThrowingCallable) () -> sut.tap(null);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("action");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void tapErr_withNullAction_expectedException() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.tapErr(null);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("action");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void zip_withNullOther_expectedException() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var action = (ThrowingCallable) () -> sut.zip(null, (left, right) -> left);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("other");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void zip_withNullCombiner_expectedException() {
    // Arrange
    final var sut = Result.<String, String>ok("value");
    final var other = Result.<Integer, String>ok(2);

    // Act
    final var action = (ThrowingCallable) () -> sut.zip(other, null);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("combiner");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void or_withNullOther_expectedException() {
    // Arrange
    final var sut = Result.<String, String>err("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.or(null);

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("fallback");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void flatMap_withNullMapper_expectedException() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var action = (ThrowingCallable) () -> sut.flatMap(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class).hasMessage("mapper");
  }

  @Test
  void flatMap_withNullMapped_expectedException() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var action = (ThrowingCallable) () -> sut.flatMap(value -> null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class).hasMessage("mapped");
  }
}
