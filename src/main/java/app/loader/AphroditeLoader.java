package app.loader;

import material.utils.Log;

public class AphroditeLoader {

    private static AphroditeLoader instance;
    private LoaderWindow loaderWindow;

    public AphroditeLoader() {

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

    public static AphroditeLoader getInstance() {
        if (instance == null)
            instance = new AphroditeLoader();
        return instance;
    }
}
