package pl.Vorpack.app.Background;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import pl.Vorpack.app.DatabaseAccess.OrdersAccess;
import pl.Vorpack.app.GlobalVariables.OrdVariables;

public class GetOrders  extends ScheduledService<Void> {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>(){
            protected Void call() {
                OrdersAccess ordersAccess = new OrdersAccess();
                OrdVariables.setOrdersFromDatabase(ordersAccess.findOrdersWithFinished(false));
                return null;
            }
        };
    }
}
