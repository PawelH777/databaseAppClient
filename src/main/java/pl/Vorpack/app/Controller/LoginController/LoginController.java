package pl.Vorpack.app.Controller.LoginController;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.codec.digest.DigestUtils;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Constans.Path;
import pl.Vorpack.app.Domain.User;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Service.ServiceImpl.UserServiceImpl;
import pl.Vorpack.app.Service.UserService;

import java.io.IOException;

import static pl.Vorpack.app.Constans.User.ADMIN;
import static pl.Vorpack.app.Constans.User.USER;

/**
 * Created by Paweł on 2018-02-01.
 */
public class LoginController {
    @FXML
    private BorderPane borderPane;
    @FXML
    private JFXTextField loginTextField;
    @FXML
    private Label loginStatus;
    @FXML
    private JFXPasswordField passwordTextField;

    private TextAnimations textAnimations;

    @FXML
    public void initialize() {
        textAnimations = new TextAnimations(loginStatus);
        loginStatus.setOpacity(0);
        borderPane.setOpacity(0);
        fadeIn();
    }

    @FXML
    public void onMouseClicked() {
        String login = loginTextField.getText();
        String password = DigestUtils.sha1Hex(passwordTextField.getText());
        UserService userService = new UserServiceImpl();
        setGlobalAuthentication(login, password);
        try {
            User user = userService.findByLogin(login);
            setGlobalAuthentication(login, password);
            if (user.isAdmin())
                GlobalVariables.setAccess(ADMIN);
            else
                GlobalVariables.setAccess(USER);
            fadeOut();
        } catch(RuntimeException ex) {
            loginStatus.setText("Istnieje więcej użytkowników o tym loginie");
            textAnimations.startLabelsPulsing();
        }
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER)
            onMouseClicked();
    }

    private void setGlobalAuthentication(String login, String haslo) {
        GlobalVariables.setName(login);
        GlobalVariables.setPassword(haslo);
    }

    private void fadeOut() {
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

    private void fadeIn() {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(250));
        fadeTransition.setNode(borderPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void loadNextScene() {
        try {
            Parent mainPane;
            mainPane = (BorderPane) FXMLLoader.load(getClass().getResource(Path.MAIN_PANE_PATH));
            Scene mainScene = new Scene(mainPane);
            Stage curStage = (Stage) borderPane.getScene().getWindow();
            curStage.setScene(mainScene);
            curStage.setMaximized(true);
        } catch (IOException e) {
            InfoAlerts.viewErrorIfIsNotLoadingNextScene();
        }
    }
}
