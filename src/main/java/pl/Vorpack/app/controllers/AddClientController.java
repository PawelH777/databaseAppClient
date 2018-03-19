package pl.Vorpack.app.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.Vorpack.app.Properties.mainPaneProperty;
import pl.Vorpack.app.domain.Client;
import pl.Vorpack.app.global_variables.cliVariables;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.text.ParseException;

/**
 * Created by Paweł on 2018-02-21.
 */
public class AddClientController {

    private String name;

    @FXML
    private VBox vBox;

    @FXML
    private Label mainLabel;

    @FXML
    private JFXTextField textFirmName = new JFXTextField(){
        @Override
        public void paste(){}
    };

    @FXML
    JFXButton btnProceed;


    mainPaneProperty cliProperty = new mainPaneProperty();

    Client object = new Client();

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
            textFirmName.textProperty().setValue(object.getFirm_name().toString());
            btnProceed.setText("Zmień");
        }
    }

    public void btnAddClicked(MouseEvent mouseEvent) throws ParseException {

        boolean endGate = false;
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
        EntityManager entityManager = entityManagerFactory.createEntityManager();


        entityManager.getTransaction().begin();

        TypedQuery<Client> existedRecord = entityManager.createQuery("SELECT c FROM Client c WHERE firm_name = :firmName", Client.class)
                .setParameter("firmName", textFirmName.textProperty().getValue());

        if(existedRecord.getResultList().size() == 0) {
            if (cliVariables.getObject() == null) {
                Client cli = new Client(textFirmName.textProperty().getValue());
                entityManager.persist(cli);

            } else {
                object.setFirm_name(textFirmName.textProperty().getValue());
                entityManager.merge(object);
            }

            endGate = true;
        }

        entityManager.getTransaction().commit();

        entityManager.close();
        entityManagerFactory.close();


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
