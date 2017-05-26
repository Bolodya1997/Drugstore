package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.SQLException;

public abstract class DBObject {

    public final String table;

    protected BigDecimal id;

    protected boolean insert;
    protected boolean update;
    protected boolean delete;

    protected DBObject(String table) {
        this.table = table;
    }

    public BigDecimal getId() {
        return id;
    }

    public void delete() {
        insert = false;
        update = false;
        delete = true;
    }

    public void commit() throws SQLException {
        if (insert)
            Connection.insert(this);
        else if (update)
            Connection.update(this);
        else if (delete)
            Connection.delete(this);

        insert = false;
        update = false;
        delete = false;
    }

    public boolean isInsert() {
        return insert;
    }

    public boolean isUpdate() {
        return update;
    }

    public boolean isDelete() {
        return delete;
    }

    protected String nameModifier() {
        if (insert)
            return "+";

        if (update)
            return "*";

        if (delete)
            return "-";

        return "";
    }

    abstract String getIdName();
    abstract Connection.Column[] getColumns();

    abstract void reload() throws SQLException;
}
