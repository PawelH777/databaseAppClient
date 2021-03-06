package pl.Vorpack.app.Controller.ClosedOrderController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Constans.PathConstans;
import pl.Vorpack.app.Domain.Clients;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.GlobalVariables.ClientVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Dto.OrdersDTO;
import pl.Vorpack.app.Service.ClientService;
import pl.Vorpack.app.Service.CommonService;
import pl.Vorpack.app.Service.OrdersService;
import pl.Vorpack.app.Service.ServiceImpl.ClientServiceImpl;
import pl.Vorpack.app.Service.ServiceImpl.CommonServiceImpl;
import pl.Vorpack.app.Service.ServiceImpl.OrdersServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static pl.Vorpack.app.Constans.DateItemConstans.ORDER_DATE;
import static pl.Vorpack.app.Constans.DateItemConstans.RECEIVE_ORDER_DATE;
import static pl.Vorpack.app.Constans.UserConstans.USER;
import static pl.Vorpack.app.Constans.OrderColumnConstans.*;
import static pl.Vorpack.app.Constans.OrderColumnConstans.MATERIALS;
import static pl.Vorpack.app.Constans.OrderColumnConstans.UNFINISHED_TASKS;

/**
 * Created by Paweł on 2018-03-01.
 */
public class ClosedOrderController {

    @FXML
    public JFXButton btnToogle;
    @FXML
    private JFXComboBox columnsCmbBox;
    @FXML
    private Label statusViewer;
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

    private OrdersService ordersService;
    private ClientService clientService;
    private CommonService commonService;
    private SortedList<OrdersDTO> sortedData;
    private FilteredList<OrdersDTO> filteredList;
    private TextAnimations textAnimations = new TextAnimations(statusViewer);

