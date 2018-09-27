package pl.Vorpack.app.Domain.Controller;

import javafx.collections.transformation.FilteredList;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import pl.Vorpack.app.Domain.Clients;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pl.Vorpack.app.Constans.ClientColumn.*;

public class ClientControllerTest {

    List<Clients> clients;
    FilteredList<Clients> filteredClients;

    @BeforeEach
    public void start(){

    }

    @Test
    public void shouldInitialize(){
        List<String> expectedFilterComboBoxItems = new ArrayList<>(Arrays.asList(ALL, ID, FIRM_NAME));
        String expectedFirstColumn = "client_id";
        String expectedSecondColumn = "firmName";
        boolean expectedButtonsDisableValue = true;

    }
}
