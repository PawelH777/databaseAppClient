package pl.Vorpack.app.DatabaseAccess;

import pl.Vorpack.app.Domain.User;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class UsersAccess {
    private final String findClientsByLoginURI = "/users/user/login";
    private final String createNewUserURI = "/users/createuser";
    private final String updateUserURI = "/users/user/update";
    private final String findAllUsersURI = "/users";
    private final String deleteUserURI = "/users/user/delete";
    private Client client;
    private String URI;
    private Response response;

    public List<User> findByLogin(String login){
        client = DatabaseAccess
                .accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + findClientsByLoginURI;
        response = client
                .target(URI)
                .path(login)
                .request(MediaType.APPLICATION_JSON)
                .get();
        List<User> allClientsFromDatabase = response.readEntity(new GenericType<ArrayList<User>>(){});
        client.close();
        return  allClientsFromDatabase;
    }

    public void create(User userObject){
        client = DatabaseAccess
                .accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + createNewUserURI;
        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(userObject, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }

    public void update(User userObject){
        client = DatabaseAccess
                .accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + updateUserURI;
        response =  client
                .target(URI)
                .path(String.valueOf(userObject.getUser_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(userObject, MediaType.APPLICATION_JSON_TYPE));
        client.close();
    }

    public List<User> findAll(){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + findAllUsersURI;
        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<User> allUsersFromDatabase = response.readEntity(new GenericType<ArrayList<User>>(){});
        client.close();
        return allUsersFromDatabase;
    }

    public void delete(User userObject){
        client =
                DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + deleteUserURI;
        response = client
                .target(URI)
                .path(String.valueOf(userObject.getUser_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();

        client.close();
    }
}
