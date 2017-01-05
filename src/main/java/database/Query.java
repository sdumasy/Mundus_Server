package database;

import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;

/**
 * A sql query.
 */
public abstract class Query {
    private String sql;
    private Object[] params;

    /**
     * Creates a new sql query.
     *
     * @param sql    Sql query to be executed.
     * @param params Parameters to pass to the sql query.
     */
    protected Query(String sql, Object... params) {
        this.sql = sql;
        this.params = params;
    }

    /**
     * Getter for the sql query.
     *
     * @return The sql query.
     */
    protected String getSql() {
        return sql;
    }

    /**
     * Getter for the parameters of the query.
     *
     * @return The parameters.
     */
    protected Object[] getParams() {
        return params;
    }

    /**
     * Executes the query on the given query runner with the given connection.
     *
     * @param queryRunner The QueryRunner.
     * @param connection  The connection to the db.
     */
    protected abstract void execute(QueryRunner queryRunner, Connection connection);
}
