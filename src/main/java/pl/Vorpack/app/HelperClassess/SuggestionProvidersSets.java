package pl.Vorpack.app.HelperClassess;

import pl.Vorpack.app.Domain.Dimiensions;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuggestionProvidersSets {
    public Set<BigDecimal> firstDim = new HashSet<>();
    public Set<BigDecimal> secondDim = new HashSet<>();
    public Set<BigDecimal> thick = new HashSet<>();
    public Set<BigDecimal> weight = new HashSet<>();

    public void update(List<Dimiensions> dimensions){
        firstDim.clear();
        secondDim.clear();
        thick.clear();
        weight.clear();

        for(Dimiensions dims : dimensions){
            firstDim.add(dims.getFirstDimension());
            secondDim.add(dims.getSecondDimension());
            thick.add(dims.getThickness());
            weight.add(dims.getWeight());
        }
    }
}
