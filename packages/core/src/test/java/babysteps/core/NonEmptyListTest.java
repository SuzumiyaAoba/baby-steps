package babysteps.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class NonEmptyListTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void of_withSingle_expectedHeadTailSize() {
    // Arrange
    // Act
    final var sut = NonEmptyList.of("value");

    // Assert
    softly.assertThat(sut.head()).isEqualTo("value");
    softly.assertThat(sut.tail()).isEmpty();
    softly.assertThat(sut.size()).isEqualTo(1);
  }

  @Test
  void of_withTail_expectedOrder() {
    // Arrange
    // Act
    final var sut = NonEmptyList.of("a", "b", "c");

    // Assert
    softly.assertThat(sut.toList()).containsExactly("a", "b", "c");
  }

  @Test
  void fromList_withEmpty_expectedNone() {
    // Arrange
    final var values = List.<String>of();

    // Act
    final var result = NonEmptyList.fromList(values);

    // Assert
    softly.assertThat(result.isPresent()).isFalse();
  }

  @Test
  void fromList_withValues_expectedSomeAndDefensiveCopy() {
    // Arrange
    final var values = new ArrayList<String>();
    values.add("a");

    // Act
    final var result = NonEmptyList.fromList(values);
    values.add("b");

    // Assert
    softly.assertThat(result.isPresent()).isTrue();
    softly.assertThat(result.get().toList()).containsExactly("a");
  }

  @Test
  void fromIterable_withEmpty_expectedNone() {
    // Arrange
    final var values = List.<String>of();

    // Act
    final var result = NonEmptyList.fromIterable(values);

    // Assert
    softly.assertThat(result.isPresent()).isFalse();
  }

  @Test
  void fromIterable_withValues_expectedSome() {
    // Arrange
    final var values = List.of("a", "b");

    // Act
    final var result = NonEmptyList.fromIterable(values);

    // Assert
    softly.assertThat(result.isPresent()).isTrue();
    softly.assertThat(result.get().toList()).containsExactly("a", "b");
  }

  @Test
  void head_withNull_expectedNull() {
    // Arrange
    // Act
    final var sut = NonEmptyList.of(null);

    // Assert
    softly.assertThat(sut.head()).isNull();
  }

  @Test
  void append_withValue_expectedAppendedList() {
    // Arrange
    final var sut = NonEmptyList.of("a");

    // Act
    final var result = sut.append("b");

    // Assert
    softly.assertThat(result.toList()).containsExactly("a", "b");
  }

  @Test
  void prepend_withValue_expectedPrependedList() {
    // Arrange
    final var sut = NonEmptyList.of("a");

    // Act
    final var result = sut.prepend("z");

    // Assert
    softly.assertThat(result.toList()).containsExactly("z", "a");
  }

  @Test
  void concat_withOther_expectedConcatenatedList() {
    // Arrange
    final var sut = NonEmptyList.of("a");
    final var other = NonEmptyList.of("b", "c");

    // Act
    final var result = sut.concat(other);

    // Assert
    softly.assertThat(result.toList()).containsExactly("a", "b", "c");
  }

  @Test
  void map_withMapper_expectedMappedList() {
    // Arrange
    final var sut = NonEmptyList.of("a", "bb");

    // Act
    final var result = sut.map(String::length);

    // Assert
    softly.assertThat(result.toList()).containsExactly(1, 2);
  }

  @Test
  void flatMap_withMapper_expectedFlattenedList() {
    // Arrange
    final var sut = NonEmptyList.of("a", "bb");

    // Act
    final var result = sut.flatMap(value -> NonEmptyList.of(value, value));

    // Assert
    softly.assertThat(result.toList()).containsExactly("a", "a", "bb", "bb");
  }

  @Test
  void fold_withValues_expectedAccumulatedValue() {
    // Arrange
    final var sut = NonEmptyList.of(1, 2, 3);

    // Act
    final var result = sut.fold(0, Integer::sum);

    // Assert
    softly.assertThat(result).isEqualTo(6);
  }

  @Test
  void tail_withNullValues_expectedUnmodifiableList() {
    // Arrange
    final var sut = NonEmptyList.of("a", null, "c");

    // Act
    final var result = sut.tail();

    // Assert
    softly.assertThat(result).containsExactly(null, "c");
  }

  @Test
  void toList_expectedUnmodifiable() {
    // Arrange
    final var sut = NonEmptyList.of("a");

    // Act
    final ThrowingCallable action = () -> sut.toList().add("b");

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void fromList_withNullableValues_expectedSome() {
    // Arrange
    final var values = Arrays.asList("a", null, "c");

    // Act
    final var result = NonEmptyList.fromList(values);

    // Assert
    softly.assertThat(result.isPresent()).isTrue();
    softly.assertThat(result.get().toList()).containsExactly("a", null, "c");
  }
}
