package app.dialogs;

import app.main.Frost;
import app.settings.constraints.ComponentParameters;
import material.component.MaterialTextPane;
import material.constants.Size;
import material.utils.GraphicsUtils;
import material.window.DecorationParameters;
import material.window.MaterialWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class ErrorDialog extends FrostDialog {
    private static final String NAME = "Error";
    private static Size minSize = Size.getGoldenSize(120);
    private Size SIZE = Size.getGoldenSize(120);
    private final MaterialTextPane _label = new MaterialTextPane();
    private static final MaterialWindow window = Frost.getInstance().getWindow();

    ErrorDialog(String err) {
        super(NAME,minSize);
        if (window != null) {
            SIZE = computeRequiredSize(err);
            setSize(SIZE);
            setMinimumSize(SIZE);
            repaint();
            revalidate();
            SwingUtilities.invokeLater(() -> {

                getRootPanel().setDialogType(DialogType.ERROR);
                getRootPanel().add(_label, ComponentParameters.DIALOG_CONTENT_CONSTRAINT);
                _label.setText(err);
                _label.setPreferredSize(SIZE);
                _label.setAlignmentX(Component.CENTER_ALIGNMENT);
                _label.setMaximumSize(SIZE);
                this.pack();
                this.setVisible(true);
            });
        }
    }
    private Size computeRequiredSize(String str){
        Graphics2D g2d = (Graphics2D) new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB).getGraphics();
        FontMetrics fontMetrics = g2d.getFontMetrics();
        double strH = fontMetrics.getHeight();
        double strW = fontMetrics.stringWidth(str);
        double maxW = MAX_SIZE.getWidth();
        double rows = 1;
        if(strW > maxW)
            rows = strW/maxW;

        strH = (strH * rows) + DecorationParameters.getTitleBarHeight() + (DialogRootPanel.INSETS * 2) + fontMetrics.getAscent() + fontMetrics.getDescent();
        strW = strW / rows - DialogRootPanel.INSETS * 2;

        _label.setText(GraphicsUtils.truncateString(g2d, str, strW));

        return new Size((int)Math.ceil(strW), (int)Math.ceil(strH));
    }
}
