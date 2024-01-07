package app.loader;

import app.main.Frost;
import material.utils.Log;

public class FrostLoader {

    private static FrostLoader instance;
    Frost FROST = Frost.getInstance();
    LoaderWindow loaderWindow;

    public FrostLoader() {

    }




    public synchronized void show() {
            Log.info("Showing loader window...");
            loaderWindow = new LoaderWindow();
            loaderWindow.setVisible(true);
    }

    public synchronized void hide() {
        if(loaderWindow != null)
             loaderWindow.close();
    }

    public static FrostLoader getInstance() {
        if (instance == null)
            instance = new FrostLoader();
        return instance;
    }
}
