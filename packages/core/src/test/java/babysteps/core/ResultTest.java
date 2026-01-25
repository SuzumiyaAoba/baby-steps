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
            () ->
                sut.unwrapOrThrow(
                    error -> new IllegalArgumentException("mapped-" + error));

    // Assert
    softly.assertThatThrownBy(action)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("mapped-error");
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
    softly.assertThatThrownBy(action)
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
  void contains_withOk_expectedTrue() {
    // Arrange
    final var sut = Result.<String, String>ok("value");

    // Act
    final var result = sut.contains("value");

    // Assert
    softly.assertThat(result).isTrue();
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
