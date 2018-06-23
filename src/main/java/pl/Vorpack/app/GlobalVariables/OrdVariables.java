package pl.Vorpack.app.GlobalVariables;

import pl.Vorpack.app.Domain.Orders;

/**
 * Created by Paweł on 2018-03-01.
 */
public class OrdVariables {
    private static Orders orderObject = new Orders();

    public static Orders getOrderObject() {
        return orderObject;
    }

    public static void setOrderObject(Orders orderObject) {
        OrdVariables.orderObject = orderObject;
    }
}
