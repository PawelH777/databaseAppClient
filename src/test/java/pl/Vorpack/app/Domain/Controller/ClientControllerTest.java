package pl.Vorpack.app.Domain.Controller;

import com.jfoenix.controls.JFXComboBox;
import de.saxsys.javafx.test.JfxRunner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.Vorpack.app.Controller.ClientController.ClientController;
import pl.Vorpack.app.Domain.Clients;
import pl.Vorpack.app.Service.ClientService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static pl.Vorpack.app.Constans.ClientColumn.*;

@RunWith(JfxRunner.class)
public class ClientControllerTest {
    @InjectMocks
    private ClientController controller;
    @Mock
    private ClientService clientsService;
    private JFXComboBox<String> filterComboBox = new JFXComboBox<>();
    private FilteredList<Clients> filteredClients;
    private SortedList<Clients> sortedClients;

    @Before
    public void start(){
        MockitoAnnotations.initMocks(this);
        Clients firstExpectedClient = new Clients("first company");
        Clients secondExpectedClient = new Clients("second company");
        List<Clients> clients = new ArrayList<>(Arrays.asList(firstExpectedClient, secondExpectedClient));
        filteredClients = new FilteredList<>(FXCollections.observableArrayList(clients), c -> true);
        sortedClients = new SortedList<>(filteredClients);
    }

    @Test
    public void shouldInitialize() throws Exception {
        //given
        controller.setClientService(clientsService);
        ObservableList<String> expectedFilterComboBoxItems =
                FXCollections.observableArrayList(new ArrayList<>(Arrays.asList(ALL, ID, FIRM_NAME)));
        boolean expectedButtonsDisableValue = true;
        SortedList expectedClients = sortedClients;
        Mockito.when(clientsService.getPreparedData()).thenReturn(filteredClients);

        //when
        controller.initialize();
        boolean actualBtnModifyDisableValue = controller.btnModify.isDisable();
        boolean actualBtnDeleteDisableValue = controller.btnDelete.isDisable();
        boolean actualTxtSearchDisableValue = controller.txtSearch.isDisable();
        SortedList<Clients> actualClients = controller.clientsViewer.getItems().sorted();
        ObservableList<String> actualFilterComboBoxItems = controller.filterComboBox.getItems();

        //then
        assertEquals(expectedFilterComboBoxItems, actualFilterComboBoxItems);
        assertEquals(expectedButtonsDisableValue, actualBtnModifyDisableValue);
        assertEquals(expectedButtonsDisableValue, actualBtnDeleteDisableValue);
        assertEquals(expectedButtonsDisableValue, actualTxtSearchDisableValue);
        assertEquals(expectedClients, actualClients);
    }
}
