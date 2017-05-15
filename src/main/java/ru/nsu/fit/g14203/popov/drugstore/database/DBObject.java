package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.SQLException;

abstract class DBObject {

    final String table;

    private BigDecimal id;

    DBObject(String table) {
        this.table = table;
    }

    public BigDecimal getId() {
        return id;
    }

    void setId(BigDecimal id) {
        this.id = id;
    }

    public void delete() throws SQLException {
        Connection.delete(this);
    }

    abstract String getIDName();

    abstract Connection.Column[] getColumns();
}
