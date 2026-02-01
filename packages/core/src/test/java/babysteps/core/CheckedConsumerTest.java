package babysteps.core;

import java.util.concurrent.atomic.AtomicReference;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class CheckedConsumerTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void accept_withValue_expectedSideEffect() throws Exception {
    // Arrange
    final var seen = new AtomicReference<String>();
    final CheckedConsumer<String> consumer = seen::set;

    // Act
    consumer.accept("value");

    // Assert
    softly.assertThat(seen.get()).isEqualTo("value");
  }

  @Test
  void accept_withException_expectedException() {
    // Arrange
    final CheckedConsumer<String> consumer = value -> {
      throw new Exception("boom");
    };

    // Act
    final ThrowingCallable action = () -> consumer.accept("value");

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(Exception.class).hasMessage("boom");
  }
}
