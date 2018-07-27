package pl.Vorpack.app.GlobalVariables;

import pl.Vorpack.app.Domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 2018-02-22.
 */
public class UsrVariables {

    private static User object = new User();
    private static List<User> usersInDatabase = new ArrayList<>();

    public static User getObject() {
        return object;
    }

    public static void setObject(User object) {
        UsrVariables.object = object;
    }

    public static List<User> getUsersInDatabase() {
        return usersInDatabase;
    }

    public static void setUsersInDatabase(List<User> usersInDatabase) {
        UsrVariables.usersInDatabase = usersInDatabase;
    }
}
