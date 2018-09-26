package pl.Vorpack.app.HelperClassess;

import impl.org.controlsfx.autocompletion.SuggestionProvider;

import java.math.BigDecimal;
import java.util.Set;

public class SuggestionProviders {
    private SuggestionProvider<BigDecimal> firstDim;
    private SuggestionProvider<BigDecimal> secondDim;
    private SuggestionProvider<BigDecimal> thick;
    private SuggestionProvider<BigDecimal> weight;


    public SuggestionProviders(SuggestionProvider<BigDecimal> firstDim, SuggestionProvider<BigDecimal> secondDim,
                               SuggestionProvider<BigDecimal> thick, SuggestionProvider<BigDecimal> weight){
        this.firstDim = firstDim;
        this.secondDim = secondDim;
        this.thick = thick;
        this.weight = weight;
    }

    private void refresh(Set<BigDecimal> hashSet, SuggestionProvider<BigDecimal> provider) {
        provider.clearSuggestions();
        provider.addPossibleSuggestions(hashSet);
    }

    public void refreshSuggestions(SuggestionProvidersSets providersSets, SuggestionProviders provider){
        provider.refresh(providersSets.firstDim, provider.firstDim);
        provider.refresh(providersSets.secondDim, provider.secondDim);
        provider.refresh(providersSets.thick, provider.thick);
        provider.refresh(providersSets.weight, provider.weight);
    }

    public void clearSuggestions(SuggestionProviders providers){
        providers.firstDim.clearSuggestions();
        providers.secondDim.clearSuggestions();
        providers.thick.clearSuggestions();
        providers.weight.clearSuggestions();
    }

}
