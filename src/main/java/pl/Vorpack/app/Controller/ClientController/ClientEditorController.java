package pl.Vorpack.app.Controller.ClientController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Constans.ActionConstans;
import pl.Vorpack.app.Domain.Clients;
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

    private StringBuilder actionStatus;
    private boolean clientExist = false;
    private Clients client = new Clients();

    private MainPaneProperty cliProperty = new MainPaneProperty();
    private ClientService clientService = new ClientServiceImpl();

    public ClientEditorController(StringBuilder ActionStatus) {
        this.actionStatus = ActionStatus;
        setClientService(new ClientServiceImpl());
    }

    public ClientEditorController(StringBuilder ActionStatus, Clients client) {
        this.actionStatus = ActionStatus;
        this.client = client;
        setClientService(new ClientServiceImpl());
        if(this.client != null)
            clientExist = true;
    }

    @FXML
    public void initialize() {
        btnProceed.disableProperty().bindBidirectional(cliProperty.disableBtnProperty());
        if (client != null)
            setUpdateView();
        firmName.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.isEmpty())
                cliProperty.setDisableBtn(false);
            else
                cliProperty.setDisableBtn(true);
        });
        btnProceed.setOnAction(new EventHandler<ActionEvent>() {
                                   @Override
                                   public void handle(ActionEvent event) {
                                       click();
                                   }
                               }

        );
    }

    public void setClientService(ClientService clientService){
        this.clientService = clientService;
    }

    private void click() {
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
            actionStatus.setLength(0);
            actionStatus.append(ActionConstans.IS_FINISHED);
            thisStage.close();
        } else
            statusLabel.setText("Firma z wpisanymi danymi już istnieje");
    }

    private void setUpdateView() {
        firmName.textProperty().setValue(client.getFirmName());
        btnProceed.setText("Zmień");
    }

    private boolean createOrUpdate() {
        boolean endGate;
        if (!clientExist) {
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
