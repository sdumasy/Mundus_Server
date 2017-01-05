package database;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Search Query.
 */
public class Search extends Query {
    private SearchCallback callback;
    private List<Map<String, Object>> result;

    /**
     * Creates a new sql Search query.
     *
     * @param sql      Sql query to be executed.
     * @param callback Callback function.
     * @param params   Parameters to pass to the sql query.
     */
    public Search(String sql, SearchCallback callback, Object... params) {
        super(sql, params);
        this.callback = callback;
    }

    @Override
    public void execute(QueryRunner runner, Connection connection) {
        try {
            List<Map<String, Object>> result =
                    runner.query(connection, getSql(), new MapListHandler(), getParams());
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
    public List<Map<String, Object>> getResult() {
        return result;
    }

    /**
     * Interface of the callback function.
     */
    public interface SearchCallback {
        /**
         * Gets called with the query result.
         *
         * @param result Result of the query.
         */
        void handle(List<Map<String, Object>> result);
    }
}
