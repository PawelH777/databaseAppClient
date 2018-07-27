package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
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
import pl.Vorpack.app.DatabaseAccess.DimensionsAccess;
import pl.Vorpack.app.DatabaseAccess.OrdersAccess;
import pl.Vorpack.app.DatabaseAccess.SingleOrdersAccess;
import pl.Vorpack.app.DatabaseAccess.TraysAccess;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.SingleOrdVariables;
import pl.Vorpack.app.HelperClassess.AddOrderDimensionValuesInString;
import pl.Vorpack.app.HelperClassess.AddOrderHashSets;
import pl.Vorpack.app.HelperClassess.AddOrderSuggestionProviders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class AddOrChangeSingleOrderController {

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

    private TextAnimations textAnimations = null;
    private Boolean blockAutoFill = false;
    private Boolean blockFiltering = false;
    private SingleOrders singleOrdersObject = new SingleOrders();
    private Orders orderObject = new Orders();
    private Dimiensions dimensionObject = new Dimiensions();
    private DimensionsAccess dimensionsAccess = new DimensionsAccess();
    private SingleOrdersAccess singleOrdersAccess = new SingleOrdersAccess();
    private OrdersAccess ordersAccess = new OrdersAccess();
    private AddOrderDimensionValuesInString dimValuesFromSet = new AddOrderDimensionValuesInString();
    private AddOrderDimensionValuesInString dimValuesFromFields = new AddOrderDimensionValuesInString();
    private AddOrderHashSets addOrderHashSets = new AddOrderHashSets();
    private AddOrderSuggestionProviders addOrderSuggestionProviders = new AddOrderSuggestionProviders();

    private ObservableList<Dimiensions> proxyToDimensionFields;
    private FilteredList<Dimiensions> filteredListToDimensionFields;


    @FXML
    public void initialize(){
        textAnimations = new TextAnimations(statusViewer);
        formatFields();
        getAllDimensions();
        attachProviders();
        orderObject = OrdVariables.getOrderObject();

        if(!GlobalVariables.getIsCreate()){
            getExistingSingleOrder();
        }

        txtFirstDim.textProperty().addListener((obs, oldValue, newValue)->{
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            dimValuesFromFields = new AddOrderDimensionValuesInString(newValue,
                    txtSecondDim.getText(),txtThick.getText(), txtWeight.getText());
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
            if(!newValue.isEmpty() && !blockFiltering) {
                filteringDimsTextFields(1);
                fromObjectToList(filteredListToDimensionFields);
                refreshingSuggestions();
            }

            if(newValue.isEmpty())
                dimensionObject.setFirstDimension(BigDecimal.ZERO);
            else
                dimensionObject.setFirstDimension(BigDecimal.valueOf(Double.valueOf(newValue)));
        });

        txtSecondDim.textProperty().addListener((obs, oldValue, newValue)->{
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            dimValuesFromFields = new AddOrderDimensionValuesInString (txtFirstDim.getText(),
                    newValue,txtThick.getText(), txtWeight.getText());
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
            if(!newValue.isEmpty() && !blockFiltering) {
                filteringDimsTextFields(2);
                fromObjectToList(filteredListToDimensionFields);
                refreshingSuggestions();
            }
            if(newValue.isEmpty())
                dimensionObject.setSecondDimension(BigDecimal.ZERO);
            else
                dimensionObject.setSecondDimension(BigDecimal.valueOf(Double.valueOf(newValue)));
        });

        txtThick.textProperty().addListener((obs, oldValue, newValue)->{
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            dimValuesFromFields = new AddOrderDimensionValuesInString(txtFirstDim.getText(), txtSecondDim.getText(),
                    newValue, txtWeight.getText());
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
            if(!newValue.isEmpty()  && !blockFiltering) {
                filteringDimsTextFields(3);
                fromObjectToList(filteredListToDimensionFields);
                refreshingSuggestions();
            }
            if(newValue.isEmpty())
                dimensionObject.setThickness(BigDecimal.ZERO);
            else
                dimensionObject.setThickness(BigDecimal.valueOf(Double.valueOf(newValue)));
        });

        txtWeight.textProperty().addListener((obs, oldValue, newValue) -> {
            countMaterialsAndShowItInTextBox();
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            dimValuesFromFields = new AddOrderDimensionValuesInString(txtFirstDim.getText(), txtSecondDim.getText(),
                    txtThick.getText(), newValue);
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
            if(!newValue.isEmpty() && !blockFiltering) {
                filteringDimsTextFields(4);
                fromObjectToList(filteredListToDimensionFields);
                refreshingSuggestions();
            }
        });

        checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue)
                blockFiltering = false;
            else{
                blockFiltering = true;
                filteredListToDimensionFields = new FilteredList<>(proxyToDimensionFields, p -> true);
                fromObjectToList(filteredListToDimensionFields);
                addOrderSuggestionProviders.enableOrRefereshSuggestionsToFirstDim(addOrderHashSets.setFirstDims);
                addOrderSuggestionProviders.enableOrRefreshSuggestionsToSecondDim(addOrderHashSets.setSecondDims);
                addOrderSuggestionProviders.enableOrRefreshSuggestionsToThick(addOrderHashSets.setThicks);
                addOrderSuggestionProviders.enableOrRefreshSuggestionsToWeight(addOrderHashSets.setWeights);
            }
        });

        txtLength.textProperty().addListener((obs, oldValue, newValue) -> {
            countMetrsAndShowItInTextBox();
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
        });

        txtQuantityOnTray.textProperty().addListener((obs, oldValue, newValue) -> {
            countOverallQuantityOfPieces();
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
        });

        txtTrays.textProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            countOverallQuantityOfPieces();
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
        });

        txtQuantityOverall.textProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            countMetrsAndShowItInTextBox();
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
        });

        txtMetrs.textProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            countMaterialsAndShowItInTextBox();
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
        });

        txtMaterials.textProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
        });
    }

    private void getExistingSingleOrder() {
        singleOrdersObject = SingleOrdVariables.getSingleOrderObject();
        dimensionObject = new Dimiensions(singleOrdersObject.getDimension().getDimension_id(),
                singleOrdersObject.getDimension().getFirstDimension(), singleOrdersObject.getDimension().getSecondDimension(),
                singleOrdersObject.getDimension().getThickness(), singleOrdersObject.getDimension().getWeight());
        attachValuesToFields();
    }

    private void attachProviders() {
        addOrderSuggestionProviders.prov_FirstDims = getBigDecimalSuggestionProvider(addOrderHashSets.setFirstDims, txtFirstDim);
        addOrderSuggestionProviders.prov_SecondDims = getBigDecimalSuggestionProvider(addOrderHashSets.setSecondDims, txtSecondDim);
        addOrderSuggestionProviders.prov_Thick = getBigDecimalSuggestionProvider(addOrderHashSets.setThicks, txtThick);
        addOrderSuggestionProviders.prov_Weights = getBigDecimalSuggestionProvider(addOrderHashSets.setWeights, txtWeight);
    }

    private void getAllDimensions(){
        try{
            List<Dimiensions> allDimensionsInDatabase = dimensionsAccess.findAllDimensions();
            proxyToDimensionFields = FXCollections.observableArrayList(allDimensionsInDatabase);
            filteredListToDimensionFields = new FilteredList<>(proxyToDimensionFields, p -> true);
            fromObjectToList(filteredListToDimensionFields);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }
    private void attachValuesToFields(){
        txtFirstDim.setText(singleOrdersObject.getDimension().getFirstDimension() + "");
        txtSecondDim.setText(singleOrdersObject.getDimension().getSecondDimension() + "");
        txtThick.setText(singleOrdersObject.getDimension().getThickness() + "");
        txtWeight.setText(singleOrdersObject.getDimension().getWeight() + "");
        txtLength.setText(singleOrdersObject.getLength_in_mm() + "");
        txtQuantityOnTray.setText(singleOrdersObject.getQuantity_on_tray() + "");
        txtTrays.setText(singleOrdersObject.getAmount_of_trays() + "");
        txtQuantityOverall.setText(singleOrdersObject.getOverall_quantity() + "");
        txtMetrs.setText(singleOrdersObject.getMetrs() + "");
        txtMaterials.setText(singleOrdersObject.getMaterials() + "");
        txtCommentary.setText(singleOrdersObject.getCommentary());
    }

    private void countMetrsAndShowItInTextBox() {
        try{
            singleOrdersObject.setLength_in_mm(BigDecimal.valueOf(Double.valueOf(txtLength.textProperty().getValue())));
        }
        catch(Exception e){
            singleOrdersObject.setLength_in_mm(BigDecimal.ZERO);
        }

        try{
            singleOrdersObject.setOverall_quantity(BigDecimal.valueOf(Double.valueOf(txtQuantityOverall.textProperty().getValue())));
        }
        catch (Exception e){
            singleOrdersObject.setOverall_quantity(BigDecimal.ZERO);
        }
        singleOrdersObject.setMetrs(singleOrdersObject.getLength_in_mm().multiply(singleOrdersObject.getOverall_quantity()));
        if (singleOrdersObject.getMetrs().scale()<0)
            singleOrdersObject.setMetrs(singleOrdersObject.getMetrs().setScale(0));

        txtMetrs.textProperty().setValue("" + singleOrdersObject.getMetrs());
    }

    private void countMaterialsAndShowItInTextBox() {
        try{
            dimensionObject.setWeight(BigDecimal.valueOf(Double.valueOf(txtWeight.textProperty().getValue())));
        }
        catch(Exception e){
            dimensionObject.setWeight(BigDecimal.ZERO);
        }
        try{
            singleOrdersObject.setMetrs(BigDecimal.valueOf(Double.valueOf(txtMetrs.textProperty().getValue())));
        }
        catch(Exception e){
            singleOrdersObject.setMetrs(BigDecimal.ZERO);
        }
        singleOrdersObject.setMaterials(dimensionObject.getWeight().multiply(singleOrdersObject.getMetrs()));

        if (singleOrdersObject.getMaterials().scale()<0)
            singleOrdersObject.setMaterials(singleOrdersObject.getMaterials().setScale(0));

        txtMaterials.textProperty().setValue("" + singleOrdersObject.getMaterials());
    }

    private void countOverallQuantityOfPieces(){
        try{
            singleOrdersObject.setQuantity_on_tray(BigDecimal.valueOf(Double.valueOf(txtQuantityOnTray.textProperty().getValue())));
        }
        catch(Exception e){
            singleOrdersObject.setQuantity_on_tray(BigDecimal.ZERO);
        }
        try{
            Long amountOfTrays = Long.parseLong(txtTrays.textProperty().getValue());
            singleOrdersObject.setAmount_of_trays(amountOfTrays);
        }
        catch(Exception e){
            singleOrdersObject.setAmount_of_trays(0L);
        }
        singleOrdersObject.setOverall_quantity(singleOrdersObject.getQuantity_on_tray()
                .multiply(BigDecimal.valueOf(singleOrdersObject.getAmount_of_trays())));

        if (singleOrdersObject.getOverall_quantity().scale()<0)
            singleOrdersObject.setOverall_quantity(singleOrdersObject.getOverall_quantity().setScale(0));

        txtQuantityOverall.textProperty().setValue("" + singleOrdersObject.getOverall_quantity());
    }

    private void checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons() {
        if(!txtFirstDim.textProperty().getValue().isEmpty() && !txtSecondDim.textProperty().getValue().isEmpty()
                && !txtThick.textProperty().getValue().isEmpty() && !txtWeight.textProperty().getValue().isEmpty()
                && !txtLength.textProperty().getValue().isEmpty() && !txtQuantityOnTray.textProperty().getValue().isEmpty()
                && !txtTrays.textProperty().getValue().isEmpty()){

            btnSave.setDisable(false);
        }
        else{
            btnSave.setDisable(true);
        }
    }

    private void checkIfAreFieldsFilledAndChangeCleanButtonsAccess(ObservableValue<? extends String> obs) {
        if(obs != null)
            btnClean.setDisable(false);

        if(txtFirstDim.textProperty().getValue().isEmpty() && txtSecondDim.textProperty().getValue().isEmpty()
                && txtThick.textProperty().getValue().isEmpty() && txtWeight.textProperty().getValue().isEmpty()
                && txtLength.textProperty().getValue().isEmpty() && !txtQuantityOnTray.textProperty().getValue().isEmpty()
                && !txtTrays.textProperty().getValue().isEmpty())
            btnClean.setDisable(true);
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

    private TextFormatter getFormatter(){
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        return new TextFormatter((UnaryOperator<TextFormatter.Change>) change
                -> pattern.matcher(change.getControlNewText()).matches() ? change : null);
    }

    private void filteringDimsTextFields(int gate) {
        filteredListToDimensionFields.setPredicate(NextDimensionFromSet -> {
            Boolean filterResult = false;
            setObjVariablesValuesToFilter(NextDimensionFromSet);

            if(gate == 1)
                filterResult = dimValuesFromSet.checkIfDimensionIsInSetToFirstDim(dimValuesFromFields, dimValuesFromSet);
            else if(gate == 2)
                filterResult = dimValuesFromSet.checkIfDimensionIsInSetToSecondDim(dimValuesFromFields, dimValuesFromSet);
            else if(gate == 3)
                filterResult = dimValuesFromSet.checkIfDimensionIsInSetToThickness(dimValuesFromFields, dimValuesFromSet);
            else if(gate == 4)
                filterResult = dimValuesFromSet.checkIfDimensionIsInSetToWeight(dimValuesFromFields, dimValuesFromSet);

            return filterResult;
        });
        if(filteredListToDimensionFields.size() == 1 && !blockAutoFill){

            String firstDim_temp = String.valueOf(filteredListToDimensionFields.get(0).getFirstDimension());
            String secDim_temp = String.valueOf(filteredListToDimensionFields.get(0).getSecondDimension());
            String thick_temp = String.valueOf(filteredListToDimensionFields.get(0).getThickness());
            String weight_temp = String.valueOf(filteredListToDimensionFields.get(0).getWeight());

            txtFirstDim.textProperty().setValue(firstDim_temp);
            txtSecondDim.textProperty().setValue(secDim_temp);
            txtThick.textProperty().setValue(thick_temp);
            txtWeight.textProperty().setValue(weight_temp);

            addOrderSuggestionProviders.prov_FirstDims.clearSuggestions();
            addOrderSuggestionProviders.prov_SecondDims.clearSuggestions();
            addOrderSuggestionProviders.prov_Weights.clearSuggestions();
            addOrderSuggestionProviders.prov_Thick.clearSuggestions();
        }
    }

    private void setObjVariablesValuesToFilter(Dimiensions obj) {
        dimValuesFromSet.objFirstDim = String.valueOf(obj.getFirstDimension());
        dimValuesFromSet.objSecondDim = String.valueOf(obj.getSecondDimension());
        dimValuesFromSet.objThickness = String.valueOf(obj.getThickness());
        dimValuesFromSet.objWeight = String.valueOf(obj.getWeight());
    }

    private void fromObjectToList(List<Dimiensions> dimiensionsList) {
        addOrderHashSets.setFirstDims.clear();
        addOrderHashSets.setSecondDims.clear();
        addOrderHashSets.setThicks.clear();
        addOrderHashSets.setWeights.clear();

        for(Dimiensions dims : dimiensionsList){
            addOrderHashSets.setFirstDims.add(dims.getFirstDimension());
            addOrderHashSets.setSecondDims.add(dims.getSecondDimension());
            addOrderHashSets.setThicks.add(dims.getThickness());
            addOrderHashSets.setWeights.add(dims.getWeight());
        }
    }

    private void refreshingSuggestions() {
        if(addOrderHashSets.setSecondDims.size() > 1 || addOrderHashSets.setFirstDims.size() > 1 ||
                addOrderHashSets.setWeights.size() >  1 || addOrderHashSets.setThicks.size() > 1){
            addOrderSuggestionProviders.enableOrRefereshSuggestionsToFirstDim(addOrderHashSets.setFirstDims);
            addOrderSuggestionProviders.enableOrRefreshSuggestionsToSecondDim(addOrderHashSets.setSecondDims);
            addOrderSuggestionProviders.enableOrRefreshSuggestionsToThick(addOrderHashSets.setThicks);
            addOrderSuggestionProviders.enableOrRefreshSuggestionsToWeight(addOrderHashSets.setWeights);
        }
    }

    private SuggestionProvider<BigDecimal> getBigDecimalSuggestionProvider(Set<BigDecimal> setFirstDims, JFXTextField txtFirstDim) {
        SuggestionProvider<BigDecimal> prov_FirstDims = SuggestionProvider.create(setFirstDims);
        new AutoCompletionTextFieldBinding<>(txtFirstDim, prov_FirstDims);
        return prov_FirstDims;
    }

    public void exitButtonClicked(){
        Stage thisStage = (Stage) anchorPane.getScene().getWindow();
        thisStage.close();
    }

    public void cleanButtonClicked(){
        nullSingleOrdersFields();
        zeroFieldsValues();
        filteringDimsTextFields(1);
        fromObjectToList(filteredListToDimensionFields);
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
        singleOrdersObject = new SingleOrders();
        dimensionObject = new Dimiensions();
    }

    public void saveButtonClicked(){
        singleOrdersObject.setOrders(OrdVariables.getOrderObject());
        if(GlobalVariables.getIsCreate())
            createSingleOrder();
        else
            updateSingleOrder();
    }

    private void createSingleOrder(){
        singleOrdersObject.setCommentary(txtCommentary.getText());
        singleOrdersObject.setFinished(false);
        try{
            createNewSingleOrderObject();
            createTrays();
            updateOrder();
            GlobalVariables.setIsActionCompleted(true);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void createTrays() {
        TraysAccess traysAccess = new TraysAccess();
        traysAccess.createTraysBySingleOrder(singleOrdersObject);
    }

    private void updateOrder() {
        Long finishedSingleOrdersValue = 0L;
        Long unfinishedSingleOrdersValue = 0L;
        BigDecimal sumMaterialsValue = BigDecimal.ZERO;
        List<SingleOrders> singleOrdersList = singleOrdersAccess.findByOrders(orderObject);
        for(SingleOrders object : singleOrdersList){
            if(object.getFinished())
                finishedSingleOrdersValue++;
            else
                unfinishedSingleOrdersValue++;

            sumMaterialsValue = sumMaterialsValue.add(object.getMaterials());
        }
        orderObject.setMaterials(sumMaterialsValue);
        orderObject.setSingle_orders_finished(finishedSingleOrdersValue);
        orderObject.setSingle_orders_unfinished(unfinishedSingleOrdersValue);
        ordersAccess.updateOrder(orderObject);
    }

    private void createNewSingleOrderObject() {
        checkIfConcercDimensionIsInDatabase();
        singleOrdersObject = singleOrdersAccess.createSingleOrder(singleOrdersObject);
    }

    private void checkIfConcercDimensionIsInDatabase() {
        boolean findDim = false;
        List<Dimiensions> dims = dimensionsAccess.findDimension(dimensionObject);
        if(dims.size() != 0){
            findDim = true;
            dimensionObject = dims.get(0);
        }

        if(!findDim){
            createDimension();
        }

        singleOrdersObject.setDimension(dimensionObject);
    }

    private void createDimension() {
        List<Dimiensions> dims;
        dimensionsAccess.createDimension(dimensionObject);
        dims = dimensionsAccess.findDimension(dimensionObject);
        dimensionObject = dims.get(0);
    }

    private void updateSingleOrder(){
        singleOrdersObject.setCommentary(txtCommentary.getText());
        try{
            updateSingleOrderObject();
            updateOrder();
            GlobalVariables.setIsActionCompleted(true);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void updateSingleOrderObject() {
        checkIfConcercDimensionIsInDatabase();
        singleOrdersAccess.updateSingleOrder(singleOrdersObject);
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        blockAutoFill = keyEvent.getCode() == KeyCode.BACK_SPACE;
    }
}
