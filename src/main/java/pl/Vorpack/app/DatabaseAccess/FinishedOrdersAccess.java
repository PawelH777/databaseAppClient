package pl.Vorpack.app.DatabaseAccess;

import pl.Vorpack.app.Domain.Client;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.FinishedOrders;
import pl.Vorpack.app.GlobalVariables.FinishedOrdVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class FinishedOrdersAccess {

    private final String findAllFinishedOrdersUri = "/orderstory";
    private final String deleteFinishedOrderUri = "/orderstory//order/delete";
    private final String updateOrderUri = "/orderstory/order/update";
    private final String createOrderUri = "/orderstory/createorder";
    private final String findOrdersWithClient = "/orderstory/clients";
    private final String deleteOrdersWithClient = "/orderstory/delete/clientObject";
    private final String moveFinishedOrderToOrdersUri = "/orderstory/move-finished-order";

    private javax.ws.rs.client.Client client =
            DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
    private Response response;
    private String URI;

    public List<FinishedOrders> findAllFinishedOrders(){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + findAllFinishedOrdersUri;
        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        List<FinishedOrders> allFinishedOrdersFromDatabase = response.readEntity(new GenericType<List<FinishedOrders>>(){});
        client.close();
        return allFinishedOrdersFromDatabase;
    }

    public void createFinishedOrder(FinishedOrders finishedOrderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + createOrderUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(finishedOrderObject, MediaType.APPLICATION_JSON_TYPE));

        client.close();
    }

    public void updateFinishedOrder(FinishedOrders finishedOrderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());

        URI  = GlobalVariables.getSite_name() + updateOrderUri;
        response = client
                .target(URI)
                .path(String.valueOf(finishedOrderObject.getOrder_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(finishedOrderObject, MediaType.APPLICATION_JSON_TYPE));

        client.close();
    }

    public void deleteFinishedOrder(FinishedOrders finishedOrderObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());

        URI  = GlobalVariables.getSite_name() + deleteFinishedOrderUri;

        response = client
                .target(URI)
                .path(String.valueOf(finishedOrderObject.getOrder_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();

        client.close();
    }

    public List<FinishedOrders> findAllOrdersWithClient(Client clientObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + findOrdersWithClient;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(clientObject, MediaType.APPLICATION_JSON_TYPE));

        List<FinishedOrders> allOrdersWithClient = response.readEntity(new GenericType<List<FinishedOrders>>(){});
        client.close();
        return allOrdersWithClient;
    }

    public void deleteOrdersWithClient(Client clientObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + deleteOrdersWithClient;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(clientObject, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }

    public void moveFinishedOrderToOrders(FinishedOrders finishedOrderObject) {
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + moveFinishedOrderToOrdersUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(finishedOrderObject, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }
}
