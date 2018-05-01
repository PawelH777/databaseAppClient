package pl.Vorpack.app.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import pl.Vorpack.app.Properties.mainPaneProperty;
import pl.Vorpack.app.domain.Client;
import pl.Vorpack.app.domain.Dimiensions;
import pl.Vorpack.app.domain.Orders;
import pl.Vorpack.app.global_variables.cliVariables;
import pl.Vorpack.app.global_variables.dimVariables;
import pl.Vorpack.app.global_variables.ordVariables;
import pl.Vorpack.app.global_variables.userData;
import pl.Vorpack.app.infoAlerts;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Created by Paweł on 2018-02-23.
 */
public class AddOrderController {


    @FXML
    private VBox vBox;

    @FXML
    private JFXTextField txtMetrs;

    @FXML
    private JFXTextField txtMaterials;

    @FXML
    private JFXTextField txtFirmName;

    @FXML
    private JFXTextField txtFirstDim;

    @FXML
    private JFXTextField txtSecondDim;

    @FXML
    private JFXTextField txtThick;

    @FXML
    private JFXTextField txtWeight;

    @FXML
    private DatePicker dateOrder;

    @FXML
    private DatePicker dateCreateOrder;

    @FXML
    private JFXTextArea txtNote;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXButton btnSaveExit;

    @FXML
    private JFXCheckBox checkBox;

    @FXML
    private Label statusLabel;

    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal materials;

    private LocalDate createOrderDate;

    private List<Client> c = new ArrayList<>();

    private List<Dimiensions> d = new ArrayList<>();

    private FilteredList<Dimiensions> filteredList;

    private Set<String> setFirmNames = new HashSet<>();

    private Set<BigDecimal> setFirstDims = new HashSet<>();

    private Set<BigDecimal> setSecondDims = new HashSet<>();

    private Set<BigDecimal> setThicks= new HashSet<>();

    private Set<BigDecimal> setWeights = new HashSet<>();

    private Dimiensions dim;

    private Client cli;

    private Boolean findDim = false, findClient = false;

    private mainPaneProperty ordProperty = new mainPaneProperty();

    private boolean blockAutoFill = false;

    private boolean blockFiltering = false;

    private String objValue = null;
    private String objValue_1 = null;
    private String objValue_2 = null;
    private String objValue_3 = null;

    private ObservableList<Dimiensions> data;

    private Boolean isModify = false;

