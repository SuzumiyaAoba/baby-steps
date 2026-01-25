## Coding Style

- Prefer a functional style in application code, for example by using `Stream` and `Optional`.
- Prefer an imperative style in library code when it offers a significant performance advantage.
- Use `var` for local variable declarations within methods.
- Declare variables as `final` whenever possible.
- Use JSpecify’s `@Nullable` for any value that may be `null`.
- Use JSpecify’s `@NonNull` for any value that must not be `null`.
- Use `@Nullable`/`@NonNull` annotations for return types and type parameters.
- Do not reassign variables.
