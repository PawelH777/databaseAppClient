package pl.Vorpack.app.Controller.ClosedOrderController;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Domain.Trays;
import pl.Vorpack.app.Dto.TraysDTO;
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
    private TextArea commentary;

    private SingleOrdersService singleOrdersAccess = new SingleOrdersServiceImpl();
    private TrayService trayService = new TrayServiceImpl();
    private SingleOrders singleOrder;
    private List<Trays> trays = new ArrayList<>();
    private List<TraysDTO> traysDTOS = new ArrayList<>();

    @FXML
    public void initialize(){
        singleOrder = SingleOrdVariables.getSingleOrderObject();
        commentary.setText(singleOrder.getCommentary());
        attachValuesToColumns();
        fillTable();
    }

    public void exitButtonClicked(MouseEvent mouseEvent) {
    }

    public void changeCommentButtonClicked(MouseEvent mouseEvent) {
        try{
            singleOrder.setCommentary(commentary.getText());
            singleOrdersAccess.update(singleOrder);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts infoAlerts = new InfoAlerts();
            infoAlerts.generalAlert();
        }
    }

    private void attachValuesToColumns(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("traysName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusIcon"));
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
}
