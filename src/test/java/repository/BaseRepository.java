package repository;

import utils.DBUtils;

import java.util.List;
import java.util.Map;

/**
 * Generic, table-agnostic query helpers. Entity-specific repositories
 * (e.g. PaymentRepository) extend this and add table-specific methods.
 *
 *     DbSteps.java          ← calls repository methods
 *         │
 *         ▼
 *     PaymentRepository     ← extends BaseRepository, adds entity-specific methods
 *         │
 *         ▼
 *     BaseRepository        ← generic helpers (this file)
 *         │
 *         ▼
 *     DBUtils.java          ← raw JDBC connection + SQL execution
 */
public abstract class BaseRepository {

    protected boolean existsByField(String table, String column, Object value) {
        String sql = "SELECT 1 FROM " + table + " WHERE " + column + " = ? LIMIT 1";
        return DBUtils.recordExists(sql, value);
    }

    protected boolean existsByFields(String table, String col1, Object val1, String col2, Object val2) {
        String sql = "SELECT 1 FROM " + table + " WHERE " + col1 + " = ? AND " + col2 + " = ? LIMIT 1";
        return DBUtils.recordExists(sql, val1, val2);
    }

    protected int countAll(String table) {
        return DBUtils.getRowCount("SELECT COUNT(*) FROM " + table);
    }

    protected int countByField(String table, String column, Object value) {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + column + " = ?";
        return DBUtils.getRowCount(sql, value);
    }

    protected Map<String, Object> findOneByField(String table, String column, Object value) {
        String sql = "SELECT * FROM " + table + " WHERE " + column + " = ? LIMIT 1";
        return DBUtils.executeQuerySingleRow(sql, value);
    }

    protected List<Map<String, Object>> findAllByField(String table, String column, Object value) {
        String sql = "SELECT * FROM " + table + " WHERE " + column + " = ?";
        return DBUtils.executeQuery(sql, value);
    }

    protected List<Map<String, Object>> findAll(String table) {
        return DBUtils.executeQuery("SELECT * FROM " + table);
    }

    protected Map<String, Object> findById(String table, Object id) {
        return findOneByField(table, "id", id);
    }

    protected List<Map<String, Object>> findByCustomQuery(String sql, Object... params) {
        return DBUtils.executeQuery(sql, params);
    }

    protected Map<String, Object> findSingleByCustomQuery(String sql, Object... params) {
        return DBUtils.executeQuerySingleRow(sql, params);
    }

    // ---------- Column extraction with safe defaults ----------

    protected String getColumnAsString(Map<String, Object> row, String column) {
        if (row == null) return null;
        Object value = row.get(column);
        return value == null ? null : value.toString();
    }

    protected int getColumnAsInt(Map<String, Object> row, String column, int defaultValue) {
        String value = getColumnAsString(row, column);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    protected long getColumnAsLong(Map<String, Object> row, String column, long defaultValue) {
        String value = getColumnAsString(row, column);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    protected boolean getColumnAsBoolean(Map<String, Object> row, String column, boolean defaultValue) {
        String value = getColumnAsString(row, column);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    protected String getScalarValue(String sql, Object... params) {
        return DBUtils.executeScalar(sql, params);
    }

    // ---------- DML ----------

    protected int deleteByField(String table, String column, Object value) {
        String sql = "DELETE FROM " + table + " WHERE " + column + " = ?";
        return DBUtils.executeUpdate(sql, value);
    }

    protected int updateField(String table, String setColumn, Object setValue, String whereColumn, Object whereValue) {
        String sql = "UPDATE " + table + " SET " + setColumn + " = ? WHERE " + whereColumn + " = ?";
        return DBUtils.executeUpdate(sql, setValue, whereValue);
    }
}
