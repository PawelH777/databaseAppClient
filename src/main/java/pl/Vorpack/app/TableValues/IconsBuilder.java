package pl.Vorpack.app.TableValues;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import pl.Vorpack.app.Data.TraysStatus;

public class IconsBuilder {
    public static FontAwesomeIconView build(TraysStatus traysStatus){
        if(traysStatus.equals(TraysStatus.WAITING))
            return new FontAwesomeIconView(FontAwesomeIcon.HOURGLASS_1);

        if(traysStatus.equals(TraysStatus.IN_PROGRESS))
            return new FontAwesomeIconView(FontAwesomeIcon.REFRESH);

        if(traysStatus.equals(TraysStatus.FINISHED))
            return new FontAwesomeIconView(FontAwesomeIcon.CHECK);

        if(traysStatus.equals(TraysStatus.ABANDONED))
            return new FontAwesomeIconView(FontAwesomeIcon.BAN);

        return null;
    }
}
