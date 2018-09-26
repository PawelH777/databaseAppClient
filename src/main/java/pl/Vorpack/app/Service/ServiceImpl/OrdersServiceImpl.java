package pl.Vorpack.app.Service.ServiceImpl;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Constans.OrderColumn;
import pl.Vorpack.app.DatabaseAccess.OrdersAccess;
import pl.Vorpack.app.DatabaseAccess.SingleOrdersAccess;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Dto.OrdersDTO;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.Service.OrdersService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static pl.Vorpack.app.Constans.DateItem.ORDER_DATE;
import static pl.Vorpack.app.Constans.DateItem.RECEIVE_ORDER_DATE;
import static pl.Vorpack.app.Constans.OrderColumn.ALL_ITEMS;

public class OrdersServiceImpl implements OrdersService {

    private static final String NULL = "null";
    private FilteredList<OrdersDTO> orders;
    private OrdersAccess ordersAccess = new OrdersAccess();
    private SingleOrdersAccess singleOrdersAccess = new SingleOrdersAccess();
    private JFXComboBox<String> columnsCmbBox;
    private JFXComboBox datesCmbBox;

    public OrdersServiceImpl(JFXComboBox<String> columnsCmbBox, JFXComboBox datesCmbBox) {
        this.columnsCmbBox = columnsCmbBox;
        this.datesCmbBox = datesCmbBox;
    }

    public OrdersServiceImpl() {
    }

    @Override
    public FilteredList<OrdersDTO> getOrders(Boolean isFinished) {
        OrdVariables.setOrdersFromDatabase(ordersAccess.findOrdersWithFinished(isFinished));
        List<OrdersDTO> ordersTableValuesCollection = new ArrayList<>();
        OrdersDTO ordersDTO;
        try {
            List<Orders> ordersCollection = OrdVariables.getOrdersFromDatabase();

            for (Orders o : ordersCollection) {
                ordersDTO = new OrdersDTO(o.getOrder_id(), o.getClients().getFirmName(),
                        o.getOrder_date(), o.getOrder_receive_date(), o.getSingle_orders_finished(),
                        o.getSingle_orders_unfinished(), o.getMaterials(), o.getOrder_note());
                ordersTableValuesCollection.add(ordersDTO);
            }
            ObservableList<OrdersDTO> data = FXCollections.observableArrayList(ordersTableValuesCollection);
            orders = new FilteredList<>(data, p -> true);
        } catch (Exception e) {
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
        return orders;
    }

    @Override
    public void filterRecords(String searchedText, String searchedData) {
        if (columnsCmbBox.getSelectionModel().getSelectedItem() == null ||
                columnsCmbBox.getSelectionModel().getSelectedItem().equals(ALL_ITEMS))
            filterByEveryColumn(searchedText, searchedData);
        else
            filterByOneColumn(searchedText, searchedData);
    }

    @Override
    public void updateOrder(Orders order) {
        ordersAccess.updateOrder(order);
    }

    @Override
    public void deleteOrder(Orders order) {
        ordersAccess.deleteOrder(order);
    }

    @Override
    public void createOrder(Orders order) {
        ordersAccess.createOrder(order);
    }

    @Override
    public void updateBySingleOrder(Orders order) {
        SingleOrdersAccess singleOrdersAccess = new SingleOrdersAccess();
        Long finishedSingleOrdersValue = 0L;
        Long unfinishedSingleOrdersValue = 0L;
        BigDecimal sumMaterialsValue = BigDecimal.ZERO;
        List<SingleOrders> singleOrdersList = singleOrdersAccess.findByOrders(order);
        for (SingleOrders object : singleOrdersList) {
            if (object.getFinished())
                finishedSingleOrdersValue++;
            else
                unfinishedSingleOrdersValue++;

            sumMaterialsValue = sumMaterialsValue.add(object.getMaterials());
        }
        order.setMaterials(sumMaterialsValue);
        order.setSingle_orders_finished(finishedSingleOrdersValue);
        order.setSingle_orders_unfinished(unfinishedSingleOrdersValue);
        order.setOrderFinished(areAllSingleOrdersFinished(order));
        updateOrder(order);
    }

    @Override
    public void changeOrdersStatus(Orders order) {
        ordersAccess.changeOrdersStatus(order);
    }

    private void filterByEveryColumn(String searchedText, String searchedData) {
        orders.setPredicate(obj -> {
            if ((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;
            String lowerCaseValue = searchedText.toLowerCase();
            String date = null;
            if (datesCmbBox.getSelectionModel().getSelectedItem() != null)
                date = getDate(obj);
            if (searchedData == null || searchedData.isEmpty() || searchedData.equals(NULL)) {
                if (String.valueOf(obj.getOrder_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if (String.valueOf(obj.getMaterials()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else return String.valueOf(obj.getFirmName()).toLowerCase().contains(lowerCaseValue);
            } else {
                if (String.valueOf(obj.getOrder_id()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if (String.valueOf(obj.getMaterials()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else return String.valueOf(obj.getFirmName()).toLowerCase().contains(lowerCaseValue)
                            && searchedData.equals(date);
            }
        });
    }

    private void filterByOneColumn(String searchedText, String searchedData) {
        orders.setPredicate(obj -> {
            if ((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals(NULL)))
                return true;
            String lowerCaseValue = getNonNullLowerCaseValue(searchedText);
            String date = null;
            if (datesCmbBox.getSelectionModel().getSelectedItem() != null)
                date = getDate(obj);
            String filterValue = getFilter(columnsCmbBox.getSelectionModel().getSelectedItem(), obj);
            if (searchedData == null || searchedData.isEmpty() || searchedData.equals(NULL)) {
                return filterValue.contains(lowerCaseValue);
            } else {
                return filterValue.contains(lowerCaseValue) && searchedData.equals(date);
            }
        });
    }

    private String getDate(OrdersDTO obj) {
        if (datesCmbBox.getSelectionModel().getSelectedItem().equals(ORDER_DATE))
            return String.valueOf(obj.getOrder_date());
        else if (datesCmbBox.getSelectionModel().getSelectedItem().equals(RECEIVE_ORDER_DATE))
            return String.valueOf(obj.getOrder_receive_date());
        throw new RuntimeException();
    }

    private String getNonNullLowerCaseValue(String string) {
        if (string == null)
            return "";

        return string.toLowerCase();
    }

    private String getFilter(String column, OrdersDTO obj) {
        switch (column) {
            case OrderColumn.ID:
                return String.valueOf(obj.getOrder_id()).toLowerCase();
            case OrderColumn.FIRM_NAME:
                return String.valueOf(obj.getFirmName()).toLowerCase();
            case OrderColumn.MATERIALS:
                return String.valueOf(obj.getMaterials()).toLowerCase();
            case OrderColumn.FINISHED_TASKS:
                return String.valueOf(obj.getSingle_orders_completed()).toLowerCase();
            case OrderColumn.UNFINISHED_TASKS:
                return String.valueOf(obj.getSingle_orders_unfinished()).toLowerCase();
            default:
                return "";
        }
    }

    private boolean areAllSingleOrdersFinished(Orders order) {
        int finishedSingleOrders = 0;
        try {
            List<SingleOrders> singleOrders = singleOrdersAccess.findByOrders(order);
            for (SingleOrders singleOrder : singleOrders)
                if (singleOrder.getFinished())
                    finishedSingleOrders++;
            return finishedSingleOrders == singleOrders.size();
        } catch (Exception e) {
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
        throw new RuntimeException();
    }
}
