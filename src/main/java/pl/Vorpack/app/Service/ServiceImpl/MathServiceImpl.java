package pl.Vorpack.app.Service.ServiceImpl;

import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Enums.MathEnum;
import pl.Vorpack.app.Service.MathService;

import java.math.BigDecimal;

public class MathServiceImpl implements MathService {


    public void setOverallQuantity(SingleOrders singleOrder, String first, String second){
        MathEnum.QUANTITY_ON_TRAY.setValue(singleOrder, first);
        MathEnum.AMOUNT_OF_TRAYS.setValue(singleOrder, second);
        singleOrder.setOverall_quantity(multiplyBigDecimals(singleOrder.getQuantity_on_tray(),
                singleOrder.getAmount_of_trays()));

        if (singleOrder.getOverall_quantity().scale()<0)
            singleOrder.setOverall_quantity(singleOrder.getOverall_quantity().setScale(0));
    }

    public void setMetrs(SingleOrders singleOrder, String first, String second){
        MathEnum.LENGTH_N_MM.setValue(singleOrder, first);
        MathEnum.OVERALL_QUANTITY.setValue(singleOrder, second);
        singleOrder.setMetrs(multiplyBigDecimals(singleOrder.getLength_in_mm(),
                singleOrder.getOverall_quantity()));

        if (singleOrder.getMetrs().scale()<0)
            singleOrder.setMetrs(singleOrder.getMetrs().setScale(0));
    }

    public void setMaterials( Dimiensions dimension, SingleOrders singleOrder, String first, String second){
        MathEnum.WEIGHT.setValue(dimension, first);
        MathEnum.METRS.setValue(singleOrder, second);
        singleOrder.setMaterials(multiplyBigDecimals(dimension.getWeight(), singleOrder.getMetrs()));

        if (singleOrder.getMaterials().scale()<0)
            singleOrder.setMaterials(singleOrder.getMaterials().setScale(0));
    }

    private BigDecimal multiplyBigDecimals(BigDecimal first, BigDecimal second){
        return first.multiply(second);
    }

    private BigDecimal multiplyBigDecimals(BigDecimal first, Long second){
        return first.multiply(BigDecimal.valueOf(second.doubleValue()));
    }
}
