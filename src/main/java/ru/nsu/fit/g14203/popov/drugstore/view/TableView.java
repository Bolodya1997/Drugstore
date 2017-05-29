package ru.nsu.fit.g14203.popov.drugstore.view;

import ru.nsu.fit.g14203.popov.drugstore.database.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.function.Consumer;

class TableView extends JTable {

    private TableNode<Type> typesNode;
    private TableNode<Medicine> medicinesNode;
    private TableNode<Customer> customersNode;
    private TableNode<Order> ordersNode;
    private TableNode<Schema> schemasNode;

    TableView(TableNode<Type> typesNode,
              TableNode<Medicine> medicinesNode,
              TableNode<Customer> customersNode,
              TableNode<Order> ordersNode,
              TableNode<Schema> schemasNode,
              Consumer<TableNode.DBNode> showObjectNode) {
        this.typesNode = typesNode;
        this.medicinesNode = medicinesNode;
        this.customersNode = customersNode;
        this.ordersNode = ordersNode;
        this.schemasNode = schemasNode;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 2)
                    return;

                if (!isColumnSelected(0))
                    return;

                TableNode.DBNode dbNode = (TableNode.DBNode) getModel().getValueAt(getSelectedRow(), 0);
                showObjectNode.accept(dbNode);
            }
        });
    }

    void showTable(TableNode table) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        setModel(tableModel);

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
        DefaultTableModel tableModel = (DefaultTableModel) getModel();

        tableModel.addColumn("type");
        tableModel.addColumn(Type.INNER_ABLE);
        tableModel.addColumn(Type.OUTER_ABLE);
        tableModel.addColumn(Type.MIX_ABLE);

        for (TableNode<Type>.DBNode dbNode : typesNode.getChildren()) {
            Type typeObject = dbNode.object;

            Object[] data = {
                    dbNode,
                    typeObject.getInnerAble(),
                    typeObject.getOuterAble(),
                    typeObject.getMixAble()
            };
            tableModel.addRow(data);
        }
    }

    private void showMedicinesTable() {
        DefaultTableModel tableModel = (DefaultTableModel) getModel();

        tableModel.addColumn("medicine");
        tableModel.addColumn("type");
        tableModel.addColumn(Medicine.PRICE);
        tableModel.addColumn(Medicine.AMOUNT);
        tableModel.addColumn(Medicine.MINIMUM);
        tableModel.addColumn(Medicine.IS_COMPLEX);

        for (TableNode<Medicine>.DBNode dbNode : medicinesNode.getChildren()) {
            Medicine medicineObject = dbNode.object;

            Object[] data = {
                    dbNode,
                    typeIdToType(typesNode, medicineObject.getIdType()),
                    medicineObject.getPrice(),
                    medicineObject.getAmount(),
                    medicineObject.getMinimum(),
                    medicineObject.getComplex()
            };
            tableModel.addRow(data);
        }
    }

    private void showCustomersTable() {
        DefaultTableModel tableModel = (DefaultTableModel) getModel();

        tableModel.addColumn("customer");
        tableModel.addColumn(Customer.PHONE_NUMBER);
        tableModel.addColumn(Customer.ADDRESS);

        for (TableNode<Customer>.DBNode dbNode : customersNode.getChildren()) {
            Customer customerObject = dbNode.object;

            Object[] data = {
                    dbNode,
                    customerObject.getPhoneNumber(),
                    customerObject.getAddress()
            };
            tableModel.addRow(data);
        }
    }

    private void showOrdersTable() {
        DefaultTableModel tableModel = (DefaultTableModel) getModel();

        tableModel.addColumn("order");
        tableModel.addColumn("customer");
        tableModel.addColumn("medicine");
        tableModel.addColumn(Order.AMOUNT);
        tableModel.addColumn("status");

        for (TableNode<Order>.DBNode dbNode : ordersNode.getChildren()) {
            Order orderObject = dbNode.object;

            Object[] data = {
                    dbNode,
                    customerIdToCustomer(customersNode, orderObject.getIdCustomer()),
                    medicineIdToMedicine(medicinesNode, orderObject.getIdMedicine()),
                    orderObject.getAmount(),
                    orderObject.getEvents().getLast()
            };
            tableModel.addRow(data);
        }
    }

    private void showSchemasTable() {
        DefaultTableModel tableModel = (DefaultTableModel) getModel();

        tableModel.addColumn("schema");
        tableModel.addColumn("medicine");
        tableModel.addColumn("time");

        for (TableNode<Schema>.DBNode dbNode : schemasNode.getChildren()) {
            Schema schemaObject = dbNode.object;

            Object[] data = {
                    dbNode,
                    medicineIdToMedicine(medicinesNode, schemaObject.getIdMedicine()),
                    schemaObject.getTime()
            };
            tableModel.addRow(data);
        }
    }

    static Type typeIdToType(TableNode<Type> typesNode, BigDecimal typeId) {
        if (typeId == null)
            return null;

        return typesNode.getChildren().stream()
                .map(child -> child.object)
                .filter(typeObject -> typeObject.getId().equals(typeId))
                .findFirst().orElse(null);
    }

    static Customer customerIdToCustomer(TableNode<Customer> customersNode, BigDecimal customerID) {
        if (customerID == null)
            return null;

        return customersNode.getChildren().stream()
                .map(child -> child.object)
                .filter(customerObject -> customerObject.getId().equals(customerID))
                .findFirst().orElse(null);
    }

    static Medicine medicineIdToMedicine(TableNode<Medicine> medicinesNode, BigDecimal medicineID) {
        if (medicineID == null)
            return null;

        return medicinesNode.getChildren().stream()
                .map(child -> child.object)
                .filter(medicineObject -> medicineObject.getId().equals(medicineID))
                .findFirst().orElse(null);
    }
}
