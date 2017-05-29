package ru.nsu.fit.g14203.popov.drugstore.view;

import ru.nsu.fit.g14203.popov.drugstore.database.*;
import ru.nsu.fit.g14203.popov.drugstore.database.Event;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;

class OrderView extends JPanel {

    private TableNode<Customer> customersNode;
    private TableNode<Medicine> medicinesNode;

    private JLabel orderLabel = new JLabel();
    private JComboBox<Customer> customerComboBox = new JComboBox<>();
    private JComboBox<Medicine> medicineComboBox = new JComboBox<>();
    private JTextField amountTextField = new JTextField();

    private JTable events = new JTable();
    {
        events.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 2)
                    return;

                Container tmp = getParent();
                while (!(tmp instanceof MainFrame))
                    tmp = tmp.getParent();
                MainFrame parentFrame = (MainFrame) tmp;

                int row = events.getSelectedRow();
                Event selected = (Event) events.getValueAt(row, 1);
                new EventCreateDialog(parentFrame, selected);

                Object[] data = {
                        selected.getDate(),
                        selected
                };
                ((DefaultTableModel) events.getModel()).removeRow(row);
                ((DefaultTableModel) events.getModel()).insertRow(row, data);
            }
        });
    }

    private Order orderObject;

    private LinkedList<Event> eventsToAdd = new LinkedList<>();
    private ArrayList<Event> eventsToDelete = new ArrayList<>();

    private JButton addButton = new JButton("Add");
    {
        addButton.addActionListener(e -> {
            Container tmp = getParent();
            while (!(tmp instanceof MainFrame))
                tmp = tmp.getParent();
            MainFrame parentFrame = (MainFrame) tmp;

            int size = eventsToAdd.size();
            new EventCreateDialog(parentFrame, orderObject, eventsToAdd);

            if (eventsToAdd.size() > size) {
                Object[] data = {
                        eventsToAdd.getLast().getDate(),
                        eventsToAdd.getLast()
                };
                ((DefaultTableModel) events.getModel()).addRow(data);
            }

            repaint();
        });
    }

    private JButton deleteButton = new JButton("Delete");
    {
        deleteButton.addActionListener(e -> {
            int row = events.getSelectedRow();
            if (row == -1)
                return;

            Event selected = (Event) events.getValueAt(row, 1);
            if (eventsToAdd.contains(selected))
                eventsToAdd.remove(selected);
            else
                eventsToDelete.add(selected);

            ((DefaultTableModel) events.getModel()).removeRow(row);
        });
    }

    private JButton applyButton = new JButton("Apply");
    {
        applyButton.addActionListener(e -> {
            orderObject.updateValues(((Customer) customerComboBox.getSelectedItem()).getId(),
                    ((Medicine) medicineComboBox.getSelectedItem()).getId(),
                    Integer.decode(amountTextField.getText()));
            orderObject.getEvents().addAll(eventsToAdd);
            eventsToDelete.forEach(DBObject::delete);

            getParent().getParent().repaint();
        });
    }

    private JButton cancelButton = new JButton("Cancel");
    {
        cancelButton.addActionListener(e -> showOrder(orderObject));
    }

    OrderView(TableNode<Customer> customersNode, TableNode<Medicine> medicinesNode) {
        this.customersNode = customersNode;
        this.medicinesNode = medicinesNode;

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

//        ------   order   ------
        JLabel __orderLabel = new JLabel("order");

        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy = 0;
        constraints.weightx = 0.1;
        constraints.weighty = 0;

        add(__orderLabel, constraints);

        constraints.weightx = 1;
        constraints.gridwidth = 6;

        add(orderLabel, constraints);

//        ------   customer   ------
        JLabel customerLabel = new JLabel("customer");

        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.gridwidth = 1;

        add(customerLabel, constraints);

        constraints.gridwidth = 6;

        add(customerComboBox, constraints);

//        ------   medicine   ------
        JLabel medicineLabel = new JLabel("medicine");

        constraints.gridy = 2;
        constraints.weightx = 0;
        constraints.gridwidth = 1;

        add(medicineLabel, constraints);

        constraints.gridwidth = 6;

        add(medicineComboBox, constraints);

//        ------   amount   ------
        JLabel amountLabel = new JLabel("amount");

        constraints.gridy = 3;
        constraints.gridwidth = 1;

        add(amountLabel, constraints);

        constraints.gridwidth = 6;

        add(amountTextField, constraints);

//        ------   events   ------
        JScrollPane scrollPane = new JScrollPane(events);

        constraints.gridy = 4;
        constraints.weighty = 1;
        constraints.gridwidth = 7;

        add(scrollPane, constraints);

//        ------   events buttons   ------
        constraints.gridx = 4;
        constraints.gridy = 5;
        constraints.weightx = 0.1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);

        add(Box.createHorizontalGlue(), constraints);

        constraints.gridx = 5;

        add(addButton, constraints);

        constraints.gridx = 6;

        add(deleteButton, constraints);

//        ------   buttons   ------
        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.weightx = 0.5;
        constraints.gridwidth = 5;

        add(applyButton, constraints);

        constraints.gridx = 5;
        constraints.gridwidth = 3;

        add(cancelButton, constraints);
    }

    void showOrder(Order orderObject) {
        this.orderObject = orderObject;

        DefaultComboBoxModel<Customer> customerComboBoxModel = new DefaultComboBoxModel<>();
        for (TableNode<Customer>.DBNode dbNode : customersNode.getChildren()) {
            Customer customerObject = dbNode.object;
            if (customerObject.getId() == null)
                continue;

            customerComboBoxModel.addElement(customerObject);
            if (customerObject.getId().equals(orderObject.getIdCustomer()))
                customerComboBoxModel.setSelectedItem(customerObject);
        }

        DefaultComboBoxModel<Medicine> medicineComboBoxModel = new DefaultComboBoxModel<>();
        for (TableNode<Medicine>.DBNode dbNode : medicinesNode.getChildren()) {
            Medicine medicineObject = dbNode.object;
            if (medicineObject.getId() == null)
                continue;

            medicineComboBoxModel.addElement(medicineObject);
            if (medicineObject.getId().equals(orderObject.getIdMedicine()))
                medicineComboBoxModel.setSelectedItem(medicineObject);
        }

        orderLabel.setText(orderObject.toString());
        customerComboBox.setModel(customerComboBoxModel);
        medicineComboBox.setModel(medicineComboBoxModel);
        amountTextField.setText(orderObject.getAmount().toString());

        eventsToAdd.clear();
        eventsToDelete.clear();

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        events.setModel(tableModel);

        tableModel.addColumn("date");
        tableModel.addColumn("event");

        for (Event event : orderObject.getEvents()) {
            if (event.isDelete())
                continue;

            Object[] data = {
                    event.getDate(),
                    event
            };
            tableModel.addRow(data);
        }
    }
}
