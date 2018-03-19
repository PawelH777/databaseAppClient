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
import pl.Vorpack.app.Properties.mainPaneProperty;
import pl.Vorpack.app.domain.Dimiensions;
import pl.Vorpack.app.global_variables.dimVariables;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.text.ParseException;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Created by Paweł on 2018-02-16.
 */
public class AddDimensionController {

    private Double firstLength;

    private Double secondLength;

    private Double thick;

    private Double weight;

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
            textFirstLength.textProperty().setValue(object.getFirst_dimension().toString());
            textSecondLength.textProperty().setValue(object.getSecond_dimension().toString());
            textThick.textProperty().setValue(object.getThickness().toString());
            textWeight.textProperty().setValue(object.getWeight().toString());
            btnProceed.setText("Zmień");
        }

    }


    public void btnAddClicked(MouseEvent mouseEvent) throws ParseException {

        boolean endGate = false;

        firstLength = Double.parseDouble(textFirstLength.getText());

        secondLength = Double.parseDouble(textSecondLength.getText());

        thick = Double.parseDouble(textThick.getText());

        weight = Double.parseDouble(textWeight.getText());

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
        EntityManager entityManager = entityManagerFactory.createEntityManager();


        entityManager.getTransaction().begin();

        TypedQuery<Dimiensions> existedRecord = entityManager.createQuery("SELECT d FROM Dimiensions d WHERE " +
                "first_dimension = :firstDim AND second_dimension = :secDim " +
                "AND thickness = :thick AND weight = :weight", Dimiensions.class)
                .setParameter("firstDim", firstLength)
                .setParameter("secDim", secondLength)
                .setParameter("thick", thick)
                .setParameter("weight", weight);

        if(existedRecord.getResultList().size() == 0) {
            if (dimVariables.getObject() == null) {

                Dimiensions dim = new Dimiensions(firstLength, secondLength, thick, weight);
                entityManager.persist(dim);
            } else {
                object.setFirst_dimension(firstLength);
                object.setSecond_dimension(secondLength);
                object.setThickness(thick);
                object.setWeight(weight);
                entityManager.merge(object);
            }
            endGate = true;
        }

        entityManager.getTransaction().commit();

        entityManager.close();
        entityManagerFactory.close();

        if(endGate) {
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
