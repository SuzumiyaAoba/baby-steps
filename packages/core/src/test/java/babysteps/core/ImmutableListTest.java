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
  void lastOption_withEmpty_expectedNone() {
    // Arrange
    final var sut = ImmutableList.<String>empty();

    // Act
    final var result = sut.lastOption();

    // Assert
    softly.assertThat(result.isPresent()).isFalse();
  }

  @Test
  void lastOption_withValues_expectedSome() {
    // Arrange
    final var sut = ImmutableList.of("a", "b");

    // Act
    final var result = sut.lastOption();

    // Assert
    softly.assertThat(result.isPresent()).isTrue();
    softly.assertThat(result.get()).isEqualTo("b");
  }

  @Test
  void getOption_withValidIndex_expectedSome() {
    // Arrange
    final var sut = ImmutableList.of("a", "b");

    // Act
    final var result = sut.getOption(1);

    // Assert
    softly.assertThat(result.isPresent()).isTrue();
    softly.assertThat(result.get()).isEqualTo("b");
  }

  @Test
  void getOption_withInvalidIndex_expectedNone() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final var result = sut.getOption(2);

    // Assert
    softly.assertThat(result.isPresent()).isFalse();
  }

  @Test
  void getOrElse_withInvalidIndex_expectedFallback() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final var result = sut.getOrElse(4, "fallback");

    // Assert
    softly.assertThat(result).isEqualTo("fallback");
  }

  @Test
  void getOrElse_withValidIndex_expectedValue() {
    // Arrange
    final var sut = ImmutableList.of("a", "b");

    // Act
    final var result = sut.getOrElse(1, "fallback");

    // Assert
    softly.assertThat(result).isEqualTo("b");
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
  void contains_withValue_expectedTrue() {
    // Arrange
    final var sut = ImmutableList.of("a", "b");

    // Act
    final var result = sut.contains("b");

    // Assert
    softly.assertThat(result).isTrue();
  }

  @Test
  void indexOf_withMissingValue_expectedMinusOne() {
    // Arrange
    final var sut = ImmutableList.of("a", "b");

    // Act
    final var result = sut.indexOf("c");

    // Assert
    softly.assertThat(result).isEqualTo(-1);
  }

  @Test
  void lastIndexOf_withValue_expectedLastIndex() {
    // Arrange
    final var sut = ImmutableList.of("a", "b", "a");

    // Act
    final var result = sut.lastIndexOf("a");

    // Assert
    softly.assertThat(result).isEqualTo(2);
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
  void stream_expectedCollectedValues() {
    // Arrange
    final var sut = ImmutableList.of("a", "b");

    // Act
    final var result = sut.stream().toList();

    // Assert
    softly.assertThat(result).containsExactly("a", "b");
  }

  @Test
  void toArray_expectedObjectArray() {
    // Arrange
    final var sut = ImmutableList.of("a", "b");

    // Act
    final var result = sut.toArray();

    // Assert
    softly.assertThat(result).containsExactly("a", "b");
  }

  @Test
  void toArray_withGenerator_expectedTypedArray() {
    // Arrange
    final var sut = ImmutableList.of("a", "b");

    // Act
    final var result = sut.toArray(String[]::new);

    // Assert
    softly.assertThat(result).containsExactly("a", "b");
  }

  @Test
  void toArray_withNullGenerator_expectedException() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final ThrowingCallable action = () -> sut.toArray(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
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
  void filter_withMatches_expectedFilteredList() {
    // Arrange
    final var sut = ImmutableList.of("a", "bb", "ccc");

    // Act
    final var result = sut.filter(value -> value.length() >= 2);

    // Assert
    softly.assertThat(result.toList()).containsExactly("bb", "ccc");
  }

  @Test
  void filter_withNoMatches_expectedEmpty() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final var result = sut.filter(value -> value.length() > 10);

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void filter_withNullPredicate_expectedException() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final ThrowingCallable action = () -> sut.filter(null);

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
  void take_withCount_expectedPrefix() {
    // Arrange
    final var sut = ImmutableList.of("a", "b", "c");

    // Act
    final var result = sut.take(2);

    // Assert
    softly.assertThat(result.toList()).containsExactly("a", "b");
  }

  @Test
  void take_withZero_expectedEmpty() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final var result = sut.take(0);

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void drop_withCount_expectedSuffix() {
    // Arrange
    final var sut = ImmutableList.of("a", "b", "c");

    // Act
    final var result = sut.drop(2);

    // Assert
    softly.assertThat(result.toList()).containsExactly("c");
  }

  @Test
  void drop_withLargeCount_expectedEmpty() {
    // Arrange
    final var sut = ImmutableList.of("a", "b");

    // Act
    final var result = sut.drop(10);

    // Assert
    softly.assertThat(result.isEmpty()).isTrue();
  }

  @Test
  void takeWhile_withPredicate_expectedPrefix() {
    // Arrange
    final var sut = ImmutableList.of("a", "bb", "ccc");

    // Act
    final var result = sut.takeWhile(value -> value.length() <= 2);

    // Assert
    softly.assertThat(result.toList()).containsExactly("a", "bb");
  }

  @Test
  void takeWhile_withNullPredicate_expectedException() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final ThrowingCallable action = () -> sut.takeWhile(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
  }

  @Test
  void dropWhile_withPredicate_expectedSuffix() {
    // Arrange
    final var sut = ImmutableList.of("a", "bb", "ccc");

    // Act
    final var result = sut.dropWhile(value -> value.length() <= 2);

    // Assert
    softly.assertThat(result.toList()).containsExactly("ccc");
  }

  @Test
  void dropWhile_withNullPredicate_expectedException() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final ThrowingCallable action = () -> sut.dropWhile(null);

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NullPointerException.class);
  }

  @Test
  void distinct_withDuplicates_expectedDistinctList() {
    // Arrange
    final var sut = ImmutableList.of("a", "b", "a");

    // Act
    final var result = sut.distinct();

    // Assert
    softly.assertThat(result.toList()).containsExactly("a", "b");
  }

  @Test
  void reverse_withValues_expectedReversedList() {
    // Arrange
    final var sut = ImmutableList.of("a", "b", "c");

    // Act
    final var result = sut.reverse();

    // Assert
    softly.assertThat(result.toList()).containsExactly("c", "b", "a");
  }

  @Test
  void sorted_withComparator_expectedSortedList() {
    // Arrange
    final var sut = ImmutableList.of("bb", "a", "ccc");

    // Act
    final var result = sut.sorted((left, right) -> left.length() - right.length());

    // Assert
    softly.assertThat(result.toList()).containsExactly("a", "bb", "ccc");
  }

  @Test
  void sorted_withNullComparator_expectedException() {
    // Arrange
    final var sut = ImmutableList.of("a");

    // Act
    final ThrowingCallable action = () -> sut.sorted(null);

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
