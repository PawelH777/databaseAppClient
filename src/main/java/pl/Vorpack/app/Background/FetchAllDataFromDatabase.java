package pl.Vorpack.app.Background;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import pl.Vorpack.app.Animations.TextAnimations;
import pl.Vorpack.app.DatabaseAccess.*;
import pl.Vorpack.app.GlobalVariables.*;

public class FetchAllDataFromDatabase extends ScheduledService<Void> {
    private Label statusViewer;
    private String textInStatusViewer;

    public FetchAllDataFromDatabase(Label statusViewer){
        this.statusViewer = statusViewer;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>(){
            protected Void call() {
                TextAnimations textAnimations = new TextAnimations(statusViewer);

                UsersAccess usersAccess = new UsersAccess();
                UsrVariables.setUsersInDatabase(usersAccess.findAllUsers());
                UsrVariables.getUsersInDatabase().removeIf(u -> u.getLogin().equals("Admin"));
                textInStatusViewer = "Ładowanie danych użytkowników";
                fetchStatusViewerAndStartPulsuation(textAnimations);

                ClientAccess clientsAccess = new ClientAccess();
                CliVariables.setClientsFromDatabase(clientsAccess.findAllClients());
                textInStatusViewer = "Ładowanie danych klientów";
                fetchStatusViewerAndStartPulsuation(textAnimations);

                DimensionsAccess dimensionsAccess = new DimensionsAccess();
                DimVariables.setDimsFromDatabase(dimensionsAccess.findAllDimensions());
                textInStatusViewer = "Ładowanie danych wymiarów";
                fetchStatusViewerAndStartPulsuation(textAnimations);

                OrdersAccess ordersAccess = new OrdersAccess();
                OrdVariables.setOrdersFromDatabase(ordersAccess.findOrdersWithFinished(false));
                textInStatusViewer = "Ładowanie danych zamówień";
                fetchStatusViewerAndStartPulsuation(textAnimations);

                OrdersAccess finishedOrdersAccess = new OrdersAccess();
                FinishedOrdVariables.setOrdersFromDatabase(finishedOrdersAccess.findOrdersWithFinished(true));
                textInStatusViewer = "Ładowanie danych zakończonych zamówień";
                fetchStatusViewerAndStartPulsuation(textAnimations);
                return null;
            }
        };
    }


    private void fetchStatusViewerAndStartPulsuation(TextAnimations textAnimations) {
        statusViewer.setText(textInStatusViewer);
        textAnimations.startLabelsPulsing();
    }
}
