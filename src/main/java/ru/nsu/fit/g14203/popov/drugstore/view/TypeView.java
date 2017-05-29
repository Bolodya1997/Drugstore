package ru.nsu.fit.g14203.popov.drugstore.view;

import ru.nsu.fit.g14203.popov.drugstore.database.Medicine;
import ru.nsu.fit.g14203.popov.drugstore.database.Type;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class TypeView extends JPanel {

    private TableNode<Medicine> medicinesNode;

    private JTextField typeTextField = new JTextField();
    private JCheckBox innerCheckBox = new JCheckBox("inner_able");
    private JCheckBox outerCheckBox = new JCheckBox("outer_able");
    private JCheckBox mixCheckBox = new JCheckBox("mix_able");

    private JTable medicines = new JTable();
    {
        medicines.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 2)
                    return;

                TableNode.DBNode dbNode = (TableNode.DBNode) medicines.getModel()
                        .getValueAt(medicines.getSelectedRow(), 0);

                Container tmp = getParent();
                while (!(tmp instanceof MainFrame))
                    tmp = tmp.getParent();

                ((MainFrame) tmp).showObjectNode(dbNode);
            }
        });
    }

    private Type typeObject;

    private JButton applyButton = new JButton("Apply");
    {
        applyButton.addActionListener(e -> {
            typeObject.updateValues(typeTextField.getText(),
                    innerCheckBox.isSelected(),
                    outerCheckBox.isSelected(),
                    mixCheckBox.isSelected());

            getParent().getParent().repaint();
        });
    }

    private JButton cancelButton = new JButton("Cancel");
    {
        cancelButton.addActionListener(e -> showType(typeObject));
    }

    TypeView(TableNode<Medicine> medicinesNode) {
        this.medicinesNode = medicinesNode;

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

//        ------   type   ------
        JLabel typeLabel = new JLabel("type");

        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy = 0;
        constraints.weightx = 0.1;
        constraints.weighty = 0;

        add(typeLabel, constraints);

        constraints.weightx = 1;
        constraints.gridwidth = 2;

        add(typeTextField, constraints);

//        ------   inner   ------
        constraints.gridy = 1;

        add(innerCheckBox, constraints);

//        ------   outer   ------
        add(outerCheckBox, constraints);

//        ------   mix   ------
        constraints.gridy = 2;

        add(mixCheckBox, constraints);

//        ------   medicines   ------
        JScrollPane scrollPane = new JScrollPane(medicines);

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

    void showType(Type typeObject) {
        this.typeObject = typeObject;

        typeTextField.setText(typeObject.getName());
        innerCheckBox.setSelected(typeObject.getInnerAble());
        outerCheckBox.setSelected(typeObject.getOuterAble());
        mixCheckBox.setSelected(typeObject.getMixAble());

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medicines.setModel(tableModel);

        tableModel.addColumn("medicine");
        tableModel.addColumn(Medicine.PRICE);
        tableModel.addColumn(Medicine.AMOUNT);
        tableModel.addColumn(Medicine.MINIMUM);
        tableModel.addColumn(Medicine.IS_COMPLEX);

        for (TableNode<Medicine>.DBNode dbNode : medicinesNode.getChildren()) {
            Medicine medicineObject = dbNode.object;
            if (!medicineObject.getIdType().equals(typeObject.getId()))
                continue;

            Object[] data = {
                    dbNode,
                    medicineObject.getPrice(),
                    medicineObject.getAmount(),
                    medicineObject.getMinimum(),
                    medicineObject.getComplex()
            };
            tableModel.addRow(data);
        }
    }
}
