package pl.Vorpack.app.domain;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Paweł on 2018-02-03.
 */

@Entity
@Table(name = "Wymiary")
public class Dimiensions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Id_wymiaru")
    private long dimension_id;

    @Column(name="Dlugosc_pierwszego_ramienia", nullable = false)
    private Double first_dimension;

    @Column(name="Dlugosc_drugiego_ramienia", nullable = false)
    private Double second_dimension;

    @Column(name="Grubosc_ramion", nullable = false)
    private Double thickness;

    @Column(name="Waga", nullable = false)
    private Double weight;

    public long getDimension_id() {
        return dimension_id;
    }

    public void setDimension_id(long dimension_id) {
        this.dimension_id = dimension_id;
    }

    public Double getFirst_dimension() {
        return first_dimension;
    }

    public void setFirst_dimension(Double first_dimension) {
        this.first_dimension = first_dimension;
    }

    public Double getSecond_dimension() {
        return second_dimension;
    }

    public void setSecond_dimension(Double second_dimension) {
        this.second_dimension = second_dimension;
    }

    public Double getThickness() {
        return thickness;
    }

    public void setThickness(Double thickness) {
        this.thickness = thickness;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Dimiensions(){

    }

    public Dimiensions(Double a, Double b, Double c, Double d){
        first_dimension = a;
        second_dimension = b;
        thickness = c;
        weight = d;
    }

}
