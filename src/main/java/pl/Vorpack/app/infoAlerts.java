package pl.Vorpack.app;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Created by Paweł on 2018-04-21.
 */
public class infoAlerts {

    private static final String StatusWhileRecordChanged = "Rekord został zmieniony";

    private static final String StatusWhileRecordAdded = "Rekord został dodany";

    private static final String StatusWhileRecordIsNotChanged = "Rekord nie został zmieniony!";

    private static final String StatusWhileRecordIsNotAdded= "Rekord nie został dodany!";

    private static final String StatusWhileRecordIsRecovered = "Rekord został przywrócony";

    private static final String StatusWhileRecordIsNotRecovered = "Rekord nie został przywrócony!";

    public static String getStatusWhileRecordChanged() {
        return StatusWhileRecordChanged;
    }

    public static String getStatusWhileRecordAdded() {
        return StatusWhileRecordAdded;
    }

    public static String getStatusWhileRecordIsNotChanged() {
        return StatusWhileRecordIsNotChanged;
    }

    public static String getStatusWhileRecordIsNotAdded() {
        return StatusWhileRecordIsNotAdded;
    }

    public static String getStatusWhileRecordIsRecovered() {
        return StatusWhileRecordIsRecovered;
    }

    public static String getStatusWhileRecordIsNotRecovered() {
        return StatusWhileRecordIsNotRecovered;
    }

    public static void generalAlert(){
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Uwaga!");
        infoAlert.setHeaderText("Błąd aplikacji");
        infoAlert.setContentText("Podczas wykonywania procesu, został wygenerowany błąd. Proszę o skontaktowanie się z " +
                "twórcą programu");
        infoAlert.showAndWait();
    }

    public static Boolean deleteRecord(String zakladka, String zakladka2){
        Alert infoAlert = new Alert(Alert.AlertType.CONFIRMATION);
        infoAlert.setTitle("Uwaga!");
        infoAlert.setHeaderText("Wykryto powiązanie z innym obiektem");

        if(zakladka2 == null)
            infoAlert.setContentText("Klient jest powiązany z zamówieniami znajdującymi się w zakładce " + zakladka +
                    ". Jeśli chcesz usunąć klienta, należy przedtem skasować powiązane obiekty. Program wykona to po naciśnięciu"
                    + " na przycisk OK.");
        if(zakladka2 != null)
            infoAlert.setContentText("Klient jest powiązany z zamówieniami znajdującymi się w zakładce " + zakladka + " oraz w zakładce " +
                    zakladka2 + ". Jeśli chcesz usunąć klienta, należy przedtem skasować powiązane obiekty. Program wykona to po naciśnięciu"
                    + " na przycisk OK.");

        java.util.Optional<ButtonType> result = infoAlert.showAndWait();

        if(result.isPresent())
            if(result.get() == ButtonType.OK)
                return true;
            else if(result.get() == ButtonType.CANCEL)
                return false;


        return false;


    }

    public static void addRecord(String addmodify){
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Informacja");
        infoAlert.setHeaderText("Przeprowadzono operację");
        infoAlert.setContentText("Rekord został " + addmodify + ".");
        infoAlert.showAndWait();
    }
}
