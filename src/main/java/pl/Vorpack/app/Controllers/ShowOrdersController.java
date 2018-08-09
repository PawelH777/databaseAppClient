package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.DatabaseAccess.ClientAccess;
import pl.Vorpack.app.DatabaseAccess.OrdersAccess;
import pl.Vorpack.app.Domain.Client;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.GlobalVariables.CliVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Dto.OrdersDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Paweł on 2018-02-22.
 */
public class ShowOrdersController {

    private static final String SHOW_SINGLE_ORDERS_PANE_FXML = "/fxml/orders/ShowSingleOrders.fxml";
    private static final String ADD_OR_CHANGE_ORDER_PANE_FXML = "/fxml/orders/AddOrderOrChangeOrder.fxml";

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
    private JFXButton btnToogle;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private DatePicker orderDatePicker;
    @FXML
    private JFXComboBox datesCmbBox;
    @FXML
    private TableView<OrdersDTO> ordersViewer;
    @FXML
    private TableColumn<OrdersDTO, Long> idColumn;
    @FXML
    private TableColumn<OrdersDTO, String> firmNameColumn;
    @FXML
    private TableColumn<OrdersDTO, LocalDate> orderDateColumn;
    @FXML
    private TableColumn<OrdersDTO, LocalDate> orderReceiveDateColumn;
    @FXML
    private TableColumn<OrdersDTO, Long> finishedOrdersColumn;
    @FXML
    private TableColumn<OrdersDTO, Long> unfinishedOrdersColumn;
    @FXML
    private TableColumn<OrdersDTO, BigDecimal> orderMaterialsColumn;

    private List<Orders> ordersCollection;
    private OrdersAccess ordersAccess = new OrdersAccess();
    private Orders orderObject = new Orders();
    private SortedList<OrdersDTO> sortedData;
    private FilteredList<OrdersDTO> ordersFilteredList;
    private int dateGate;
    private TextAnimations textAnimations;


