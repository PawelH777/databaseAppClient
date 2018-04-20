package pl.Vorpack.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import pl.Vorpack.app.Properties.mainPaneProperty;
import pl.Vorpack.app.domain.User;
import pl.Vorpack.app.global_variables.userData;
import pl.Vorpack.app.global_variables.usrVariables;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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
    private Label mainLabel;

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
    JFXButton btnProceed;


    mainPaneProperty usrProperty = new mainPaneProperty();

    User object = new User();

    private Boolean booleanValue;

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
        }
    }

    public void btnAddClicked(MouseEvent mouseEvent) throws ParseException {

        boolean endGate = false;

        if(cmbAdminYesNo.getValue()== "Tak")
            booleanValue = true;
        else if(cmbAdminYesNo.getValue() == "Nie")
            booleanValue = false;

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                .nonPreemptive()
                .credentials(userData.getName(), userData.getPassword())
                .build();

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(feature);

        Client client = ClientBuilder.newClient(clientConfig);

        String URI = "http://localhost:8080/users/user/login";

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

                URI = "http://localhost:8080/users/createuser";

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
            URI = "http://localhost:8080/users/user/update";
            response =  client
                    .target(URI)
                    .path(String.valueOf(object.getUser_id()))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .put(Entity.entity(object, MediaType.APPLICATION_JSON_TYPE));
            endGate = true;
        }


        client.close();


        if(endGate) {
            Stage thisStage = (Stage) vBox.getScene().getWindow();

            thisStage.close();
        }
        else if(!endGate){
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Uwaga!");
            infoAlert.setHeaderText("Pojawił się błąd");
            infoAlert.setContentText("User z tym loginem już istnieje");
            infoAlert.showAndWait();
        }
    }
}
