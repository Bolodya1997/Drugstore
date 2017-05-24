package ru.nsu.fit.g14203.popov.view;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainFrame extends JFrame {

    public MainFrame() throws SQLException {
        super("Drugstore");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setSize(800, 600);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

//        ------   tree panel   ------
        TreePanel treePanel = new TreePanel();

        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy = 0;
        constraints.weightx = 0.35;
        constraints.weighty = 1;

        add(treePanel, constraints);

//        ------   table panel   ------
        TablePanel tablePanel = new TablePanel();

        constraints.weightx = 1;

        add(tablePanel, constraints);

//        ------   end of init   ------
        setVisible(true);
    }
}
