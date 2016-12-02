package ua.ardas.db.sqltracker;

/**
 * Created by Igor Dmitriev / Mikalai Alimenkou on 12/6/15
 */
public class QueryCountInfoHolder {
    private static QueryCountInfo queryInfo = new QueryCountInfo();

    public static QueryCountInfo getQueryInfo() {
        return queryInfo;
    }

    public static void clear() {
        queryInfo.clear();
    }
}
