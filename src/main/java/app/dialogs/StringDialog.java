package app.dialogs;

import app.main.Quartz;
import material.Padding;
import material.component.MaterialTextPane;
import material.constants.Size;
import material.utils.GraphicsUtils;
import material.window.MaterialWindow;
import material.window.win32procedures.DefaultDecorationParameter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class StringDialog extends QuartzDialog {
    private static final String NAME = "Error";
    private static Size minSize = Size.getGoldenSize(120);
    private Size SIZE = Size.getGoldenSize(120);
    private static final MaterialWindow window = Quartz.getInstance().getWindow();
    public static final Padding INSETS = new Padding(5);
    private final MaterialTextPane _label = new MaterialTextPane().setPadding(INSETS);

    StringDialog(String str,DialogType dialogType) {
        super(NAME,minSize,dialogType);
        if (window != null) {
            SIZE = computeRequiredSize(str);
            setSize(SIZE);
            setMinimumSize(SIZE);
            repaint();
            revalidate();
            SwingUtilities.invokeLater(() -> {

                getRootPanel().add(_label, "grow");
                _label.setText(str);
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

        strH = (strH * rows) + DefaultDecorationParameter.getTitleBarHeight() + (INSETS.getVertical()) + fontMetrics.getAscent() + fontMetrics.getDescent();
        strW = strW / rows - INSETS.getHorizontal();

        _label.setText(GraphicsUtils.truncateString(g2d, str, strW));

        return new Size((int)Math.ceil(strW), (int)Math.ceil(strH));
    }
}
