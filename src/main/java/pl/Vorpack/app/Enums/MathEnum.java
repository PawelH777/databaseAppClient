package pl.Vorpack.app.Enums;

import pl.Vorpack.app.Domain.Dimiensions;
import pl.Vorpack.app.Domain.SingleOrders;

import java.math.BigDecimal;

public enum MathEnum {

    LENGTH_N_MM {
        public void setValue(SingleOrders singleOrder, String value){
            try{
                BigDecimal convertedValue = BigDecimal.valueOf(Double.valueOf(value));
                singleOrder.setLength_in_mm(convertedValue);
            }
            catch(Exception e){
                singleOrder.setLength_in_mm(BigDecimal.ZERO);
            }
        }

        public void setValue(Dimiensions dimension, String value){
        }
    },

    OVERALL_QUANTITY{
        public void setValue(SingleOrders singleOrder, String value){
            try{
                BigDecimal convertedValue = BigDecimal.valueOf(Double.valueOf(value));
                singleOrder.setOverall_quantity(convertedValue);
            }
            catch(Exception e){
                singleOrder.setOverall_quantity(BigDecimal.ZERO);
            }
        }

        public void setValue(Dimiensions dimension, String value){
        }
    },

    QUANTITY_ON_TRAY{
        public void setValue(SingleOrders singleOrder, String value){
            try{
                singleOrder.setQuantity_on_tray(BigDecimal.valueOf(Double.valueOf(value)));
            }
            catch(Exception e){
                singleOrder.setQuantity_on_tray(BigDecimal.ZERO);
            }
        }

        public void setValue(Dimiensions dimension, String value){
        }
    },

    AMOUNT_OF_TRAYS{
        public void setValue(SingleOrders singleOrder, String value){
            try{
                Long amountOfTrays = Long.parseLong(value);
                singleOrder.setAmount_of_trays(amountOfTrays);
            }
            catch(Exception e){
                singleOrder.setAmount_of_trays(0L);
            }
        }

        public void setValue(Dimiensions dimension, String value){
        }
    },

    METRS{
        public void setValue(SingleOrders singleOrder, String value){
            try{
                BigDecimal convertedValue = BigDecimal.valueOf(Double.valueOf(value));
                singleOrder.setMetrs(convertedValue);
            }
            catch(Exception e){
                singleOrder.setMetrs(BigDecimal.ZERO);
            }
        }

        public void setValue(Dimiensions dimension, String value){
        }
    },


    WEIGHT{
        public void setValue(SingleOrders singleOrder, String value){
        }

        public void setValue(Dimiensions dimension, String value){
            try{
                BigDecimal convertedValue = BigDecimal.valueOf(Double.valueOf(value));
                dimension.setWeight(convertedValue);
            }
            catch(Exception e){
                dimension.setWeight(BigDecimal.ZERO);
            }
        }
    };

    public abstract void setValue(SingleOrders singleOrder, String value);
    public abstract void setValue(Dimiensions dimension, String value);
}
