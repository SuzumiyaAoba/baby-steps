package babysteps.core;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class UnitTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void instance_expectedSameInstance() {
    // Arrange
    // Act
    final var first = Unit.instance();
    final var second = Unit.instance();

    // Assert
    softly.assertThat(first).isSameAs(second);
  }

  @Test
  void toString_expectedUnit() {
    // Arrange
    final var sut = Unit.instance();

    // Act
    final var result = sut.toString();

    // Assert
    softly.assertThat(result).isEqualTo("Unit");
  }

  @Test
  void equals_expectedTrueForUnitInstances() {
    // Arrange
    final var first = Unit.instance();
    final var second = Unit.instance();

    // Act
    final var result = first.equals(second);

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void equals_withNonUnit_expectedFalse() {
    // Arrange
    final var sut = Unit.instance();

    // Act
    final var result = sut.equals("unit");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void hashCode_expectedZero() {
    // Arrange
    final var sut = Unit.instance();

    // Act
    final var result = sut.hashCode();

    // Assert
    softly.assertThat(result).isEqualTo(0);
  }
}
