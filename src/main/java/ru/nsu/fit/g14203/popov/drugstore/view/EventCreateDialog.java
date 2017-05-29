package ru.nsu.fit.g14203.popov.drugstore.view;

import ru.nsu.fit.g14203.popov.drugstore.database.Event;
import ru.nsu.fit.g14203.popov.drugstore.database.Order;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class EventCreateDialog extends JDialog {

    private JComboBox<String> typeComboBox = new JComboBox<>();
    {
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        typeComboBox.setModel(comboBoxModel);

        comboBoxModel.addElement(Event.TYPE_OPEN);
        comboBoxModel.addElement(Event.TYPE_WAIT);
        comboBoxModel.addElement(Event.TYPE_PROCESS);
        comboBoxModel.addElement(Event.TYPE_READY);
        comboBoxModel.addElement(Event.TYPE_CLOSE);
    }

    private Event eventObject;

    private JButton OKButton = new JButton("OK");
    {
        OKButton.addActionListener(e -> {
            eventObject.updateValues(eventObject.getIdOrder(), (String) typeComboBox.getSelectedItem());
            dispose();
        });
    }

    private JButton cancelButton = new JButton("Cancel");
    {
        cancelButton.addActionListener(e -> dispose());
    }

    private EventCreateDialog(JFrame owner) {
        super(owner, "Event", true);

        setSize(300, 200);
        setResizable(false);
        setLocationRelativeTo(owner);

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

//        ------   type   ------
        JLabel typeLabel = new JLabel("type");

        constraints.gridx = GridBagConstraints.RELATIVE;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(5, 5, 5, 5);

        add(typeLabel, constraints);
        add(typeComboBox, constraints);

//        ------   buttons   ------
        constraints.gridy = 1;

        add(OKButton, constraints);
        add(cancelButton, constraints);
    }

    EventCreateDialog(JFrame owner, Order orderObject, List<Event> tmpEvents) {
        this(owner);

        eventObject = new Event(orderObject.getId(), "");
        OKButton.addActionListener(e -> tmpEvents.add(eventObject));

        setVisible(true);
    }

    EventCreateDialog(JFrame owner, Event eventObject) {
        this(owner);

        this.eventObject = eventObject;
        typeComboBox.setSelectedItem(eventObject.getType());

        setVisible(true);
    }
}
