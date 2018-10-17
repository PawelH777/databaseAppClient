package pl.Vorpack.app.Service;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

public interface CommonService {

    void openScene(String resource, String title, Boolean isMaximized) throws IOException;

    void openScene(String title, Boolean isMaximized, FXMLLoader loader) throws IOException;
}
