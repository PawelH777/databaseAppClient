package pl.Vorpack.app.global_variables;

/**
 * Created by Paweł on 2018-03-05.
 */
public class GlobalVariables {
    private static String name;
    private static String password;
    private static String access;
    private static final String site_name = "https://blooming-peak-56674.herokuapp.com/";
    private static Boolean isActionCompleted = false;

    public static Boolean getIsActionCompleted() {
        return isActionCompleted;
    }

    public static void setIsActionCompleted(Boolean isActionCompleted) {
        GlobalVariables.isActionCompleted = isActionCompleted;
    }

    public static String getSite_name() {
        return site_name;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        GlobalVariables.name = name;
    }

    public static String getAccess() {
        return access;
    }

    public static void setAccess(String access) {
        GlobalVariables.access = access;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        GlobalVariables.password = password;
    }
}
