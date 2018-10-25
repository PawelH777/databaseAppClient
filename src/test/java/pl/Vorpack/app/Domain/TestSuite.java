package pl.Vorpack.app.Domain;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import pl.Vorpack.app.Domain.Controller.Client.ClientControllerTest;
import pl.Vorpack.app.Domain.Controller.Client.ClientEditorControllerTest;
import pl.Vorpack.app.Domain.Service.ClientServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({  ClientControllerTest.class, ClientEditorControllerTest.class, ClientServiceTest.class})
public class TestSuite {
}
