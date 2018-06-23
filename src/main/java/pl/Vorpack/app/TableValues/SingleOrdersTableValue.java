package pl.Vorpack.app.TableValues;

import java.math.BigDecimal;

public class SingleOrdersTableValue {

    private Long singleOrderID;
    private String dimension;
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal quantity;
    private BigDecimal metrs;
    private BigDecimal materials;
    private String status;

    public Long getSingleOrderID() {
        return singleOrderID;
    }

    public void setSingleOrderID(Long singleOrderID) {
        this.singleOrderID = singleOrderID;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SingleOrdersTableValue(Long singleOrderID, String dimension, BigDecimal weight, BigDecimal length, BigDecimal quantity, BigDecimal metrs, BigDecimal materials, String status) {
        this.singleOrderID = singleOrderID;
        this.dimension = dimension;
        this.weight = weight;
        this.length = length;
        this.quantity = quantity;
        this.metrs = metrs;
        this.materials = materials;
        this.status = status;
    }
}
