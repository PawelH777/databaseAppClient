package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.DatabaseAccess.SingleOrdersAccess;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.OrdVariables;
import pl.Vorpack.app.GlobalVariables.SingleOrdVariables;
import pl.Vorpack.app.TableValues.SingleOrdersDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShowSingleOrdersController {

    private static final String CRUD_SINGLE_ORDER_PANE = "/fxml/orders/AddOrChangeSingleOrders.fxml";
    private static final String TRAYS_PANE = "/fxml/orders/ShowTraysPane.fxml";

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
    private JFXTextArea commentary;
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
    @FXML
    private JFXButton btnChangeComment;


    private List<SingleOrders> allSingleOrdersFromOrder = new ArrayList<>();
    private List<SingleOrdersDTO> valuesToTable = new ArrayList<>();

    private SingleOrders singleOrdersObject = new SingleOrders();
    private Orders orderObject = new Orders();
    private SingleOrdersAccess singleOrdersAccess = new SingleOrdersAccess();
    private TextAnimations textAnimations;

    @FXML
    public void initialize(){
        textAnimations = new TextAnimations(statusLabel);
        orderObject = OrdVariables.getOrderObject();
        setView();
        changeButtonsDisability(true);

        if("Użytkownik".equals(GlobalVariables.getAccess())){
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

    private void changeButtonsDisability(boolean b) {
        btnModifyRecord.setDisable(b);
        btnDelete.setDisable(b);
        btnOpen.setDisable(b);
        btnChangeComment.setDisable(b);
    }

    private void setView(){
        attachAttributesToColumns();
        setItemsInViewer();
    }

    private void convertClasses(){
        valuesToTable = new ArrayList<>();
        SingleOrdersDTO singleOrdersDTOObject;
        String fullDimensionValueAsString;
        String finishedStatusAsString;
        for(SingleOrders record : allSingleOrdersFromOrder){
            fullDimensionValueAsString = record.getDimension().getFirstDimension() + "x" + record.getDimension().getSecondDimension()
                    + "x" + record.getDimension().getThickness();

            if(record.getFinished())
                finishedStatusAsString = "Ukończono";
            else
                finishedStatusAsString = "Nieukończono";

            singleOrdersDTOObject = new SingleOrdersDTO(fullDimensionValueAsString,
                    record.getDimension().getWeight(), record.getLength_in_mm(), record.getQuantity_on_tray(),
                    record.getAmount_of_trays(), record.getOverall_quantity(), record.getMetrs(), record.getMaterials(),
                    finishedStatusAsString, record.getCommentary());
            valuesToTable.add(singleOrdersDTOObject);
        }
    }

    private void attachAttributesToColumns(){
        IDColumn.setCellValueFactory(new PropertyValueFactory<SingleOrdersDTO, Long>("single_active_order_id"));
        dimensionColumn.setCellValueFactory(new PropertyValueFactory<SingleOrdersDTO, String>("dimension"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<SingleOrdersDTO, BigDecimal>("weight"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<SingleOrdersDTO, BigDecimal>("length_in_mm"));
        quantityOnTrayColumn.setCellValueFactory(new PropertyValueFactory<SingleOrdersDTO, BigDecimal>("quantity_on_tray"));
        amountOfTraysColumn.setCellValueFactory(new PropertyValueFactory<SingleOrdersDTO, BigDecimal>("amount_of_trays"));
        quantityOverallColumn.setCellValueFactory(new PropertyValueFactory<SingleOrdersDTO, BigDecimal>("overall_quantity"));
        metrsColumn.setCellValueFactory(new PropertyValueFactory<SingleOrdersDTO, BigDecimal>("metrs"));
        materialsColumn.setCellValueFactory(new PropertyValueFactory<SingleOrdersDTO, BigDecimal>("materials"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<SingleOrdersDTO, String>("finished"));
    }

    private void getSingleOrdersRecords(){
        allSingleOrdersFromOrder.clear();
        try{
            allSingleOrdersFromOrder = singleOrdersAccess.findByOrders(orderObject);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void summaryOrdersMaterials() {
        BigDecimal singleOrdersMaterialsSummary = BigDecimal.ZERO;

        for(SingleOrders singleOrdersObject : allSingleOrdersFromOrder)
            if(!singleOrdersObject.getFinished())
                singleOrdersMaterialsSummary = singleOrdersMaterialsSummary.add(singleOrdersObject.getMaterials());

        materialsNumber.setText(String.valueOf(singleOrdersMaterialsSummary));
    }

    public void addButtonClicked(){
        GlobalVariables.setIsCreate(true);
        openNewWindow(CRUD_SINGLE_ORDER_PANE);
    }

    private void openNewWindow(String pathToFile) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(pathToFile));
        AnchorPane anchorPane = null;
        try {
            anchorPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert(anchorPane != null);
        Scene scene = new Scene(anchorPane);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();
        setItemsInViewer();

        if(GlobalVariables.getIsActionCompleted())
            statusLabel.setText(InfoAlerts.getStatusWhileRecordAdded());
        else
            statusLabel.setText(InfoAlerts.getStatusWhileRecordIsNotAdded());

        textAnimations.startLabelsPulsing();
    }

    public void modifyButtonClicked(){
        OrdVariables.setOrderObject(orderObject);
        setSingleOrdersObject();
        SingleOrdVariables.setSingleOrderObject(singleOrdersObject);
        GlobalVariables.setIsCreate(false);
        openNewWindow(CRUD_SINGLE_ORDER_PANE);
    }

    private void setItemsInViewer() {
        getSingleOrdersRecords();
        summaryOrdersMaterials();
        convertClasses();
        ObservableList<SingleOrdersDTO> data = FXCollections.observableArrayList(valuesToTable);
        singleOrdersViewer.setItems(data);
    }

    private void setSingleOrdersObject(){
        int position = singleOrdersViewer.getSelectionModel().getSelectedIndex();
        singleOrdersObject = allSingleOrdersFromOrder.get(position);
    }

    public void deleteButtonClicked(){
        try{
            singleOrdersAccess.deleteSingleOrder(singleOrdersObject);
            setItemsInViewer();
            GlobalVariables.setIsActionCompleted(true);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    public void changeCommentClicked(){
        try{
            singleOrdersObject.setCommentary(commentary.getText());
            singleOrdersAccess.updateSingleOrder(singleOrdersObject);
            setItemsInViewer();
            GlobalVariables.setIsActionCompleted(true);

        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    public void openButtonClicked(){
        setSingleOrdersObject();
        SingleOrdVariables.setSingleOrderObject(singleOrdersObject);
        openNewWindow(TRAYS_PANE);
    }

    public void exitButtonClicked(){
        Stage thisStage = (Stage) anchorPane.getScene().getWindow();
        thisStage.close();
    }
}
