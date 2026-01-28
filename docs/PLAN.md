# Implementation Plan (Updated: 2026-01-28)

## Goals
- [x] Modular Java 21 library where users can adopt only what they need
- [x] Practical, ergonomic FP-style APIs
- [x] Fill gaps in the Java standard library (without replacing it)

## Completed
### core
- [x] Option (Some/None, map/flatMap, filter, fold, toOptional, tests)
- [x] Result (Ok/Err, map/mapErr, flatMap, fold, recover, toOption, tests)
- [x] Either (Left/Right, map/flatMap, mapLeft, fold, swap, toOption/toResult, tests)
- [x] Try (exception capture model, Result/Option bridges, tests)
- [x] Unit (void substitute, tests)
- [x] Validated (error accumulation, combine/zip/bridge, tests)

### fp
- [x] Functions (compose/pipe/curry/partial/memoize/tupled, tests)
- [x] Predicates (and/or/not, tests)
- [x] Consumers (tee/tap, tests)
- [x] Tuple2 (mapFirst/mapSecond/bimap/swap, tests)

## Near-Term Plan (core + fp)
### 1) core: NonEmptyList
- [ ] API design
  - [ ] Factories: `of(...)`, `fromList(...)`, `fromIterable(...)`
  - [ ] Base API: `head`, `tail`, `size`, `iterator`
  - [ ] Immutability policy (defensive copy + `toList()` unmodifiable)
- [ ] Transformations / operations
  - [ ] `append` / `prepend` / `concat`
  - [ ] `map` / `flatMap` / `fold`
  - [ ] `toList`, `toArray` (if needed)
- [ ] Validated integration
  - [ ] Convenience API: `Validated.errs(NonEmptyList<E>)` (or bridge via `toList()`)
  - [ ] Helper to accumulate multiple `Validated` into `NonEmptyList`
- [ ] Tests
  - [ ] Factories/ops/transformations/exception paths
  - [ ] equals/hashCode/toString behavior

### 2) fp: Checked functional interfaces
- [ ] New interfaces
  - [ ] `CheckedSupplier<T>`
  - [ ] `CheckedFunction<T, R>`
  - [ ] `CheckedConsumer<T>`
- [ ] Lift/bridge helpers
  - [ ] Lift into `Try` (e.g., `Try.of(checked)`)
  - [ ] Lift into `Result` (e.g., `Result.of(checked, errorMapper)`)
  - [ ] Decide whether to add to `Functions` or introduce a new utility
- [ ] Tests
  - [ ] Exceptions are captured into `Try/Result`
  - [ ] Happy-path value propagation

### 3) Tuples expansion (fp)
- [ ] Add `Tuple3`
  - [ ] `of`, `mapFirst`/`mapSecond`/`mapThird`, `bimap`/`trimap`
  - [ ] Keep `swap` only for 2-element tuples; consider explicit `rotate` for 3
- [ ] Tests
  - [ ] Mapping functions and construction APIs

## Mid-Term Plan (collections + stream)
### Immutable collections
- [ ] Decide persistence strategy for List/Map/Set (reuse vs custom)
- [ ] Builder API (efficient construction)
- [ ] Interop with core/fp types (Option/Result/Validated)

### Stream helpers
- [ ] Safe terminals (`first/last/single`)
- [ ] Lazy/chunk helpers (`chunked`, `windowed`, etc.)
- [ ] Exception handling with `Try/Result`

## Long-Term Plan (io + concurrency + testing)
### IO
- [ ] try-with-resources safety helpers
- [ ] Functional wrappers over `Files/Paths`

### Concurrency
- [ ] `Task/Promise` design
- [ ] `retry/backoff`, `timeout`

### Testing
- [ ] Property-based generators and matchers
- [ ] Standard generators for core/fp types

## Docs / Release
- [ ] Module-specific README (usage + migration notes)
- [ ] Publishing steps under `babysteps` group
- [ ] Versioning policy (SemVer)
