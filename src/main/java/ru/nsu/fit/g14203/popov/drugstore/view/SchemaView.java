package ru.nsu.fit.g14203.popov.drugstore.view;

import ru.nsu.fit.g14203.popov.drugstore.database.*;
import ru.nsu.fit.g14203.popov.drugstore.database.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

class SchemaView extends JPanel {

    private static class TimeSpinner extends JPanel {

        JSpinner daySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9, 1));
        JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        JSpinner secondSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

        TimeSpinner() {
            add(daySpinner);
            add(hourSpinner);
            add(minuteSpinner);
            add(secondSpinner);
        }

        void setValue(String value) {   //  d h:m:s.x
            int[] date = { 0, 0, 0, 0 };
            if (value != null) {
                date = Arrays.stream(value.replaceAll("\\..*", "").split(" "))
                        .flatMap(s -> Arrays.stream(s.split(":")))
                        .filter(s -> !s.isEmpty())
                        .mapToInt(Integer::decode)
                        .toArray();
            }

            daySpinner.setValue(date[0]);
            hourSpinner.setValue(date[1]);
            minuteSpinner.setValue(date[2]);
            secondSpinner.setValue(date[3]);
        }

        @Override
        public String toString() {
            int day = (int) daySpinner.getValue();
            int hour = (int) hourSpinner.getValue();
            int minute = (int) minuteSpinner.getValue();
            int second = (int) secondSpinner.getValue();

            return String.format("%d %02d:%02d:%02d.0", day, hour, minute, second);
        }
    }

    private TableNode<Type>     typesNode;
    private TableNode<Medicine> medicinesNode;

    private JLabel schemaLabel = new JLabel();
    private JComboBox<Medicine> medicineComboBox = new JComboBox<>();
    private TimeSpinner timeSpinner = new TimeSpinner();
    private JTextArea descriptionTextArea = new JTextArea();
    {
        descriptionTextArea.setEditable(true);
        descriptionTextArea.setLineWrap(true);
    }

    private JTable components = new JTable();
    {
        components.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || e.getClickCount() != 2)
                    return;

                Container tmp = getParent();
                while (!(tmp instanceof MainFrame))
                    tmp = tmp.getParent();
                MainFrame parentFrame = (MainFrame) tmp;

                int row = components.getSelectedRow();
                Component selected = (Component) components.getValueAt(row, 1);
                new ComponentCreateDialog(parentFrame, medicinesNode, typesNode, selected);

                Object[] data = {
                        TableView.medicineIdToMedicine(medicinesNode, selected.getIdMedicine()),
                        selected
                };
                ((DefaultTableModel) components.getModel()).removeRow(row);
                ((DefaultTableModel) components.getModel()).insertRow(row, data);
            }
        });
    }

    private Schema schemaObject;

    private LinkedList<Component> componentsToAdd = new LinkedList<>();
    private ArrayList<Component> componentsToDelete = new ArrayList<>();

    private JButton addButton = new JButton("Add");
    {
        addButton.addActionListener(e -> {
            Container tmp = getParent();
            while (!(tmp instanceof MainFrame))
                tmp = tmp.getParent();
            MainFrame parentFrame = (MainFrame) tmp;

            int size = componentsToAdd.size();
            new ComponentCreateDialog(parentFrame, medicinesNode, typesNode,
                    schemaObject, componentsToAdd);
            if (componentsToAdd.size() > size) {
                Object[] data = {
                        TableView.medicineIdToMedicine(medicinesNode,
                                componentsToAdd.getLast().getIdMedicine()),
                        componentsToAdd.getLast()
                };
                ((DefaultTableModel) components.getModel()).addRow(data);
            }

            repaint();
        });
    }

    private JButton deleteButton = new JButton("Delete");
    {
        deleteButton.addActionListener(e -> {
            int row = components.getSelectedRow();
            if (row == -1)
                return;

            Component selected = (Component) components.getValueAt(row, 1);
            if (componentsToAdd.contains(selected))
                componentsToAdd.remove(selected);
            else
                componentsToDelete.add(selected);

            ((DefaultTableModel) components.getModel()).removeRow(row);
        });
    }

    private JButton applyButton = new JButton("Apply");
    {
        applyButton.addActionListener(e -> {
            schemaObject.updateValues(((Medicine) medicineComboBox.getSelectedItem()).getId(),
                    descriptionTextArea.getText(),
                    timeSpinner.toString());
            schemaObject.getComponents().addAll(componentsToAdd);
            componentsToDelete.forEach(DBObject::delete);

            getParent().getParent().repaint();
        });
    }

    private JButton cancelButton = new JButton("Cancel");
    {
        cancelButton.addActionListener(e -> showSchema(schemaObject));
    }

    SchemaView(TableNode<Type> typesNode,
               TableNode<Medicine> medicinesNode) {
        this.typesNode = typesNode;
        this.medicinesNode = medicinesNode;

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

//        ------   schema   ------
        JLabel __schemaLabel = new JLabel("schema");

        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy = 0;
        constraints.weightx = 0.1;
        constraints.weighty = 0;

        add(__schemaLabel, constraints);

        constraints.weightx = 1;
        constraints.gridwidth = 6;

        add(schemaLabel, constraints);

//        ------   medicine   ------
        JLabel medicineLabel = new JLabel("medicine");

        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.gridwidth = 1;

        add(medicineLabel, constraints);

        constraints.gridwidth = 6;

        add(medicineComboBox, constraints);

//        ------   time   ------
        JLabel timeLabel = new JLabel("time");

        constraints.gridy = 2;
        constraints.weightx = 0;
        constraints.gridwidth = 1;

        add(timeLabel, constraints);

        constraints.gridwidth = 6;

        add(timeSpinner, constraints);

//        ------   description   ------
        JLabel descriptionLabel = new JLabel("description");

        constraints.gridy = 3;
        constraints.weightx = 0;
        constraints.gridwidth = 1;

        add(descriptionLabel, constraints);

        constraints.weighty = 0.5;
        constraints.gridwidth = 6;

        add(new JScrollPane(descriptionTextArea), constraints);

//        ------   components   ------
        JScrollPane scrollPane = new JScrollPane(components);

        constraints.gridy = 5;
        constraints.weighty = 1;
        constraints.gridwidth = 7;

        add(scrollPane, constraints);

//        ------   components buttons   ------
        constraints.gridx = 4;
        constraints.gridy = 6;
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
        constraints.gridy = 7;
        constraints.weightx = 0.5;
        constraints.gridwidth = 5;

        add(applyButton, constraints);

        constraints.gridx = 5;
        constraints.gridwidth = 3;

        add(cancelButton, constraints);
    }

    void showSchema(Schema schemaObject) {
        this.schemaObject = schemaObject;

        DefaultComboBoxModel<Medicine> comboBoxModel = new DefaultComboBoxModel<>();
        for (TableNode<Medicine>.DBNode dbNode : medicinesNode.getChildren()) {
            Medicine medicineObject = dbNode.object;
            if (medicineObject.getId() == null || !medicineObject.getComplex())
                continue;

            comboBoxModel.addElement(medicineObject);
            if (medicineObject.getId().equals(schemaObject.getIdMedicine()))
                comboBoxModel.setSelectedItem(medicineObject);
        }

        schemaLabel.setText(schemaObject.toString());
        medicineComboBox.setModel(comboBoxModel);
        timeSpinner.setValue(schemaObject.getTime());
        descriptionTextArea.setText(schemaObject.getDescription());

        componentsToAdd.clear();
        componentsToDelete.clear();

        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        components.setModel(tableModel);

        tableModel.addColumn("component");
        tableModel.addColumn("amount");

        for (Component component : schemaObject.getComponents()) {
            if (component.isDelete())
                continue;

            Object[] data = {
                    TableView.medicineIdToMedicine(medicinesNode, component.getIdMedicine()),
                    component
            };
            tableModel.addRow(data);
        }
    }
}
