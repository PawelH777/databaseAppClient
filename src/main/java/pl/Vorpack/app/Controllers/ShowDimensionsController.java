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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.Vorpack.app.DatabaseAccess.DimensionsAccess;
import pl.Vorpack.app.DatabaseAccess.SingleOrdersAccess;
import pl.Vorpack.app.Properties.MainPaneProperty;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.GlobalVariables.DimVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Alerts.InfoAlerts;

import java.io.IOException;
import java.util.List;

/**
 * Created by Paweł on 2018-02-18.
 */
public class ShowDimensionsController {

    private static final String ADD_DIMENSION_PANE_FXML = "/fxml/dimensions/AddDimensionPane.fxml";

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
    private JFXTextField txtSearch;
    @FXML
    private TableView<Dimiensions> dimTableView;
    @FXML
    private TableColumn<Dimiensions, Integer> idColumn;
    @FXML
    private TableColumn<Dimiensions, Double> firstDimColumn;
    @FXML
    private TableColumn<Dimiensions, Double> secondDimColumn;
    @FXML
    private TableColumn<Dimiensions, Double> thickColumn;
    @FXML
    private TableColumn<Dimiensions, Double> weightColumn;

    private MainPaneProperty dimProperty = new MainPaneProperty();
    private SortedList<Dimiensions> sortedData;
    private FilteredList<Dimiensions> filteredList;
    private TextAnimations textAnimations;
    private Dimiensions dimensionObject;
    private DimensionsAccess dimensionsAccess = new DimensionsAccess();

    @FXML
    void initialize(){
        StatusViewer.setOpacity(0);
        textAnimations = new TextAnimations(StatusViewer);
        txtSearch.disableProperty().setValue(true);
        columnsCmbBox.getItems().addAll(
                "Wszystkie",
                "Identyfikator",
                "Pierwszy bok",
                "Drugi bok",
                "Grubość",
                "Waga"
        );
        attachParametrsToTable();
        getRecords();
        sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(dimTableView.comparatorProperty());
        dimTableView.setItems(sortedData);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            getAllResult();
        });

        columnsCmbBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            getAllResult();
            txtSearch.disableProperty().setValue(false);
        });

        btnModify.disableProperty().bindBidirectional(dimProperty.disableModifyBtnProperty());
        btnDelete.disableProperty().bindBidirectional(dimProperty.disableDeleteBtnProperty());

        dimTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelect, newSelect) -> {
            if(newSelect != null){
                dimProperty.setDisableModifyBtn(false);
                dimProperty.setDisableDeleteBtn(false);
            }
        });
    }

    private void attachParametrsToTable() {
        idColumn.setCellValueFactory( new PropertyValueFactory<Dimiensions,Integer>("dimension_id"));
        firstDimColumn.setCellValueFactory(new PropertyValueFactory<Dimiensions, Double>("firstDimension"));
        secondDimColumn.setCellValueFactory(new PropertyValueFactory<Dimiensions, Double>("secondDimension"));
        thickColumn.setCellValueFactory(new PropertyValueFactory<Dimiensions, Double>("thickness"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<Dimiensions, Double>("weight"));
    }

    private void getRecords() {
        try{
            List<Dimiensions> query = DimVariables.getDimsFromDatabase();
            ObservableList<Dimiensions> data = FXCollections.observableArrayList(query);
            filteredList = new FilteredList<>(data, p -> true);
        }
        catch(Exception e){
            e.printStackTrace();
            InfoAlerts.generalAlert();
        }
    }

    private void getAllResult() {
        if("Wszystkie".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(dimension -> {
                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(dimension.getDimension_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimension.getFirstDimension()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimension.getSecondDimension()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimension.getThickness()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimension.getWeight()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;
            });
        }
        else if("Identyfikator".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(dimension -> {
                if(txtSearch == null || txtSearch.getText().isEmpty())

                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(dimension.getDimension_id()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;
            });
        }
        else if("Pierwszy bok".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(dimension -> {
                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(dimension.getFirstDimension()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;
            });
        }
        else if("Drugi bok".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(dimension -> {
                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(dimension.getSecondDimension()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;
            });
        }
        else if("Grubość".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(dimension -> {
                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(dimension.getThickness()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;
            });
        }
        else if("Waga".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(dimension -> {
                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(dimension.getWeight()).toLowerCase().contains(lowerCaseValue))
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
        try{
            dimensionObject = dimTableView.getSelectionModel().getSelectedItem();
            Boolean isDelete = InfoAlerts.deleteRecord("Posiadasz powiązane z obiektem niezakończone oraz zakończone zamówienia. By usunąć" +
                    " wymiar, program usunie również powiązane z nim obiekty. Jeśli program ma kontynuować pracę, naciśnij " +
                    "OK.");

            if(isDelete){
                dimensionsAccess.deleteDimension(dimensionObject);
                StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsDeleted());
            }
            else
                StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotDeleted());
        }
        catch(Exception e){
            e.printStackTrace();
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotDeleted());
        }
        getRecords();
        getAllResult();
        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
        textAnimations.startLabelsPulsing();
    }

    public void onBtnAddClicked() throws IOException {
        GlobalVariables.setIsActionCompleted(false);
        DimVariables.setObject(null);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_DIMENSION_PANE_FXML));
        VBox vBox = fxmlLoader.load();
        Scene scene = new Scene(vBox);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();
        getRecords();
        getAllResult();

        if(GlobalVariables.getIsActionCompleted())
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordAdded());
        else
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotAdded());

        textAnimations.startLabelsPulsing();
    }

    public void btnModifyClicked(MouseEvent mouseEvent) throws IOException {

        GlobalVariables.setIsActionCompleted(false);
        DimVariables.setObject(dimTableView.getSelectionModel().getSelectedItem());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_DIMENSION_PANE_FXML));
        VBox vBox = fxmlLoader.load();
        Scene scene = new Scene(vBox);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();
        getRecords();

        getAllResult();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
        if(GlobalVariables.getIsActionCompleted())
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordChanged());
        else
            StatusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotChanged());

        textAnimations.startLabelsPulsing();
    }

    public void btnRefreshClicked() throws IOException{
        getRecords();
        getAllResult();
        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
    }

    public void dimTableClicked(){

    }
}
