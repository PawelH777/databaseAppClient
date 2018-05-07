package pl.Vorpack.app.controllers;

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
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import pl.Vorpack.app.DatabaseAccess;
import pl.Vorpack.app.Properties.mainPaneProperty;
import pl.Vorpack.app.domain.User;
import pl.Vorpack.app.global_variables.GlobalVariables;
import pl.Vorpack.app.global_variables.usrVariables;
import pl.Vorpack.app.infoAlerts;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.ArrayList;
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

    private mainPaneProperty usrProperty = new mainPaneProperty();

    private User object = new User();

    private Boolean booleanValue;

    private Boolean isModify = false;

    @FXML
    public void initialize(){

        cmbAdminYesNo.getItems().addAll(
                "Tak",
                "Nie"
        );

        login.textProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue.isEmpty() && !password.textProperty().getValue().isEmpty()
                    && !repeatPassword.textProperty().getValue().isEmpty()
                    && cmbAdminYesNo.getSelectionModel().getSelectedItem() != null
                    && password.textProperty().getValue().equals(repeatPassword.textProperty().getValue())){
                usrProperty.setDisableBtnProoced(false);
            }else{
                usrProperty.setDisableBtnProoced(true);
            }
        });

        password.textProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue.isEmpty() && !login.textProperty().getValue().isEmpty()
                    && !repeatPassword.textProperty().getValue().isEmpty()
                    && cmbAdminYesNo.getSelectionModel().getSelectedItem() != null
                    && newValue.equals(repeatPassword.textProperty().getValue())){
                usrProperty.setDisableBtnProoced(false);
            }else{
                usrProperty.setDisableBtnProoced(true);
            }

            if(!newValue.isEmpty() && !repeatPassword.textProperty().getValue().isEmpty()
                    && newValue.equals(repeatPassword.textProperty().getValue()))
                errorStatus.setText("Hasła pasują do siebie");
            else if(!newValue.isEmpty() && !repeatPassword.textProperty().getValue().isEmpty()
                    && !newValue.equals(repeatPassword.textProperty().getValue()))
                errorStatus.setText("Hasła nie pasują do siebie");
        });

        repeatPassword.textProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue.isEmpty() && !login.textProperty().getValue().isEmpty()
                    && !password.textProperty().getValue().isEmpty()
                    && cmbAdminYesNo.getSelectionModel().getSelectedItem() != null
                    && newValue.equals(password.textProperty().getValue())){
                usrProperty.setDisableBtnProoced(false);
            }else{
                usrProperty.setDisableBtnProoced(true);
            }

            if(!newValue.isEmpty() && !password.textProperty().getValue().isEmpty()
                    && newValue.equals(password.textProperty().getValue()))
                errorStatus.setText("Hasła pasują do siebie");
            else if(!newValue.isEmpty() && !password.textProperty().getValue().isEmpty()
                    && !newValue.equals(password.textProperty().getValue()))
                errorStatus.setText("Hasła nie pasują do siebie");
        });

        cmbAdminYesNo.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null && !login.textProperty().getValue().isEmpty()
                    && !password.textProperty().getValue().isEmpty()
                    && !repeatPassword.textProperty().getValue().isEmpty()
                    && password.textProperty().getValue().equals(repeatPassword.textProperty().getValue())){
                usrProperty.setDisableBtnProoced(false);
            }else{
                usrProperty.setDisableBtnProoced(true);
            }
        });

        btnProceed.disableProperty().bindBidirectional(usrProperty.disableBtnProocedProperty());

        if(usrVariables.getObject() != null){
            object = usrVariables.getObject();
            login.textProperty().setValue(object.getLogin().toString());
            password.textProperty().setValue(object.getPassword().toString());
            repeatPassword.textProperty().setValue(object.getPassword().toString());

            if(object.isAdmin())
                cmbAdminYesNo.setValue("Tak");
            else
                cmbAdminYesNo.setValue("Nie");

            btnProceed.setText("Zmień");

            isModify = true;
        }
    }

    public void btnAddClicked(MouseEvent mouseEvent) throws ParseException {

        boolean endGate = false;

        if(cmbAdminYesNo.getValue()== "Tak")
            booleanValue = true;
        else if(cmbAdminYesNo.getValue() == "Nie")
            booleanValue = false;


        try{

            Client client = DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());

            String URI = GlobalVariables.getSite_name() + "/users/user/login";

            Response response = client
                    .target(URI)
                    .path(login.getText())
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            List<User> existedRecord = response.readEntity(new GenericType<ArrayList<User>>(){});

            String pass = DigestUtils.sha1Hex(password.textProperty().getValue());

            if (usrVariables.getObject() == null ) {

                if(existedRecord.size() == 0){
                    User cli = new User(login.textProperty().getValue(), pass, booleanValue);

                    URI = GlobalVariables.getSite_name() + "/users/createuser";

                    response =  client
                            .target(URI)
                            .request(MediaType.APPLICATION_JSON_TYPE)
                            .post(Entity.entity(cli, MediaType.APPLICATION_JSON_TYPE));
                    endGate = true;
                }
                else{
                    endGate = false;
                }
            }else if(usrVariables.getObject() != null){
                object.setLogin(login.textProperty().getValue());
                object.setPassword(pass);
                object.setAdmin(booleanValue);
                URI = GlobalVariables.getSite_name() + "/users/user/update";
                response =  client
                        .target(URI)
                        .path(String.valueOf(object.getUser_id()))
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .put(Entity.entity(object, MediaType.APPLICATION_JSON_TYPE));
                endGate = true;
            }


            client.close();
        } catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
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
