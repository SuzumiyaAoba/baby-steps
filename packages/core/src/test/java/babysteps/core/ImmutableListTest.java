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
class ImmutableListTest {
  @InjectSoftAssertions private SoftAssertions softly;

  @Test
  void empty_expectedEmptyList() {
    // Arrange
    // Act
    final var sut = ImmutableList.<String>empty();

    // Assert
    softly.assertThat(sut.isEmpty()).isTrue();
    softly.assertThat(sut.size()).isZero();
    softly.assertThat(sut.toList()).isEmpty();
  }

  @Test
  void of_withEmptyValues_expectedEmpty() {
    // Arrange
    // Act
    final var sut = ImmutableList.of();

    // Assert
    softly.assertThat(sut.isEmpty()).isTrue();
  }

  @Test
  void of_withNullValues_expectedException() {
    // Arrange
    // Act
    final ThrowingCallable action = () -> ImmutableList.of((String[]) null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
  }

  @Test
  void of_withValues_expectedOrder() {
    // Arrange
    // Act
    final var sut = ImmutableList.of("a", "b");

    // Assert
    softly.assertThat(sut.toList()).containsExactly("a", "b");
  }

  @Test
  void fromList_withEmpty_expectedEmpty() {
    // Arrange
    final var values = List.<String>of();

    // Act
    final var sut = ImmutableList.fromList(values);

    // Assert
    softly.assertThat(sut.isEmpty()).isTrue();
  }

  @Test
  void fromList_withNull_expectedException() {
    // Arrange
    // Act
    final ThrowingCallable action = () -> ImmutableList.fromList(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
  }

  @Test
  void fromList_withValues_expectedDefensiveCopy() {
    // Arrange
    final var values = new ArrayList<String>();
    values.add("a");

    // Act
    final var sut = ImmutableList.fromList(values);
    values.add("b");

    // Assert
    softly.assertThat(sut.toList()).containsExactly("a");
  }

  @Test
  void fromIterable_withEmpty_expectedEmpty() {
    // Arrange
    final var values = List.<String>of();

    // Act
    final var sut = ImmutableList.fromIterable(values);

    // Assert
    softly.assertThat(sut.isEmpty()).isTrue();
  }

  @Test
  void fromIterable_withNull_expectedException() {
    // Arrange
    // Act
    final ThrowingCallable action = () -> ImmutableList.fromIterable(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
  }

  @Test
  void fromIterable_withValues_expectedOrder() {
    // Arrange
    final var values = List.of("a", "b");

    // Act
    final var sut = ImmutableList.fromIterable(values);

    // Assert
    softly.assertThat(sut.toList()).containsExactly("a", "b");
  }

  @Test
  void headOption_withEmpty_expectedNone() {
    // Arrange
    final var sut = ImmutableList.<String>empty();

    // Act
    final var result = sut.headOption();

    // Assert
    softly.assertThat(result.isPresent()).isFalse();
  }

  @Test
  void headOption_withValues_expectedSome() {
    // Arrange
    final var sut = ImmutableList.of("a", "b");

    // Act
    final var result = sut.headOption();

    // Assert
    softly.assertThat(result.isPresent()).isTrue();
    softly.assertThat(result.get()).isEqualTo("a");
  }

  @Test
  void tail_withEmpty_expectedEmpty() {
    // Arrange
    final var sut = ImmutableList.<String>empty();

    // Act
    final var result = sut.tail();

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void tail_withSingle_expectedEmpty() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final var result = sut.tail();

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void tail_withValues_expectedTail() {
    // Arrange
    final var sut = ImmutableList.of("a", "b", "c");

    // Act
    final var result = sut.tail();

    // Assert
    softly.assertThat(result.toList()).containsExactly("b", "c");
  }

  @Test
  void toList_expectedUnmodifiable() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final ThrowingCallable action = () -> sut.toList().add("b");

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void toNonEmptyList_withEmpty_expectedNone() {
    // Arrange
    final var sut = ImmutableList.<String>empty();

    // Act
    final var result = sut.toNonEmptyList();

    // Assert
    softly.assertThat(result.isPresent()).isFalse();
  }

  @Test
  void toNonEmptyList_withValues_expectedSome() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final var result = sut.toNonEmptyList();

    // Assert
    softly.assertThat(result.isPresent()).isTrue();
    softly.assertThat(result.get().toList()).containsExactly("a");
  }

  @Test
  void append_withValue_expectedAppended() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final var result = sut.append("b");

    // Assert
    softly.assertThat(result.toList()).containsExactly("a", "b");
  }

  @Test
  void prepend_withValue_expectedPrepended() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final var result = sut.prepend("z");

    // Assert
    softly.assertThat(result.toList()).containsExactly("z", "a");
  }

  @Test
  void concat_withEmpty_expectedSameInstance() {
    // Arrange
    final var sut = ImmutableList.of("a");
    final var other = ImmutableList.<String>empty();

    // Act
    final var result = sut.concat(other);

    // Assert
    softly.assertThat(result).isSameAs(sut);
  }

  @Test
  void concat_withOther_expectedConcatenated() {
    // Arrange
    final var sut = ImmutableList.of("a");
    final var other = ImmutableList.of("b", "c");

    // Act
    final var result = sut.concat(other);

    // Assert
    softly.assertThat(result.toList()).containsExactly("a", "b", "c");
  }

  @Test
  void concat_withNull_expectedException() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final ThrowingCallable action = () -> sut.concat(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
  }

  @Test
  void map_withMapper_expectedMappedList() {
    // Arrange
    final var sut = ImmutableList.of("a", "bb");

    // Act
    final var result = sut.map(String::length);

    // Assert
    softly.assertThat(result.toList()).containsExactly(1, 2);
  }

  @Test
  void map_withEmpty_expectedEmpty() {
    // Arrange
    final var sut = ImmutableList.<String>empty();

    // Act
    final var result = sut.map(String::length);

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void map_withNullMapper_expectedException() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final ThrowingCallable action = () -> sut.map(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
  }

  @Test
  void flatMap_withMapper_expectedFlattenedList() {
    // Arrange
    final var sut = ImmutableList.of("a", "bb");

    // Act
    final var result = sut.flatMap(value -> ImmutableList.of(value, value));

    // Assert
    softly.assertThat(result.toList()).containsExactly("a", "a", "bb", "bb");
  }

  @Test
  void flatMap_withEmpty_expectedEmpty() {
    // Arrange
    final var sut = ImmutableList.<String>empty();

    // Act
    final var result = sut.flatMap(value -> ImmutableList.of(value, value));

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void flatMap_withNullMapper_expectedException() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final ThrowingCallable action = () -> sut.flatMap(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
  }

  @Test
  void flatMap_withNullMapped_expectedException() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final ThrowingCallable action = () -> sut.flatMap(value -> null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
  }

  @Test
  void fold_withValues_expectedAccumulatedValue() {
    // Arrange
    final var sut = ImmutableList.of(1, 2, 3);

    // Act
    final var result = sut.fold(0, Integer::sum);

    // Assert
    softly.assertThat(result).isEqualTo(6);
  }

  @Test
  void fold_withEmpty_expectedInitial() {
    // Arrange
    final var sut = ImmutableList.<Integer>empty();

    // Act
    final var result = sut.fold(10, Integer::sum);

    // Assert
    softly.assertThat(result).isEqualTo(10);
  }

  @Test
  void fold_withNullFolder_expectedException() {
    // Arrange
    final var sut = ImmutableList.of(1);

    // Act
    final ThrowingCallable action = () -> sut.fold(0, null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
  }

  @Test
  void iterator_remove_expectedException() {
    // Arrange
    final var sut = ImmutableList.of("a", "b");
    final var iterator = sut.iterator();
    iterator.next();

    // Act
    final ThrowingCallable action = iterator::remove;

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void fromList_withNullableValues_expectedSome() {
    // Arrange
    final var values = Arrays.asList("a", null, "c");

    // Act
    final var sut = ImmutableList.fromList(values);

    // Assert
    softly.assertThat(sut.toList()).containsExactly("a", null, "c");
  }

  @Test
  void equals_withSameValues_expectedTrueAndHashCodeMatch() {
    // Arrange
    final var left = ImmutableList.of("a", "b");
    final var right = ImmutableList.of("a", "b");

    // Act
    final var result = left.equals(right);

    // Assert
    softly.assertThat(result).isTrue();
    softly.assertThat(left.hashCode()).isEqualTo(right.hashCode());
  }

  @Test
  void equals_withDifferentValues_expectedFalse() {
    // Arrange
    final var left = ImmutableList.of("a");
    final var right = ImmutableList.of("b");

    // Act
    final var result = left.equals(right);

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void equals_withNonList_expectedFalse() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final var result = sut.equals("a");

    // Assert
    softly.assertThat(result).isFalse();
  }

  @Test
  void toString_expectedValue() {
    // Arrange
    final var sut = ImmutableList.of("a", "b");

    // Act
    final var result = sut.toString();

    // Assert
    softly.assertThat(result).isEqualTo("ImmutableList[a, b]");
  }
}
