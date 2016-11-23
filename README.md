# Sql Tracker Usage

* ***Add dependency***
```
<dependency>
    <groupId>ua.ardas</groupId>
    <artifactId>db-test-utils</artifactId>
    <version>1.0</version>
    <scope>test</scope>
</dependency>
```

* ***Add into application-test.properties***
```
spring.jpa.properties.hibernate.session_factory.statement_inspector=ua.ardas.db.sqltracker.StatementInspectorImpl
```

* ***Reset before each test***
```
    @Before
    public void setUp() throws Exception {
        AssertSqlCount.reset();
    }
```

* ***Check queries count***
```
    @Test
    public void testCount() {
        repository.selectUsersWithSubscriptions();        
        AssertSqlCount.assertSelectCount(1);
    }
```
