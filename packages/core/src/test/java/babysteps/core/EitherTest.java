package babysteps.core;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class EitherTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void isRight_withRight_expectedTrue() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.isRight();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void isRight_withLeft_expectedFalse() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.isRight();

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void isLeft_withRight_expectedFalse() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.isLeft();

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void isLeft_withLeft_expectedTrue() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.isLeft();

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void unwrapRight_withRight_expectedValue() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.unwrapRight();

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void unwrapLeft_withLeft_expectedValue() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.unwrapLeft();

    // Assert
    softly.assertThat(result).isEqualTo("error");
  }

  @Test
  void unwrapRight_withLeft_expectedException() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var action = (ThrowingCallable) sut::unwrapRight;

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Either is Left");
  }

  @Test
  void unwrapLeft_withRight_expectedException() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var action = (ThrowingCallable) sut::unwrapLeft;

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Either is Right");
  }

  @Test
  void left_withNull_expectedValue() {
    // Arrange
    final var sut = Either.<String, String>left(null);

    // Act
    final var result = sut.unwrapLeft();

    // Assert
    softly.assertThat(result).isNull();
  }

  @Test
  void right_withNull_expectedValue() {
    // Arrange
    final var sut = Either.<String, String>right(null);

    // Act
    final var result = sut.unwrapRight();

    // Assert
    softly.assertThat(result).isNull();
  }

  @Test
  void fromResult_withOk_expectedRight() {
    // Arrange
    final var result = Result.ok("value");

    // Act
    final var sut = Either.fromResult(result);

    // Assert
    softly.assertThat(sut.unwrapRight()).isEqualTo("value");
  }

  @Test
  void fromResult_withErr_expectedLeft() {
    // Arrange
    final var result = Result.<String, String>err("error");

    // Act
    final var sut = Either.fromResult(result);

    // Assert
    softly.assertThat(sut.unwrapLeft()).isEqualTo("error");
  }

  @Test
  void unwrapRightOr_withLeft_expectedFallback() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.unwrapRightOr("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void unwrapRightOr_withRight_expectedValue() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.unwrapRightOr("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }
  @Test
  void unwrapRightOrElse_withRight_expectedValueAndSupplierNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Either.<String, String>right("value");

    // Act
    final var result =
        sut.unwrapRightOrElse(
            () -> {
              called.set(true);
              return "fallback";
            });

    // Assert
    softly.assertThat(result).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void unwrapRightOrElse_withLeft_expectedFallback() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.unwrapRightOrElse(() -> "fallback");

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void unwrapLeftOr_withRight_expectedFallback() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.unwrapLeftOr("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void unwrapLeftOr_withLeft_expectedValue() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.unwrapLeftOr("fallback");

    // Assert
    softly.assertThat(result).isEqualTo("error");
  }

  @Test
  void unwrapLeftOrElse_withLeft_expectedValueAndSupplierNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Either.<String, String>left("error");

    // Act
    final var result =
        sut.unwrapLeftOrElse(
            () -> {
              called.set(true);
              return "fallback";
            });

    // Assert
    softly.assertThat(result).isEqualTo("error");
    softly.assertThat(called).isFalse();
  }

  @Test
  void unwrapLeftOrElse_withRight_expectedFallback() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.unwrapLeftOrElse(() -> "fallback");

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void orElse_withRight_expectedSameEither() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.orElse(Either.left("fallback"));

    // Assert
    softly.assertThat(result.unwrapRight()).isEqualTo("value");
  }

  @Test
  void orElse_withLeft_expectedFallback() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.orElse(Either.right("fallback"));

    // Assert
    softly.assertThat(result.unwrapRight()).isEqualTo("fallback");
  }

  @Test
  void orElse_withNullFallback_expectedException() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.orElse(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class).hasMessage("fallback");
  }

  @Test
  void orElseGet_withRight_expectedValueAndSupplierNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Either.<String, String>right("value");

    // Act
    final var result =
        sut.orElseGet(
            () -> {
              called.set(true);
              return Either.right("fallback");
            });

    // Assert
    softly.assertThat(result.unwrapRight()).isEqualTo("value");
    softly.assertThat(called).isFalse();
  }

  @Test
  void orElseGet_withLeft_expectedFallback() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.orElseGet(() -> Either.right("fallback"));

    // Assert
    softly.assertThat(result.unwrapRight()).isEqualTo("fallback");
  }

  @Test
  void orElseGet_withLeftNullResult_expectedException() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.orElseGet(() -> null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class).hasMessage("result");
  }

  @Test
  void orElseThrow_withRight_expectedValue() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.orElseThrow(IllegalStateException::new);

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void orElseThrow_withLeft_expectedException() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.orElseThrow(IllegalStateException::new);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void orElseThrow_withNullException_expectedNpe() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var action = (ThrowingCallable) () -> sut.orElseThrow(() -> null);

    // Assert
    softly
        .assertThatThrownBy(action)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("exception");
  }

  @Test
  void fromResult_withNull_expectedException() {
    // Arrange
    final var action = (ThrowingCallable) () -> Either.fromResult(null);

    // Act
    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class).hasMessage("result");
  }

  @Test
  void map_withRight_expectedMappedRight() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.map(value -> value + "!");

    // Assert
    softly.assertThat(result.unwrapRight()).isEqualTo("value!");
  }

  @Test
  void map_withLeft_expectedSameLeft() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.map(value -> value + "!");

    // Assert
    softly.assertThat(result.unwrapLeft()).isEqualTo("error");
  }

  @Test
  void mapBoth_withRight_expectedMappedRight() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.mapBoth(value -> value + "?", value -> value + "!");

    // Assert
    softly.assertThat(result.unwrapRight()).isEqualTo("value!");
  }

  @Test
  void mapBoth_withLeft_expectedMappedLeft() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.mapBoth(value -> value + "?", value -> value + "!");

    // Assert
    softly.assertThat(result.unwrapLeft()).isEqualTo("error?");
  }

  @Test
  void flatMap_withRight_expectedMappedRight() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.flatMap(value -> Either.right(value + "!"));

    // Assert
    softly.assertThat(result.unwrapRight()).isEqualTo("value!");
  }

  @Test
  void flatMap_withLeft_expectedSameLeft() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.flatMap(value -> Either.right(value + "!"));

    // Assert
    softly.assertThat(result.unwrapLeft()).isEqualTo("error");
  }

  @Test
  void mapLeft_withLeft_expectedMappedLeft() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.mapLeft(value -> value + "!");

    // Assert
    softly.assertThat(result.unwrapLeft()).isEqualTo("error!");
  }

  @Test
  void mapLeft_withRight_expectedSameRight() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.mapLeft(value -> value + "!");

    // Assert
    softly.assertThat(result.unwrapRight()).isEqualTo("value");
  }

  @Test
  void flatMapLeft_withLeft_expectedMappedLeft() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.flatMapLeft(value -> Either.left(value + "!"));

    // Assert
    softly.assertThat(result.unwrapLeft()).isEqualTo("error!");
  }

  @Test
  void flatMapLeft_withRight_expectedSameRight() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.flatMapLeft(value -> Either.left(value + "!"));

    // Assert
    softly.assertThat(result.unwrapRight()).isEqualTo("value");
  }

  @Test
  void mapRight_withRight_expectedMappedRight() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.mapRight(value -> value + "!");

    // Assert
    softly.assertThat(result.unwrapRight()).isEqualTo("value!");
  }

  @Test
  void peek_withRight_expectedActionCalled() {
    // Arrange
    final var captured = new AtomicReference<String>();
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.peek(captured::set);

    // Assert
    softly.assertThat(captured.get()).isEqualTo("value");
    softly.assertThat(result).isSameAs(sut);
  }

  @Test
  void peek_withLeft_expectedActionNotCalled() {
    // Arrange
    final var called = new AtomicBoolean(false);
    final var sut = Either.<String, String>left("error");

    // Act
    sut.peek(value -> called.set(true));

    // Assert
    softly.assertThat(called).isFalse();
  }

  @Test
  void peekLeft_withLeft_expectedActionCalled() {
    // Arrange
    final var captured = new AtomicReference<String>();
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.peekLeft(captured::set);

    // Assert
    softly.assertThat(captured.get()).isEqualTo("error");
    softly.assertThat(result).isSameAs(sut);
  }

  @Test
  void peekRight_withRight_expectedActionCalled() {
    // Arrange
    final var captured = new AtomicReference<String>();
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.peekRight(captured::set);

    // Assert
    softly.assertThat(captured.get()).isEqualTo("value");
    softly.assertThat(result).isSameAs(sut);
  }

  @Test
  void tapBoth_withLeft_expectedLeftHandler() {
    // Arrange
    final var leftCaptured = new AtomicReference<String>();
    final var rightCalled = new AtomicBoolean(false);
    final var sut = Either.<String, String>left("error");

    // Act
    final var result =
        sut.tapBoth(
            leftCaptured::set,
            value -> rightCalled.set(true));

    // Assert
    softly.assertThat(leftCaptured.get()).isEqualTo("error");
    softly.assertThat(rightCalled).isFalse();
    softly.assertThat(result).isSameAs(sut);
  }

  @Test
  void tapBoth_withRight_expectedRightHandler() {
    // Arrange
    final var rightCaptured = new AtomicReference<String>();
    final var leftCalled = new AtomicBoolean(false);
    final var sut = Either.<String, String>right("value");

    // Act
    final var result =
        sut.tapBoth(
            value -> leftCalled.set(true),
            rightCaptured::set);

    // Assert
    softly.assertThat(rightCaptured.get()).isEqualTo("value");
    softly.assertThat(leftCalled).isFalse();
    softly.assertThat(result).isSameAs(sut);
  }

  @Test
  void fold_withLeft_expectedLeftHandler() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.fold(value -> value + "!", value -> value + "?");

    // Assert
    softly.assertThat(result).isEqualTo("error!");
  }

  @Test
  void fold_withRight_expectedRightHandler() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.fold(value -> value + "!", value -> value + "?");

    // Assert
    softly.assertThat(result).isEqualTo("value?");
  }

  @Test
  void swap_withLeft_expectedRight() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.swap();

    // Assert
    softly.assertThat(result.unwrapRight()).isEqualTo("error");
  }

  @Test
  void swap_withRight_expectedLeft() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.swap();

    // Assert
    softly.assertThat(result.unwrapLeft()).isEqualTo("value");
  }

  @Test
  void swap_withRightNull_expectedLeftNull() {
    // Arrange
    final var sut = Either.<String, String>right(null);

    // Act
    final var result = sut.swap();

    // Assert
    softly.assertThat(result.unwrapLeft()).isNull();
  }

  @Test
  void left_withLeft_expectedOptional() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.left();

    // Assert
    softly.assertThat(result).isEqualTo(Optional.of("error"));
  }

  @Test
  void left_withRight_expectedEmpty() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.left();

    // Assert
    softly.assertThat(result).isEqualTo(Optional.empty());
  }

  @Test
  void right_withRight_expectedOptional() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.right();

    // Assert
    softly.assertThat(result).isEqualTo(Optional.of("value"));
  }

  @Test
  void right_withRightNull_expectedEmpty() {
    // Arrange
    final var sut = Either.<String, String>right(null);

    // Act
    final var result = sut.right();

    // Assert
    softly.assertThat(result).isEqualTo(Optional.empty());
  }

  @Test
  void toOption_withRight_expectedSome() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.toOption();

    // Assert
    softly.assertThat(result).isEqualTo(Option.some("value"));
  }

  @Test
  void toOption_withRightNull_expectedNone() {
    // Arrange
    final var sut = Either.<String, String>right(null);

    // Act
    final var result = sut.toOption();

    // Assert
    softly.assertThat(result).isEqualTo(Option.none());
  }

  @Test
  void toOption_withLeft_expectedNone() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.toOption();

    // Assert
    softly.assertThat(result).isEqualTo(Option.none());
  }

  @Test
  void toResult_withRight_expectedOk() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.toResult();

    // Assert
    softly.assertThat(result.isOk()).isTrue();
    softly.assertThat(result.unwrap()).isEqualTo("value");
  }

  @Test
  void toResult_withLeft_expectedErr() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.toResult();

    // Assert
    softly.assertThat(result.isErr()).isTrue();
    softly.assertThat(result.unwrapErr()).isEqualTo("error");
  }

  @Test
  void toTry_withRight_expectedSuccess() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.toTry(IllegalStateException::new);

    // Assert
    softly.assertThat(result.isSuccess()).isTrue();
    softly.assertThat(result.get()).isEqualTo("value");
  }

  @Test
  void toTry_withLeft_expectedFailure() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.toTry(IllegalStateException::new);

    // Assert
    softly.assertThat(result.isFailure()).isTrue();
    softly.assertThat(result.getCause()).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void contains_withMatchingRight_expectedTrue() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.contains("value");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void contains_withLeft_expectedFalse() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.contains("value");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void containsLeft_withMatchingLeft_expectedTrue() {
    // Arrange
    final var sut = Either.<String, String>left("error");

    // Act
    final var result = sut.containsLeft("error");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void containsLeft_withRight_expectedFalse() {
    // Arrange
    final var sut = Either.<String, String>right("value");

    // Act
    final var result = sut.containsLeft("error");

    // Assert
    softly.assertThat(result).isFalse();
  }
}
