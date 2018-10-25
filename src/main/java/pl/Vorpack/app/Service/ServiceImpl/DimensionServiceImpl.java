package pl.Vorpack.app.Service.ServiceImpl;

import javafx.collections.transformation.FilteredList;
import pl.Vorpack.app.Constans.DimensionColumnConstans;
import pl.Vorpack.app.DatabaseAccess.DimensionsAccess;
import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Service.DimensionService;

import java.util.List;

import static pl.Vorpack.app.Constans.OrderColumnConstans.ALL_ITEMS;

public class DimensionServiceImpl implements DimensionService {

    private DimensionsAccess dimensionsAccess = new DimensionsAccess();

    public DimensionServiceImpl() {
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
    public void filter(String filter, String searchedText, FilteredList<Dimiensions> dims) {
        if (filter == null || filter.equals(ALL_ITEMS))
            filterByEveryColumn(searchedText, dims);
        else
            filterByOneColumn(filter, searchedText, dims);
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

    private void filterByOneColumn(String filter, String searchedText, FilteredList<Dimiensions> dims) {
        dims.setPredicate(obj -> {
            if (searchedText == null || searchedText.isEmpty())
                return true;
            String lowerCaseValue = getNonNullLowerCaseValue(searchedText);
            String filterValue = getFilter(filter, obj);
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
            case DimensionColumnConstans.ID:
                return String.valueOf(obj.getDimension_id()).toLowerCase();
            case DimensionColumnConstans.FIRST_DIMENSION:
                return String.valueOf(obj.getFirstDimension()).toLowerCase();
            case DimensionColumnConstans.SECOND_DIMENSION:
                return String.valueOf(obj.getFirstDimension()).toLowerCase();
            case DimensionColumnConstans.THICKNESS:
                return String.valueOf(obj.getFirstDimension()).toLowerCase();
            case DimensionColumnConstans.WEIGHT:
                return String.valueOf(obj.getFirstDimension()).toLowerCase();
            default:
                return "";
        }
    }
}
