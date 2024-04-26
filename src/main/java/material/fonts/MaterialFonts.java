package material.fonts;

import material.utils.Log;

import java.awt.*;
import java.io.InputStream;

public class MaterialFonts {
    public static final float size = 15f;
    private static Font DEFAULT_FONT = null;

    private static MaterialFonts instance;
    private static Font ROBOTO;
    private static Font ROBOTO_BOLD;
    private static Font NOTOSANS;
    private static Font ROBOTO_ITALIC;
    private static Font ROBOTO_BOLD_ITALIC;

    private MaterialFonts() {
        ROBOTO = read("/Roboto.ttf");
        NOTOSANS = read("/NotoSans.ttf");
        if(ROBOTO !=null)
            addFont(ROBOTO);
        if(NOTOSANS != null)
            addFont(NOTOSANS);
        setDefaultFont(ROBOTO);
    }

    private boolean setDefaultFont(Font font) {
        if(font == null)
            return false;

        DEFAULT_FONT = font;
        return true;
    }

    private void addFont(Font f) {
        if (f != null)
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
        else
            Log.error("Font to add is null in Material Fonts");
    }

    public Font getDefaultFont() {
        return DEFAULT_FONT;
    }

    private Font read(String path) {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            if(inputStream == null)
                return null;
            return Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(size);
        } catch (Exception e) {
            Log.error(e);
        }
        return null;
    }

    public static MaterialFonts getInstance() {
        if (instance == null)
            instance = new MaterialFonts();
        return instance;
    }

    public boolean setDefaultFont(String resourceURL) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream(resourceURL)) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
            return setDefaultFont(font);
        }catch (Exception e){
            return false;
        }
    }
}
