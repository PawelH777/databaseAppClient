package pl.Vorpack.app.TableValues;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrdersDTO {
    private Long order_id;
    private String firmName;
    private LocalDate order_date;
    private LocalDate order_receive_date;
    private Long single_orders_completed;
    private Long single_orders_unfinished;
    private BigDecimal materials;

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public LocalDate getOrder_date() {
        return order_date;
    }

    public void setOrder_date(LocalDate order_date) {
        this.order_date = order_date;
    }

    public LocalDate getOrder_receive_date() {
        return order_receive_date;
    }

    public void setOrder_receive_date(LocalDate order_receive_date) {
        this.order_receive_date = order_receive_date;
    }

    public Long getSingle_orders_completed() {
        return single_orders_completed;
    }

    public void setSingle_orders_completed(Long single_orders_completed) {
        this.single_orders_completed = single_orders_completed;
    }

    public Long getSingle_orders_unfinished() {
        return single_orders_unfinished;
    }

    public void setSingle_orders_unfinished(Long single_orders_unfinished) {
        this.single_orders_unfinished = single_orders_unfinished;
    }

    public BigDecimal getMaterials() {
        return materials;
    }

    public void setMaterials(BigDecimal materials) {
        this.materials = materials;
    }

    public OrdersDTO(Long order_id, String firmName, LocalDate order_date, LocalDate order_receive_date,
                     Long single_orders_completed, Long single_orders_unfinished, BigDecimal materials) {
        this.order_id = order_id;
        this.firmName = firmName;
        this.order_date = order_date;
        this.order_receive_date = order_receive_date;
        this.single_orders_completed = single_orders_completed;
        this.single_orders_unfinished = single_orders_unfinished;
        this.materials = materials;
    }
}
