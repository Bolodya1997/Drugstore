package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Component extends DBObject {

    private final static String TABLE   = "components";
    private final static String ID_NAME = "id_component";

    private final static String ID_SCHEMA   = "id_schema";
    private final static String ID_MEDICINE = "id_medicine";
    private final static String AMOUNT      = "amount";

    private BigDecimal  idSchema;
    private BigDecimal  idMedicine;
    private Integer     amount;

    Component(ResultSet rawData) throws SQLException {
        super(TABLE);

        id          = rawData.getBigDecimal(ID_NAME);
        idSchema    = rawData.getBigDecimal(ID_SCHEMA);
        idMedicine  = rawData.getBigDecimal(ID_MEDICINE);
        amount      = rawData.getInt(AMOUNT);
    }

    public Component() {
        super(TABLE);

        amount = 0;

        insert = true;
    }

    public Component(BigDecimal idSchema, BigDecimal idMedicine, int amount) {
        this();
        updateValues(idSchema, idMedicine, amount);
    }

    public void updateValues(BigDecimal idSchema, BigDecimal idMedicine, int amount) {
        this.idSchema = idSchema;
        this.idMedicine = idMedicine;
        this.amount = amount;

        if (!insert)
            update = true;
    }

    public BigDecimal getIdSchema() {
        return idSchema;
    }

    public BigDecimal getIdMedicine() {
        return idMedicine;
    }

    public Integer getAmount() {
        return amount;
    }

    @Override
    String getIdName() {
        return ID_NAME;
    }

    @Override
    Connection.Column[] getColumns() {
        return new Connection.Column[]{
                new Connection.Column<>(ID_SCHEMA, idSchema),
                new Connection.Column<>(ID_MEDICINE, idMedicine),
                new Connection.Column<>(AMOUNT, amount)
        };
    }

    @Override
    void reload() throws SQLException {
        ResultSet rawData = Connection.selectSingle(this);

        idSchema    = rawData.getBigDecimal(ID_SCHEMA);
        idMedicine  = rawData.getBigDecimal(ID_MEDICINE);
        amount      = rawData.getInt(AMOUNT);

        insert = false;
        update = false;
        delete = false;
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
