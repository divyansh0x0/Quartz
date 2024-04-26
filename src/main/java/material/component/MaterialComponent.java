package material.component;

import material.MaterialParameters;
import material.animation.AnimationLibrary;
import material.fonts.MaterialFonts;
import material.listeners.SelectionListener;
import material.theme.ThemeColors;
import material.theme.ThemeManager;
import material.theme.enums.Elevation;
import material.utils.GraphicsUtils;
import material.utils.Log;
import material.utils.structures.LanguageCompatibleString;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public abstract class MaterialComponent extends JComponent implements Serializable {
    private boolean allowMouseAnimation = true;
    private static final Point LastMouseLocation = new Point();
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private final ArrayList<SelectionListener> selectionListeners = new ArrayList<>();

    private Font font;
    private static Font defaultFont = new Font("Dialog", Font.PLAIN, 16);
    private boolean isBold = false;
    private boolean isItalic = false;
    private Dimension preferredSize;
    private boolean isSelected;
    private float fontSize = 14;
    boolean applyNewFont = false;
    private static MaterialComponent animatingComp = null;

    static {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            Object source = event.getSource();
            if (event.getID() == MouseEvent.MOUSE_EXITED) {
                if (source instanceof MaterialComponent comp) {
                    if (animatingComp != null && animatingComp == comp) {
                        animatingComp.animateMouseExit();
                        animatingComp = null;
                    }
                }
            } else if (event.getID() != MouseEvent.MOUSE_MOVED) {
                if (source instanceof MaterialComponent comp) {
                    if (comp.isMouseAnimationAllowed()) {
                        if (animatingComp != comp) {
                            if (animatingComp != null) {
                                animatingComp.animateMouseExit();
                            }
                            animatingComp = comp;
                            animatingComp.animateMouseEnter();
                        }
                    }
                } else {
                    if (animatingComp != null) {
                        animatingComp.animateMouseExit();
                        animatingComp = null;
                    }

                }
            }
        }, AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);

    }

    private boolean isAnimatingMouse() {
        return isAnimatingMouse;
    }

    private boolean isAnimatingMouse = false;

    private void setIsAnimatingMouse(boolean b) {
        isAnimatingMouse = b;
    }

    public static Point getMouseLocation() {
        return LastMouseLocation;
    }

    public MaterialComponent() throws HeadlessException {
        super();
        setOpaque(false);
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        ThemeManager.getInstance().addThemeListener(this::updateTheme);
        updateTheme();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_RESOLUTION_VARIANT, RenderingHints.VALUE_RESOLUTION_VARIANT_DPI_FIT);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
//        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        if (!isEnabled()) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        super.paint(g2d);
    }

    public void animateFG(Color to) {
        AnimationLibrary.animateForeground(this, to, MaterialParameters.COLOR_ANIMATION_DURATION.toMillis());
    }

    public void animateBG(Color to) {
        AnimationLibrary.animateBackground(this, to, MaterialParameters.COLOR_ANIMATION_DURATION.toMillis());
    }

    protected abstract void animateMouseEnter();

    protected abstract void animateMouseExit();
//    abstract void animateMousePress();

    @Override
    public JToolTip createToolTip() {
        JToolTip toolTip = super.createToolTip();
        toolTip.setBackground(ThemeColors.getColorByElevation(Elevation._24));
        toolTip.setForeground(ThemeColors.getTextPrimary());
        toolTip.setBorder(BorderFactory.createLineBorder(ThemeColors.getAccent(), 1, true));
        return toolTip;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
        repaint();
    }

    @Override
    public Font getFont() {
        if (defaultFont == null)
            defaultFont = super.getFont();
        if (font == null)
            if (MaterialFonts.getInstance().getDefaultFont() != null)
                return MaterialFonts.getInstance().getDefaultFont();
            else
                return super.getFont();
        else
            return font;
    }

    public abstract void updateTheme();


    //Had to override these two methods as they weren't working
    @Override
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
        super.setPreferredSize(preferredSize);
    }

    @Override
    public Dimension getPreferredSize() {
        if (preferredSize == null) {
            return new Dimension(100, 20);
        } else {
            return super.getPreferredSize();
        }
    }

    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @Override
    public int getHeight() {
        return super.getHeight();
    }

    public void setFontSize(float size) {
        var f = this.getFont();
        fontSize = size;
        if (f != null) {
            this.setFont(f.deriveFont(fontSize));
            applyNewFont = false;
        } else applyNewFont = true;
    }

    public float getFontSize() {
        return fontSize;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
        applyFontStyle();
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
        applyFontStyle();
    }

    public void applyFontStyle() {
        Font f = this.getFont();
        if (f != null) {

            if (isBold && isItalic) {
                this.setFont(f.deriveFont(Font.BOLD | Font.ITALIC, getFontSize()));
            } else if (isBold) {
                this.setFont(f.deriveFont(Collections.singletonMap(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD)));
            } else if (isItalic) {
                this.setFont(f.deriveFont(Collections.singletonMap(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE)));
            }
            this.repaint();
        }
    }


    @Override
    public void addNotify() {
        super.addNotify();
        if (applyNewFont)
            setFontSize(fontSize);
        applyFontStyle();
    }

    public void setSelected(boolean isSelected) {
        SwingUtilities.invokeLater(() -> {
            this.isSelected = isSelected;
            for (SelectionListener selectionListener : selectionListeners) {
                selectionListener.selectionChanged(isSelected);
            }
            repaint();
        });
    }

    public void addSelectionListener(SelectionListener listener) {
        if (!selectionListeners.contains(listener))
            selectionListeners.add(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        selectionListeners.remove(listener);
    }

    public boolean isSelected() {
        return isSelected;
    }

    protected void drawLanguageCompatibleString(String str, int x, int y, Graphics2D g2d, Font fontToUse) {
        LanguageCompatibleString languageCompatibleString = GraphicsUtils.getLanguageCompatibleString(str, getFont());
        if(languageCompatibleString.isCompletelyCompatible()) {
            g2d.setFont(fontToUse);
            g2d.drawString(str,x,y);
        }
        else {
            Iterator<LanguageCompatibleString.Node> i = languageCompatibleString.getIterator();
            Font defaultFont = getDefaultFont().deriveFont(fontToUse.getStyle(), fontToUse.getSize());
            while (i.hasNext()) {
                LanguageCompatibleString.Node node = i.next();
                String sliceOfString = node.getCurr();
                boolean isCompatible = node.isSupportedByFont();
                if (isCompatible)
                    g2d.setFont(fontToUse);
                else
                    g2d.setFont(defaultFont);
                g2d.drawString(sliceOfString, x, y);
                x += g2d.getFontMetrics().stringWidth(sliceOfString);
            }
        }
    }

    /**
     * Allows the component to handle a KeyEvent. The component may or may not consume the event.
     */
    public void handleKeyPressEvent(KeyEvent e) {

    }

    public static Font getDefaultFont() {
        return defaultFont;
    }

    public boolean isMouseAnimationAllowed() {
        return allowMouseAnimation;
    }

    public void setAllowMouseAnimation(boolean allowMouseAnimation) {
        this.allowMouseAnimation = allowMouseAnimation;
    }
}
