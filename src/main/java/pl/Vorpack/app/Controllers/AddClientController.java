package pl.Vorpack.app.Controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.Vorpack.app.DatabaseAccess.ClientAccess;
import pl.Vorpack.app.Properties.MainPaneProperty;
import pl.Vorpack.app.Domain.Client;
import pl.Vorpack.app.GlobalVariables.CliVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;

import java.text.ParseException;
import java.util.List;

/**
 * Created by Paweł on 2018-02-21.
 */
public class AddClientController {

    @FXML
    private VBox vBox;
    @FXML
    private JFXTextField textFirmName = new JFXTextField(){
        @Override
        public void paste(){}
    };
    @FXML
    private JFXButton btnProceed;
    @FXML
    private Label statusLabel;
    private MainPaneProperty cliProperty = new MainPaneProperty();
    private Client object = new Client();
    private Boolean isModify;
    private ClientAccess clientAccess = new ClientAccess();

    @FXML
    public void initialize(){

        isModify = false;
        textFirmName.textProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue.isEmpty()){
                cliProperty.setDisableBtn(false);
            }
            else {
                cliProperty.setDisableBtn(true);
            }
        });

        btnProceed.disableProperty().bindBidirectional(cliProperty.disableBtnProperty());

        if(CliVariables.getObject() != null){
            object = CliVariables.getObject();
            textFirmName.textProperty().setValue(object.getFirmName());
            btnProceed.setText("Zmień");
            isModify = true;
        }
    }

    public void btnAddClicked(MouseEvent mouseEvent) throws ParseException {

        boolean endGate = false;
        object.setFirmName(textFirmName.getText());
        try{
            List<Client> existedRecord = clientAccess.findClient(textFirmName.getText());

            if(CliVariables.getObject() == null) {
                if(existedRecord.size() == 0){
                    clientAccess.createNewClient(object);
                    endGate = true;
                }
                else
                    endGate = false;
            }
            else if(CliVariables.getObject() != null){
                clientAccess.updateClient(object);
                endGate = true;
            }
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
        else if(!endGate){
            statusLabel.setText("Firma z wpisanymi danymi już istnieje");
        }

    }


}
