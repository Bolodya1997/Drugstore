package ru.nsu.fit.g14203.popov.drugstore.database;

import oracle.sql.INTERVALDS;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Schema extends DBObject {

    private final static String TABLE       = "schemas";
    private final static String ID_NAME     = "id_schema";

    public final static String ID_MEDICINE = "id_medicine";
    public final static String DESCRIPTION = "description";
    public final static String TIME        = "time_";

    private BigDecimal          idMedicine;
    private String              description;
    private INTERVALDS          time;
    private List<Component>     components = new ArrayList<>();

    public static Schema[] loadFromDataBase() throws SQLException {
        ResultSet rawData = Connection.selectTable(TABLE, ID_NAME);
        ResultSet componentsRawData = Connection.selectComponents();

        Component cur = null;

        Stream.Builder<Schema> schemas = Stream.builder();
loop:   while (rawData.next()) {
            Schema schema = new Schema(rawData);
            schemas.add(schema);

            while (true) {
                if (cur == null) {
                    if (componentsRawData.next()) {
                        cur = new Component(componentsRawData);
                    } else {
                        continue loop;
                    }
                }

                if (cur.getIdSchema().equals(schema.id)) {
                    schema.components.add(cur);
                    cur = null;
                } else {
                    continue loop;
                }
            }
        }

        return schemas.build().toArray(Schema[]::new);
    }

    private Schema(ResultSet rawData) throws SQLException {
        super(TABLE);

        id          = rawData.getBigDecimal(ID_NAME);
        idMedicine  = rawData.getBigDecimal(ID_MEDICINE);
        description = rawData.getString(DESCRIPTION);
        time        = (INTERVALDS) rawData.getObject(TIME);
    }

    public Schema() {
        super(TABLE);

        time = new INTERVALDS("0 00:00:00.0");

        insert = true;
    }

    public Schema(BigDecimal idMedicine, String description, String time) {
        this();
        updateValues(idMedicine, description, time);
    }

    public void updateValues(BigDecimal idMedicine, String description, String time) {
        this.idMedicine     = idMedicine;
        this.description    = description;
        this.time.setBytes(INTERVALDS.toBytes(time));

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
        return time.toString();
    }

    public List<Component> getComponents() {
        return components;
    }

    @Override
    void setId(BigDecimal id) {
        super.setId(id);

        for (Component component : components) {
            component.updateValues(id, component.getIdMedicine(), component.getAmount());
        }
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
    public void delete() {
        for (Component component : components) {
            component.delete();
        }

        super.delete();
    }

    @Override
    public void commit() throws SQLException {
        if (delete) {
            for (Component component : components) {
                component.commit();
            }
        }

        super.commit();

        if (!delete) {
            for (Component component : components) {
                component.commit();
            }
        }
    }

    @Override
    void reload() throws SQLException {
        ResultSet rawData = Connection.selectSingle(this);
        ResultSet componentsRawData = Connection.selectComponent(id);

        idMedicine  = rawData.getBigDecimal(ID_MEDICINE);
        description = rawData.getString(DESCRIPTION);
        time        = (INTERVALDS) rawData.getObject(TIME);

        components.clear();
        while (componentsRawData.next()) {
            components.add(new Component(componentsRawData));
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

        return String.format("%sSchema#%s", nameModifier(), id);
    }
}
