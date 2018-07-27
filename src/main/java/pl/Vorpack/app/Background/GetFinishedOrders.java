package pl.Vorpack.app.Background;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import pl.Vorpack.app.DatabaseAccess.OrdersAccess;
import pl.Vorpack.app.GlobalVariables.FinishedOrdVariables;

public class GetFinishedOrders extends ScheduledService<Void> {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>(){
            protected Void call() {
                OrdersAccess finishedOrdersAccess = new OrdersAccess();
                FinishedOrdVariables.setOrdersFromDatabase(finishedOrdersAccess.findOrdersWithFinished(true));
                return null;
            }
        };
    }
}
