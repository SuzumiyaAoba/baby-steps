package babysteps.fp;

import babysteps.core.Option;
import babysteps.core.Try;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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

  @Test
  void chunked_withValues_expectedChunks() {
    // Arrange
    // Act
    final var result = Streams.chunked(Stream.of(1, 2, 3, 4, 5), 2).toList();

    // Assert
    softly.assertThat(result).containsExactly(List.of(1, 2), List.of(3, 4), List.of(5));
  }

  @Test
  void chunked_withInvalidSize_expectedException() {
    // Arrange
    // Act
    final var action = (ThrowingCallable) () -> Streams.chunked(Stream.of(1), 0).toList();

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void chunked_iteratorNextWithoutElements_expectedException() {
    // Arrange
    final var iterator = Streams.chunked(Stream.of(1), 1).iterator();
    iterator.next();

    // Act
    final var action = (ThrowingCallable) iterator::next;

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(java.util.NoSuchElementException.class);
  }

  @Test
  void windowed_withStep_expectedWindows() {
    // Arrange
    // Act
    final var result = Streams.windowed(Stream.of(1, 2, 3, 4, 5), 3, 2).toList();

    // Assert
    softly.assertThat(result).containsExactly(List.of(1, 2, 3), List.of(3, 4, 5));
  }

  @Test
  void windowed_withDefaultStep_expectedWindows() {
    // Arrange
    // Act
    final var result = Streams.windowed(Stream.of(1, 2, 3), 2).toList();

    // Assert
    softly.assertThat(result).containsExactly(List.of(1, 2), List.of(2, 3));
  }

  @Test
  void windowed_withTooLargeSize_expectedEmpty() {
    // Arrange
    // Act
    final var result = Streams.windowed(Stream.of(1, 2), 3).toList();

    // Assert
    softly.assertThat(result).isEmpty();
  }

  @Test
  void windowed_withPartial_expectedPartialWindow() {
    // Arrange
    // Act
    final var result = Streams.windowed(Stream.of(1, 2, 3), 2, 2, true).toList();

    // Assert
    softly.assertThat(result).containsExactly(List.of(1, 2), List.of(3));
  }

  @Test
  void windowed_nextCalledWithoutHasNext_expectedWindow() {
    // Arrange
    final var iterator = Streams.windowed(Stream.of(1, 2, 3), 2).iterator();

    // Act
    final var result = iterator.next();

    // Assert
    softly.assertThat(result).containsExactly(1, 2);
  }

  @Test
  void windowed_withStepGreaterThanSize_expectedSkipUnderlying() {
    // Arrange
    // Act
    final var result = Streams.windowed(Stream.of(1, 2, 3, 4, 5, 6), 2, 3).toList();

    // Assert
    softly.assertThat(result).containsExactly(List.of(1, 2), List.of(4, 5));
  }

  @Test
  void windowed_nextWithoutElements_expectedException() {
    // Arrange
    final var iterator = Streams.windowed(Stream.<Integer>empty(), 2).iterator();

    // Act
    final var action = (ThrowingCallable) iterator::next;

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void windowed_withInvalidSize_expectedException() {
    // Arrange
    // Act
    final var action = (ThrowingCallable) ()
        -> Streams.windowed(Stream.of(1), 0, 1, false).toList();

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void windowed_withInvalidStep_expectedException() {
    // Arrange
    // Act
    final var action = (ThrowingCallable) ()
        -> Streams.windowed(Stream.of(1), 1, 0, false).toList();

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void windowed_iteratorNextWithoutElements_expectedException() {
    // Arrange
    final var iterator = Streams.windowed(Stream.of(1), 2).iterator();

    // Act
    final var action = (ThrowingCallable) iterator::next;

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(java.util.NoSuchElementException.class);
  }

  @Test
  void indexed_expectedIndexPairs() {
    // Arrange
    // Act
    final var result = Streams.indexed(Stream.of("a", "b")).toList();

    // Assert
    softly.assertThat(result).containsExactly(Tuple2.of(0, "a"), Tuple2.of(1, "b"));
  }

  @Test
  void takeWhileInclusive_expectedIncludeFirstFailure() {
    // Arrange
    // Act
    final var result =
        Streams.takeWhileInclusive(Stream.of(1, 2, 3, 4), value -> value < 3).toList();

    // Assert
    softly.assertThat(result).containsExactly(1, 2, 3);
  }

  @Test
  void takeWhileInclusive_withEmptyStream_expectedEmpty() {
    // Arrange
    // Act
    final var result =
        Streams.takeWhileInclusive(Stream.<Integer>empty(), value -> value < 3).toList();

    // Assert
    softly.assertThat(result).isEmpty();
  }

  @Test
  void takeWhileInclusive_iteratorNextWithoutElements_expectedException() {
    // Arrange
    final var iterator = Streams.takeWhileInclusive(Stream.of(1), value -> value < 0).iterator();
    iterator.next();

    // Act
    final var action = (ThrowingCallable) iterator::next;

    // Assert
    softly.assertThatThrownBy(action).isInstanceOf(java.util.NoSuchElementException.class);
  }

  @Test
  void dropWhileInclusive_expectedDropFirstFailure() {
    // Arrange
    // Act
    final var result =
        Streams.dropWhileInclusive(Stream.of(1, 2, 3, 4), value -> value < 3).toList();

    // Assert
    softly.assertThat(result).containsExactly(4);
  }

  @Test
  void dropWhileInclusive_withAllMatching_expectedEmpty() {
    // Arrange
    // Act
    final var result = Streams.dropWhileInclusive(Stream.of(1, 2), value -> value < 3).toList();

    // Assert
    softly.assertThat(result).isEmpty();
  }

  @Test
  void distinctBy_expectedDistinctKeys() {
    // Arrange
    // Act
    final var result = Streams.distinctBy(Stream.of("a", "aa", "b", "bb"), String::length).toList();

    // Assert
    softly.assertThat(result).containsExactly("a", "aa");
  }

  @Test
  void mapCatching_whenFailure_expectedFailure() {
    // Arrange
    final var exception = new IllegalStateException("boom");

    // Act
    final var result =
        Streams.mapCatching(
                Stream.of("ok", "fail"),
                value -> {
                  if ("fail".equals(value)) {
                    throw exception;
                  }
                  return value.length();
                })
            .toList();

    // Assert
    softly.assertThat(result.get(0)).isEqualTo(Try.success(2));
    softly.assertThat(result.get(1)).isEqualTo(Try.failure(exception));
  }

  @Test
  void filterCatching_whenPredicateThrows_expectedFailure() {
    // Arrange
    final var exception = new IllegalArgumentException("boom");

    // Act
    final var result =
        Streams.filterCatching(
                Stream.of("keep", "fail", "drop"),
                value -> {
                  if ("fail".equals(value)) {
                    throw exception;
                  }
                  return "keep".equals(value);
                })
            .toList();

    // Assert
    softly.assertThat(result).containsExactly(Try.success("keep"), Try.failure(exception));
  }
}
