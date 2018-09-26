package pl.Vorpack.app.Service;

import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;

import java.util.List;

public interface SingleOrdersService {

    List<SingleOrders> getSingleOrdersBySingleOrder(Orders order);
    void create(SingleOrders singleOrders, Orders order);
    void update(SingleOrders singleOrders, Orders order);

    void update(SingleOrders singleOrders);
}
