package pl.Vorpack.app.Service;

import javafx.collections.transformation.FilteredList;
import pl.Vorpack.app.Domain.Clients;

public interface ClientService {
    FilteredList<Clients>  findAll();

    FilteredList<Clients> getPreparedData();

    Clients findByFirmName(String firmName);

    Clients create(String firmName);

    void update(Clients client);

    void delete(Clients clients);

    void filter(String filter, String searchedText, FilteredList<Clients> clients);
}
