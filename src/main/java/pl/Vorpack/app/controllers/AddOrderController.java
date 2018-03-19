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
import pl.Vorpack.app.Properties.mainPaneProperty;
import pl.Vorpack.app.domain.Client;
import pl.Vorpack.app.domain.Dimiensions;
import pl.Vorpack.app.domain.Orders;
import pl.Vorpack.app.global_variables.cliVariables;
import pl.Vorpack.app.global_variables.dimVariables;
import pl.Vorpack.app.global_variables.ordVariables;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
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
    private Label labelHeader;

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
    private Label headerLabel;

    @FXML
    private JFXCheckBox checkBox;

    private Double weight, length, materials, firstDim, secondDim, thick;

    private String firmName, note;

    private LocalDate orderDate, createOrderDate;

    private TypedQuery<Client> c;

    private TypedQuery<Dimiensions> d;

    private FilteredList<Dimiensions> filteredList;

    private Set<String> setFirmNames = new HashSet<>();

    private Set<Double> setFirstDims = new HashSet<>();

    private Set<Double> setSecondDims = new HashSet<>();

    private Set<Double> setThicks= new HashSet<>();

    private Set<Double> setWeights = new HashSet<>();
    
    private List<Dimiensions> dimsList = new ArrayList<>();

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

    SuggestionProvider<String> prov_firmsName;

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



        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        d = entityManager.createQuery("SELECT d FROM Dimiensions d", Dimiensions.class);

        ObservableList<Dimiensions> data = FXCollections.observableArrayList(d.getResultList());

        filteredList = new FilteredList<>(data, p -> true);

        fromObjectToList(filteredList);

        c = entityManager.createQuery("SELECT c FROM Client c", Client.class);

        for(Client cli : c.getResultList()){
            setFirmNames.add(cli.getFirm_name());
        }

        entityManager.getTransaction().commit();
        entityManager.close();
        entityManagerFactory.close();

        SuggestionProvider<String> prov_firmsName = SuggestionProvider.create(setFirmNames);
        new AutoCompletionTextFieldBinding<>(txtFirmName, prov_firmsName);

        SuggestionProvider<Double> prov_FirstDims = SuggestionProvider.create(setFirstDims);
        new AutoCompletionTextFieldBinding<>(txtFirstDim, prov_FirstDims);

        SuggestionProvider<Double> prov_SecondDims = SuggestionProvider.create(setSecondDims);
        new AutoCompletionTextFieldBinding<>(txtSecondDim, prov_SecondDims);

        SuggestionProvider<Double> prov_Thick = SuggestionProvider.create(setThicks);
        new AutoCompletionTextFieldBinding<>(txtThick, prov_Thick);

        SuggestionProvider<Double> prov_Weights = SuggestionProvider.create(setWeights);
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
                weight = Double.valueOf(txtWeight.textProperty().getValue());
            }catch(Exception e){
                weight = 0.0;
            }

            try{
                length = Double.valueOf(newValue);
            }catch(Exception e){
                length = 0.0;
            }

            materials = weight * length;
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
                weight = Double.valueOf(newValue);
            }catch(Exception e){
                weight = 0.0;
            }

            try{
                length = Double.valueOf(txtMetrs.textProperty().getValue());
            }catch(Exception e){
                length = 0.0;
            }

            materials = weight * length;


            long factor = (long) Math.pow(10, 2);
            materials = materials * factor;
            long tmp = Math.round(materials);
            materials = (double) tmp/factor;

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


            String value = newValue;
            String value_1 = txtFirstDim.getText();
            String value_2 = txtSecondDim.getText();
            String value_3 = txtThick.getText();


            if(!newValue.isEmpty() && !blockFiltering) {


                filteringDimsTextFields(prov_FirstDims, prov_SecondDims, prov_Thick, prov_Weights, value, value_1, value_2, value_3, 4);

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

            String value = newValue;
            String value_1 = txtFirstDim.getText();
            String value_2 = txtSecondDim.getText();
            String value_3 = txtWeight.getText();


            if(!newValue.isEmpty()  && !blockFiltering) {

                filteringDimsTextFields(prov_FirstDims, prov_SecondDims, prov_Thick, prov_Weights, value, value_1, value_2, value_3, 3);

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
            txtFirmName.setText(cliVariables.getObject().getFirm_name());
            txtFirstDim.setText(dimVariables.getObject().getFirst_dimension().toString());
            txtSecondDim.setText(dimVariables.getObject().getSecond_dimension().toString());
            txtThick.setText(dimVariables.getObject().getThickness().toString());
            txtWeight.setText(dimVariables.getObject().getWeight().toString());
            txtNote.setText(ordVariables.getObject().getNote());

        }
    }

    private void refreshingSuggestions(SuggestionProvider<Double> prov_FirstDims, SuggestionProvider<Double> prov_SecondDims, SuggestionProvider<Double> prov_Thick, SuggestionProvider<Double> prov_Weights) {
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

    private void filteringDimsTextFields(SuggestionProvider<Double> prov_FirstDims, SuggestionProvider<Double> prov_SecondDims, SuggestionProvider<Double> prov_Thick, SuggestionProvider<Double> prov_Weights, 
                                         String newValue, String value_1, String value_2, String value_3,int gate) {


        filteredList.setPredicate(obj -> {
            if(gate == 1){
                objValue = String.valueOf(obj.getFirst_dimension());
                objValue_1 = String.valueOf(obj.getSecond_dimension());
                objValue_2 = String.valueOf(obj.getThickness());
                objValue_3 = String.valueOf(obj.getWeight());

            }
            else if(gate == 2){
                objValue = String.valueOf(obj.getSecond_dimension());
                objValue_1 = String.valueOf(obj.getFirst_dimension());
                objValue_2 = String.valueOf(obj.getThickness());
                objValue_3 = String.valueOf(obj.getWeight());
            }
            else if(gate == 3){
                objValue = String.valueOf(obj.getThickness());
                objValue_1 = String.valueOf(obj.getFirst_dimension());
                objValue_2 = String.valueOf(obj.getSecond_dimension());
                objValue_3 = String.valueOf(obj.getWeight());
            }
            else if(gate == 4){
                objValue = String.valueOf(obj.getWeight());
                objValue_1 = String.valueOf(obj.getFirst_dimension());
                objValue_2 = String.valueOf(obj.getSecond_dimension());
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

            String firstDim_temp = String.valueOf(filteredList.get(0).getFirst_dimension());
            String secDim_temp = String.valueOf(filteredList.get(0).getSecond_dimension());
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
            setFirstDims.add(dims.getFirst_dimension());
            setSecondDims.add(dims.getSecond_dimension());
            setThicks.add(dims.getThickness());
            setWeights.add(dims.getWeight());
        }
    }

    public void btnSaveClicked(MouseEvent mouseEvent) {
        persistRecord();

    }

    public void btnSaveExitClicked(MouseEvent mouseEvent) {
        persistRecord();

        Stage thisStage = (Stage) vBox.getScene().getWindow();

        thisStage.close();

    }

    public Callback<DatePicker, DateCell> restrainDatePicker(LocalDate lcldate){

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
        firstDim = Double.parseDouble(txtFirstDim.textProperty().getValue());
        secondDim =  Double.parseDouble(txtSecondDim.textProperty().getValue());
        thick = Double.parseDouble(txtThick.textProperty().getValue());

        weight  = Double.parseDouble(txtWeight.textProperty().getValue());
        length = Double.parseDouble(txtMetrs.textProperty().getValue());

        //trzeba dodac dzien, bo w bazie zapisuje sie data z dniem do tyłu
        orderDate = dateOrder.getValue().plusDays(1);

        //trzeba dodac dzien, bo w bazie zapisuje sie data z dniem do tyłu
        createOrderDate = dateCreateOrder.getValue().plusDays(1);

        firmName = txtFirmName.textProperty().getValue();

        note = txtNote.textProperty().getValue();

        Orders ord = new Orders();

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myDatabase");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        d = entityManager.createQuery("SELECT d FROM Dimiensions d WHERE first_dimension = :firstDim " +
                "AND second_dimension = :secondDim AND thickness = :thick AND weight = :weight", Dimiensions.class)
                .setParameter("firstDim", firstDim)
                .setParameter("secondDim", secondDim)
                .setParameter("thick", thick)
                .setParameter("weight", weight);

        if(d.getResultList().size() != 0){
            findDim = true;

            for(Dimiensions item : d.getResultList()){
                dim = item;
                break;
            }
        }

        c = entityManager.createQuery("SELECT c FROM Client c WHERE firm_name = :firmName ", Client.class)
                .setParameter("firmName", firmName);

        if(c.getResultList().size() != 0){
            findClient = true;

            for(Client item : c.getResultList()){
                cli = item;
                break;
            }
        }

        if(!findDim){

            dim = new Dimiensions(firstDim, secondDim, thick, weight);
            entityManager.persist(dim);

            d = entityManager.createQuery("SELECT d FROM Dimiensions d WHERE first_dimension = :firstDim " +
                    "AND second_dimension = :secondDim AND thickness = :thick AND weight = :weight", Dimiensions.class)
                    .setParameter("firstDim", firstDim)
                    .setParameter("secondDim", secondDim)
                    .setParameter("thick", thick)
                    .setParameter("weight", weight);
        }

        if(!findClient){

            cli = new Client(firmName);
            entityManager.persist(cli);
            c = entityManager.createQuery("SELECT c FROM Client c WHERE firm_name = :firmName ", Client.class)
                    .setParameter("firmName", firmName);
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
            entityManager.merge(ord);
        }else{

            ord = new Orders(dim, cli, length, materials, createOrderDate, orderDate, note);

            entityManager.persist(ord);

        }
        entityManager.getTransaction().commit();

        entityManager.close();
        entityManagerFactory.close();


    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.BACK_SPACE)
           blockAutoFill = true;
        else
            blockAutoFill = false;


    }
}
