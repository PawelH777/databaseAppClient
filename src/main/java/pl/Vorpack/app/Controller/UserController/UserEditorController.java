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
    private static final String PASSWORD = "*****";
    private static final String PATTERN = "\\b.*[a-zA-Z].*\\b";
    @FXML
    private VBox vBox;
    @FXML
    private Label errorStatus;
    @FXML
    private JFXTextField login = new JFXTextField() {
        @Override
        public void paste() {
        }
    };
    @FXML
    private JFXPasswordField password = new JFXPasswordField() {
        @Override
        public void paste() {
        }
    };
    @FXML
    private JFXPasswordField repeatPassword = new JFXPasswordField() {
        @Override
        public void paste() {
        }
    };
    @FXML
    private JFXComboBox<String> cmbAdminYesNo;
    @FXML
    private
    JFXButton btnProceed;
    @FXML
    private Label statusLabel;

    private UserService userService = new UserServiceImpl();
    private User user = new User();
    private Boolean booleanValue;
    private InfoAlerts infoAlerts = new InfoAlerts();
    private int eventCounter = 0;
    private boolean isFirstLoop = true;

    @FXML
    public void initialize() {
        cmbAdminYesNo.getItems().addAll(
                YES,
                NO
        );
        btnProceed.setDisable(true);
        login.textProperty().addListener((obs, oldValue, newValue) -> {
            checkIfFieldsAreNotEmpty();
        });
        password.textProperty().addListener((obs, oldValue, newValue) -> {
            eventCounter++;
            int loopsNumberWhenUserFirstTimeWriteNewPassword = 2;
            if (eventCounter == loopsNumberWhenUserFirstTimeWriteNewPassword) {
                isFirstLoop = false;
            }
            checkIfFieldsAreNotEmpty();
        });
        repeatPassword.textProperty().addListener((obs, oldValue, newValue) -> {
            checkIfFieldsAreNotEmpty();
        });
        cmbAdminYesNo.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            checkIfFieldsAreNotEmpty();
        });
        if (UsrVariables.getId() != null) {
            user = userService.findById(UsrVariables.getId());
            login.textProperty().setValue(user.getLogin());
            password.textProperty().setValue(PASSWORD);
            repeatPassword.textProperty().setValue(PASSWORD);
            if (user.isAdmin())
                cmbAdminYesNo.setValue(YES);
            else
                cmbAdminYesNo.setValue(NO);
            btnProceed.setText("Zmień");
        }
    }

    public void btnAddClicked() {
        boolean endGate = false;
        if (cmbAdminYesNo.getValue().equals(YES))
            booleanValue = true;
        else if (cmbAdminYesNo.getValue().equals(NO))
            booleanValue = false;
        try {
            boolean isLoginExist = userService.findByLogin(login.getText()) != null;
            String hashedPass = DigestUtils.sha1Hex(password.textProperty().getValue());
            if (user == null && !isLoginExist) {
                user = new User(login.textProperty().getValue(), hashedPass, booleanValue);
                userService.create(user);
                endGate = true;
            } else if(user != null){
                user.setLogin(login.textProperty().getValue());
                if (!isFirstLoop) {
                    user.setPassword(hashedPass);
                }
                user.setAdmin(booleanValue);
                userService.update(user);
                endGate = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            infoAlerts.generalAlert();
        }
        if (endGate) {
            Stage thisStage = (Stage) vBox.getScene().getWindow();
            GlobalVariables.setIsActionCompleted(true);
            thisStage.close();
        } else
            statusLabel.setText("User z tym loginem już istnieje");
    }

    private void checkIfFieldsAreNotEmpty() {
        String passwordValue = password.textProperty().getValue();
        String repeatPasswordValue = repeatPassword.textProperty().getValue();
        String loginValue = login.textProperty().getValue();
        String selectedItemValue = cmbAdminYesNo.getSelectionModel().getSelectedItem();
        if (!loginValue.isEmpty() && !passwordValue.isEmpty() && !repeatPasswordValue.isEmpty() && selectedItemValue != null) {
            if ((passwordValue.equals(repeatPasswordValue) && passwordValue.matches(PATTERN)) || (isFirstLoop &&
                    UsrVariables.getId() != null)) {
                errorStatus.setText("Hasła pasują do siebie");
                btnProceed.setDisable(false);
            } else {
                errorStatus.setText("Hasła nie pasują do siebie");
                btnProceed.setDisable(true);
            }
        } else
            btnProceed.setDisable(true);
    }
}
