package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.DatabaseAccess.OrdersAccess;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.GlobalVariables.CliVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.TableValues.OrdersDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    private OrdersAccess ordersAccess = new OrdersAccess();

    private List<Orders> ordersCollection = new ArrayList<>();
    private SortedList<OrdersDTO> sortedData;
    private FilteredList<OrdersDTO> filteredList;

    private Orders order;

    private int dateGate;

    private TextAnimations textAnimations = new TextAnimations(StatusViewer);

    @FXML
    private void initialize(){
        attachValuesToTableColumns();
        makeStatusBarPulsing();
        OrdVariables.setOrderObject(null);
        CliVariables.setObject(null);
        blockAccessToButtons();
        getAllRecords();
        sortedData = new SortedList<>(filteredList);
        ordersViewer.setItems(FXCollections.observableArrayList(sortedData));
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

        ordersViewer.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
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

    private void blockAccessToButtons() {
        btnRecover.setDisable(true);
        btnModify.setDisable(true);
        btnDelete.setDisable(true);
    }

    private void getRecordsIDColumn(String searchedText, String searchedData) {
        filteredList.setPredicate(obj -> {

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
        filteredList.setPredicate(obj -> {

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
            } else {
                if(String.valueOf(obj.getFirmName()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
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
        filteredList.setPredicate(obj -> {
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
        filteredList.setPredicate(obj -> {
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

        filteredList.setPredicate(obj -> {

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

    private void getRowsCmbWszystkie(String searchedText, String searchedData) {
        filteredList.setPredicate(obj -> {
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
        List<OrdersDTO> ordersTableValuesCollection = new ArrayList<>();
        OrdersDTO ordersDTO;
        try{
            ordersCollection = OrdVariables.getOrdersFromDatabase();

            for(Orders o  : ordersCollection){
                ordersDTO = new OrdersDTO(o.getOrder_id(), o.getClient().getFirmName(),
                        o.getOrder_date(), o.getOrder_receive_date(), o.getSingle_orders_finished(),
                        o.getSingle_orders_unfinished(), o.getMaterials());
                ordersTableValuesCollection.add(ordersDTO);
            }
            ObservableList<OrdersDTO> data = FXCollections.observableArrayList(ordersTableValuesCollection);
            filteredList = new FilteredList<>(data, p -> true);
        }catch (Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
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
        ordersViewer.setItems(FXCollections.observableArrayList(sortedData));
    }

    public void onBtnModifyClicked(MouseEvent mouseEvent) {
        GlobalVariables.setIsActionCompleted(false);
        int position = ordersViewer.getSelectionModel().getSelectedIndex();
        OrdVariables.setOrderObject(ordersCollection.get(position));
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
        int position = ordersViewer.getSelectionModel().getSelectedIndex();
        order = ordersCollection.get(position);
        try{
           ordersAccess.deleteOrder(order);
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
        int position = ordersViewer.getSelectionModel().getSelectedIndex();
        order = ordersCollection.get(position);
        try{
            ordersAccess.changeOrdersStatus(order);
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
