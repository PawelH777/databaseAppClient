package pl.Vorpack.app.Controller.ClosedOrderController;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Constans.PathConstans;
import pl.Vorpack.app.DatabaseAccess.SingleOrdersAccess;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Dto.SingleOrdersDTO;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.SingleOrdVariables;
import pl.Vorpack.app.Service.CommonService;
import pl.Vorpack.app.Service.ServiceImpl.CommonServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SingleOrderController {
    private static final String TRAYS_WINDOW_PANE = "Okno palet";
    private static final String CLOSED = "Ukończono";
    private static final String IN_PROGRESS = "Nieukończono";

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView<SingleOrdersDTO> singleOrdersViewer;
    @FXML
    private TableColumn<SingleOrdersDTO, Long> IDColumn;
    @FXML
    private TableColumn<SingleOrdersDTO, String> dimensionColumn;
    @FXML
    private TableColumn<SingleOrdersDTO, BigDecimal> weightColumn;
    @FXML
    private TableColumn<SingleOrdersDTO, BigDecimal> lengthColumn;
    @FXML
    private TableColumn<SingleOrdersDTO, BigDecimal> quantityOnTrayColumn;
    @FXML
    private TableColumn<SingleOrdersDTO, BigDecimal> amountOfTraysColumn;
    @FXML
    private TableColumn<SingleOrdersDTO, BigDecimal> quantityOverallColumn;
    @FXML
    private TableColumn<SingleOrdersDTO, BigDecimal> metrsColumn;
    @FXML
    private TableColumn<SingleOrdersDTO, BigDecimal> materialsColumn;
    @FXML
    private TableColumn<SingleOrdersDTO, String> statusColumn;
    @FXML
    private Label statusLabel;
    @FXML
    private Label orderNumber;
    @FXML
    private JFXButton btnOpen;

    private List<SingleOrders> allSingleOrdersFromOrder = new ArrayList<>();
    private List<SingleOrdersDTO> valuesToTable = new ArrayList<>();
    private SingleOrders singleOrdersObject = new SingleOrders();
    private Orders orderObject = new Orders();
    private SingleOrdersAccess singleOrdersAccess = new SingleOrdersAccess();
    private TextAnimations textAnimations;
    private CommonService commonService = new CommonServiceImpl();

    @FXML
    public void initialize(){
        textAnimations = new TextAnimations(statusLabel);
        orderObject = OrdVariables.getOrderObject();
        orderNumber.setText(orderObject.getOrder_id() + "");
        setView();
        changeButtonsDisability(true);
        singleOrdersViewer.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                changeButtonsDisability(false);
            }
            else{
                changeButtonsDisability(true);
            }
        });
    }

    public void exitButtonClicked() {
        Stage thisStage = (Stage) anchorPane.getScene().getWindow();
        thisStage.close();
    }

    public void openButtonClicked() throws IOException {
        setSingleOrdersObject();
        SingleOrdVariables.setSingleOrderObject(singleOrdersObject);
        commonService.openScene(PathConstans.CLOSED_TRAYS_PANE_PATH, TRAYS_WINDOW_PANE, false);
        setReturnedInformation();
    }

    private void setView(){
        attachAttributesToColumns();
        setItemsInViewer();
    }

    private void attachAttributesToColumns(){
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("single_active_order_id"));
        dimensionColumn.setCellValueFactory(new PropertyValueFactory<>("dimension"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length_in_mm"));
        quantityOnTrayColumn.setCellValueFactory(new PropertyValueFactory<>("quantity_on_tray"));
        amountOfTraysColumn.setCellValueFactory(new PropertyValueFactory<>("amount_of_trays"));
        quantityOverallColumn.setCellValueFactory(new PropertyValueFactory<>("overall_quantity"));
        metrsColumn.setCellValueFactory(new PropertyValueFactory<>("metrs"));
        materialsColumn.setCellValueFactory(new PropertyValueFactory<>("materials"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("finished"));
    }

    private void setItemsInViewer() {
        getSingleOrders();
        convertClasses();
        ObservableList<SingleOrdersDTO> data = FXCollections.observableArrayList(valuesToTable);
        singleOrdersViewer.setItems(data);
    }

    private void getSingleOrders(){
        allSingleOrdersFromOrder.clear();
        try{
            allSingleOrdersFromOrder = singleOrdersAccess.findByOrders(orderObject);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts infoAlerts = new InfoAlerts();
            infoAlerts.generalAlert();
        }
    }

    private void convertClasses(){
        valuesToTable = new ArrayList<>();
        SingleOrdersDTO singleOrdersDTOObject;
        String fullDimensionValueAsString;
        String status;
        for(SingleOrders record : allSingleOrdersFromOrder){
            fullDimensionValueAsString = record.getDimension().getFirstDimension() + "x" + record.getDimension().getSecondDimension()
                    + "x" + record.getDimension().getThickness();
            if(record.getFinished())
                status = CLOSED;
            else
                status = IN_PROGRESS;
            singleOrdersDTOObject = new SingleOrdersDTO(record.getSingle_active_order_id(), fullDimensionValueAsString,
                    record.getDimension().getWeight(), record.getLength_in_mm(), record.getQuantity_on_tray(),
                    record.getAmount_of_trays(), record.getOverall_quantity(), record.getMetrs(), record.getMaterials(),
                    status, record.getCommentary());
            valuesToTable.add(singleOrdersDTOObject);
        }
    }

    private void changeButtonsDisability(boolean b) {
        btnOpen.setDisable(b);
    }

    private void setSingleOrdersObject(){
        int position = singleOrdersViewer.getSelectionModel().getSelectedIndex();
        singleOrdersObject = allSingleOrdersFromOrder.get(position);
    }

    private void setReturnedInformation() {
        getSingleOrders();

        if(GlobalVariables.getIsActionCompleted())
            statusLabel.setText(InfoAlerts.getStatusWhileRecordAdded());
        else
            statusLabel.setText(InfoAlerts.getStatusWhileRecordIsNotAdded());

        textAnimations.startLabelsPulsing();
    }
}
