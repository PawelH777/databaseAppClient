package pl.Vorpack.app.TableValues;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

public class TraysTableValue {
    private Long traysId;
    private String traysName;
    private FontAwesomeIconView statusIcon;
    private JFXButton changeStatusButton;

    public Long getTraysId() {
        return traysId;
    }

    public void setTraysId(Long traysId) {
        this.traysId = traysId;
    }

    public String getTraysName() {
        return traysName;
    }

    public void setTraysName(String traysName) {
        this.traysName = traysName;
    }

    public FontAwesomeIconView getStatusIcon() {
        return statusIcon;
    }

    public void setStatusIcon(FontAwesomeIconView statusIcon) {
        this.statusIcon = statusIcon;
    }

    public JFXButton getChangeStatusButton() {
        return changeStatusButton;
    }

    public void setChangeStatusButton(JFXButton changeStatusButton) {
        this.changeStatusButton = changeStatusButton;
    }

    public TraysTableValue(Long traysId, String traysName, FontAwesomeIconView statusIcon, JFXButton changeStatusButton) {
        this.traysId = traysId;
        this.traysName = traysName;
        this.statusIcon = statusIcon;
        this.changeStatusButton = changeStatusButton;
    }
}
