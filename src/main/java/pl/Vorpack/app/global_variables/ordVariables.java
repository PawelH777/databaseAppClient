package pl.Vorpack.app.global_variables;

import pl.Vorpack.app.domain.Orders;

import java.time.LocalDate;

/**
 * Created by Paweł on 2018-03-01.
 */
public class ordVariables {
    private static Orders object = new Orders();

    public static Orders getObject() {
        return object;
    }

    public static void setObject(Orders object) {
        ordVariables.object = object;
    }
}
