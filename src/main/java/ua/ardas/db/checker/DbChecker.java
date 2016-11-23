package ua.ardas.db.checker;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import junit.framework.AssertionFailedError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.sql.ResultSetMetaData;
import java.util.List;

@Service
public class DbChecker {

    @Autowired
    JdbcTemplate template;

    private static final Log LOG = LogFactory.getLog(DbChecker.class);

    public void checkDb(ExpectedData expected, String query, Object... params) {
        List<List<String>> actual = executeQuery(query, params);

        try {
            ReflectionAssert.assertReflectionEquals(expected.getData(), actual, ReflectionComparatorMode.IGNORE_DEFAULTS);
        } catch (AssertionFailedError e) {
            StringBuilder sb = new StringBuilder("ExpectedData must be:\nnew ExpectedData()\n");

            for (List<String> list : actual) {
                sb.append(String.format(".addRow(\"%s\")\n", Joiner.on("\", \"").join(list)));
            }

            LOG.error(sb.toString().replaceAll("\"" + ExpectedData.NULL + "\"", "null"));
            throw e;
        }
    }


    private List<List<String>> executeQuery(String query, Object... params) {
        RowMapper<List<String>> rowMapper = (rs, rowNum) -> {
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();

            List<String> res = Lists.newArrayList();
            for (int i = 1; i <= columnCount; i++) {
                String val = rs.getString(i);
                res.add(null == val ? ExpectedData.NULL : val);
            }

            return res;

        };

        return template.query(query, params, rowMapper);
    }

	public void expectOne(String query, Object... params) {
		checkDb(new ExpectedData().addRow("1"), query, params);
	}

	public static ExpectedData expectedData() {
		return new ExpectedData();
	}

	public static class ExpectedData {

		public final static String NULL = "__NULL__";

		private List<List<String>> data = Lists.newArrayList();

		public ExpectedData addRow(String... values) {
			data.add(Lists.newArrayList(values));
			return this;
		}

		public List<List<String>> getData() {
			return data;
		}
	}
}