package ru.nsu.fit.g14203.popov.drugstore.database;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Schema extends DBObject {

    private final static String TABLE       = "schemas";
    private final static String ID_NAME     = "id_schema";

    public final static String ID_MEDICINE = "id_medicine";
    public final static String DESCRIPTION = "description";
    public final static String TIME        = "time_";

    private BigDecimal      idMedicine;
    private String          description;
    private String          time;

    public static Schema[] loadFromDataBase() throws SQLException {
        ResultSet rawData = Connection.selectTable(TABLE);

        Stream.Builder<Schema> schemas = Stream.builder();
        while (rawData.next()) {
            BigDecimal id           = rawData.getBigDecimal(ID_NAME);
            BigDecimal idMedicine   = rawData.getBigDecimal(ID_MEDICINE);
            String description      = rawData.getString(DESCRIPTION);
            String time             = rawData.getString(TIME);

            schemas.add(new Schema(id, idMedicine, description, time));
        }

        return schemas.build().toArray(Schema[]::new);
    }

    private Schema(BigDecimal id,
                   BigDecimal idMedicine, String description, String time) {
        super(TABLE);

        this.id             = id;
        this.idMedicine     = idMedicine;
        this.description    = description;
        this.time           = time;
    }

    public Schema() {
        super(TABLE);
        insert = true;
    }

    public Schema(BigDecimal idMedicine, String description, String time) {
        super(TABLE);
        updateValues(idMedicine, description, time);
    }

    public void updateValues(BigDecimal idMedicine, String description, String time) {
        this.idMedicine = idMedicine;
        this.description = description;
        this.time = time;

        if (!insert)
            update = true;
    }

    public BigDecimal getIdMedicine() {
        return idMedicine;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    @Override
    String getIdName() {
        return ID_NAME;
    }

    @Override
    Connection.Column[] getColumns() {
        return new Connection.Column[]{
                new Connection.Column<>(ID_MEDICINE, idMedicine),
                new Connection.Column<>(DESCRIPTION, description),
                new Connection.Column<>(TIME, time)
        };
    }

    @Override
    void reload() throws SQLException {
        ResultSet rawData = Connection.selectSingle(this);

        idMedicine  = rawData.getBigDecimal(ID_MEDICINE);
        description = rawData.getString(DESCRIPTION);
        time        = rawData.getString(TIME);

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

        return String.format("%sSchema#%s", nameModifier(), id);
    }
}
