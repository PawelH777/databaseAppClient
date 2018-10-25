package pl.Vorpack.app.DatabaseAccess;

import pl.Vorpack.app.Constans.AccessPathsConstans;
import pl.Vorpack.app.DatabaseAccess.DatabaseAccess;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class SingleOrdersAccess {
    private javax.ws.rs.client.Client client =
            DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
    private Response response;
    private String URI;

    public List<SingleOrders> findAll(){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  AccessPathsConstans.findAllSingleOrdersUri;

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
        URI  = GlobalVariables.getSite_name() +  AccessPathsConstans.findAllSingleOrdersFromOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(orderObject, MediaType.APPLICATION_JSON_TYPE));

        List<SingleOrders> allSingleOrdersFromOrder = response.readEntity(new GenericType<List<SingleOrders>>(){});
        client.close();
        return allSingleOrdersFromOrder;
    }

    public SingleOrders createSingleOrder(SingleOrders singleOrdersObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  AccessPathsConstans.createSingleOrderUri;

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
        URI  = GlobalVariables.getSite_name() +  AccessPathsConstans.updateSingleOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(singleOrdersObject, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }

    public void deleteSingleOrder(SingleOrders singleOrdersObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  AccessPathsConstans.deleteSingleOrderUri;
        response = client
                .target(URI)
                .path(String.valueOf(singleOrdersObject.getSingle_active_order_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();
        client.close();
    }
}
