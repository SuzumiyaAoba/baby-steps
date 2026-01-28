---
name: github
description: Use this to run `gh` command.
---

# `gh` command

## Pull Rquest

- Write pull request's body in English.
- Format the pull request body using the template below..

## Template

```
## Summary
<!-- What changed? (1-3 bullets) -->

## Purpose
<!-- Why are we doing this? -->

## Scope / Non-goals
<!-- In scope / out of scope -->

## Details
<!-- Key implementation details + notable trade-offs -->

## How to Test
<!-- Steps / commands / expected results -->

## Risk & Impact
<!-- Impacted areas, breaking changes, edge cases, known risks -->

## Rollout / Rollback
<!-- Release plan (flags, phased rollout) and how to revert safely -->

## Screenshots / Demo (if UI)
<!-- Before/After -->

## Related
<!-- Links: issues, docs, design, incidents. Use "Fixes #..." when appropriate -->

## Sequence (if program flow changes)
<!-- Sequence diagram -->
```

## Omit unnecessary sections

- Include only the sections that are relevant to this change.
- Delete any unused sections from the PR body (do not leave empty headings).
- If a section is partially relevant, keep it and write “N/A” only for the non-applicable parts.
- As a minimum, include **Summary**, **Purpose**, and **How to Test**.
  - If **How to Test** is not possible, explain why and what evidence you have instead (e.g., unit tests, logs, screenshots).
