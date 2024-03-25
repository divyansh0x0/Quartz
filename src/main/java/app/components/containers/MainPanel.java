package app.components.containers;

import app.audio.player.AphroditeAudioController;
import app.components.enums.ViewType;
import app.components.spectrum.SpectrumViewer;
import app.main.Aphrodite;
import app.settings.StartupSettings;
import app.settings.constraints.ComponentParameters;
import material.containers.MaterialPanel;
import material.utils.OsInfo;
import material.window.buttons.CloseButton;
import material.window.buttons.MaxRestoreButton;
import material.window.buttons.MinimizeButton;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MainPanel extends MaterialPanel {
    public  final MaxRestoreButton MAX_RESTORE_BUTTON = new MaxRestoreButton();
    public  final CloseButton CLOSE_BUTTON = new CloseButton();
    public  final MinimizeButton MINIMIZE_BUTTON = new MinimizeButton();

    private int CAPTION_BUTTON_SIZE = 30;
    private @NotNull ViewType currentView = StartupSettings.DEFAULT_MAIN_PANEL_ACTIVE_VIEW;
    public final MaterialPanel _titleBar = new MaterialPanel(new MigLayout("insets 0, fill,gap 0")).setElevationDP(null);
    private final SideNavigationPanel _sidePanel = SideNavigationPanel.getInstance();

    private final ViewManagerPanel _defaultView = ViewManagerPanel.getInstance();
    private final SpectrumViewer _spectrumViewer = SpectrumViewer.getInstance();
    private static MainPanel instance;
    private Rectangle MouseDragArea;


    private MainPanel() {
        setLayout(new MigLayout("fill, insets 0"));
        setElevationDP(ComponentParameters.VIEWER_ELEVATION_DP);
        switchView(currentView);
        initTitleBar();

    }

    private void initTitleBar() {
        if (OsInfo.isCustomWindowSupported()) {
            add(_titleBar, "north,align right, w " + (CAPTION_BUTTON_SIZE * 3) + "!, h " + CAPTION_BUTTON_SIZE + "!");
            addCaptionButtons();

        }
    }
    private void addCaptionButtons() {
        String captionBtnConstraint = "growY,w " + CAPTION_BUTTON_SIZE + "!";
        _titleBar.add(MINIMIZE_BUTTON,captionBtnConstraint);
        _titleBar.add(MAX_RESTORE_BUTTON, captionBtnConstraint);
        _titleBar.add(CLOSE_BUTTON, captionBtnConstraint);
        Aphrodite.getInstance().getWindow().setCustomCaptionBar(_titleBar);
    }
    public synchronized void switchView(@NotNull ViewType mainPanelView) {
        currentView = mainPanelView;
        AphroditeAudioController.getInstance().enableVisualizerSampling(mainPanelView == ViewType.SpectrumView);

        switch (mainPanelView) {
            case DefaultView -> switchToDefaultView();
            case SpectrumView -> switchToSpectrumView();
        }

    }

    private void switchToDefaultView() {
        removeAll();
        add(_sidePanel, "west, h 0:100%:100%");
        add(_defaultView, "center, grow");
        if (OsInfo.isWindowsVistaOrLater()) {
            setElevationDP(_defaultView.getSearchHeader().getElevationDP());
            initTitleBar();
        }

        PlaybackControlPanel.SPECTRUM_BUTTON.setActive(false);
        repaint();
        revalidate();
        _defaultView.repaint();
        _defaultView.validate();
    }



    private void switchToSpectrumView() {
        removeAll();
        if (OsInfo.isWindowsVistaOrLater()) {
            setElevationDP(_spectrumViewer.getElevationDP());
            initTitleBar();
        }
        add(_spectrumViewer, "grow");
        PlaybackControlPanel.SPECTRUM_BUTTON.setActive(true);
        _spectrumViewer.repaint();
        repaint();
        revalidate();

    }


    public @NotNull ViewType getCurrentView() {
        return currentView;
    }

    public static MainPanel getInstance() {
        if (instance == null)
            instance = new MainPanel();
        return instance;
    }


}
