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
import pl.Vorpack.app.DatabaseAccess.OrdersAccess;
import pl.Vorpack.app.DatabaseAccess.SingleOrdersAccess;
import pl.Vorpack.app.Domain.Client;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.GlobalVariables.CliVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;

import java.io.IOException;
import java.util.*;

/**
 * Created by Paweł on 2018-02-22.
 */
public class ShowOrdersController {


    private static final String SHOW_SINGLE_ORDERS_PANE_FXML = "/fxml/orders/ShowSingleOrders.fxml";

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXComboBox columnsCmbBox;
    @FXML
    private Label StatusViewer;
    @FXML
    private JFXButton btnModify;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXButton btnEnd;
    @FXML
    private JFXButton btnAdd;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private JFXListView<String> lstOrders;
    @FXML
    private DatePicker orderDatePicker;
    @FXML
    private JFXComboBox datesCmbBox;


    private List<Orders> ord = new ArrayList<>();
    private List<Client> cli = new ArrayList<>();
    private List<Dimiensions> dim = new ArrayList<>();
    private OrdersAccess ordersAccess = new OrdersAccess();
    private Orders orderObject = new Orders();
    private SortedList<Object []> sortedData;
    private FilteredList<Object []> ordersFilteredList;
    private List<String> listToView = new ArrayList<>();
    private int gate, dateGate;
    private TextAnimations textAnimations;


