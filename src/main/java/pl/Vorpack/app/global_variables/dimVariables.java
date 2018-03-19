package pl.Vorpack.app.global_variables;

import pl.Vorpack.app.domain.Dimiensions;

/**
 * Created by Paweł on 2018-02-20.
 */
public class dimVariables {

    private static Dimiensions object = new Dimiensions();


    public static Dimiensions getObject() {
        return object;
}

    public static void setObject(Dimiensions object) {
        dimVariables.object = object;
    }
}
