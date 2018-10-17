package pl.Vorpack.app.Controller.UserController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import pl.Vorpack.app.Domain.User;
import pl.Vorpack.app.Dto.UsersDTO;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.GlobalVariables.UsrVariables;
import pl.Vorpack.app.Service.CommonService;
import pl.Vorpack.app.Service.ServiceImpl.CommonServiceImpl;
import pl.Vorpack.app.Service.ServiceImpl.UserServiceImpl;
import pl.Vorpack.app.Service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static pl.Vorpack.app.Constans.UserColumn.*;

/**
 * Created by Paweł on 2018-02-22.
 */
public class UserController {
    @FXML
    private JFXComboBox<String> columnsCmbBox;
    @FXML
    private Label statusViewer;
    @FXML
    private JFXButton btnModify;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private TableView<UsersDTO> usersViewer;
    @FXML
    private TableColumn<UsersDTO, Integer> idColumn;
    @FXML
    private TableColumn<UsersDTO, String> login;
    @FXML
    private TableColumn<UsersDTO, String> password;
    @FXML
    private TableColumn<UsersDTO, String> adminYesNo;

    private UserService userService;
    private CommonService commonService;
    private User user = new User();
    private SortedList<UsersDTO> sortedData;
    private FilteredList<UsersDTO> filteredUsers;
    private TextAnimations textAnimations;
    private InfoAlerts infoAlerts = new InfoAlerts();

    @FXML
    public void initialize(){
        columnsCmbBox.getItems().addAll(
                ALL,
                ID,
                LOGIN,
                PASSWORD,
                ADMINISTRATOR
        );
        initServices();
        assignColumns();
        txtSearch.setDisable(true);
        setButtonsDisableValue(true);
        statusViewer.setOpacity(0);
        textAnimations = new TextAnimations(statusViewer);
        prepareUsers();
        sortedData = new SortedList<>(filteredUsers);
        sortedData.comparatorProperty().bind(usersViewer.comparatorProperty());
        usersViewer.setItems(sortedData);
        usersViewer.getSelectionModel().selectedItemProperty().addListener((obs, oldSelect, newSelect) -> {
            if(newSelect != null)
                setButtonsDisableValue(false);
        });
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filter(txtSearch.textProperty().getValue());
        });
        columnsCmbBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            txtSearch.setDisable(false);
            filter(txtSearch.textProperty().getValue());
        });
    }

    private void setButtonsDisableValue(boolean b) {
        btnModify.setDisable(b);
        btnDelete.setDisable(b);
    }

    private void initServices(){
        userService = new UserServiceImpl(columnsCmbBox);
        commonService = new CommonServiceImpl();
    }

    private void assignColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        login.setCellValueFactory(new PropertyValueFactory<>("login"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));
        adminYesNo.setCellValueFactory(new PropertyValueFactory<>("admin"));
    }

    private void prepareUsers() {
        try{
            List<UsersDTO> result = changingClass(UsrVariables.getUsersInDatabase());
            ObservableList<UsersDTO> data = FXCollections.observableArrayList(result);
            filteredUsers = new FilteredList<>(data, p -> true);
        } catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }
    }

    private void getUsers(){
        filteredUsers = changingClass(userService.findAll());
        filter(txtSearch.textProperty().getValue());
    }

    private void filter(String searchedText){
        userService.filter(searchedText, filteredUsers);
        sortedData = new SortedList<>(filteredUsers);
        usersViewer.setItems(sortedData);
    }

    public void btnDeleteClicked(){
        user = changingObjectType(usersViewer.getSelectionModel().getSelectedItem());
        try{
            userService.delete(user);
        }
        catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }
        getUsers();
        setButtonsDisableValue(true);
    }

    public void onBtnAddClicked() throws IOException {
        GlobalVariables.setIsActionCompleted(false);
        UsrVariables.setObject(null);
        commonService.openScene(Path.USER_EDITOR_PANE_PATH, "Edytor użytkownika", false);
        getUsers();
        if(GlobalVariables.getIsActionCompleted())
            statusViewer.setText(InfoAlerts.getStatusWhileRecordAdded());
        else
            statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotAdded());
        textAnimations.startLabelsPulsing();
    }

    public void btnModifyClicked() throws IOException {
        GlobalVariables.setIsActionCompleted(false);
        user = changingObjectType(usersViewer.getSelectionModel().getSelectedItem());
        UsrVariables.setObject(user);
        commonService.openScene(Path.USER_EDITOR_PANE_PATH, "Edytor użytkownika", false);
        getUsers();
        setButtonsDisableValue(true);
        if(GlobalVariables.getIsActionCompleted())
            statusViewer.setText(InfoAlerts.getStatusWhileRecordChanged());
        else
            statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotChanged());
        textAnimations.startLabelsPulsing();
    }

    private FilteredList<UsersDTO> changingClass(List<User> query){
        List<UsersDTO> result = new ArrayList<>();
        if(query.size() !=0){
            for(User u : query){
                UsersDTO record = new UsersDTO();
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
        ObservableList<UsersDTO> data = FXCollections.observableArrayList(result);
        return new FilteredList<>(data, p -> true);
    }

    private User changingObjectType(UsersDTO rec){
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
        getUsers();
        setButtonsDisableValue(true);
    }
}
