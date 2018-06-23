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
import pl.Vorpack.app.DatabaseAccess.UsersAccess;
import pl.Vorpack.app.Properties.MainPaneProperty;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Domain.User;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.UsrVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.TableValues.UsersTableValue;

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
    private JFXTextField txtSearch;
    @FXML
    private TableView<UsersTableValue> dimTableView;
    @FXML
    private TableColumn<UsersTableValue, Integer> idColumn;
    @FXML
    private TableColumn<UsersTableValue, String> login;
    @FXML
    private TableColumn<UsersTableValue, String> password;
    @FXML
    private TableColumn<UsersTableValue, String> adminYesNo;

    private MainPaneProperty userProperty = new MainPaneProperty();
    private User userObject = new User();
    private UsersAccess usersAccess = new UsersAccess();
    private SortedList<UsersTableValue> sortedData;
    private FilteredList<UsersTableValue> filteredList;
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
        attachParametrsToTable();

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
            getRecordsWithActualConfigure();
        });

        columnsCmbBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            txtSearch.disableProperty().setValue(false);
            getRecordsWithActualConfigure();
        });

    }

    private void attachParametrsToTable() {
        idColumn.setCellValueFactory( new PropertyValueFactory<UsersTableValue,Integer>("user_id"));
        login.setCellValueFactory( new PropertyValueFactory<UsersTableValue, String>("login"));
        password.setCellValueFactory( new PropertyValueFactory<UsersTableValue,String>("password"));
        adminYesNo.setCellValueFactory( new PropertyValueFactory<UsersTableValue, String>("admin"));
    }

    private void getRecords() {
        try{
            List<User> users = usersAccess.findAllUsers();
            users.removeIf(u -> u.getLogin().equals("Admin"));
            List<UsersTableValue> result = changingClass(users);
            ObservableList<UsersTableValue> data = FXCollections.observableArrayList(result);
            filteredList = new FilteredList<>(data, p -> true);
        }catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void getRecordsWithActualConfigure() {
       if("Wszystkie".equals(columnsCmbBox.getValue()))
        {
            filteredList.setPredicate(UsersTableValue -> {
                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;
                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(UsersTableValue.getUser_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(UsersTableValue.getLogin()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(UsersTableValue.getPassword()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(UsersTableValue.getAdmin()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;
            });
        }
        else if("Identyfikator".equals(columnsCmbBox.getValue()))
        {
            filteredList.setPredicate(UsersTableValue -> {
                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;
                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(UsersTableValue.getUser_id()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;
            });
        }
        else if("Login".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(UsersTableValue -> {
                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(UsersTableValue.getLogin()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;
            });
        }
        else if("Hasło".equals(columnsCmbBox.getValue()))
        {
            filteredList.setPredicate(UsersTableValue -> {

                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(UsersTableValue.getPassword()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;

            });
        }
        else if("Administrator".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(UsersTableValue -> {

                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();

                if(String.valueOf(UsersTableValue.getAdmin()).toLowerCase().contains(lowerCaseValue))
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
        userObject = changingObjectType(dimTableView.getSelectionModel().getSelectedItem());
        try{
            usersAccess.deleteUser(userObject);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
        getRecords();
        getRecordsWithActualConfigure();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
    }

    public void onBtnAddClicked() throws IOException {
        GlobalVariables.setIsActionCompleted(false);
        UsrVariables.setObject(null);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_USERS_PANE_FXML));
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
        userObject = changingObjectType(dimTableView.getSelectionModel().getSelectedItem());
        UsrVariables.setObject(userObject);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_USERS_PANE_FXML));
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

    public void dimTableClicked(){

    }

    private List<UsersTableValue> changingClass(List<User> query){
        List<UsersTableValue> result = new ArrayList<UsersTableValue>();
        if(query.size() !=0){
            for(User u : query){
                UsersTableValue record = new UsersTableValue();
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

    private User changingObjectType(UsersTableValue rec){
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
        getRecords();
        getRecordsWithActualConfigure();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
    }

}
