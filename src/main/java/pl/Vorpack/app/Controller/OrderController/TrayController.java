package pl.Vorpack.app.Controller.OrderController;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Data.TraysStatus;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Domain.Trays;
import pl.Vorpack.app.Dto.ActionButtonTableCell;
import pl.Vorpack.app.Dto.TraysDTO;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.SingleOrdVariables;
import pl.Vorpack.app.Service.ServiceImpl.SingleOrdersServiceImpl;
import pl.Vorpack.app.Service.ServiceImpl.TrayServiceImpl;
import pl.Vorpack.app.Service.SingleOrdersService;
import pl.Vorpack.app.Service.TrayService;

import java.util.ArrayList;
import java.util.List;

public class TrayController {

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

    private SingleOrders singleOrder;
    private Orders order;
    private List<Trays> trays = new ArrayList<>();
    private List<TraysDTO> traysDTOS = new ArrayList<>();
    private TextAnimations textAnimations;
    private TrayService trayService = new TrayServiceImpl();
    private SingleOrdersService singleOrdersService = new SingleOrdersServiceImpl();

    @FXML
    public void initialize(){
        textAnimations = new TextAnimations(statusViewer);
        singleOrder = SingleOrdVariables.getSingleOrderObject();
        order = OrdVariables.getOrderObject();
        commentary.setText(singleOrder.getCommentary());
        attachValuesToColumns();
        fillTable();
    }

    public void changeCommentClicked(){
        try{
            singleOrder.setCommentary(commentary.getText());
            singleOrdersService.update(singleOrder);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    public void btnExitClicked(){

    }

    private void attachValuesToColumns(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("traysName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusIcon"));
        changeStatusColumn.setCellFactory(ActionButtonTableCell.<TraysDTO>forTableColumn("Zmień status", (TraysDTO p) -> {
            int endedOrders = 0;
            for(Trays tray : trays){
                if(tray.getTray_id().equals(p.getTraysId())) {
                    try {
                        tray.setTray_status(tray.getTray_status().next());
                        trayService.updateStatus(tray);
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
        trays = trayService.findBySingleOrder(singleOrder);
    }

    private void convertClasses(){
        TraysDTO traysDTO;
        traysDTOS = new ArrayList<>();
        for(Trays tray : trays){
            traysDTO = new TraysDTO(tray.getTray_id(), tray.getTray_name(), tray.getTray_status().getValue(), new JFXButton("Zmień status"));
            traysDTOS.add(traysDTO);
        }
    }

    private void updateSingleOrder(boolean status){
        try{
            singleOrder.setFinished(status);
            singleOrdersService.update(singleOrder, order);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void enableStatusViewerPulsing(){
        if(GlobalVariables.getIsActionCompleted())
            statusViewer.setText(InfoAlerts.getStatusWhileRecordChanged());
        else
            statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotChanged());
        textAnimations.startLabelsPulsing();
    }
}
