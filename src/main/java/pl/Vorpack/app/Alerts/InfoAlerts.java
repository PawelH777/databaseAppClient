package pl.Vorpack.app.Alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

/**
 * Created by Paweł on 2018-04-21.
 */
public class InfoAlerts {

    private static final String StatusWhileRecordChanged = "Rekord został zmieniony";

    private static final String StatusWhileRecordAdded = "Rekord został dodany";

    private static final String StatusWhileRecordIsNotChanged = "Rekord nie został zmieniony!";

    private static final String StatusWhileRecordIsNotAdded= "Rekord nie został dodany!";

    private static final String StatusWhileRecordIsClosed = "Zamówienie zostało zamknięte";

    private static final String StatusWhileRecordIsNotClosed = "Zamówienie zostało zamknięte!";

    private static final String StatusWhileRecordIsRecovered = "Rekord został przywrócony";

    private static final String StatusWhileRecordIsNotRecovered = "Rekord nie został przywrócony!";

    private static final String StatusWhileRecordIsDeleted= "Rekord został usunięty";

    private static final String StatusWhileRecordIsNotDeleted = "Rekord nie został usunięty!";

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

    public static String getStatusWhileRecordIsClosed() {
        return StatusWhileRecordIsClosed;
    }

    public static String getStatusWhileRecordIsNotClosed() {
        return StatusWhileRecordIsNotClosed;
    }

    public static String getStatusWhileRecordIsDeleted() {
        return StatusWhileRecordIsDeleted;
    }

    public static String getStatusWhileRecordIsNotDeleted() {
        return StatusWhileRecordIsNotDeleted;
    }

    public void generalAlert(){
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Uwaga!");
        infoAlert.setHeaderText("Błąd aplikacji");
        infoAlert.setContentText("Podczas wykonywania procesu, został wygenerowany błąd. Proszę o skontaktowanie się z " +
                "twórcą programu");
        infoAlert.showAndWait();
    }

    public Boolean deleteRecord(String message){
        Alert infoAlert = new Alert(Alert.AlertType.CONFIRMATION);
        infoAlert.setTitle("Uwaga!");
        infoAlert.setHeaderText("Wykryto powiązanie z innym obiektem");
        infoAlert.setContentText(message);

        java.util.Optional<ButtonType> result = infoAlert.showAndWait();
        if(result.isPresent())
            if(result.get() == ButtonType.OK)
                return true;
            else if(result.get() == ButtonType.CANCEL)
                return false;
        return false;
    }

    public void viewError(Throwable ex){
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.getDialogPane().minHeight(Region.USE_PREF_SIZE);
        infoAlert.setTitle("Uwaga!");
        infoAlert.setHeaderText("Pojawił się błąd");
        infoAlert.setContentText(ex.toString());
        infoAlert.showAndWait();
    }

    public void viewErrorIfIsNotLoadingNextScene(){
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Uwaga!");
        infoAlert.setHeaderText("Pojawił się błąd");
        infoAlert.setContentText("Niestety, pojawiła się usterka blokująca wyświetlanie menu głównego. " +
                "Proszę skontaktować się z " + "autorem programu.");
        infoAlert.showAndWait();
    }
}
