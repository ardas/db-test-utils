# db-test-utils

Utilities for asserting relational database state in integration tests. The library provides a Spring component, `DbChecker`, plus a small expected-data builder for comparing query results with retry support.

## Purpose
- Assert database rows in tests using compact expected-data declarations.
- Retry assertions while asynchronous writes settle.
- Reuse the same JDBC query helper across Spring-based test suites.

## Problems it solves
- Removes repeated `JdbcTemplate` query and row-mapping boilerplate from tests.
- Produces copyable expected-data snippets when actual database state does not match.
- Keeps database assertions independent from a specific test framework assertion style.

## How to use

Maven dependency:
```xml
<dependency>
  <groupId>ua.ardas</groupId>
  <artifactId>db-test-utils</artifactId>
  <version>${db-test-utils.version}</version>
  <scope>test</scope>
</dependency>
```

Spring wiring:
```java
@Autowired
private DbChecker dbChecker;
```

Example assertion:
```java
import ua.ardas.db.checker.CheckerExpectedData;
import ua.ardas.db.checker.DbChecker;

class ContactsIT {
  private DbChecker dbChecker;

  void checkContacts() {
    dbChecker.checkDb(
        new CheckerExpectedData()
            .addRow("1", "test@example.com")
            .addRow("2", CheckerExpectedData.NULL),
        "select ContactId, Email from dbo.Contacts where ContactId in (?, ?)",
        1,
        2
    );
  }
}
```

Expected data can be created either with `new CheckerExpectedData()` or the deprecated compatibility type `new DbChecker.ExpectedData()`.

## Configuration
`DbChecker` is a Spring `@Component` and requires a `DataSource` bean. Include `ua.ardas.db.checker` in component scanning, or construct `DbChecker` manually with a `DataSource` in non-Spring tests.

`checkDb` retries for up to 10 seconds and uses the configured `JdbcTemplate` query timeout of 120 seconds.

## Key types
- `DbChecker` executes SQL queries and compares results with expected rows.
- `CheckerExpectedData` builds ordered expected rows and provides the `NULL` marker for SQL `NULL` values.
- `DbChecker.ExpectedData` is a deprecated compatibility alias for `CheckerExpectedData`.

## Compatibility
- `2.2.x`: Spring Boot 2.7.18-aligned branch.
- `2.5.x`: Spring Boot 3.5.x / Java 21-aligned branch.
