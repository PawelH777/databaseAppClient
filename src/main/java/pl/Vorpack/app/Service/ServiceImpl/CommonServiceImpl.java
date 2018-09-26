package pl.Vorpack.app.Service.ServiceImpl;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.Vorpack.app.Service.CommonService;

import java.io.IOException;

public class CommonServiceImpl implements CommonService {

    @Override
    public void openScene(String resource, String title, Boolean isMaximized) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
        Pane anchorPane = fxmlLoader.load();
        Scene scene = new Scene(anchorPane);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setMaximized(isMaximized);
        stage.showAndWait();
    }
}
