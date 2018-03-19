package pl.Vorpack.app.domain;

import javax.persistence.*;

/**
 * Created by Paweł on 2018-02-03.
 */

@Entity
@Table(name = "Klienci")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Id_klienta")
    private long client_id;

    @Column(name = "Nazwa_firmy", nullable = false)
    private String firm_name;

    public long getClient_id() {
        return client_id;
    }

    public void setClient_id(long client_id) {
        this.client_id = client_id;
    }

    public String getFirm_name() {
        return firm_name;
    }

    public void setFirm_name(String firm_name) {
        this.firm_name = firm_name;
    }


    public Client() {
    }

    public Client(String firm_name) {
        this.firm_name = firm_name;
    }
}
