package babysteps.core;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class OptionTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void isPresent_withSome_expectedTrue() {
    // Arrange
    final var sut = Option.some("value");

    // Act
    final var result = sut.isPresent();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void isPresent_withNone_expectedFalse() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.isPresent();

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void isEmpty_withSome_expectedFalse() {
    // Arrange
    final var sut = Option.some("value");

    // Act
    final var result = sut.isEmpty();

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void isEmpty_withNone_expectedTrue() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.isEmpty();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void get_withSome_expectedValue() {
    // Arrange
    final var sut = Option.some("value");

    // Act
    final var result = sut.get();

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void get_withNone_expectedException() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var action = (ThrowingCallable) sut::get;

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void contains_withMatchingValue_expectedTrue() {
    // Arrange
    final var sut = Option.some("value");

    // Act
    final var result = sut.contains("value");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void contains_withNone_expectedFalse() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.contains("value");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void ofNullable_withValue_expectedSome() {
    // Arrange
    final var value = "name";

    // Act
    final var result = Option.ofNullable(value);

    // Assert
    softly.assertThat(result).isInstanceOf(Option.Some.class);
  }

  @Test
  void ofNullable_withNull_expectedNone() {
    // Act
    final var result = Option.ofNullable(null);

    // Assert
    softly.assertThat(result).isInstanceOf(Option.None.class);
  }

  @Test
  void fromOptional_withValue_expectedSome() {
    // Arrange
    final var value = Optional.of("opt");

    // Act
    final var result = Option.fromOptional(value);

    // Assert
    softly.assertThat(result.get()).isEqualTo("opt");
  }

  @Test
  void fromOptional_withEmpty_expectedNone() {
    // Arrange
    final var value = Optional.empty();

    // Act
    final var result = Option.fromOptional(value);

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void map_withSome_expectedMappedValue() {
    // Arrange
    final var sut = Option.some(" 10 ");

    // Act
    final var result = sut.map(String::trim);

    // Assert
    softly.assertThat(result.get()).isEqualTo("10");
  }

  @Test
  void map_withNone_expectedNone() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.map(String::trim);

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void filter_withPassingPredicate_expectedSome() {
    // Arrange
    final var sut = Option.some(10);

    // Act
    final var result = sut.filter(value -> value > 5);

    // Assert
    softly.assertThat(result.get()).isEqualTo(10);
  }

  @Test
  void filter_withNone_expectedNone() {
    // Arrange
    final var sut = Option.<Integer>none();

    // Act
    final var result = sut.filter(value -> value > 5);

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void filter_withFailingPredicate_expectedNone() {
    // Arrange
    final var sut = Option.some(3);

    // Act
    final var result = sut.filter(value -> value > 5);

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void filterNot_withMatchingPredicate_expectedNone() {
    // Arrange
    final var sut = Option.some(10);

    // Act
    final var result = sut.filterNot(value -> value > 5);

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void flatMap_withSome_expectedFlattenedValue() {
    // Arrange
    final var sut = Option.some(10);

    // Act
    final var result = sut.flatMap(value -> Option.some(value + 1));

    // Assert
    softly.assertThat(result.get()).isEqualTo(11);
  }

  @Test
  void flatMap_withNone_expectedNone() {
    // Arrange
    final var sut = Option.<Integer>none();

    // Act
    final var result = sut.flatMap(value -> Option.some(value + 1));

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void getOrElse_withNone_expectedFallback() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.getOrElse("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void getOrElse_withSome_expectedValue() {
    // Arrange
    final var sut = Option.some("value");

    // Act
    final var result = sut.getOrElse("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void getOrElseGet_withNone_expectedSupplierValue() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.getOrElseGet(() -> "fallback");

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void getOrElseGet_withSome_expectedValueAndSupplierNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Option.some("value");

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
  void orElse_withNone_expectedFallback() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.orElse(Option.some("fallback"));

    // Assert
    softly.assertThat(result.get()).isEqualTo("fallback");
  }

  @Test
  void orElse_withSome_expectedOriginal() {
    // Arrange
    final var sut = Option.some("value");

    // Act
    final var result = sut.orElse(Option.some("fallback"));

    // Assert
    softly.assertThat(result.get()).isEqualTo("value");
  }

  @Test
  void orElseGet_withNone_expectedFallback() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.orElseGet(() -> Option.some("fallback"));

    // Assert
    softly.assertThat(result.get()).isEqualTo("fallback");
  }

  @Test
  void orElseGet_withSome_expectedOriginalAndSupplierNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Option.some("value");

    // Act
    final var result =
        sut.orElseGet(
            () -> {
              called.set(true);
              return Option.some("fallback");
            });

    // Assert
    softly.assertThat(result.get()).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void fold_withNone_expectedEmptyValue() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.fold(() -> "empty", value -> "value:" + value);

    // Assert
    softly.assertThat(result).isEqualTo("empty");
  }

  @Test
  void fold_withSome_expectedMappedValue() {
    // Arrange
    final var sut = Option.some("a");

    // Act
    final var result = sut.fold(() -> "empty", value -> "value:" + value);

    // Assert
    softly.assertThat(result).isEqualTo("value:a");
  }

  @Test
  void orElseThrow_withNone_expectedException() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var action =
        (ThrowingCallable) () -> sut.orElseThrow(() -> new IllegalStateException("missing"));

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("missing");
  }

  @Test
  void orElseThrow_withSome_expectedValue() {
    // Arrange
    final var sut = Option.some("value");

    // Act
    final var result = sut.orElseThrow(() -> new IllegalStateException("missing"));

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void peek_withSome_expectedConsumerInvocation() {
    // Arrange
    final var seen = new AtomicBoolean(false);
    final var sut = Option.some("a");

    // Act
    final var result = sut.peek(value -> seen.set(true));

    // Assert
    softly.assertThat(result.get()).isEqualTo("a");
    softly.assertThat(seen).isTrue();
  }

  @Test
  void peek_withNone_expectedNoInvocation() {
    // Arrange
    final var seen = new AtomicBoolean(false);
    final var sut = Option.<String>none();

    // Act
    final var result = sut.peek(value -> seen.set(true));

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
    softly.assertThat(seen).isFalse();
  }

  @Test
  void ifPresent_withSome_expectedConsumerInvocation() {
    // Arrange
    final var seen = new AtomicBoolean(false);
    final var sut = Option.some("a");

    // Act
    sut.ifPresent(value -> seen.set(true));

    // Assert
    softly.assertThat(seen).isTrue();
  }

  @Test
  void ifPresent_withNone_expectedNoInvocation() {
    // Arrange
    final var seen = new AtomicBoolean(false);
    final var sut = Option.<String>none();

    // Act
    sut.ifPresent(value -> seen.set(true));

    // Assert
    softly.assertThat(seen).isFalse();
  }

  @Test
  void ifPresentOrElse_withNone_expectedElseInvocation() {
    // Arrange
    final var seen = new AtomicBoolean(false);
    final var sut = Option.<String>none();

    // Act
    sut.ifPresentOrElse(value -> seen.set(false), () -> seen.set(true));

    // Assert
    softly.assertThat(seen).isTrue();
  }

  @Test
  void ifPresentOrElse_withSome_expectedPresentInvocation() {
    // Arrange
    final var seen = new AtomicBoolean(false);
    final var sut = Option.some("value");

    // Act
    sut.ifPresentOrElse(value -> seen.set(true), () -> seen.set(false));

    // Assert
    softly.assertThat(seen).isTrue();
  }

  @Test
  void exists_withSome_expectedTrue() {
    // Arrange
    final var sut = Option.some(5);

    // Act
    final var result = sut.exists(value -> value > 3);

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void exists_withSomeFailingPredicate_expectedFalse() {
    // Arrange
    final var sut = Option.some(5);

    // Act
    final var result = sut.exists(value -> value > 10);

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void exists_withNone_expectedFalse() {
    // Arrange
    final var sut = Option.<Integer>none();

    // Act
    final var result = sut.exists(value -> value > 3);

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void forAll_withNone_expectedTrue() {
    // Arrange
    final var sut = Option.<Integer>none();

    // Act
    final var result = sut.forAll(value -> value > 3);

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void forAll_withSome_expectedTrue() {
    // Arrange
    final var sut = Option.some(5);

    // Act
    final var result = sut.forAll(value -> value > 3);

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void forAll_withSomeFailingPredicate_expectedFalse() {
    // Arrange
    final var sut = Option.some(2);

    // Act
    final var result = sut.forAll(value -> value > 3);

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void toOptional_withSome_expectedOptional() {
    // Arrange
    final var sut = Option.some("x");

    // Act
    final var result = sut.toOptional();

    // Assert
    softly.assertThat(result).isEqualTo(Optional.of("x"));
  }

  @Test
  void toOptional_withNone_expectedEmpty() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.toOptional();

    // Assert
    softly.assertThat(result).isEqualTo(Optional.empty());
  }

  @Test
  void toResult_withSome_expectedOk() {
    // Arrange
    final var sut = Option.some("x");

    // Act
    final var result = sut.toResult(() -> "err");

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("x");
  }

  @Test
  void toResult_withNone_expectedErr() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.toResult(() -> "err");

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).isEqualTo("err");
  }

  @Test
  void stream_withSome_expectedValue() {
    // Arrange
    final var sut = Option.some("x");

    // Act
    final var result = sut.stream();

    // Assert
    softly.assertThat(result).containsExactly("x");
  }

  @Test
  void stream_withNone_expectedEmpty() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.stream();

    // Assert
    softly.assertThat(result).isEmpty();
  }

  @Test
  void contains_withNonMatchingValue_expectedFalse() {
    // Arrange
    final var sut = Option.some("value");

    // Act
    final var result = sut.contains("other");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void toString_withSome_expectedFormat() {
    // Arrange
    final var sut = Option.some("value");

    // Act
    final var result = sut.toString();

    // Assert
    softly.assertThat(result).isEqualTo("Some(value)");
  }

  @Test
  void toString_withNone_expectedValue() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.toString();

    // Assert
    softly.assertThat(result).isEqualTo("None");
  }

  @Test
  void noneEquality_expectedTrue() {
    // Arrange
    final var left = Option.<String>none();
    final var right = Option.<Integer>none();

    // Act
    final var result = left.equals(right);

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void noneHashCode_expectedZero() {
    // Arrange
    final var sut = Option.<String>none();

    // Act
    final var result = sut.hashCode();

    // Assert
    softly.assertThat(result).isEqualTo(0);
  }
}