    @FXML
    public void initialize(){
        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        TextFormatter formatter2 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        TextFormatter formatter3 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        TextFormatter formatter4 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        TextFormatter formatter5 = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        txtFirstDim.setTextFormatter(formatter);
        txtSecondDim.setTextFormatter(formatter2);
        txtThick.setTextFormatter(formatter3);
        txtWeight.setTextFormatter(formatter4);
        txtMetrs.setTextFormatter(formatter5);

        btnSave.disableProperty().bindBidirectional(ordProperty.disableBtnProocedProperty());
        btnSaveExit.disableProperty().bindBidirectional(ordProperty.disableBtnProocedandExitProperty());

        dateCreateOrder.setValue(LocalDate.now());

        dateOrder.setValue(dateCreateOrder.getValue().plusDays(1));

        final Callback<DatePicker, DateCell>  dayCellFactory = restrainDatePicker(LocalDate.now());
        dateOrder.setDayCellFactory(dayCellFactory);

        dateCreateOrder.valueProperty().addListener((obs, oldValue, newValue) ->{
            createOrderDate = newValue;

            if(newValue.isAfter(dateOrder.getValue())){
                dateOrder.setValue(newValue.plusDays(1));
            }

            final Callback<DatePicker, DateCell> dayCellFactory_2 = restrainDatePicker(newValue);
            dateOrder.setDayCellFactory(dayCellFactory_2);

            if(newValue != null && dateOrder.valueProperty().getValue() != null
                    && !txtMetrs.textProperty().getValue().isEmpty() && !txtFirmName.textProperty().getValue().isEmpty()
                    && !txtFirstDim.textProperty().getValue().isEmpty() && !txtSecondDim.textProperty().getValue().isEmpty()
                    && !txtThick.textProperty().getValue().isEmpty() && !txtWeight.textProperty().getValue().isEmpty()){

                ordProperty.setDisableBtnProoced(false);
                ordProperty.setDisableBtnProocedandExit(false);
            }
            else{
                ordProperty.setDisableBtnProoced(true);
                ordProperty.setDisableBtnProocedandExit(true);
            }

        });

        dateOrder.valueProperty().addListener((obs, oldValue, newValue) ->{

            if(newValue != null && dateCreateOrder.valueProperty().getValue() != null
                    && !txtMetrs.textProperty().getValue().isEmpty() && !txtFirmName.textProperty().getValue().isEmpty()
                    && !txtFirstDim.textProperty().getValue().isEmpty() && !txtSecondDim.textProperty().getValue().isEmpty()
                    && !txtThick.textProperty().getValue().isEmpty() && !txtWeight.textProperty().getValue().isEmpty()){

                ordProperty.setDisableBtnProoced(false);
                ordProperty.setDisableBtnProocedandExit(false);
            }else{
                ordProperty.setDisableBtnProoced(true);
                ordProperty.setDisableBtnProocedandExit(true);
            }
        });


        try{
            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                    .nonPreemptive()
                    .credentials(userData.getName(), userData.getPassword())
                    .build();

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(feature);

            javax.ws.rs.client.Client clientBuilder = ClientBuilder.newClient(clientConfig);

            String URI = "http://localhost:8080/dims";

            Response response = clientBuilder.target(URI).request(MediaType.APPLICATION_JSON_TYPE).get();

            d = response.readEntity(new GenericType<ArrayList<Dimiensions>>(){});

            data = FXCollections.observableArrayList(d);

            filteredList = new FilteredList<>(data, p -> true);

            fromObjectToList(filteredList);

            URI = "http://localhost:8080/clients";

            response = clientBuilder.target(URI).request(MediaType.APPLICATION_JSON_TYPE).get();

            c = response.readEntity(new GenericType<List<Client>>(){});

            clientBuilder.close();
        }catch(Exception e){
            e.printStackTrace();
            infoAlerts.generalAlert();
        }


        for(Client cli : c){
            setFirmNames.add(cli.getFirmName());
        }

        SuggestionProvider<String> prov_firmsName = SuggestionProvider.create(setFirmNames);
        new AutoCompletionTextFieldBinding<>(txtFirmName, prov_firmsName);

        SuggestionProvider<BigDecimal> prov_FirstDims = SuggestionProvider.create(setFirstDims);
        new AutoCompletionTextFieldBinding<>(txtFirstDim, prov_FirstDims);

        SuggestionProvider<BigDecimal> prov_SecondDims = SuggestionProvider.create(setSecondDims);
        new AutoCompletionTextFieldBinding<>(txtSecondDim, prov_SecondDims);

        SuggestionProvider<BigDecimal> prov_Thick = SuggestionProvider.create(setThicks);
        new AutoCompletionTextFieldBinding<>(txtThick, prov_Thick);

        SuggestionProvider<BigDecimal> prov_Weights = SuggestionProvider.create(setWeights);
        new AutoCompletionTextFieldBinding<>(txtWeight, prov_Weights);


        checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> {

            if(newValue)
                blockFiltering = false;
            else{
                blockFiltering = true;
                filteredList = new FilteredList<>(data, p -> true);
                fromObjectToList(filteredList);
                prov_FirstDims.clearSuggestions();
                prov_FirstDims.addPossibleSuggestions(setFirstDims);

                prov_SecondDims.clearSuggestions();
                prov_SecondDims.addPossibleSuggestions(setSecondDims);

                prov_Thick.clearSuggestions();
                prov_Thick.addPossibleSuggestions(setThicks);

                prov_Weights.clearSuggestions();
                prov_Weights.addPossibleSuggestions(setWeights);
            }
        });

