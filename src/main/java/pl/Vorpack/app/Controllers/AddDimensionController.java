package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.Vorpack.app.DatabaseAccess.DimensionsAccess;
import pl.Vorpack.app.Properties.MainPaneProperty;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.GlobalVariables.DimVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;

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
    private JFXButton btnProceed;
    @FXML
    private Label statusLabel;

    private DimensionsAccess dimensionsAccess = new DimensionsAccess();
    private Boolean isModify = false;
    private MainPaneProperty dimProperty = new MainPaneProperty();
    Boolean a = false, b = false, c = false, d = false;
    private Dimiensions object;
    public AddDimensionController() {
        object = new Dimiensions();
    }

    @FXML
    public void initialize(){

        setFormatters();
        textFirstLength.textProperty().addListener((obs, oldValue, newValue) -> {
            checkIfFieldsAreEmpty();
        });

        textSecondLength.textProperty().addListener((obs, oldValue, newValue) -> {
            checkIfFieldsAreEmpty();
        });

        textThick.textProperty().addListener((obs, oldValue, newValue) -> {
            checkIfFieldsAreEmpty();
        });

        textWeight.textProperty().addListener((obs, oldValue, newValue) -> {
            checkIfFieldsAreEmpty();
        });

        btnProceed.disableProperty().bindBidirectional(dimProperty.disableBtnProperty());

        if(DimVariables.getObject() != null){
            object = DimVariables.getObject();
            setFieldsInForm();
            btnProceed.setText("Zmień");
            isModify = true;
        }

    }

    private void setFormatters(){
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
    }

    private void checkIfFieldsAreEmpty(){
        if(!textFirstLength.textProperty().getValue().isEmpty() && !textSecondLength.textProperty().getValue().isEmpty() &&
                !textThick.textProperty().getValue().isEmpty() && !textWeight.textProperty().getValue().isEmpty()){
            dimProperty.setDisableBtn(false);
        }
        else{
            dimProperty.setDisableBtn(true);
        }
    }

    private void setFieldsInForm(){
        textFirstLength.textProperty().setValue(object.getFirstDimension().toString());
        textSecondLength.textProperty().setValue(object.getSecondDimension().toString());
        textThick.textProperty().setValue(object.getThickness().toString());
        textWeight.textProperty().setValue(object.getWeight().toString());
    }


    public void btnAddClicked(MouseEvent mouseEvent) throws ParseException {

        boolean endGate = false;

        BigDecimal firstLength = BigDecimal.valueOf(Double.parseDouble(textFirstLength.getText()));
        BigDecimal secondLength = BigDecimal.valueOf(Double.parseDouble(textSecondLength.getText()));
        BigDecimal thick = BigDecimal.valueOf(Double.parseDouble(textThick.getText()));
        BigDecimal weight = BigDecimal.valueOf(Double.parseDouble(textWeight.getText()));

        object.setFirstDimension(firstLength);
        object.setSecondDimension(secondLength);
        object.setThickness(thick);
        object.setWeight(weight);

        try{
            List<Dimiensions> existingRecords = dimensionsAccess.findDimension(object);

            if(DimVariables.getObject() == null) {
                if (existingRecords.size() == 0) {
                    dimensionsAccess.createDimension(object);
                    endGate = true;
                }
                else
                    endGate = false;

            }
            else if(DimVariables.getObject() != null){
                object.setFirstDimension(firstLength);
                object.setSecondDimension(secondLength);
                object.setThickness(thick);
                object.setWeight(weight);

                dimensionsAccess.updateDimension(object);
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
            statusLabel.setText("Wymiar z wpisanymi danymi już istnieje");
        }
    }

}
