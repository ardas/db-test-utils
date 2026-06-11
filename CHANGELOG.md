# Changelog
All notable changes to this project will be documented in this file.

The format is based on Keep a Changelog, and this project adheres to Semantic Versioning.

## [Unreleased]
### Added
### Changed
### Deprecated
### Removed
### Fixed
### Security

## [2.2.1] - 2026-06-11
### Changed
- Changed Maven dependency management to import Spring Boot 2.7.18 and use Boot-managed Spring, Lombok, SLF4J, Awaitility, H2, and Mockito versions.

## [2.2.0] - 2026-05-05
### ⚠️ Breaking changes
- Raised the required Java baseline from 8 to 11. Migration: build and run tests that use `db-test-utils` on Java 11 or newer.

### Changed
- Updated published dependencies, including Guava `33.5.0-jre` and `es-test-utils` `2.1.0`.

## [2.1.0] - 2025-12-28
### Changed
- Changed dependencies to align with the Spring Boot 2.7.18 baseline.

## [2.0.0] - 2025-12-28
### ⚠️ Breaking changes
- Removed SqlTracker. Migration: remove any SqlTracker usage.

### Changed
- Changed dependencies to align with the Spring Boot 2.6.15 baseline.

### Deprecated
- Deprecated DbChecker.ExpectedData; use CheckerExpectedData instead.

### Removed
- Removed the Unitils dependency.
