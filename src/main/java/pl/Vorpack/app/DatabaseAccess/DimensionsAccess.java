package pl.Vorpack.app.DatabaseAccess;

import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class DimensionsAccess {

    private final String findDimensionsUri = "/dims";
    private final String findDimensionUri = "/dims/dim/find";
    private final String createDimensionUri = "/dims/createdim";
    private final String updateDimensionUri = "/dims/dim/update";
    private final String deleteDimensionUri = "/dims/dim/delete";
    private Client client;
    private String URI;
    private Response response;

    public List<Dimiensions> findAllDimensions(){
        client = DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + findDimensionsUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        List<Dimiensions> AllDimensionsFromDatabase = response.readEntity(new GenericType<List<Dimiensions>>(){});
        client.close();
        return AllDimensionsFromDatabase;
    }

    public List<Dimiensions> find(Dimiensions object){
        client = DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + findDimensionUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(object,MediaType.APPLICATION_JSON_TYPE));

        List<Dimiensions> AllDimensionsFromDatabase = response.readEntity(new GenericType<List<Dimiensions>>(){});
        client.close();
        return AllDimensionsFromDatabase;
    }

    public Dimiensions create(Dimiensions object){
        client = DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + createDimensionUri;

        response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(object, MediaType.APPLICATION_JSON_TYPE));

        Dimiensions dim = response.readEntity(new GenericType<Dimiensions>(){});
        client.close();
        return  dim;
    }

    public void update(Dimiensions object){
        client = DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() +  updateDimensionUri;

        response = client
                .target(URI)
                .path(String.valueOf(object.getDimension_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(object, MediaType.APPLICATION_JSON_TYPE));

        client.close();
    }

    public void delete(Dimiensions dimensionObject){
        client = DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());
        URI = GlobalVariables.getSite_name() + deleteDimensionUri;

        response = client
                .target(URI)
                .path(String.valueOf(dimensionObject.getDimension_id()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();
        client.close();
    }
}
