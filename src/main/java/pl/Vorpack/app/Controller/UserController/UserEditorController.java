package pl.Vorpack.app.Controller.UserController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Domain.User;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.UsrVariables;
import pl.Vorpack.app.Service.ServiceImpl.UserServiceImpl;
import pl.Vorpack.app.Service.UserService;

/**
 * Created by Paweł on 2018-02-22.
 */
public class UserEditorController {
    private static final String YES = "Tak";
    private static final String NO = "Nie";
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
    private JFXComboBox<String> cmbAdminYesNo;
    @FXML
    private
    JFXButton btnProceed;
    @FXML
    private Label statusLabel;

    private UserService userService = new UserServiceImpl();
    private Boolean booleanValue;
    private InfoAlerts infoAlerts = new InfoAlerts();

    @FXML
    public void initialize(){
        cmbAdminYesNo.getItems().addAll(
                YES,
                NO
        );
        btnProceed.setDisable(true);
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
        if(UsrVariables.getObject() != null){
            User user = UsrVariables.getObject();
            login.textProperty().setValue(user.getLogin());
            password.textProperty().setValue(user.getPassword());
            repeatPassword.textProperty().setValue(user.getPassword());
            if(user.isAdmin())
                cmbAdminYesNo.setValue(YES);
            else
                cmbAdminYesNo.setValue(NO);
            btnProceed.setText("Zmień");
        }
    }

    private void checkIfFieldsAreNotEmpty() {
        if(!login.textProperty().getValue().isEmpty() && !password.textProperty().getValue().isEmpty()
                && !repeatPassword.textProperty().getValue().isEmpty()
                && cmbAdminYesNo.getSelectionModel().getSelectedItem() != null){
            if(password.textProperty().getValue().equals(repeatPassword.textProperty().getValue())){
                errorStatus.setText("Hasła pasują do siebie");
                btnProceed.setDisable(false);
            }
            else{
                errorStatus.setText("Hasła nie pasują do siebie");
                btnProceed.setDisable(true);
            }
        }
        else
            btnProceed.setDisable(true);
    }

    public void btnAddClicked() {
        boolean endGate = false;
        if(cmbAdminYesNo.getValue().equals(YES))
            booleanValue = true;
        else if(cmbAdminYesNo.getValue().equals(NO))
            booleanValue = false;
        try{
            User user =
                    userService.findByLogin(login.getText());
            String hashedPass = DigestUtils.sha1Hex(password.textProperty().getValue());
            if (UsrVariables.getObject() == null ) {
                if(user == null){
                    user = new User(login.textProperty().getValue(), hashedPass, booleanValue);
                    userService.create(user);
                    endGate = true;
                }
            }
            else {
                user = UsrVariables.getObject();
                user.setLogin(login.textProperty().getValue());
                user.setPassword(hashedPass);
                user.setAdmin(booleanValue);
                userService.update(user);
                endGate = true;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }
        if(endGate) {
            Stage thisStage = (Stage) vBox.getScene().getWindow();
            GlobalVariables.setIsActionCompleted(true);
            thisStage.close();
        }
        else
            statusLabel.setText("User z tym loginem już istnieje");
    }
}
