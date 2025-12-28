package ua.ardas.db.checker;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SuppressWarnings({"SqlResolve", "SqlNoDataSourceInspection", "ResultOfMethodCallIgnored"})
class DbCheckerTest {
    private DbChecker dbChecker;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        DataSource dataSource = createDataSource();
        dbChecker = new DbChecker(dataSource);
        jdbcTemplate = dbChecker.getJdbcTemplate();
        jdbcTemplate.execute("create table sample (id int primary key, label varchar(255))");
    }

    @Test
    void check_db_by_query_matches_expected_rows() {
        jdbcTemplate.update("insert into sample (id, label) values (?, ?)", 1, "alpha");
        jdbcTemplate.update("insert into sample (id, label) values (?, ?)", 2, null);

        CheckerExpectedData expected = new CheckerExpectedData()
            .addRow("1", "alpha")
            .addRow("2", CheckerExpectedData.NULL);

        assertThatCode(() ->
            dbChecker.checkDbByQuery(expected, "select id, label from sample order by id")
        ).doesNotThrowAnyException();
    }

    @Test
    void check_db_retries_until_row_exists() throws Exception {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        try {
            executor.schedule(
                () -> jdbcTemplate.update("insert into sample (id, label) values (?, ?)", 1, "late"),
                250,
                TimeUnit.MILLISECONDS
            );

            CheckerExpectedData expected = new CheckerExpectedData().addRow("1", "late");

            assertThatCode(() ->
                dbChecker.checkDb(expected, "select id, label from sample")
            ).doesNotThrowAnyException();
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    @Test
    void query_for_row_set_returns_values() {
        jdbcTemplate.update("insert into sample (id, label) values (?, ?)", 3, "row");

        SqlRowSet rowSet = dbChecker.queryForRowSet(
            "select id, label from sample where id = ?",
            3
        );

        assertThat(rowSet.next()).isTrue();
        assertThat(rowSet.getInt("id")).isEqualTo(3);
        assertThat(rowSet.getString("label")).isEqualTo("row");
        assertThat(rowSet.next()).isFalse();
    }

    private static DataSource createDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        return dataSource;
    }
}
