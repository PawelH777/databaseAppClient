package pl.Vorpack.app.Controller.OrderController;

import com.jfoenix.controls.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
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

/**
 * Created by Paweł on 2018-02-22.
 */
public class OrderController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXComboBox<String> columnsCmbBox;
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
    private JFXButton btnToggle;
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
    private CommonService commonService;
    private ClientService clientService;
    private SortedList<OrdersDTO> sortedData;
    private FilteredList<OrdersDTO> ordersFilteredList;
    private TextAnimations textAnimations;

    @FXML
    private void initialize() {
        assignColumns();
        pulseStatusBar();
        OrdVariables.setOrderObject(null);
        ClientVariables.setObject(null);
        if (GlobalVariables.getAccess().equals(USER))
            btnAdd.setDisable(true);
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
        ordersFilteredList = ordersService.getOrders(false);
        sortedData = new SortedList<>(ordersFilteredList);
        sortedData.comparatorProperty().bind(ordersViewer.comparatorProperty());
        ordersViewer.setItems(FXCollections.observableArrayList(sortedData));
        ordersViewer.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                btnDelete.setDisable(true);
                btnModify.setDisable(true);
                btnEnd.setDisable(true);
                btnToggle.setDisable(true);
            } else {
                btnEnd.setDisable(false);
                btnToggle.setDisable(false);
                if (!GlobalVariables.getAccess().equals(USER)) {
                    btnDelete.setDisable(false);
                    btnModify.setDisable(false);
                }
            }
        }));
        orderDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            filter(txtSearch.textProperty().getValue(),
                    String.valueOf(orderDatePicker.valueProperty().getValue()));
        });
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filter(txtSearch.textProperty().getValue(),
                    String.valueOf(orderDatePicker.valueProperty().getValue()));
        });
        columnsCmbBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.isEmpty())
                txtSearch.setDisable(true);
            else
                txtSearch.setDisable(false);
            filter(txtSearch.textProperty().getValue(),
                    String.valueOf(orderDatePicker.valueProperty().getValue()));
        });
        datesCmbBox.valueProperty().addListener((ObservableValue obs, Object oldValue, Object newValue) -> {
            if (!newValue.toString().isEmpty())
                orderDatePicker.disableProperty().setValue(false);
            filter(txtSearch.textProperty().getValue(),
                    String.valueOf(orderDatePicker.valueProperty().getValue()));
        });
    }

    private void initServices() {
        ordersService = new OrdersServiceImpl();
        commonService = new CommonServiceImpl();
        clientService = new ClientServiceImpl();
    }

    private void disableButtons() {
        btnToggle.setDisable(true);
        btnEnd.setDisable(true);
        btnDelete.setDisable(true);
        btnModify.setDisable(true);
        txtSearch.setDisable(true);
        orderDatePicker.setDisable(true);
    }

    private void pulseStatusBar() {
        StatusViewer.setOpacity(0);
        textAnimations = new TextAnimations(StatusViewer);
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

    private void filter(String searchedText, String searchedData) {
        ordersService.filterRecords(columnsCmbBox.getSelectionModel().getSelectedItem(),
                columnsCmbBox.getSelectionModel().getSelectedItem(), searchedText, searchedData);
        sortedData = new SortedList<>(ordersFilteredList);
        ordersViewer.setItems(FXCollections.observableArrayList(sortedData));
    }

    public void onBtnAddClicked() throws IOException {
        GlobalVariables.setIsActionCompleted(false);
        OrdVariables.setOrderObject(null);
        GlobalVariables.setIsCreate(true);
        String addWindowTitle = "Dodaj zamówienie";
        commonService.openScene(PathConstans.ORDER_EDITOR_PANE_PATH, addWindowTitle, false);
        setReturnedInformation();
    }

    public void onBtnModifyClicked() throws IOException {
        GlobalVariables.setIsActionCompleted(false);
        OrdVariables.setOrderObject(getSelectedItem());
        GlobalVariables.setIsCreate(false);
        String modifyWindowTitle = "Dodaj zamówienie";
        commonService.openScene(PathConstans.ORDER_EDITOR_PANE_PATH, modifyWindowTitle, false);
        setReturnedInformation();
    }

    public void onBtnToggleClicked() throws IOException {
        OrdVariables.setOrderObject(getSelectedItem());
        String toggleWindowTitle = "Zamówienia jednostkowe";
        commonService.openScene(PathConstans.SINGLE_ORDERS_PANE_PATH, toggleWindowTitle, true);
        setReturnedInformation();
    }

    private void setReturnedInformation() {
        getOrderRecords();
        if (GlobalVariables.getIsActionCompleted())
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordAdded());
        else
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotAdded());
        textAnimations.startLabelsPulsing();
    }

    private Orders getSelectedItem() {
        OrdersDTO ordersDTO = ordersViewer.getSelectionModel().getSelectedItem();
        Clients clients = getClient(ordersDTO);
        return new Orders(ordersDTO.getOrder_id(), clients, ordersDTO.getMaterials(), ordersDTO.getOrder_receive_date(),
                ordersDTO.getOrder_date(), ordersDTO.getOrderNote(), ordersDTO.getSingle_orders_completed(),
                ordersDTO.getSingle_orders_unfinished(), false);
    }

    private Clients getClient(OrdersDTO ordersDTO) {
        return clientService.findByFirmName(ordersDTO.getFirmName());
    }

    public void onBtnDeleteClicked() {
        ordersService.deleteOrder(getSelectedItem());
        getOrderRecords();
        textAnimations.startLabelsPulsing();
    }

    public void onEndButtonClicked() {
        ordersService.updateOrder(getSelectedItem());
        getOrderRecords();
        textAnimations.startLabelsPulsing();
    }

    private void getOrderRecords() {
        ordersFilteredList = ordersService.getOrders(false);
        filter(txtSearch.textProperty().getValue(),
                String.valueOf(orderDatePicker.valueProperty().getValue()));
    }

    public void btnRefreshClicked() {
        getOrderRecords();
        btnDelete.setDisable(true);
        btnModify.setDisable(true);
    }
}
