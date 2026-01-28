package babysteps.fp;

import babysteps.core.Option;
import java.util.stream.Stream;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class StreamsTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void first_withEmptyStream_expectedNone() {
    // Arrange
    // Act
    final var result = Streams.first(Stream.empty());

    // Assert
    softly.assertThat(result).isEqualTo(Option.none());
  }

  @Test
  void first_withValues_expectedFirst() {
    // Arrange
    // Act
    final var result = Streams.first(Stream.of("first", "second"));

    // Assert
    softly.assertThat(result).isEqualTo(Option.some("first"));
  }

  @Test
  void first_withNullValue_expectedSome() {
    // Arrange
    // Act
    final var result = Streams.first(Stream.of((String) null));

    // Assert
    softly.assertThat(result.isPresent()).isTrue();
    softly.assertThat(result.get()).isNull();
  }

  @Test
  void last_withEmptyStream_expectedNone() {
    // Arrange
    // Act
    final var result = Streams.last(Stream.empty());

    // Assert
    softly.assertThat(result).isEqualTo(Option.none());
  }

  @Test
  void last_withValues_expectedLast() {
    // Arrange
    // Act
    final var result = Streams.last(Stream.of("first", "second", "third"));

    // Assert
    softly.assertThat(result).isEqualTo(Option.some("third"));
  }

  @Test
  void single_withEmptyStream_expectedNone() {
    // Arrange
    // Act
    final var result = Streams.single(Stream.empty());

    // Assert
    softly.assertThat(result).isEqualTo(Option.none());
  }

  @Test
  void single_withSingleValue_expectedSome() {
    // Arrange
    // Act
    final var result = Streams.single(Stream.of("only"));

    // Assert
    softly.assertThat(result).isEqualTo(Option.some("only"));
  }

  @Test
  void single_withMultipleValues_expectedNone() {
    // Arrange
    // Act
    final var result = Streams.single(Stream.of("first", "second"));

    // Assert
    softly.assertThat(result).isEqualTo(Option.none());
  }
}
