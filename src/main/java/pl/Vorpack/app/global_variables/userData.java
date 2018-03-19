package pl.Vorpack.app.global_variables;

/**
 * Created by Paweł on 2018-03-05.
 */
public class userData {
    private static String name;
    private static String access;

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        userData.name = name;
    }

    public static String getAccess() {
        return access;
    }

    public static void setAccess(String access) {
        userData.access = access;
    }
}
