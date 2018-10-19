package pl.Vorpack.app.Service.ServiceImpl;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import pl.Vorpack.app.Constans.ClientColumn;
import pl.Vorpack.app.DatabaseAccess.ClientAccess;
import pl.Vorpack.app.Domain.Clients;
import pl.Vorpack.app.GlobalVariables.ClientVariables;
import pl.Vorpack.app.Service.ClientService;

import java.util.List;

import static pl.Vorpack.app.Constans.ClientColumn.ALL;

public class ClientServiceImpl implements ClientService {

    private ClientAccess access;
    private JFXComboBox<String> columnsCmbBox;

    public ClientServiceImpl(JFXComboBox<String> columnsCmbBox,ClientAccess access) {
        this.columnsCmbBox = columnsCmbBox;
        this.access = access;
    }

    public ClientServiceImpl(ClientAccess access) {
        this.access = access;
    }

    public ClientServiceImpl(){

    }

    @Override
    public void setJFXComboBox(JFXComboBox<String> comboBox){
        this.columnsCmbBox = comboBox;
    }

    @Override
    public FilteredList<Clients> findAll() {
        ObservableList<Clients> data = FXCollections.observableArrayList(access.findAll());
        return new FilteredList<>(data, p -> true);
    }

    @Override
    public FilteredList<Clients> getPreparedData(){
        ObservableList<Clients> data = FXCollections.observableArrayList(ClientVariables.getClientsFromDatabase());
        return new FilteredList<>(data, p -> true);
    }

    @Override
    public Clients findByFirmName(String firmName) {
        return access.findByFirmName(firmName).size() != 0 ? access.findByFirmName(firmName).get(0) : null;
    }

    @Override
    public Clients create(String firmName) {
        List<Clients> clients = access.findByFirmName(firmName);
        if (clients.size() == 0)
            access.create(new Clients(firmName));
        return access.findByFirmName(firmName).get(0);
    }

    @Override
    public void update(Clients client){
        access.update(client);
    }

    @Override
    public void delete(Clients clients) {
        access.delete(clients);
    }

    @Override
    public void filter(String searchedText, FilteredList<Clients> clients) {
        if (columnsCmbBox.getSelectionModel().getSelectedItem() == null ||
                columnsCmbBox.getSelectionModel().getSelectedItem().equals(ALL))
            filterByEveryColumn(searchedText, clients);
        else
            filterByOneColumn(searchedText, clients);
    }

    private void filterByEveryColumn(String searchedText, FilteredList<Clients> clients) {
        clients.setPredicate(obj -> {
            if (searchedText == null || searchedText.isEmpty())
                return true;
            String lowerCaseValue = searchedText.toLowerCase();
            if (String.valueOf(obj.getClient_id()).toLowerCase().contains(lowerCaseValue))
                return true;
            else return String.valueOf(obj.getFirmName()).toLowerCase().contains(lowerCaseValue);
        });
    }

    private void filterByOneColumn(String searchedText, FilteredList<Clients> clients) {
        clients.setPredicate(obj -> {
            if (searchedText == null || searchedText.isEmpty())
                return true;
            String lowerCaseValue = getNonNullLowerCaseValue(searchedText);
            String filterValue = getFilter(columnsCmbBox.getSelectionModel().getSelectedItem(), obj);
            return filterValue.contains(lowerCaseValue);
        });
    }

    private String getNonNullLowerCaseValue(String string) {
        if (string == null)
            return "";

        return string.toLowerCase();
    }

    private String getFilter(String column, Clients obj) {
        switch (column) {
            case ClientColumn.ID:
                return String.valueOf(obj.getClient_id()).toLowerCase();
            case ClientColumn.FIRM_NAME:
                return String.valueOf(obj.getFirmName()).toLowerCase();
            default:
                return "";
        }
    }
}
