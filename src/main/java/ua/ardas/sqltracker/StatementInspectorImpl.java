package ua.ardas.sqltracker;

import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * Created by Igor Dmitriev / Mikalai Alimenkou on 4/30/16
 */
public class StatementInspectorImpl implements StatementInspector {

    private static final QueryHandler QUERY_HANDLER = new QueryCountInfoHandler();

    @Override
    public String inspect(String sql) {
        System.out.println("==========> sql: " +sql);
        QUERY_HANDLER.handleSql(sql);
        return sql;
    }
}
