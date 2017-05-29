package ru.nsu.fit.g14203.popov.drugstore.view;

import ru.nsu.fit.g14203.popov.drugstore.database.*;

import javax.swing.*;

class ObjectPanel extends JPanel {

    private TypeView        typeView;
    private MedicineView    medicineView;
    private CustomerView    customerView;
    private OrderView       orderView;
    private SchemaView      schemaView;

    ObjectPanel (TableNode<Type> typesNode,
                 TableNode<Medicine> medicinesNode,
                 TableNode<Customer> customersNode,
                 TableNode<Order> ordersNode,
                 TableNode<Schema> schemasNode) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        typeView = new TypeView(medicinesNode);
        typeView.setVisible(false);
        add(typeView);

        medicineView = new MedicineView(typesNode, schemasNode);
        medicineView.setVisible(false);
        add(medicineView);

        customerView = new CustomerView(ordersNode, medicinesNode);
        customerView.setVisible(false);
        add(customerView);

        orderView = new OrderView(customersNode, medicinesNode);
        orderView.setVisible(false);
        add(orderView);

        schemaView = new SchemaView(typesNode, medicinesNode);
        schemaView.setVisible(false);
        add(schemaView);
    }

    void showObject(DBObject object) {
        typeView.setVisible(false);
        medicineView.setVisible(false);
        customerView.setVisible(false);
        orderView.setVisible(false);
        schemaView.setVisible(false);

        if (object instanceof Type) {
            typeView.setVisible(true);
            typeView.showType((Type) object);

        } else if (object instanceof Medicine) {
            medicineView.setVisible(true);
            medicineView.showMedicine((Medicine) object);

        } else if (object instanceof Customer) {
            customerView.setVisible(true);
            customerView.showCustomer((Customer) object);

        } else if (object instanceof Order) {
            orderView.setVisible(true);
            orderView.showOrder((Order) object);

        } else if (object instanceof Schema) {
            schemaView.setVisible(true);
            schemaView.showSchema((Schema) object);

        } else {
            throw new IllegalArgumentException(String.format("unexpected type %s", object.getClass()));
        }
    }
}
