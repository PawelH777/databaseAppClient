package pl.Vorpack.app.GlobalVariables;

import pl.Vorpack.app.Domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 2018-02-22.
 */
public class UsrVariables {

    private static Long id;
    private static List<User> usersInDatabase = new ArrayList<>();

    public static Long getId() {
        return id;
    }

    public static void setId(Long id) {
        UsrVariables.id = id;
    }

    public static List<User> getUsersInDatabase() {
        return usersInDatabase;
    }

    public static void setUsersInDatabase(List<User> usersInDatabase) {
        UsrVariables.usersInDatabase = usersInDatabase;
    }
}
