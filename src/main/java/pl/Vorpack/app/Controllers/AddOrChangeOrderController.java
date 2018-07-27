package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.DatabaseAccess.ClientAccess;
import pl.Vorpack.app.DatabaseAccess.OrdersAccess;
import pl.Vorpack.app.DatabaseAccess.SingleOrdersAccess;
import pl.Vorpack.app.Domain.Client;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


public class AddOrChangeOrderController {

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

    private ClientAccess clientAccess = new ClientAccess();
    private OrdersAccess ordersAccess = new OrdersAccess();
    private Orders orderObject = new Orders();

    private String firmNameValue;
    private LocalDate createOrderDateValue;
    private LocalDate orderDateValue;
    private String noteValue;
    private Long finishedSingleOrdersValue = 0L;
    private Long unfinishedSingleOrdersValue = 0L;
    private BigDecimal sumMaterialsValue = BigDecimal.ZERO;


    @FXML
    public void initialize(){
        dateCreateOrder.setValue(LocalDate.now());
        dateOrder.setValue(dateCreateOrder.getValue().plusDays(1));

        refreshDateBlockade(LocalDate.now());

        if(!GlobalVariables.getIsCreate()){
            btnAddOrChangeOrder.setText("Zmień zamówienie");
            orderObject = OrdVariables.getOrderObject();
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
        if(!"Użytkownik".equals(GlobalVariables.getAccess()))
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
    }

    private void addNewOrder(){
        attachValuesToVariables();

        Client clientObject = new Client(firmNameValue);
        try{
            List<Client> allClientsWithFirmName = clientAccess.findClient(firmNameValue);

            if(allClientsWithFirmName.size() == 0){
                clientAccess.createNewClient(clientObject);
                allClientsWithFirmName = clientAccess.findClient(firmNameValue);
            }

            clientObject = allClientsWithFirmName.get(0);

            orderObject = new Orders(clientObject, BigDecimal.ZERO, createOrderDateValue, orderDateValue,
                        noteValue, 0L, 0L, false);
            ordersAccess.createOrder(orderObject);

            GlobalVariables.setIsActionCompleted(true);
        }
        catch (Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
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
        Client clientObject = new Client(firmNameValue);

        try{
            List<Client> allClientsWithFirmName = clientAccess.findClient(firmNameValue);

            if(allClientsWithFirmName.size() == 0){
                clientAccess.createNewClient(clientObject);
                allClientsWithFirmName = clientAccess.findClient(firmNameValue);
            }

            clientObject = allClientsWithFirmName.get(0);

            SingleOrdersAccess singleOrdersAccess = new SingleOrdersAccess();
            List<SingleOrders> singleOrdersList = singleOrdersAccess.findByOrders(orderObject);
            for(SingleOrders object : singleOrdersList){
                if(object.getFinished())
                    finishedSingleOrdersValue++;
                else
                    unfinishedSingleOrdersValue++;

                sumMaterialsValue = sumMaterialsValue.add(object.getMaterials());
            }

            orderObject.setClient(clientObject);
            orderObject.setMaterials(sumMaterialsValue);
            orderObject.setOrder_receive_date(createOrderDateValue);
            orderObject.setOrder_date(orderDateValue);
            orderObject.setOrder_note(noteValue);
            orderObject.setSingle_orders_finished(finishedSingleOrdersValue);
            orderObject.setSingle_orders_unfinished(unfinishedSingleOrdersValue);
            ordersAccess.updateOrder(orderObject);
            GlobalVariables.setIsActionCompleted(true);
        }
        catch (Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }
}
