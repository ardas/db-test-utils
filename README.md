# Db Checker Usage

* ***Add dependency***
```
<dependency>
    <groupId>ua.ardas</groupId>
    <artifactId>db-test-utils</artifactId>
    <version>${db-test-utils.version}</version>
    <scope>test</scope>
</dependency>
```

* ***Use DbChecker in tests***
```
@Autowired
private DbChecker dbChecker;

@Test
public void check_contacts_row() {
    dbChecker.checkDb(
        DbChecker.expectedData()
            .addRow("1", "test@example.com")
            .addRow("2", DbChecker.ExpectedData.NULL),
        "select ContactId, Email from dbo.Contacts where ContactId in (?, ?)",
        1, 2
    );
}
```
