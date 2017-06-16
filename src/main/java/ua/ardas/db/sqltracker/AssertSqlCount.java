package ua.ardas.db.sqltracker;

import static ua.ardas.db.sqltracker.QueryCountInfoHolder.getQueryInfo;

/**
 * Created by Igor Dmitriev / Mikalai Alimenkou on 12/6/15
 */
public class AssertSqlCount {

    public static void reset() {
        getQueryInfo().clear();
    }

    public static void assertQueryCount(int expected) {
        assertSqlCount("query", expected, getQueryInfo().countAll());
    }

    public static void assertQueryCountLessOrEqual(int expected) {
        assertSqlCountLessOrEqual("query less or equal", expected, getQueryInfo().countAll());
    }

    public static void assertSelectCount(int expectedSelectCount) {
        assertSqlCount("select", expectedSelectCount, getQueryInfo().getSelectCount());
    }

    public static void assertSelectCountLessOrEqual(int expectedSelectCount) {
        assertSqlCountLessOrEqual("select less or equal", expectedSelectCount, getQueryInfo().getSelectCount());
    }

    public static void assertUpdateCount(int expectedUpdateCount) {
        assertSqlCount("update", expectedUpdateCount, getQueryInfo().getUpdateCount());
    }

    public static void assertInsertCount(int expectedInsertCount) {
        assertSqlCount("insert", expectedInsertCount, getQueryInfo().getInsertCount());
    }

    public static void assertDeleteCount(int expectedDeleteCount) {
        assertSqlCount("delete", expectedDeleteCount, getQueryInfo().getDeleteCount());

    }

    public static void assertCallCount(int expectedCallCount) {
        assertSqlCount("call", expectedCallCount, getQueryInfo().getCallCount());
    }

    private static void assertSqlCount(String statement, int expectedCount, int actualCount) {
        if (expectedCount != actualCount) {
            throw new SqlCountMismatchException(statement, expectedCount, actualCount);
        }
    }

    private static void assertSqlCountLessOrEqual(String statement, int expectedCount, int actualCount) {
        if (expectedCount < actualCount) {
            throw new SqlCountMismatchException(statement, expectedCount, actualCount);
        }
    }
}
