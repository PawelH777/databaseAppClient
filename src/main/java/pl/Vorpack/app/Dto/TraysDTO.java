package pl.Vorpack.app.Dto;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

public class TraysDTO {
    private Long traysId;
    private String traysName;
    private String statusIcon;
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

    public String getStatusIcon() {
        return statusIcon;
    }

    public void setStatusIcon(String statusIcon) {
        this.statusIcon = statusIcon;
    }

    public JFXButton getChangeStatusButton() {
        return changeStatusButton;
    }

    public void setChangeStatusButton(JFXButton changeStatusButton) {
        this.changeStatusButton = changeStatusButton;
    }

    public TraysDTO(Long traysId, String traysName, String statusIcon, JFXButton changeStatusButton) {
        this.traysId = traysId;
        this.traysName = traysName;
        this.statusIcon = statusIcon;
        this.changeStatusButton = changeStatusButton;

        changeStatusButton.setOnAction(event -> {

        });
    }
}
