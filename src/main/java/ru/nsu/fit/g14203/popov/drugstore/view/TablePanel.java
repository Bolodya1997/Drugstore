package ru.nsu.fit.g14203.popov.drugstore.view;

import ru.nsu.fit.g14203.popov.drugstore.database.*;

import javax.swing.*;
import java.util.function.Consumer;

class TablePanel extends JPanel {

    private TableView tableView;

    TablePanel(TableNode<Type> typesNode,
               TableNode<Medicine> medicinesNode,
               TableNode<Customer> customersNode,
               TableNode<Order> ordersNode,
               TableNode<Schema> schemasNode,
               Consumer<TableNode.DBNode> showObjectNode) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        tableView = new TableView(typesNode, medicinesNode, customersNode, ordersNode, schemasNode,
                showObjectNode);
        add(new JScrollPane(tableView));
    }

    void showTable(TableNode table) {
        tableView.showTable(table);
    }
}
