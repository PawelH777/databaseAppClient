package pl.Vorpack.app.Service;

import java.io.IOException;

public interface CommonService {

    void openScene(String resource, String title, Boolean isMaximized) throws IOException;
}
