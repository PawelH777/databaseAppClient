package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.DatabaseAccess.FinishedOrdersAccess;
import pl.Vorpack.app.Domain.Client;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.FinishedOrders;
import pl.Vorpack.app.GlobalVariables.CliVariables;
import pl.Vorpack.app.GlobalVariables.FinishedOrdVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Paweł on 2018-03-01.
 */
public class ShowFinishedOrdersController {

    private static final String SHOW_SINGLE_ORDERS_PANE_FXML = "/fxml/story/ShowSingleFinishedOrders.fxml";

    @FXML
    private JFXComboBox columnsCmbBox;
    @FXML
    private Label StatusViewer;
    @FXML
    private JFXButton btnModify;
    @FXML
    private JFXButton btnRecover;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXListView<String> lstOrders;
    @FXML
    private DatePicker orderDatePicker;
    @FXML
    private JFXComboBox datesCmbBox;

    private FinishedOrdersAccess finishedOrdersAccess = new FinishedOrdersAccess();

    private List<FinishedOrders> results  = new ArrayList<>();
    private List<String> records = new ArrayList<String>();
    private List<Object[]> finishedOrdersCollection = new ArrayList<>();
    private List<FinishedOrders> ord = new ArrayList<>();
    private List<Client> cli = new ArrayList<>();
    private List<Dimiensions> dim = new ArrayList<>();
    private String item;
    private SortedList<Object []> sortedData;
    private FilteredList<Object []> filteredList;

    private FinishedOrders finishedOrdersObject;

    private List<String> listToView = new ArrayList<>();

    private int gate, dateGate;

    private TextAnimations textAnimations = new TextAnimations(StatusViewer);

