package pl.Vorpack.app.Controller.ClientController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Constans.Path;
import pl.Vorpack.app.Domain.Clients;
import pl.Vorpack.app.GlobalVariables.CliVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
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
    private Label statusViewer;
    @FXML
    private JFXComboBox<String> filterComboBox;
    @FXML
    private JFXButton btnModify;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private TableView<Clients> clientsViewer;
    @FXML
    private TableColumn<Clients, Integer> idColumn;
    @FXML
    private TableColumn<Clients, String> firmName;

    private SortedList<Clients> sortedData;
    private FilteredList<Clients> filteredClients;
    private TextAnimations textAnimations;
    private ClientService clientService;
    private CommonService commonService;

    @FXML
    public void initialize(){
        filterComboBox.getItems().addAll(
                ALL,
                ID,
                FIRM_NAME
        );
        initServices();
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

    public void btnDeleteClicked() {
        try {
            Clients clients = clientsViewer.getSelectionModel().getSelectedItem();
            Boolean isDelete = InfoAlerts.deleteRecord("Posiadasz powiązane z obiektem niezakończone oraz zakończone zamówienia. By usunąć" +
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
        GlobalVariables.setIsActionCompleted(false);
        CliVariables.setObject(null);
        commonService.openScene(Path.CLIENTS_EDITOR_PANE_PATH, CLIENTS_EDITOR, false);
        setInfoOnReturn();
    }

    public void btnModifyClicked() throws IOException {
        GlobalVariables.setIsActionCompleted(false);
        CliVariables.setObject(clientsViewer.getSelectionModel().getSelectedItem());
        commonService.openScene(Path.CLIENTS_EDITOR_PANE_PATH, CLIENTS_EDITOR, false);
        setInfoOnReturn();
    }

    public void btnRefreshClicked(){
        getClients();
    }

    private void initServices(){
        clientService = new ClientServiceImpl(filterComboBox);
        commonService = new CommonServiceImpl();
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
        if(GlobalVariables.getIsActionCompleted())
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
