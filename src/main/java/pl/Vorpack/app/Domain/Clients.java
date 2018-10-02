package pl.Vorpack.app.Domain;

import java.util.Objects;

/**
 * Created by Paweł on 2018-02-03.
 */

public class Clients {

    private long client_id;

    private String firmName;

    public long getClient_id() {
        return client_id;
    }

    public void setClient_id(long client_id) {
        this.client_id = client_id;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public Clients(String firmName) {
        this.firmName = firmName;
    }

    public Clients() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clients clients = (Clients) o;
        return client_id == clients.client_id &&
                Objects.equals(firmName, clients.firmName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client_id, firmName);
    }
}
