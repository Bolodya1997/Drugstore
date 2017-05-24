package ru.nsu.fit.g14203.popov.drugstore;

import ru.nsu.fit.g14203.popov.drugstore.database.Medicine;

import java.sql.SQLException;

public class Client {

    public Client() throws SQLException {
        Medicine[] medicines = Medicine.loadFromDataBase();
        for (Medicine medicine : medicines) {
            medicine.delete();
        }
    }
}
