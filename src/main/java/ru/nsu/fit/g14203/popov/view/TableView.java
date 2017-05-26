package ru.nsu.fit.g14203.popov.view;

import ru.nsu.fit.g14203.popov.drugstore.database.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class TableView extends JTable {

    private TableNode<Type> typesNode;
    private TableNode<Medicine> medicinesNode;
    private TableNode<Customer> customersNode;
    private TableNode<Order> ordersNode;
    private TableNode<Schema> schemasNode;

    public TableView(TableNode<Type> typesNode,
                     TableNode<Medicine> medicinesNode,
                     TableNode<Customer> customersNode,
                     TableNode<Order> ordersNode,
                     TableNode<Schema> schemasNode) {
        this.typesNode = typesNode;
        this.medicinesNode = medicinesNode;
        this.customersNode = customersNode;
        this.ordersNode = ordersNode;
        this.schemasNode = schemasNode;
    }

    void showTable(TableNode table) {
        if (table == typesNode)
            showTypesTable();
        else if (table == medicinesNode)
            showMedicinesTable();
        else if (table == customersNode)
            showCustomersTable();
        else if (table == ordersNode)
            showOrdersTable();
        else if (table == schemasNode)
            showSchemasTable();
        else
            throw new IllegalArgumentException(String.format("unexpected type %s", table));
    }

    private void showTypesTable() {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableModel.addColumn(Type.NAME);
        tableModel.addColumn(Type.INNER_ABLE);
        tableModel.addColumn(Type.OUTER_ABLE);
        tableModel.addColumn(Type.MIX_ABLE);

        for (Object o : typesNode.getChildren()) {
            Type typeObject = ((TableNode<Type>.DBNode) o).object;

            Object[] data = {
                    typeObject.getName(),
                    typeObject.getInnerAble(),
                    typeObject.getOuterAble(),
                    typeObject.getMixAble()
            };
            tableModel.addRow(data);
        }

        setModel(tableModel);
    }

    private void showMedicinesTable() {
        //  TODO: here
    }

    private void showCustomersTable() {

    }

    private void showOrdersTable() {

    }

    private void showSchemasTable() {

    }


}
