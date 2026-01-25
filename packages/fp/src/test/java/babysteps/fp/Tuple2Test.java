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
}
