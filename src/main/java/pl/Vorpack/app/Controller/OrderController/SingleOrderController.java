package pl.Vorpack.app.Controller.OrderController;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Constans.Path;
import pl.Vorpack.app.DatabaseAccess.SingleOrdersAccess;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.SingleOrdVariables;
import pl.Vorpack.app.Dto.SingleOrdersDTO;
import pl.Vorpack.app.Service.CommonService;
import pl.Vorpack.app.Service.ServiceImpl.CommonServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static pl.Vorpack.app.Constans.User.USER;

public class SingleOrderController {
    private static final String ADD_WINDOW_TITLE = "Okno dodawania zamówień jednostkowych";
    private static final String MODIFY_WINDOW_TITLE = "Okno zmiany zamówienia jednostkowego";
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
    private Label orderNumber;
    @FXML
    private Label statusLabel;
    @FXML
    private Label materialsNumber;
    @FXML
    private JFXButton btnAdd;
    @FXML
    private JFXButton btnModifyRecord;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXButton btnOpen;

    private List<SingleOrders> singleOrders = new ArrayList<>();
    private List<SingleOrdersDTO> valuesToTable = new ArrayList<>();
    private SingleOrders singleOrdersObject = new SingleOrders();
    private Orders orderObject = new Orders();
    private SingleOrdersAccess singleOrdersService = new SingleOrdersAccess();
    private TextAnimations textAnimations;
    private CommonService commonService = new CommonServiceImpl();
    private InfoAlerts infoAlerts = new InfoAlerts();

    @FXML
    public void initialize(){
        textAnimations = new TextAnimations(statusLabel);
        orderObject = OrdVariables.getOrderObject();
        orderNumber.setText(orderObject.getOrder_id() + "");
        setView();
        changeButtonsDisability(true);

        if(USER.equals(GlobalVariables.getAccess())){
            btnAdd.setVisible(false);
            btnModifyRecord.setVisible(false);
            btnDelete.setVisible(false);
        }

        singleOrdersViewer.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                changeButtonsDisability(false);
            }
            else{
                changeButtonsDisability(true);
            }
        });
    }

    public void addButtonClicked() throws IOException {
        GlobalVariables.setIsCreate(true);
        commonService.openScene(Path.SINGLE_ORDER_EDITOR_PANE_PATH, ADD_WINDOW_TITLE, false);
        setReturnedInformation();
    }

    public void modifyButtonClicked() throws IOException {
        OrdVariables.setOrderObject(orderObject);
        setSingleOrdersObject();
        SingleOrdVariables.setSingleOrderObject(singleOrdersObject);
        GlobalVariables.setIsCreate(false);
        commonService.openScene(Path.SINGLE_ORDER_EDITOR_PANE_PATH, MODIFY_WINDOW_TITLE, false);
        setReturnedInformation();
    }

    public void deleteButtonClicked(){
        setSingleOrdersObject();
        try{
            singleOrdersService.deleteSingleOrder(singleOrdersObject);
            setItemsInViewer();
            GlobalVariables.setIsActionCompleted(true);
        }
        catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }
    }

    public void openButtonClicked() throws IOException {
        setSingleOrdersObject();
        SingleOrdVariables.setSingleOrderObject(singleOrdersObject);
        commonService.openScene(Path.TRAYS_PANE_PATH, TRAYS_WINDOW_PANE, false);
        setReturnedInformation();
    }

    public void exitButtonClicked(){
        Stage thisStage = (Stage) anchorPane.getScene().getWindow();
        thisStage.close();
    }

    private void changeButtonsDisability(boolean b) {
        btnModifyRecord.setDisable(b);
        btnDelete.setDisable(b);
        btnOpen.setDisable(b);
    }

    private void setView(){
        attachAttributesToColumns();
        setItemsInViewer();
    }

    private void convertClasses(){
        valuesToTable = new ArrayList<>();
        SingleOrdersDTO singleOrdersDTOObject;
        String fullDimensionValueAsString;
        String status;
        for(SingleOrders record : singleOrders){
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

    private void getSingleOrders(){
        singleOrders.clear();
        try{
            singleOrders = singleOrdersService.findByOrders(orderObject);
        }
        catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }
    }

    private void summaryOrdersMaterials() {
        BigDecimal singleOrdersMaterialsSummary = BigDecimal.ZERO;
        for(SingleOrders singleOrdersObject : singleOrders)
            if(!singleOrdersObject.getFinished())
                singleOrdersMaterialsSummary = singleOrdersMaterialsSummary.add(singleOrdersObject.getMaterials());
        materialsNumber.setText(String.valueOf(singleOrdersMaterialsSummary));
    }

    private void setItemsInViewer() {
        getSingleOrders();
        summaryOrdersMaterials();
        convertClasses();
        ObservableList<SingleOrdersDTO> data = FXCollections.observableArrayList(valuesToTable);
        singleOrdersViewer.setItems(data);
    }

    private void setReturnedInformation() {
        if(GlobalVariables.getIsActionCompleted()){
            statusLabel.setText(InfoAlerts.getStatusWhileRecordAdded());
            setItemsInViewer();
        }
        else
            statusLabel.setText(InfoAlerts.getStatusWhileRecordIsNotAdded());
        textAnimations.startLabelsPulsing();
    }

    private void setSingleOrdersObject(){
        int position = singleOrdersViewer.getSelectionModel().getSelectedIndex();
        singleOrdersObject = singleOrders.get(position);
    }
}
