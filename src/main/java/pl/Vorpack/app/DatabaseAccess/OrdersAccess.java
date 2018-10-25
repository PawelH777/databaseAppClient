package pl.Vorpack.app.DatabaseAccess;

import pl.Vorpack.app.Constans.AccessPathsConstans;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class OrdersAccess {

    private javax.ws.rs.client.Client client =
            DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
    private Response response;
    private String URI;

    public List<Orders> findOrdersWithFinished(Boolean finished){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  AccessPathsConstans.findOrdersWithFinishedUri;

        response = client
                .target(URI)
                .path(String.valueOf(finished))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        List<Orders> allOrdersFromDatabase = response.readEntity(new GenericType<List<Orders>>(){});
        client.close();
        return allOrdersFromDatabase;
    }

    public void createOrder(Orders orderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() + AccessPathsConstans.createOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(orderObject, MediaType.APPLICATION_JSON_TYPE));

        client.close();
    }

    public void updateOrder(Orders orderObject){
        client =
            DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());

        URI  = GlobalVariables.getSite_name() + AccessPathsConstans.updateOrderUri;
        response = client
                .target(URI)
                .path(String.valueOf(orderObject.getOrder_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(orderObject, MediaType.APPLICATION_JSON_TYPE));

        client.close();
    }

    public Orders changeOrdersStatus(Orders orderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() + AccessPathsConstans.changeStatusUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(orderObject, MediaType.APPLICATION_JSON_TYPE));

        Orders obj = response.readEntity(new GenericType<Orders>(){});
        client.close();
        return obj;
    }

    public void deleteOrder(Orders orderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());

        URI  = GlobalVariables.getSite_name() + AccessPathsConstans.deleteOrderUri;

        response = client
                .target(URI)
                .path(String.valueOf(orderObject.getOrder_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();
        client.close();
    }
}
