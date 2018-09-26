package pl.Vorpack.app.Service.ServiceImpl;


import pl.Vorpack.app.DatabaseAccess.SingleOrdersAccess;
import pl.Vorpack.app.Domain.Orders;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Service.DimensionService;
import pl.Vorpack.app.Service.OrdersService;
import pl.Vorpack.app.Service.SingleOrdersService;
import pl.Vorpack.app.Service.TrayService;

import java.util.List;

public class SingleOrdersServiceImpl implements SingleOrdersService {

    private SingleOrdersAccess singleOrdersAccess = new SingleOrdersAccess();
    private OrdersService ordersService = new OrdersServiceImpl();
    private DimensionService dimensionService = new DimensionServiceImpl();

    public List<SingleOrders> getSingleOrdersBySingleOrder(Orders order){
        return singleOrdersAccess.findByOrders(order);
    }

    @Override
    public void create(SingleOrders singleOrders, Orders order) {
        SingleOrders singleOrder = singleOrdersAccess.createSingleOrder(singleOrders);
        TrayService trayService = new TrayServiceImpl();
        trayService.createBySingleOrder(singleOrder);
        ordersService.updateBySingleOrder(order);
    }

    @Override
    public void update(SingleOrders singleOrders, Orders order) {
        singleOrdersAccess.updateSingleOrder(singleOrders);
        ordersService.updateBySingleOrder(order);
    }

    @Override
    public void update(SingleOrders singleOrders) {
        singleOrdersAccess.updateSingleOrder(singleOrders);
    }
}
