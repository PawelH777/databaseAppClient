package pl.Vorpack.app.GlobalVariables;

import pl.Vorpack.app.Domain.Clients;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 2018-02-21.
 */
public class CliVariables {
    private static Clients object = new Clients();
    private static List<Clients> clientsFromDatabase = new ArrayList<>();

    public static Clients getObject() {
        return object;
    }

    public static void setObject(Clients object) {
        CliVariables.object = object;
    }

    public static List<Clients> getClientsFromDatabase() {
        return clientsFromDatabase;
    }

    public static void setClientsFromDatabase(List<Clients> clientsFromDatabase) {
        CliVariables.clientsFromDatabase = clientsFromDatabase;
    }
}
