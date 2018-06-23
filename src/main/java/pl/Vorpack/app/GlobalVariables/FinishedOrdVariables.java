package pl.Vorpack.app.GlobalVariables;

import pl.Vorpack.app.Domain.FinishedOrders;

public class FinishedOrdVariables {

    private static FinishedOrders orderObject = new FinishedOrders();

    public static FinishedOrders getOrderObject() {
        return orderObject;
    }

    public static void setOrderObject(FinishedOrders orderObject) {
        FinishedOrdVariables.orderObject = orderObject;
    }
}
