package app.audio.player.notification.dbus;

import material.utils.Log;

public class PlayerInterfaceImpl implements PlayerInterface {
    @Override
    public void Next() {

    }

    @Override
    public void Previous() {

    }

    @Override
    public void Pause() {

    }

    @Override
    public void PlayPause() {

    }

    @Override
    public void Play() {
        Log.success("Playing");
    }

    @Override
    public void Stop() {
        Log.success("Stopping");
    }

    @Override
    public void Seek(long x) {

    }

    @Override
    public void OpenUri(String uri) {

    }

    @Override
    public boolean isRemote() {
        return PlayerInterface.super.isRemote();
    }

    @Override
    public String getObjectPath() {
        return PlayerInterface.PATH;
    }
}
