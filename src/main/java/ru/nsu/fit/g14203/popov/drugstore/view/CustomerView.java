package ru.nsu.fit.g14203.popov.drugstore.view;

import ru.nsu.fit.g14203.popov.drugstore.database.Customer;
import ru.nsu.fit.g14203.popov.drugstore.database.Medicine;
import ru.nsu.fit.g14203.popov.drugstore.database.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class CustomerView extends JPanel {

    private TableNode<Order> ordersNode;
    private TableNode<Medicine> medicinesNode;

    private JTextField nameTextField = new JTextField();
    private JTextField phoneTextField = new JTextField();
    private JTextField addressTextField = new JTextField();

    private JTable orders = new JTable();
    {
        orders.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 2)
                    return;

                TableNode.DBNode dbNode = (TableNode.DBNode) orders.getModel()
                        .getValueAt(orders.getSelectedRow(), 0);

                Container tmp = getParent();
                while (!(tmp instanceof MainFrame))
                    tmp = tmp.getParent();

                ((MainFrame) tmp).showObjectNode(dbNode);
            }
        });
    }

    private Customer customerObject;

    private JButton applyButton = new JButton("Apply");
    {
        applyButton.addActionListener(e -> {
            customerObject.updateValues(nameTextField.getText(),
                    phoneTextField.getText(),
                    addressTextField.getText());

            getParent().getParent().repaint();
        });
    }

    private JButton cancelButton = new JButton("Cancel");
    {
        cancelButton.addActionListener(e -> showCustomer(customerObject));
    }

    CustomerView(TableNode<Order> ordersNode,
                 TableNode<Medicine> medicinesNode) {
        this.ordersNode = ordersNode;
        this.medicinesNode = medicinesNode;

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

//        ------   name   ------
        JLabel nameLabel = new JLabel("name");

        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy = 0;
        constraints.weightx = 0.1;
        constraints.weighty = 0;

        add(nameLabel, constraints);

        constraints.weightx = 1;
        constraints.gridwidth = 2;

        add(nameTextField, constraints);

//        ------   phone   ------
        JLabel phoneLabel = new JLabel("phone_number");

        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.gridwidth = 1;

        add(phoneLabel, constraints);

        constraints.gridwidth = 2;

        add(phoneTextField, constraints);

//        ------   address   ------
        JLabel addressLabel = new JLabel("address");

        constraints.gridy = 2;
        constraints.gridwidth = 1;

        add(addressLabel, constraints);

        constraints.gridwidth = 2;

        add(addressTextField, constraints);

//        ------   orders   ------
        JScrollPane scrollPane = new JScrollPane(orders);

        constraints.gridy = 3;
        constraints.weighty = 1;
        constraints.gridwidth = 3;

        add(scrollPane, constraints);

//        ------   buttons   ------
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.weightx = 0.5;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);

        add(applyButton, constraints);

        constraints.gridx = 2;

        add(cancelButton, constraints);
    }

    void showCustomer(Customer customerObject) {
        this.customerObject = customerObject;

        nameTextField.setText(customerObject.getName());
        phoneTextField.setText(customerObject.getPhoneNumber());
        addressTextField.setText(customerObject.getAddress());

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orders.setModel(tableModel);

        tableModel.addColumn("order");
        tableModel.addColumn("medicine");
        tableModel.addColumn(Order.AMOUNT);
        tableModel.addColumn("status");

        for (TableNode<Order>.DBNode dbNode : ordersNode.getChildren()) {
            Order orderObject = dbNode.object;
            if (!orderObject.getIdCustomer().equals(customerObject.getId()))
                continue;

            Object[] data = {
                    dbNode,
                    TableView.medicineIdToMedicine(medicinesNode, orderObject.getIdMedicine()),
                    orderObject.getAmount(),
                    orderObject.getEvents().getLast()
            };
            tableModel.addRow(data);
        }
    }
}
