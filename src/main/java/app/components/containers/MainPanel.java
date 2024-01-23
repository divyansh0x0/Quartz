package app.components.containers;

import app.audio.player.AphroditeAudioController;
import app.components.enums.ViewType;
import app.components.spectrum.SpectrumViewer;
import app.main.Aphrodite;
import app.settings.StartupSettings;
import material.containers.MaterialPanel;
import material.utils.OsUtils;
import material.window.DecorationParameters;
import material.window.MaterialWindow;
import material.window.buttons.CloseButton;
import material.window.buttons.MaxRestoreButton;
import material.window.buttons.MinimizeButton;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import static app.settings.constraints.ComponentParameters.CAPTION_BAR_CONSTRAINT;
import static app.settings.constraints.ComponentParameters.CAPTION_BUTTONS_CONSTRAINT;

public class MainPanel extends MaterialPanel {
    private @NotNull ViewType currentView = StartupSettings.DEFAULT_MAIN_PANEL_ACTIVE_VIEW;
    public final MaterialPanel _captionBar = new MaterialPanel(new MigLayout("insets 0, fill"));
    private final SideNavigationPanel _sidePanel = SideNavigationPanel.getInstance();

    private final ViewManagerPanel _defaultView = ViewManagerPanel.getInstance();
    private final SpectrumViewer _spectrumViewer = SpectrumViewer.getInstance();
    private static MainPanel instance;
    private Rectangle MouseDragArea;


    private MainPanel() {
        setLayout(new MigLayout("fill, insets 0"));
        switchView(currentView);
        initTitleBar();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    _defaultView.revalidate();
                    _sidePanel.revalidate();
                });
//                revalidate();
            }
        });

        MaterialWindow window = Aphrodite.getInstance().getWindow();
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Rectangle rect = MouseDragArea == null ? new Rectangle(0, 0, 0, 0) : MouseDragArea;
                rect.setSize(window.getWidth() - DecorationParameters.getControlBoxWidth(), DecorationParameters.getTitleBarHeight());
                Aphrodite.getInstance().getWindow().setMouseDragArea(rect);
            }
        });
    }

    private void initTitleBar() {
        if (OsUtils.isCustomWindowSupported()) {
            add(_captionBar, CAPTION_BAR_CONSTRAINT);
//            _captionBar.setAnimatedColorChange(false);
            _captionBar.add(new CloseButton(), CAPTION_BUTTONS_CONSTRAINT);
            _captionBar.add(new MaxRestoreButton(), CAPTION_BUTTONS_CONSTRAINT);
            _captionBar.add(new MinimizeButton(), CAPTION_BUTTONS_CONSTRAINT);
        }
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
        if (OsUtils.isWindowsVistaOrLater()) {
            _captionBar.setElevationDP(_defaultView.getSearchHeader().getElevationDP());
            add(_captionBar, CAPTION_BAR_CONSTRAINT);
        }

        PlaybackControlPanel.SPECTRUM_BUTTON.setActive(false);
        repaint();
        revalidate();
        _defaultView.repaint();
        _defaultView.validate();
    }

    private void switchToSpectrumView() {
        removeAll();
        if (OsUtils.isWindowsVistaOrLater()) {
            _captionBar.setElevationDP(_spectrumViewer.getElevationDP());
            add(_captionBar, CAPTION_BAR_CONSTRAINT);
            _captionBar.repaint();
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
