package pl.Vorpack.app.DatabaseAccess;

import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Domain.Trays;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class TraysAccess {
    private final String findAllUri = "/trays";
    private final String findAllTraysBySingleOrderUri = "/trays//find-by-single-order";
    private final String createTraysBySingleOrderUri = "/trays/create-by-single-order";
    private final String changeTraysStatusUri = "/trays/update";
    private javax.ws.rs.client.Client client =
            DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
    private Response response;
    private String URI;

    public List<Trays> findAll(){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  findAllUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        List<Trays> allSingleOrdersFromDatabase = response.readEntity(new GenericType<List<Trays>>(){});
        client.close();
        return allSingleOrdersFromDatabase;
    }

    public List<Trays> findAllTraysBySingleOrder(SingleOrders singleOrder){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  findAllTraysBySingleOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(singleOrder, MediaType.APPLICATION_JSON_TYPE));

        List<Trays> allSingleOrdersFromDatabase = response.readEntity(new GenericType<List<Trays>>(){});
        client.close();
        return allSingleOrdersFromDatabase;
    }

    public void createTraysBySingleOrder(SingleOrders singleOrder){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  createTraysBySingleOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(singleOrder, MediaType.APPLICATION_JSON_TYPE));

        client.close();
    }

    public void changeTraysStatus(Trays tray){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  changeTraysStatusUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(tray, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }
}
