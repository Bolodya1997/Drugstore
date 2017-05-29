package ru.nsu.fit.g14203.popov;

import ru.nsu.fit.g14203.popov.drugstore.view.MainFrame;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            new MainFrame();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
