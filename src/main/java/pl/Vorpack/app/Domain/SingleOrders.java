package pl.Vorpack.app.Domain;

import java.math.BigDecimal;

public class SingleOrders {

    private Long single_active_order_id;
    private Orders orders;
    private Dimiensions dimension;
    private BigDecimal quantity;
    private BigDecimal length;
    private BigDecimal metrs;
    private BigDecimal materials;
    private Boolean finished;



    public Long getSingle_active_order_id() {
        return single_active_order_id;
    }

    public void setSingle_active_order_id(Long single_active_order_id) {
        this.single_active_order_id = single_active_order_id;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
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

    public SingleOrders(Long single_active_order_id, Orders orders, Dimiensions dimension, BigDecimal quantity, BigDecimal length,
                        BigDecimal metrs, BigDecimal materials, Boolean finished) {
        this.single_active_order_id = single_active_order_id;
        this.orders = orders;
        this.dimension = dimension;
        this.quantity = quantity;
        this.length = length;
        this.metrs = metrs;
        this.materials = materials;
        this.finished = finished;
    }

    public SingleOrders(Orders orders, Dimiensions dimension, BigDecimal quantity, BigDecimal length, BigDecimal metrs,
                        BigDecimal materials, Boolean finished) {
        this.orders = orders;
        this.dimension = dimension;
        this.quantity = quantity;
        this.length = length;
        this.metrs = metrs;
        this.materials = materials;
        this.finished = finished;
    }

    public SingleOrders() {
    }
}
