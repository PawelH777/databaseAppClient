package pl.Vorpack.app.Controllers;

import javafx.animation.FadeTransition;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Background.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

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
    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

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

    private String textInStatusViewer = "Pobrano rekordy: ";

    @FXML
    private void initialize(){
        statusViewer.setWrapText(true);
        TextAnimations textAnimations = new TextAnimations(statusViewer);
        userLabel.setText(GlobalVariables.getName() + System.lineSeparator() + GlobalVariables.getAccess());
        borderPane.setOpacity(0);
        makeFadeInTransition();

        if(GlobalVariables.getAccess().equals("Użytkownik")){
            btnDims.setDisable(true);
            btnClients.setDisable(true);
            btnUsers.setDisable(true);
        }
        GetUsers getUsers = new GetUsers();
        GetOrders getOrders = new GetOrders();
        GetClients getClients = new GetClients();
        GetDimensions getDimensions = new GetDimensions();
        GetFinishedOrders getFinishedOrders = new GetFinishedOrders();

        AtomicInteger task = new AtomicInteger(0);

        getUsers.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                task.incrementAndGet();
                textInStatusViewer = textInStatusViewer + "userów";
                setTextAndCheckIfItIsFifthTaskEnded(task, textAnimations);
            }
        });

        getUsers.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                LOG.error(event.getSource().getException().toString());
            }
        });

        getClients.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                task.incrementAndGet();
                textInStatusViewer = textInStatusViewer + "klientów";
                setTextAndCheckIfItIsFifthTaskEnded(task, textAnimations);
            }
        });

        getClients.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                LOG.error(event.getSource().getException().toString());
            }
        });

        getDimensions.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                task.incrementAndGet();
                textInStatusViewer = textInStatusViewer + "wymiarów";
                setTextAndCheckIfItIsFifthTaskEnded(task, textAnimations);
            }
        });

        getDimensions.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                LOG.error(event.getSource().getException().toString());
            }
        });

        getOrders.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                task.incrementAndGet();
                textInStatusViewer = textInStatusViewer + "zamówień";
                setTextAndCheckIfItIsFifthTaskEnded(task, textAnimations);
            }
        });

        getOrders.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                LOG.error(event.getSource().getException().toString());
            }
        });

        getFinishedOrders.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                task.incrementAndGet();
                textInStatusViewer = textInStatusViewer + "zakończonych zamówień";
                setTextAndCheckIfItIsFifthTaskEnded(task, textAnimations);
            }
        });

        getFinishedOrders.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                LOG.error(event.getSource().getException().toString());
            }
        });

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

    private void setTextAndCheckIfItIsFifthTaskEnded(AtomicInteger task, TextAnimations textAnimations) {
        statusViewer.setText(textInStatusViewer);
        if(task.get() == 5){
            task.set(0);
            textInStatusViewer = "Pobrano rekordy: ";
            textAnimations.startLabelsPulsing();
        }
        else
            textInStatusViewer = textInStatusViewer + ", ";
    }

    private void fetchStatusViewerAndStartPulsuation(TextAnimations textAnimations) {
        statusViewer.setText(textInStatusViewer);
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
