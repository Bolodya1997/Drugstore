package ru.nsu.fit.g14203.popov.drugstore.database;

import oracle.jdbc.pool.OracleDataSource;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

final class Connection {

    static class Column<T> {
        String name;
        T value;

        Column(String name, T value) {
            this.name = name;
            this.value = value;
        }
    }

    private final static java.sql.Connection CONNECTION;
    static {
        try {
            OracleDataSource ods = new OracleDataSource();
            ods.setURL("jdbc:oracle:thin:database/password@localhost:1521:xe");

            CONNECTION = ods.getConnection();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setColumn(ResultSet resultSet, Column column)
            throws SQLException {

        String name = column.name;
        Object value = column.value;

        if (value instanceof BigDecimal) {
            resultSet.updateBigDecimal(name, (BigDecimal) value);
            return;
        }

        if (value instanceof String) {
            resultSet.updateString(name, (String) value);
            return;
        }

        if (value instanceof Integer) {
            resultSet.updateInt(name, (Integer) value);
            return;
        }

        if (value instanceof Boolean) {
            resultSet.updateBoolean(name, (Boolean) value);
            return;
        }

        if (value instanceof Date) {
            resultSet.updateDate(name, (Date) value);
            return;
        }

        throw new IllegalArgumentException(String.format("unexpected type %s", value.getClass()));
    }

    static void insert(DBObject dbObject) throws SQLException {
        String sql = String.format("SELECT %s.* FROM %s WHERE 0 = 1", dbObject.table, dbObject.table);
        Statement statement = CONNECTION.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = statement.executeQuery(sql);

        resultSet.moveToInsertRow();

        Column[] columns = dbObject.getColumns();
        for (Column column : columns) {
            setColumn(resultSet, column);
        }

        resultSet.insertRow();

//        ------   get ID   ------
        sql = String.format("SELECT MAX(%s) FROM %s", dbObject.getIdName(), dbObject.table);
        statement = CONNECTION.createStatement();
        resultSet = statement.executeQuery(sql);

        resultSet.next();
        dbObject.setId(resultSet.getBigDecimal(1));
    }

    static void update(DBObject dbObject) throws SQLException {
        String sql = String.format("SELECT %s.* FROM %s WHERE %s = %s", dbObject.table, dbObject.table,
                dbObject.getIdName(), dbObject.getId());
        Statement statement = CONNECTION.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = statement.executeQuery(sql);

        resultSet.next();

        Column[] columns = dbObject.getColumns();
        for (Column column : columns) {
            setColumn(resultSet, column);
        }

        resultSet.updateRow();
    }

    static void delete(DBObject dbObject) throws SQLException {
        String sql = String.format("SELECT %s.* FROM %s WHERE %s = %s", dbObject.table, dbObject.table,
                dbObject.getIdName(), dbObject.getId());
        Statement statement = CONNECTION.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = statement.executeQuery(sql);

        resultSet.next();
        resultSet.deleteRow();
    }

    static ResultSet select(String table) throws SQLException {
        String sql = String.format("SELECT * FROM %s", table);
        Statement statement = CONNECTION.createStatement();
        return statement.executeQuery(sql);
    }
}