        txtMetrs.textProperty().addListener((ObservableValue<? extends String> obs, String oldValue, String newValue) -> {

            try{
                weight = BigDecimal.valueOf(Double.valueOf(txtWeight.textProperty().getValue()));
            }catch(Exception e){
                weight = BigDecimal.ZERO;
            }

            try{
                length = BigDecimal.valueOf(Double.valueOf(newValue));
            }catch(Exception e){
                length = BigDecimal.ZERO;
            }

            materials = weight.multiply(length).stripTrailingZeros();

            if (materials.scale()<0)
                materials= materials.setScale(0);

            txtMaterials.textProperty().setValue("" + materials);

            if(dateOrder.valueProperty().getValue() != null && dateCreateOrder.valueProperty().getValue() != null
                    && !newValue.isEmpty() && !txtFirmName.textProperty().getValue().isEmpty()
                    && !txtFirstDim.textProperty().getValue().isEmpty() && !txtSecondDim.textProperty().getValue().isEmpty()
                    && !txtThick.textProperty().getValue().isEmpty() && !txtWeight.textProperty().getValue().isEmpty()){

                ordProperty.setDisableBtnProoced(false);
                ordProperty.setDisableBtnProocedandExit(false);
            }else{
                ordProperty.setDisableBtnProoced(true);
                ordProperty.setDisableBtnProocedandExit(true);
            }
        });

        txtWeight.textProperty().addListener((obs, oldValue, newValue) -> {

            try{
                weight = BigDecimal.valueOf(Double.valueOf(newValue));
            }catch(Exception e){
                weight = BigDecimal.ZERO;
            }

            try{
                length = BigDecimal.valueOf(Double.valueOf(txtMetrs.textProperty().getValue()));
            }catch(Exception e){
                length = BigDecimal.ZERO;
            }

            materials = weight.multiply(length).stripTrailingZeros();

            if (materials.scale()<0)
                materials= materials.setScale(0);

            txtMaterials.textProperty().setValue("" + materials);

            if(dateOrder.valueProperty().getValue() != null && dateCreateOrder.valueProperty().getValue() != null
                    && !newValue.isEmpty() && !txtFirmName.textProperty().getValue().isEmpty()
                    && !txtFirstDim.textProperty().getValue().isEmpty() && !txtSecondDim.textProperty().getValue().isEmpty()
                    && !txtThick.textProperty().getValue().isEmpty() && !txtMetrs.textProperty().getValue().isEmpty()){

                ordProperty.setDisableBtnProoced(false);
                ordProperty.setDisableBtnProocedandExit(false);
            }
            else{
                ordProperty.setDisableBtnProoced(true);
                ordProperty.setDisableBtnProocedandExit(true);
            }


            String value_1 = txtFirstDim.getText();
            String value_2 = txtSecondDim.getText();
            String value_3 = txtThick.getText();


            if(!newValue.isEmpty() && !blockFiltering) {


                filteringDimsTextFields(prov_FirstDims, prov_SecondDims, prov_Thick, prov_Weights, newValue, value_1, value_2, value_3, 4);

                fromObjectToList(filteredList);

                refreshingSuggestions(prov_FirstDims, prov_SecondDims, prov_Thick, prov_Weights);
            }

        });

        txtFirmName.textProperty().addListener((obs, oldValue, newValue)->{

            if(dateOrder.valueProperty().getValue() != null && dateCreateOrder.valueProperty().getValue() != null
                    && !newValue.isEmpty() && !txtWeight.textProperty().getValue().isEmpty()
                    && !txtFirstDim.textProperty().getValue().isEmpty() && !txtSecondDim.textProperty().getValue().isEmpty()
                    && !txtThick.textProperty().getValue().isEmpty() && !txtMetrs.textProperty().getValue().isEmpty()){

                ordProperty.setDisableBtnProoced(false);
                ordProperty.setDisableBtnProocedandExit(false);
            }
            else{
                ordProperty.setDisableBtnProoced(true);
                ordProperty.setDisableBtnProocedandExit(true);
            }
        });

