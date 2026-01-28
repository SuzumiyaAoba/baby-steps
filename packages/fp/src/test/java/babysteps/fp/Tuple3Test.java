package babysteps.fp;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class Tuple3Test {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void of_expectedTupleValues() {
    // Arrange
    // Act
    final var result = Tuple3.of("first", "second", "third");

    // Assert
    softly.assertThat(result.first()).isEqualTo("first");
    softly.assertThat(result.second()).isEqualTo("second");
    softly.assertThat(result.third()).isEqualTo("third");
  }

  @Test
  void mapFirst_expectedMappedValue() {
    // Arrange
    final var sut = Tuple3.of("first", "second", "third");

    // Act
    final var result = sut.mapFirst(String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo(5);
    softly.assertThat(result.second()).isEqualTo("second");
    softly.assertThat(result.third()).isEqualTo("third");
  }

  @Test
  void mapSecond_expectedMappedValue() {
    // Arrange
    final var sut = Tuple3.of("first", "second", "third");

    // Act
    final var result = sut.mapSecond(String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo("first");
    softly.assertThat(result.second()).isEqualTo(6);
    softly.assertThat(result.third()).isEqualTo("third");
  }

  @Test
  void mapThird_expectedMappedValue() {
    // Arrange
    final var sut = Tuple3.of("first", "second", "third");

    // Act
    final var result = sut.mapThird(String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo("first");
    softly.assertThat(result.second()).isEqualTo("second");
    softly.assertThat(result.third()).isEqualTo(5);
  }

  @Test
  void bimap_expectedMappedValues() {
    // Arrange
    final var sut = Tuple3.of("first", "second", "third");

    // Act
    final var result = sut.bimap(String::length, String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo(5);
    softly.assertThat(result.second()).isEqualTo(6);
    softly.assertThat(result.third()).isEqualTo("third");
  }

  @Test
  void trimap_expectedMappedValues() {
    // Arrange
    final var sut = Tuple3.of("first", "second", "third");

    // Act
    final var result = sut.trimap(String::length, String::length, String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo(5);
    softly.assertThat(result.second()).isEqualTo(6);
    softly.assertThat(result.third()).isEqualTo(5);
  }
}
