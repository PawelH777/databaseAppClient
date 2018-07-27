package pl.Vorpack.app.Domain;

import pl.Vorpack.app.Data.TraysStatus;

public class Trays {
    private Long tray_id;
    private SingleOrders single_order;
    private String tray_name;
    private TraysStatus tray_status;

    public Long getTray_id() {
        return tray_id;
    }

    public void setTray_id(Long tray_id) {
        this.tray_id = tray_id;
    }

    public SingleOrders getSingle_order() {
        return single_order;
    }

    public void setSingle_order(SingleOrders single_order) {
        this.single_order = single_order;
    }

    public String getTray_name() {
        return tray_name;
    }

    public void setTray_name(String tray_name) {
        this.tray_name = tray_name;
    }

    public TraysStatus getTray_status() {
        return tray_status;
    }

    public void setTray_status(TraysStatus tray_status) {
        this.tray_status = tray_status;
    }

    public Trays(Long tray_id, SingleOrders single_order, String tray_name) {
        this.tray_id = tray_id;
        this.single_order = single_order;
        this.tray_name = tray_name;
    }

    public Trays(SingleOrders single_order, String tray_name) {
        this.single_order = single_order;
        this.tray_name = tray_name;
    }

    public Trays() {
    }
}
