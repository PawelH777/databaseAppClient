package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import pl.Vorpack.app.DatabaseAccess.ClientAccess;
import pl.Vorpack.app.DatabaseAccess.DimensionsAccess;
import pl.Vorpack.app.DatabaseAccess.OrdersAccess;
import pl.Vorpack.app.DatabaseAccess.SingleOrdersAccess;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.HelperClassess.AddOrderDimensionValuesInString;
import pl.Vorpack.app.HelperClassess.AddOrderHashSets;
import pl.Vorpack.app.HelperClassess.AddOrderSuggestionProviders;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Domain.Client;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.TableValues.SingleOrdersTableValue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Created by Paweł on 2018-02-23.
 */
public class ShowSingleOrdersController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView<SingleOrdersTableValue> singleOrdersViewer;
    @FXML
    private TableColumn<SingleOrdersTableValue, Long> IDColumn;
    @FXML
    private TableColumn<SingleOrdersTableValue, String> dimensionColumn;
    @FXML
    private TableColumn<SingleOrdersTableValue, BigDecimal> weightColumn;
    @FXML
    private TableColumn<SingleOrdersTableValue, BigDecimal> lengthColumn;
    @FXML
    private TableColumn<SingleOrdersTableValue, Long> quantityColumn;
    @FXML
    private TableColumn<SingleOrdersTableValue, BigDecimal> metrsColumn;
    @FXML
    private TableColumn<SingleOrdersTableValue, BigDecimal> materialsColumn;
    @FXML
    private TableColumn<SingleOrdersTableValue, String> statusColumn;
    @FXML
    private JFXTextField txtLength;
    @FXML
    private JFXTextField txtQuantity;
    @FXML
    private JFXTextField txtMetrs;
    @FXML
    private JFXTextField txtMaterials;
    @FXML
    private JFXTextField txtFirmName;
    @FXML
    private JFXTextField txtFirstDim;
    @FXML
    private JFXTextField txtSecondDim;
    @FXML
    private JFXTextField txtThick;
    @FXML
    private JFXTextField txtWeight;
    @FXML
    private DatePicker dateOrder;
    @FXML
    private DatePicker dateCreateOrder;
    @FXML
    private JFXTextArea txtNote;
    @FXML
    private JFXButton btnAdd;
    @FXML
    private JFXButton btnModifyRecord;
    @FXML
    private JFXButton btnModifyStatus;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXButton btnAddOrder;
    @FXML
    private JFXButton btnModifyOrder;
    @FXML
    private JFXButton btnClean;
    @FXML
    private JFXCheckBox checkBox;
    @FXML
    private Label statusLabel;
    @FXML
    private Label orderNumber;
    @FXML
    private Label materialsNumber;

    private BigDecimal sumMaterialsValue;
    private Long finishedSingleOrdersValue;
    private Long unfinishedSingleOrdersValue;
    private Boolean findDim = false;
    private Boolean blockAutoFill = false;
    private Boolean blockFiltering = false;
    private Boolean isObjectInDatabase = false;

    private List<Client> allClientsInDatabase = new ArrayList<>();
    private List<Dimiensions> allDimensionsInDatabase = new ArrayList<>();
    private List<Orders> allOrders = new ArrayList<>();
    private List<SingleOrders> allSingleOrdersFromOrder = new ArrayList<>();
    private List<SingleOrdersTableValue> valuesToTable = new ArrayList<>();
    private ObservableList<Dimiensions> proxyToDimensionFields;
    private FilteredList<Dimiensions> filteredListToDimensionFields;

    private SingleOrders singleOrdersObject = new SingleOrders();
    private Orders orderObject = new Orders();
    private Orders lastOrder = new Orders();
    private Dimiensions dimensionObject = new Dimiensions();
    private ClientAccess clientAccess = new ClientAccess();
    private DimensionsAccess dimensionsAccess = new DimensionsAccess();
    private SingleOrdersAccess singleOrdersAccess = new SingleOrdersAccess();
    private OrdersAccess ordersAccess = new OrdersAccess();
    private AddOrderDimensionValuesInString dimValuesFromSet = new AddOrderDimensionValuesInString();
    private AddOrderDimensionValuesInString dimValuesFromFields = new AddOrderDimensionValuesInString();
    private AddOrderHashSets addOrderHashSets = new AddOrderHashSets();
    private AddOrderSuggestionProviders addOrderSuggestionProviders = new AddOrderSuggestionProviders();
    private TextAnimations textAnimations;

    @FXML
    public void initialize(){
        textAnimations = new TextAnimations(statusLabel);
        dateCreateOrder.setValue(LocalDate.now());
        dateOrder.setValue(dateCreateOrder.getValue().plusDays(1));
        formatFields();
        refreshDateBlockade(LocalDate.now());
        attachParametrsToTable();

        try{
            allDimensionsInDatabase = dimensionsAccess.findAllDimensions();
            proxyToDimensionFields = FXCollections.observableArrayList(allDimensionsInDatabase);
            filteredListToDimensionFields = new FilteredList<>(proxyToDimensionFields, p -> true);
            fromObjectToList(filteredListToDimensionFields);
            allClientsInDatabase = clientAccess.findAllClients();
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }

        for(Client cli : allClientsInDatabase){
            addOrderHashSets.setFirmNames.add(cli.getFirmName());
        }

        bindAutoCompletionToFirstNameField();
        addOrderSuggestionProviders.prov_FirstDims = getBigDecimalSuggestionProvider(addOrderHashSets.setFirstDims, txtFirstDim);
        addOrderSuggestionProviders.prov_SecondDims = getBigDecimalSuggestionProvider(addOrderHashSets.setSecondDims, txtSecondDim);
        addOrderSuggestionProviders.prov_Thick = getBigDecimalSuggestionProvider(addOrderHashSets.setThicks, txtThick);
        addOrderSuggestionProviders.prov_Weights = getBigDecimalSuggestionProvider(addOrderHashSets.setWeights, txtWeight);

        btnModifyOrder.setDisable(true);
        btnAddOrder.setDisable(true);
        btnAdd.setDisable(true);
        btnClean.setDisable(true);
        txtMetrs.setMouseTransparent(true);
        txtMaterials.setMouseTransparent(true);
        disableRecordOtherOptions();

        if("Użytkownik".equals(GlobalVariables.getAccess())){
            txtFirmName.setMouseTransparent(true);
            dateCreateOrder.setMouseTransparent(true);
            dateOrder.setMouseTransparent(true);
            txtNote.setMouseTransparent(true);
            setTrasparencyToAllSingleOrderFields(true);
        }

        if(OrdVariables.getOrderObject() == null){
            setNumberOfOrderIfItDoesntExist();
            setAccessToAllSingleOrderFields(true);
        }
        else{
            orderNumber.setText(String.valueOf(OrdVariables.getOrderObject().getOrder_id()));
            orderObject = OrdVariables.getOrderObject();
            getSingleRecordsOfOrderAndBindToTable();
            setOrderFieldsInForm();
            isObjectInDatabase = true;
        }

        singleOrdersViewer.getSelectionModel().selectedItemProperty().addListener((object, oldSelect, newSelect)->{
            if(newSelect != null)
                setUpSelectedSingleOrderInEmptyFields(object);

            enableRecordOptions();
        });

        txtFirmName.textProperty().addListener((obs, oldValue, newValue)->{
            checkIfOrderFieldsAreEmptyAndChangeAccessToButtons();
        });

        dateCreateOrder.valueProperty().addListener((obs, oldValue, newValue) ->{
            if(newValue.isAfter(dateOrder.getValue())){
                dateOrder.setValue(newValue.plusDays(1));
            }
            refreshDateBlockade(newValue);
            checkIfOrderFieldsAreEmptyAndChangeAccessToButtons();
        });

        dateOrder.valueProperty().addListener((obs, oldValue, newValue) ->{
            checkIfOrderFieldsAreEmptyAndChangeAccessToButtons();
        });

        txtNote.textProperty().addListener((obs, oldValue, newValue)->{
            checkIfOrderFieldsAreEmptyAndChangeAccessToButtons();
        });

        txtFirstDim.textProperty().addListener((obs, oldValue, newValue)->{
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            dimValuesFromFields = new AddOrderDimensionValuesInString (newValue,
                    txtSecondDim.getText(),txtThick.getText(), txtWeight.getText());
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
            if(!newValue.isEmpty() && !blockFiltering) {
                filteringDimsTextFields( 1);
                fromObjectToList(filteredListToDimensionFields);
                refreshingSuggestions();
            }
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

        txtQuantity.textProperty().addListener((obs, oldValue, newValue) -> {
            countMetrsAndShowItInTextBox();
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
        });

        txtMetrs.textProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {
            countMaterialsAndShowItInTextBox();
            checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons();
            checkIfAreFieldsFilledAndChangeCleanButtonsAccess(obs);
        });
    }

    private void checkIfAreFieldsFilledAndChangeCleanButtonsAccess(ObservableValue<? extends String> obs) {
        if(obs != null)
            btnClean.setDisable(false);

        if(txtFirstDim.textProperty().getValue().isEmpty() && txtSecondDim.textProperty().getValue().isEmpty()
                && txtThick.textProperty().getValue().isEmpty() && txtWeight.textProperty().getValue().isEmpty()
                && txtLength.textProperty().getValue().isEmpty() && txtQuantity.textProperty().getValue().isEmpty())
            btnClean.setDisable(true);
    }

    private void disableRecordOtherOptions() {
        btnModifyRecord.setDisable(true);
        btnModifyStatus.setDisable(true);
        btnDelete.setDisable(true);
    }

    private void setAccessToAllSingleOrderFields(Boolean b) {
        txtFirstDim.setDisable(b);
        txtSecondDim.setDisable(b);
        txtThick.setDisable(b);
        txtWeight.setDisable(b);
        txtLength.setDisable(b);
        txtQuantity.setDisable(b);
        txtMaterials.setDisable(b);
        txtMetrs.setDisable(b);
    }

    private void setTrasparencyToAllSingleOrderFields(Boolean b){
        txtFirstDim.setMouseTransparent(b);
        txtSecondDim.setMouseTransparent(b);
        txtThick.setMouseTransparent(b);
        txtWeight.setMouseTransparent(b);
        txtLength.setMouseTransparent(b);
        txtQuantity.setMouseTransparent(b);
    }

    private void checkIfSingleOrderFieldsAreEmptyAndChangeAccessToButtons() {
        if(!txtFirstDim.textProperty().getValue().isEmpty() && !txtSecondDim.textProperty().getValue().isEmpty()
                && !txtThick.textProperty().getValue().isEmpty() && !txtWeight.textProperty().getValue().isEmpty()
                && !txtLength.textProperty().getValue().isEmpty() && !txtQuantity.textProperty().getValue().isEmpty()){
            enableRecordOptions();
        }
        else{
            btnAdd.setDisable(true);
            disableRecordOtherOptions();
        }
    }

    private void enableRecordOptions() {
        btnModifyStatus.setDisable(false);
        if(!"Użytkownik".equals(GlobalVariables.getAccess())){
            btnAdd.setDisable(false);
            btnModifyRecord.setDisable(false);
            btnDelete.setDisable(false);
        }
    }

    private void checkIfOrderFieldsAreEmptyAndChangeAccessToButtons() {
        if(!"Użytkownik".equals(GlobalVariables.getAccess()))
            if(!txtFirmName.textProperty().getValue().isEmpty() && dateCreateOrder.valueProperty().getValue() != null
                && dateOrder.valueProperty().getValue() != null){
                if(!isObjectInDatabase)  {
                    btnAddOrder.setDisable(false);
                    btnModifyOrder.setDisable(true);
                }
                else{
                    btnAddOrder.setDisable(true);
                    btnModifyOrder.setDisable(false);
                }
            }
            else{
                btnAddOrder.setDisable(true);
                btnModifyOrder.setDisable(true);
            }
    }

    private void setUpSelectedSingleOrderInEmptyFields(ObservableValue<? extends SingleOrdersTableValue> object) {
        String dimensionString;
        Boolean statusFromSelectedObject;
        Dimiensions dimFromSelectedObject = new Dimiensions();
        for(Dimiensions dimObject : allDimensionsInDatabase){
            dimensionString = dimObject.getFirstDimension() + "x" + dimObject.getSecondDimension() + "x" +
                    dimObject.getThickness();
            if(!object.getValue().getDimension().isEmpty() || object.getValue().getWeight() != null)
                if(dimensionString.equals(object.getValue().getDimension())
                        && dimObject.getWeight().equals(object.getValue().getWeight())){
                    dimFromSelectedObject = dimObject;
                    break;
                }
        }
        statusFromSelectedObject = object.getValue().getStatus().equals("Ukończono");
        singleOrdersObject = new SingleOrders(object.getValue().getSingleOrderID(), orderObject, dimFromSelectedObject,
                object.getValue().getQuantity(), object.getValue().getLength(), object.getValue().getMetrs(),
                object.getValue().getMaterials(), statusFromSelectedObject);
        txtFirstDim.setText(String.valueOf(dimFromSelectedObject.getFirstDimension()));
        txtSecondDim.setText(String.valueOf(dimFromSelectedObject.getSecondDimension()));
        txtThick.setText(String.valueOf(dimFromSelectedObject.getThickness()));
        txtWeight.setText(String.valueOf(dimFromSelectedObject.getWeight()));
        txtLength.setText(String.valueOf(object.getValue().getLength()));
        txtQuantity.setText(String.valueOf(object.getValue().getQuantity()));
        txtMetrs.setText(String.valueOf(object.getValue().getMetrs()));
        txtMaterials.setText(String.valueOf(object.getValue().getMaterials()));
    }

    private void countMetrsAndShowItInTextBox() {
        try{
            singleOrdersObject.setLength(BigDecimal.valueOf(Double.valueOf(txtLength.textProperty().getValue())));
        }
        catch(Exception e){
            singleOrdersObject.setLength(BigDecimal.ZERO);
        }

        try{
            singleOrdersObject.setQuantity(BigDecimal.valueOf(Double.valueOf(txtQuantity.textProperty().getValue())));
        }
        catch (Exception e){
            singleOrdersObject.setQuantity(BigDecimal.ZERO);
        }
        singleOrdersObject.setMetrs(singleOrdersObject.getLength().multiply(singleOrdersObject.getQuantity()));
        if (singleOrdersObject.getMetrs().scale()<0)
            singleOrdersObject.setMetrs(singleOrdersObject.getMetrs().setScale(0));

        txtMetrs.textProperty().setValue("" + singleOrdersObject.getMetrs());
    }

    private void setNumberOfOrderIfItDoesntExist() {
        getAllOrdersAndLastOrder();
        if(allOrders.size() == 0)
            orderNumber.setText(String.valueOf(0));
        else
            orderNumber.setText(String.valueOf(lastOrder.getOrder_id() + 1));
    }

    private void getAllOrdersAndLastOrder() {
        allOrders = ordersAccess.findAllOrders();
        if(allOrders.size() > 0)
            lastOrder = allOrders.get(allOrders.size() - 1);
    }


    private void formatFields() {
        txtFirstDim.setTextFormatter(getFormatter());
        txtSecondDim.setTextFormatter(getFormatter());
        txtThick.setTextFormatter(getFormatter());
        txtWeight.setTextFormatter(getFormatter());
        txtMetrs.setTextFormatter(getFormatter());
        txtQuantity.setTextFormatter(getFormatter());
        txtLength.setTextFormatter(getFormatter());
    }

    private TextFormatter getFormatter(){
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        return new TextFormatter((UnaryOperator<TextFormatter.Change>) change
                -> pattern.matcher(change.getControlNewText()).matches() ? change : null);
    }

    private void refreshDateBlockade(LocalDate now) {
        final Callback<DatePicker, DateCell> dayCellFactory = restrainDatePicker(now);
        dateOrder.setDayCellFactory(dayCellFactory);
    }

    private void attachParametrsToTable() {
        IDColumn.setCellValueFactory( new PropertyValueFactory<>("singleOrderID"));
        dimensionColumn.setCellValueFactory( new PropertyValueFactory<>("dimension"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        lengthColumn.setCellValueFactory( new PropertyValueFactory<>("length"));
        quantityColumn.setCellValueFactory( new PropertyValueFactory<>("quantity"));
        metrsColumn.setCellValueFactory(new PropertyValueFactory<>("metrs"));
        materialsColumn.setCellValueFactory(new PropertyValueFactory<>("materials"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void getSingleRecordsOfOrderAndBindToTable(){
        allSingleOrdersFromOrder.clear();
        allSingleOrdersFromOrder = singleOrdersAccess.findByOrders(orderObject);
        countFinishedAndUnfinishedSingleOrders();
        summaryOrdersMaterials();
        getValuesToTableFromSingleOrders();
        ObservableList<SingleOrdersTableValue> data = FXCollections.observableArrayList(valuesToTable);
        singleOrdersViewer.setItems(data);
    }

    private void setOrderFieldsInForm() {
        dateOrder.setValue(OrdVariables.getOrderObject().getOrder_date());
        dateCreateOrder.setValue(OrdVariables.getOrderObject().getOrder_receive_date());
        refreshDateBlockade(OrdVariables.getOrderObject().getOrder_receive_date());
        txtFirmName.setText(OrdVariables.getOrderObject().getClient().getFirmName());
        txtNote.setText(OrdVariables.getOrderObject().getOrder_note());
    }

    private void summaryOrdersMaterials() {
        BigDecimal singleOrdersMaterialsSummary = BigDecimal.ZERO;

        for(SingleOrders singleOrdersObject : allSingleOrdersFromOrder)
            if(!singleOrdersObject.getFinished())
                singleOrdersMaterialsSummary = singleOrdersMaterialsSummary.add(singleOrdersObject.getMaterials());

        sumMaterialsValue = singleOrdersMaterialsSummary;
        materialsNumber.setText(String.valueOf(singleOrdersMaterialsSummary));
    }

    private void countFinishedAndUnfinishedSingleOrders(){
        finishedSingleOrdersValue = (long)0;
        unfinishedSingleOrdersValue = (long)0;
        for(SingleOrders record : allSingleOrdersFromOrder){
            if(record.getFinished())
                finishedSingleOrdersValue++;
            else
                unfinishedSingleOrdersValue++;
        }
    }

    private void getValuesToTableFromSingleOrders(){
        valuesToTable = new ArrayList<>();
        SingleOrdersTableValue singleOrdersTableValueObject;
        String fullDimensionValueAsString;
        String finishedStatusAsString;
        for(SingleOrders record : allSingleOrdersFromOrder){
            fullDimensionValueAsString = record.getDimension().getFirstDimension() + "x" + record.getDimension().getSecondDimension()
                    + "x" + record.getDimension().getThickness();

            if(record.getFinished())
                finishedStatusAsString = "Ukończono";
            else
                finishedStatusAsString = "Nieukończono";

            singleOrdersTableValueObject = new SingleOrdersTableValue(record.getSingle_active_order_id(), fullDimensionValueAsString,
                    record.getDimension().getWeight(), record.getLength(), record.getQuantity(), record.getMetrs(),
                    record.getMaterials(), finishedStatusAsString);
            valuesToTable.add(singleOrdersTableValueObject);
        }
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

    private void bindAutoCompletionToFirstNameField() {
        SuggestionProvider<String> prov_firmsName = SuggestionProvider.create(addOrderHashSets.setFirmNames);
        new AutoCompletionTextFieldBinding<>(txtFirmName, prov_firmsName);
    }

    private SuggestionProvider<BigDecimal> getBigDecimalSuggestionProvider(Set<BigDecimal> setFirstDims, JFXTextField txtFirstDim) {
        SuggestionProvider<BigDecimal> prov_FirstDims = SuggestionProvider.create(setFirstDims);
        new AutoCompletionTextFieldBinding<>(txtFirstDim, prov_FirstDims);
        return prov_FirstDims;
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

    private void setObjVariablesValuesToFilter(Dimiensions obj) {
        dimValuesFromSet.objFirstDim = String.valueOf(obj.getFirstDimension());
        dimValuesFromSet.objSecondDim = String.valueOf(obj.getSecondDimension());
        dimValuesFromSet.objThickness = String.valueOf(obj.getThickness());
        dimValuesFromSet.objWeight = String.valueOf(obj.getWeight());
    }

    public void addButtonClicked(MouseEvent mouseEvent){
        persistRecord(true);
        statusLabel.setText("Rekord został dodany");
        textAnimations.startLabelsPulsing();
        disableRecordOtherOptions();
    }

    public void modifyRecordButtonClicked(MouseEvent mouseEvent){
        persistRecord(false);
        statusLabel.setText("Rekord został zmieniony");
        textAnimations.startLabelsPulsing();
        disableRecordOtherOptions();
    }

    public void modifyStatusButtonClicked(MouseEvent mouseEvent){
        changeSingleOrderStatus();
        statusLabel.setText("Status rekordu został zmieniony");
        textAnimations.startLabelsPulsing();
        disableRecordOtherOptions();
    }

    public void deleteRecordButtonClicked(MouseEvent mouseEvent){
        deleteRecord();
        statusLabel.setText("Rekord został usunięty");
        textAnimations.startLabelsPulsing();
        disableRecordOtherOptions();
    }

    public void addOrderButtonClicked(MouseEvent mouseEvent){
        persistOrder(true);
        statusLabel.setText("Zamówienie zostało dodane");
        textAnimations.startLabelsPulsing();
    }

    public void modifyOrderButtonClicked(MouseEvent mouseEvent){
        persistOrder(false);
        statusLabel.setText("Zamówienie zostało zmienione");
        textAnimations.startLabelsPulsing();
    }

    public void cleanButtonClicked(MouseEvent mouseEvent){
        nullSingleOrdersFields();
        zeroFieldsValues();
        filteringDimsTextFields(1);
        fromObjectToList(filteredListToDimensionFields);
        refreshingSuggestions();
        Platform.runLater(() -> {
            singleOrdersViewer.getSelectionModel().clearSelection();
        });
    }

    private void nullSingleOrdersFields() {
        Platform.runLater(() -> {
            txtFirstDim.clear();
            txtSecondDim.clear();
            txtThick.clear();
            txtWeight.clear();
            txtLength.clear();
            txtQuantity.clear();
            txtMetrs.clear();
            txtMaterials.clear();
        });
    }

    private void zeroFieldsValues() {
        singleOrdersObject = new SingleOrders();
        dimensionObject = new Dimiensions();
    }

    public void exitButtonClicked(MouseEvent mouseEvent){
        Stage thisStage = (Stage) anchorPane.getScene().getWindow();
        thisStage.close();
    }

    private Callback<DatePicker, DateCell> restrainDatePicker(LocalDate lcldate){
        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(DatePicker param) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item.isBefore(
                                        lcldate.plusDays(1))
                                        ) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };
        return dayCellFactory;
    }

    private void persistRecord(Boolean actionIsOnCreate){
        List<Dimiensions> dimsAsFromFields;
        dimensionObject = new Dimiensions(BigDecimal.valueOf(Double.parseDouble(txtFirstDim.textProperty().getValue())),
                BigDecimal.valueOf(Double.parseDouble(txtSecondDim.textProperty().getValue())),
                BigDecimal.valueOf(Double.parseDouble(txtThick.textProperty().getValue())),
                BigDecimal.valueOf(Double.parseDouble(txtWeight.textProperty().getValue())));

        try{
            dimsAsFromFields = dimensionsAccess.findDimension(dimensionObject);
            if(dimsAsFromFields.size() != 0){
                findDim = true;
                dimensionObject = dimsAsFromFields.get(0);
            }

            if(!findDim){
                dimensionsAccess.createDimension(dimensionObject);
                dimsAsFromFields = dimensionsAccess.findDimension(dimensionObject);
                dimensionObject = dimsAsFromFields.get(0);
            }

            if(actionIsOnCreate){
                singleOrdersObject = new SingleOrders(orderObject, dimensionObject,
                        BigDecimal.valueOf(Double.parseDouble(txtQuantity.textProperty().getValue())),
                        BigDecimal.valueOf(Double.parseDouble(txtLength.textProperty().getValue())),
                        BigDecimal.valueOf(Double.parseDouble(txtMetrs.textProperty().getValue())),
                        BigDecimal.valueOf(Double.parseDouble(txtMaterials.textProperty().getValue())),true);
                singleOrdersAccess.createSingleOrder(singleOrdersObject);
            }
            else{
                singleOrdersObject.setDimension(dimensionObject);
                singleOrdersObject.setQuantity(BigDecimal.valueOf(Double.parseDouble(txtQuantity.textProperty().getValue())));
                singleOrdersObject.setLength(BigDecimal.valueOf(Double.parseDouble(txtLength.textProperty().getValue())));
                singleOrdersObject.setMetrs(BigDecimal.valueOf(Double.parseDouble(txtMetrs.textProperty().getValue())));
                singleOrdersObject.setMaterials(BigDecimal.valueOf(Double.parseDouble(txtMaterials.textProperty().getValue())));
                singleOrdersAccess.updateSingleOrder(singleOrdersObject);
            }

            getSingleRecordsOfOrderAndBindToTable();
            orderObject.setMaterials(sumMaterialsValue);
            orderObject.setSingle_orders_completed(finishedSingleOrdersValue);
            orderObject.setSingle_orders_unfinished(unfinishedSingleOrdersValue);
            ordersAccess.updateOrder(orderObject);
            GlobalVariables.setIsActionCompleted(true);
        }
        catch(Exception e){
           e.printStackTrace();
           InfoAlerts.generalAlert();
        }
    }

    private void deleteRecord(){
        try{
            singleOrdersAccess.deleteSingleOrder(singleOrdersObject);
            getSingleRecordsOfOrderAndBindToTable();
            GlobalVariables.setIsActionCompleted(true);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void changeSingleOrderStatus(){
        Boolean settedFinishedStatus = singleOrdersObject.getFinished();

        if(settedFinishedStatus)
            singleOrdersObject.setFinished(false);
        else
            singleOrdersObject.setFinished(true);

        try{
            singleOrdersAccess.updateSingleOrder(singleOrdersObject);
            getSingleRecordsOfOrderAndBindToTable();
            orderObject.setMaterials(sumMaterialsValue);
            orderObject.setSingle_orders_completed(finishedSingleOrdersValue);
            orderObject.setSingle_orders_unfinished(unfinishedSingleOrdersValue);
            ordersAccess.updateOrder(orderObject);
            GlobalVariables.setIsActionCompleted(true);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void persistOrder(Boolean isOnCreate){
        String firmNameValue = txtFirmName.getText();
        LocalDate createOrderDateValue = dateCreateOrder.getValue();
        LocalDate orderDateValue = dateOrder.getValue();
        String noteValue = txtNote.getText();

        Client clientObject = new Client(firmNameValue);
        try{
            List<Client> allClientsWithFirmName = clientAccess.findClient(firmNameValue);

            if(allClientsWithFirmName.size() == 0){
                clientAccess.createNewClient(clientObject);
                allClientsWithFirmName = clientAccess.findClient(firmNameValue);
            }
            clientObject = allClientsWithFirmName.get(0);

            if(sumMaterialsValue == null)
                sumMaterialsValue = BigDecimal.ZERO;

            if(finishedSingleOrdersValue == null)
                finishedSingleOrdersValue = 0L;

            if(unfinishedSingleOrdersValue == null)
                unfinishedSingleOrdersValue = 0L;

            if(isOnCreate){
                orderObject = new Orders(clientObject, sumMaterialsValue, createOrderDateValue, orderDateValue,
                        noteValue, finishedSingleOrdersValue, unfinishedSingleOrdersValue);
                ordersAccess.createOrder(orderObject);
                setAccessToAllSingleOrderFields(false);
                getAllOrdersAndLastOrder();
                orderObject = lastOrder;
                disableAddEnableModify();
            }
            else{
                orderObject.setClient(clientObject);
                orderObject.setMaterials(sumMaterialsValue);
                orderObject.setOrder_receive_date(createOrderDateValue);
                orderObject.setOrder_date(orderDateValue);
                orderObject.setOrder_note(noteValue);
                orderObject.setSingle_orders_completed(finishedSingleOrdersValue);
                orderObject.setSingle_orders_unfinished(unfinishedSingleOrdersValue);
                ordersAccess.updateOrder(orderObject);
            }
            GlobalVariables.setIsActionCompleted(true);
        }
        catch (Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void disableAddEnableModify() {
        btnAddOrder.setDisable(true);
        isObjectInDatabase = true;
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        blockAutoFill = keyEvent.getCode() == KeyCode.BACK_SPACE;
    }
}
