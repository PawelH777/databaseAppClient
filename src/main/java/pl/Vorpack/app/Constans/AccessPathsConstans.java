package pl.Vorpack.app.Constans;

public class AccessPathsConstans {
    public static final String findAllClients = "/clients";
    public static final String searchWithFirmNameUri = "/clients/firm-name";
    public static final String createNewClientUri = "/clients/create";
    public static final String updateClientUri = "/clients/update";
    public static final String deleteClientUri = "/clients/delete";

    public static final String findDimensionsUri = "/dims";
    public static final String findDimensionUri = "/dims/find";
    public static final String createDimensionUri = "/dims/create";
    public static final String updateDimensionUri = "/dims/update";
    public static final String deleteDimensionUri = "/dims/delete";

    public static final String findOrdersWithFinishedUri = "/orders/finished";
    public static final String updateOrderUri = "/orders/update";
    public static final String changeStatusUri = "/orders/change-status";
    public static final String createOrderUri = "/orders/create";
    public static final String deleteOrderUri = "/orders/delete";

    public static final String findAllSingleOrdersUri = "/single-orders";
    public static final String findAllSingleOrdersFromOrderUri = "/single-orders/order";
    public static final String createSingleOrderUri = "/single-orders/create";
    public static final String updateSingleOrderUri = "/single-orders/update";
    public static final String deleteSingleOrderUri = "/single-orders/delete";

    public static final String findAllUri = "/trays";
    public static final String findAllTraysBySingleOrderUri = "/trays/single-order";
    public static final String createTraysBySingleOrderUri = "/trays/create";
    public static final String changeTraysStatusUri = "/trays/update";

    public static final String findClientsByLoginURI = "/users/login";
    public static final String createNewUserURI = "/users/create";
    public static final String updateUserURI = "/users/update";
    public static final String findAllUsersURI = "/users";
    public static final String deleteUserURI = "/users/delete";
}
