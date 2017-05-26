package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Customer extends DBObject {

    private final static String TABLE           = "customers";
    private final static String ID_NAME         = "id_customer";

    public final static String NAME            = "name";
    public final static String PHONE_NUMBER    = "phone_number";
    public final static String ADDRESS         = "address";

    private String      name;
    private String      phoneNumber;
    private String      address;

    public static Customer[] loadFromDataBase() throws SQLException {
        ResultSet rawData = Connection.selectTable(TABLE);

        Stream.Builder<Customer> customers = Stream.builder();
        while (rawData.next()) {
            BigDecimal id       = rawData.getBigDecimal(ID_NAME);
            String name         = rawData.getString(NAME);
            String phoneNumber  = rawData.getString(PHONE_NUMBER);
            String address      = rawData.getString(ADDRESS);

            customers.add(new Customer(id, name, phoneNumber, address));
        }

        return customers.build().toArray(Customer[]::new);
    }

    private Customer(BigDecimal id,
                     String name, String phoneNumber, String address) {
        super(TABLE);

        this.id             = id;
        this.name           = name;
        this.phoneNumber    = phoneNumber;
        this.address        = address;
    }

    public Customer() {
        super(TABLE);
        insert = true;
    }

    public Customer(String name, String phoneNumber, String address) {
        this();
        updateValues(name, phoneNumber, address);
    }

    public void updateValues(String name, String phoneNumber, String address) {
        this.name           = name;
        this.phoneNumber    = phoneNumber;
        this.address        = address;

        if (!insert)
            update = true;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    @Override
    String getIdName() {
        return ID_NAME;
    }

    @Override
    Connection.Column[] getColumns() {
        return new Connection.Column[]{
                new Connection.Column<>(NAME, name),
                new Connection.Column<>(PHONE_NUMBER, phoneNumber),
                new Connection.Column<>(ADDRESS, address)
        };
    }

    @Override
    void reload() throws SQLException {
        ResultSet rawData = Connection.selectSingle(this);

        name        = rawData.getString(NAME);
        phoneNumber = rawData.getString(PHONE_NUMBER);
        address     = rawData.getString(ADDRESS);

        insert = false;
        update = false;
        delete = false;
    }

    @Override
    public String toString() {
        return String.format("%s%s", nameModifier(), name);
    }
}
