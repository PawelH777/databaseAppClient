package pl.Vorpack.app.Background;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import pl.Vorpack.app.DatabaseAccess.ClientAccess;
import pl.Vorpack.app.GlobalVariables.ClientVariables;

public class GetClients extends ScheduledService<Void> {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>(){
            protected Void call() {
                ClientAccess clientsAccess = new ClientAccess();
                ClientVariables.setClients(clientsAccess.findAll());
                return null;
            }
        };
    }
}
