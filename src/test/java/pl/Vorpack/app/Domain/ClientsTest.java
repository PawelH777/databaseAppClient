package pl.Vorpack.app.Domain;

import static org.junit.Assert.*;

public class ClientsTest {

    @org.junit.Test
    public void getClient_id() {
        Clients clientsObject = new Clients();
        clientsObject.setFirmName("Company");
        clientsObject.setClient_id(1);
        assertEquals(1, clientsObject.getClient_id());
    }

    @org.junit.Test
    public void setClient_id() {
    }

    @org.junit.Test
    public void getFirmName() {
        Clients clientsObject = new Clients("Company");
        assertEquals("Company", clientsObject.getFirmName());
    }

    @org.junit.Test
    public void setFirmName() {
    }
}