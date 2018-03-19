package pl.Vorpack.app.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by Paweł on 2018-02-03.
 */

@Entity
@Table(name = "Zamowienia")
public class Orders {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_zamowienia")
    private Long order_id;
    @OneToOne
    @JoinColumn(name = "Id_wymiaru")
    private Dimiensions dimension;
    @OneToOne
    @JoinColumn(name = "Id_klienta")
    private Client client;
    @Column(name = "Ilosc_metrow", nullable = false)
    private Double metrs;
    @Column(name = "Materialy", nullable = false)
    private Double materials;
    @Column(name = "Data_przyjecia_zamowienia", nullable = false)
    private LocalDate receive_date;
    @Column(name = "Data_zamowienia", nullable = false)
    private LocalDate order_date;
    @Column(name = "Uwagi", nullable = true)
    private String Note;



    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public Dimiensions getDimension() {
        return dimension;
    }

    public void setDimension(Dimiensions dimension) {
        this.dimension = dimension;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Double getMetrs() {
        return metrs;
    }

    public void setMetrs(Double metrs) {
        this.metrs = metrs;
    }

    public Double getMaterials() {
        return materials;
    }

    public void setMaterials(Double materials) {
        this.materials = materials;
    }

    public LocalDate getReceive_date() {
        return receive_date;
    }

    public void setReceive_date(LocalDate receive_date) {
        this.receive_date = receive_date;
    }

    public LocalDate getOrder_date() {
        return order_date;
    }

    public void setOrder_date(LocalDate order_date) {
        this.order_date = order_date;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public Orders(Dimiensions dimension, Client client, Double metrs, Double materials, LocalDate receive_date, LocalDate order_date, String note) {
        this.dimension = dimension;
        this.client = client;
        this.metrs = metrs;
        this.materials = materials;
        this.receive_date = receive_date;
        this.order_date = order_date;
        Note = note;
    }

    public Orders() {
    }
}
