package database;

import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Update Query.
 */
public class Update extends Query {
    private UpdateCallback callback;
    private Boolean result;

    /**
     * Creates a new sql update query.
     *
     * @param sql      Sql query to be executed.
     * @param callback Callback function.
     * @param params   Parameters to pass to the sql query.
     */
    public Update(String sql, UpdateCallback callback, Object... params) {
        super(sql, params);
        this.callback = callback;
    }

    @Override
    public void execute(QueryRunner queryRunner, Connection connection) {
        try {
            Boolean result = queryRunner.update(connection, getSql(), getParams()) > 0;
            this.result = result;
            callback.handle(result);
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't query the database.", e);
        }
    }

    /**
     * getter for the result of the query.
     *
     * @return The result
     */
    public Boolean getResult() {
        return result;
    }

    /**
     * Interface of the callback function.
     */
    public interface UpdateCallback {
        /**
         * Gets called with whether the query succeeded.
         *
         * @param result Result of the query.
         */
        void handle(boolean result);
    }
}
