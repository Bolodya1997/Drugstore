package ru.nsu.fit.g14203.popov.drugstore.view;

import ru.nsu.fit.g14203.popov.drugstore.database.Component;
import ru.nsu.fit.g14203.popov.drugstore.database.Medicine;
import ru.nsu.fit.g14203.popov.drugstore.database.Schema;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

class ComponentCreateDialog extends JDialog {

    private JComboBox<Medicine> medicineComboBox = new JComboBox<>();
    private JTextField amountTextField = new JTextField("0");

    private ru.nsu.fit.g14203.popov.drugstore.database.Component componentObject;

    private JButton OKButton = new JButton("OK");
    {
        OKButton.addActionListener(e -> {
            componentObject.updateValues(componentObject.getIdSchema(),
                    ((Medicine) medicineComboBox.getSelectedItem()).getId(),
                    Integer.decode(amountTextField.getText()));
            dispose();
        });
    }

    private JButton cancelButton = new JButton("Cancel");
    {
        cancelButton.addActionListener(e -> dispose());
    }

    private ComponentCreateDialog(JFrame owner, TableNode<Medicine> medicinesNode,
                                  TableNode<ru.nsu.fit.g14203.popov.drugstore.database.Type> typesNode) {
        super(owner, "Component", true);

        setSize(300, 200);
        setResizable(false);
        setLocationRelativeTo(owner);

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

//        ------   medicine   ------
        JLabel medicineLabel = new JLabel("medicine");

        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(5, 5, 5, 5);

        add(medicineLabel, constraints);

        DefaultComboBoxModel<Medicine> comboBoxModel = new DefaultComboBoxModel<>();
        for (TableNode<Medicine>.DBNode dbNode : medicinesNode.getChildren()) {
            Medicine medicineObject = dbNode.object;
            if (medicineObject.getId() == null)
                continue;

            if (!TableView.typeIdToType(typesNode, medicineObject.getIdType()).getMixAble())
                continue;

            comboBoxModel.addElement(dbNode.object);
        }
        medicineComboBox.setModel(comboBoxModel);

        add(medicineComboBox, constraints);

//        ------   amount   ------
        JLabel amountLabel = new JLabel("amount");

        constraints.gridy = 1;

        add(amountLabel, constraints);
        add(amountTextField, constraints);

//        ------   buttons   ------
        constraints.gridy = 2;

        add(OKButton, constraints);
        add(cancelButton, constraints);
    }

    ComponentCreateDialog(JFrame owner, TableNode<Medicine> medicinesNode,
                          TableNode<ru.nsu.fit.g14203.popov.drugstore.database.Type> typesNode,
                          Schema schemaObject, java.util.List<Component> tmpComponents) {
        this(owner, medicinesNode, typesNode);

        componentObject = new Component(schemaObject.getId(), BigDecimal.ZERO, 0);
        OKButton.addActionListener(e -> tmpComponents.add(componentObject));

        setVisible(true);
    }

    ComponentCreateDialog(JFrame owner, TableNode<Medicine> medicinesNode,
                          TableNode<ru.nsu.fit.g14203.popov.drugstore.database.Type> typesNode,
                          Component componentObject) {
        this(owner, medicinesNode, typesNode);

        this.componentObject = componentObject;
        medicineComboBox.setSelectedItem(TableView.medicineIdToMedicine(medicinesNode,
                componentObject.getIdMedicine()));

        setVisible(true);
    }
}
