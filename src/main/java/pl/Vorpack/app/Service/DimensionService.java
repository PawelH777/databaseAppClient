package pl.Vorpack.app.Service;

import javafx.collections.transformation.FilteredList;
import pl.Vorpack.app.Domain.Dimiensions;

import java.util.List;

public interface DimensionService {

    Dimiensions find(Dimiensions dim);

    List<Dimiensions> findAll();
    Dimiensions returnDim(Dimiensions dimension);

    void create(Dimiensions dim);

    void update(Dimiensions dim);

    void delete(Dimiensions dim);

    void filter(String searchedText, FilteredList<Dimiensions> dims);
}
