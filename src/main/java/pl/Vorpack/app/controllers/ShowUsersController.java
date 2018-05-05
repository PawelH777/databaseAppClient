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
import pl.Vorpack.app.Properties.mainPaneProperty;
import pl.Vorpack.app.TextAnimations;
import pl.Vorpack.app.domain.User;
import pl.Vorpack.app.global_variables.GlobalVariables;
import pl.Vorpack.app.global_variables.usrVariables;
import pl.Vorpack.app.infoAlerts;
import pl.Vorpack.app.optionalclass.records;

import javax.persistence.TypedQuery;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Paweł on 2018-02-22.
 */
public class ShowUsersController {
    private static final String ADD_USERS_PANE_FXML = "/fxml/users/AddUsersPane.fxml";
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXComboBox columnsCmbBox;

    @FXML
    private Label StatusViewer;

    @FXML
    private JFXButton btnModify;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private JFXButton btnRefresh;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private TableView<records> dimTableView;

    @FXML
    private TableColumn<records, Integer> idColumn;

    @FXML
    private TableColumn<records, String> login;

    @FXML
    private TableColumn<records, String> password;

    @FXML
    private TableColumn<records, String> adminYesNo;
    private TypedQuery<User> query;

    private mainPaneProperty userProperty = new mainPaneProperty();

    private User user = new User();

    private records rec = new records();

    private SortedList<records> sortedData;
    private FilteredList<records> filteredList;

    private TextAnimations textAnimations;

