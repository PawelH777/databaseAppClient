package pl.Vorpack.app.Controller.DimensionController;

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
import javafx.scene.input.MouseEvent;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.Constans.PathConstans;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.GlobalVariables.DimVariables;
import pl.Vorpack.app.GlobalVariables.GlobalVariables;
import pl.Vorpack.app.Service.CommonService;
import pl.Vorpack.app.Service.DimensionService;
import pl.Vorpack.app.Service.ServiceImpl.CommonServiceImpl;
import pl.Vorpack.app.Service.ServiceImpl.DimensionServiceImpl;

import java.io.IOException;
import java.util.List;

import static pl.Vorpack.app.Constans.DimensionColumnConstans.*;

/**
 * Created by Paweł on 2018-02-18.
 */
public class DimensionController {

    @FXML
    private Label statusViewer;
    @FXML
    private JFXComboBox<String> columnsCmbBox;
    @FXML
    private JFXButton btnModify;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private JFXTextField txtSearch;
    @FXML
    private TableView<Dimiensions> dimsViewer;
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

    private DimensionService dimensionService;
    private CommonService commonService;
    private SortedList<Dimiensions> sortedData;
    private FilteredList<Dimiensions> filteredDims;
    private TextAnimations textAnimations;
    private InfoAlerts infoAlerts = new InfoAlerts();

    @FXML
    public void initialize(){
        statusViewer.setOpacity(0);
        columnsCmbBox.getItems().addAll(
                ALL,
                ID,
                FIRST_DIMENSION,
                SECOND_DIMENSION,
                THICKNESS,
                WEIGHT
        );
        initServices();
        assignColumns();
        setDims();
        textAnimations = new TextAnimations(statusViewer);
        txtSearch.setDisable(true);
        setButtonsDisableValue(true);
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            filter(txtSearch.textProperty().getValue());
        });
        columnsCmbBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            txtSearch.setDisable(false);
            filter(txtSearch.textProperty().getValue());
        });
        dimsViewer.getSelectionModel().selectedItemProperty().addListener((obs, oldSelect, newSelect) -> {
            if(newSelect != null)
                setButtonsDisableValue(false);
        });
    }

    public void onBtnAddClicked() throws IOException {
        GlobalVariables.setIsActionCompleted(false);
        DimVariables.setObject(null);
        commonService.openScene(PathConstans.DIMENSION_EDITOR_PANE_PATH, "Edytor wymiaru", false);
        getDims();
        if(GlobalVariables.getIsActionCompleted())
            statusViewer.setText(InfoAlerts.getStatusWhileRecordAdded());
        else
            statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotAdded());
        textAnimations.startLabelsPulsing();
    }

    public void btnModifyClicked(MouseEvent mouseEvent) throws IOException {
        GlobalVariables.setIsActionCompleted(false);
        DimVariables.setObject(dimsViewer.getSelectionModel().getSelectedItem());
        commonService.openScene(PathConstans.DIMENSION_EDITOR_PANE_PATH, "Edytor wymiaru", false);
        getDims();
        setButtonsDisableValue(true);
        if(GlobalVariables.getIsActionCompleted())
            statusViewer.setText(InfoAlerts.getStatusWhileRecordChanged());
        else
            statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotChanged());
        textAnimations.startLabelsPulsing();
    }

    public void btnDeleteClicked(){
        try{
            Dimiensions dim = dimsViewer.getSelectionModel().getSelectedItem();
            Boolean isDelete = infoAlerts.deleteRecord("Posiadasz powiązane z obiektem niezakończone oraz zakończone zamówienia. By usunąć" +
                    " wymiar, program usunie również powiązane z nim obiekty. Jeśli program ma kontynuować pracę, naciśnij " +
                    "OK.");
            if(isDelete){
                dimensionService.delete(dim);
                statusViewer.setText(InfoAlerts.getStatusWhileRecordIsDeleted());
            }
            else
                statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotDeleted());
        }
        catch(Exception e){
            e.printStackTrace();
            statusViewer.setText(InfoAlerts.getStatusWhileRecordIsNotDeleted());
        }
        getDims();
        setButtonsDisableValue(true);
        textAnimations.startLabelsPulsing();
    }

    public void btnRefreshClicked(){
        DimVariables.setDimsFromDatabase(dimensionService.findAll());
        getDims();
        setButtonsDisableValue(true);
    }

    private void initServices(){
        dimensionService = new DimensionServiceImpl();
        commonService = new CommonServiceImpl();
    }

    private void setButtonsDisableValue(boolean b) {
        btnModify.setDisable(b);
        btnDelete.setDisable(b);
    }

    private void setDims() {
        prepareDims();
        sortedData = new SortedList<>(filteredDims);
        sortedData.comparatorProperty().bind(dimsViewer.comparatorProperty());
        dimsViewer.setItems(sortedData);
    }

    private void filter(String searchedText){
        dimensionService.filter(columnsCmbBox.getSelectionModel().getSelectedItem(), searchedText, filteredDims);
        sortedData = new SortedList<>(filteredDims);
        dimsViewer.setItems(sortedData);
    }

    private void assignColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("dimension_id"));
        firstDimColumn.setCellValueFactory(new PropertyValueFactory<>("firstDimension"));
        secondDimColumn.setCellValueFactory(new PropertyValueFactory<>("secondDimension"));
        thickColumn.setCellValueFactory(new PropertyValueFactory<>("thickness"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
    }

    private void prepareDims() {
        try{
            List<Dimiensions> query = DimVariables.getDimsFromDatabase();
            ObservableList<Dimiensions> data = FXCollections.observableArrayList(query);
            filteredDims = new FilteredList<>(data, p -> true);
        }
        catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }
    }

    private void getDims(){
        List<Dimiensions> dims = dimensionService.findAll();
        ObservableList<Dimiensions> data = FXCollections.observableArrayList(dims);
        filteredDims = new FilteredList<>(data, p -> true);
        filter(txtSearch.textProperty().getValue());
    }
}
