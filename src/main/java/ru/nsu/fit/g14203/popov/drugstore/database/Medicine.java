package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Medicine extends DBObject {

    private final static String TABLE       = "medicines";
    private final static String ID_NAME     = "id_medicine";

    private final static String ID_TYPE     = "id_type";
    private final static String NAME        = "name";
    private final static String PRICE       = "price";
    private final static String MINIMUM     = "minimum";
    private final static String AMOUNT      = "amount";
    private final static String IS_COMPLEX  = "is_complex";

    private BigDecimal      idType;
    private String          name;
    private Integer         price;
    private Integer         minimum;
    private Integer         amount;
    private Boolean         isComplex;

    public static Medicine[] loadFromDataBase() throws SQLException {
        ResultSet rawData = Connection.select(TABLE);

        Stream.Builder<Medicine> medicines = Stream.builder();
        while (rawData.next()) {
            BigDecimal id       = rawData.getBigDecimal(ID_NAME);
            BigDecimal idType   = rawData.getBigDecimal(ID_TYPE);
            String name         = rawData.getString(NAME);
            int price           = rawData.getInt(PRICE);
            int minimum         = rawData.getInt(MINIMUM);
            int amount          = rawData.getInt(AMOUNT);
            boolean isComplex   = rawData.getBoolean(IS_COMPLEX);

            medicines.add(new Medicine(id, idType, name, price, minimum, amount, isComplex));
        }

        return medicines.build().toArray(Medicine[]::new);
    }

    private Medicine(BigDecimal id,
                     BigDecimal idType, String name, int price, int minimum, int amount,
                     boolean isComplex) {
        super(TABLE);
        setId(id);

        this.idType = idType;
        this.name = name;
        this.price = price;
        this.minimum = minimum;
        this.amount = amount;
        this.isComplex = isComplex;
    }

    public Medicine(BigDecimal idType, String name, int price, int minimum, int amount,
                    boolean isComplex) throws SQLException {
        super(TABLE);

        this.idType = idType;
        this.name = name;
        this.price = price;
        this.minimum = minimum;
        this.amount = amount;
        this.isComplex = isComplex;

        Connection.insert(this);
    }

    public void updateValues(BigDecimal idType, String name, int price, int minimum, int amount,
                             boolean isComplex) throws SQLException {
        this.idType = idType;
        this.name = name;
        this.price = price;
        this.minimum = minimum;
        this.amount = amount;
        this.isComplex = isComplex;

        Connection.update(this);
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
    String getIDName() {
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
}
