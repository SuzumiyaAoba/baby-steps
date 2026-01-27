package babysteps.core;

import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class ValidatedTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void isOk_withOk_expectedTrue() {
    // Arrange
    final var sut = Validated.ok("value");

    // Act
    final var result = sut.isOk();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void isErr_withOk_expectedFalse() {
    // Arrange
    final var sut = Validated.ok("value");

    // Act
    final var result = sut.isErr();

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void isOk_withErr_expectedFalse() {
    // Arrange
    final var sut = Validated.<String, String>err("error");

    // Act
    final var result = sut.isOk();

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void isErr_withErr_expectedTrue() {
    // Arrange
    final var sut = Validated.<String, String>err("error");

    // Act
    final var result = sut.isErr();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void unwrap_withOk_expectedValue() {
    // Arrange
    final var sut = Validated.ok("value");

    // Act
    final var result = sut.unwrap();

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void unwrap_withErr_expectedException() {
    // Arrange
    final var sut = Validated.<String, String>err("error");

    // Act
    final var action = (ThrowingCallable) sut::unwrap;

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Validated is Err");
  }

  @Test
  void unwrapErrs_withErr_expectedErrors() {
    // Arrange
    final var sut = Validated.<String, String>err("error");

    // Act
    final var result = sut.unwrapErrs();

    // Assert
    softly.assertThat(result).containsExactly("error");
  }

  @Test
  void unwrapErrs_withOk_expectedException() {
    // Arrange
    final var sut = Validated.ok("value");

    // Act
    final var action = (ThrowingCallable) sut::unwrapErrs;

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Validated is Ok");
  }

  @Test
  void map_withOk_expectedMappedOk() {
    // Arrange
    final var sut = Validated.ok("value");

    // Act
    final var result = sut.map(value -> value + "!");

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("value!");
  }

  @Test
  void map_withErr_expectedSameErrors() {
    // Arrange
    final var sut = Validated.<String, String>err("error");

    // Act
    final var result = sut.map(value -> value + "!");

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErrs()).containsExactly("error");
  }

  @Test
  void mapErr_withErr_expectedMappedErrors() {
    // Arrange
    final var sut = Validated.<String, String>errs(List.of("error1", "error2"));

    // Act
    final var result = sut.mapErr(error -> error + "!");

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErrs()).containsExactly("error1!", "error2!");
  }

  @Test
  void mapErr_withOk_expectedOk() {
    // Arrange
    final var sut = Validated.ok("value");

    // Act
    final var result = sut.mapErr(error -> error + "!");

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("value");
  }

  @Test
  void fold_withOk_expectedOkResult() {
    // Arrange
    final var sut = Validated.ok("value");

    // Act
    final var result = sut.fold(errors -> "errors", value -> value + "!");

    // Assert
    softly.assertThat(result).isEqualTo("value!");
  }

  @Test
  void fold_withErr_expectedErrResult() {
    // Arrange
    final var sut = Validated.<String, String>errs(List.of("error1", "error2"));

    // Act
    final var result = sut.fold(errors -> String.join(",", errors), value -> value + "!");

    // Assert
    softly.assertThat(result).isEqualTo("error1,error2");
  }

  @Test
  void toResult_withOk_expectedOk() {
    // Arrange
    final var sut = Validated.ok("value");

    // Act
    final var result = sut.toResult();

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("value");
  }

  @Test
  void toResult_withErr_expectedErrList() {
    // Arrange
    final var sut = Validated.<String, String>errs(List.of("error"));

    // Act
    final var result = sut.toResult();

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).containsExactly("error");
  }

  @Test
  void toOption_withOk_expectedSome() {
    // Arrange
    final var sut = Validated.ok("value");

    // Act
    final var result = sut.toOption();

    // Assert
    softly.assertThat(result.isPresent()).isTrue();
    softly.assertThat(result.get()).isEqualTo("value");
  }

  @Test
  void toOption_withErr_expectedNone() {
    // Arrange
    final var sut = Validated.<String, String>err("error");

    // Act
    final var result = sut.toOption();

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void zip_withBothOk_expectedCombined() {
    // Arrange
    final var left = Validated.<String, String>ok("left");
    final var right = Validated.<String, String>ok("right");

    // Act
    final var result = left.zip(right, (l, r) -> l + ":" + r);

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("left:right");
  }

  @Test
  void zip_withErrAndOk_expectedErr() {
    // Arrange
    final var left = Validated.<String, String>err("error");
    final var right = Validated.<String, String>ok("right");

    // Act
    final var result = left.zip(right, (l, r) -> l + ":" + r);

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErrs()).containsExactly("error");
  }

  @Test
  void zip_withOkAndErr_expectedErr() {
    // Arrange
    final var left = Validated.<String, String>ok("left");
    final var right = Validated.<String, String>err("error");

    // Act
    final var result = left.zip(right, (l, r) -> l + ":" + r);

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErrs()).containsExactly("error");
  }

  @Test
  void zip_withBothErr_expectedAccumulatedErrors() {
    // Arrange
    final var left = Validated.<String, String>errs(List.of("error1"));
    final var right = Validated.<String, String>errs(List.of("error2"));

    // Act
    final var result = left.zip(right, (l, r) -> l + ":" + r);

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErrs()).containsExactly("error1", "error2");
  }

  @Test
  void combine_withOk_expectedCombined() {
    // Arrange
    final var left = Validated.<String, String>ok("left");
    final var right = Validated.<String, String>ok("right");

    // Act
    final var result = left.combine(right, (l, r) -> l + ":" + r);

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("left:right");
  }

  @Test
  void mapErrs_withErr_expectedMappedErrors() {
    // Arrange
    final var sut = Validated.<String, String>errs(List.of("error1", "error2"));

    // Act
    final var result = sut.mapErrs(errors -> List.of(errors.get(1), errors.get(0)));

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErrs()).containsExactly("error2", "error1");
  }

  @Test
  void mapErrs_withOk_expectedOk() {
    // Arrange
    final var sut = Validated.ok("value");

    // Act
    final var result = sut.mapErrs(errors -> List.of("mapped"));

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("value");
  }

  @Test
  void partition_withMixed_expectedSplit() {
    // Arrange
    final var first = Validated.<String, String>ok("value1");
    final var second = Validated.<String, String>errs(List.of("error1", "error2"));
    final var third = Validated.<String, String>ok("value2");

    // Act
    final var result = Validated.partition(List.of(first, second, third));

    // Assert
    softly.assertThat(result.oks()).containsExactly("value1", "value2");
    softly.assertThat(result.errs()).containsExactly("error1", "error2");
  }

  @Test
  void sequence_withAllOk_expectedOkList() {
    // Arrange
    final var validations =
        List.of(Validated.<String, String>ok("value1"), Validated.<String, String>ok("value2"));

    // Act
    final var result = Validated.sequence(validations);

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).containsExactly("value1", "value2");
  }

  @Test
  void sequence_withErrs_expectedAccumulatedErrors() {
    // Arrange
    final var validations =
        List.of(
            Validated.<String, String>errs(List.of("error1", "error2")),
            Validated.<String, String>ok("value"),
            Validated.<String, String>err("error3"));

    // Act
    final var result = Validated.sequence(validations);

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErrs()).containsExactly("error1", "error2", "error3");
  }

  @Test
  void traverse_withAllOk_expectedOkList() {
    // Arrange
    final var values = List.of("a", "b");

    // Act
    final var result = Validated.traverse(values, value -> Validated.ok(value + "!"));

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).containsExactly("a!", "b!");
  }

  @Test
  void traverse_withErrs_expectedAccumulatedErrors() {
    // Arrange
    final var values = List.of("a", "b", "c");
    final var validations =
        List.of(
            Validated.<String, String>ok("value"),
            Validated.<String, String>err("error1"),
            Validated.<String, String>err("error2"));
    final var index = new java.util.concurrent.atomic.AtomicInteger();

    // Act
    final var result =
        Validated.traverse(values, value -> validations.get(index.getAndIncrement()));

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErrs()).containsExactly("error1", "error2");
  }

  @Test
  void combineAll_withOkValues_expectedCombined() {
    // Arrange
    final var validations =
        List.of(
            Validated.<String, String>ok("a"),
            Validated.<String, String>ok("b"),
            Validated.<String, String>ok("c"));

    // Act
    final var result = Validated.combineAll(validations, (left, right) -> left + right);

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("abc");
  }

  @Test
  void combineAll_withErrs_expectedAccumulatedErrors() {
    // Arrange
    final var validations =
        List.of(
            Validated.<String, String>ok("a"),
            Validated.<String, String>errs(List.of("error1", "error2")),
            Validated.<String, String>err("error3"));

    // Act
    final var result = Validated.combineAll(validations, (left, right) -> left + right);

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErrs()).containsExactly("error1", "error2", "error3");
  }

  @Test
  void okEquality_expectedTrue() {
    // Arrange
    final var left = Validated.<String, String>ok("value");
    final var right = Validated.<String, String>ok("value");

    // Act
    final var result = left.equals(right);

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void errEquality_expectedTrue() {
    // Arrange
    final var left = Validated.<String, String>err("error");
    final var right = Validated.<String, String>err("error");

    // Act
    final var result = left.equals(right);

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void okHashCode_expectedMatch() {
    // Arrange
    final var left = Validated.<String, String>ok("value");
    final var right = Validated.<String, String>ok("value");

    // Act
    final var result = left.hashCode() == right.hashCode();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void errHashCode_expectedMatch() {
    // Arrange
    final var left = Validated.<String, String>err("error");
    final var right = Validated.<String, String>err("error");

    // Act
    final var result = left.hashCode() == right.hashCode();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void map_withNullMapper_expectedException() {
    // Arrange
    final var sut = Validated.ok("value");

    // Act
    final var action = (ThrowingCallable) () -> sut.map(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class).hasMessage("mapper");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void mapErr_withNullMapper_expectedException() {
    // Arrange
    final var sut = Validated.<String, String>err("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.mapErr(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class).hasMessage("mapper");
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void mapErrs_withNullMapper_expectedException() {
    // Arrange
    final var sut = Validated.<String, String>err("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.mapErrs(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class).hasMessage("mapper");
  }
}
