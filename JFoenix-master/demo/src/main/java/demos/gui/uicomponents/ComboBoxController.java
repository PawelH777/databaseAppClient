package demos.gui.uicomponents;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.validation.ValidationFacade;
import io.datafx.controller.ViewController;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import javax.annotation.PostConstruct;

@ViewController(value = "/Fxml/ui/Combobox.Fxml", title = "Material Design Example")
public class ComboBoxController {

    @FXML
    private JFXComboBox<Label> jfxComboBox;
    @FXML
    private JFXComboBox<Label> jfxEditableComboBox;

    /**
     * init Fxml when loaded.
     */
    @PostConstruct
    public void init() {

        jfxComboBox.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                ValidationFacade.validate(jfxComboBox);
            }
        });

        ChangeListener<? super Boolean> comboBoxFocus = (o, oldVal, newVal) -> {
            if (!newVal) {
                ValidationFacade.validate(jfxEditableComboBox);
            }
        };
        jfxEditableComboBox.focusedProperty().addListener(comboBoxFocus);
        jfxEditableComboBox.getEditor().focusedProperty().addListener(comboBoxFocus);
        jfxEditableComboBox.setConverter(new StringConverter<Label>() {
            @Override
            public String toString(Label object) {
                return object==null? "" : object.getText();
            }
            @Override
            public Label fromString(String string) {
                return new Label(string);
            }
        });
    }

}
