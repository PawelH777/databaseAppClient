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
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import pl.Vorpack.app.domain.Client;
import pl.Vorpack.app.domain.Dimiensions;
import pl.Vorpack.app.domain.Orders;
import pl.Vorpack.app.domain.ordersStory;
import pl.Vorpack.app.global_variables.cliVariables;
import pl.Vorpack.app.global_variables.dimVariables;
import pl.Vorpack.app.global_variables.ordVariables;
import pl.Vorpack.app.global_variables.userData;
import pl.Vorpack.app.infoAlerts;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
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
    private JFXButton btnRefresh;

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

    private List<ordersStory> results  = new ArrayList<>();

    private List<String> records = new ArrayList<String>();

    private List<Object[]> objectsList = new ArrayList<>();

    private List<ordersStory> ord = new ArrayList<>();

    private List<Client> cli = new ArrayList<>();

    private List<Dimiensions> dim = new ArrayList<>();

    private String item;

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
                labelNameValue.setText(String.valueOf(c.getFirmName()));
                labelOrderDateValue.setText(String.valueOf(o.getOrder_date()));
                labelReceiverDateValue.setText(String.valueOf(o.getReceive_date()));
                labelMetrsValue.setText(String.valueOf(o.getMetrs()));
                labelMaterialsValue.setText(String.valueOf(o.getMaterials()));
                labelFirstDimValue.setText(String.valueOf(d.getFirstDimension()));
                labelSecDimValue.setText(String.valueOf(d.getSecondDimension()));
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

            String name_1 = clientObject.getFirmName().toLowerCase();
            String name_2 = clientObject_2.getFirmName().toLowerCase();


            return name_1.compareTo(name_2);
        });
    }

    private void setComparatorDimensions() {
        sortedData.setComparator((o1, o2) ->{
            Dimiensions dimiensionObject = (Dimiensions) o1[1];
            Dimiensions dimiensionObject_2 = (Dimiensions) o2[1];

            BigDecimal firstDim_1 = dimiensionObject.getFirstDimension();
            BigDecimal firstDim_2 = dimiensionObject_2.getFirstDimension();

            BigDecimal secondDim_1 = dimiensionObject.getSecondDimension();
            BigDecimal secondDim_2 = dimiensionObject_2.getSecondDimension();

            BigDecimal thick_1 = dimiensionObject.getThickness();
            BigDecimal thick_2 = dimiensionObject_2.getThickness();

            BigDecimal weight_1 = dimiensionObject.getWeight();
            BigDecimal weight_2 = dimiensionObject_2.getWeight();

            if(firstDim_1.compareTo(firstDim_2) < 0)
                return -1;
            else if(firstDim_1.compareTo(firstDim_2) > 0)
                return 1;

            if(secondDim_1.compareTo(secondDim_2) < 0)
                return -1;
            else if(secondDim_1.compareTo(secondDim_2) > 0)
                return 1;

            if(thick_1.compareTo(thick_2) < 0)
                return -1;
            else if(thick_1.compareTo(thick_2) > 0)
                return 1;

            if(weight_1.compareTo(weight_2) < 0)
                return -1;
            else if(weight_1.compareTo(weight_2) > 0)
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
                if(String.valueOf(cliObj.getFirmName()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(cliObj.getFirmName()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
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
                if(String.valueOf(dimObj.getFirstDimension()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(dimObj.getFirstDimension()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
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
                if(String.valueOf(dimObj.getSecondDimension()).toLowerCase().contains(lowerCaseValue))
                    return true;
            } else {
                if(String.valueOf(dimObj.getSecondDimension()).toLowerCase().contains(lowerCaseValue) && searchedData.equals(date))
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
                else if(String.valueOf(dimiensionsObj.getFirstDimension()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimiensionsObj.getSecondDimension()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimiensionsObj.getThickness()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(dimiensionsObj.getWeight()).toLowerCase().contains(lowerCaseValue))
                    return true;
                else if(String.valueOf(clientObj.getFirmName()).toLowerCase().contains(lowerCaseValue))
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
                else if(String.valueOf(dimiensionsObj.getFirstDimension()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(dimiensionsObj.getSecondDimension()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(dimiensionsObj.getThickness()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(dimiensionsObj.getWeight()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
                else if(String.valueOf(clientObj.getFirmName()).toLowerCase().contains(lowerCaseValue)
                        && searchedData.equals(date))
                    return true;
            }
            return false;

        });
    }


    public void getAllRecords(){

        Dimiensions dimObject;
        Client cliObject;

        objectsList = new ArrayList<>();

        try{

            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                    .nonPreemptive()
                    .credentials(userData.getName(), userData.getPassword())
                    .build();

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(feature);

            javax.ws.rs.client.Client clientBuilder = ClientBuilder.newClient(clientConfig);

            String URI = "http://localhost:8080/orderstory";

            Response response = clientBuilder.target(URI).request(MediaType.APPLICATION_JSON_TYPE)
                    .get();

            results = response.readEntity(new GenericType<List<ordersStory>>(){});

            clientBuilder.close();

            for(ordersStory o  : results){
                dimObject = o.getDimension();
                cliObject = o.getClient();
                Object[] obj = new Object[]{o, dimObject, cliObject};
                objectsList.add(obj);
            }

            records = fetchToList(objectsList);
            ObservableList<Object[]> data = FXCollections.observableArrayList(objectsList);
            filteredList = new FilteredList<>(data, p -> true);
        }catch (Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }
    }

    private List<String> fetchToList(List<Object[]> obs){

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
            item = "Firma:  " + clientObject.getFirmName() + System.lineSeparator() +
                    "Data zamówienia:  " + orderObject.getOrder_date() + System.lineSeparator() +
                    "Wymiary:  " + dimensionObject.getFirstDimension() + "x" + dimensionObject.getSecondDimension() +
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


        String dim_id = String.valueOf(storyObject.getDimension().getDimension_id());

        String cli_id = String.valueOf(storyObject.getClient().getClient_id());

        String storyOrd_id = String.valueOf(storyObject.getOrder_id());

        String URI = "http://localhost:8080/dims/dim/id/";


        try{
            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                    .nonPreemptive()
                    .credentials(userData.getName(), userData.getPassword())
                    .build();

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(feature);

            javax.ws.rs.client.Client clientBuilder = ClientBuilder.newClient(clientConfig);

            Response response = clientBuilder.target(URI).path(dim_id).request(MediaType.APPLICATION_JSON_TYPE)
                    .get();

            Dimiensions dim = response.readEntity(Dimiensions.class);

            URI = "http://localhost:8080/clients/client/id";

            response = clientBuilder.target(URI).path(cli_id).request(MediaType.APPLICATION_JSON_TYPE)
                    .get();

            Client cli = response.readEntity(Client.class);

            orderObject.setMetrs(storyObject.getMetrs());

            orderObject.setMaterials(storyObject.getMaterials());

            orderObject.setOrder_date(storyObject.getOrder_date().plusDays(1));

            orderObject.setReceive_date(storyObject.getReceive_date().plusDays(1));

            orderObject.setNote(storyObject.getNote());

            orderObject.setDimension(dim);

            orderObject.setClient(cli);

            orderObject.setOrder_id(null);

            URI = "http://localhost:8080/orders/createorder";

            response = clientBuilder.target(URI).request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(orderObject, MediaType.APPLICATION_JSON_TYPE));

            URI = "http://localhost:8080/orderstory/order/delete";

            response = clientBuilder.target(URI).path(storyOrd_id).request(MediaType.APPLICATION_JSON_TYPE)
                    .delete();

            clientBuilder.close();
        }
        catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }

        getAllRecords();
        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
    }

    public void onBtnDeleteClicked(MouseEvent mouseEvent) {

        int position = lstOrders.getSelectionModel().getSelectedIndex();

        ordersStory orderObject;
        orderObject = ord.get(position);

        String storyOrd_id = String.valueOf(orderObject.getOrder_id());

        String URI = "http://localhost:8080/orderstory/order/delete";


        try{
            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                    .nonPreemptive()
                    .credentials(userData.getName(), userData.getPassword())
                    .build();

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(feature);

            javax.ws.rs.client.Client clientBuilder = ClientBuilder.newClient(clientConfig);

            Response response = clientBuilder.target(URI).path(storyOrd_id).request(MediaType.APPLICATION_JSON_TYPE)
                    .delete();

            clientBuilder.close();
        }
        catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }

        getAllRecords();

        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));

    }

    public void btnRefreshClicked(){

        getAllRecords();

        getRecordsWithActualConfigure(txtSearch.textProperty().getValue(), String.valueOf(orderDatePicker.valueProperty().getValue()));
        btnDelete.disableProperty().setValue(true);
        btnRecover.disableProperty().setValue(true);
    }
}
