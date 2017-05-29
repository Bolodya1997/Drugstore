package ru.nsu.fit.g14203.popov.drugstore.view;

import com.sun.istack.internal.NotNull;
import ru.nsu.fit.g14203.popov.drugstore.database.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Collections;
import java.util.List;

class TableNode<T extends DBObject> extends DefaultMutableTreeNode {

    class DBNode extends DefaultMutableTreeNode {
        T object;

        DBNode(@NotNull T object) {
            this.object = object;
        }

        void delete() {
            if (object.isInsert())
                parent.remove(this);
            else
                object.delete();
        }

        @Override
        public String toString() {
            return object.toString();
        }
    }

    private enum Table {
        CUSTOMER,
        MEDICINE,
        ORDER,
        SCHEMA,
        TYPE
    }

    private Table table;
    private Class<T> __class;

    TableNode(Class<T> __class) {
        this.__class = __class;

        if (__class == Customer.class)
            table = Table.CUSTOMER;
        else if (__class == Medicine.class)
            table = Table.MEDICINE;
        else if (__class == Order.class)
            table = Table.ORDER;
        else if (__class == Schema.class)
            table = Table.SCHEMA;
        else if (__class == Type.class)
            table = Table.TYPE;
        else
            throw new IllegalArgumentException(String.format("unexpected type %s", __class));
    }

    void add(T child) {
        add(new DBNode(child));
    }

    TreePath newChild() {
        T child;
        try {
            child = __class.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        add(child);
        return new TreePath(getLastLeaf().getPath());
    }

    TreePath getTreePath() {
        return new TreePath(getPath());
    }

    List<DBNode> getChildren() {
        return Collections.list(children());
    }

    @Override
    public String toString() {
        switch (table) {
            case CUSTOMER:
                return "Customers";
            case MEDICINE:
                return "Medicines";
            case ORDER:
                return "Orders";
            case SCHEMA:
                return "Schemas";
            case TYPE:
                return "Types";
        }

        return "";
    }
}