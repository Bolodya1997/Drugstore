package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Medicine extends DBObject {

    private final static String TABLE       = "medicines";
    private final static String ID_NAME     = "id_medicine";

    public final static String ID_TYPE     = "id_type";
    public final static String NAME        = "name";
    public final static String PRICE       = "price";
    public final static String MINIMUM     = "minimum";
    public final static String AMOUNT      = "amount";
    public final static String IS_COMPLEX  = "is_complex";

    private BigDecimal      idType;
    private String          name;
    private Integer         price;
    private Integer         minimum;
    private Integer         amount;
    private Boolean         isComplex;

    public static Medicine[] loadFromDataBase() throws SQLException {
        ResultSet rawData = Connection.selectTable(TABLE, ID_NAME);

        Stream.Builder<Medicine> medicines = Stream.builder();
        while (rawData.next()) {
            medicines.add(new Medicine(rawData));
        }

        return medicines.build().toArray(Medicine[]::new);
    }

    Medicine(ResultSet rawData) throws SQLException {
        super(TABLE);

        id          = rawData.getBigDecimal(ID_NAME);
        idType      = rawData.getBigDecimal(ID_TYPE);
        name        = rawData.getString(NAME);
        price       = rawData.getInt(PRICE);
        minimum     = rawData.getInt(MINIMUM);
        amount      = rawData.getInt(AMOUNT);
        isComplex   = rawData.getBoolean(IS_COMPLEX);
    }

    public Medicine() {
        super(TABLE);

        idType      = BigDecimal.ONE;
        name        = "New medicine";
        price       = 0;
        minimum     = 0;
        amount      = 0;
        isComplex   = false;

        insert = true;
    }

    public Medicine(BigDecimal idType, String name, int price, int minimum, int amount,
                    boolean isComplex) {
        this();
        updateValues(idType, name, price, minimum, amount, isComplex);
    }

    public void updateValues(BigDecimal idType, String name, int price, int minimum, int amount,
                             boolean isComplex) {
        this.idType     = idType;
        this.name       = name;
        this.price      = price;
        this.minimum    = minimum;
        this.amount     = amount;
        this.isComplex  = isComplex;

        if (!insert)
            update = true;
    }

    public BigDecimal getIdType() {
        return idType;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getMinimum() {
        return minimum;
    }

    public Integer getAmount() {
        return amount;
    }

    public Boolean getComplex() {
        return isComplex;
    }

    @Override
    String getIdName() {
        return ID_NAME;
    }

    @Override
    Connection.Column[] getColumns() {
        return new Connection.Column[]{
                new Connection.Column<>(ID_TYPE, idType),
                new Connection.Column<>(NAME, name),
                new Connection.Column<>(PRICE, price),
                new Connection.Column<>(MINIMUM, minimum),
                new Connection.Column<>(AMOUNT, amount),
                new Connection.Column<>(IS_COMPLEX, isComplex)
        };
    }

    @Override
    void reload() throws SQLException {
        ResultSet rawData = Connection.selectSingle(this);

        idType      = rawData.getBigDecimal(ID_TYPE);
        name        = rawData.getString(NAME);
        price       = rawData.getInt(PRICE);
        minimum     = rawData.getInt(MINIMUM);
        amount      = rawData.getInt(AMOUNT);
        isComplex   = rawData.getBoolean(IS_COMPLEX);

        insert = false;
        update = false;
        delete = false;
    }

    @Override
    public String toString() {
        return String.format("%s%s", nameModifier(), name);
    }
}
