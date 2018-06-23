package pl.Vorpack.app.Domain;

import static org.junit.Assert.*;

public class ClientTest {

    @org.junit.Test
    public void getClient_id() {
        Client clientObject = new Client();
        clientObject.setFirmName("Company");
        clientObject.setClient_id(1);
        assertEquals(1, clientObject.getClient_id());
    }

    @org.junit.Test
    public void setClient_id() {
    }

    @org.junit.Test
    public void getFirmName() {
        Client clientObject = new Client("Company");
        assertEquals("Company", clientObject.getFirmName());
    }

    @org.junit.Test
    public void setFirmName() {
    }
}