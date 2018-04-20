package pl.Vorpack.app.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import pl.Vorpack.app.Properties.mainPaneProperty;
import pl.Vorpack.app.domain.Client;
import pl.Vorpack.app.global_variables.cliVariables;
import pl.Vorpack.app.global_variables.userData;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 2018-02-21.
 */
public class AddClientController {


    @FXML
    private VBox vBox;

    @FXML
    private JFXTextField textFirmName = new JFXTextField(){
        @Override
        public void paste(){}
    };

    @FXML
    private
    JFXButton btnProceed;


    private mainPaneProperty cliProperty = new mainPaneProperty();

    private Client object = new Client();

    @FXML
    public void initialize(){

        textFirmName.textProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue.isEmpty()){
                cliProperty.setDisableBtnProoced(false);
            }else{
                cliProperty.setDisableBtnProoced(true);
            }
        });

        btnProceed.disableProperty().bindBidirectional(cliProperty.disableBtnProocedProperty());

        if(cliVariables.getObject() != null){
            object = cliVariables.getObject();
            textFirmName.textProperty().setValue(object.getFirmName());
            btnProceed.setText("Zmień");
        }
    }

    public void btnAddClicked(MouseEvent mouseEvent) throws ParseException {

        boolean endGate = false;

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                .nonPreemptive()
                .credentials(userData.getName(), userData.getPassword())
                .build();

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(feature);

        javax.ws.rs.client.Client client = ClientBuilder.newClient(clientConfig);

        String URI = "http://localhost:8080/clients/client/firmname";

        Client cli = new Client();

        cli.setFirmName(textFirmName.getText());

        Response response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(cli, MediaType.APPLICATION_JSON_TYPE));

        List<Client> existedRecord = response.readEntity(new GenericType<ArrayList<Client>>(){});

        if(cliVariables.getObject() == null) {
            if(existedRecord.size() == 0){
                URI = "http://localhost:8080/clients/createclient";

                response = client
                        .target(URI)
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.entity(cli, MediaType.APPLICATION_JSON_TYPE));
                endGate = true;
            }else
                endGate = false;
        } else if(cliVariables.getObject() != null){

            object.setFirmName(textFirmName.getText());
            URI = "http://localhost:8080/clients/client/update";

            response = client
                    .target(URI)
                    .path(String.valueOf(object.getClient_id()))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .put(Entity.entity(object, MediaType.APPLICATION_JSON_TYPE));
            endGate = true;
        }

        client.close();

        if(endGate){

            Stage thisStage = (Stage) vBox.getScene().getWindow();

            thisStage.close();
        }
        else if(!endGate){
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Uwaga!");
            infoAlert.setHeaderText("Pojawił się błąd");
            infoAlert.setContentText("Firma z wpisanymi danymi już istnieje");
            infoAlert.showAndWait();
        }

    }


}
