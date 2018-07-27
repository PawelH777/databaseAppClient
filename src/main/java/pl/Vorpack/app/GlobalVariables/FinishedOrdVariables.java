package pl.Vorpack.app.GlobalVariables;

import pl.Vorpack.app.Domain.Orders;

import java.util.ArrayList;
import java.util.List;

public class FinishedOrdVariables {
    private static Orders orderObject = new Orders();
    private static List<Orders> ordersFromDatabase = new ArrayList<>();

    public static Orders getOrderObject() {
        return orderObject;
    }

    public static void setOrderObject(Orders orderObject) {
        FinishedOrdVariables.orderObject = orderObject;
    }

    public static List<Orders> getOrdersFromDatabase() {
        return ordersFromDatabase;
    }

    public static void setOrdersFromDatabase(List<Orders> ordersFromDatabase) {
        FinishedOrdVariables.ordersFromDatabase = ordersFromDatabase;
    }
}
