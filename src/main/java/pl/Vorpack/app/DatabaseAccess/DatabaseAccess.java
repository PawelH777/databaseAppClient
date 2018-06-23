package pl.Vorpack.app.DatabaseAccess;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class DatabaseAccess {

    public static Client accessToDatabase(String login, String haslo){

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                .nonPreemptive()
                .credentials(login, haslo)
                .build();

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(feature);
        clientConfig.register(JacksonJsonProvider.class);

        return ClientBuilder.newClient(clientConfig);
    }
}
