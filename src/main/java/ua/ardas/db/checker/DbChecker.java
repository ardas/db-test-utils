package ua.ardas.db.checker;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static ua.ardas.esputnik.test.utils.AssertJReflection.assertReflectionEquals;
import static ua.ardas.esputnik.test.utils.ReflectionComparisonMode.IGNORE_DEFAULTS;

@Component
@Slf4j
public class DbChecker {
    @Autowired
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public DbChecker() {
    }

    @PostConstruct
    private void postConstruct() {
        this.createJdbcTemplate(this.dataSource);
    }

    protected void createJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.setQueryTimeout(120);
    }

    public JdbcTemplate getJdbcTemplate() {
        if (this.jdbcTemplate == null) {
            this.postConstruct();
        }

        return this.jdbcTemplate;
    }

    public void checkDb(final ExpectedData expected, final String query, final Object... params) {
        await().atMost(10, SECONDS).untilAsserted(() -> {
            DbChecker.this.checkDbByQuery(expected, query, params);
        });
    }

    public void checkDbByQuery(ExpectedData expected, String query, Object... params) {
        List<List<String>> actual = this.executeQuery(query, params);

        try {
            assertReflectionEquals(expected.getData(), actual, IGNORE_DEFAULTS);
        } catch (AssertionError var9) {
            StringBuilder sb = new StringBuilder("ExpectedData must be:\nnew ExpectedData()\n");
            Iterator<List<String>> var7 = actual.iterator();

            while (var7.hasNext()) {
                List<String> list = var7.next();
                sb.append(String.format(".addRow(\"%s\")\n", Joiner.on("\", \"").join(list)));
            }

            log.error(sb.toString().replaceAll("\"" + ExpectedData.NULL + "\"", "null"));
            throw var9;
        }
    }

    private List<List<String>> executeQuery(String query, Object... params) {
        RowMapper<List<String>> rowMapper = new RowMapper<List<String>>() {
            public List<String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                ResultSetMetaData metadata = rs.getMetaData();
                int columnCount = metadata.getColumnCount();
                List<String> res = Lists.newArrayList();

                for (int i = 1; i <= columnCount; ++i) {
                    String val = rs.getString(i);
                    res.add(null == val ? ExpectedData.NULL : val);
                }

                return res;
            }
        };
        return this.getJdbcTemplate().query(query, params, rowMapper);
    }

    public SqlRowSet queryForRowSet(String query, Object... params) {
        return this.getJdbcTemplate().queryForRowSet(query, params);
    }
}
