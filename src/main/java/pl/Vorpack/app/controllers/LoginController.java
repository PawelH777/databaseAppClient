package pl.Vorpack.app.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.codec.digest.DigestUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.omg.CORBA.portable.ApplicationException;
import pl.Vorpack.app.domain.User;
import pl.Vorpack.app.global_variables.userData;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;

/**
 * Created by Paweł on 2018-02-01.
 */
public class LoginController {


    public static final String MAIN_PANE_FXML = "/fxml/main/MainPane.fxml";
    @FXML
    private BorderPane borderPane;

    @FXML
    private JFXTextField loginTextField;

    @FXML
    private Label loginStatus;

    @FXML
    private JFXPasswordField passwordTextField;

    @FXML
    public void initialize(){

        borderPane.setOpacity(0);
        makeFadeIn();
    }

    @FXML
    public void onmouseClicked(){

        String login = loginTextField.getText();
        String haslo = DigestUtils.sha1Hex(passwordTextField.getText());

    //    String haslo = passwordTextField.getText();


        try{

            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                    .nonPreemptive()
                    .credentials(login, haslo)
                    .build();

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(feature);

            Client client = ClientBuilder.newClient(clientConfig);

            String URI = "http://localhost:8080/users/user/login";

            Response response = client
                    .target(URI)
                    .path(login)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            List<User> users = response.readEntity(new GenericType<ArrayList<User>>(){});

            client.close();

            if(users.size() != 1 ){
                loginStatus.setText("Istnieje więcej użytkowników o tym loginie");
            }
            else{
                User user = users.get(0);
                    userData.setName(login);
                    userData.setPassword(haslo);
                    if(user.isAdmin())
                        userData.setAccess("Administrator");
                    else
                        userData.setAccess("Użytkownik");

                    makeFadeOut();
            }
        } catch(Throwable ex){
            loginStatus.setText("Wpisałeś błędny login lub hasło");
        }
    }

    private void makeFadeOut(){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(250));
        fadeTransition.setNode(borderPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadNextScene();
            }
        });
        fadeTransition.play();
    }

    private void makeFadeIn(){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(250));
        fadeTransition.setNode(borderPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }


    private void loadNextScene(){

        try {
            Parent mainPane;
            mainPane = (BorderPane) FXMLLoader.load(getClass().getResource(MAIN_PANE_FXML));

            Scene mainScene = new Scene(mainPane);

            Stage curStage = (Stage) borderPane.getScene().getWindow();

            curStage.setScene(mainScene);
            curStage.setMaximized(true);
        } catch (IOException e) {

            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Uwaga!");
            infoAlert.setHeaderText("Pojawił się błąd");
            infoAlert.setContentText("Niestety, pojawiła się usterka blokująca wyświetlanie menu głównego. Proszę skontaktować się z " +
                    "autorem programu.");
            infoAlert.showAndWait();
        }
    }
}
