package utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC connection management and query execution. One connection per thread
 * (ThreadLocal) so parallel DB-tagged scenarios don't share a connection —
 * same isolation principle as PlaywrightManager's per-thread Page.
 */
public class DBUtils {

    private static final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    private DBUtils() {
        // static-only utility class
    }

    // ---------- Connection lifecycle ----------

    public static Connection getConnection() {
        Connection connection = connectionThreadLocal.get();
        try {
            if (connection == null || connection.isClosed()) {
                String url = ConfigReader.get("dbUrl");
                String user = ConfigReader.get("dbUser");
                String password = ConfigReader.get("dbPassword");

                connection = DriverManager.getConnection(url, user, password);
                connection.setAutoCommit(true);
                connectionThreadLocal.set(connection);
                LogUtil.info(DBUtils.class, "DB connection opened on thread {}", Thread.currentThread().getId());
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to obtain DB connection", e);
        }
    }

    public static boolean isConnected() {
        try {
            Connection connection = connectionThreadLocal.get();
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void closeConnection() {
        try {
            Connection connection = connectionThreadLocal.get();
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LogUtil.info(DBUtils.class, "DB connection closed on thread {}", Thread.currentThread().getId());
            }
        } catch (SQLException e) {
            LogUtil.warn(DBUtils.class, "Failed to close DB connection cleanly: {}", e.getMessage());
        } finally {
            connectionThreadLocal.remove();
        }
    }

    // ---------- SELECT ----------

    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (PreparedStatement stmt = prepare(sql, params);
             ResultSet rs = stmt.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                rows.add(row);
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Query failed: " + sql, e);
        }
    }

    public static Map<String, Object> executeQuerySingleRow(String sql, Object... params) {
        List<Map<String, Object>> rows = executeQuery(sql, params);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public static String executeScalar(String sql, Object... params) {
        try (PreparedStatement stmt = prepare(sql, params);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Object value = rs.getObject(1);
                return value == null ? null : value.toString();
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Scalar query failed: " + sql, e);
        }
    }

    // ---------- DML ----------

    public static int executeUpdate(String sql, Object... params) {
        try (PreparedStatement stmt = prepare(sql, params)) {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Update failed: " + sql, e);
        }
    }

    // ---------- Checks ----------

    public static boolean recordExists(String sql, Object... params) {
        return !executeQuery(sql, params).isEmpty();
    }

    public static int getRowCount(String sql, Object... params) {
        String scalar = executeScalar(sql, params);
        return scalar == null ? 0 : Integer.parseInt(scalar);
    }

    // ---------- Transactions ----------

    public static void beginTransaction() {
        try {
            getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to begin transaction", e);
        }
    }

    public static void commitTransaction() {
        try {
            Connection connection = getConnection();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to commit transaction", e);
        }
    }

    /** Rolls back changes — used in @After hooks for DB-writing scenarios to keep the DB clean between runs. */
    public static void rollbackTransaction() {
        try {
            Connection connection = getConnection();
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            LogUtil.warn(DBUtils.class, "Failed to roll back transaction: {}", e.getMessage());
        }
    }

    // ---------- Internal ----------

    private static PreparedStatement prepare(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt;
    }
}
