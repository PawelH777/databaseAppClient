package pl.Vorpack.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import pl.Vorpack.app.domain.Dimiensions;
import pl.Vorpack.app.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Paweł on 2018-02-01.
 */
public class Main extends Application {

    public static final String LOGIN_PANE_FXML = "/fxml/main/LogInPane.fxml";


    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(LOGIN_PANE_FXML));

        BorderPane borderPane = loader.load();
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("VorApp");
        primaryStage.show();
    }
}




