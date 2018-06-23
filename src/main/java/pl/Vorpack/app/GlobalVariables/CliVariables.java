package pl.Vorpack.app.GlobalVariables;

import pl.Vorpack.app.Domain.Client;

/**
 * Created by Paweł on 2018-02-21.
 */
public class CliVariables {
   private static Client object = new Client();

    public static Client getObject() {
        return object;
    }

    public static void setObject(Client object) {
        CliVariables.object = object;
    }
}
