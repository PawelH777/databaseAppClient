package pl.Vorpack.app.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import pl.Vorpack.app.Properties.mainPaneProperty;
import pl.Vorpack.app.domain.Client;
import pl.Vorpack.app.domain.Dimiensions;
import pl.Vorpack.app.domain.Orders;
import pl.Vorpack.app.domain.ordersStory;
import pl.Vorpack.app.global_variables.cliVariables;
import pl.Vorpack.app.global_variables.dimVariables;
import pl.Vorpack.app.global_variables.ordVariables;
import pl.Vorpack.app.global_variables.userData;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 2018-03-01.
 */
public class ShowStoryController {


    @FXML
    private JFXComboBox columnsCmbBox;

    @FXML
    private JFXButton btnRecover;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXListView<String> lstOrders;

    @FXML
    private DatePicker orderDatePicker;

    @FXML
    private JFXComboBox datesCmbBox;

    @FXML
    private Label labelIdValue;

    @FXML
    private Label labelNameValue;

    @FXML
    private Label labelOrderDateValue;

    @FXML
    private Label labelReceiverDateValue;

    @FXML
    private Label labelMetrsValue;

    @FXML
    private Label labelMaterialsValue;

    @FXML
    private Label labelFirstDimValue;

    @FXML
    private Label labelSecDimValue;

    @FXML
    private Label labelThickValue;

    @FXML
    private Label labelWeightValue;

    @FXML
    private Label labelNoteValue;

    private List<Object[]> results;

    private List<String> records = new ArrayList<String>();

    private List<ordersStory> ord = new ArrayList<>();

    private List<Client> cli = new ArrayList<>();

    private List<Dimiensions> dim = new ArrayList<>();
    private String sortValue;

    private String item;

    private Orders orderObject = new Orders();
    private SortedList<Object []> sortedData;
    private FilteredList<Object []> filteredList;

    private List<String> listToView = new ArrayList<>();

    private int gate, dateGate;

