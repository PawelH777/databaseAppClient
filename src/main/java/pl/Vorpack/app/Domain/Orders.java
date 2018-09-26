package pl.Vorpack.app.Domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import pl.Vorpack.app.JsonClass.LocalDateDeserializer;
import pl.Vorpack.app.JsonClass.LocalDateSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by Paweł on 2018-02-03.
 */
public class Orders{

    private Long order_id;
    private Clients clients;
    private BigDecimal materials;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate order_receive_date;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate order_date;

    private String order_note;

    private Long single_orders_finished;

    private Long single_orders_unfinished;

    private Boolean orderFinished;

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public Clients getClients() {
        return clients;
    }

    public void setClients(Clients clients) {
        this.clients = clients;
    }

    public BigDecimal getMaterials() {
        return materials;
    }

    public void setMaterials(BigDecimal materials) {
        this.materials = materials;
    }

    public LocalDate getOrder_receive_date() {
        return order_receive_date;
    }

    public void setOrder_receive_date(LocalDate order_receive_date) {
        this.order_receive_date = order_receive_date;
    }

    public LocalDate getOrder_date() {
        return order_date;
    }

    public void setOrder_date(LocalDate order_date) {
        this.order_date = order_date;
    }

    public String getOrder_note() {
        return order_note;
    }

    public void setOrder_note(String order_note) {
        this.order_note = order_note;
    }

    public Long getSingle_orders_finished() {
        return single_orders_finished;
    }

    public void setSingle_orders_finished(Long single_orders_finished) {
        this.single_orders_finished = single_orders_finished;
    }

    public Long getSingle_orders_unfinished() {
        return single_orders_unfinished;
    }

    public void setSingle_orders_unfinished(Long single_orders_unfinished) {
        this.single_orders_unfinished = single_orders_unfinished;
    }
    public Boolean getOrderFinished() {
        return orderFinished;
    }

    public void setOrderFinished(Boolean orderFinished) {
        this.orderFinished = orderFinished;
    }

    public Orders(Long order_id, Clients clients, BigDecimal materials, LocalDate order_receive_date, LocalDate order_date,
                  String order_note, Long single_orders_finished, Long single_orders_unfinished, Boolean orderFinished) {
        this.order_id = order_id;
        this.clients = clients;
        this.materials = materials;
        this.order_receive_date = order_receive_date;
        this.order_date = order_date;
        this.order_note = order_note;
        this.single_orders_finished = single_orders_finished;
        this.single_orders_unfinished = single_orders_unfinished;
        this.orderFinished = orderFinished;
    }

    public Orders(Clients clients, BigDecimal materials, LocalDate order_receive_date, LocalDate order_date, String order_note,
                  Long single_orders_finished, Long single_orders_unfinished, Boolean orderFinished) {
        this.clients = clients;
        this.materials = materials;
        this.order_receive_date = order_receive_date;
        this.order_date = order_date;
        this.order_note = order_note;
        this.single_orders_finished = single_orders_finished;
        this.single_orders_unfinished = single_orders_unfinished;
        this.orderFinished = orderFinished;
    }

    public Orders() {
    }
}
