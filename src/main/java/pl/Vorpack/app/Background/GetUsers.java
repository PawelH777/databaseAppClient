package pl.Vorpack.app.Background;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import pl.Vorpack.app.DatabaseAccess.UsersAccess;
import pl.Vorpack.app.GlobalVariables.UsrVariables;

public class GetUsers extends ScheduledService<Void> {

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>(){
            protected Void call() {
                UsersAccess usersAccess = new UsersAccess();
                UsrVariables.setUsersInDatabase(usersAccess.findAllUsers());
                UsrVariables.getUsersInDatabase().removeIf(u -> u.getLogin().equals("Admin"));
                return null;
            }
        };
    }
}
