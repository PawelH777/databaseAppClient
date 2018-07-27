package pl.Vorpack.app.Background;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import pl.Vorpack.app.DatabaseAccess.ClientAccess;
import pl.Vorpack.app.GlobalVariables.CliVariables;

public class GetClients extends ScheduledService<Void> {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>(){
            protected Void call() {
                ClientAccess clientsAccess = new ClientAccess();
                CliVariables.setClientsFromDatabase(clientsAccess.findAllClients());
                return null;
            }
        };
    }
}
