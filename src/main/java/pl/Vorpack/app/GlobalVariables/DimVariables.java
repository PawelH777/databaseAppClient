package pl.Vorpack.app.GlobalVariables;

import pl.Vorpack.app.Domain.Dimiensions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paweł on 2018-02-20.
 */
public class DimVariables {

    private static Dimiensions object = new Dimiensions();
    private static List<Dimiensions> dimsFromDatabase = new ArrayList<>();

    public static Dimiensions getObject() {
        return object;
}

    public static void setObject(Dimiensions object) {
        DimVariables.object = object;
    }

    public static List<Dimiensions> getDimsFromDatabase() {
        return dimsFromDatabase;
    }

    public static void setDimsFromDatabase(List<Dimiensions> dimsFromDatabase) {
        DimVariables.dimsFromDatabase = dimsFromDatabase;
    }
}
