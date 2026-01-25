# Unit Testing

## Library

- JUnit5
- AssertJ
- JaCoCo
- Mockito

## AAA (Arrange-Act-Assert) pattern

Write tests using the AAA pattern.

```java
public void methodName_condition_expectedResult() {
  // Arrange
  ...

  // Act
  ...

  // Assert
  ...
}
```

## Soft Assertions with AssertJ

Use `SoftAssetions` to avoid assertion roulette.

```java
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;

@ExtendWith(SoftAssertionsExtension.class)
public class SomeClass {

  @InjectSoftAssertions
  private SoftAssertions softly;
}
```

## Naming Convertions

### Method

```java
public void methodName_condition_expectedResult() {
  // ...
}
```

### System Under Test

```java
public void methodName_condition_expectedResult() {
  // ...

  // Act
  final var sut = ...;

  // ...
}
```

## Avoid control-flow statements

Test methods MUST NOT contain control-flow statements such as if, while, for, or switch.

## Avoid multiple Act tasks

Test methods MUST NOT contain multiple Act tasks.
