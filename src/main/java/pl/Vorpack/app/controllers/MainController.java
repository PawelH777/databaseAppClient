package pl.Vorpack.app.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import pl.Vorpack.app.global_variables.userData;

import java.io.IOException;

/**
 * Created by Paweł on 2018-02-06.
 */
public class MainController {

    public static final String STORY_PANE_FXML = "/fxml/story/ShowStoryPane.fxml";
    public static final String USERS_PANE_FXML = "/fxml/users/ShowUsersPane.fxml";
    public static final String CLIENTS_PANE_FXML = "/fxml/clients/ShowClientsPane.fxml";
    public static final String DIMENSIONS_PANE_FXML = "/fxml/dimensions/ShowDimensionsPane.fxml";
    public static final String LOGIN_PANE_FXML = "/fxml/main/LogInPane.fxml";
    public static final String ORDERS_FXML = "/fxml/orders/ShowOrdersPane.fxml";
    @FXML
    private BorderPane borderPane;

    @FXML
    private ToggleButton btnLogout;

    @FXML
    private ToggleButton btnDims;

    @FXML
    private ToggleButton btnClients;

    @FXML
    private ToggleButton btnUsers;

    @FXML
    private Label userLabel;

    @FXML
    private void initialize(){
        userLabel.setText(userData.getName() + System.lineSeparator() + userData.getAccess());
        borderPane.setOpacity(0);
        makeFadeInTransition();

        if(userData.getAccess().equals("Użytkownik")){
            btnDims.setDisable(true);
            btnClients.setDisable(true);
            btnUsers.setDisable(true);
        }
    }

    public void setCenter(String fxmlPath){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        borderPane.setCenter(parent);
    }



    public void tabOrderClicked(MouseEvent mouseEvent) {

        setCenter(ORDERS_FXML);
    }

    public void tabStoryClicked(MouseEvent mouseEvent) {
        setCenter(STORY_PANE_FXML);
    }

    public void tabUsersClicked(MouseEvent mouseEvent) {
        setCenter(USERS_PANE_FXML);
    }

    public void tabClientsClicked(MouseEvent mouseEvent) {
        setCenter(CLIENTS_PANE_FXML);
    }

    public void tabDimensionsClicked(MouseEvent mouseEvent) {
        setCenter(DIMENSIONS_PANE_FXML);
    }

    public void tabLogoutClicked(MouseEvent mouseEvent) {

        makeFadeOut();
    }

    private void makeFadeInTransition(){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(250));
        fadeTransition.setNode(borderPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void makeFadeOut(){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(250));
        fadeTransition.setNode(borderPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        fadeTransition.setOnFinished(event -> loadNextScene());
        fadeTransition.play();
    }

    private void loadNextScene(){

        try {
            Parent mainPane;
            mainPane = (BorderPane) FXMLLoader.load(getClass().getResource(LOGIN_PANE_FXML));

            Scene mainScene = new Scene(mainPane);

            Stage curStage = (Stage) borderPane.getScene().getWindow();
            curStage.setScene(mainScene);
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
