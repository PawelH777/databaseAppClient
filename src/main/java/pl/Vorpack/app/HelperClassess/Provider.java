package pl.Vorpack.app.HelperClassess;

import com.jfoenix.controls.JFXTextField;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;

import java.math.BigDecimal;
import java.util.Set;

public class Provider {
    private SuggestionProvider<BigDecimal> provider;

    public void set(Set<BigDecimal> fieldsSet, JFXTextField field){
        provider = SuggestionProvider.create(fieldsSet);
        new AutoCompletionTextFieldBinding<>(field, provider);
    }

    public SuggestionProvider<BigDecimal> get(){
        return provider;
    }
}
