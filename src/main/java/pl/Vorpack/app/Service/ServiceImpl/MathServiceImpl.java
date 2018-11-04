package pl.Vorpack.app.Service.ServiceImpl;

import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.SingleOrders;
import pl.Vorpack.app.Enums.MathEnum;
import pl.Vorpack.app.Service.MathService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathServiceImpl implements MathService {

    public void setOverallQuantity(SingleOrders singleOrder, String first, String second){
        MathEnum.QUANTITY_ON_TRAY.setValue(singleOrder, first);
        MathEnum.AMOUNT_OF_TRAYS.setValue(singleOrder, second);
        BigDecimal overallQuantity = multiplyBigDecimals(singleOrder.getQuantity_on_tray(),
                singleOrder.getAmount_of_trays()).setScale(2, RoundingMode.FLOOR);
        singleOrder.setOverall_quantity(overallQuantity);
    }

    public void setMetrs(SingleOrders singleOrder, String first, String second){
        MathEnum.LENGTH_N_MM.setValue(singleOrder, first);
        MathEnum.OVERALL_QUANTITY.setValue(singleOrder, second);
        BigDecimal metrs = multiplyBigDecimals(singleOrder.getLength_in_mm(),
                singleOrder.getOverall_quantity()).setScale(2, RoundingMode.FLOOR);
        singleOrder.setMetrs(metrs);
    }

    public void setMaterials( Dimiensions dimension, SingleOrders singleOrder, String first, String second){
        MathEnum.WEIGHT.setValue(dimension, first);
        MathEnum.METRS.setValue(singleOrder, second);
        BigDecimal materials = multiplyBigDecimals(dimension.getWeight(), singleOrder.getMetrs())
                .setScale(2, RoundingMode.FLOOR);
        singleOrder.setMaterials(materials);
    }

    private BigDecimal multiplyBigDecimals(BigDecimal first, BigDecimal second){
        return first.multiply(second);
    }

    private BigDecimal multiplyBigDecimals(BigDecimal first, Long second){
        return first.multiply(BigDecimal.valueOf(second.doubleValue()));
    }
}
