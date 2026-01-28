# Coding Style

- Prefer a functional style in application code, for example by using `Stream` and `Optional`.
- Prefer an imperative style in library code when it offers a significant performance advantage.
- Use `var` for local variable declarations within methods.
- Declare variables as `final` whenever possible.
- Use JSpecify’s `@Nullable` for any value that may be `null`.
- Use JSpecify’s `@NonNull` for any value that must not be `null`.
- Use `@Nullable`/`@NonNull` annotations for return types and type parameters.
    ```java
    // Do not annotate the type parameters `A`, `B`, or `C` themselves.
    // Instead, annotate them at the point of use.
    public static <A, B, C> @NonNull Function<@Nullable A, @Nullable C> compose(
        @NonNull Function<? super @Nullable B, ? extends @Nullable C> after,
        @NonNull Function<? super @Nullable A, ? extends @Nullable B> before) {
      // ...
    }
    ```
- Do not reassign variables.
- Write detailed Javadoc.
