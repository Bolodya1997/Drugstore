package ru.nsu.fit.g14203.popov;

import ru.nsu.fit.g14203.popov.drugstore.Client;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            new Client();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
