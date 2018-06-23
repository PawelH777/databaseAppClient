package pl.Vorpack.app.Domain;

import java.math.BigDecimal;

public class SingleFinishedOrders {


    private Long single_finished_order_id;
    private FinishedOrders orderstory;
    private Dimiensions dimension;
    private BigDecimal quantity;
    private BigDecimal length;
    private BigDecimal metrs;
    private BigDecimal materials;
    private Boolean finished;

    public Long getSingle_finished_order_id() {
        return single_finished_order_id;
    }

    public void setSingle_finished_order_id(Long single_finished_order_id) {
        this.single_finished_order_id = single_finished_order_id;
    }

    public FinishedOrders getOrderstory() {
        return orderstory;
    }

    public void setOrderstory(FinishedOrders orderstory) {
        this.orderstory = orderstory;
    }

    public Dimiensions getDimension() {
        return dimension;
    }

    public void setDimension(Dimiensions dimension) {
        this.dimension = dimension;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getMetrs() {
        return metrs;
    }

    public void setMetrs(BigDecimal metrs) {
        this.metrs = metrs;
    }

    public BigDecimal getMaterials() {
        return materials;
    }

    public void setMaterials(BigDecimal materials) {
        this.materials = materials;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public SingleFinishedOrders(Long single_finished_order_id, FinishedOrders orderstory, Dimiensions dimension, BigDecimal quantity,
                                BigDecimal length, BigDecimal metrs, BigDecimal materials, Boolean finished) {
        this.single_finished_order_id = single_finished_order_id;
        this.orderstory = orderstory;
        this.dimension = dimension;
        this.quantity = quantity;
        this.length = length;
        this.metrs = metrs;
        this.materials = materials;
        this.finished = finished;
    }

    public SingleFinishedOrders(FinishedOrders orderstory, Dimiensions dimension, BigDecimal quantity, BigDecimal length,
                                BigDecimal metrs, BigDecimal materials, Boolean finished) {
        this.orderstory = orderstory;
        this.dimension = dimension;
        this.quantity = quantity;
        this.length = length;
        this.metrs = metrs;
        this.materials = materials;
        this.finished = finished;
    }

    public SingleFinishedOrders() {
    }
}
