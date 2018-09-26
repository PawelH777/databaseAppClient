package pl.Vorpack.app.DatabaseAccess;

import pl.Vorpack.app.Domain.Clients;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class ClientAccess {

    private final String findAllClients = "/clients";
    private final String searchWithFirmNameUri = "/clients/client/firmname";
    private final String createNewClientUri = "/clients/createclient";
    private final String updateClientUri = "/clients/client/update";
    private final String deleteClientUri = "/clients/client/delete";

    private Clients cli = new Clients();
    private javax.ws.rs.client.Client client;
    private Response response;
    private String URI;

    public List<Clients> findAll(){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + findAllClients;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        List<Clients> allClientsFromDatabase = response.readEntity(new GenericType<List<Clients>>(){});
        client.close();
        return allClientsFromDatabase;
    }

    public List<Clients> findByFirmName(String firmName){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        String URI = GlobalVariables.getSite_name() + searchWithFirmNameUri;

        cli.setFirmName(firmName);

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(cli, MediaType.APPLICATION_JSON_TYPE));

        List<Clients> allClientsFromDatabase = response.readEntity(new GenericType<List<Clients>>(){});
        client.close();
        return allClientsFromDatabase;
    }

    public void create(Clients objectToCreate){
        client =
            DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + createNewClientUri;
        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(objectToCreate, MediaType.APPLICATION_JSON_TYPE));

        client.close();
    }

    public void update(Clients objectToUpdate){
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

    public void delete(Clients clientsObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + deleteClientUri;

        response = client
                .target(URI)
                .path(String.valueOf(clientsObject.getClient_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();

        client.close();
    }
}
