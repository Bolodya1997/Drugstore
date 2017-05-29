package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Event extends DBObject {

    public final static String TYPE_OPEN    = "open";
    public final static String TYPE_WAIT    = "wait";
    public final static String TYPE_PROCESS = "process";
    public final static String TYPE_READY   = "ready";
    public final static String TYPE_CLOSE   = "close";

    private final static String TABLE       = "events";
    private final static String ID_NAME     = "id_event";

    private final static String ID_ORDER    = "id_order";
    public final static String DATE        = "date_";
    private final static String TYPE        = "type_";

    private BigDecimal      idOrder;
    private Date            date;
    private String          type;

    Event(ResultSet rawData) throws SQLException {
        super(TABLE);

        id      = rawData.getBigDecimal(ID_NAME);
        idOrder = rawData.getBigDecimal(ID_ORDER);
        date    = rawData.getDate(DATE);
        type    = rawData.getString(TYPE);
    }

    public Event() {
        super(TABLE);
        insert = true;
    }

    public Event(BigDecimal idOrder, String type) {
        this();
        updateValues(idOrder, type);
    }

    public void updateValues(BigDecimal idOrder, String type) {
        this.idOrder    = idOrder;
        this.type       = type;

        if (!insert)
            update = true;
    }

    public BigDecimal getIdOrder() {
        return idOrder;
    }

    public Date getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    @Override
    String getIdName() {
        return ID_NAME;
    }

    @Override
    Connection.Column[] getColumns() {
        return new Connection.Column[]{
                new Connection.Column<>(ID_ORDER, idOrder),
                new Connection.Column<>(TYPE, type)
        };
    }

    @Override
    void reload() throws SQLException {
        ResultSet rawData = Connection.selectSingle(this);

        idOrder     = rawData.getBigDecimal(ID_ORDER);
        date        = rawData.getDate(DATE);
        type        = rawData.getString(TYPE);

        insert = false;
        update = false;
        delete = false;
    }

    @Override
    public String toString() {
        return type;
    }
}
