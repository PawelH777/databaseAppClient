package pl.Vorpack.app.global_variables;

import pl.Vorpack.app.domain.User;

/**
 * Created by Paweł on 2018-02-22.
 */
public class usrVariables {

    private static User object = new User();

    public static User getObject() {
        return object;
    }

    public static void setObject(User object) {
        usrVariables.object = object;
    }
}
