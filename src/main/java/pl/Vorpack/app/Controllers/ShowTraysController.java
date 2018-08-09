package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Data.TraysStatus;
import pl.Vorpack.app.DatabaseAccess.OrdersAccess;
import pl.Vorpack.app.DatabaseAccess.SingleOrdersAccess;
import pl.Vorpack.app.DatabaseAccess.TraysAccess;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Domain.Trays;
import pl.Vorpack.app.Dto.ActionButtonTableCell;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.SingleOrdVariables;
import pl.Vorpack.app.Dto.TraysDTO;

import java.util.ArrayList;
import java.util.List;

public class ShowTraysController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView<TraysDTO> traysTable;
    @FXML
    private TableColumn<TraysDTO, String> nameColumn;
    @FXML
    private TableColumn<TraysDTO,String> statusColumn;
    @FXML
    private TableColumn<TraysDTO, JFXButton> changeStatusColumn;
    @FXML
    private Label statusViewer;
    @FXML
    private TextArea commentary;

    private SingleOrdersAccess singleOrdersAccess = new SingleOrdersAccess();
    private SingleOrders singleOrder;
    private Orders order;
    private List<Trays> trays = new ArrayList<>();
    private List<TraysDTO> traysDTOS = new ArrayList<>();
    private TraysAccess traysAccess = new TraysAccess();
    private TextAnimations textAnimations;

    @FXML
    public void initialize(){
        textAnimations = new TextAnimations(statusViewer);
        singleOrder = SingleOrdVariables.getSingleOrderObject();
        order = OrdVariables.getOrderObject();
        commentary.setText(singleOrder.getCommentary());
        attachValuesToColumns();
        fillTable();
    }

    private void attachValuesToColumns(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<TraysDTO, String>("traysName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<TraysDTO, String>("statusIcon"));
        changeStatusColumn.setCellFactory(ActionButtonTableCell.<TraysDTO>forTableColumn("Zmień status", (TraysDTO p) -> {
            int endedOrders = 0;
            for(Trays tray : trays){
                if(tray.getTray_id().equals(p.getTraysId())) {
                    try {
                        tray.setTray_status(tray.getTray_status().next());
                        traysAccess.changeTraysStatus(tray);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if(tray.getTray_status().equals(TraysStatus.FINISHED) || tray.getTray_status().equals(TraysStatus.ABANDONED))
                    endedOrders++;
            }
            fillTable();

            if(endedOrders == trays.size() && !singleOrder.getFinished())
                updateSingleOrder(true);
            else if(endedOrders != trays.size() && singleOrder.getFinished())
                updateSingleOrder(false);

            enableStatusViewerPulsing();
            return p;
        }));
    }

    private void fillTable(){
        getTraysFromDatabase();
        convertClasses();
        ObservableList<TraysDTO> data = FXCollections.observableArrayList(traysDTOS);
        traysTable.setItems(data);
    }

    private void getTraysFromDatabase(){
        trays = traysAccess.findAllTraysBySingleOrder(singleOrder);
    }

    private void convertClasses(){
        TraysDTO traysDTO;
        traysDTOS = new ArrayList<>();
        for(Trays tray : trays){
            traysDTO = new TraysDTO(tray.getTray_id(), tray.getTray_name(), tray.getTray_status().getValue(), new JFXButton("Zmień status"));
            traysDTOS.add(traysDTO);
        }
    }

    public void changeCommentClicked(){
        try{
            singleOrder.setCommentary(commentary.getText());
            singleOrdersAccess.updateSingleOrder(singleOrder);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    public void btnExitClicked(){

    }

    private void updateSingleOrder(boolean status){
        try{
            singleOrder.setFinished(status);
            singleOrdersAccess.updateSingleOrder(singleOrder);
            updateOrder();
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private boolean areAllSingleOrdersFinished(){
        int finishedSingleOrders = 0;
        try{
            List<SingleOrders> singleOrders = singleOrdersAccess.findByOrders(order);
            for(SingleOrders singleOrder : singleOrders)
                if(singleOrder.getFinished())
                    finishedSingleOrders++;

            return finishedSingleOrders == singleOrders.size();
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
        throw new RuntimeException();
    }

    private void updateOrder(){
        OrdersAccess ordersAccess = new OrdersAccess();
        setOrderFinished();

        if(areAllSingleOrdersFinished() && !order.getOrderFinished())
            setOrderFinished();
        else if(!areAllSingleOrdersFinished() && order.getOrderFinished())
            setOrderUnfinished();

        try{
            ordersAccess.updateOrder(order);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void setOrderFinished() {
        order.setSingle_orders_finished(order.getSingle_orders_finished() + 1);
        order.setSingle_orders_unfinished(order.getSingle_orders_unfinished() - 1);
        order.setOrderFinished(true);
    }

    private void setOrderUnfinished() {
        order.setSingle_orders_finished(order.getSingle_orders_finished() - 1);
        order.setSingle_orders_unfinished(order.getSingle_orders_unfinished() + 1);
        order.setOrderFinished(false);
    }

    private void enableStatusViewerPulsing(){
        if(GlobalVariables.getIsActionCompleted())
            statusViewer.setText(InfoAlerts.getStatusWhileRecordChanged());
        else
            statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotChanged());

        textAnimations.startLabelsPulsing();
    }
}
