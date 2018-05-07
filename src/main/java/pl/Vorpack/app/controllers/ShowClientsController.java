package pl.Vorpack.app.controllers;

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
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import pl.Vorpack.app.DatabaseAccess;
import pl.Vorpack.app.Properties.mainPaneProperty;
import pl.Vorpack.app.TextAnimations;
import pl.Vorpack.app.domain.Client;
import pl.Vorpack.app.domain.Orders;
import pl.Vorpack.app.domain.ordersStory;
import pl.Vorpack.app.global_variables.cliVariables;
import pl.Vorpack.app.global_variables.GlobalVariables;
import pl.Vorpack.app.infoAlerts;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private JFXButton btnRefresh;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private TableView<Client> dimTableView;

    @FXML
    private TableColumn<Client, Integer> idColumn;

    @FXML
    private TableColumn<Client, String> firmName;

    private mainPaneProperty clientProperty = new mainPaneProperty();

    private SortedList<Client> sortedData;
    private FilteredList<Client> filteredList;

    private TextAnimations textAnimations;


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


        idColumn.setCellValueFactory( new PropertyValueFactory<Client,Integer>("client_id"));
        firmName.setCellValueFactory( new PropertyValueFactory<Client, String>("firmName"));


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


                if(Objects.equals(columnsCmbBox.getValue().toString(), "Wszystkie")){
                    filteredList.setPredicate(user -> {

                        if(newValue == null || newValue.isEmpty())
                            return true;

                        String lowerCaseValue = newValue.toLowerCase();

                        if(String.valueOf(user.getClient_id()).toLowerCase().contains(lowerCaseValue))
                            return true;
                        else if(String.valueOf(user.getFirmName()).toLowerCase().contains(lowerCaseValue))
                            return true;

                        return false;

                    });
                }
                else if(Objects.equals(columnsCmbBox.getValue().toString(), "Identyfikator")){
                    filteredList.setPredicate(user -> {

                        if(newValue == null || newValue.isEmpty())
                            return true;

                        String lowerCaseValue = newValue.toLowerCase();

                        if(String.valueOf(user.getClient_id()).toLowerCase().contains(lowerCaseValue))
                            return true;

                        return false;

                    });
                }
                else if(Objects.equals(columnsCmbBox.getValue().toString(), "Nazwa firmy")){
                    filteredList.setPredicate(user -> {

                        if(newValue == null || newValue.isEmpty())
                            return true;

                        String lowerCaseValue = newValue.toLowerCase();

                        if(String.valueOf(user.getFirmName()).toLowerCase().contains(lowerCaseValue))
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
        });

        columnsCmbBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {

            txtSearch.disableProperty().setValue(false);
            if(newValue == "Wszystkie"){
                filteredList.setPredicate(user -> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())
                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();

                    if(String.valueOf(user.getClient_id()).toLowerCase().contains(lowerCaseValue))
                        return true;
                    else if(String.valueOf(user.getFirmName()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(newValue == "Identyfikator"){
                filteredList.setPredicate(user -> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())
                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();

                    if(String.valueOf(user.getClient_id()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(newValue == "Nazwa firmy"){
                filteredList.setPredicate(user -> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())
                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();

                    if(String.valueOf(user.getFirmName()).toLowerCase().contains(lowerCaseValue))
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
        });

    }

    private void getRecords() {

        try{


            javax.ws.rs.client.Client clientBulider =
                    DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());

            String URI = GlobalVariables.getSite_name() + "/clients";

            Response response = clientBulider
                    .target(URI)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();

            List<Client> query = response.readEntity(new GenericType<ArrayList<Client>>(){});

            clientBulider.close();


            ObservableList<Client> data = FXCollections.observableArrayList(query);

            filteredList = new FilteredList<>(data, p -> true);

        }catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }
    }



    private void getRecordsWithActualConfigure() {
        getRecords();

        if("Wszystkie".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(user -> {

                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();

                if(String.valueOf(user.getClient_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(user.getFirmName()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;

            });
        }
        else if("Identyfikator".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(user -> {

                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();

                if(String.valueOf(user.getClient_id()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;

            });
        }
        else if("Nazwa firmy".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(user -> {

                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();

                if(String.valueOf(user.getFirmName()).toLowerCase().contains(lowerCaseValue))
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

        Boolean isDelete = true;

        try{

            Client client = dimTableView.getSelectionModel().getSelectedItem();

            javax.ws.rs.client.Client clientBulider =
                    DatabaseAccess.accessToDatabase(GlobalVariables.getName(), GlobalVariables.getPassword());

            String URI = GlobalVariables.getSite_name() + "/orders/clients";

            Response response = clientBulider
                    .target(URI)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(client, MediaType.APPLICATION_JSON_TYPE));

            List<Orders> ordersWithClient = response.readEntity(new GenericType<List<Orders>>(){});

            URI = GlobalVariables.getSite_name() + "/orderstory/clients";

            response = clientBulider
                    .target(URI)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(client, MediaType.APPLICATION_JSON_TYPE));

            List<ordersStory> storyOrdersWithClient = response.readEntity(new GenericType<List<ordersStory>>(){});

            if(ordersWithClient.size() != 0 && storyOrdersWithClient.size() == 0){

                isDelete = infoAlerts.deleteRecord("ZAMÓWIENIA", null);

                if(isDelete){

                    URI = GlobalVariables.getSite_name() + "/orders/delete/client";

                    response = clientBulider
                            .target(URI)
                            .request(MediaType.APPLICATION_JSON_TYPE)
                            .post(Entity.entity(client, MediaType.APPLICATION_JSON_TYPE));
                }

            }
            else if(ordersWithClient.size() == 0 && storyOrdersWithClient.size() != 0){
                isDelete = infoAlerts.deleteRecord("HISTORIA ZAMÓWIEŃ", null);

                if(isDelete){

                    URI = GlobalVariables.getSite_name() + "/orderstory/delete/client";

                    response = clientBulider
                            .target(URI)
                            .request(MediaType.APPLICATION_JSON_TYPE)
                            .post(Entity.entity(client, MediaType.APPLICATION_JSON_TYPE));
                }
            }
            else if(ordersWithClient.size() != 0 && storyOrdersWithClient.size() != 0){
                isDelete = infoAlerts.deleteRecord("ZAMÓWIENIA", "HISTORIA ZAMÓWIEŃ");

                if(isDelete){

                    URI = GlobalVariables.getSite_name() + "/orders/delete/client";

                    response = clientBulider
                            .target(URI)
                            .request(MediaType.APPLICATION_JSON_TYPE)
                            .post(Entity.entity(client, MediaType.APPLICATION_JSON_TYPE));

                    URI = GlobalVariables.getSite_name() + "/orderstory/delete/client";

                    response = clientBulider
                            .target(URI)
                            .request(MediaType.APPLICATION_JSON_TYPE)
                            .post(Entity.entity(client, MediaType.APPLICATION_JSON_TYPE));
                }
            }

            if(isDelete){


                URI = GlobalVariables.getSite_name() + "/clients/client/delete";

                response = clientBulider
                        .target(URI)
                        .path(String.valueOf(client.getClient_id()))
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .delete();
            }

            clientBulider.close();

        } catch(Exception e){

            e.printStackTrace();
            infoAlerts.generalAlert();
        }

        getRecordsWithActualConfigure();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
    }


    public void onBtnAddClicked() throws IOException {
        GlobalVariables.setIsActionCompleted(false);
        cliVariables.setObject(null);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_CLIENTS_PANE_FXML));
        VBox vBox = fxmlLoader.load();
        Scene scene = new Scene(vBox);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();

        getRecordsWithActualConfigure();

        if(GlobalVariables.getIsActionCompleted())
            StatusViewer.setText(infoAlerts.getStatusWhileRecordAdded());
        else
            StatusViewer.setText(infoAlerts.getStatusWhileRecordIsNotAdded());

        textAnimations.startLabelsPulsing();
    }

    public void btnModifyClicked() throws IOException {

        GlobalVariables.setIsActionCompleted(false);

        cliVariables.setObject(dimTableView.getSelectionModel().getSelectedItem());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_CLIENTS_PANE_FXML));
        VBox vBox = fxmlLoader.load();
        Scene scene = new Scene(vBox);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();

        getRecordsWithActualConfigure();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);

        if(GlobalVariables.getIsActionCompleted())
            StatusViewer.setText(infoAlerts.getStatusWhileRecordChanged());
        else
            StatusViewer.setText(infoAlerts.getStatusWhileRecordIsNotChanged());

        textAnimations.startLabelsPulsing();
    }

    public void btnRefreshClicked() throws IOException{
        getRecordsWithActualConfigure();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
    }

    public void dimTableClicked(){

    }

}