    @FXML
    private void initialize(){
        attachValuesToTableColumns();
        makeStatusBarPulsing();
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
        btnToogle.setDisable(true);
        btnEnd.setDisable(true);
        btnDelete.setDisable(true);
        btnModify.setDisable(true);
        txtSearch.setDisable(true);
        orderDatePicker.setDisable(true);

        getAllRecords();
        sortedData = new SortedList<>(ordersFilteredList);
        sortedData.comparatorProperty().bind(ordersViewer.comparatorProperty());
        ordersViewer.setItems(FXCollections.observableArrayList(sortedData));

        ordersViewer.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue == null){
                btnDelete.setDisable(true);
                btnModify.setDisable(true);
                btnEnd.setDisable(true);
                btnToogle.setDisable(true);
            }
            else{
                btnEnd.setDisable(false);
                btnToogle.setDisable(false);
                if(!GlobalVariables.getAccess().equals("Użytkownik")){
                    btnDelete.setDisable(false);
                    btnModify.setDisable(false);
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
                txtSearch.setDisable(true);
            }
            else{
                txtSearch.setDisable(false);
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

    private void makeStatusBarPulsing() {
        StatusViewer.setOpacity(0);
        textAnimations = new TextAnimations(StatusViewer);
    }

    private void attachValuesToTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<OrdersDTO, Long>("order_id"));
        firmNameColumn.setCellValueFactory(new PropertyValueFactory<OrdersDTO, String>("firmName"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<OrdersDTO, LocalDate>("order_date"));
        orderReceiveDateColumn.setCellValueFactory(new PropertyValueFactory<OrdersDTO, LocalDate>("order_receive_date"));
        finishedOrdersColumn.setCellValueFactory(new PropertyValueFactory<OrdersDTO, Long>("single_orders_completed"));
        unfinishedOrdersColumn.setCellValueFactory(new PropertyValueFactory<OrdersDTO, Long>("single_orders_unfinished"));
        orderMaterialsColumn.setCellValueFactory(new PropertyValueFactory<OrdersDTO, BigDecimal>("materials"));
    }

    private void getRecordsIDColumn(String searchedText, String searchedData) {
        ordersFilteredList.setPredicate(obj -> {
            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;
            String lowerCaseValue = searchedText.toLowerCase();
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(obj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(obj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(obj.getOrder_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(obj.getOrder_id()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
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
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(obj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(obj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(obj.getFirmName()).toLowerCase().contains(lowerCaseValue))
                    return true;
            }
            else {
                if(String.valueOf(obj.getFirmName()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
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
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(obj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(obj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(obj.getMaterials()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(obj.getMaterials()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
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
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(obj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(obj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(obj.getSingle_orders_completed()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(obj.getSingle_orders_completed()).toLowerCase().contains(lowerCaseValue)
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
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(obj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(obj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(obj.getSingle_orders_unfinished()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(obj.getSingle_orders_unfinished()).toLowerCase().contains(lowerCaseValue)
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

            String date = null;

            if(dateGate == 1)
                date = String.valueOf(obj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(obj.getOrder_receive_date());

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

            String date = null;

            if(dateGate == 1)
                date = String.valueOf(obj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(obj.getOrder_receive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(obj.getOrder_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(obj.getMaterials()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(obj.getFirmName()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(obj.getOrder_id()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(obj.getMaterials()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(obj.getFirmName()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
            }
            return false;

        });
    }

    private void getAllRecords(){
        OrdVariables.setOrdersFromDatabase(ordersAccess.findOrdersWithFinished(false));
        List<OrdersDTO> ordersTableValuesCollection = new ArrayList<>();
        OrdersDTO ordersDTO;
        try{
            ordersCollection = OrdVariables.getOrdersFromDatabase();

            for(Orders o : ordersCollection){
                ordersDTO = new OrdersDTO(o.getOrder_id(), o.getClient().getFirmName(),
                        o.getOrder_date(), o.getOrder_receive_date(), o.getSingle_orders_finished(),
                        o.getSingle_orders_unfinished(), o.getMaterials());
                ordersTableValuesCollection.add(ordersDTO);
            }
            ObservableList<OrdersDTO> data = FXCollections.observableArrayList(ordersTableValuesCollection);
            ordersFilteredList = new FilteredList<>(data, p -> true);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
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
        ordersViewer.setItems(FXCollections.observableArrayList(sortedData));
    }

    public void onBtnAddClicked(MouseEvent mouseEvent) {
        GlobalVariables.setIsActionCompleted(false);
        OrdVariables.setOrderObject(null);
        GlobalVariables.setIsCreate(true);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_OR_CHANGE_ORDER_PANE_FXML));
        openNewScene(fxmlLoader);
    }

    private void openNewScene(FXMLLoader fxmlLoader) {
        AnchorPane anchorPane = null;
        try {
            anchorPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert(anchorPane != null);
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
        position = ordersViewer.getSelectionModel().getSelectedIndex();
        OrdVariables.setOrderObject(ordersCollection.get(position));
        GlobalVariables.setIsCreate(false);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_OR_CHANGE_ORDER_PANE_FXML));
        openNewScene(fxmlLoader);
    }

    public void onBtnToogleClicked() {
        OrdVariables.setOrderObject(getSelectedItem());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(SHOW_SINGLE_ORDERS_PANE_FXML));
        AnchorPane anchorPane = null;
        try {
            anchorPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert(anchorPane != null);
        Scene scene = new Scene(anchorPane);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.setTitle("Zamówienia jednostkowe");
        stage.setMaximized(true);
        stage.showAndWait();
        getAllRecords();
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));

        if(GlobalVariables.getIsActionCompleted())
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordAdded());
        else
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotAdded());

        textAnimations.startLabelsPulsing();
    }

    private Orders getSelectedItem(){
        OrdersDTO ordersDTO = ordersViewer.getSelectionModel().getSelectedItem();
        Client client = getClient(ordersDTO);
        return new Orders(ordersDTO.getOrder_id(), client, ordersDTO.getMaterials(), ordersDTO.getOrder_receive_date(),
                ordersDTO.getOrder_date(), null, ordersDTO.getSingle_orders_completed(),
                ordersDTO.getSingle_orders_unfinished(), false);
    }

    private Client getClient(OrdersDTO ordersDTO) {
        ClientAccess access = new ClientAccess();
        return access.findClient(ordersDTO.getFirmName()).get(0);
    }

    public void onBtnDeleteClicked() {
        int position = ordersViewer.getSelectionModel().getSelectedIndex();
        orderObject = ordersCollection.get(position);
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

    public void onEndButtonClicked(){
        int position = ordersViewer.getSelectionModel().getSelectedIndex();
        orderObject = ordersCollection.get(position);
        try{
            ordersAccess.changeOrdersStatus(orderObject);
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

    public void btnRefreshClicked(){
        getAllRecords();
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(),
                String.valueOf(orderDatePicker.valueProperty().getValue()));
        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
    }
}
