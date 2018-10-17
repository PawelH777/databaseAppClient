package pl.Vorpack.app.Domain.Controller.Client;

import de.saxsys.javafx.test.JfxRunner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import pl.Vorpack.app.Alerts.InfoAlerts;
import pl.Vorpack.app.Constans.ActionConstans;
import pl.Vorpack.app.Controller.ClientController.ClientController;
import pl.Vorpack.app.Domain.Clients;
import pl.Vorpack.app.Service.ClientService;
import pl.Vorpack.app.Service.CommonService;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static pl.Vorpack.app.Constans.ClientColumn.*;

@RunWith(JfxRunner.class)
public class ClientControllerTest {
    private ClientController controller = new ClientController();

    @Mock
    private ClientService clientsServiceMock;

    @Mock
    private InfoAlerts infoAlertsMock;

    @Spy
    private ClientService clientServiceSpy;

    @Spy
    private CommonService commonServiceSpy;

    private List<Clients> clients;
    private FilteredList<Clients> filteredClients;
    private SortedList<Clients> sortedClients;

    @Before
    public void start() throws InterruptedException {
        MockitoAnnotations.initMocks(this);
        Clients secondExpectedClient = new Clients("first company");
        Clients firstExpectedClient = new Clients("second company");
        clients = new ArrayList<>(Arrays.asList(firstExpectedClient, secondExpectedClient));
        filteredClients = new FilteredList<>(FXCollections.observableArrayList(clients), c -> true);
        sortedClients = new SortedList<>(filteredClients);
    }

    @Test
    public void shouldInitialize() throws InterruptedException {
        //given
        ObservableList<String> expectedFilterComboBoxItems =
                FXCollections.observableArrayList(new ArrayList<>(Arrays.asList(ALL, ID, FIRM_NAME)));
        boolean expectedButtonsDisableValue = true;
        SortedList expectedClients = sortedClients;
        Mockito.when(clientsServiceMock.getPreparedData()).thenReturn(filteredClients);
        controller.setClientService(clientsServiceMock);

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

    @Test
    public void shouldDelete(){
        //given
        String expectedStatusViewerText = "Rekord został usunięty";
        Mockito.when(clientServiceSpy.getPreparedData()).thenReturn(filteredClients);
        doNothing().when(clientServiceSpy).delete(any(Clients.class));
        Mockito.when(clientServiceSpy.findAll()).thenReturn(
                new FilteredList<>(FXCollections.observableArrayList(new ArrayList<>(
                        Collections.singletonList(new Clients("second company"))
                )), c -> true));
        Mockito.when(infoAlertsMock.deleteRecord(any(String.class))).thenReturn(true);
        controller.setClientService(clientServiceSpy);
        controller.initialize();
        controller.clientsViewer.getSelectionModel().select(0);
        controller.setInfoAlerts(infoAlertsMock);

        //when
        controller.onBtnDeleteClicked();
        String actualStatusViewerText = controller.statusViewer.getText();

        //then
        Mockito.verify(clientServiceSpy, times(1)).delete(any(Clients.class));
        assertEquals(expectedStatusViewerText, actualStatusViewerText);
    }

    @Test
    public void shouldAdd() throws IOException, NoSuchFieldException, IllegalAccessException {
        //given
        String expectedStatusViewerText = InfoAlerts.getStatusWhileRecordAdded();
        doReturn(filteredClients).when(clientServiceSpy).getPreparedData();
        doReturn(  new FilteredList<>(FXCollections.observableArrayList(new ArrayList<>()), c -> true))
                .when(clientServiceSpy).findAll();
        doAnswer((Answer) invocation -> {
            Field f = controller.getClass().getDeclaredField("actionStatus");
            f.setAccessible(true);
            StringBuilder actionStatus = (StringBuilder) f.get(controller);
            actionStatus.setLength(0);
            actionStatus.append(ActionConstans.IS_FINISHED);
            return null;
        }).when(commonServiceSpy).openScene(anyString(), anyBoolean(), any(FXMLLoader.class));
        controller.setClientService(clientServiceSpy);
        controller.setCommonService(commonServiceSpy);
        controller.initialize();
        controller.clientsViewer.getSelectionModel().select(0);

        //when
        controller.onBtnAddClicked();
        String actualStatusViewerText = controller.statusViewer.getText();

        //then
        verify(commonServiceSpy, times(1))
                .openScene(anyString(), anyBoolean(), any(FXMLLoader.class));
        assertEquals(expectedStatusViewerText, actualStatusViewerText);
    }

    @Test
    public void shouldModify() throws IOException {
        //given
        String expectedStatusViewerText = InfoAlerts.getStatusWhileRecordAdded();
        doReturn(filteredClients).when(clientServiceSpy).getPreparedData();
        doReturn(  new FilteredList<>(FXCollections.observableArrayList(new ArrayList<>()), c -> true))
                .when(clientServiceSpy).findAll();
        doAnswer((Answer) invocation -> {
            Field f = controller.getClass().getDeclaredField("actionStatus");
            f.setAccessible(true);
            StringBuilder actionStatus = (StringBuilder) f.get(controller);
            actionStatus.setLength(0);
            actionStatus.append(ActionConstans.IS_FINISHED);
            return null;
        }).when(commonServiceSpy).openScene(anyString(), anyBoolean(), any(FXMLLoader.class));
        controller.setClientService(clientServiceSpy);
        controller.setCommonService(commonServiceSpy);
        controller.initialize();
        controller.clientsViewer.getSelectionModel().select(0);

        //when
        controller.onBtnModifyClicked();
        String actualStatusViewerText = controller.statusViewer.getText();

        //then
        verify(commonServiceSpy, times(1))
                .openScene(anyString(), anyBoolean(), any(FXMLLoader.class));
        assertEquals(expectedStatusViewerText, actualStatusViewerText);
    }

    @Test
    public void shouldRefresh(){
        //given
        doReturn(filteredClients).when(clientServiceSpy).getPreparedData();
        doReturn(filteredClients).when(clientServiceSpy).findAll();
        controller.setClientService(clientServiceSpy);
        controller.initialize();
        clients.add(new Clients("trzeci"));
        filteredClients =  new FilteredList<>(FXCollections.observableArrayList(clients), c -> true);
        FilteredList<Clients> expectedClients = filteredClients;

        //when
        controller.onBtnRefreshClicked();
        FilteredList<Clients> actualClients = controller.clientsViewer.getItems().filtered(p -> true);

        //then
        assertEquals(expectedClients, filteredClients);
    }
}
