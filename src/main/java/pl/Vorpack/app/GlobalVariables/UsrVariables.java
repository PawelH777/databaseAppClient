package pl.Vorpack.app.GlobalVariables;

import pl.Vorpack.app.Domain.User;

/**
 * Created by Paweł on 2018-02-22.
 */
public class UsrVariables {

    private static User object = new User();

    public static User getObject() {
        return object;
    }

    public static void setObject(User object) {
        UsrVariables.object = object;
    }
}
