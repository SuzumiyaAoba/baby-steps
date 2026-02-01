package babysteps.core;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class CheckedSupplierTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void get_withValue_expectedResult() throws Exception {
    // Arrange
    final CheckedSupplier<String> supplier = () -> "value";

    // Act
    final var result = supplier.get();

    // Assert
    softly.assertThat(result).isEqualTo("value");
  }

  @Test
  void get_withException_expectedException() {
    // Arrange
    final CheckedSupplier<String> supplier = () -> {
      throw new Exception("boom");
    };

    // Act
    final ThrowingCallable action = supplier::get;

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(Exception.class).hasMessage("boom");
  }
}
