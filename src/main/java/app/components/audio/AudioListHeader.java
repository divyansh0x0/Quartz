package app.components.audio;

import app.audio.FrostAudio;
import app.components.Icons;
import app.components.enums.SortingPolicy;
import material.component.MaterialComboBox;
import material.component.MaterialIconButton;
import material.component.MaterialLabel;
import material.component.MaterialMenuItem;
import material.component.enums.LabelStyle;
import material.containers.MaterialPanel;
import net.miginfocom.swing.MigLayout;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class AudioListHeader extends MaterialPanel {
    private boolean showBackButton;
    private final CompoundAudioImage HeaderImage = new CompoundAudioImage();
    private final MaterialLabel Heading = new MaterialLabel("Heading", LabelStyle.PRIMARY);
    private final MaterialLabel SubHeading = new MaterialLabel("SubHeading", LabelStyle.SECONDARY);
    private final MaterialIconButton BackButton = new MaterialIconButton(Icons.PREV_ICON).setTransparentBackground(true);
    private static final String[] COMBO_BOX_ITEMS = {"Title", "Artist", "Duration", "Date added"};
    private final MaterialComboBox dropDown = new MaterialComboBox(COMBO_BOX_ITEMS);
    private Runnable backButtonClickHandler;
    private MaterialComboBox.ComboSelectionChanged comboSelectionChangedListener;
    public AudioListHeader(boolean showBackButton) {
        super(new MigLayout("nogrid, flowX, fill, insets 10"));
//        dropDown.setActiveItem(0);

        MaterialPanel textContainer = new MaterialPanel(new MigLayout("alignY center, alignX Left, nogrid, flowy, fillX, insets 0"));

        add(HeaderImage, "h 200!, w 200!");
        add(textContainer, "growX");
        add(dropDown,"south, w 200!, h 30!, gapX 10, align right");

        textContainer.add(Heading,"growX");
        textContainer.add(SubHeading, "growX");

        textContainer.setElevationDP(null);
        setElevationDP(null);
        setShowBackButton(showBackButton);
        BackButton.addLeftClickListener(e -> backButtonClickHandler.run());

        Heading.setFontSize(18);
        SubHeading.setFontSize(10);

        dropDown.setSelectedItemIndex(0);
        dropDown.addSelectionListener(new MaterialComboBox.ComboSelectionChanged() {
            @Override
            public void selectionChanged(MaterialMenuItem item, String name) {
                comboSelectionChangedListener.selectionChanged(item,name);
            }
        });
    }



    public void setHeading(String text) {
        Heading.setText(text);
    }

    public void setSubHeading(String text) {
        SubHeading.setText(text);
    }

    public void setFrostAudios(ArrayList<FrostAudio> audios){
        HeaderImage.setAudioFiles(audios);
        HeaderImage.revalidate();
        repaint();
        revalidate();
    }

    public boolean isShowBackButton() {
        return showBackButton;
    }

    public void setShowBackButton(boolean showBackButton) {
        this.showBackButton = showBackButton;
        if(showBackButton)
            add(BackButton, "north, w 50!, h 50!");
        else remove(BackButton);
    }

    public void setBackButtonClickHandler(Runnable runnable) {
        this.backButtonClickHandler = runnable;
    }

    public MaterialComboBox.ComboSelectionChanged getComboSelectionChangedListener() {
        return comboSelectionChangedListener;
    }

    public void setComboSelectionChangedListener(MaterialComboBox.ComboSelectionChanged comboSelectionChangedListener) {
        this.comboSelectionChangedListener = comboSelectionChangedListener;
    }
    //"Title", "Artist", "Album", "Date added"
    public static SortingPolicy getSortingPolicy(String s) {
        switch (s.toLowerCase(Locale.ROOT)){
            case "title" -> {
                return SortingPolicy.TITLE;
            }
            case "artist" -> {
                return SortingPolicy.ARTIST;
            }
            case "duration" -> {
                return SortingPolicy.DURATION;
            }
            case "date added" -> {
                return SortingPolicy.DATE_ADDED;
            }
            default -> {
                return SortingPolicy.UNKNOWN;
            }
        }
    }
}