    @FXML
    private void initialize(){
        assignColumns();
        makeStatusBarPulsing();
        OrdVariables.setOrderObject(null);
        ClientVariables.setObject(null);
        columnsCmbBox.getItems().addAll(
                ALL_ITEMS,
                ID,
                FIRM_NAME,
                FINISHED_TASKS,
                UNFINISHED_TASKS,
                MATERIALS
        );
        datesCmbBox.getItems().addAll(
                ORDER_DATE,
                RECEIVE_ORDER_DATE
        );
        initServices();
        disableButtons();
        filteredList = ordersService.getOrders(true);
        sortedData = new SortedList<>(filteredList);
        ordersViewer.setItems(FXCollections.observableArrayList(sortedData));
        txtSearch.disableProperty().setValue(true);
        orderDatePicker.disableProperty().setValue(true);
        ordersViewer.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue == null){
                btnDelete.setDisable(true);
                btnRecover.setDisable(true);
                btnToogle.setDisable(true);
            }
            else{
                if(!GlobalVariables.getAccess().equals(USER))
                    btnDelete.setDisable(false);
                btnRecover.setDisable(false);
                btnToogle.setDisable(false);
            }
        }));
        orderDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            filter(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filter(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });
        columnsCmbBox.valueProperty().addListener((obs, oldValue, newValue) ->{
            if(newValue.toString().isEmpty())
                txtSearch.setDisable(true);
            else
                txtSearch.setDisable(false);
            filter(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });
        datesCmbBox.valueProperty().addListener((ObservableValue obs, Object oldValue, Object newValue) ->{
            if(!newValue.toString().isEmpty())
                orderDatePicker.disableProperty().setValue(false);

            filter(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });
    }

    public void onRefreshButtonClicked(MouseEvent mouseEvent) {
        getOrders();
        filter(txtSearch.textProperty().getValue(),
                String.valueOf(orderDatePicker.valueProperty().getValue()));
        btnDelete.disableProperty().setValue(true);
        btnRecover.disableProperty().setValue(true);
    }

    public void onDeleteButtonClicked(MouseEvent mouseEvent) {
        ordersService.deleteOrder(getSelectedItem());
        getOrders();
        textAnimations.startLabelsPulsing();
    }

    public void onRecoverButtonClicked(){
        Orders order = getSelectedItem();
        order.setOrderFinished(false);
        try{
            ordersService.updateOrder(order);
            statusViewer.setText(InfoAlerts.getStatusWhileRecordIsRecovered());
        }
        catch(Exception e){
            e.printStackTrace();
            statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotRecovered());
        }
        getOrders();
        filter(txtSearch.textProperty().getValue(),
                String.valueOf(orderDatePicker.valueProperty().getValue()));

        textAnimations.startLabelsPulsing();
    }

    public void onToggleButtonClicked() throws IOException {
        OrdVariables.setOrderObject(getSelectedItem());
        String toggleWindowTitle = "Zamówienia jednostkowe";
        commonService.openScene(PathConstans.CLOSED_SINGLE_ORDERS_PANE_PATH, toggleWindowTitle, true);
        setReturnedInformation();
    }

    private void initServices() {
        ordersService = new OrdersServiceImpl();
        clientService = new ClientServiceImpl();
        commonService = new CommonServiceImpl();
    }

    private void assignColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("order_id"));
        firmNameColumn.setCellValueFactory(new PropertyValueFactory<>("firmName"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("order_date"));
        orderReceiveDateColumn.setCellValueFactory(new PropertyValueFactory<>("order_receive_date"));
        finishedOrdersColumn.setCellValueFactory(new PropertyValueFactory<>("single_orders_completed"));
        unfinishedOrdersColumn.setCellValueFactory(new PropertyValueFactory<>("single_orders_unfinished"));
        orderMaterialsColumn.setCellValueFactory(new PropertyValueFactory<>("materials"));
    }

    private void makeStatusBarPulsing() {
        statusViewer.setOpacity(0);
        textAnimations = new TextAnimations(statusViewer);
    }

    private void disableButtons() {
        btnRecover.setDisable(true);
        btnDelete.setDisable(true);
    }

    private void filter(String searchedText, String searchedData) {
        String columnsCmbBoxValue;
        String datesCmbBoxValue;
        if(columnsCmbBox.getSelectionModel().getSelectedItem() == null)
            columnsCmbBoxValue = "";
        else
            columnsCmbBoxValue = columnsCmbBox.getSelectionModel().getSelectedItem().toString();
        if (datesCmbBox.getSelectionModel().getSelectedItem() == null)
            datesCmbBoxValue = "";
        else
            datesCmbBoxValue = datesCmbBox.getSelectionModel().getSelectedItem().toString();
        ordersService.filterRecords(columnsCmbBoxValue, datesCmbBoxValue, searchedText, searchedData);
        sortedData = new SortedList<>(filteredList);
        ordersViewer.setItems(FXCollections.observableArrayList(sortedData));
    }

    private void getOrders(){
        filteredList = ordersService.getOrders(true);
        filter(txtSearch.textProperty().getValue(),
                String.valueOf(orderDatePicker.valueProperty().getValue()));
    }

    private Orders getSelectedItem(){
        OrdersDTO ordersDTO = ordersViewer.getSelectionModel().getSelectedItem();
        Clients clients = getClient(ordersDTO);
        return new Orders(ordersDTO.getOrder_id(), clients, ordersDTO.getMaterials(), ordersDTO.getOrder_receive_date(),
                ordersDTO.getOrder_date(), ordersDTO.getOrderNote(), ordersDTO.getSingle_orders_completed(),
                ordersDTO.getSingle_orders_unfinished(), true);
    }

    private Clients getClient(OrdersDTO ordersDTO) {
        return clientService.findByFirmName(ordersDTO.getFirmName());
    }

    private void setReturnedInformation() {
        getOrders();

        if(GlobalVariables.getIsActionCompleted())
            statusViewer.setText(InfoAlerts.getStatusWhileRecordAdded());
        else
            statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotAdded());

        textAnimations.startLabelsPulsing();
    }
}
