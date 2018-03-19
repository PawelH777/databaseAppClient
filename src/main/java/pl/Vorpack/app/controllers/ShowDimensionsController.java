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
import javafx.scene.Parent;
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
import pl.Vorpack.app.domain.Dimiensions;
import pl.Vorpack.app.global_variables.dimVariables;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.Console;
import java.io.IOException;

/**
 * Created by Paweł on 2018-02-18.
 */
public class ShowDimensionsController {

    public static final String ADD_DIMENSION_PANE_FXML = "/fxml/dimensions/AddDimensionPane.fxml";

    Dimiensions dim;

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

    private mainPaneProperty dimProperty = new mainPaneProperty();
    private TypedQuery<Dimiensions> query;

    private SortedList<Dimiensions> sortedData;
    private FilteredList<Dimiensions> filteredList;



    @FXML
    void initialize(){

        txtSearch.disableProperty().setValue(true);

        columnsCmbBox.getItems().addAll(
                "Wszystkie",
                "Identyfikator",
                "Pierwszy bok",
                "Drugi bok",
                "Grubość",
                "Waga"
        );

        idColumn.setCellValueFactory( new PropertyValueFactory<Dimiensions,Integer>("dimension_id"));
        firstDimColumn.setCellValueFactory(new PropertyValueFactory<Dimiensions, Double>("first_dimension"));
        secondDimColumn.setCellValueFactory(new PropertyValueFactory<Dimiensions, Double>("second_dimension"));
        thickColumn.setCellValueFactory(new PropertyValueFactory<Dimiensions, Double>("thickness"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<Dimiensions, Double>("weight"));

        getRecords();

        sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(dimTableView.comparatorProperty());

        dimTableView.setItems(sortedData);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            if(columnsCmbBox.getSelectionModel().selectedItemProperty().getValue().toString().contains("Wszystkie")){
                filteredList.setPredicate(dimension -> {

                    if(newValue == null || newValue.isEmpty())
                        return true;

                    String lowerCaseValue = newValue.toLowerCase();

                    if(String.valueOf(dimension.getDimension_id()).toLowerCase().contains(lowerCaseValue))
                        return true;
                    else if(String.valueOf(dimension.getFirst_dimension()).toLowerCase().contains(lowerCaseValue))
                        return true;
                    else if(String.valueOf(dimension.getSecond_dimension()).toLowerCase().contains(lowerCaseValue))
                        return true;
                    else if(String.valueOf(dimension.getThickness()).toLowerCase().contains(lowerCaseValue))
                        return true;
                    else if(String.valueOf(dimension.getWeight()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });


            }
            else if(columnsCmbBox.getSelectionModel().selectedItemProperty().getValue().toString().contains("Identyfikator")){
                filteredList.setPredicate(dimension -> {

                    if(newValue == null || newValue.isEmpty())
                        return true;

                    String lowerCaseValue = newValue.toLowerCase();

                    if(String.valueOf(dimension.getDimension_id()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(columnsCmbBox.getSelectionModel().selectedItemProperty().getValue().toString().contains("Pierwszy bok")){
                filteredList.setPredicate(dimension -> {

                    if(newValue == null || newValue.isEmpty())
                        return true;

                    String lowerCaseValue = newValue.toLowerCase();

                    if(String.valueOf(dimension.getFirst_dimension()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(columnsCmbBox.getSelectionModel().selectedItemProperty().getValue().toString().contains("Drugi bok")){
                filteredList.setPredicate(dimension -> {

                    if(newValue == null || newValue.isEmpty())
                        return true;

                    String lowerCaseValue = newValue.toLowerCase();

                    if(String.valueOf(dimension.getSecond_dimension()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(columnsCmbBox.getSelectionModel().selectedItemProperty().getValue().toString().contains("Grubość")){
                filteredList.setPredicate(dimension -> {

                    if(newValue == null || newValue.isEmpty())
                        return true;

                    String lowerCaseValue = newValue.toLowerCase();

                    if(String.valueOf(dimension.getThickness()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(columnsCmbBox.getSelectionModel().selectedItemProperty().getValue().toString().contains("Waga")){
                filteredList.setPredicate(dimension -> {

                    if(newValue == null || newValue.isEmpty())
                        return true;

                    String lowerCaseValue = newValue.toLowerCase();

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

        });

        columnsCmbBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue.toString().contains("Wszystkie")){
                filteredList.setPredicate(dimension -> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())
                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();

                    if(String.valueOf(dimension.getDimension_id()).toLowerCase().contains(lowerCaseValue))
                        return true;
                    else if(String.valueOf(dimension.getFirst_dimension()).toLowerCase().contains(lowerCaseValue))
                        return true;
                    else if(String.valueOf(dimension.getSecond_dimension()).toLowerCase().contains(lowerCaseValue))
                        return true;
                    else if(String.valueOf(dimension.getThickness()).toLowerCase().contains(lowerCaseValue))
                        return true;
                    else if(String.valueOf(dimension.getWeight()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });


            }
            else if(newValue.toString().contains("Identyfikator")){
                filteredList.setPredicate(dimension -> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())

                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();
                    if(String.valueOf(dimension.getDimension_id()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(newValue.toString().contains("Pierwszy bok")){
                filteredList.setPredicate(dimension -> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())
                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();
                    if(String.valueOf(dimension.getFirst_dimension()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(newValue.toString().contains("Drugi bok")){
                filteredList.setPredicate(dimension -> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())
                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();
                    if(String.valueOf(dimension.getSecond_dimension()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(newValue.toString().contains("Grubość")){
                filteredList.setPredicate(dimension -> {

                    if(txtSearch == null || txtSearch.getText().isEmpty())
                        return true;

                    String lowerCaseValue = txtSearch.getText().toLowerCase();
                    if(String.valueOf(dimension.getThickness()).toLowerCase().contains(lowerCaseValue))
                        return true;

                    return false;

                });
            }
            else if(newValue.toString().contains("Waga")){
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

    private void getRecords() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();
        query = entityManager.createQuery("SELECT d FROM Dimiensions d", Dimiensions.class);

        entityManager.getTransaction().commit();
        ObservableList<Dimiensions> data = FXCollections.observableArrayList(query.getResultList());

        entityManager.close();
        entityManagerFactory.close();

        filteredList = new FilteredList<>(data, p -> true);
    }

    private void getAllResult() {
        getRecords();

        if("Wszystkie".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(dimension -> {

                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();

                if(String.valueOf(dimension.getDimension_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimension.getFirst_dimension()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimension.getSecond_dimension()).toLowerCase().contains(lowerCaseValue))
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
                if(String.valueOf(dimension.getFirst_dimension()).toLowerCase().contains(lowerCaseValue))
                    return true;

                return false;

            });
        }
        else if("Drugi bok".equals(columnsCmbBox.getValue())){
            filteredList.setPredicate(dimension -> {

                if(txtSearch == null || txtSearch.getText().isEmpty())
                    return true;

                String lowerCaseValue = txtSearch.getText().toLowerCase();
                if(String.valueOf(dimension.getSecond_dimension()).toLowerCase().contains(lowerCaseValue))
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

    public void onBtnAddClicked() throws IOException {

        dimVariables.setObject(null);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_DIMENSION_PANE_FXML));
        VBox vBox = fxmlLoader.load();
        Scene scene = new Scene(vBox);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();

        getAllResult();

    }

    public void btnDeleteClicked(){

        dim = dimTableView.getSelectionModel().getSelectedItem();

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        entityManager.remove(entityManager.contains(dim) ? dim : entityManager.merge(dim));

        entityManager.getTransaction().commit();

        entityManager.close();
        entityManagerFactory.close();

        getAllResult();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);

    }

    public void btnModifyClicked(MouseEvent mouseEvent) throws IOException {

        dimVariables.setObject(dimTableView.getSelectionModel().getSelectedItem());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ADD_DIMENSION_PANE_FXML));
        VBox vBox = fxmlLoader.load();
        Scene scene = new Scene(vBox);
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();

        getAllResult();

        btnDelete.disableProperty().setValue(true);
        btnModify.disableProperty().setValue(true);
    }

    public void dimTableClicked(){

    }
}
