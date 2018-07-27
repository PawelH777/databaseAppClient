package pl.Vorpack.app.Background;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import pl.Vorpack.app.DatabaseAccess.DimensionsAccess;
import pl.Vorpack.app.GlobalVariables.DimVariables;

public class GetDimensions extends ScheduledService<Void> {
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>(){
            protected Void call() {
                DimensionsAccess dimensionsAccess = new DimensionsAccess();
                DimVariables.setDimsFromDatabase(dimensionsAccess.findAllDimensions());
                return null;
            }
        };
    }
}
