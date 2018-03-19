package pl.Vorpack.app.Properties;

import javafx.beans.property.*;

/**
 * Created by Paweł on 2018-02-17.
 */
public class mainPaneProperty {

    private BooleanProperty disableModifyBtn = new SimpleBooleanProperty(true);

    private  BooleanProperty disableDeleteBtn = new SimpleBooleanProperty(true);

    private BooleanProperty disableBtnProoced = new SimpleBooleanProperty(true);

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

    public boolean isDisableBtnProoced() {
        return disableBtnProoced.get();
    }

    public BooleanProperty disableBtnProocedProperty() {
        return disableBtnProoced;
    }

    public void setDisableBtnProoced(boolean disableBtnProoced) {
        this.disableBtnProoced.set(disableBtnProoced);
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