    @FXML
    private void initialize(){

        ordVariables.setObject(null);

        dimVariables.setObject(null);

        cliVariables.setObject(null);

        labelNoteValue.setWrapText(true);



        columnsCmbBox.getItems().addAll(
                "Wszystkie",
                "Identyfikator",
                "Nazwa firmy",
                "Pierwszy bok",
                "Drugi bok",
                "Grubość",
                "Waga",
                "Ilość metrów",
                "Ilość materiałów"
        );

        datesCmbBox.getItems().addAll(
                "Data zamówienia",
                "Data stworzenia"
        );

        btnDelete.disableProperty().setValue(true);

        btnRecover.disableProperty().setValue(true);

        lstOrders.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            int index = 0;
            if(newValue == null){
                btnDelete.disableProperty().setValue(true);

                btnRecover.disableProperty().setValue(true);

                labelIdValue.setText("");
                labelNameValue.setText("");
                labelOrderDateValue.setText("");
                labelReceiverDateValue.setText("");
                labelMetrsValue.setText("");
                labelMaterialsValue.setText("");
                labelFirstDimValue.setText("");
                labelSecDimValue.setText("");
                labelThickValue.setText("");
                labelWeightValue.setText("");
                labelNoteValue.setText("");
            }
            else{

                index = lstOrders.getSelectionModel().getSelectedIndex();

                if(!userData.getAccess().equals("Użytkownik"))
                    btnDelete.disableProperty().setValue(false);

                btnRecover.disableProperty().setValue(false);

                ordersStory o = (ordersStory) sortedData.get(index)[0];
                Dimiensions d  =(Dimiensions) sortedData.get(index)[1];
                Client c = (Client) sortedData.get(index)[2];

                labelIdValue.setText(String.valueOf(o.getOrder_id()));
                labelNameValue.setText(String.valueOf(c.getFirm_name()));
                labelOrderDateValue.setText(String.valueOf(o.getOrder_date()));
                labelReceiverDateValue.setText(String.valueOf(o.getReceive_date()));
                labelMetrsValue.setText(String.valueOf(o.getMetrs()));
                labelMaterialsValue.setText(String.valueOf(o.getMaterials()));
                labelFirstDimValue.setText(String.valueOf(d.getFirst_dimension()));
                labelSecDimValue.setText(String.valueOf(d.getSecond_dimension()));
                labelThickValue.setText(String.valueOf(d.getThickness()));
                labelWeightValue.setText(String.valueOf(d.getWeight()));
                labelNoteValue.setText(String.valueOf(o.getNote()));
            }
        }));

        getAllRecords();


        sortedData = new SortedList<>(filteredList);

        sortGate();

        listToView = fetchToList(sortedData);

        lstOrders.setItems(FXCollections.observableArrayList(listToView));


        orderDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });


        txtSearch.disableProperty().setValue(true);
        orderDatePicker.disableProperty().setValue(true);

        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> {
            getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });

        columnsCmbBox.valueProperty().addListener((obs, oldValue, newValue) ->{
            if(newValue.toString().isEmpty()){
                txtSearch.disableProperty().setValue(true);
            }
            else{
                txtSearch.disableProperty().setValue(false);
            }

            getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });

        datesCmbBox.valueProperty().addListener((ObservableValue obs, Object oldValue, Object newValue) ->{

            if(!newValue.toString().isEmpty())
                orderDatePicker.disableProperty().setValue(false);

            if ("Data zamówienia".equals(datesCmbBox.getValue())){
                dateGate = 1;
            }
            else if ("Data stworzenia".equals(datesCmbBox.getValue()))
            {
                dateGate = 2;
            }
            getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        });


    }

    private void sortGate() {
        if(gate == 1)
            setComparatorOrderDate();
        else if(gate == 2)
            setComparatorReceiveDate();
        else if(gate == 3)
            setComparatorDimensions();
        else if(gate == 4)
            setComparatorFirmName();
    }

    private void setComparatorFirmName() {
            sortedData.setComparator((o1, o2) -> {
            Client clientObject = (Client)o1[2];
            Client clientObject_2 = (Client)o2[2];

            String name_1 = clientObject.getFirm_name();
            String name_2 = clientObject_2.getFirm_name();


            return name_1.compareTo(name_2);
        });
    }

    private void setComparatorDimensions() {
        sortedData.setComparator((o1, o2) ->{
            Dimiensions dimiensionObject = (Dimiensions) o1[1];
            Dimiensions dimiensionObject_2 = (Dimiensions) o2[1];

            Double firstDim_1 = dimiensionObject.getFirst_dimension();
            Double firstDim_2 = dimiensionObject_2.getFirst_dimension();

            Double secondDim_1 = dimiensionObject.getSecond_dimension();
            Double secondDim_2 = dimiensionObject_2.getSecond_dimension();

            Double thick_1 = dimiensionObject.getThickness();
            Double thick_2 = dimiensionObject_2.getThickness();

            Double weight_1 = dimiensionObject.getWeight();
            Double weight_2 = dimiensionObject_2.getWeight();

            if(firstDim_1 < firstDim_2)
                return -1;
            else if(firstDim_1 > firstDim_2)
                return 1;

            if(secondDim_1 <secondDim_2)
                return -1;
            else if(secondDim_1 > secondDim_2)
                return 1;

            if(thick_1 < thick_2)
                return -1;
            else if(thick_1 > thick_2)
                return 1;

            if(weight_1 < weight_2)
                return -1;
            else if(weight_1 > weight_2)
                return 1;

            return 0;



        });
    }

    private void setComparatorReceiveDate() {
            sortedData.setComparator((o1, o2) -> {
            ordersStory ordersObject = (ordersStory)o1[0];
            ordersStory ordersObject_2 = (ordersStory)o2[0];

            return ordersObject.getReceive_date().isBefore(ordersObject_2.getReceive_date()) ? -1
                    : ordersObject_2.getReceive_date().isBefore(ordersObject.getReceive_date()) ? 1
                    : 0;
        });

    }

    private void setComparatorOrderDate() {
            sortedData.setComparator((o1, o2) -> {
                ordersStory ordersObject = (ordersStory)o1[0];
                ordersStory ordersObject_2 = (ordersStory)o2[0];

            return ordersObject.getOrder_date().isBefore(ordersObject_2.getOrder_date()) ? -1
                    : ordersObject_2.getOrder_date().isBefore(ordersObject.getOrder_date()) ? 1
                    : 0;
        });
    }

    private void getRecordsIDColumn(String searchedText, String searchedData) {
        filteredList.setPredicate(obj -> {

            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            ordersStory ordersObj = (ordersStory) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getReceive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(ordersObj.getOrder_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(ordersObj.getOrder_id()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
                    return true;
            }

            return false;

        });
    }
    private void getRecordsFirmNameColumn(String searchedText, String searchedData) {
        filteredList.setPredicate(obj -> {

            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            Client cliObj = (Client) obj[2];
            ordersStory ordersObj = (ordersStory) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getReceive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(cliObj.getFirm_name()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(cliObj.getFirm_name()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
                    return true;
            }

            return false;

        });
    }

    private void getRecordsFirstDimensionColumn(String searchedText, String searchedData) {
        filteredList.setPredicate(obj -> {

            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            Dimiensions dimObj = (Dimiensions) obj[1];
            ordersStory ordersObj = (ordersStory) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getReceive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(dimObj.getFirst_dimension()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(dimObj.getFirst_dimension()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
                    return true;
            }

            return false;

        });
    }

    private void getRecordsSecondDimensionColumn(String searchedText, String searchedData) {

        filteredList.setPredicate(obj -> {

            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            Dimiensions dimObj = (Dimiensions) obj[1];
            ordersStory ordersObj = (ordersStory) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getReceive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(dimObj.getSecond_dimension()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(dimObj.getSecond_dimension()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
                    return true;
            }

            return false;

        });
    }

    private void getRecordsThickColumn(String searchedText, String searchedData) {
        filteredList.setPredicate(obj -> {

            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            Dimiensions dimObj = (Dimiensions) obj[1];
            ordersStory ordersObj = (ordersStory) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getReceive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(dimObj.getThickness()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(dimObj.getThickness()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
                    return true;
            }

            return false;

        });

    }

    private void getRecordsWeightColumn(String searchedText, String searchedData) {

        filteredList.setPredicate(obj -> {

            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            Dimiensions dimObj = (Dimiensions) obj[1];
            ordersStory ordersObj = (ordersStory) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getReceive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(dimObj.getWeight()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(dimObj.getWeight()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
                    return true;
            }

            return false;

        });
    }

    private void getRecordsMetrsColumn(String searchedText, String searchedData) {
        filteredList.setPredicate(obj -> {

            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            ordersStory ordersObj = (ordersStory) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getReceive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(ordersObj.getMetrs()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(ordersObj.getMetrs()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
                    return true;
            }

            return false;

        });
    }

    private void getRecordsMaterialsColumn(String searchedText, String searchedData) {

        filteredList.setPredicate(obj -> {

            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();
            ordersStory ordersObj = (ordersStory) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getReceive_date());

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(ordersObj.getMaterials()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(ordersObj.getMaterials()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
                    return true;
            }

            return false;

        });
    }

    private void getRecordsDate(String searchedData) {

        filteredList.setPredicate(obj -> {

            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null"))
                return true;

            String lowerCaseValue = searchedData.toLowerCase();
            ordersStory ordersObj = (ordersStory) obj[0];
            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getReceive_date());

            if(searchedData.equals(date))
                return true;

            return false;

        });
    }

    private void getRowsCmbWszystkie(String searchedText, String searchedData) {


        filteredList.setPredicate(obj -> {

            if((searchedText == null || searchedText.isEmpty()) &&
                    (searchedData == null || searchedData.isEmpty() || searchedData.equals("null")))
                return true;

            String lowerCaseValue = searchedText.toLowerCase();

            ordersStory ordersObj = (ordersStory) obj[0];
            Dimiensions dimiensionsObj = (Dimiensions) obj[1];
            Client clientObj = (Client) obj[2];

            String date = null;

            if(dateGate == 1)
                date = String.valueOf(ordersObj.getOrder_date());
            else if(dateGate == 2)
                date = String.valueOf(ordersObj.getReceive_date());



            if(searchedData == null || searchedData.isEmpty() || searchedData.equals("null")){
                if(String.valueOf(ordersObj.getOrder_id()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(ordersObj.getMetrs()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(ordersObj.getMaterials()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimiensionsObj.getFirst_dimension()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimiensionsObj.getSecond_dimension()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimiensionsObj.getThickness()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimiensionsObj.getWeight()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(clientObj.getFirm_name()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(ordersObj.getOrder_id()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(ordersObj.getMetrs()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(ordersObj.getMaterials()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(dimiensionsObj.getFirst_dimension()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(dimiensionsObj.getSecond_dimension()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(dimiensionsObj.getThickness()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(dimiensionsObj.getWeight()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(clientObj.getFirm_name()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
            }
            return false;

        });
    }


    public void getAllRecords(){
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();
        results = entityManager.createQuery("SELECT o, d, c FROM ordersStory as o, Dimiensions as d, Client as c WHERE " +
                "o.dimension = d.dimension_id AND o.client = c.client_id")
                .getResultList();

        entityManager.getTransaction().commit();
        entityManager.close();
        entityManagerFactory.close();
        records = fetchToList(results);
        ObservableList<Object []> data = FXCollections.observableArrayList(results);
        filteredList = new FilteredList<>(data, p -> true);
    }

    public List<String> fetchToList(List<Object[]> obs){

        List<String> temp_records = new ArrayList<>();
        ord.clear();
        cli.clear();
        dim.clear();
        for(Object[] o : obs){
            ordersStory orderObject = (ordersStory) o[0];
            ord.add(orderObject);
            Dimiensions dimensionObject = (Dimiensions) o[1];
            dim.add(dimensionObject);
            Client clientObject = (Client) o[2];
            cli.add(clientObject);
            item = "Firma:  " + clientObject.getFirm_name() + System.lineSeparator() +
                    "Data zamówienia:  " + orderObject.getOrder_date() + System.lineSeparator() +
                    "Wymiary:  " + dimensionObject.getFirst_dimension() + "x" + dimensionObject.getSecond_dimension() +
                    "x" + dimensionObject.getThickness() + System.lineSeparator() +
                    "Waga:  " + dimensionObject.getWeight();
            temp_records.add(item);
        }

        return temp_records;
    }

    public void btnFirmNameClicked(MouseEvent mouseEvent) {
        gate = 4;
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));

    }

    public void btnDimensionsClicked(MouseEvent mouseEvent) {
        gate = 3;
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
    }

    public void btnOrderReceiveDateClicked(MouseEvent mouseEvent) {
        gate = 2;
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
    }

    public void btnOrderDateClicked(MouseEvent mouseEvent) {
        gate = 1;
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
    }

    private void getRecordsWithActualConfigure(String searchedText, String searchedData) {
        if(columnsCmbBox.valueProperty().getValue() == "Wszystkie")
            getRowsCmbWszystkie(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Identyfikator")
            getRecordsIDColumn(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Nazwa firmy")
            getRecordsFirmNameColumn(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Pierwszy bok")
            getRecordsFirstDimensionColumn(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Drugi bok")
            getRecordsSecondDimensionColumn(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Grubość")
            getRecordsThickColumn(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Waga")
            getRecordsWeightColumn(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Ilość metrów")
            getRecordsMetrsColumn(searchedText, searchedData);
        else if(columnsCmbBox.valueProperty().getValue() == "Ilość materiałów")
            getRecordsMaterialsColumn(searchedText, searchedData);
        else if(String.valueOf(columnsCmbBox.getValue()).equals("null")
                || String.valueOf(columnsCmbBox.getValue()).isEmpty()
                ||  String.valueOf(columnsCmbBox.getValue()) == null)
            getRecordsDate(searchedData);


        sortedData = new SortedList<>(filteredList);
        sortGate();

        listToView = fetchToList(sortedData);

        lstOrders.setItems(FXCollections.observableArrayList(listToView));
    }



    public void onBtnModifyClicked(MouseEvent mouseEvent) {

        int position = lstOrders.getSelectionModel().getSelectedIndex();
        ordersStory storyObject = ord.get(position);
        Orders orderObject = new Orders();

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        Dimiensions dim = entityManager.find(Dimiensions.class, storyObject.getDimension().getDimension_id());

        Client cli = entityManager.find(Client.class, storyObject.getClient().getClient_id());

        orderObject.setMetrs(storyObject.getMetrs());

        orderObject.setMaterials(storyObject.getMaterials());

        orderObject.setOrder_date(storyObject.getOrder_date().plusDays(1));

        orderObject.setReceive_date(storyObject.getReceive_date().plusDays(1));

        orderObject.setNote(storyObject.getNote());

        orderObject.setDimension(dim);

        orderObject.setClient(cli);

        orderObject.setOrder_id(null);

        entityManager.persist(orderObject);

        entityManager.remove(entityManager.contains(storyObject) ? storyObject : entityManager.merge(storyObject));

        entityManager.getTransaction().commit();


        entityManager.close();
        entityManagerFactory.close();
        getAllRecords();
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
    }

    public void onBtnDeleteClicked(MouseEvent mouseEvent) {

        int position = lstOrders.getSelectionModel().getSelectedIndex();

        ordersStory orderObject = new ordersStory();
        orderObject = ord.get(position);

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        entityManager.remove(entityManager.contains(orderObject) ? orderObject : entityManager.merge(orderObject));

        entityManager.getTransaction().commit();


        entityManager.close();
        entityManagerFactory.close();
        getAllRecords();

        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));

    }
}