    @FXML
    private void initialize(){
        StatusViewer.setOpacity(0);
        textAnimations = new TextAnimations(StatusViewer);
        OrdVariables.setOrderObject(null);
        CliVariables.setObject(null);
        changeAccessToButtons();
        getAllRecords();
        sortedData = new SortedList<>(filteredList);
        sortGate();
        listToView = fetchToList(sortedData);
        lstOrders.setItems(FXCollections.observableArrayList(listToView));
        txtSearch.disableProperty().setValue(true);
        orderDatePicker.disableProperty().setValue(true);

        columnsCmbBox.getItems().addAll(
                "Wszystkie",
                "Identyfikator",
                "Nazwa firmy",
                "Do ukończenia",
                "Ukończono",
                "Ilość materiałów"
        );

        datesCmbBox.getItems().addAll(
                "Data zamówienia",
                "Data stworzenia"
        );

        lstOrders.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            int index = 0;
            if(newValue == null){
                btnDelete.setDisable(true);
                btnRecover.setDisable(true);
            }
            else{
                if(!GlobalVariables.getAccess().equals("Użytkownik"))
                    btnDelete.setDisable(false);
                btnRecover.setDisable(false);
                btnModify.setDisable(false);
            }
        }));

        orderDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });

        columnsCmbBox.valueProperty().addListener((obs, oldValue, newValue) ->{
            if(newValue.toString().isEmpty())
                txtSearch.setDisable(true);
            else
                txtSearch.setDisable(false);
            getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });

        datesCmbBox.valueProperty().addListener((ObservableValue obs, Object oldValue, Object newValue) ->{
            if(!newValue.toString().isEmpty())
                orderDatePicker.disableProperty().setValue(false);

            if ("Data zamówienia".equals(datesCmbBox.getValue()))
                dateGate = 1;
            else if ("Data stworzenia".equals(datesCmbBox.getValue()))
                dateGate = 2;

            getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });
    }

    private void changeAccessToButtons() {
        btnRecover.setDisable(true);
        btnModify.setDisable(true);
        btnDelete.setDisable(true);
    }

    private void sortGate() {
        if(gate == 1)
            setComparatorOrderDate();
        else if(gate == 2)
            setComparatorReceiveDate();
        else if(gate == 3)
            setComparatorFirmName();
    }

    private void setComparatorFirmName() {
            sortedData.setComparator((o1, o2) -> {
            Client clientObject = (Client)o1[1];
            Client clientObject_2 = (Client)o2[1];
            String name_1 = clientObject.getFirmName().toLowerCase();
            String name_2 = clientObject_2.getFirmName().toLowerCase();
            return name_1.compareTo(name_2);
        });
    }

    private void setComparatorReceiveDate() {
            sortedData.setComparator((o1, o2) -> {
            FinishedOrders ordersObject = (FinishedOrders)o1[0];
            FinishedOrders ordersObject_2 = (FinishedOrders)o2[0];

            return ordersObject.getOrder_receive_date().isBefore(ordersObject_2.getOrder_receive_date()) ? -1
                    : ordersObject_2.getOrder_receive_date().isBefore(ordersObject.getOrder_receive_date()) ? 1
                    : 0;
        });

    }

    private void setComparatorOrderDate() {
            sortedData.setComparator((o1, o2) -> {
                FinishedOrders ordersObject = (FinishedOrders)o1[0];
                FinishedOrders ordersObject_2 = (FinishedOrders)o2[0];

            return ordersObject.getOrder_date().isBefore(ordersObject_2.getOrder_date()) ? -1
                    : ordersObject_2.getOrder_date().isBefore(ordersObject.getOrder_date()) ? 1
                    : 0;
        });
    }

    private void getRecordsIDColumn(String searchedText, String searchedData) {
        filteredList.setPredicate(obj -> {

            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            FinishedOrders ordersObj = (FinishedOrders) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(ordersObj.getOrder_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(ordersObj.getOrder_id()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
                    return true;
            }

            return false;

        });
    }
    private void getRecordsFirmNameColumn(String searchedText, String searchedData) {
        filteredList.setPredicate(obj -> {

            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            Client cliObj = (Client) obj[1];
            FinishedOrders ordersObj = (FinishedOrders) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(cliObj.getFirmName()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(cliObj.getFirmName()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
                    return true;
            }

            return false;

        });
    }

    private void getRecordsMaterialsColumn(String searchedText, String searchedData) {

        filteredList.setPredicate(obj -> {

            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            FinishedOrders ordersObj = (FinishedOrders) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(ordersObj.getMaterials()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(ordersObj.getMaterials()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
                    return true;
            }

            return false;

        });
    }

    private void getRecordsFinishedTaskColumn(String searchedText, String searchedData){
        filteredList.setPredicate(obj -> {
            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            Orders ordersObj = (Orders) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(ordersObj.getSingle_orders_completed()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(ordersObj.getSingle_orders_completed()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
            }
            return false;
        });
    }

    private void getRecordsUnfinishedTaskColumn(String searchedText, String searchedData){
        filteredList.setPredicate(obj -> {
            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            Orders ordersObj = (Orders) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(ordersObj.getSingle_orders_unfinished()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(ordersObj.getSingle_orders_unfinished()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
            }
            return false;
        });
    }

    private void getRecordsDate(String searchedData) {

        filteredList.setPredicate(obj -> {

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null"))
                return true;

            String lowerCaseValue = searchedData.toLowerCase();
            FinishedOrders ordersObj = (FinishedOrders) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getOrder_receive_date());

            if(searchedData.equals(date))
                return true;

            return false;

        });
    }

    private void getRowsCmbWszystkie(String searchedText, String searchedData) {
        filteredList.setPredicate(obj -> {
            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            FinishedOrders ordersObj = (FinishedOrders) obj[0];
            Client clientObj = (Client) obj[1];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(ordersObj.getOrder_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(ordersObj.getMaterials()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(clientObj.getFirmName()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(ordersObj.getOrder_id()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(ordersObj.getMaterials()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(clientObj.getFirmName()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
            }
            return false;

        });
    }

    public void getAllRecords(){
        Client cliObject;
        finishedOrdersCollection = new ArrayList<>();
        try{
            results = finishedOrdersAccess.findAllFinishedOrders();

            for(FinishedOrders o  : results){
                cliObject = o.getClient();
                Object[] obj = new Object[]{o, cliObject};
                finishedOrdersCollection.add(obj);
            }
            records = fetchToList(finishedOrdersCollection);
            ObservableList<Object[]> data = FXCollections.observableArrayList(finishedOrdersCollection);
            filteredList = new FilteredList<>(data, p -> true);
        }catch (Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private List<String> fetchToList(List<Object[]> obs){
        List<String> temp_records = new ArrayList<>();
        ord.clear();
        cli.clear();
        for(Object[] o : obs){
            FinishedOrders orderObject = (FinishedOrders) o[0];
            ord.add(orderObject);
            Client clientObject = (Client) o[1];
            cli.add(clientObject);

            item = "Firma:  " + clientObject.getFirmName() + System.lineSeparator() +
                    "Data zamówienia:  " + orderObject.getOrder_date() + System.lineSeparator() +
                    "Data przyjęcia zamówienia:  " + orderObject.getOrder_receive_date() + System.lineSeparator() +
                    "Ukończono:  " + orderObject.getSingle_orders_completed() + System.lineSeparator() +
                    "Do ukończenia:  " + orderObject.getSingle_orders_unfinished() + System.lineSeparator() +
                    "Potrzebne materiały:   " + orderObject.getMaterials();
            temp_records.add(item);
        }
        return temp_records;
    }

    public void btnFirmNameClicked(MouseEvent mouseEvent) {
        gate = 3;
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
    }

    public void btnOrderReceiveDateClicked(MouseEvent mouseEvent) {
        gate = 2;
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
    }

    public void btnOrderDateClicked(MouseEvent mouseEvent) {
        gate = 1;
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
    }

    private void getRecordsWithActualConfigure(String searchedText, String searchedData) {
        if(columnsCmbBox.valueProperty().getValue() == "Wszystkie")
            getRowsCmbWszystkie(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Identyfikator")
            getRecordsIDColumn(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Nazwa firmy")
            getRecordsFirmNameColumn(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Ilość materiałów")
            getRecordsMaterialsColumn(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Ukończono")
            getRecordsFinishedTaskColumn(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Do ukończenia")
            getRecordsUnfinishedTaskColumn(searchedText, searchedData);
        else if(String.valueOf(columnsCmbBox.getValue()).equals("null")
                || String.valueOf(columnsCmbBox.getValue()).isEmpty()
                ||  String.valueOf(columnsCmbBox.getValue()) == null)
            getRecordsDate(searchedData);
        sortedData = new SortedList<>(filteredList);
        sortGate();
        listToView = fetchToList(sortedData);
        lstOrders.setItems(FXCollections.observableArrayList(listToView));
    }

    public void onBtnModifyClicked(MouseEvent mouseEvent) {
        GlobalVariables.setIsActionCompleted(false);
        int position = lstOrders.getSelectionModel().getSelectedIndex();
        FinishedOrdVariables.setOrderObject(ord.get(position));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(SHOW_SINGLE_ORDERS_PANE_FXML));
        AnchorPane anchorPane = null;
        try {
            anchorPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(Objects.requireNonNull(anchorPane));
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();

        getAllRecords();
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));

        if(GlobalVariables.getIsActionCompleted())
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordChanged());
        else
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotChanged());

        textAnimations.startLabelsPulsing();
    }

    public void onBtnDeleteClicked(MouseEvent mouseEvent) {
        int position = lstOrders.getSelectionModel().getSelectedIndex();
        finishedOrdersObject = ord.get(position);
        try{
           finishedOrdersAccess.deleteFinishedOrder(finishedOrdersObject);
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsDeleted());
        }
        catch(Exception e){
            e.printStackTrace();
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotDeleted());
        }
        getAllRecords();
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(),
                String.valueOf(orderDatePicker.valueProperty().getValue()));

        textAnimations.startLabelsPulsing();
    }

    public void onRecoverButtonClicked(MouseEvent event){
        int position = lstOrders.getSelectionModel().getSelectedIndex();
        finishedOrdersObject = ord.get(position);
        try{
            finishedOrdersAccess.moveFinishedOrderToOrders(finishedOrdersObject);
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsRecovered());
        }
        catch(Exception e){
            e.printStackTrace();
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotRecovered());
        }
        getAllRecords();
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(),
                String.valueOf(orderDatePicker.valueProperty().getValue()));

        textAnimations.startLabelsPulsing();
    }

    public void btnRefreshClicked(){
        getAllRecords();
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(),
                String.valueOf(orderDatePicker.valueProperty().getValue()));
        btnDelete.disableProperty().setValue(true);
        btnRecover.disableProperty().setValue(true);
    }
}
