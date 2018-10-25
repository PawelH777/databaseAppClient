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
import pl.Vorpack.app.Background.*;
import pl.Vorpack.app.Constans.PathConstans;
import pl.Vorpack.app.Constans.UserConstans;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;

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
    private void initialize() {
        textAnimations = new TextAnimations(statusViewer);
        statusViewer.setWrapText(true);
        userLabel.setText(GlobalVariables.getName() + System.lineSeparator() + GlobalVariables.getAccess());
        borderPane.setOpacity(0);
        makeFadeInTransition();
        if (GlobalVariables.getAccess().equals(UserConstans.USER)) {
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

    public void tabOrderClicked() throws IOException {
        setCenter(PathConstans.ORDERS_PANE_PATH, MainController.ORDERS);
    }

    public void tabStoryClicked() throws IOException {
        setCenter(PathConstans.CLOSED_ORDERS_PANE_PATH, MainController.CLOSED_ORDERS);
    }

    public void tabUsersClicked() throws IOException {
        setCenter(PathConstans.USERS_PANE_PATH, MainController.USERS);
    }

    public void tabClientsClicked() throws IOException {
        setCenter(PathConstans.CLIENTS_PANE_PATH, MainController.CLIENTS);
    }

    public void tabDimensionsClicked() throws IOException {
        setCenter(PathConstans.DIMENSIONS_PANE_PATH, MainController.DIMS);
    }

    public void tabLogoutClicked() {
        makeFadeOut();
    }

    private EventHandler<WorkerStateEvent> onSucceded(String downloadContent) {
        return new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                task.incrementAndGet();
                statusViewerText = statusViewerText + downloadContent;
                updateTaskProgress(task, textAnimations);
            }
        };
    }

    private EventHandler<WorkerStateEvent> onFailed() {
        return new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                LOG.error(event.getSource().getException().toString());
            }
        };
    }

    private void updateTaskProgress(AtomicInteger task, TextAnimations textAnimations) {
        statusViewer.setText(statusViewerText);
        if (task.get() == 5) {
            task.set(0);
            statusViewerText = "Pobrano rekordy: ";
            textAnimations.startLabelsPulsing();
        } else
            statusViewerText = statusViewerText + ", ";
    }

    private void setCenter(String fxmlPath, String tab) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent parent = fxmlLoader.load();
        borderPane.setCenter(parent);
    }
//
//    private void setParameters(FXMLLoader fxmlLoader, String tab) {
//        switch (tab) {
//            case MainController.CLIENTS:
//                ClientController controller = fxmlLoader.<ClientController>getController();
//                controller.setClientService(new ClientServiceImpl());
//                controller.setCommonService(new CommonServiceImpl());
//                controller.txtSearch.setText("dsgsadgasf");
////                ClientController controller = new ClientController(new ClientServiceImpl(), new CommonServiceImpl());
////                fxmlLoader.setController(controller);
//                break;
//            default:
//                break;
//        }
//    }

    private void makeFadeInTransition() {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(250));
        fadeTransition.setNode(borderPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void makeFadeOut() {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(250));
        fadeTransition.setNode(borderPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(event -> {
            try {
                loadNextScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fadeTransition.play();
    }

    private void loadNextScene() throws IOException {
        Parent mainPane = (BorderPane) FXMLLoader.load(getClass().getResource(PathConstans.LOGIN_PANE_PATH));
        Scene mainScene = new Scene(mainPane);
        Stage curStage = (Stage) borderPane.getScene().getWindow();
        curStage.setScene(mainScene);
    }
}
