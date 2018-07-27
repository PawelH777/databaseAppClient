package pl.Vorpack.app.GlobalVariables;

import pl.Vorpack.app.Domain.Client;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 2018-02-21.
 */
public class CliVariables {
    private static Client object = new Client();
    private static List<Client> clientsFromDatabase = new ArrayList<>();

    public static Client getObject() {
        return object;
    }

    public static void setObject(Client object) {
        CliVariables.object = object;
    }

    public static List<Client> getClientsFromDatabase() {
        return clientsFromDatabase;
    }

    public static void setClientsFromDatabase(List<Client> clientsFromDatabase) {
        CliVariables.clientsFromDatabase = clientsFromDatabase;
    }
}
