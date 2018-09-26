package pl.Vorpack.app.Service;

import javafx.collections.transformation.FilteredList;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Dto.OrdersDTO;

public interface OrdersService {

    FilteredList<OrdersDTO> getOrders(Boolean isFinished);
    void filterRecords(String searchedText, String searchedData);
    void updateOrder(Orders order);
    void deleteOrder(Orders order);
    void createOrder(Orders order);
    void updateBySingleOrder(Orders order);
    void changeOrdersStatus(Orders order);
}
