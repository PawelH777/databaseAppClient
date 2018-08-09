package pl.Vorpack.app.DatabaseAccess;

import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.SingleOrdVariables;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class SingleOrdersAccess {
    private final String findAllSingleOrdersUri = "/single-orders";
    private final String findAllSingleOrdersFromOrderUri = "/single-orders/order";
    private final String findAllSingleOrdersFromDimensionUri = "/single-orders/dimension";
    private final String createSingleOrderUri = "/single-orders/create-single-order";
    private final String updateSingleOrderUri = "/single-orders/update-single-order";
    private final String deleteSingleOrderUri = "/single-orders/delete";
    private final String deleteSingleOrdersWithOrderUri = "/single-orders/delete/order";
    private final String deleteSingleOrdersWithDimensionUri = "/single-orders/delete/dimension";
    private javax.ws.rs.client.Client client =
            DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
    private Response response;
    private String URI;

    public List<SingleOrders> findAll(){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  findAllSingleOrdersUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        List<SingleOrders> allSingleOrdersFromDatabase = response.readEntity(new GenericType<List<SingleOrders>>(){});
        client.close();
        return allSingleOrdersFromDatabase;
    }

    public List<SingleOrders> findByOrders(Orders orderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  findAllSingleOrdersFromOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(orderObject, MediaType.APPLICATION_JSON_TYPE));

        List<SingleOrders> allSingleOrdersFromOrder = response.readEntity(new GenericType<List<SingleOrders>>(){});
        client.close();
        return allSingleOrdersFromOrder;
    }

    public List<SingleOrders> findByDimiensions(Dimiensions dimensionObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  findAllSingleOrdersFromDimensionUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(dimensionObject, MediaType.APPLICATION_JSON_TYPE));

        List<SingleOrders> allSingleOrdersFromDimension
                = response.readEntity(new GenericType<List<SingleOrders>>(){});
        client.close();
        return allSingleOrdersFromDimension;
    }

    public SingleOrders createSingleOrder(SingleOrders singleOrdersObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  createSingleOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(singleOrdersObject, MediaType.APPLICATION_JSON_TYPE));

        singleOrdersObject = response.readEntity(new GenericType<SingleOrders>(){});
        client.close();
        return singleOrdersObject;
    }

    public void updateSingleOrder(SingleOrders singleOrdersObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  updateSingleOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(singleOrdersObject, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }

    public void deleteSingleOrder(SingleOrders singleOrdersObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  deleteSingleOrderUri;
        response = client
                .target(URI)
                .path(String.valueOf(singleOrdersObject.getSingle_active_order_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();
        client.close();
    }

    public void deleteSingleOrderByOrder(Orders orderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + deleteSingleOrdersWithOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(orderObject, MediaType.APPLICATION_JSON_TYPE));
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
