package pl.Vorpack.app.Controller.ClientController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.Vorpack.app.Domain.Clients;
import pl.Vorpack.app.Properties.MainPaneProperty;
import pl.Vorpack.app.GlobalVariables.CliVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Service.ClientService;
import pl.Vorpack.app.Service.ServiceImpl.ClientServiceImpl;

import java.text.ParseException;

/**
 * Created by Paweł on 2018-02-21.
 */
public class ClientEditorController {

    @FXML
    private VBox vBox;
    @FXML
    private JFXTextField firmName = new JFXTextField(){
        @Override
        public void paste(){}
    };
    @FXML
    private JFXButton btnProceed;
    @FXML
    private Label statusLabel;

    private MainPaneProperty cliProperty = new MainPaneProperty();
    private Clients client = new Clients();
    private ClientService clientService = new ClientServiceImpl();

    @FXML
    public void initialize(){
        firmName.textProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue.isEmpty())
                cliProperty.setDisableBtn(false);
            else
                cliProperty.setDisableBtn(true);
        });
        btnProceed.disableProperty().bindBidirectional(cliProperty.disableBtnProperty());
        if(CliVariables.getObject() != null)
            setUpdateView();
    }

    public void btnAddClicked(MouseEvent mouseEvent) throws ParseException {
        boolean endGate = false;
        client.setFirmName(firmName.getText());
        try{
            endGate = createOrUpdate();
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
        if(endGate){
            Stage thisStage = (Stage) vBox.getScene().getWindow();
            GlobalVariables.setIsActionCompleted(true);
            thisStage.close();
        }
        else
            statusLabel.setText("Firma z wpisanymi danymi już istnieje");
    }

    private void setUpdateView() {
        client = CliVariables.getObject();
        firmName.textProperty().setValue(client.getFirmName());
        btnProceed.setText("Zmień");
    }

    private boolean createOrUpdate() {
        boolean endGate;
        if(CliVariables.getObject() == null) {
            if(clientService.findByFirmName(firmName.getText()) == null){
                clientService.create(firmName.getText());
                endGate = true;
            }
            else
                endGate = false;
        }
        else {
            clientService.update(client);
            endGate = true;
        }
        return endGate;
    }
}
