package pl.Vorpack.app.Controller.DimensionController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.GlobalVariables.DimVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Service.DimensionService;
import pl.Vorpack.app.Service.ServiceImpl.DimensionServiceImpl;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Created by Paweł on 2018-02-16.
 */
public class DimensionEditorController {

    @FXML
    private VBox vBox;
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

    private DimensionService dimensionService = new DimensionServiceImpl();
    private Dimiensions dim;
    public DimensionEditorController() {
        dim = new Dimiensions();
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
        btnProceed.setDisable(true);
        if(DimVariables.getObject() != null){
            dim = DimVariables.getObject();
            setFieldsInForm();
            btnProceed.setText("Zmień");
        }
    }

    public void btnAddClicked() {
        boolean endGate = false;
        BigDecimal firstLength = BigDecimal.valueOf(Double.parseDouble(textFirstLength.getText()));
        BigDecimal secondLength = BigDecimal.valueOf(Double.parseDouble(textSecondLength.getText()));
        BigDecimal thick = BigDecimal.valueOf(Double.parseDouble(textThick.getText()));
        BigDecimal weight = BigDecimal.valueOf(Double.parseDouble(textWeight.getText()));
        dim.setFirstDimension(firstLength);
        dim.setSecondDimension(secondLength);
        dim.setThickness(thick);
        dim.setWeight(weight);
        try{
            if(DimVariables.getObject() == null) {
                if (dimensionService.find(dim) == null) {
                    dimensionService.create(dim);
                    endGate = true;
                }
            }
            else{
                dimensionService.update(dim);
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
        else
            statusLabel.setText("Wymiar z wpisanymi danymi już istnieje");
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
                !textThick.textProperty().getValue().isEmpty() && !textWeight.textProperty().getValue().isEmpty())
            btnProceed.setDisable(false);
        else
            btnProceed.setDisable(true);
    }

    private void setFieldsInForm(){
        textFirstLength.textProperty().setValue(dim.getFirstDimension().toString());
        textSecondLength.textProperty().setValue(dim.getSecondDimension().toString());
        textThick.textProperty().setValue(dim.getThickness().toString());
        textWeight.textProperty().setValue(dim.getWeight().toString());
    }
}
