package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.Vorpack.app.DatabaseAccess.ClientAccess;
import pl.Vorpack.app.DatabaseAccess.OrdersAccess;
import pl.Vorpack.app.Properties.MainPaneProperty;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Domain.Client;
import pl.Vorpack.app.GlobalVariables.CliVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;

import java.io.IOException;

/**
 * Created by Paweł on 2018-02-21.
 */
public class ShowClientsController {

    private static final String ADD_CLIENTS_PANE_FXML = "/fxml/clients/AddClientsPane.fxml";
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label StatusViewer;
    @FXML
    private JFXComboBox columnsCmbBox;
    @FXML
    private JFXButton btnModify;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private TableView<Client> dimTableView;
    @FXML
    private TableColumn<Client, Integer> idColumn;
    @FXML
    private TableColumn<Client, String> firmName;

    private MainPaneProperty clientProperty = new MainPaneProperty();
    private SortedList<Client> sortedData;
    private FilteredList<Client> filteredList;
    private TextAnimations textAnimations;
    private ClientAccess clientAccess = new ClientAccess();

    @FXML
    public void initialize(){
        txtSearch.disableProperty().setValue(true);
        StatusViewer.setOpacity(0);
        textAnimations = new TextAnimations(StatusViewer);
        columnsCmbBox.getItems().addAll(
                "Wszystkie",
                "Identyfikator",
                "Nazwa firmy"
        );
        attachParametrsToTable();

        btnModify.disableProperty().bindBidirectional(clientProperty.disableModifyBtnProperty());
        btnDelete.disableProperty().bindBidirectional(clientProperty.disableDeleteBtnProperty());

        dimTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelect, newSelect) -> {
            if(newSelect != null){
                clientProperty.setDisableModifyBtn(false);
                clientProperty.setDisableDeleteBtn(false);
            }
        });

        getRecords();
        sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(dimTableView.comparatorProperty());
        dimTableView.setItems(sortedData);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            getRecordsWithActualConfigure();
        });

        columnsCmbBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            txtSearch.disableProperty().setValue(false);
            getRecordsWithActualConfigure();
        });

    }

    private void attachParametrsToTable() {
        idColumn.setCellValueFactory( new PropertyValueFactory<Client,Integer>("client_id"));
        firmName.setCellValueFactory( new PropertyValueFactory<Client, String>("firmName"));
    }

    private void getRecords() {
        try{
            ObservableList<Client> data = FXCollections.observableArrayList(CliVariables.getClientsFromDatabase());
            filteredList = new FilteredList<>(data, p -> true);
        }catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void getRecordsWithActualConfigure() {
        if("Wszystkie".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(record -> {
                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;
                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(record.getClient_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(record.getFirmName()).toLowerCase().contains(lowerCaseValue))
                    return true;
                return false;
            });
        }
        else if("Identyfikator".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(record -> {
                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;
                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(record.getClient_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
                return false;
            });
        }
        else if("Nazwa firmy".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(record -> {
                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;
                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(record.getFirmName()).toLowerCase().contains(lowerCaseValue))
                    return true;
                return false;
            });
        }
        else{
            txtSearch.disableProperty().setValue(true);
        }
        sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(dimTableView.comparatorProperty());
        dimTableView.setItems(sortedData);
    }

    public void btnDeleteClicked(){
        try{
            Client clientObject = dimTableView.getSelectionModel().getSelectedItem();
            Boolean isDelete = InfoAlerts.deleteRecord("Posiadasz powiązane z obiektem niezakończone oraz zakończone zamówienia. By usunąć" +
                    " klienta, program usunie również powiązane z nim obiekty. Jeśli program ma kontynuować pracę, naciśnij " +
                    "OK.");

            if(isDelete) {
                clientAccess.deleteClient(clientObject);
                StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsDeleted());
            }
            else
                StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotDeleted());
        }
        catch(Exception e){
            e.printStackTrace();
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotDeleted());
        }
        getRecords();
        getRecordsWithActualConfigure();
        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
        textAnimations.startLabelsPulsing();
    }

    public void onBtnAddClicked() throws IOException {
        GlobalVariables.setIsActionCompleted(false);
        CliVariables.setObject(null);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_CLIENTS_PANE_FXML));
        VBox vBox = fxmlLoader.load();
        Scene scene = new Scene(vBox);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();
        getRecords();
        getRecordsWithActualConfigure();

        if(GlobalVariables.getIsActionCompleted())
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordAdded());
        else
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotAdded());

        textAnimations.startLabelsPulsing();
    }

    public void btnModifyClicked() throws IOException {

        GlobalVariables.setIsActionCompleted(false);

        CliVariables.setObject(dimTableView.getSelectionModel().getSelectedItem());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_CLIENTS_PANE_FXML));
        VBox vBox = fxmlLoader.load();
        Scene scene = new Scene(vBox);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();
        getRecords();
        getRecordsWithActualConfigure();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);

        if(GlobalVariables.getIsActionCompleted())
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordChanged());
        else
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotChanged());

        textAnimations.startLabelsPulsing();
    }

    public void btnRefreshClicked() throws IOException{
        getRecords();
        getRecordsWithActualConfigure();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
    }

    public void dimTableClicked(){

    }

}
