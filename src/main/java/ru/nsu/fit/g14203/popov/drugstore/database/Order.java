package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.stream.Stream;

public class Order extends DBObject {

    private final static String TABLE       = "orders";
    private final static String ID_NAME     = "id_order";

    private final static String ID_CUSTOMER = "id_customer";
    private final static String ID_MEDICINE = "id_medicine";

    private BigDecimal      idCustomer;
    private BigDecimal      idMedicine;

    public static Order[] loadFromDataBase() throws SQLException {
        ResultSet rawData = Connection.selectTable(TABLE);

        Stream.Builder<Order> orders = Stream.builder();
        while (rawData.next()) {
            BigDecimal id           = rawData.getBigDecimal(ID_NAME);
            BigDecimal idCustomer   = rawData.getBigDecimal(ID_CUSTOMER);
            BigDecimal idMedicine   = rawData.getBigDecimal(ID_MEDICINE);

            orders.add(new Order(id, idCustomer, idMedicine));
        }

        return orders.build().toArray(Order[]::new);
    }

    private Order(BigDecimal id,
                  BigDecimal idCustomer, BigDecimal idMedicine) {
        super(TABLE);

        this.id         = id;
        this.idCustomer = idCustomer;
        this.idMedicine = idMedicine;
    }

    public Order() {
        super(TABLE);
        insert = true;
    }

    public Order(BigDecimal idCustomer, BigDecimal idMedicine) {
        this();
        updateValues(idCustomer, idMedicine);
    }

    public void updateValues(BigDecimal idCustomer, BigDecimal idMedicine) {
        this.idCustomer = idCustomer;
        this.idMedicine = idMedicine;

        if (!insert)
            update = true;
    }

    public BigDecimal getIdCustomer() {
        return idCustomer;
    }

    public BigDecimal getIdMedicine() {
        return idMedicine;
    }

    @Override
    String getIdName() {
        return ID_NAME;
    }

    @Override
    Connection.Column[] getColumns() {
        return new Connection.Column[]{
                new Connection.Column<>(ID_CUSTOMER, idCustomer),
                new Connection.Column<>(ID_MEDICINE, idMedicine)
        };
    }

    @Override
    void reload() throws SQLException {
        ResultSet rawData = Connection.selectSingle(this);

        idCustomer  = rawData.getBigDecimal(ID_CUSTOMER);
        idMedicine  = rawData.getBigDecimal(ID_MEDICINE);

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