        txtFirstDim.textProperty().addListener((obs, oldValue, newValue)->{

            if(dateOrder.valueProperty().getValue() != null && dateCreateOrder.valueProperty().getValue() != null
                    && !newValue.isEmpty() && !txtWeight.textProperty().getValue().isEmpty()
                    && !txtFirmName.textProperty().getValue().isEmpty() && !txtSecondDim.textProperty().getValue().isEmpty()
                    && !txtThick.textProperty().getValue().isEmpty() && !txtMetrs.textProperty().getValue().isEmpty()){

                ordProperty.setDisableBtnProoced(false);
                ordProperty.setDisableBtnProocedandExit(false);
            }
            else{
                ordProperty.setDisableBtnProoced(true);
                ordProperty.setDisableBtnProocedandExit(true);
            }

            String value_1 = txtSecondDim.getText();
            String value_2 = txtThick.getText();
            String value_3 = txtWeight.getText();


           if(!newValue.isEmpty() && !blockFiltering) {


               filteringDimsTextFields(prov_FirstDims, prov_SecondDims, prov_Thick, prov_Weights, newValue, value_1, value_2, value_3, 1);

               fromObjectToList(filteredList);

               refreshingSuggestions(prov_FirstDims, prov_SecondDims, prov_Thick, prov_Weights);

           }



        });

        txtSecondDim.textProperty().addListener((obs, oldValue, newValue)->{

            if(dateOrder.valueProperty().getValue() != null && dateCreateOrder.valueProperty().getValue() != null
                    && !newValue.isEmpty() && !txtWeight.textProperty().getValue().isEmpty()
                    && !txtFirstDim.textProperty().getValue().isEmpty() && !txtFirmName.textProperty().getValue().isEmpty()
                    && !txtThick.textProperty().getValue().isEmpty() && !txtMetrs.textProperty().getValue().isEmpty()){

                ordProperty.setDisableBtnProoced(false);
                ordProperty.setDisableBtnProocedandExit(false);
            }
            else{
                ordProperty.setDisableBtnProoced(true);
                ordProperty.setDisableBtnProocedandExit(true);
            }


            String value = newValue;
            String value_1 = txtFirstDim.getText();
            String value_2 = txtThick.getText();
            String value_3 = txtWeight.getText();


            if(!newValue.isEmpty() && !blockFiltering) {


                filteringDimsTextFields(prov_FirstDims, prov_SecondDims, prov_Thick, prov_Weights, value, value_1, value_2, value_3, 2);

                fromObjectToList(filteredList);

                refreshingSuggestions(prov_FirstDims, prov_SecondDims, prov_Thick, prov_Weights);

            }
        });

        txtThick.textProperty().addListener((obs, oldValue, newValue)->{

            if(dateOrder.valueProperty().getValue() != null && dateCreateOrder.valueProperty().getValue() != null
                    && !newValue.isEmpty() && !txtWeight.textProperty().getValue().isEmpty()
                    && !txtFirstDim.textProperty().getValue().isEmpty() && !txtSecondDim.textProperty().getValue().isEmpty()
                    && !txtFirmName.textProperty().getValue().isEmpty() && !txtMetrs.textProperty().getValue().isEmpty()){

                ordProperty.setDisableBtnProoced(false);
                ordProperty.setDisableBtnProocedandExit(false);
            }
            else{
                ordProperty.setDisableBtnProoced(true);
                ordProperty.setDisableBtnProocedandExit(true);
            }

            String value_1 = txtFirstDim.getText();
            String value_2 = txtSecondDim.getText();
            String value_3 = txtWeight.getText();


            if(!newValue.isEmpty()  && !blockFiltering) {

                filteringDimsTextFields(prov_FirstDims, prov_SecondDims, prov_Thick, prov_Weights, newValue, value_1, value_2, value_3, 3);

                fromObjectToList(filteredList);

                refreshingSuggestions(prov_FirstDims, prov_SecondDims, prov_Thick, prov_Weights);
            }
        });

