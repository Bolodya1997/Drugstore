package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Order extends DBObject {

    private final static String TABLE       = "orders";
    private final static String ID_NAME     = "id_order";

    private final static String ID_CUSTOMER = "id_customer";
    private final static String ID_MEDICINE = "id_medicine";

    private BigDecimal      idCustomer;
    private BigDecimal      idMedicine;

    public Order[] loadFromDataBase() throws SQLException {
        ResultSet rawData = Connection.select(TABLE);

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
        setId(id);

        this.idCustomer = idCustomer;
        this.idMedicine = idMedicine;
    }

    public Order(BigDecimal idCustomer, BigDecimal idMedicine) throws SQLException {
        super(TABLE);

        this.idCustomer = idCustomer;
        this.idMedicine = idMedicine;

        Connection.insert(this);
    }

    public void updateValues(BigDecimal idCustomer, BigDecimal idMedicine) throws SQLException {
        this.idCustomer = idCustomer;
        this.idMedicine = idMedicine;

        Connection.update(this);
    }

    public BigDecimal getIdCustomer() {
        return idCustomer;
    }

    public BigDecimal getIdMedicine() {
        return idMedicine;
    }

    @Override
    String getIDName() {
        return ID_NAME;
    }

    @Override
    Connection.Column[] getColumns() {
        return new Connection.Column[]{
                new Connection.Column<>(ID_CUSTOMER, idCustomer),
                new Connection.Column<>(ID_MEDICINE, idMedicine)
        };
    }
}
