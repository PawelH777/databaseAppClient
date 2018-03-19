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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.Vorpack.app.Properties.mainPaneProperty;
import pl.Vorpack.app.domain.Client;
import pl.Vorpack.app.domain.Dimiensions;
import pl.Vorpack.app.global_variables.cliVariables;
import pl.Vorpack.app.global_variables.dimVariables;
import pl.Vorpack.app.optionalclass.records;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.IOException;

/**
 * Created by Paweł on 2018-02-21.
 */
public class ShowClientsController {

    public static final String ADD_CLIENTS_PANE_FXML = "/fxml/clients/AddClientsPane.fxml";
    @FXML
    private AnchorPane anchorPane;

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
    private TypedQuery<Client> query;

    private mainPaneProperty clientProperty = new mainPaneProperty();

    private Client client = new Client();

    private SortedList<Client> sortedData;
    private FilteredList<Client> filteredList;

    @FXML
    public void initialize(){
        txtSearch.disableProperty().setValue(true);

        columnsCmbBox.getItems().addAll(
                "Wszystkie",
                "Identyfikator",
                "Nazwa firmy"
        );


        idColumn.setCellValueFactory( new PropertyValueFactory<Client,Integer>("client_id"));
        firmName.setCellValueFactory( new PropertyValueFactory<Client, String>("firm_name"));


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


                if(columnsCmbBox.getValue().toString() == "Wszystkie"){
                    filteredList.setPredicate(user -> {

                        if(newValue == null || newValue.isEmpty())
                            return true;

                        String lowerCaseValue = newValue.toLowerCase();

                        if(String.valueOf(user.getClient_id()).toLowerCase().contains(lowerCaseValue))
                            return true;
                        else if(String.valueOf(user.getFirm_name()).toLowerCase().contains(lowerCaseValue))
                            return true;

                        return false;

                    });
                }
                else if(columnsCmbBox.getValue().toString() == "Identyfikator"){
                    filteredList.setPredicate(user -> {

                        if(newValue == null || newValue.isEmpty())
                            return true;

                        String lowerCaseValue = newValue.toLowerCase();

                        if(String.valueOf(user.getClient_id()).toLowerCase().contains(lowerCaseValue))
                            return true;

                        return false;

                    });
                }
                else if(columnsCmbBox.getValue().toString() == "Nazwa firmy"){
                    filteredList.setPredicate(user -> {

                        if(newValue == null || newValue.isEmpty())
                            return true;

                        String lowerCaseValue = newValue.toLowerCase();

                        if(String.valueOf(user.getFirm_name()).toLowerCase().contains(lowerCaseValue))
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
                    else if(String.valueOf(user.getFirm_name()).toLowerCase().contains(lowerCaseValue))
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

                    if(String.valueOf(user.getFirm_name()).toLowerCase().contains(lowerCaseValue))
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
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();
        query = entityManager.createQuery("SELECT c FROM Client c", Client.class);

        entityManager.getTransaction().commit();

        ObservableList<Client> data = FXCollections.observableArrayList(query.getResultList());

        entityManager.close();
        entityManagerFactory.close();
        filteredList = new FilteredList<>(data, p -> true);
    }


    public void onBtnAddClicked() throws IOException {

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
                else if(String.valueOf(user.getFirm_name()).toLowerCase().contains(lowerCaseValue))
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

                if(String.valueOf(user.getFirm_name()).toLowerCase().contains(lowerCaseValue))
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

        client = dimTableView.getSelectionModel().getSelectedItem();

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        entityManager.remove(entityManager.contains(client) ? client : entityManager.merge(client));

        entityManager.getTransaction().commit();

        entityManager.close();
        entityManagerFactory.close();

        getRecordsWithActualConfigure();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
    }

    public void btnModifyClicked() throws IOException {

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

    }

    public void dimTableClicked(){

    }

}
