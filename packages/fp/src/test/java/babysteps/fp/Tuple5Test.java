package babysteps.fp;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class Tuple5Test {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void of_expectedTupleValues() {
    // Arrange
    // Act
    final var result = Tuple5.of("first", "second", "third", "fourth", "fifth");

    // Assert
    softly.assertThat(result.first()).isEqualTo("first");
    softly.assertThat(result.second()).isEqualTo("second");
    softly.assertThat(result.third()).isEqualTo("third");
    softly.assertThat(result.fourth()).isEqualTo("fourth");
    softly.assertThat(result.fifth()).isEqualTo("fifth");
    softly.assertThat(result._1()).isEqualTo("first");
    softly.assertThat(result._2()).isEqualTo("second");
    softly.assertThat(result._3()).isEqualTo("third");
    softly.assertThat(result._4()).isEqualTo("fourth");
    softly.assertThat(result._5()).isEqualTo("fifth");
  }

  @Test
  void mapFirst_expectedMappedValue() {
    // Arrange
    final var sut = Tuple5.of("first", "second", "third", "fourth", "fifth");

    // Act
    final var result = sut.mapFirst(String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo(5);
    softly.assertThat(result.second()).isEqualTo("second");
    softly.assertThat(result.third()).isEqualTo("third");
    softly.assertThat(result.fourth()).isEqualTo("fourth");
    softly.assertThat(result.fifth()).isEqualTo("fifth");
  }

  @Test
  void mapSecond_expectedMappedValue() {
    // Arrange
    final var sut = Tuple5.of("first", "second", "third", "fourth", "fifth");

    // Act
    final var result = sut.mapSecond(String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo("first");
    softly.assertThat(result.second()).isEqualTo(6);
    softly.assertThat(result.third()).isEqualTo("third");
    softly.assertThat(result.fourth()).isEqualTo("fourth");
    softly.assertThat(result.fifth()).isEqualTo("fifth");
  }

  @Test
  void mapThird_expectedMappedValue() {
    // Arrange
    final var sut = Tuple5.of("first", "second", "third", "fourth", "fifth");

    // Act
    final var result = sut.mapThird(String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo("first");
    softly.assertThat(result.second()).isEqualTo("second");
    softly.assertThat(result.third()).isEqualTo(5);
    softly.assertThat(result.fourth()).isEqualTo("fourth");
    softly.assertThat(result.fifth()).isEqualTo("fifth");
  }

  @Test
  void mapFourth_expectedMappedValue() {
    // Arrange
    final var sut = Tuple5.of("first", "second", "third", "fourth", "fifth");

    // Act
    final var result = sut.mapFourth(String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo("first");
    softly.assertThat(result.second()).isEqualTo("second");
    softly.assertThat(result.third()).isEqualTo("third");
    softly.assertThat(result.fourth()).isEqualTo(6);
    softly.assertThat(result.fifth()).isEqualTo("fifth");
  }

  @Test
  void mapFifth_expectedMappedValue() {
    // Arrange
    final var sut = Tuple5.of("first", "second", "third", "fourth", "fifth");

    // Act
    final var result = sut.mapFifth(String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo("first");
    softly.assertThat(result.second()).isEqualTo("second");
    softly.assertThat(result.third()).isEqualTo("third");
    softly.assertThat(result.fourth()).isEqualTo("fourth");
    softly.assertThat(result.fifth()).isEqualTo(5);
  }

  @Test
  void bimap_expectedMappedValues() {
    // Arrange
    final var sut = Tuple5.of("first", "second", "third", "fourth", "fifth");

    // Act
    final var result = sut.bimap(String::length, String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo(5);
    softly.assertThat(result.second()).isEqualTo(6);
    softly.assertThat(result.third()).isEqualTo("third");
    softly.assertThat(result.fourth()).isEqualTo("fourth");
    softly.assertThat(result.fifth()).isEqualTo("fifth");
  }

  @Test
  void trimap_expectedMappedValues() {
    // Arrange
    final var sut = Tuple5.of("first", "second", "third", "fourth", "fifth");

    // Act
    final var result = sut.trimap(String::length, String::length, String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo(5);
    softly.assertThat(result.second()).isEqualTo(6);
    softly.assertThat(result.third()).isEqualTo(5);
    softly.assertThat(result.fourth()).isEqualTo("fourth");
    softly.assertThat(result.fifth()).isEqualTo("fifth");
  }

  @Test
  void quintmap_expectedMappedValues() {
    // Arrange
    final var sut = Tuple5.of("first", "second", "third", "fourth", "fifth");

    // Act
    final var result =
        sut.quintmap(String::length, String::length, String::length, String::length, String::length);

    // Assert
    softly.assertThat(result.first()).isEqualTo(5);
    softly.assertThat(result.second()).isEqualTo(6);
    softly.assertThat(result.third()).isEqualTo(5);
    softly.assertThat(result.fourth()).isEqualTo(6);
    softly.assertThat(result.fifth()).isEqualTo(5);
  }
}
