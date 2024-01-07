package app.audio.player.notification.dbus;

import material.utils.Log;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

public class MediaPlayerControl {


    public static void main(String[] args) throws DBusException {

    }
    private static void printMethods(Object o){
        Method[] methods = o.getClass().getMethods();;
        int nMethod = 1;
        System.out.println("1. List of all methods of object: " + o.getClass().getName());
        for (Method method : methods) {
            System.out.printf("%d. %s", ++nMethod, method);
            System.out.println();
        }
        Log.success("End - all  methods of class:  " + ++nMethod);
    }
    private static byte[] getSystemIconData(String iconName) {
        try {
            Process process = new ProcessBuilder("gio", "info", "-a", "standard::icon", iconName)
                    .start();
            try (InputStream inputStream = process.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return outputStream.toByteArray();
            }
        }catch (Exception e){
            Log.error(e);
        }
        return null;
    }
}
// Define a class to represent the Notification signal
class Notification extends DBusSignal implements DBusInterface {
    private String message;

    public Notification(String _source, String _path, String _iface, String _member, String _sig, Object... _args) throws DBusException {
        super(_source, _path, _iface, _member, _sig, _args);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isRemote() {
        return DBusInterface.super.isRemote();
    }

    @Override
    public String getObjectPath() {
        return NotificationsImpl.PATH;
    }
}

