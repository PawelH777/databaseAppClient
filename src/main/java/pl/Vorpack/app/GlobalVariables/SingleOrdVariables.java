package pl.Vorpack.app.GlobalVariables;

import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;

public class SingleOrdVariables {
    private static SingleOrders singleOrderObject = new SingleOrders();

    public static SingleOrders getSingleOrderObject() {
        return singleOrderObject;
    }

    public static void setSingleOrderObject(SingleOrders singleOrderObject) {
        SingleOrdVariables.singleOrderObject = singleOrderObject;
    }
}
