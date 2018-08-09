package pl.Vorpack.app.DatabaseAccess;

import pl.Vorpack.app.Domain.Client;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class OrdersAccess {

    private final String findAllOrdersUri = "/orders";
    private final String findOrdersWithFinishedUri = "/orders/finished";
    private final String updateOrderUri = "/orders/order/update";
    private final String changeStatusUri = "/orders/order/change-status";
    private final String createOrderUri = "/orders/createorder";
    private final String deleteOrderUri = "/orders/order/delete";
    private final String findOrdersWithClientUri = "/orders/clients";
    private final String deleteOrdersWithClientUri = "/orders/delete/clientObject";
    private final String moveOrderToOrdersStoryUri = "/orders/move-order";

    private javax.ws.rs.client.Client client =
            DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
    private Response response;
    private String URI;

    public List<Orders> findAllOrders(){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  findAllOrdersUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        List<Orders> allOrdersFromDatabase = response.readEntity(new GenericType<List<Orders>>(){});
        client.close();
        return allOrdersFromDatabase;
    }

    public List<Orders> findOrdersWithFinished(Boolean finished){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() +  findOrdersWithFinishedUri;

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
        URI  = GlobalVariables.getSite_name() + createOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(orderObject, MediaType.APPLICATION_JSON_TYPE));

        client.close();
    }

    public void updateOrder(Orders orderObject){
        client =
            DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());

        URI  = GlobalVariables.getSite_name() + updateOrderUri;
        response = client
                .target(URI)
                .path(String.valueOf(orderObject.getOrder_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(orderObject, MediaType.APPLICATION_JSON_TYPE));

        client.close();
    }

    public void changeOrdersStatus(Orders orderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI  = GlobalVariables.getSite_name() + changeStatusUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(orderObject, MediaType.APPLICATION_JSON_TYPE));


        client.close();
    }

    public void deleteOrder(Orders orderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());

        URI  = GlobalVariables.getSite_name() + deleteOrderUri;

        response = client
                .target(URI)
                .path(String.valueOf(orderObject.getOrder_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();
        client.close();
    }

    public List<Orders> findAllOrdersWithClient(Client clientObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + findOrdersWithClientUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(clientObject, MediaType.APPLICATION_JSON_TYPE));

        List<Orders> allOrdersWithClient = response.readEntity(new GenericType<List<Orders>>(){});
        client.close();
        return allOrdersWithClient;
    }

    public void deleteOrdersWithClient(Client clientObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + deleteOrdersWithClientUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(clientObject, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }

    public void moveOrderToOrdersStory(Orders orderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + moveOrderToOrdersStoryUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(orderObject, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }
}
