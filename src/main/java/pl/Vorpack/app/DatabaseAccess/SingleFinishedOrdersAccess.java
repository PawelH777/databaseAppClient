package pl.Vorpack.app.DatabaseAccess;

import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.FinishedOrders;
import pl.Vorpack.app.Domain.SingleFinishedOrders;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class SingleFinishedOrdersAccess {
    private final String findAllSingleFinishedOrdersUri = "/single-orders-finished";
    private final String findAllSingleFinishedOrdersByFinishedOrderUri = "/single-orders-finished/finished-order";
    private final String findAllSingleFinishedOrdersByDimiensionsUri = "/single-orders-finished/dimension";
    private final String createSingleFinishedOrderUri = "/single-orders-finished/create-single-order-finished";
    private final String updateSingleFinishedOrderUri = "/single-orders-finished/update-single-order-finished";
    private final String deleteSingleOrderFinishedUri = "/single-orders-finished/delete/{id}";
    private final String deleteSingleOrdersFinishedByOrdersFinishedUri = "/single-orders-finished/delete/finished-order";
    private final String deleteSingleOrdersWithDimensionUri = "/single-orders-finished/delete/dimension";

    private javax.ws.rs.client.Client client =
            DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
    private Response response;
    private String URI;

    public List<SingleFinishedOrders> findAll(){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  findAllSingleFinishedOrdersUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        List<SingleFinishedOrders> allSingleOrdersFromDatabase = response.readEntity(new GenericType<List<SingleFinishedOrders>>(){});
        client.close();
        return allSingleOrdersFromDatabase;
    }

    public List<SingleFinishedOrders> findByOrdersFinished(FinishedOrders finishedOrderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  findAllSingleFinishedOrdersByFinishedOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(finishedOrderObject, MediaType.APPLICATION_JSON_TYPE));

        List<SingleFinishedOrders> allSingleOrdersFromOrder = response.readEntity(new GenericType<List<SingleFinishedOrders>>(){});
        client.close();
        return allSingleOrdersFromOrder;
    }

    public List<SingleFinishedOrders> findByDimiensions(Dimiensions dimensionObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  findAllSingleFinishedOrdersByDimiensionsUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(dimensionObject, MediaType.APPLICATION_JSON_TYPE));

        List<SingleFinishedOrders> allSingleOrdersFromDimension
                = response.readEntity(new GenericType<List<SingleFinishedOrders>>(){});
        client.close();
        return allSingleOrdersFromDimension;
    }

    public void createSingleOrderFinished(SingleFinishedOrders singleFinishedOrderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  createSingleFinishedOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(singleFinishedOrderObject, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }

    public void updateSingleOrderFinished(SingleFinishedOrders singleFinishedOrderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() + updateSingleFinishedOrderUri;

        response = client
                .target(URI)
                .path(String.valueOf(singleFinishedOrderObject.getSingle_finished_order_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(singleFinishedOrderObject, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }

    public void deleteSingleOrderFinished(SingleFinishedOrders singleFinishedOrderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  deleteSingleOrderFinishedUri;
        response = client
                .target(URI)
                .path(String.valueOf(singleFinishedOrderObject.getSingle_finished_order_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();
        client.close();
    }

    public void deleteSingleOrdersFinishedByOrdersFinished(FinishedOrders finishedOrderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + deleteSingleOrdersFinishedByOrdersFinishedUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(finishedOrderObject, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }

    public void deleteSingleOrderByDimension(Dimiensions dimensionObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + deleteSingleOrdersWithDimensionUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(dimensionObject, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }
}
