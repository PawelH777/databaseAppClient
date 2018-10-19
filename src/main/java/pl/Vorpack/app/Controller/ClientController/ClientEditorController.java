package pl.Vorpack.app.Controller.ClientController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Domain.Clients;
import pl.Vorpack.app.GlobalVariables.ClientVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Properties.MainPaneProperty;
import pl.Vorpack.app.Service.ClientService;
import pl.Vorpack.app.Service.ServiceImpl.ClientServiceImpl;

/**
 * Created by Paweł on 2018-02-21.
 */
public class ClientEditorController {

    @FXML
    public VBox vBox = new VBox();
    @FXML
    public JFXTextField firmName = new JFXTextField() {
        @Override
        public void paste() {
        }
    };
    @FXML
    public JFXButton btnProceed = new JFXButton();
    @FXML
    public Label statusLabel = new Label();

    private Clients client = new Clients();
    private MainPaneProperty cliProperty = new MainPaneProperty();
    private ClientService clientService = new ClientServiceImpl();

    @FXML
    public void initialize() {
        btnProceed.disableProperty().bindBidirectional(cliProperty.disableBtnProperty());
        if (ClientVariables.getObject() != null)
            setUpdateView();
        firmName.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.isEmpty())
                cliProperty.setDisableBtn(false);
            else
                cliProperty.setDisableBtn(true);
        });
    }

    public void onBtnProceedClicked(MouseEvent mouseEvent) {
        boolean endGate = false;
        client.setFirmName(firmName.textProperty().get());
        try {
            endGate = createOrUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            InfoAlerts infoAlerts = new InfoAlerts();
            infoAlerts.generalAlert();
        }
        if (endGate) {
            Stage thisStage = (Stage) vBox.getScene().getWindow();
            GlobalVariables.setIsActionCompleted(true);
            thisStage.close();
        } else
            statusLabel.setText("Firma z wpisanymi danymi już istnieje");
    }

    public void setClientService(ClientService clientService){
        this.clientService = clientService;
    }

    private void setUpdateView() {
        client = ClientVariables.getObject();
        firmName.textProperty().setValue(client.getFirmName());
        btnProceed.setText("Zmień");
    }

    private boolean createOrUpdate() {
        boolean endGate;
        if (ClientVariables.getObject() == null) {
            if (clientService.findByFirmName(firmName.getText()) == null) {
                clientService.create(firmName.getText());
                endGate = true;
            } else
                endGate = false;
        } else {
            clientService.update(client);
            endGate = true;
        }
        return endGate;
    }
}
