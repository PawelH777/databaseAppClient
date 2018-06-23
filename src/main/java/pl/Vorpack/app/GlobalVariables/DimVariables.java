package pl.Vorpack.app.GlobalVariables;

import pl.Vorpack.app.Domain.Dimiensions;

/**
 * Created by Paweł on 2018-02-20.
 */
public class DimVariables {

    private static Dimiensions object = new Dimiensions();


    public static Dimiensions getObject() {
        return object;
}

    public static void setObject(Dimiensions object) {
        DimVariables.object = object;
    }
}
