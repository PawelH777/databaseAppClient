package pl.Vorpack.app.Domain.Controller.Client;

import org.testfx.framework.junit.ApplicationTest;

public class ClientEditorControllerTest extends ApplicationTest {
//    private ClientEditorController controller;
//
//    @Mock
//    private VBox vBoxMock;
//
//    @Spy
//    private ClientService clientServiceSpy;
//
//    @Override
//    public void start(Stage primaryStage) throws IOException {
//        MockitoAnnotations.initMocks(this);
//        StringBuilder builder = new StringBuilder();
//        builder.append(ActionConstans.IS_UNFINISHED);
//        showWindow(builder);
//    }
//
//    @Before
//    public void setUp(){
//    }
//
//    @Test
//    public void shouldAdd() throws NoSuchFieldException, IllegalAccessException, IOException {
//        //given
//        String expectedActionStatus = ActionConstans.IS_FINISHED;
//
//        //when
//        clickOn("#btnProceed");
//        String actualActionStatus = getActionStatus();
//
//        //then
//        assertEquals(expectedActionStatus, actualActionStatus);
//        verify(clientServiceSpy, times(1)).create(anyString());
//    }
//
//    private void showWindow(StringBuilder builder) throws IOException {
//        controller = new ClientEditorController(builder);
//        doReturn(null).when(clientServiceSpy).findByFirmName(anyString());
//        doReturn(null).when(clientServiceSpy).create(anyString());
//        controller.setClientService(clientServiceSpy);
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(PathConstans.CLIENTS_EDITOR_PANE_PATH));
//        fxmlLoader.setController(controller);
//        Pane pane = fxmlLoader.load();
//        Scene scene = new Scene(pane);
//        Stage stage = new Stage();
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.initOwner(pane.getScene().getWindow());
//        stage.setTitle("");
//        stage.setScene(scene);
//        stage.setMaximized(false);
//        stage.showAndWait();
//    }
//
//    private String getActionStatus() throws NoSuchFieldException, IllegalAccessException {
//        Field f = controller.getClass().getDeclaredField("actionStatus");
//        f.setAccessible(true);
//        StringBuilder actionStatus = (StringBuilder) f.get(controller);
//        return actionStatus.toString();
//    }
}
