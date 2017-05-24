package ru.nsu.fit.g14203.popov.view;

import ru.nsu.fit.g14203.popov.drugstore.database.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.awt.*;
import java.sql.SQLException;
import java.util.function.Function;

class TreePanel extends JPanel {

    private static class DBNode<T extends DBObject> extends DefaultMutableTreeNode {
        T object;
        Function<T, String> getName;

        DBNode(T object, Function<T, String> getName) {
            this.object = object;
            this.getName = getName;
        }

        @Override
        public String toString() {
            return getName.apply(object);
        }
    }

    private final static Function<Type, String> TYPE_NAMER = Type::getName;
    private final static Function<Medicine, String> MEDICINE_NAMER = Medicine::getName;
    private final static Function<Customer, String> CUSTOMER_NAMER = Customer::getName;
    private final static Function<Order, String> ORDER_NAMER = order -> order.getId().toString();
    private final static Function<Schema, String> SCHEMA_NAMER = schema -> schema.getId().toString();

    private JTree tree;
    private DefaultMutableTreeNode root             = new DefaultMutableTreeNode("Drugstore");

    private DefaultMutableTreeNode typesNode        = new DefaultMutableTreeNode("Types");
    private DefaultMutableTreeNode medicinesNode    = new DefaultMutableTreeNode("Medicines");
    private DefaultMutableTreeNode customersNode    = new DefaultMutableTreeNode("Customers");
    private DefaultMutableTreeNode ordersNode       = new DefaultMutableTreeNode("Orders");
    private DefaultMutableTreeNode schemasNode      = new DefaultMutableTreeNode("Schemas");

    TreePanel() throws SQLException {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        tree = new JTree(root);

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane);

        root.add(typesNode);
        root.add(medicinesNode);
        root.add(customersNode);
        root.add(ordersNode);
        root.add(schemasNode);

        reload();
    }

    void reload() throws SQLException {
        typesNode.removeAllChildren();
        for (Type type : Type.loadFromDataBase()) {
            typesNode.add(new DBNode<>(type, TYPE_NAMER));
        }

        medicinesNode.removeAllChildren();
        for (Medicine medicine : Medicine.loadFromDataBase()) {
            medicinesNode.add(new DBNode<>(medicine, MEDICINE_NAMER));
        }

        customersNode.removeAllChildren();
        for (Customer customer : Customer.loadFromDataBase()) {
            customersNode.add(new DBNode<>(customer, CUSTOMER_NAMER));
        }

        ordersNode.removeAllChildren();
        for (Order order : Order.loadFromDataBase()) {
            ordersNode.add(new DBNode<>(order, ORDER_NAMER));
        }

        schemasNode.removeAllChildren();
        for (Schema schema : Schema.loadFromDataBase()) {
            schemasNode.add(new DBNode<>(schema, SCHEMA_NAMER));
        }

        SwingUtilities.invokeLater(this::repaint);
    }
}
