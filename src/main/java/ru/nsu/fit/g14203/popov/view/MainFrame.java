package ru.nsu.fit.g14203.popov.view;

import ru.nsu.fit.g14203.popov.drugstore.database.DBObject;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainFrame extends JFrame {

    private TablePanel tablePanel;

    public MainFrame() throws SQLException {
        super("Drugstore");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setSize(800, 600);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

//        ------   tree panel   ------
        TreePanel treePanel = new TreePanel(this::showObject, this::showTable);

        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy = 0;
        constraints.weightx = 0.35;
        constraints.weighty = 1;

        add(treePanel, constraints);

//        ------   table panel   ------
        tablePanel = new TablePanel(treePanel.typesNode,
                                    treePanel.medicinesNode,
                                    treePanel.customersNode,
                                    treePanel.ordersNode,
                                    treePanel.schemasNode);

        constraints.weightx = 1;

        add(tablePanel, constraints);

//        ------   end of init   ------
        setVisible(true);
    }

    private void showObject(DBObject dbObject) {

    }

    private void showTable(TableNode tableNode) {
        tablePanel.tableView.showTable(tableNode);
        tablePanel.repaint();
    }
}
