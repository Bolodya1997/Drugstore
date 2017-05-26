package ru.nsu.fit.g14203.popov.view;

import ru.nsu.fit.g14203.popov.drugstore.database.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

class TreePanel extends JPanel {

    private DefaultMutableTreeNode root         = new DefaultMutableTreeNode("Drugstore");

    TableNode<Type> typesNode           = new TableNode<>(Type.class);
    TableNode<Medicine> medicinesNode   = new TableNode<>(Medicine.class);
    TableNode<Customer> customersNode   = new TableNode<>(Customer.class);
    TableNode<Order> ordersNode         = new TableNode<>(Order.class);
    TableNode<Schema> schemasNode       = new TableNode<>(Schema.class);

    private JTree tree = new JTree(root);
    {
        root.add(typesNode);
        root.add(medicinesNode);
        root.add(customersNode);
        root.add(ordersNode);
        root.add(schemasNode);
    }

    private TableNode<DBObject> selectedTable;
    private TableNode.DBNode selectedNode;

    private ActionListener reloadAction = e -> {
        try {
            reload();
        } catch (SQLException e1) {
            throw new RuntimeException(e1);
        }
    };

    private ActionListener commitAction = e -> {
        try {
            commit();
        } catch (SQLException e1) {
            throw new RuntimeException(e1);
        }
    };

    private ActionListener newAction = e -> {
        TreePath path = selectedTable.newChild();

        reloadNode(selectedTable);
        tree.setSelectionPath(path);
    };

    private ActionListener deleteAction = e -> {
        TreeNode parent = selectedNode.getParent();
        selectedNode.delete();

        reloadNode(parent);
    };

    private JPopupMenu rootMenu = new JPopupMenu();
    {
        JMenuItem reloadMenuItem = new JMenuItem("Reload");
        reloadMenuItem.addActionListener(reloadAction);
        rootMenu.add(reloadMenuItem);

        JMenuItem commitMenuItem = new JMenuItem("Commit");
        commitMenuItem.addActionListener(commitAction);
        rootMenu.add(commitMenuItem);
    }

    private JPopupMenu tableMenu = new JPopupMenu();
    {
        JMenuItem reloadMenuItem = new JMenuItem("Reload");
        reloadMenuItem.addActionListener(reloadAction);
        tableMenu.add(reloadMenuItem);

        JMenuItem commitMenuItem = new JMenuItem("Commit");
        commitMenuItem.addActionListener(commitAction);
        tableMenu.add(commitMenuItem);

        tableMenu.addSeparator();

        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(newAction);
        tableMenu.add(newMenuItem);
    }

    private JPopupMenu nodeMenu = new JPopupMenu();
    {
        JMenuItem reloadMenuItem = new JMenuItem("Reload");
        reloadMenuItem.addActionListener(reloadAction);
        nodeMenu.add(reloadMenuItem);

        JMenuItem commitMenuItem = new JMenuItem("Commit");
        commitMenuItem.addActionListener(commitAction);
        nodeMenu.add(commitMenuItem);

        nodeMenu.addSeparator();

        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(newAction);
        nodeMenu.add(newMenuItem);

        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.addActionListener(deleteAction);
        nodeMenu.add(deleteMenuItem);
    }

    TreePanel(Consumer<DBObject> showObject, Consumer<TableNode> showTable) throws SQLException {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        tree.expandRow(0);
        tree.setExpandsSelectedPaths(true);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON3)
                    return;

                TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
                if (path == null || !tree.getPathBounds(path).contains(e.getPoint()))
                    return;


                if (path.getLastPathComponent() == root) {
                    rootMenu.show(TreePanel.this, e.getX(), e.getY());
                } else if (path.getLastPathComponent() instanceof TableNode) {
                    selectedTable = (TableNode<DBObject>) path.getLastPathComponent();
                    tableMenu.show(TreePanel.this, e.getX(), e.getY());
                } else if (path.getLastPathComponent() instanceof TableNode.DBNode) {
                    selectedNode = (TableNode.DBNode) path.getLastPathComponent();
                    selectedTable = (TableNode<DBObject>) selectedNode.getParent();
                    nodeMenu.show(TreePanel.this, e.getX(), e.getY());
                }
            }
        });
        tree.addTreeSelectionListener(e -> {
            Object node = e.getPath().getLastPathComponent();

            if (node instanceof TableNode.DBNode)
                showObject.accept(((TableNode.DBNode) node).object);
            else if (node instanceof TableNode)
                showTable.accept((TableNode) node);
        });

        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane);

        reload();
    }

    void reload() throws SQLException {
        typesNode.removeAllChildren();
        for (Type type : Type.loadFromDataBase()) {
            typesNode.add(type);
        }

        medicinesNode.removeAllChildren();
        for (Medicine medicine : Medicine.loadFromDataBase()) {
            medicinesNode.add(medicine);
        }

        customersNode.removeAllChildren();
        for (Customer customer : Customer.loadFromDataBase()) {
            customersNode.add(customer);
        }

        ordersNode.removeAllChildren();
        for (Order order : Order.loadFromDataBase()) {
            ordersNode.add(order);
        }

        schemasNode.removeAllChildren();
        for (Schema schema : Schema.loadFromDataBase()) {
            schemasNode.add(schema);
        }

        reloadNode(null);
    }

    void commit() throws SQLException {
        List children = typesNode.getChildren();
        for (Object child : children) {
            ((TableNode.DBNode) child).object.commit();
        }

        children = medicinesNode.getChildren();
        for (Object child : children) {
            ((TableNode.DBNode) child).object.commit();
        }

        children = customersNode.getChildren();
        for (Object child : children) {
            ((TableNode.DBNode) child).object.commit();
        }

        children = ordersNode.getChildren();
        for (Object child : children) {
            ((TableNode.DBNode) child).object.commit();
        }

        children = schemasNode.getChildren();
        for (Object child : children) {
            ((TableNode.DBNode) child).object.commit();
        }

        reload();
    }

    private void reloadNode(TreeNode node) {
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        if (node != null)
            treeModel.reload(node);
        else
            treeModel.reload();
    }
}
