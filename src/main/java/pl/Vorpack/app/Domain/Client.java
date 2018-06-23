package pl.Vorpack.app.Domain;

/**
 * Created by Paweł on 2018-02-03.
 */

public class Client {

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

    public Client(String firmName) {
        this.firmName = firmName;
    }

    public Client() {
    }
}
