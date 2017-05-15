package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Event extends DBObject {

    private final static String TABLE       = "events";
    private final static String ID_NAME     = "id_event";

    private final static String ID_ORDER    = "id_order";
    private final static String DATE        = "date_";
    private final static String TYPE        = "type_";

    private BigDecimal      idOrder;
    private Date            date;
    private String          type;

    public static Event[] loadFromDataBase() throws SQLException {
        ResultSet rawData = Connection.select(TABLE);

        Stream.Builder<Event> events = Stream.builder();
        while (rawData.next()) {
            BigDecimal id       = rawData.getBigDecimal(ID_NAME);
            BigDecimal idOrder  = rawData.getBigDecimal(ID_ORDER);
            Date date           = rawData.getDate(DATE);
            String type         = rawData.getString(TYPE);

            events.add(new Event(id, idOrder, date, type));
        }

        return events.build().toArray(Event[]::new);
    }

    private Event(BigDecimal id,
                  BigDecimal idOrder, Date date, String type) {
        super(TABLE);
        setId(id);

        this.idOrder = idOrder;
        this.date = date;
        this.type = type;
    }

    public Event(BigDecimal idOrder, Date date, String type) throws SQLException {
        super(TABLE);

        this.idOrder = idOrder;
        this.date = date;
        this.type = type;

        Connection.insert(this);
    }

    public void updateValues(BigDecimal idOrder, Date date, String type) throws SQLException {
        this.idOrder = idOrder;
        this.date = date;
        this.type = type;

        Connection.update(this);
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
    String getIDName() {
        return ID_NAME;
    }

    @Override
    Connection.Column[] getColumns() {
        return new Connection.Column[]{
                new Connection.Column<>(ID_ORDER, idOrder),
                new Connection.Column<>(DATE, date),
                new Connection.Column<>(TYPE, type)
        };
    }
}
