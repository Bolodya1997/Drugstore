package ru.nsu.fit.g14203.popov.drugstore.database;

import oracle.jdbc.pool.OracleDataSource;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class Connection {

    static class Column<T> {
        String name;
        T value;

        Column(String name, T value) {
            this.name = name;
            this.value = value;
        }
    }

    private static BigDecimal counterID = BigDecimal.ZERO;
    private static BigDecimal nextID() {
        BigDecimal result = counterID;
        counterID = counterID.add(BigDecimal.ONE);

        return result;
    }

    private final static java.sql.Connection CONNECTION;
    static  {
        try {
            OracleDataSource ods = new OracleDataSource();
            ods.setURL("jdbc:oracle:thin:database/password@localhost:1521:xe");

            CONNECTION = ods.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void insert(DBObject dbObject) throws SQLException {
        StringBuilder sql = new StringBuilder(String.format("INSERT INTO %s (", dbObject.table));

        Column<BigDecimal> id = new Column<>(dbObject.getIDName(), nextID());
        Column[] columns = dbObject.getColumns();

        String names = Stream.concat(Stream.of(id), Arrays.stream(columns))
                .map(column -> column.name)
                .collect(Collectors.joining(", "));
        sql.append(String.format("%s) VALUES(", names));

        String values = Stream.concat(Stream.of(id), Arrays.stream(columns))
                .map(column -> column.value.toString())
                .collect(Collectors.joining(", "));
        sql.append(String.format("%s)", values));

        CONNECTION.createStatement().execute(sql.toString());

        dbObject.setId(id.value);
    }

    static void update(DBObject dbObject) throws SQLException {
        StringBuilder sql = new StringBuilder(String.format("UPDATE %s SET ", dbObject.table));

        Column<BigDecimal> id = new Column<>(dbObject.getIDName(), dbObject.getId());
        Column[] columns = dbObject.getColumns();

        String set = Arrays.stream(columns)
                .map(column -> String.format("%s=%s", column.name, column.value))
                .collect(Collectors.joining(", "));
        sql.append(set);

        String where = String.format("WHERE %s = %s", id.name, id.value);
        sql.append(where);

        CONNECTION.createStatement().execute(sql.toString());
    }

    static void delete(DBObject dbObject) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE %s = %s",
                dbObject.table, dbObject.getIDName(), dbObject.getId());

        CONNECTION.createStatement().execute(sql);
    }

    static ResultSet select(String table) throws SQLException {
        String sql = String.format("SELECT * FROM %s", table);

        return CONNECTION.createStatement().executeQuery(sql);
    }
}
