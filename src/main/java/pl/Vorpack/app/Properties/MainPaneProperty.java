package pl.Vorpack.app.Properties;

import javafx.beans.property.*;

/**
 * Created by Paweł on 2018-02-17.
 */
public class MainPaneProperty {

    private BooleanProperty disableModifyBtn = new SimpleBooleanProperty(true);

    private  BooleanProperty disableDeleteBtn = new SimpleBooleanProperty(true);

    private BooleanProperty disableBtn = new SimpleBooleanProperty(true);

    private BooleanProperty disableBtnProocedandExit = new SimpleBooleanProperty(true);

    public boolean isDisableModifyBtn() {
        return disableModifyBtn.get();
    }

    public BooleanProperty disableModifyBtnProperty() {
        return disableModifyBtn;
    }

    public void setDisableModifyBtn(boolean disableModifyBtn) {
        this.disableModifyBtn.set(disableModifyBtn);
    }

    public boolean isDisableDeleteBtn() {
        return disableDeleteBtn.get();
    }

    public BooleanProperty disableDeleteBtnProperty() {
        return disableDeleteBtn;
    }

    public void setDisableDeleteBtn(boolean disableDeleteBtn) {
        this.disableDeleteBtn.set(disableDeleteBtn);
    }

    public boolean getDisableBtn() {
        return disableBtn.get();
    }

    public BooleanProperty disableBtnProperty() {
        return disableBtn;
    }

    public void setDisableBtn(boolean disableBtn) {
        this.disableBtn.set(disableBtn);
    }

    public boolean isDisableBtnProocedandExit() {
        return disableBtnProocedandExit.get();
    }

    public BooleanProperty disableBtnProocedandExitProperty() {
        return disableBtnProocedandExit;
    }

    public void setDisableBtnProocedandExit(boolean disableBtnProocedandExit) {
        this.disableBtnProocedandExit.set(disableBtnProocedandExit);
    }
}
