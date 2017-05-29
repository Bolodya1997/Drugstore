package ru.nsu.fit.g14203.popov.drugstore.view;

import ru.nsu.fit.g14203.popov.drugstore.database.Medicine;
import ru.nsu.fit.g14203.popov.drugstore.database.Schema;
import ru.nsu.fit.g14203.popov.drugstore.database.Type;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class MedicineView extends JPanel {

    private TableNode<Type> typesNode;
    private TableNode<Schema> schemasNode;

    private JTextField medicineTextField = new JTextField();
    private JComboBox<Type> typeComboBox = new JComboBox<>();
    private JTextField priceTextField = new JTextField();
    private JTextField amountTextField = new JTextField();
    private JTextField minimumTextField = new JTextField();
    private JCheckBox complexCheckBox = new JCheckBox("is_complex");

    private JTable schemas = new JTable();
    {
        schemas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 2)
                    return;

                TableNode.DBNode dbNode = (TableNode.DBNode) schemas.getModel()
                        .getValueAt(schemas.getSelectedRow(), 0);

                Container tmp = getParent();
                while (!(tmp instanceof MainFrame))
                    tmp = tmp.getParent();

                ((MainFrame) tmp).showObjectNode(dbNode);
            }
        });
    }

    private Medicine medicineObject;

    private JButton applyButton = new JButton("Apply");
    {
        applyButton.addActionListener(e -> {
            medicineObject.updateValues(((Type) typeComboBox.getSelectedItem()).getId(),
                    medicineTextField.getText(),
                    Integer.decode(priceTextField.getText()),
                    Integer.decode(minimumTextField.getText()),
                    Integer.decode(amountTextField.getText()),
                    complexCheckBox.isSelected());

            getParent().getParent().repaint();
        });
    }

    private JButton cancelButton = new JButton("Cancel");
    {
        cancelButton.addActionListener(e -> showMedicine(medicineObject));
    }

    MedicineView(TableNode<Type> typesNode,
                 TableNode<Schema> schemasNode) {
        this.typesNode = typesNode;
        this.schemasNode = schemasNode;

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

//        ------   name   ------
        JLabel medicineLabel = new JLabel("medicine");

        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy = 0;
        constraints.weightx = 0.1;
        constraints.weighty = 0;

        add(medicineLabel, constraints);

        constraints.weightx = 1;
        constraints.gridwidth = 2;

        add(medicineTextField, constraints);

//        ------   type   ------
        JLabel typeLabel = new JLabel("type");

        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.gridwidth = 1;

        add(typeLabel, constraints);

        constraints.gridwidth = 2;

        add(typeComboBox, constraints);

//        ------   price   ------
        JLabel priceLabel = new JLabel("price");

        constraints.gridy = 2;
        constraints.gridwidth = 1;

        add(priceLabel, constraints);

        constraints.gridwidth = 2;

        add(priceTextField, constraints);

//        ------   amount   ------
        JLabel amountLabel = new JLabel("amount");

        constraints.gridy = 3;
        constraints.gridwidth = 1;

        add(amountLabel, constraints);

        constraints.gridwidth = 2;

        add(amountTextField, constraints);

//        ------   minimum   ------
        JLabel minimumLabel = new JLabel("minimum");

        constraints.gridy = 4;
        constraints.gridwidth = 1;

        add(minimumLabel, constraints);

        constraints.gridwidth = 2;

        add(minimumTextField, constraints);

//        ------   complex   ------
        constraints.gridy = 5;
        constraints.gridwidth = 2;

        add(complexCheckBox, constraints);

//        ------   schemas   ------
        JScrollPane scrollPane = new JScrollPane(schemas);

        constraints.gridy = 6;
        constraints.weighty = 1;
        constraints.gridwidth = 3;

        add(scrollPane, constraints);

//        ------   buttons   ------
        constraints.gridx = 1;
        constraints.gridy = 7;
        constraints.weightx = 0.5;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);

        add(applyButton, constraints);

        constraints.gridx = 2;

        add(cancelButton, constraints);
    }

    void showMedicine(Medicine medicineObject) {
        this.medicineObject = medicineObject;

        DefaultComboBoxModel<Type> comboBoxModel = new DefaultComboBoxModel<>();
        for (TableNode<Type>.DBNode dbNode : typesNode.getChildren()) {
            Type typeObject = dbNode.object;
            if (typeObject.getId() == null)
                continue;

            comboBoxModel.addElement(typeObject);
            if (typeObject.getId().equals(medicineObject.getIdType()))
                comboBoxModel.setSelectedItem(typeObject);
        }

        medicineTextField.setText(medicineObject.getName());
        typeComboBox.setModel(comboBoxModel);
        priceTextField.setText(medicineObject.getPrice().toString());
        amountTextField.setText(medicineObject.getAmount().toString());
        minimumTextField.setText(medicineObject.getMinimum().toString());
        complexCheckBox.setSelected(medicineObject.getComplex());

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        schemas.setModel(tableModel);

        tableModel.addColumn("schema");
        tableModel.addColumn("time");

        for (TableNode<Schema>.DBNode dbNode : schemasNode.getChildren()) {
            Schema schemaObject = dbNode.object;
            if (schemaObject.getId() == null)
                continue;

            if (!schemaObject.getIdMedicine().equals(medicineObject.getId()))
                continue;

            Object[] data = {
                    dbNode,
                    schemaObject.getTime()
            };
            tableModel.addRow(data);
        }
    }
}
