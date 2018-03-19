package pl.Vorpack.app.controllers;

import java.io.IOException;
import java.util.Collection;
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
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.codec.digest.DigestUtils;
import pl.Vorpack.app.domain.PersistenceInfo;
import pl.Vorpack.app.domain.User;
import pl.Vorpack.app.global_variables.userData;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by Paweł on 2018-02-01.
 */
public class LoginController {


    public static final String MAIN_PANE_FXML = "/fxml/main/MainPane.fxml";
    @FXML
    private BorderPane borderPane;

    @FXML
    private Label textLabel;

    @FXML
    private JFXTextField loginTextField;

    @FXML
    private Label loginStatus;

    @FXML
    private JFXPasswordField passwordTextField;

    @FXML
    private Label passwordStatus;

    @FXML
    private JFXCheckBox memoryCheckBox;

    @FXML
    private JFXButton loginButton;

    @FXML
    public void initialize(){

        borderPane.setOpacity(0);
        makeFadeIn();
    }

    @FXML
    public void onmouseClicked(){

        String login = loginTextField.getText();
        String haslo = DigestUtils.sha1Hex(passwordTextField.getText());


        Map<String, String> properties = new HashMap<String, String>();

        properties = PersistenceInfo.putTokens();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("default", properties);
        EntityManager entityManager = emf.createEntityManager();

        entityManager.getTransaction().begin();
        List<User> result = entityManager.createQuery(
          "SELECT u FROM User u WHERE Login LIKE :lgn AND Haslo LIKE :pass")
                .setParameter("lgn", login)
                .setParameter("pass", haslo)
                .getResultList();

        entityManager.getTransaction().commit();
        if(result.size() != 1 || result.isEmpty()){
            loginStatus.setText("Login lub hasło jest niepoprawne!");
        }
        else{

            userData.setName(login);

            if(result.get(0).isAdmin())
                userData.setAccess("Administrator");
            else
                userData.setAccess("Użytkownik");

           makeFadeOut();
        }

        entityManager.close();
        emf.close();



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
