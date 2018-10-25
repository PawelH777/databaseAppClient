package pl.Vorpack.app.Controller.OrderController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Domain.Clients;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.Service.ClientService;
import pl.Vorpack.app.Service.OrdersService;
import pl.Vorpack.app.Service.ServiceImpl.ClientServiceImpl;
import pl.Vorpack.app.Service.ServiceImpl.OrdersServiceImpl;
import pl.Vorpack.app.Service.ServiceImpl.SingleOrdersServiceImpl;
import pl.Vorpack.app.Service.SingleOrdersService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static pl.Vorpack.app.Constans.UserConstans.USER;


public class OrderEditorController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private DatePicker dateOrder;
    @FXML
    private DatePicker dateCreateOrder;
    @FXML
    private JFXTextField txtFirmName;
    @FXML
    private JFXTextArea txtNote;
    @FXML
    private JFXButton btnAddOrChangeOrder;

    private OrdersService ordersService = new OrdersServiceImpl();
    private ClientService clientService = new ClientServiceImpl();
    private SingleOrdersService singleOrdersService = new SingleOrdersServiceImpl();

    private Orders orderObject = new Orders();
    private String firmNameValue;
    private LocalDate createOrderDateValue;
    private LocalDate orderDateValue;
    private String noteValue;
    private Long finishedSingleOrdersValue = 0L;
    private Long unfinishedSingleOrdersValue = 0L;
    private BigDecimal sumMaterialsValue = BigDecimal.ZERO;
    private InfoAlerts infoAlerts = new InfoAlerts();


    @FXML
    public void initialize(){
        dateCreateOrder.setValue(LocalDate.now());
        dateOrder.setValue(dateCreateOrder.getValue().plusDays(1));
        refreshDateBlockade(LocalDate.now());
        if(!GlobalVariables.getIsCreate()){
            btnAddOrChangeOrder.setText("Zmień zamówienie");
            orderObject = OrdVariables.getOrderObject();
            setFields();
        }
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

        txtFirmName.textProperty().addListener((obs, oldValue, newValue)->{
            checkIfOrderFieldsAreEmptyAndChangeAccessToButtons();
        });
        txtNote.textProperty().addListener((obs, oldValue, newValue)->{
            checkIfOrderFieldsAreEmptyAndChangeAccessToButtons();
        });
    }

    private void setFields() {
        txtFirmName.setText(orderObject.getClients().getFirmName());
        dateCreateOrder.setValue(orderObject.getOrder_receive_date());
        dateOrder.setValue(orderObject.getOrder_date());
        txtNote.setText(orderObject.getOrder_note());
    }

    private void refreshDateBlockade(LocalDate now) {
        final Callback<DatePicker, DateCell> dayCellFactory = restrainDatePicker(now);
        dateOrder.setDayCellFactory(dayCellFactory);
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

    private void checkIfOrderFieldsAreEmptyAndChangeAccessToButtons() {
        if(!USER.equals(GlobalVariables.getAccess()))
            if(!txtFirmName.textProperty().getValue().isEmpty() && dateCreateOrder.valueProperty().getValue() != null
                    && dateOrder.valueProperty().getValue() != null)
                btnAddOrChangeOrder.setDisable(false);
            else
                btnAddOrChangeOrder.setDisable(true);

    }

    public void addOrChangeOrderButtonClicked(){
        if(GlobalVariables.getIsCreate())
            addNewOrder();
        else
            updateNewOrder();
        Stage thisStage = (Stage) anchorPane.getScene().getWindow();
        thisStage.close();
    }

    private void addNewOrder(){
        attachValuesToVariables();
        try{
            Clients clientsObject = clientService.create(firmNameValue);
            ordersService.createOrder(new Orders(clientsObject, BigDecimal.ZERO, createOrderDateValue, orderDateValue,
                    noteValue, 0L, 0L, false));
            GlobalVariables.setIsActionCompleted(true);
        }
        catch (Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }
    }

    private void attachValuesToVariables() {
        firmNameValue = txtFirmName.getText();
        createOrderDateValue = dateCreateOrder.getValue();
        orderDateValue = dateOrder.getValue();
        noteValue = txtNote.getText();
    }

    private void updateNewOrder(){
        attachValuesToVariables();
        try{
            Clients clientsObject = clientService.create(firmNameValue);
            List<SingleOrders> singleOrdersList = singleOrdersService.getSingleOrdersBySingleOrder(orderObject);
            for(SingleOrders object : singleOrdersList){
                if(object.getFinished())
                    finishedSingleOrdersValue++;
                else
                    unfinishedSingleOrdersValue++;
                sumMaterialsValue = sumMaterialsValue.add(object.getMaterials());
            }
            orderObject.setClients(clientsObject);
            orderObject.setMaterials(sumMaterialsValue);
            orderObject.setOrder_receive_date(createOrderDateValue);
            orderObject.setOrder_date(orderDateValue);
            orderObject.setOrder_note(noteValue);
            orderObject.setSingle_orders_finished(finishedSingleOrdersValue);
            orderObject.setSingle_orders_unfinished(unfinishedSingleOrdersValue);
            ordersService.updateOrder(orderObject);
            GlobalVariables.setIsActionCompleted(true);
        }
        catch (Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }
    }
}
