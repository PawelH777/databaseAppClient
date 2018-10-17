package pl.Vorpack.app.Controller.ClientController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Constans.ActionConstans;
import pl.Vorpack.app.Constans.Path;
import pl.Vorpack.app.Domain.Clients;
import pl.Vorpack.app.GlobalVariables.CliVariables;
import pl.Vorpack.app.Service.ClientService;
import pl.Vorpack.app.Service.CommonService;
import pl.Vorpack.app.Service.ServiceImpl.ClientServiceImpl;
import pl.Vorpack.app.Service.ServiceImpl.CommonServiceImpl;

import java.io.IOException;

import static pl.Vorpack.app.Constans.ClientColumn.*;

/**
 * Created by Paweł on 2018-02-21.
 */
public class ClientController {
    private static final String CLIENTS_EDITOR = "Edytor klienta";
    @FXML
    public Label statusViewer = new Label();
    @FXML
    public JFXComboBox<String> filterComboBox = new JFXComboBox<>();
    @FXML
    public JFXButton btnModify = new JFXButton();
    @FXML
    public JFXButton btnDelete = new JFXButton();
    @FXML
    public JFXTextField txtSearch = new JFXTextField();
    @FXML
    public TableView<Clients> clientsViewer = new TableView<>();
    @FXML
    public TableColumn<Clients, Integer> idColumn = new TableColumn<>();
    @FXML
    public TableColumn<Clients, String> firmName = new TableColumn<>();

    private SortedList<Clients> sortedData;
    private FilteredList<Clients> filteredClients;
    private TextAnimations textAnimations;
    private StringBuilder actionStatus = new StringBuilder();

    private ClientService clientService;
    private CommonService commonService;
    private InfoAlerts infoAlerts;

    public ClientController(){
        setClientService(new ClientServiceImpl());
        setCommonService(new CommonServiceImpl());
        setInfoAlerts(new InfoAlerts());
    }

    @FXML
    public void initialize(){
        filterComboBox.getItems().addAll(
                ALL,
                ID,
                FIRM_NAME
        );
        clientService.setJFXComboBox(filterComboBox);
        setAnimations();
        assignColumns();
        txtSearch.setDisable(true);
        setButtonsDisableValue(true);
        clientsViewer.setItems(prepareClients());
        clientsViewer.getSelectionModel().selectedItemProperty().addListener((obs, oldSelect, newSelect) -> {
            if(newSelect != null){
                setButtonsDisableValue(false);
            }
        });
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            txtSearch.setDisable(false);
            filter(txtSearch.textProperty().getValue());
        });
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filter(txtSearch.textProperty().getValue());
        });
    }

    public void onBtnDeleteClicked() {
        try {
            Clients clients = clientsViewer.getSelectionModel().getSelectedItem();
            Boolean isDelete = infoAlerts.deleteRecord("Posiadasz powiązane z obiektem niezakończone oraz zakończone zamówienia. By usunąć" +
                    " klienta, program usunie również powiązane z nim obiekty. Jeśli program ma kontynuować pracę, naciśnij " +
                    "OK.");
            if (isDelete) {
                clientService.delete(clients);
                statusViewer.setText(InfoAlerts.getStatusWhileRecordIsDeleted());
            } else
                statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotDeleted());
        } catch (Exception e) {
            e.printStackTrace();
            statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotDeleted());
        }
        getClients();
        setButtonsDisableValue(true);
        textAnimations.startLabelsPulsing();
    }

    public void onBtnAddClicked() throws IOException {
        setUnfinishedStatus();
        ClientEditorController clientEditorController = new ClientEditorController(actionStatus);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Path.CLIENTS_EDITOR_PANE_PATH));
        fxmlLoader.setController(clientEditorController);
        commonService.openScene(CLIENTS_EDITOR, false, fxmlLoader);
        setInfoOnReturn();
    }

    public void onBtnModifyClicked() throws IOException {
        setUnfinishedStatus();
        Clients client = clientsViewer.getSelectionModel().getSelectedItem();
        ClientEditorController clientEditorController = new ClientEditorController(actionStatus, client);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Path.CLIENTS_EDITOR_PANE_PATH));
        fxmlLoader.setController(clientEditorController);
        commonService.openScene(CLIENTS_EDITOR, false, fxmlLoader);
        setInfoOnReturn();
    }

    public void onBtnRefreshClicked(){
        getClients();
    }

    public void setClientService(ClientService clientService){
        this.clientService = clientService;
    }

    public void setInfoAlerts(InfoAlerts infoAlerts){
        this.infoAlerts = infoAlerts;
    }

    public void setCommonService(CommonService commonService){
        this.commonService = commonService;
    }

    private void setUnfinishedStatus() {
        actionStatus.setLength(0);
        actionStatus.append(ActionConstans.IS_UNFINISHED);
    }

    private void filter(String searchedText){
        clientService.filter(searchedText, filteredClients);
        sortedData = new SortedList<>(filteredClients);
        clientsViewer.setItems(sortedData);
    }

    private SortedList<Clients> prepareClients() {
        filteredClients = clientService.getPreparedData();
        sortedData = new SortedList<>(filteredClients);
        sortedData.comparatorProperty().bind(clientsViewer.comparatorProperty());
        return sortedData;
    }

    private void setButtonsDisableValue(boolean b) {
        btnModify.setDisable(b);
        btnDelete.setDisable(b);
    }

    private void setAnimations() {
        statusViewer.setOpacity(0);
        textAnimations = new TextAnimations(statusViewer);
    }

    private void assignColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("client_id"));
        firmName.setCellValueFactory(new PropertyValueFactory<>("firmName"));
    }

    private void setInfoOnReturn(){
        getClients();
        setButtonsDisableValue(true);
        if(actionStatus.toString().equals(ActionConstans.IS_FINISHED))
            statusViewer.setText(InfoAlerts.getStatusWhileRecordAdded());
        else
            statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotAdded());
        textAnimations.startLabelsPulsing();
    }

    private void getClients() {
        filteredClients = clientService.findAll();
        CliVariables.setClientsFromDatabase(filteredClients);
        filter(txtSearch.textProperty().getValue());
    }
}
