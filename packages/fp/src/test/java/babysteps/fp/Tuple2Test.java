package babysteps.fp;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class Tuple2Test {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void of_expectedTupleValues() {
    // Arrange
    // Act
    final var result = Tuple2.of("left", "right");

    // Assert
    softly.assertThat(result.first()).isEqualTo("left");
    softly.assertThat(result.second()).isEqualTo("right");
  }

  @Test
  void swap_expectedSwappedTuple() {
    // Arrange
    final var sut = Tuple2.of("left", "right");

    // Act
    final var result = sut.swap();

    // Assert
    softly.assertThat(result.first()).isEqualTo("right");
    softly.assertThat(result.second()).isEqualTo("left");
  }

  @Test
  void mapFirst_expectedMappedValue() {
    // Arrange
    final var sut = Tuple2.of("left", "right");

    // Act
    final var result = sut.mapFirst(String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo(4);
    softly.assertThat(result.second()).isEqualTo("right");
  }

  @Test
  void mapSecond_expectedMappedValue() {
    // Arrange
    final var sut = Tuple2.of("left", "right");

    // Act
    final var result = sut.mapSecond(String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo("left");
    softly.assertThat(result.second()).isEqualTo(5);
  }

  @Test
  void bimap_expectedMappedValues() {
    // Arrange
    final var sut = Tuple2.of("left", "right");

    // Act
    final var result = sut.bimap(String::length, String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo(4);
    softly.assertThat(result.second()).isEqualTo(5);
  }
}
