package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Order extends DBObject {

    private final static String TABLE       = "orders";
    private final static String ID_NAME     = "id_order";

    private final static String ID_CUSTOMER = "id_customer";
    private final static String ID_MEDICINE = "id_medicine";
    public final static String AMOUNT       = "amount";

    private BigDecimal  idCustomer;
    private BigDecimal  idMedicine;
    private Integer     amount;
    private LinkedList<Event> events = new LinkedList<>();

    public static Order[] loadFromDataBase() throws SQLException {
        ResultSet rawData = Connection.selectTable(TABLE, ID_NAME);
        ResultSet eventsRawData = Connection.selectEvents();

        Event cur = null;

        Stream.Builder<Order> orders = Stream.builder();
loop:   while (rawData.next()) {
            Order order = new Order(rawData);
            orders.add(order);

            while (true) {
                if (cur == null) {
                    if (eventsRawData.next())
                        cur = new Event(eventsRawData);
                    else
                        continue loop;
                }

                if (cur.getIdOrder().equals(order.id)) {
                    order.events.add(cur);
                    cur = null;
                } else {
                    continue loop;
                }
            }
        }

        return orders.build().toArray(Order[]::new);
    }

    private Order(ResultSet rawData) throws SQLException {
        super(TABLE);

        id          = rawData.getBigDecimal(ID_NAME);
        idCustomer  = rawData.getBigDecimal(ID_CUSTOMER);
        idMedicine  = rawData.getBigDecimal(ID_MEDICINE);
        amount      = rawData.getInt(AMOUNT);
    }

    public Order() {
        super(TABLE);

        amount = 0;
        events.add(new Event(id, Event.TYPE_OPEN));

        insert = true;
    }

    public Order(BigDecimal idCustomer, BigDecimal idMedicine, int amount) {
        this();
        updateValues(idCustomer, idMedicine, amount);
    }

    public void updateValues(BigDecimal idCustomer, BigDecimal idMedicine, int amount) {
        this.idCustomer = idCustomer;
        this.idMedicine = idMedicine;
        this.amount     = amount;

        if (!insert)
            update = true;
    }

    public BigDecimal getIdCustomer() {
        return idCustomer;
    }

    public BigDecimal getIdMedicine() {
        return idMedicine;
    }

    public Integer getAmount() {
        return amount;
    }

    public LinkedList<Event> getEvents() {
        return events;
    }

    @Override
    void setId(BigDecimal id) {
        super.setId(id);

        for (Event event : events) {
            event.updateValues(id, event.getType());
        }
    }

    @Override
    String getIdName() {
        return ID_NAME;
    }

    @Override
    Connection.Column[] getColumns() {
        return new Connection.Column[]{
                new Connection.Column<>(ID_CUSTOMER, idCustomer),
                new Connection.Column<>(ID_MEDICINE, idMedicine),
                new Connection.Column<>(AMOUNT, amount)
        };
    }

    @Override
    public void delete() {
        for (Event event : events) {
            event.delete();
        }

        super.delete();
    }

    @Override
    public void commit() throws SQLException {
        if (delete) {
            for (Event event : events) {
                event.commit();
            }
        }

        super.commit();

        if (!delete) {
            for (Event event : events) {
                event.commit();
            }
        }
    }

    @Override
    void reload() throws SQLException {
        ResultSet rawData = Connection.selectSingle(this);
        ResultSet eventsRawData = Connection.selectEvent(id);

        idCustomer  = rawData.getBigDecimal(ID_CUSTOMER);
        idMedicine  = rawData.getBigDecimal(ID_MEDICINE);
        amount      = rawData.getInt(AMOUNT);

        events.clear();
        while (eventsRawData.next()) {
            events.add(new Event(eventsRawData));
        }

        insert = false;
        update = false;
        delete = false;
    }

    @Override
    public String toString() {
        String id;
        if (insert)
            id = "";
        else
            id = this.id.toString();

        return String.format("%sOrder#%s", nameModifier(), id);
    }
}