    @FXML
    private void initialize(){
        StatusViewer.setOpacity(0);
        textAnimations = new TextAnimations(StatusViewer);
        gate = 1;
        OrdVariables.setOrderObject(null);
        CliVariables.setObject(null);

        if(GlobalVariables.getAccess().equals("Użytkownik")){
            btnAdd.setDisable(true);
        }

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
        btnEnd.setDisable(true);
        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
        txtSearch.disableProperty().setValue(true);
        orderDatePicker.disableProperty().setValue(true);

        getAllRecords();
        sortedData = new SortedList<>(ordersFilteredList);
        sortGate();
        listToView = fetchToList(sortedData);
        lstOrders.setItems(FXCollections.observableArrayList(listToView));

        lstOrders.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            int index = 0;
            if(newValue == null){
                btnDelete.disableProperty().setValue(true);
                btnModify.disableProperty().setValue(true);
                btnEnd.setDisable(true);
            }
            else{
                btnEnd.setDisable(false);
                btnModify.disableProperty().setValue(false);
                if(!GlobalVariables.getAccess().equals("Użytkownik")){
                    btnDelete.disableProperty().setValue(false);
                }
            }
        }));

        orderDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });

        columnsCmbBox.valueProperty().addListener((obs, oldValue, newValue) ->{
            if(newValue.toString().isEmpty()){
                txtSearch.disableProperty().setValue(true);
            }
            else{
                txtSearch.disableProperty().setValue(false);
            }
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

    private void sortGate() {
        if(gate == 1)
            setComparatorOrderDate();
        else if(gate == 2)
            setComparatorReceiveDate();
        else if(gate == 3)
            setComparatorFirmName();
    }

    private void setComparatorOrderDate() {
        sortedData.setComparator((o1, o2) -> {
            Orders ordersObject = (Orders)o1[0];
            Orders ordersObject_2 = (Orders)o2[0];

            return ordersObject.getOrder_date().isBefore(ordersObject_2.getOrder_date()) ? -1
                    : ordersObject_2.getOrder_date().isBefore(ordersObject.getOrder_date()) ? 1
                    : 0;
        });
    }

    private void setComparatorReceiveDate() {
        sortedData.setComparator((o1, o2) -> {
            Orders ordersObject = (Orders)o1[0];
            Orders ordersObject_2 = (Orders)o2[0];

            return ordersObject.getOrder_receive_date().isBefore(ordersObject_2.getOrder_receive_date()) ? -1
                    : ordersObject_2.getOrder_receive_date().isBefore(ordersObject.getOrder_receive_date()) ? 1
                    : 0;
        });
    }

    private void setComparatorFirmName(){
        sortedData.setComparator((o1, o2) -> {
            Client clientObject = (Client)o1[1];
            Client clientObject_2 = (Client)o2[1];
            String name_1 = clientObject.getFirmName().toLowerCase();
            String name_2 = clientObject_2.getFirmName().toLowerCase();

            return name_1.compareTo(name_2);
        });
    }

    private void getRecordsIDColumn(String searchedText, String searchedData) {
        ordersFilteredList.setPredicate(obj -> {
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
        ordersFilteredList.setPredicate(obj -> {
            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            Client cliObj = (Client) obj[1];
            Orders ordersObj = (Orders) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(cliObj.getFirmName()).toLowerCase().contains(lowerCaseValue))
                    return true;
            }
            else {
                if(String.valueOf(cliObj.getFirmName()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
                    return true;
            }
            return false;
        });
    }

    private void getRecordsMaterialsColumn(String searchedText, String searchedData) {
        ordersFilteredList.setPredicate(obj -> {
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
        ordersFilteredList.setPredicate(obj -> {
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
        ordersFilteredList.setPredicate(obj -> {
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
        ordersFilteredList.setPredicate(obj -> {
            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null"))
                return true;

            Orders ordersObj = (Orders) obj[0];
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

    private void getAllRowsColumns(String searchedText, String searchedData) {
        ordersFilteredList.setPredicate(obj -> {
            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            Orders ordersObj = (Orders) obj[0];
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

    private void getAllRecords(){
        List<Object[]> ordersCollection = new ArrayList<>();

        OrdersAccess ordersAccess = new OrdersAccess();
        try{
            List<Orders> results = ordersAccess.findAllOrders();

            for(Orders o  : results){
                Client clientObject = o.getClient();
                Object[] obj = new Object[]{o, clientObject};
                ordersCollection.add(obj);
            }
            ObservableList<Object[]> data = FXCollections.observableArrayList(ordersCollection);
            ordersFilteredList = new FilteredList<>(data, p -> true);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private List<String> fetchToList(List<Object[]> obs){
        String item;
        List<String> temp_records = new ArrayList<>();
        ord.clear();
        cli.clear();
        for(Object[] o : obs){
            Orders orderObject = (Orders) o[0];
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
            getAllRowsColumns(searchedText, searchedData);
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
        sortedData = new SortedList<>(ordersFilteredList);
        sortGate();
        listToView = fetchToList(sortedData);
        lstOrders.setItems(FXCollections.observableArrayList(listToView));
    }

    public void onBtnAddClicked(MouseEvent mouseEvent) {
        GlobalVariables.setIsActionCompleted(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(SHOW_SINGLE_ORDERS_PANE_FXML));
        AnchorPane anchorPane = null;
        OrdVariables.setOrderObject(null);
        CliVariables.setObject(null);

        try {
            anchorPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(anchorPane);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();
        getAllRecords();
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));

        if(GlobalVariables.getIsActionCompleted())
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordAdded());
        else
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotAdded());

        textAnimations.startLabelsPulsing();
    }

    public void onBtnModifyClicked(MouseEvent mouseEvent) {
        GlobalVariables.setIsActionCompleted(false);
        int position;
        position = lstOrders.getSelectionModel().getSelectedIndex();
        OrdVariables.setOrderObject(ord.get(position));
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
        orderObject = ord.get(position);
        try{
            ordersAccess.deleteOrder(orderObject);
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsDeleted());
        }
        catch (Exception e){
            e.printStackTrace();
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotDeleted());
        }
        getAllRecords();
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(),
                String.valueOf(orderDatePicker.valueProperty().getValue()));
        textAnimations.startLabelsPulsing();
    }

    public void onEndButtonClicked(MouseEvent mouseEvent){
        int position = lstOrders.getSelectionModel().getSelectedIndex();
        orderObject = ord.get(position);
        try{
            ordersAccess.moveOrderToOrdersStory(orderObject);
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsClosed());
        }
        catch(Exception e){
            e.printStackTrace();
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotClosed());
        }
        getAllRecords();
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(),
                String.valueOf(orderDatePicker.valueProperty().getValue()));
        textAnimations.startLabelsPulsing();
    }

    public void btnRefreshClicked() throws IOException{
        getAllRecords();
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(),
                String.valueOf(orderDatePicker.valueProperty().getValue()));
        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
    }
}
