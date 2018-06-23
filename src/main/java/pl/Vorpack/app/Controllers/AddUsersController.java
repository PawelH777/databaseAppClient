package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;
import pl.Vorpack.app.DatabaseAccess.UsersAccess;
import pl.Vorpack.app.Properties.MainPaneProperty;
import pl.Vorpack.app.Domain.User;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.UsrVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;

import java.text.ParseException;
import java.util.List;

/**
 * Created by Paweł on 2018-02-22.
 */
public class AddUsersController {
    @FXML
    private VBox vBox;
    @FXML
    private Label errorStatus;
    @FXML
    private JFXTextField login = new JFXTextField(){
        @Override
        public void paste(){}
    };
    @FXML
    private JFXPasswordField password = new JFXPasswordField(){
        @Override
        public void paste(){}
    };
    @FXML
    private JFXPasswordField repeatPassword = new JFXPasswordField(){
        @Override
        public void paste(){}
    };
    @FXML
    private JFXComboBox cmbAdminYesNo = new JFXComboBox();
    @FXML
    private
    JFXButton btnProceed;
    @FXML
    private Label statusLabel;

    private UsersAccess usersAccess = new UsersAccess();
    private MainPaneProperty usrProperty = new MainPaneProperty();
    private User userObject = new User();

    private Boolean booleanValue;
    private Boolean isModify = false;

    @FXML
    public void initialize(){

        cmbAdminYesNo.getItems().addAll(
                "Tak",
                "Nie"
        );

        login.textProperty().addListener((obs, oldValue, newValue) -> {
            checkIfFieldsAreNotEmpty();
        });

        password.textProperty().addListener((obs, oldValue, newValue) -> {
            checkIfFieldsAreNotEmpty();
        });

        repeatPassword.textProperty().addListener((obs, oldValue, newValue) -> {
            checkIfFieldsAreNotEmpty();
        });

        cmbAdminYesNo.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            checkIfFieldsAreNotEmpty();
        });

        btnProceed.disableProperty().bindBidirectional(usrProperty.disableBtnProperty());

        if(UsrVariables.getObject() != null){
            userObject = UsrVariables.getObject();
            login.textProperty().setValue(userObject.getLogin());
            password.textProperty().setValue(userObject.getPassword());
            repeatPassword.textProperty().setValue(userObject.getPassword());

            if(userObject.isAdmin())
                cmbAdminYesNo.setValue("Tak");
            else
                cmbAdminYesNo.setValue("Nie");

            btnProceed.setText("Zmień");
            isModify = true;
        }
    }

    private void checkIfFieldsAreNotEmpty() {
        if(!login.textProperty().getValue().isEmpty() && !password.textProperty().getValue().isEmpty()
                && !repeatPassword.textProperty().getValue().isEmpty()
                && cmbAdminYesNo.getSelectionModel().getSelectedItem() != null){
            if(password.textProperty().getValue().equals(repeatPassword.textProperty().getValue())){
                errorStatus.setText("Hasła pasują do siebie");
                usrProperty.setDisableBtn(false);
            }
            else{
                errorStatus.setText("Hasła nie pasują do siebie");
                usrProperty.setDisableBtn(true);
            }
        }
        else{
            usrProperty.setDisableBtn(true);
        }
    }

    public void btnAddClicked(MouseEvent mouseEvent) throws ParseException {

        boolean endGate = false;

        if(cmbAdminYesNo.getValue()== "Tak")
            booleanValue = true;
        else if(cmbAdminYesNo.getValue() == "Nie")
            booleanValue = false;

        try{
            List<User> allClientsFromDatabase =
                    usersAccess.findClientsByLogin(login.getText());

            String hashedPass = DigestUtils.sha1Hex(password.textProperty().getValue());

            if (UsrVariables.getObject() == null ) {
                if(allClientsFromDatabase.size() == 0){
                    userObject = new User(login.textProperty().getValue(), hashedPass, booleanValue);
                    usersAccess.createNewUser(userObject);
                    endGate = true;
                }
                else
                    endGate = false;
            }
            else if(UsrVariables.getObject() != null){
                userObject.setLogin(login.textProperty().getValue());
                userObject.setPassword(hashedPass);
                userObject.setAdmin(booleanValue);
                usersAccess.updateUser(userObject);
                endGate = true;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }

        if(endGate) {
            Stage thisStage = (Stage) vBox.getScene().getWindow();
            GlobalVariables.setIsActionCompleted(true);
            thisStage.close();
        }
        else if(!endGate){
            statusLabel.setText("User z tym loginem już istnieje");
        }
    }
}
