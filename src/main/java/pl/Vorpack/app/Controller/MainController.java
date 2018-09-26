package pl.Vorpack.app.Controller;

import javafx.animation.FadeTransition;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Constans.Path;
import pl.Vorpack.app.Constans.User;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Background.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Paweł on 2018-02-06.
 */
public class MainController {

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);
    private static final String USERS = "userów";
    private static final String CLIENTS = "klientów";
    private static final String DIMS = "wymiarów";
    private static final String ORDERS = "zamówień";
    private static final String CLOSED_ORDERS = "zakończonych zamówień";

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
    private Label statusViewer;

    private String statusViewerText = "Pobrano rekordy: ";
    private AtomicInteger task = new AtomicInteger(0);
    private TextAnimations textAnimations;

    @FXML
    private void initialize(){
        textAnimations = new TextAnimations(statusViewer);
        statusViewer.setWrapText(true);
        userLabel.setText(GlobalVariables.getName() + System.lineSeparator() + GlobalVariables.getAccess());
        borderPane.setOpacity(0);
        makeFadeInTransition();
        if(GlobalVariables.getAccess().equals(User.USER)){
            btnDims.setDisable(true);
            btnClients.setDisable(true);
            btnUsers.setDisable(true);
        }
        GetUsers getUsers = new GetUsers();
        GetOrders getOrders = new GetOrders();
        GetClients getClients = new GetClients();
        GetDimensions getDimensions = new GetDimensions();
        GetFinishedOrders getFinishedOrders = new GetFinishedOrders();
        getUsers.setOnSucceeded(onSucceded(USERS));
        getUsers.setOnFailed(onFailed());
        getClients.setOnSucceeded(onSucceded(CLIENTS));
        getClients.setOnFailed(onFailed());
        getDimensions.setOnSucceeded(onSucceded(DIMS));
        getDimensions.setOnFailed(onFailed());
        getOrders.setOnSucceeded(onSucceded(ORDERS));
        getOrders.setOnFailed(onFailed());
        getFinishedOrders.setOnSucceeded(onSucceded(CLOSED_ORDERS));
        getFinishedOrders.setOnFailed(onFailed());
        getUsers.setPeriod(Duration.seconds(60));
        getClients.setPeriod(Duration.seconds(60));
        getDimensions.setPeriod(Duration.seconds(60));
        getOrders.setPeriod(Duration.seconds(60));
        getFinishedOrders.setPeriod(Duration.seconds(60));
        getUsers.start();
        getClients.start();
        getDimensions.start();
        getOrders.start();
        getFinishedOrders.start();
    }

    private EventHandler<WorkerStateEvent> onSucceded(String downloadContent){
        return new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                task.incrementAndGet();
                statusViewerText = statusViewerText + downloadContent;
                updateTaskProgress(task, textAnimations);
            }
        };
    }

    private EventHandler<WorkerStateEvent> onFailed(){
        return new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                LOG.error(event.getSource().getException().toString());
            }
        };
    }

    private void updateTaskProgress(AtomicInteger task, TextAnimations textAnimations) {
        statusViewer.setText(statusViewerText);
        if(task.get() == 5){
            task.set(0);
            statusViewerText = "Pobrano rekordy: ";
            textAnimations.startLabelsPulsing();
        }
        else
            statusViewerText = statusViewerText + ", ";
    }

    private void fetchStatusViewerAndStartPulsuation(TextAnimations textAnimations) {
        statusViewer.setText(statusViewerText);
        textAnimations.startLabelsPulsing();
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

    public void tabOrderClicked() {
        setCenter(Path.ORDERS_PANE_PATH, "ZAMÓWIENIA");
    }

    public void tabStoryClicked() {
        setCenter(Path.CLOSED_ORDERS_PANE_PATH, "HISTORIA ZAMÓWIEŃ");
    }

    public void tabUsersClicked() {
        setCenter(Path.USERS_PANE_PATH, "UŻYTKOWNICY");
    }

    public void tabClientsClicked() {
        setCenter(Path.CLIENTS_PANE_PATH, "KLIENCI");
    }

    public void tabDimensionsClicked() {
        setCenter(Path.DIMENSIONS_PANE_PATH, "WYMIARY");
    }

    public void tabLogoutClicked() {
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
            mainPane = (BorderPane) FXMLLoader.load(getClass().getResource(Path.LOGIN_PANE_PATH));
            Scene mainScene = new Scene(mainPane);
            Stage curStage = (Stage) borderPane.getScene().getWindow();
            curStage.setScene(mainScene);
        }
        catch (IOException e) {
            InfoAlerts.generalAlert();
        }
    }
}
