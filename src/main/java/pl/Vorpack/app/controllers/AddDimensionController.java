package pl.Vorpack.app.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import pl.Vorpack.app.Properties.mainPaneProperty;
import pl.Vorpack.app.domain.Dimiensions;
import pl.Vorpack.app.global_variables.dimVariables;
import pl.Vorpack.app.global_variables.userData;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Created by Paweł on 2018-02-16.
 */
public class AddDimensionController {

    @FXML
    private VBox vBox;

    @FXML
    private Label mainLabel;

    @FXML
    private JFXTextField textFirstLength = new JFXTextField(){
        @Override
        public void paste(){}
    };

    @FXML
    private JFXTextField textSecondLength = new JFXTextField(){
        @Override
        public void paste(){}
    };

    @FXML
    private JFXTextField textThick = new JFXTextField(){
        @Override
        public void paste(){}
    };

    @FXML
    private JFXTextField textWeight = new JFXTextField(){
        @Override
        public void paste(){}
    };

    @FXML
    JFXButton btnProceed;


    mainPaneProperty dimProperty = new mainPaneProperty();

    Boolean a = false, b = false, c = false, d = false;

    Dimiensions object = new Dimiensions();

    @FXML
    public void initialize(){

        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        TextFormatter formatter2 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        TextFormatter formatter3 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        TextFormatter formatter4 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        textFirstLength.setTextFormatter(formatter);
        textSecondLength.setTextFormatter(formatter2);
        textThick.setTextFormatter(formatter3);
        textWeight.setTextFormatter(formatter4);

        textFirstLength.textProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue.isEmpty() && !textSecondLength.textProperty().getValue().isEmpty() &&
            !textThick.textProperty().getValue().isEmpty() && !textWeight.textProperty().getValue().isEmpty()){
                dimProperty.setDisableBtnProoced(false);
            }else{
                dimProperty.setDisableBtnProoced(true);
            }
        });

        textSecondLength.textProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue.isEmpty() && !textFirstLength.textProperty().getValue().isEmpty() &&
                    !textThick.textProperty().getValue().isEmpty() && !textWeight.textProperty().getValue().isEmpty()){
                dimProperty.setDisableBtnProoced(false);
            }else{
                dimProperty.setDisableBtnProoced(true);
            }
        });

        textThick.textProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue.isEmpty() && !textSecondLength.textProperty().getValue().isEmpty() &&
                    !textFirstLength.textProperty().getValue().isEmpty() && !textWeight.textProperty().getValue().isEmpty()){
                dimProperty.setDisableBtnProoced(false);
            }else{
                dimProperty.setDisableBtnProoced(true);
            }
        });

        textWeight.textProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue.isEmpty() && !textSecondLength.textProperty().getValue().isEmpty() &&
                    !textThick.textProperty().getValue().isEmpty() && !textFirstLength.textProperty().getValue().isEmpty()){
                dimProperty.setDisableBtnProoced(false);
            }else{
                dimProperty.setDisableBtnProoced(true);
            }
        });

        btnProceed.disableProperty().bindBidirectional(dimProperty.disableBtnProocedProperty());

        if(dimVariables.getObject() != null){
            object = dimVariables.getObject();
            textFirstLength.textProperty().setValue(object.getFirstDimension().toString());
            textSecondLength.textProperty().setValue(object.getSecondDimension().toString());
            textThick.textProperty().setValue(object.getThickness().toString());
            textWeight.textProperty().setValue(object.getWeight().toString());
            btnProceed.setText("Zmień");
        }

    }


    public void btnAddClicked(MouseEvent mouseEvent) throws ParseException {

        boolean endGate = false;

        BigDecimal firstLength = BigDecimal.valueOf(Double.parseDouble(textFirstLength.getText()));

        BigDecimal secondLength = BigDecimal.valueOf(Double.parseDouble(textSecondLength.getText()));

        BigDecimal thick = BigDecimal.valueOf(Double.parseDouble(textThick.getText()));

        BigDecimal weight = BigDecimal.valueOf(Double.parseDouble(textWeight.getText()));

        Dimiensions dimObj = new Dimiensions();

        dimObj.setFirstDimension(firstLength);
        dimObj.setSecondDimension(secondLength);
        dimObj.setThickness(thick);
        dimObj.setWeight(weight);

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                .nonPreemptive()
                .credentials(userData.getName(), userData.getPassword())
                .build();

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(feature);

        Client client = ClientBuilder.newClient(clientConfig);

        String URI = "http://localhost:8080/dims/dim/find";

        Response response = client
                .target(URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(dimObj,MediaType.APPLICATION_JSON_TYPE));

        List<Dimiensions> existingRecords = response.readEntity(new GenericType<List<Dimiensions>>(){});

        if(dimVariables.getObject() == null) {
            if (existingRecords.size() == 0) {

                Dimiensions dim = new Dimiensions(firstLength, secondLength, thick, weight);
                URI = "http://localhost:8080/dims/createdim";

                response = client
                        .target(URI)
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.entity(dim, MediaType.APPLICATION_JSON_TYPE));

                endGate = true;
            } else
                 endGate = false;

        } else if(dimVariables.getObject() != null){
            object.setFirstDimension(firstLength);
            object.setSecondDimension(secondLength);
            object.setThickness(thick);
            object.setWeight(weight);

            URI = "http://localhost:8080/dims/dim/update";

            response = client
                    .target(URI)
                    .path(String.valueOf(object.getDimension_id()))
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
            infoAlert.setContentText("Wymiar z wpisanymi danymi już istnieje");
            infoAlert.showAndWait();
        }
    }

}
