package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import pl.Vorpack.app.DatabaseAccess.TraysAccess;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Domain.Trays;
import pl.Vorpack.app.GlobalVariables.SingleOrdVariables;
import pl.Vorpack.app.TableValues.IconsBuilder;
import pl.Vorpack.app.TableValues.TraysTableValue;

import java.util.ArrayList;
import java.util.List;

public class ShowTraysController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView<TraysTableValue> traysTable;
    @FXML
    private TableColumn<TraysTableValue, Long> IDColumn;
    @FXML
    private TableColumn<TraysTableValue, String> nameColumn;
    @FXML
    private TableColumn<TraysTableValue,FontAwesomeIconView> statusColumn;
    @FXML
    private TableColumn<TraysTableValue, JFXButton> changeStatusColumn;
    @FXML
    private Label statusViewer;
    @FXML
    private Label messageViewer;

    private SingleOrders singleOrder;
    private List<Trays> trays = new ArrayList<>();
    private List<TraysTableValue> traysTableValues = new ArrayList<>();

    @FXML
    public void initialize(){
        singleOrder = SingleOrdVariables.getSingleOrderObject();
        attachValuesToColumns();
        fillTable();
    }

    private void attachValuesToColumns(){
        IDColumn.setCellValueFactory(new PropertyValueFactory<TraysTableValue, Long>("traysId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<TraysTableValue, String>("traysName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<TraysTableValue, FontAwesomeIconView>("statusIcon"));
        changeStatusColumn.setCellValueFactory(new PropertyValueFactory<TraysTableValue, JFXButton>("changeStatusButton"));
    }

    private void fillTable(){
        getTraysFromDatabase();
        convertClasses();
        ObservableList<TraysTableValue> data = FXCollections.observableArrayList(traysTableValues);
        traysTable.setItems(data);
    }

    private void getTraysFromDatabase(){
        TraysAccess traysAccess = new TraysAccess();
        trays = traysAccess.findAllTraysBySingleOrder(singleOrder);
    }

    private void convertClasses(){
        TraysTableValue traysTableValue;
        FontAwesomeIconView fontAwesomeIconView;
        JFXButton changeStatus = new JFXButton();
        changeStatus.setText("Zmień status");

        for(Trays tray : trays){
            fontAwesomeIconView = IconsBuilder.build(tray.getTray_status());
            traysTableValue = new TraysTableValue(tray.getTray_id(), tray.getTray_name(), fontAwesomeIconView, changeStatus);
            traysTableValues.add(traysTableValue);
        }
    }

    public void btnExitClicked(){

    }
}