        if(ordVariables.getObject() != null && cliVariables.getObject() != null
                && dimVariables.getObject() != null){

            btnSave.setVisible(false);
            btnSaveExit.setText("Zmień");

            txtMetrs.setText(ordVariables.getObject().getMetrs().toString());
            txtMaterials.setText(ordVariables.getObject().getMaterials().toString());
            dateOrder.setValue(ordVariables.getObject().getOrder_date());
            dateCreateOrder.setValue(ordVariables.getObject().getReceive_date());
            txtFirmName.setText(cliVariables.getObject().getFirmName());
            txtFirstDim.setText(dimVariables.getObject().getFirstDimension().toString());
            txtSecondDim.setText(dimVariables.getObject().getSecondDimension().toString());
            txtThick.setText(dimVariables.getObject().getThickness().toString());
            txtWeight.setText(dimVariables.getObject().getWeight().toString());
            txtNote.setText(ordVariables.getObject().getNote());

            isModify = true;
        }
    }

    private void refreshingSuggestions(SuggestionProvider<BigDecimal> prov_FirstDims, SuggestionProvider<BigDecimal> prov_SecondDims, SuggestionProvider<BigDecimal> prov_Thick, SuggestionProvider<BigDecimal> prov_Weights) {
        if(setSecondDims.size() > 1 || setFirstDims.size() > 1 || setWeights.size() >  1 || setThicks.size() > 1){

            prov_FirstDims.clearSuggestions();
            prov_FirstDims.addPossibleSuggestions(setFirstDims);

            prov_SecondDims.clearSuggestions();
            prov_SecondDims.addPossibleSuggestions(setSecondDims);

            prov_Weights.clearSuggestions();
            prov_Weights.addPossibleSuggestions(setWeights);

            prov_Thick.clearSuggestions();
            prov_Thick.addPossibleSuggestions(setThicks);

        }
    }

    private void filteringDimsTextFields(SuggestionProvider<BigDecimal> prov_FirstDims, SuggestionProvider<BigDecimal> prov_SecondDims, SuggestionProvider<BigDecimal> prov_Thick, SuggestionProvider<BigDecimal> prov_Weights,
                                         String newValue, String value_1, String value_2, String value_3,int gate) {


        filteredList.setPredicate(obj -> {
            if(gate == 1){
                objValue = String.valueOf(obj.getFirstDimension());
                objValue_1 = String.valueOf(obj.getSecondDimension());
                objValue_2 = String.valueOf(obj.getThickness());
                objValue_3 = String.valueOf(obj.getWeight());

            }
            else if(gate == 2){
                objValue = String.valueOf(obj.getSecondDimension());
                objValue_1 = String.valueOf(obj.getFirstDimension());
                objValue_2 = String.valueOf(obj.getThickness());
                objValue_3 = String.valueOf(obj.getWeight());
            }
            else if(gate == 3){
                objValue = String.valueOf(obj.getThickness());
                objValue_1 = String.valueOf(obj.getFirstDimension());
                objValue_2 = String.valueOf(obj.getSecondDimension());
                objValue_3 = String.valueOf(obj.getWeight());
            }
            else if(gate == 4){
                objValue = String.valueOf(obj.getWeight());
                objValue_1 = String.valueOf(obj.getFirstDimension());
                objValue_2 = String.valueOf(obj.getSecondDimension());
                objValue_3 = String.valueOf(obj.getThickness());
            }


            if(value_1.isEmpty() && value_2.isEmpty() && value_3.isEmpty()) {
                if(objValue.contains(newValue))
                    return true;
            }
            else if(!value_1.isEmpty() && value_2.isEmpty() && value_3.isEmpty()){
                if(objValue.contains(newValue) && objValue_1.contains(value_1))
                    return true;
            }
            else if(!value_2.isEmpty() && value_1.isEmpty() && value_3.isEmpty()){
                if(objValue.contains(newValue) && objValue_2.contains(value_2))
                    return true;
            }
            else if(!value_3.isEmpty()&& value_2.isEmpty() && value_1.isEmpty()){
                if(objValue.contains(newValue) && objValue_3.contains(value_3))
                    return true;
            }
            else if(!value_1.isEmpty() && !value_2.isEmpty() && value_3.isEmpty()){
                if(objValue.contains(newValue) && objValue_1.contains(value_1)
                        && objValue_2.contains(value_2))
                    return true;
            }
            else if(!value_1.isEmpty() && !value_3.isEmpty() && value_2.isEmpty()){
                if(objValue.contains(newValue) && objValue_1.contains(value_1)
                        && objValue_3.contains(value_3))
                    return true;
            }
            else if(!value_2.isEmpty() && !value_3.isEmpty() && value_1.isEmpty()){
                if(objValue.contains(newValue) && objValue_2.contains(value_2)
                        && objValue_3.contains(value_3))
                    return true;
            }
            else if(!value_2.isEmpty() && !value_3.isEmpty() && !value_1.isEmpty()){
                if(objValue.contains(newValue)
                        && objValue_1.contains(value_1)
                        && objValue_2.contains(value_2)
                        && objValue_3.contains(value_3))
                    return true;
            }

            return false;
        });


        if(filteredList.size() == 1 && !blockAutoFill){

            String firstDim_temp = String.valueOf(filteredList.get(0).getFirstDimension());
            String secDim_temp = String.valueOf(filteredList.get(0).getSecondDimension());
            String thick_temp = String.valueOf(filteredList.get(0).getThickness());
            String weight_temp = String.valueOf(filteredList.get(0).getWeight());

            txtFirstDim.textProperty().setValue(firstDim_temp);
            txtSecondDim.textProperty().setValue(secDim_temp);
            txtThick.textProperty().setValue(thick_temp);
            txtWeight.textProperty().setValue(weight_temp);

            prov_FirstDims.clearSuggestions();
            prov_SecondDims.clearSuggestions();
            prov_Thick.clearSuggestions();
            prov_Weights.clearSuggestions();
        }
    }


    private void fromObjectToList(List<Dimiensions> dimiensionsList) {
        setFirstDims.clear();
        setSecondDims.clear();
        setThicks.clear();
        setWeights.clear();


        for(Dimiensions dims : dimiensionsList){
            setFirstDims.add(dims.getFirstDimension());
            setSecondDims.add(dims.getSecondDimension());
            setThicks.add(dims.getThickness());
            setWeights.add(dims.getWeight());
        }
    }

    public void btnSaveClicked(MouseEvent mouseEvent) {
        persistRecord();
        statusLabel.setText("Rekord został dodany");
    }

    public void btnSaveExitClicked(MouseEvent mouseEvent) {
        persistRecord();

        if (isModify)
            infoAlerts.addRecord("zmieniony");
        else if(!isModify)
            infoAlerts.addRecord("dodany");
        Stage thisStage = (Stage) vBox.getScene().getWindow();

        thisStage.close();

    }

    private Callback<DatePicker, DateCell> restrainDatePicker(LocalDate lcldate){

        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(DatePicker param) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item.isBefore(
                                        lcldate.plusDays(1))
                                        ) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };
        return dayCellFactory;
    }

    public void persistRecord(){
        List<Dimiensions> dimsList = new ArrayList<>();
        List<Client> cliList = new ArrayList<>();

        BigDecimal firstDim = BigDecimal.valueOf(Double.parseDouble(txtFirstDim.textProperty().getValue()));
        BigDecimal secondDim = BigDecimal.valueOf(Double.parseDouble(txtSecondDim.textProperty().getValue()));
        BigDecimal thick = BigDecimal.valueOf(Double.parseDouble(txtThick.textProperty().getValue()));

        weight  = BigDecimal.valueOf(Double.parseDouble(txtWeight.textProperty().getValue()));
        length = BigDecimal.valueOf(Double.parseDouble(txtMetrs.textProperty().getValue()));

            //trzeba dodac dzien, bo w bazie zapisuje sie data z dniem do tyłu
        LocalDate orderDate = dateOrder.getValue();

            //trzeba dodac dzien, bo w bazie zapisuje sie data z dniem do tyłu
            createOrderDate = dateCreateOrder.getValue();

        String firmName = txtFirmName.textProperty().getValue();

        String note = txtNote.textProperty().getValue();

        Orders ord = new Orders();
        dim = new Dimiensions();

        dim.setFirstDimension(firstDim);
        dim.setSecondDimension(secondDim);
        dim.setThickness(thick);
        dim.setWeight(weight);

        try{
            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                    .nonPreemptive()
                    .credentials(userData.getName(), userData.getPassword())
                    .build();

            ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(feature);

            javax.ws.rs.client.Client clientBuilder  = ClientBuilder.newClient(clientConfig);

            String URI = "http://localhost:8080/dims/dim/find";

            Response response = clientBuilder.target(URI).request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(dim, MediaType.APPLICATION_JSON_TYPE));

            dimsList = response.readEntity(new GenericType<List<Dimiensions>>(){});
            if(dimsList.size() != 0){
                findDim = true;

                for(Dimiensions item : dimsList){
                    dim = item;
                    break;
                }
            }

            cli = new Client();

            cli.setFirmName(firmName);

            URI = "http://localhost:8080/clients/client/firmname";

            response = clientBuilder.target(URI).request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(cli, MediaType.APPLICATION_JSON_TYPE));

            cliList = response.readEntity(new GenericType<List<Client>>(){});

            if(cliList.size() != 0){
                findClient = true;

                for(Client item : cliList){
                    cli = item;
                    break;
                }
            }

            if(!findDim){

                dim = new Dimiensions(firstDim, secondDim, thick, weight);
                URI  = "http://localhost:8080/dims/createdim";

                response = clientBuilder.target(URI).request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.entity(dim, MediaType.APPLICATION_JSON_TYPE));


                URI = "http://localhost:8080/dims/dim/find";

                response = clientBuilder.target(URI).request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.entity(dim, MediaType.APPLICATION_JSON_TYPE));

                dimsList = response.readEntity(new GenericType<List<Dimiensions>>(){});

                dim = dimsList.get(0);

            }

            if(!findClient){

                cli = new Client(firmName);
                URI  = "http://localhost:8080/clients/createclient";

                response = clientBuilder.target(URI).request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.entity(cli, MediaType.APPLICATION_JSON_TYPE));

                URI = "http://localhost:8080/clients/client/firmname";

                response = clientBuilder.target(URI).request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.entity(cli, MediaType.APPLICATION_JSON_TYPE));

                cliList = response.readEntity(new GenericType<List<Client>>(){});

                cli = cliList.get(0);
            }

            if(ordVariables.getObject() != null && cliVariables.getObject() != null
                    && dimVariables.getObject() != null){
                ord.setOrder_id(ordVariables.getObject().getOrder_id());
                ord.setClient(cli);
                ord.setDimension(dim);
                ord.setMaterials(materials);
                ord.setMetrs(length);
                ord.setNote(note);
                ord.setOrder_date(orderDate);
                ord.setReceive_date(createOrderDate);

                String ord_id = String.valueOf(ordVariables.getObject().getOrder_id());

                URI  = "http://localhost:8080/orders/order/update";

                response = clientBuilder.target(URI).path(ord_id).request(MediaType.APPLICATION_JSON_TYPE)
                        .put(Entity.entity(ord, MediaType.APPLICATION_JSON_TYPE));
            }else{

                ord = new Orders(dim, cli, length, materials, createOrderDate, orderDate, note);

                URI  = "http://localhost:8080/orders/createorder";

                response = clientBuilder.target(URI).request(MediaType.APPLICATION_JSON_TYPE)
                        .post(Entity.entity(ord, MediaType.APPLICATION_JSON_TYPE));
            }

            clientBuilder.close();
        }catch(Exception e){
           e.printStackTrace();
           infoAlerts.generalAlert();
        }

    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.BACK_SPACE)
           blockAutoFill = true;
        else
            blockAutoFill = false;


    }
}
