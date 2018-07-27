package pl.Vorpack.app.Controllers;

import java.io.IOException;
import java.util.List;

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
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.DatabaseAccess.UsersAccess;
import pl.Vorpack.app.Domain.User;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;

/**
 * Created by Paweł on 2018-02-01.
 */
public class LoginController {

    private static final String MAIN_PANE_FXML = "/fxml/main/MainPane.fxml";
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
    public void initialize(){
        textAnimations = new TextAnimations(loginStatus);
        loginStatus.setOpacity(0);
        borderPane.setOpacity(0);
        makeFadeIn();
    }

    @FXML
    public void onmouseClicked(){
        String login = loginTextField.getText();
        String haslo = DigestUtils.sha1Hex(passwordTextField.getText());

        UsersAccess usersAccess = new UsersAccess();
        setGlobalAuthentication(login, haslo);
        try{
            List<User> allUsersFromDatabaseWithLogin =
                    usersAccess.findClientsByLogin(login);

            if(allUsersFromDatabaseWithLogin.size() != 1 ){
                loginStatus.setText("Istnieje więcej użytkowników o tym loginie");
                textAnimations.startLabelsPulsing();
            }
            else{
                User user = allUsersFromDatabaseWithLogin.get(0);
                setGlobalAuthentication(login, haslo);
                if(user.isAdmin())
                    GlobalVariables.setAccess("Administrator");
                else
                    GlobalVariables.setAccess("Użytkownik");

                makeFadeOut();
            }
        } catch(Throwable ex){
            loginStatus.setText("Wpisałeś błędny login lub hasło");
            textAnimations.startLabelsPulsing();
        }
    }

    private void setGlobalAuthentication(String login, String haslo) {
        GlobalVariables.setName(login);
        GlobalVariables.setPassword(haslo);
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
        }
        catch (IOException e) {
            InfoAlerts.viewErrorIfIsNotLoadingNextScene();
        }
    }
}
