package pl.Vorpack.app.Service.ServiceImpl;

import pl.Vorpack.app.DatabaseAccess.TraysAccess;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Domain.Trays;
import pl.Vorpack.app.Service.TrayService;

import java.util.List;

public class TrayServiceImpl implements TrayService {
    private TraysAccess traysAccess = new TraysAccess();

    @Override
    public List<Trays> findBySingleOrder(SingleOrders singleOrder){
        return traysAccess.findBySingleOrder(singleOrder);
    }

    @Override
    public void createBySingleOrder(SingleOrders singleOrders) {
        traysAccess.createTraysBySingleOrder(singleOrders);
    }

    @Override
    public void updateStatus(Trays tray){
        traysAccess.updateStatus(tray);
    }

}
