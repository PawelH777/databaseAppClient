package pl.Vorpack.app.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import pl.Vorpack.app.JsonClass.LocalDateDeserializer;
import pl.Vorpack.app.JsonClass.LocalDateSerializer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by Paweł on 2018-02-03.
 */

public class ordersStory {
    private Long order_id;
    private Dimiensions dimension;
    private Client client;
    private BigDecimal metrs;
    private BigDecimal materials;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate receive_date;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate order_date;
    private String Note;

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

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public ordersStory(Dimiensions dimension, Client client, BigDecimal metrs, BigDecimal materials, LocalDate receive_date, LocalDate order_date, String note) {
        this.dimension = dimension;
        this.client = client;
        this.metrs = metrs;
        this.materials = materials;
        this.receive_date = receive_date;
        this.order_date = order_date;
        Note = note;
    }

    public ordersStory() {
    }
}
