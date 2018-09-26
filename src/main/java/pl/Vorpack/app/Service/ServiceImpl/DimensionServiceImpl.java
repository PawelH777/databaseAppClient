package pl.Vorpack.app.Service.ServiceImpl;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.transformation.FilteredList;
import pl.Vorpack.app.Constans.DimensionColumn;
import pl.Vorpack.app.DatabaseAccess.DimensionsAccess;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Service.DimensionService;

import java.util.List;

import static pl.Vorpack.app.Constans.OrderColumn.ALL_ITEMS;

public class DimensionServiceImpl implements DimensionService {

    private DimensionsAccess dimensionsAccess = new DimensionsAccess();
    private JFXComboBox<String> columnsCmbBox;

    public DimensionServiceImpl() {
    }

    public DimensionServiceImpl(JFXComboBox<String> columnsCmbBox) {
        this.columnsCmbBox = columnsCmbBox;
    }

    @Override
    public Dimiensions returnDim(Dimiensions dim) {
        boolean findDim = false;
        List<Dimiensions> dims = dimensionsAccess.find(dim);
        if(dims.size() != 0){
            findDim = true;
            dim = dims.get(0);
        }
        if(!findDim)
            dim = dimensionsAccess.create(dim);
        return dim;
    }

    @Override
    public Dimiensions find(Dimiensions dim){
        List<Dimiensions> dims = dimensionsAccess.find(dim);
        return !dims.isEmpty() ? dims.get(0) : null;
    }

    @Override
    public List<Dimiensions> findAll(){
        return dimensionsAccess.findAllDimensions();
    }

    @Override
    public void create(Dimiensions dim){
        dimensionsAccess.create(dim);
    }

    @Override
    public void update(Dimiensions dim){
        dimensionsAccess.update(dim);
    }

    @Override
    public void delete(Dimiensions dim){
        dimensionsAccess.delete(dim);
    }

    @Override
    public void filter(String searchedText, FilteredList<Dimiensions> dims) {
        if (columnsCmbBox.getSelectionModel().getSelectedItem() == null ||
                columnsCmbBox.getSelectionModel().getSelectedItem().equals(ALL_ITEMS))
            filterByEveryColumn(searchedText, dims);
        else
            filterByOneColumn(searchedText, dims);
    }

    private void filterByEveryColumn(String searchedText, FilteredList<Dimiensions> dims) {
        dims.setPredicate(obj -> {
            if (searchedText == null || searchedText.isEmpty())
                return true;
            String lowerCaseValue = searchedText.toLowerCase();
            if (String.valueOf(obj.getDimension_id()).toLowerCase().contains(lowerCaseValue))
                return true;
            else if (String.valueOf(obj.getFirstDimension()).toLowerCase().contains(lowerCaseValue))
                return true;
            else if (String.valueOf(obj.getSecondDimension()).toLowerCase().contains(lowerCaseValue))
                return true;
            else if (String.valueOf(obj.getThickness()).toLowerCase().contains(lowerCaseValue))
                return true;
            else return String.valueOf(obj.getWeight()).toLowerCase().contains(lowerCaseValue);
        });
    }

    private void filterByOneColumn(String searchedText, FilteredList<Dimiensions> dims) {
        dims.setPredicate(obj -> {
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

    private String getFilter(String column, Dimiensions obj) {
        switch (column) {
            case DimensionColumn.ID:
                return String.valueOf(obj.getDimension_id()).toLowerCase();
            case DimensionColumn.FIRST_DIMENSION:
                return String.valueOf(obj.getFirstDimension()).toLowerCase();
            case DimensionColumn.SECOND_DIMENSION:
                return String.valueOf(obj.getFirstDimension()).toLowerCase();
            case DimensionColumn.THICKNESS:
                return String.valueOf(obj.getFirstDimension()).toLowerCase();
            case DimensionColumn.WEIGHT:
                return String.valueOf(obj.getFirstDimension()).toLowerCase();
            default:
                return "";
        }
    }
}
