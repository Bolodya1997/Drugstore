package ru.nsu.fit.g14203.popov.drugstore.view;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainFrame extends JFrame {

    private TreePanel treePanel;
    private TablePanel tablePanel;
    private ObjectPanel objectPanel;

    public MainFrame() throws SQLException {
        super("Drugstore");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setSize(800, 600);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

//        ------   tree panel   ------
        treePanel = new TreePanel(this::showObjectNode, this::showTable);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.5;
        constraints.weighty = 1;

        add(treePanel, constraints);

//        ------   table panel   ------
        tablePanel = new TablePanel(treePanel.typesNode,
                                    treePanel.medicinesNode,
                                    treePanel.customersNode,
                                    treePanel.ordersNode,
                                    treePanel.schemasNode,
                                    this::showObjectNode);

        constraints.gridx = 1;
        constraints.weightx = 1;

        add(tablePanel, constraints);

//        ------   object panel   ------
        objectPanel = new ObjectPanel(treePanel.typesNode,
                                      treePanel.medicinesNode,
                                      treePanel.customersNode,
                                      treePanel.ordersNode,
                                      treePanel.schemasNode);

        add(objectPanel, constraints);

//        ------   end of init   ------
        setVisible(true);
    }

    void showObjectNode(TableNode.DBNode dbNode) {
        tablePanel.setVisible(false);
        objectPanel.setVisible(true);

        treePanel.selectNode(dbNode);

        objectPanel.showObject(dbNode.object);
        objectPanel.repaint();
    }

    private void showTable(TableNode tableNode) {
        objectPanel.setVisible(false);
        tablePanel.setVisible(true);

        tablePanel.showTable(tableNode);
        tablePanel.repaint();
    }
}
