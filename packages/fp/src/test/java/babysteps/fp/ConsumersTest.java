package babysteps.fp;

import java.util.concurrent.atomic.AtomicReference;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class ConsumersTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void tee_expectedInvokeBothConsumers() {
    // Arrange
    final var left = new AtomicReference<String>();
    final var right = new AtomicReference<String>();
    final var sut =
        Consumers.tee(value -> left.set("left:" + value), value -> right.set("right:" + value));

    // Act
    sut.accept("value");

    // Assert
    softly.assertThat(left.get()).isEqualTo("left:value");
    softly.assertThat(right.get()).isEqualTo("right:value");
  }

  @Test
  void compose_expectedInvokeBothConsumers() {
    // Arrange
    final var left = new AtomicReference<String>();
    final var right = new AtomicReference<String>();
    final var sut =
        Consumers.compose(value -> left.set("left:" + value), value -> right.set("right:" + value));

    // Act
    sut.accept("value");

    // Assert
    softly.assertThat(left.get()).isEqualTo("left:value");
    softly.assertThat(right.get()).isEqualTo("right:value");
  }

  @Test
  void teeAll_expectedInvokeAllConsumers() {
    // Arrange
    final var left = new AtomicReference<String>();
    final var right = new AtomicReference<String>();
    final var middle = new AtomicReference<String>();
    final var sut =
        Consumers.teeAll(
            value -> left.set("left:" + value),
            value -> right.set("right:" + value),
            value -> middle.set("middle:" + value));

    // Act
    sut.accept("value");

    // Assert
    softly.assertThat(left.get()).isEqualTo("left:value");
    softly.assertThat(right.get()).isEqualTo("right:value");
    softly.assertThat(middle.get()).isEqualTo("middle:value");
  }

  @Test
  void noop_expectedDoNothing() {
    // Arrange
    final var sut = Consumers.<String>noop();

    // Act
    sut.accept("value");

    // Assert
    softly.assertThat(sut).isNotNull();
  }

  @Test
  void tap_expectedReturnOriginalValue() {
    // Arrange
    final var seen = new AtomicReference<String>();
    final var sut = Consumers.tap(seen::set);

    // Act
    final var result = sut.apply("value");

    // Assert
    softly.assertThat(result).isEqualTo("value");
    softly.assertThat(seen.get()).isEqualTo("value");
  }
}
