package ua.ardas.db.checker;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSetMetaData;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static ua.ardas.esputnik.test.utils.AssertJReflection.assertReflectionEquals;
import static ua.ardas.esputnik.test.utils.ReflectionComparisonMode.IGNORE_DEFAULTS;

@Component
@Slf4j
public class DbChecker {
    private final DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DbChecker(DataSource dataSource) {
        this.dataSource = dataSource;
        this.createJdbcTemplate(dataSource);
    }

    protected void createJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setQueryTimeout(120);
    }

    public JdbcTemplate getJdbcTemplate() {
        if (this.jdbcTemplate == null) {
            this.createJdbcTemplate(this.dataSource);
        }

        return this.jdbcTemplate;
    }

    public void checkDb(final CheckerExpectedData expected, final String query, final Object... params) {
        try {
            await().atMost(10, SECONDS).untilAsserted(() -> DbChecker.this.checkDbByQuery(expected, query, params));
        } catch (ConditionTimeoutException error) {
            List<List<String>> actual = this.executeQuery(query, params);
            log.error(this.buildExpectedDataMessage(actual));
            throw error;
        }
    }

    public void checkDbByQuery(CheckerExpectedData expected, String query, Object... params) {
        List<List<String>> actual = this.executeQuery(query, params);
        this.assertExpectedMatches(expected, actual);
    }

    private void assertExpectedMatches(CheckerExpectedData expected, List<List<String>> actual) {
        assertReflectionEquals(expected.getData(), actual, IGNORE_DEFAULTS);
    }

    private String buildExpectedDataMessage(List<List<String>> actual) {
        StringBuilder sb = new StringBuilder("ExpectedData must be:\nnew CheckerExpectedData()\n");

        for (List<String> list : actual) {
            sb.append(String.format(".addRow(\"%s\")\n", Joiner.on("\", \"").join(list)));
        }

        return sb.toString().replaceAll("\"" + CheckerExpectedData.NULL + "\"", "null");
    }

    private List<List<String>> executeQuery(String query, Object... params) {
        RowMapper<List<String>> rowMapper = (rs, rowNum) -> {
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();
            List<String> res = Lists.newArrayList();

            for (int i = 1; i <= columnCount; ++i) {
                String val = rs.getString(i);
                    res.add(null == val ? CheckerExpectedData.NULL : val);
            }

            return res;
        };
        return this.getJdbcTemplate().query(query, params, rowMapper);
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public SqlRowSet queryForRowSet(String query, Object... params) {
        return this.getJdbcTemplate().queryForRowSet(query, params);
    }

    /**
     * @deprecated Use {@link CheckerExpectedData} instead.
     */
    @Deprecated
    public static class ExpectedData extends CheckerExpectedData {
    }
}
