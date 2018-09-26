package pl.Vorpack.app.Service;

import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.SingleOrders;

public interface MathService {
    void setOverallQuantity(SingleOrders singleOrders, String first, String second);
    void setMetrs(SingleOrders singleOrder, String first, String second);
    void setMaterials(Dimiensions dimension, SingleOrders singleOrder, String first, String second);
}
