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
- [x] NonEmptyList (factories/ops/Validated integration, tests)

### fp
- [x] Functions (compose/pipe/curry/partial/memoize/tupled, tests)
- [x] Predicates (and/or/not, tests)
- [x] Consumers (tee/tap, tests)
- [x] Tuple2 (mapFirst/mapSecond/bimap/swap, tests)

## Near-Term Plan (core + fp)
### 1) core: NonEmptyList
- [x] API design
  - [x] Factories: `of(...)`, `fromList(...)`, `fromIterable(...)`
  - [x] Base API: `head`, `tail`, `size`, `iterator`
  - [x] Immutability policy (defensive copy + `toList()` unmodifiable)
- [x] Transformations / operations
  - [x] `append` / `prepend` / `concat`
  - [x] `map` / `flatMap` / `fold`
  - [x] `toList`, `toArray` (if needed)
- [x] Validated integration
  - [x] Convenience API: `Validated.errs(NonEmptyList<E>)` (or bridge via `toList()`)
  - [x] Helper to accumulate multiple `Validated` into `NonEmptyList`
- [x] Tests
  - [x] Factories/ops/transformations/exception paths
  - [x] equals/hashCode/toString behavior

### 2) core: Checked functional interfaces
- [x] New interfaces
  - [x] `CheckedSupplier<T>`
  - [x] `CheckedFunction<T, R>`
  - [x] `CheckedConsumer<T>`
- [x] Result helpers
  - [x] `Result.of(CheckedSupplier, errorMapper)`
  - [x] `Result.from(CheckedFunction, errorMapper)`
  - [x] `Result.fromConsumer(CheckedConsumer, errorMapper)`
- [x] Tests
  - [x] Exceptions are captured into `Result`
  - [x] Happy-path value propagation

### 3) Tuples expansion (fp)
- [x] Add `Tuple3`/`Tuple4`/`Tuple5`
  - [x] `of`, `mapFirst...mapNth`, `bimap`/`trimap`/`quadmap`/`quintmap`
  - [x] Keep `swap` only for 2-element tuples; consider explicit `rotate` for 3
- [x] Tests
  - [x] Mapping functions and construction APIs

## Mid-Term Plan (collections + stream)
### Immutable collections
- [ ] Decide persistence strategy for List/Map/Set (reuse vs custom)
- [ ] Builder API (efficient construction)
- [ ] Interop with core/fp types (Option/Result/Validated)

### Stream helpers
- [x] Safe terminals (`first/last/single`)
- [x] Lazy/chunk helpers (`chunked`, `windowed`, etc.)
- [x] Exception handling with `Try/Result`

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
