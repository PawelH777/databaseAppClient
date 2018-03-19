package pl.Vorpack.app.global_variables;

import pl.Vorpack.app.domain.Client;

/**
 * Created by Paweł on 2018-02-21.
 */
public class cliVariables {
   private static Client object = new Client();

    public static Client getObject() {
        return object;
    }

    public static void setObject(Client object) {
        cliVariables.object = object;
    }
}
