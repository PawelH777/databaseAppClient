package pl.Vorpack.app.Domain.Service;

import de.saxsys.javafx.test.JfxRunner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.Vorpack.app.DatabaseAccess.ClientAccess;
import pl.Vorpack.app.Domain.Clients;
import pl.Vorpack.app.Service.ClientService;
import pl.Vorpack.app.Service.ServiceImpl.ClientServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static pl.Vorpack.app.Constans.ClientColumnConstans.ALL;
import static pl.Vorpack.app.Constans.ClientColumnConstans.ID;

@RunWith(JfxRunner.class)
public class ClientServiceTest {
    private ClientService clientService = new ClientServiceImpl();

    @Mock
    private ClientAccess clientAccess;

    private Clients expectedClient;
    private List<Clients> clients;
    private ObservableList<Clients> data;

    @Before
    public void start(){
        MockitoAnnotations.initMocks(this);
        expectedClient = new Clients(1L, "Paweł");
        Clients secondClient = new Clients(2L, "Adam");
        clients = new ArrayList<>(Arrays.asList(expectedClient, secondClient));
        data = FXCollections.observableArrayList(clients);
    }

    @Test
    public void shouldFindAll(){
        //given
        FilteredList expectedClients =  new FilteredList<>(data, p -> true);
        doReturn(clients).when(clientAccess).findAll();
        clientService = new ClientServiceImpl(clientAccess);

        //when
        FilteredList<Clients> actualClients = clientService.findAll();

        //then
        assertEquals(expectedClients, actualClients);
    }

    @Test
    public void shouldFindByName(){
        //given
        doReturn(clients).when(clientAccess).findByFirmName(anyString());
        clientService = new ClientServiceImpl(clientAccess);

        //when
        Clients actualClient = clientService.findByFirmName("Paweł");

        //then
        assertEquals(expectedClient, actualClient);
    }

    @Test
    public void shouldNotFindByName(){
        //given
        doReturn(new ArrayList<>()).when(clientAccess).findByFirmName(anyString());
        clientService = new ClientServiceImpl(clientAccess);

        //when
        Clients actualClient = clientService.findByFirmName("Paweł");

        //then
        assertNull(actualClient);
    }

    @Test
    public void shouldCreate(){
        //given
        doReturn(new ArrayList<>()).doReturn(clients).when(clientAccess).findByFirmName(anyString());
        doNothing().when(clientAccess).create(any(Clients.class));
        clientService = new ClientServiceImpl(clientAccess);

        //when
        Clients actualClient = clientService.create("Paweł");

        //then
        assertEquals(expectedClient, actualClient);
        Mockito.verify(clientAccess, times(1)).create(any(Clients.class));
    }

    @Test
    public void shouldReturnFoundObjectByCreate(){
        //given
        doReturn(clients).when(clientAccess).findByFirmName(anyString());
        doNothing().when(clientAccess).create(any(Clients.class));
        clientService = new ClientServiceImpl(clientAccess);

        //when
        Clients actualClient = clientService.create("Paweł");

        //then
        assertEquals(expectedClient, actualClient);
        Mockito.verify(clientAccess, times(0)).create(any(Clients.class));
    }

    @Test
    public void shouldFilterAllAndReturnAll(){
        //given
        FilteredList<Clients> actualClients =  new FilteredList<>(data, p -> true);

        //when
        clientService.filter(ALL, "", actualClients);
        FilteredList expectedClients =  new FilteredList<>(data, p -> true);

        //then
        assertEquals(expectedClients.get(0), actualClients.get(0));
        assertEquals(expectedClients.size(), actualClients.size());
    }

    @Test
    public void shouldFilterAll(){
        //given
        FilteredList<Clients> actualClients =  new FilteredList<>(data, p -> true);

        //when
        clientService.filter(ALL, "Pa", actualClients);
        ObservableList<Clients> clientsList =  FXCollections.observableArrayList(Collections.singletonList(expectedClient));
        FilteredList<Clients> expectedClients = new FilteredList<>(clientsList);

        //then
        assertEquals(expectedClients.get(0), actualClients.get(0));
        assertEquals(expectedClients.size(), actualClients.size());
    }

    @Test
    public void shouldFilterAllAndReturnEmptyList(){
        //given
        int expectedClientsSize = 0;
        FilteredList<Clients> actualClients =  new FilteredList<>(data, p -> true);

        //when
        clientService.filter(ALL, "z", actualClients);

        //then
        assertEquals(expectedClientsSize, actualClients.size());
    }

    @Test
    public void shouldFilterId(){
        //given
        FilteredList<Clients> actualClients =  new FilteredList<>(data, p -> true);

        //when
        clientService.filter(ID, "1", actualClients);
        ObservableList<Clients> clientsList =  FXCollections.observableArrayList(Collections.singletonList(expectedClient));
        FilteredList<Clients> expectedClients = new FilteredList<>(clientsList);

        //then
        assertEquals(expectedClients.get(0), actualClients.get(0));
        assertEquals(expectedClients.size(), actualClients.size());
    }

    @Test
    public void shouldFilterIdAndReturnEmptyList(){
        //given
        int expectedClientsSize = 0;
        FilteredList<Clients> actualClients =  new FilteredList<>(data, p -> true);

        //when
        clientService.filter(ID, "0", actualClients);
        ObservableList<Clients> clientsList =  FXCollections.observableArrayList(Collections.singletonList(expectedClient));
        FilteredList<Clients> expectedClients = new FilteredList<>(clientsList);

        //then
        assertEquals(expectedClientsSize, actualClients.size());
    }
}
