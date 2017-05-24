package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Type extends DBObject {

    private final static String TABLE       = "types";
    private final static String ID_NAME     = "id_type";

    private final static String NAME        = "name";
    private final static String INNER_ABLE  = "inner_able";
    private final static String OUTER_ABLE  = "outer_able";
    private final static String MIX_ABLE    = "mix_able";

    private String      name;
    private Boolean     innerAble;
    private Boolean     outerAble;
    private Boolean     mixAble;

    public static Type[] loadFromDataBase() throws SQLException {
        ResultSet rawData = Connection.select(TABLE);

        Stream.Builder<Type> types = Stream.builder();
        while (rawData.next()) {
            BigDecimal id       = rawData.getBigDecimal(ID_NAME);
            String name         = rawData.getString(NAME);
            boolean innerAble   = rawData.getBoolean(INNER_ABLE);
            boolean outerAble   = rawData.getBoolean(OUTER_ABLE);
            boolean mixAble     = rawData.getBoolean(MIX_ABLE);

            types.add(new Type(id, name, innerAble, outerAble, mixAble));
        }

        return types.build().toArray(Type[]::new);
    }

    private Type(BigDecimal id,
                 String name, boolean innerAble, boolean outerAble, boolean mixAble) {
        super(TABLE);
        setId(id);

        this.name = name;
        this.innerAble = innerAble;
        this.outerAble = outerAble;
        this.mixAble = mixAble;
    }

    public Type(String name, boolean innerAble, boolean outerAble, boolean mixAble)
            throws SQLException {
        super(TABLE);

        this.name = name;
        this.innerAble = innerAble;
        this.outerAble = outerAble;
        this.mixAble = mixAble;

        Connection.insert(this);
    }

    public void updateValues(String name, boolean innerAble, boolean outerAble, boolean mixAble)
            throws SQLException {
        this.name = name;
        this.innerAble = innerAble;
        this.outerAble = outerAble;
        this.mixAble = mixAble;

        Connection.update(this);
    }

    public String getName() {
        return name;
    }

    public Boolean getInnerAble() {
        return innerAble;
    }

    public Boolean getOuterAble() {
        return outerAble;
    }

    public Boolean getMixAble() {
        return mixAble;
    }

    @Override
    String getIdName() {
        return ID_NAME;
    }

    @Override
    Connection.Column[] getColumns() {
        return new Connection.Column[]{
                new Connection.Column<>(NAME, name),
                new Connection.Column<>(INNER_ABLE, innerAble),
                new Connection.Column<>(OUTER_ABLE, outerAble),
                new Connection.Column<>(MIX_ABLE, mixAble)
        };
    }
}
