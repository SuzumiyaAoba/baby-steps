package babysteps.core;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class CheckedFunctionTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void apply_withValue_expectedResult() throws Exception {
    // Arrange
    final CheckedFunction<String, Integer> function = String::length;

    // Act
    final var result = function.apply("value");

    // Assert
    softly.assertThat(result).isEqualTo(5);
  }

  @Test
  void apply_withException_expectedException() {
    // Arrange
    final CheckedFunction<String, Integer> function = value -> {
      throw new Exception("boom");
    };

    // Act
    final ThrowingCallable action = () -> function.apply("value");

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(Exception.class).hasMessage("boom");
  }
}
