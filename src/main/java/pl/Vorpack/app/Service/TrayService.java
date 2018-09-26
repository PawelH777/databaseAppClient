package pl.Vorpack.app.Service;

import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Domain.Trays;

import java.util.List;

public interface TrayService {

    List<Trays> findBySingleOrder(SingleOrders singleOrder);

    void createBySingleOrder(SingleOrders singleOrders);

    void updateStatus(Trays tray);
}
