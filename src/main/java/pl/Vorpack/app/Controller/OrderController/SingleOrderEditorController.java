package pl.Vorpack.app.Controller.OrderController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Dto.DimensionDTO;
import pl.Vorpack.app.Enums.DimensionEnum;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.SingleOrdVariables;
import pl.Vorpack.app.HelperClassess.Filter;
import pl.Vorpack.app.HelperClassess.Provider;
import pl.Vorpack.app.HelperClassess.SuggestionProvidersSets;
import pl.Vorpack.app.HelperClassess.SuggestionProviders;
import pl.Vorpack.app.Service.DimensionService;
import pl.Vorpack.app.Service.MathService;
import pl.Vorpack.app.Service.ServiceImpl.DimensionServiceImpl;
import pl.Vorpack.app.Service.ServiceImpl.MathServiceImpl;
import pl.Vorpack.app.Service.ServiceImpl.SingleOrdersServiceImpl;
import pl.Vorpack.app.Service.SingleOrdersService;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class SingleOrderEditorController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXTextField txtFirstDim;
    @FXML
    private JFXTextField txtSecondDim;
    @FXML
    private JFXTextField txtThick;
    @FXML
    private JFXTextField txtWeight;
    @FXML
    private JFXTextField txtLength;
    @FXML
    private JFXTextField txtQuantityOnTray;
    @FXML
    private JFXTextField txtTrays;
    @FXML
    private JFXTextField txtQuantityOverall;
    @FXML
    private JFXTextField txtMetrs;
    @FXML
    private JFXTextField txtMaterials;
    @FXML
    private JFXTextField txtCommentary;
    @FXML
    private Label statusViewer;
    @FXML
    private JFXButton btnClean;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXCheckBox checkBox;

    private DimensionService dimensionService = new DimensionServiceImpl();
    private MathService mathService = new MathServiceImpl();
    private SingleOrdersService singleOrdersService = new SingleOrdersServiceImpl();

    private Boolean blockAutoFill = false;
    private Boolean blockFiltering = false;
    private SingleOrders singleOrder = new SingleOrders();
    private Orders order = new Orders();
    private Dimiensions dimension = new Dimiensions();
    private DimensionDTO setsValues;
    private DimensionDTO fieldsValues;
    private SuggestionProvidersSets sets = new SuggestionProvidersSets();
    private SuggestionProviders providers;

    private Provider firstDim = new Provider();
    private Provider secondDim = new Provider();
    private Provider thick = new Provider();
    private Provider weight = new Provider();

    private ObservableList<Dimiensions> proxyToDimensionFields;
    private FilteredList<Dimiensions> filteredDims;

    @FXML
    public void initialize() {
        TextAnimations textAnimations = new TextAnimations(statusViewer);
        formatFields();
        getDimensions();
        firstDim.set(sets.firstDim, txtFirstDim);
        secondDim.set(sets.secondDim, txtSecondDim);
        thick.set(sets.thick, txtThick);
        weight.set(sets.weight, txtWeight);
        providers = new SuggestionProviders(firstDim.get(), secondDim.get(), thick.get(), weight.get());
        order = OrdVariables.getOrderObject();

        if (!GlobalVariables.getIsCreate()) {
            getExistingSingleOrder();
        }

        txtFirstDim.textProperty().addListener((obs, oldValue, newValue) -> {
            changeButtonsDisableValue(obs);
            fieldsValues = new DimensionDTO(newValue,
                    txtSecondDim.getText(), txtThick.getText(), txtWeight.getText());
            if (!newValue.isEmpty() && !blockFiltering) {
                filteringDimsTextFields(DimensionEnum.FIRST_DIM);
                sets.update(filteredDims);
                refreshingSuggestions();
            }

            if (newValue.isEmpty())
                dimension.setFirstDimension(BigDecimal.ZERO);
            else
                dimension.setFirstDimension(BigDecimal.valueOf(Double.valueOf(newValue)));
        });

        txtSecondDim.textProperty().addListener((obs, oldValue, newValue) -> {
            changeButtonsDisableValue(obs);
            fieldsValues = new DimensionDTO(txtFirstDim.getText(),
                    newValue, txtThick.getText(), txtWeight.getText());
            if (!newValue.isEmpty() && !blockFiltering) {
                filteringDimsTextFields(DimensionEnum.SECOND_DIM);
                sets.update(filteredDims);
                refreshingSuggestions();
            }
            if (newValue.isEmpty())
                dimension.setSecondDimension(BigDecimal.ZERO);
            else
                dimension.setSecondDimension(BigDecimal.valueOf(Double.valueOf(newValue)));
        });

        txtThick.textProperty().addListener((obs, oldValue, newValue) -> {
            changeButtonsDisableValue(obs);
            fieldsValues = new DimensionDTO(txtFirstDim.getText(), txtSecondDim.getText(),
                    newValue, txtWeight.getText());
            if (!newValue.isEmpty() && !blockFiltering) {
                filteringDimsTextFields(DimensionEnum.THICK);
                sets.update(filteredDims);
                refreshingSuggestions();
            }
            if (newValue.isEmpty())
                dimension.setThickness(BigDecimal.ZERO);
            else
                dimension.setThickness(BigDecimal.valueOf(Double.valueOf(newValue)));
        });

        txtWeight.textProperty().addListener((obs, oldValue, newValue) -> {
            setMaterials();
            changeButtonsDisableValue(obs);
            fieldsValues = new DimensionDTO(txtFirstDim.getText(), txtSecondDim.getText(),
                    txtThick.getText(), newValue);
            if (!newValue.isEmpty() && !blockFiltering) {
                filteringDimsTextFields(DimensionEnum.WEIGHT);
                sets.update(filteredDims);
                refreshingSuggestions();
            }
        });

        checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue)
                blockFiltering = false;
            else {
                blockFiltering = true;
                filteredDims = new FilteredList<>(proxyToDimensionFields, p -> true);
                sets.update(filteredDims);
                providers.refreshSuggestions(sets, providers);
            }
        });

        txtLength.textProperty().addListener((obs, oldValue, newValue) -> {
            setMetrs();
            changeButtonsDisableValue(obs);
        });

        txtQuantityOnTray.textProperty().addListener((obs, oldValue, newValue) -> {
            setOverallQuantity();
            changeButtonsDisableValue(obs);
        });

        txtTrays.textProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            setOverallQuantity();
            changeButtonsDisableValue(obs);
        });

        txtQuantityOverall.textProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            setMetrs();
            changeButtonsDisableValue(obs);
        });

        txtMetrs.textProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            setMaterials();
            changeButtonsDisableValue(obs);
        });

        txtMaterials.textProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            changeButtonsDisableValue(obs);
        });
    }

    private void formatFields() {
        txtFirstDim.setTextFormatter(getFormatter());
        txtSecondDim.setTextFormatter(getFormatter());
        txtThick.setTextFormatter(getFormatter());
        txtWeight.setTextFormatter(getFormatter());
        txtLength.setTextFormatter(getFormatter());
        txtQuantityOnTray.setTextFormatter(getFormatter());
        txtTrays.setTextFormatter(getFormatter());
        txtQuantityOverall.setTextFormatter(getFormatter());
        txtMetrs.setTextFormatter(getFormatter());
        txtMaterials.setTextFormatter(getFormatter());
    }

    private TextFormatter getFormatter() {
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        return new TextFormatter((UnaryOperator<TextFormatter.Change>) change
                -> pattern.matcher(change.getControlNewText()).matches() ? change : null);
    }

    private void getDimensions() {
        List<Dimiensions> allDimensionsInDatabase = dimensionService.findAll();
        proxyToDimensionFields = FXCollections.observableArrayList(allDimensionsInDatabase);
        filteredDims = new FilteredList<>(proxyToDimensionFields, p -> true);
        sets.update(filteredDims);
    }

    private void getExistingSingleOrder() {
        singleOrder = SingleOrdVariables.getSingleOrderObject();
        dimension = new Dimiensions(singleOrder.getDimension().getDimension_id(),
                singleOrder.getDimension().getFirstDimension(), singleOrder.getDimension().getSecondDimension(),
                singleOrder.getDimension().getThickness(), singleOrder.getDimension().getWeight());
        attachValuesToFields();
    }

    private void attachValuesToFields() {
        txtFirstDim.setText(singleOrder.getDimension().getFirstDimension() + "");
        txtSecondDim.setText(singleOrder.getDimension().getSecondDimension() + "");
        txtThick.setText(singleOrder.getDimension().getThickness() + "");
        txtWeight.setText(singleOrder.getDimension().getWeight() + "");
        txtLength.setText(singleOrder.getLength_in_mm() + "");
        txtQuantityOnTray.setText(singleOrder.getQuantity_on_tray() + "");
        txtTrays.setText(singleOrder.getAmount_of_trays() + "");
        txtQuantityOverall.setText(singleOrder.getOverall_quantity() + "");
        txtMetrs.setText(singleOrder.getMetrs() + "");
        txtMaterials.setText(singleOrder.getMaterials() + "");
        txtCommentary.setText(singleOrder.getCommentary());
    }

    private void setMetrs() {
        mathService.setMetrs(singleOrder, txtLength.getText(), txtQuantityOverall.getText());
        txtMetrs.textProperty().setValue("" + singleOrder.getMetrs());
    }

    private void setMaterials() {
        mathService.setMaterials(dimension, singleOrder, txtWeight.getText(), txtMetrs.getText());
        txtMaterials.textProperty().setValue("" + singleOrder.getMaterials());
    }

    private void setOverallQuantity() {
        mathService.setOverallQuantity(singleOrder, txtQuantityOnTray.getText(), txtTrays.getText());
        txtQuantityOverall.textProperty().setValue("" + singleOrder.getOverall_quantity());
    }

    private void changeButtonsDisableValue(ObservableValue<? extends String> obs) {
        if (obs != null)
            btnClean.setDisable(false);

        if (txtFirstDim.textProperty().getValue().isEmpty() && txtSecondDim.textProperty().getValue().isEmpty()
                && txtThick.textProperty().getValue().isEmpty() && txtWeight.textProperty().getValue().isEmpty()
                && txtLength.textProperty().getValue().isEmpty() && !txtQuantityOnTray.textProperty().getValue().isEmpty()
                && !txtTrays.textProperty().getValue().isEmpty())
            btnClean.setDisable(true);

        if (!txtFirstDim.textProperty().getValue().isEmpty() && !txtSecondDim.textProperty().getValue().isEmpty()
                && !txtThick.textProperty().getValue().isEmpty() && !txtWeight.textProperty().getValue().isEmpty()
                && !txtLength.textProperty().getValue().isEmpty() && !txtQuantityOnTray.textProperty().getValue().isEmpty()
                && !txtTrays.textProperty().getValue().isEmpty()) {
            btnSave.setDisable(false);
        } else {
            btnSave.setDisable(true);
        }
    }

    private void filteringDimsTextFields(DimensionEnum dimEnum) {
        filteredDims.setPredicate(dim -> {
            setSetsValues(dim);
            return filter(dimEnum);
        });
        if (filteredDims.size() == 1 && !blockAutoFill) {
            txtFirstDim.textProperty().setValue(
                    String.valueOf(filteredDims.get(0).getFirstDimension()));
            txtSecondDim.textProperty().setValue(
                    String.valueOf(filteredDims.get(0).getSecondDimension()));
            txtThick.textProperty().setValue(
                    String.valueOf(filteredDims.get(0).getThickness()));
            txtWeight.textProperty().setValue(
                    String.valueOf(filteredDims.get(0).getWeight()));
            providers.clearSuggestions(providers);
        }
    }

    private void setSetsValues(Dimiensions obj) {
        setsValues = new DimensionDTO(String.valueOf(obj.getFirstDimension()),
                String.valueOf(obj.getSecondDimension()),
                String.valueOf(obj.getThickness()),
                String.valueOf(obj.getWeight()));
    }

    private boolean filter(DimensionEnum dimEnum){
        Filter filter = new Filter();
        if (dimEnum.equals(DimensionEnum.FIRST_DIM))
            return filter.isDimensionInSet(setsValues.getFirstDim(), setsValues.getSecondDim(), setsValues.getThick(),
                    setsValues.getWeight(), fieldsValues.getFirstDim(), fieldsValues.getSecondDim(), fieldsValues.getThick(),
                    fieldsValues.getWeight());
        else if (dimEnum.equals(DimensionEnum.SECOND_DIM))
            return filter.isDimensionInSet(setsValues.getSecondDim(), setsValues.getFirstDim(), setsValues.getThick(),
                    setsValues.getWeight(), fieldsValues.getSecondDim(), fieldsValues.getFirstDim(), fieldsValues.getThick(),
                    fieldsValues.getWeight());
        else if (dimEnum.equals(DimensionEnum.THICK))
            return filter.isDimensionInSet(setsValues.getThick(), setsValues.getFirstDim(), setsValues.getSecondDim(),
                    setsValues.getWeight(), fieldsValues.getThick(), fieldsValues.getFirstDim(), fieldsValues.getSecondDim(),
                    fieldsValues.getWeight());
        else if (dimEnum.equals(DimensionEnum.WEIGHT))
            return filter.isDimensionInSet(setsValues.getWeight(), setsValues.getFirstDim(), setsValues.getSecondDim(),
                    setsValues.getThick(), fieldsValues.getWeight(), fieldsValues.getFirstDim(), fieldsValues.getSecondDim(),
                    fieldsValues.getWeight());
        else
            return true;
    }

    private void refreshingSuggestions() {
        if (sets.secondDim.size() > 1 || sets.firstDim.size() > 1 ||
                sets.weight.size() > 1 || sets.thick.size() > 1) {
            providers.refreshSuggestions(sets, providers);
        }
    }

    public void onMouseClicked() {

    }

    public void exitButtonClicked() {
        Stage thisStage = (Stage) anchorPane.getScene().getWindow();
        thisStage.close();
    }

    public void cleanButtonClicked() {
        nullSingleOrdersFields();
        zeroFieldsValues();
        filteringDimsTextFields(DimensionEnum.FIRST_DIM);
        sets.update(filteredDims);
        refreshingSuggestions();
    }

    private void nullSingleOrdersFields() {
        Platform.runLater(() -> {
            txtFirstDim.clear();
            txtSecondDim.clear();
            txtThick.clear();
            txtWeight.clear();
            txtLength.clear();
            txtQuantityOnTray.clear();
            txtTrays.clear();
            txtQuantityOverall.clear();
            txtMetrs.clear();
            txtMaterials.clear();
            txtCommentary.clear();
        });
    }

    private void zeroFieldsValues() {
        singleOrder = new SingleOrders();
        dimension = new Dimiensions();
    }

    public void saveButtonClicked() {
        singleOrder.setOrders(OrdVariables.getOrderObject());
        if (GlobalVariables.getIsCreate())
            createSingleOrder();
        else
            updateSingleOrder();

        Stage thisStage = (Stage) anchorPane.getScene().getWindow();
        thisStage.close();
    }

    private void createSingleOrder() {
        singleOrder.setCommentary(txtCommentary.getText());
        singleOrder.setFinished(false);
        try {
            singleOrder.setDimension(dimensionService.returnDim(dimension));
            singleOrdersService.create(singleOrder, order);
            GlobalVariables.setIsActionCompleted(true);
            singleOrder.setSingle_active_order_id(null);
        } catch (Exception e) {
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void updateSingleOrder() {
        singleOrder.setCommentary(txtCommentary.getText());
        try {
            updateSingleOrderObject();
            GlobalVariables.setIsActionCompleted(true);
        } catch (Exception e) {
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void updateSingleOrderObject() {
        singleOrder.setDimension(dimensionService.returnDim(dimension));
        singleOrdersService.update(singleOrder, order);
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        blockAutoFill = keyEvent.getCode() == KeyCode.BACK_SPACE;
    }
}
