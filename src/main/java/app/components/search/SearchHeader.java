package app.components.search;

import app.components.Icons;
import app.settings.constraints.ComponentParameters;
import material.Padding;
import material.animation.MaterialFixedTimer;
import material.component.MaterialIconButton;
import material.containers.MaterialPanel;
import material.theme.enums.Elevation;
import material.utils.Log;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import javax.swing.*;

public class SearchHeader extends MaterialPanel {
    private static final int TYPING_DELAY = 500_000_000; // Adjust the delay duration as needed

    private final MaterialFixedTimer typingTimer;
    private JTextField textField;
    private static SearchListener SEARCH_LISTENER = null;
    private static final float ICON_SIZE_RATIO = 0.8f;
    private static final int BUTTON_SIZE = 30;
    private static final Padding PADDING = new Padding(5);
    private final MaterialIconButton undoButton = new MaterialIconButton(Icons.PREV_ICON,"",false).setCornerRadius(BUTTON_SIZE).setIconSizeRatio(ICON_SIZE_RATIO).setPadding(PADDING);
    private final MaterialIconButton redoButton = new MaterialIconButton(Icons.NEXT_ICON,"",false).setCornerRadius(BUTTON_SIZE).setIconSizeRatio(ICON_SIZE_RATIO).setPadding(PADDING);
    private static final MaterialIconButton settingsButton = new MaterialIconButton(FluentUiRegularMZ.SETTINGS_28).setTransparentBackground(true).setCornerRadius(BUTTON_SIZE).setIconSizeRatio(ICON_SIZE_RATIO).setPadding(PADDING);
    private final SearchBar searchBar = new SearchBar();
    private static final String buttonConstraints = " w " + BUTTON_SIZE + "!, h " + BUTTON_SIZE + "!";

    private long startTimer = -1;
    public SearchHeader() {
        super(new MigLayout("fill, inset 10"));
        setElevationDP(getDefaultElevation());

        add(undoButton, "west, gapX 10, " + buttonConstraints);
        add(redoButton, "west, gapX 10, " + buttonConstraints);
        add(searchBar, "gapX 30, growX, h " + ComponentParameters.SEARCH_PANEL_HEIGHT + "!, w 100");
        add(Box.createVerticalStrut(10), "east, " + buttonConstraints);
        add(settingsButton, "east, " + buttonConstraints);
        redoButton.setEnabled(false);
        undoButton.setEnabled(false);

        redoButton.addLeftClickListener(c -> {
            Log.success("Redo");
        });

        undoButton.addLeftClickListener(c -> {
            Log.success("Undo");
        });
        // Create a timer with a delay
        typingTimer = new MaterialFixedTimer(100) {
            @Override
            public void tick(float deltaMillis) {
                if(startTimer != -1 && System.nanoTime() - startTimer > TYPING_DELAY) {
                    searchCallback();
                    startTimer = -1;
                    Log.info("search called");
                }
            }
        };
        typingTimer.start();
        searchBar.addTextChangedListener(newText -> accumulateTime());
    }

    private void accumulateTime() {
        synchronized (typingTimer) {
            startTimer = System.nanoTime();
        }
    }

    public Elevation getScrollingElevation() {
        return Elevation.get(ComponentParameters.VIEWER_ELEVATION_DP.ordinal() + 1);
    }

    public MaterialIconButton getUndoButton() {
        return undoButton;
    }

    public MaterialIconButton getRedoButton() {
        return redoButton;
    }

    public Elevation getDefaultElevation() {
        return ComponentParameters.VIEWER_ELEVATION_DP;
    }

    public SearchBar getSearchBar() {
        return searchBar;
    }

    public void setSearchListener(@Nullable SearchListener listener) {
        SEARCH_LISTENER = listener;
    }

    private void searchCallback() {
        if (SEARCH_LISTENER != null)
            SEARCH_LISTENER.search(searchBar.getTextBox().getText().strip());
    }

    public void setSearchBarVisible(boolean b) {
        searchBar.setVisible(b);
    }


    public interface SearchListener {
        void search(String text);
    }
}