    @FXML
    public void initialize(){
        txtSearch.disableProperty().setValue(true);

        StatusViewer.setOpacity(0);
        textAnimations = new TextAnimations(StatusViewer);
        columnsCmbBox.getItems().addAll(
                "Wszystkie",
                "Identyfikator",
                "Login",
                "Hasło",
                "Administrator"
        );

        idColumn.setCellValueFactory( new PropertyValueFactory<records,Integer>("user_id"));
        login.setCellValueFactory( new PropertyValueFactory<records, String>("login"));
        password.setCellValueFactory( new PropertyValueFactory<records,String>("password"));
        adminYesNo.setCellValueFactory( new PropertyValueFactory<records, String>("admin"));

        btnModify.disableProperty().bindBidirectional(userProperty.disableModifyBtnProperty());
        btnDelete.disableProperty().bindBidirectional(userProperty.disableDeleteBtnProperty());

        dimTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelect, newSelect) -> {
            if(newSelect != null){
                userProperty.setDisableModifyBtn(false);
                userProperty.setDisableDeleteBtn(false);
            }
        });

        getRecords();

        sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(dimTableView.comparatorProperty());

        dimTableView.setItems(sortedData);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {

                if(columnsCmbBox.getValue().toString().equals("Wszystkie"))
                {
                    filteredList.setPredicate(records-> {

                        if(newValue == null || newValue.isEmpty())
                            return true;

                        String lowerCaseValue = newValue.toLowerCase();

                        if(String.valueOf(records.getUser_id()).toLowerCase().contains(lowerCaseValue))
                            return true;
                        else if(String.valueOf(records.getLogin()).toLowerCase().contains(lowerCaseValue))
                            return true;
                        else if(String.valueOf(records.getPassword()).toLowerCase().contains(lowerCaseValue))
                            return true;
                        else if(String.valueOf(records.getAdmin()).toLowerCase().contains(lowerCaseValue))
                            return true;

                        return false;

                    });
                }
                else if(columnsCmbBox.getValue().toString().equals("Identyfikator"))
                {
                    filteredList.setPredicate(records-> {

                        if(newValue == null || newValue.isEmpty())
                            return true;

                        String lowerCaseValue = newValue.toLowerCase();

                        if(String.valueOf(records.getUser_id()).toLowerCase().contains(lowerCaseValue))
                            return true;

                        return false;

                    });
                }
                else if(columnsCmbBox.getValue().toString().equals("Login")){
                    filteredList.setPredicate(records-> {

                        if(newValue == null || newValue.isEmpty())
                            return true;

                        String lowerCaseValue = newValue.toLowerCase();

                        if(String.valueOf(records.getLogin()).toLowerCase().contains(lowerCaseValue))
                            return true;

                        return false;

                    });
                }
                else if(columnsCmbBox.getValue().toString().equals("Hasło"))
                {
                    filteredList.setPredicate(records-> {

                        if(newValue == null || newValue.isEmpty())
                            return true;

                        String lowerCaseValue = newValue.toLowerCase();
                        if(String.valueOf(records.getPassword()).toLowerCase().contains(lowerCaseValue))
                            return true;

                        return false;

                    });
                }
                else if(columnsCmbBox.getValue().toString().equals("Administrator")){
                    filteredList.setPredicate(records-> {

                        if(newValue == null || newValue.isEmpty())
                            return true;

                        String lowerCaseValue = newValue.toLowerCase();

                        if(String.valueOf(records.getAdmin()).toLowerCase().contains(lowerCaseValue))
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
                filteredList.setPredicate(records-> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())
                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();

                    if(String.valueOf(records.getUser_id()).toLowerCase().contains(lowerCaseValue))
                        return true;
                    else if(String.valueOf(records.getLogin()).toLowerCase().contains(lowerCaseValue))
                        return true;
                    else if(String.valueOf(records.getPassword()).toLowerCase().contains(lowerCaseValue))
                        return true;
                    else if(String.valueOf(records.getAdmin()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(newValue == "Identyfikator"){
                filteredList.setPredicate(records-> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())
                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();

                    if(String.valueOf(records.getUser_id()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(newValue == "Login"){
                filteredList.setPredicate(records-> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())
                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();

                    if(String.valueOf(records.getLogin()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(newValue == "Hasło"){
                filteredList.setPredicate(records-> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())
                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();
                    if(String.valueOf(records.getPassword()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });

            }
            else if(newValue == "Administrator"){
                filteredList.setPredicate(records-> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())
                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();

                    if(String.valueOf(records.getAdmin()).toLowerCase().contains(lowerCaseValue))
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

            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                    .nonPreemptive()
                    .credentials(GlobalVariables.getName(), GlobalVariables.getPassword())
                    .build();

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(feature);
            javax.ws.rs.client.Client client = ClientBuilder.newClient(clientConfig);

            String URI = GlobalVariables.getSite_name() + "/users";

            Response response = client
                    .target(URI)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            List<User> users = response.readEntity(new GenericType<ArrayList<User>>(){});

            users.removeIf(u -> u.getLogin().equals("Admin"));

            client.close();

            List<records> result = changingClass(users);

            ObservableList<records> data = FXCollections.observableArrayList(result);
            filteredList = new FilteredList<>(data, p -> true);
        }catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }
    }

    private void getRecordsWithActualConfigure() {
       getRecords();

       if("Wszystkie".equals(columnsCmbBox.getValue()))
        {
            filteredList.setPredicate(records-> {

                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();

                if(String.valueOf(records.getUser_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(records.getLogin()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(records.getPassword()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(records.getAdmin()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;

            });
        }
        else if("Identyfikator".equals(columnsCmbBox.getValue()))
        {
            filteredList.setPredicate(records-> {

                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();

                if(String.valueOf(records.getUser_id()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;

            });
        }
        else if("Login".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(records-> {

                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();

                if(String.valueOf(records.getLogin()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;

            });
        }
        else if("Hasło".equals(columnsCmbBox.getValue()))
        {
            filteredList.setPredicate(records-> {

                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(records.getPassword()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;

            });
        }
        else if("Administrator".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(records-> {

                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();

                if(String.valueOf(records.getAdmin()).toLowerCase().contains(lowerCaseValue))
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


        user = changingObjectType(dimTableView.getSelectionModel().getSelectedItem());

        try{

            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                    .nonPreemptive()
                    .credentials(GlobalVariables.getName(), GlobalVariables.getPassword())
                    .build();

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(feature);

            javax.ws.rs.client.Client client = ClientBuilder.newClient(clientConfig);

            String URI = GlobalVariables.getSite_name() + "/users/user/delete";

            Response response = client
                    .target(URI)
                    .path(String.valueOf(user.getUser_id()))
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .delete();

            client.close();

        }catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }

        getRecordsWithActualConfigure();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
    }

    public void onBtnAddClicked() throws IOException {

        GlobalVariables.setIsActionCompleted(false);
        usrVariables.setObject(null);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_USERS_PANE_FXML));
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
        user = changingObjectType(dimTableView.getSelectionModel().getSelectedItem());

        usrVariables.setObject(user);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_USERS_PANE_FXML));
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

    public void dimTableClicked(){

    }

    private List<records> changingClass(List<User> query){
        List<records> result = new ArrayList<records>();;

        if(query.size() !=0){

            for(User u : query){
                records record = new records();
                record.setUser_id(u.getUser_id());
                record.setLogin(u.getLogin());
                String pass = u.getPassword();

                record.setPassword(pass);

                if(u.isAdmin())
                    record.setAdmin("Tak");
                else if(!u.isAdmin())
                    record.setAdmin("Nie");

                result.add(record);
            }
        }


        return result;
    }

    private User changingObjectType(records rec){
        User usr = new User();

        usr.setUser_id(rec.getUser_id());
        usr.setLogin(rec.getLogin());
        usr.setPassword(rec.getPassword());

        if(Objects.equals(rec.getAdmin(), "Tak"))
            usr.setAdmin(true);
        else if(Objects.equals(rec.getAdmin(), "Nie"))
            usr.setAdmin(false);


        return usr;
    }

    public void btnRefreshClicked(){
        getRecordsWithActualConfigure();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
    }

}
