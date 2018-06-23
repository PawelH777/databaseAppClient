package pl.Vorpack.app.DatabaseAccess;

import pl.Vorpack.app.Domain.Client;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class ClientAccess {

    private final String searchWithFirmNameUri = "/clients/client/firmname";
    private final String createNewClientUri = "/clients/createclient";
    private final String updateClientUri = "/clients/client/update";
    private final String deleteClientUri = "/clients/client/delete";

    private Client cli = new Client();
    private javax.ws.rs.client.Client client;
    private Response response;
    private String URI;

    public List<Client> findAllClients(){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + "/clients";

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        List<Client> AllClientsFromDatabase = response.readEntity(new GenericType<List<Client>>(){});
        client.close();
        return AllClientsFromDatabase;
    }

    public List<Client> findClient(String firmName){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        String URI = GlobalVariables.getSite_name() + searchWithFirmNameUri;

        cli.setFirmName(firmName);

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(cli, MediaType.APPLICATION_JSON_TYPE));

        List<Client> AllClientsFromDatabase = response.readEntity(new GenericType<List<Client>>(){});
        client.close();
        return AllClientsFromDatabase;
    }

    public void createNewClient(Client objectToCreate){
        client =
            DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + createNewClientUri;
        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(objectToCreate, MediaType.APPLICATION_JSON_TYPE));

        client.close();
    }

    public void updateClient(Client objectToUpdate){
        client =
            DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + updateClientUri;
        response = client
                .target(URI)
                .path(String.valueOf(objectToUpdate.getClient_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(objectToUpdate, MediaType.APPLICATION_JSON_TYPE));

        client.close();
    }

    public void deleteClient(Client clientObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + deleteClientUri;

        response = client
                .target(URI)
                .path(String.valueOf(clientObject.getClient_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();

        client.close();
    }
}
