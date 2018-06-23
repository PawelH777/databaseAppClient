package pl.Vorpack.app.HelperClassess;

import impl.org.controlsfx.autocompletion.SuggestionProvider;

import java.math.BigDecimal;
import java.util.Set;

public class AddOrderSuggestionProviders {
    public SuggestionProvider<BigDecimal> prov_FirstDims;
    public SuggestionProvider<BigDecimal> prov_SecondDims;
    public SuggestionProvider<BigDecimal> prov_Thick;
    public SuggestionProvider<BigDecimal> prov_Weights;



    public void enableOrRefreshSuggestionsToWeight(Set<BigDecimal> hashSet) {
        prov_Weights.clearSuggestions();
        prov_Weights.addPossibleSuggestions(hashSet);
    }

    public void enableOrRefreshSuggestionsToThick(Set<BigDecimal> hashSet) {
        prov_Thick.clearSuggestions();
        prov_Thick.addPossibleSuggestions(hashSet);
    }

    public void enableOrRefreshSuggestionsToSecondDim(Set<BigDecimal> hashSet) {
        prov_SecondDims.clearSuggestions();
        prov_SecondDims.addPossibleSuggestions(hashSet);
    }

    public void enableOrRefereshSuggestionsToFirstDim(Set<BigDecimal> hashSet) {
        prov_FirstDims.clearSuggestions();
        prov_FirstDims.addPossibleSuggestions(hashSet);
    }
}
