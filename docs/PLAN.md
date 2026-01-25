# Implementation Plan (Task List)

## Goals
- [ ] Provide a modular Java 21 library where users can adopt only the needed parts.
- [ ] Support functional programming style with practical, ergonomic APIs.
- [ ] Fill gaps in the Java standard library without replacing it wholesale.

## Near-Term Scope (core + fp)
### core
- [ ] Option
  - [x] Some/None, map/flatMap, filter, toOptional
  - [x] fold, orElse, peek, contains, equals/hashCode tests
- [ ] Result
  - [x] Ok/Err, map/mapErr, flatMap, unwrap helpers
  - [x] fold, recover, toOption, contains, equals/hashCode tests
- [ ] Try
  - [x] Capture checked exceptions into Result-like model
- [ ] Unit
  - [x] Singleton type to model void in FP pipelines

### fp
- [ ] Functions
  - [x] compose/pipe/curry
  - [x] tupled/untupled, flip, partial, memoize
- [ ] Predicates
  - [x] and/or/not combinators
- [ ] Consumers
  - [ ] tee/tap for side effects without breaking chains

## Mid-Term Scope (collections + stream)
- [ ] Immutable collections
  - [ ] Persistent List/Map/Set
  - [ ] Builders for efficient creation
- [ ] Stream helpers
  - [ ] Safe terminal ops (first/last/single)
  - [ ] Lazy evaluation helpers and chunking

## Longer-Term Scope (io + concurrency + testing)
- [ ] IO
  - [ ] Resource safety (try-with-resources helper types)
  - [ ] Functional wrappers over Files/Paths
- [ ] Concurrency
  - [ ] Task/Promise, retry/backoff, timeout
- [ ] Testing
  - [ ] Property-based generators and matchers

## Packaging and Release
- [ ] Publish each module independently under group `babysteps`.
- [ ] Align versions across modules; semantic versioning.
- [ ] Provide module-specific README with usage and migration notes.
