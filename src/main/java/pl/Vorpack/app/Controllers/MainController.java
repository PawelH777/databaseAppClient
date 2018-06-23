package pl.Vorpack.app.Controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;

import java.io.IOException;

/**
 * Created by Paweł on 2018-02-06.
 */
public class MainController {

    private static final String STORY_PANE_FXML = "/fxml/story/ShowFinishedOrders.fxml";
    private static final String USERS_PANE_FXML = "/fxml/users/ShowUsersPane.fxml";
    private static final String CLIENTS_PANE_FXML = "/fxml/clients/ShowClientsPane.fxml";
    private static final String DIMENSIONS_PANE_FXML = "/fxml/dimensions/ShowDimensionsPane.fxml";

    private static final String LOGIN_PANE_FXML = "/fxml/main/LogInPane.fxml";
    private static final String ORDERS_FXML = "/fxml/orders/ShowOrdersPane.fxml";
    @FXML
    private BorderPane borderPane;

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
        userLabel.setText(GlobalVariables.getName() + System.lineSeparator() + GlobalVariables.getAccess());
        borderPane.setOpacity(0);
        makeFadeInTransition();

        if(GlobalVariables.getAccess().equals("Użytkownik")){
            btnDims.setDisable(true);
            btnClients.setDisable(true);
            btnUsers.setDisable(true);
        }
    }

    private void setCenter(String fxmlPath, String zakladka){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
            borderPane.setCenter(parent);
        } catch (IOException e) {
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }



    public void tabOrderClicked(MouseEvent mouseEvent) {
        setCenter(ORDERS_FXML, "ZAMÓWIENIA");
    }

    public void tabStoryClicked(MouseEvent mouseEvent) {
        setCenter(STORY_PANE_FXML, "HISTORIA ZAMÓWIEŃ");
    }

    public void tabUsersClicked(MouseEvent mouseEvent) {
        setCenter(USERS_PANE_FXML, "UŻYTKOWNICY");
    }

    public void tabClientsClicked(MouseEvent mouseEvent) {
        setCenter(CLIENTS_PANE_FXML, "KLIENCI");
    }

    public void tabDimensionsClicked(MouseEvent mouseEvent) {
        setCenter(DIMENSIONS_PANE_FXML, "WYMIARY");
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
        }
        catch (IOException e) {
            InfoAlerts.generalAlert();
        }
    }
}
