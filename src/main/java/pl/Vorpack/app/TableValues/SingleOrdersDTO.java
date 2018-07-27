package pl.Vorpack.app.TableValues;

import java.math.BigDecimal;

public class SingleOrdersDTO {
    private Long single_active_order_id;
    private String dimension;
    private BigDecimal weight;
    private BigDecimal length_in_mm;
    private BigDecimal quantity_on_tray;
    private Long amount_of_trays;
    private BigDecimal overall_quantity;
    private BigDecimal metrs;
    private BigDecimal materials;
    private String finished;
    private String commentary;

    public Long getSingle_active_order_id() {
        return single_active_order_id;
    }

    public void setSingle_active_order_id(Long single_active_order_id) {
        this.single_active_order_id = single_active_order_id;
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

    public BigDecimal getLength_in_mm() {
        return length_in_mm;
    }

    public void setLength_in_mm(BigDecimal length_in_mm) {
        this.length_in_mm = length_in_mm;
    }

    public BigDecimal getQuantity_on_tray() {
        return quantity_on_tray;
    }

    public void setQuantity_on_tray(BigDecimal quantity_on_tray) {
        this.quantity_on_tray = quantity_on_tray;
    }

    public Long getAmount_of_trays() {
        return amount_of_trays;
    }

    public void setAmount_of_trays(Long amount_of_trays) {
        this.amount_of_trays = amount_of_trays;
    }

    public BigDecimal getOverall_quantity() {
        return overall_quantity;
    }

    public void setOverall_quantity(BigDecimal overall_quantity) {
        this.overall_quantity = overall_quantity;
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

    public String getFinished() {
        return finished;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public SingleOrdersDTO(String dimension, BigDecimal weight, BigDecimal length_in_mm, BigDecimal quantity_on_tray,
                           Long amount_of_trays, BigDecimal overall_quantity, BigDecimal metrs, BigDecimal materials, String finished, String commentary) {
        this.dimension = dimension;
        this.weight = weight;
        this.length_in_mm = length_in_mm;
        this.quantity_on_tray = quantity_on_tray;
        this.amount_of_trays = amount_of_trays;
        this.overall_quantity = overall_quantity;
        this.metrs = metrs;
        this.materials = materials;
        this.finished = finished;
        this.commentary = commentary;
    }

    public SingleOrdersDTO() {
    }
}
