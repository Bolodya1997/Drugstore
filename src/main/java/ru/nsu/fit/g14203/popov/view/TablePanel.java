package ru.nsu.fit.g14203.popov.view;

import ru.nsu.fit.g14203.popov.drugstore.database.*;

import javax.swing.*;

class TablePanel extends JPanel {

    TableView tableView;

    TablePanel(TableNode<Type> typesNode,
               TableNode<Medicine> medicinesNode,
               TableNode<Customer> customersNode,
               TableNode<Order> ordersNode,
               TableNode<Schema> schemasNode) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        tableView = new TableView(typesNode, medicinesNode, customersNode, ordersNode, schemasNode);
        add(new JScrollPane(tableView));
    }
}
