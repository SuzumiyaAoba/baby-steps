# Unit Testing

## Library

- JUnit5
- AssertJ
- Mockito

### Soft Assertions with AssertJ

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
