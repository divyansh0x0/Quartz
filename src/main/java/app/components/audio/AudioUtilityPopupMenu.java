package app.components.audio;

import app.components.Icons;
import app.settings.constraints.ComponentParameters;
import material.component.MaterialMenuItem;
import material.theme.enums.Elevation;
import material.window.MaterialPopup;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.Ikon;

import java.awt.*;

public class AudioUtilityPopupMenu {
    private static final MaterialPopup popupMenu = new MaterialPopup();
    private static final int WIDTH = 200;
    private static final int HEIGHT = MaterialMenuItem.HEIGHT;
    private static final Ikon[] icons = {Icons.PLAY_AUDIO, Icons.REMOVE_AUDIO, Icons.DELETE_PERMANENTLY, Icons.MUSIC_ICON, Icons.EDIT_FILE_ATTR};
    private static final MaterialMenuItem _playBtn =  new MaterialMenuItem(icons[0], "Play");
    private static final MaterialMenuItem _removeBtn = new MaterialMenuItem(icons[1], "Remove");
    private static final MaterialMenuItem _deleteBtn = new MaterialMenuItem(icons[2], "Delete");
    private static final MaterialMenuItem _propertiesBtn = new MaterialMenuItem(icons[3], "Properties");
    private static final MaterialMenuItem _metadataEditorBtn = new MaterialMenuItem(icons[4], "Edit metadata");

    static {
        popupMenu.add(_playBtn);
        popupMenu.setLayout(new MigLayout(ComponentParameters.VerticalTileFlow));
        popupMenu.setPreferredSize(new Dimension(WIDTH, MaterialMenuItem.HEIGHT * 5));
        popupMenu.add(_playBtn, MaterialMenuItem.CONSTRAINTS);
        popupMenu.add(_removeBtn, MaterialMenuItem.CONSTRAINTS);
        popupMenu.add(_deleteBtn, MaterialMenuItem.CONSTRAINTS);
        popupMenu.add(_propertiesBtn, MaterialMenuItem.CONSTRAINTS);
        popupMenu.add(_metadataEditorBtn, MaterialMenuItem.CONSTRAINTS);
        popupMenu.pack();
        popupMenu.setElevation(Elevation._6);
    }

    public AudioUtilityPopupMenu() {
        super();
    }

    public void show(Point location,AudioTile audioTile) {
        removeAllListeners();
        _playBtn.addLeftClickListener(e -> {
            if (audioTile != null) {
                audioTile.play();
            }
            popupMenu.setVisible(false);
        });
        popupMenu.show(location);
    }

    private void removeAllListeners() {
        _playBtn.removeLeftClickListeners();
    }
}
