package app.audio.player.notification.dbus;

import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;

public class NotificationsImpl implements Notifications {
    public static String PATH = "/org/freedesktop/Notifications";
    public static String BUS_NAME = "org.freedesktop.Notifications";
    @Override
    public String getObjectPath() {
        return PATH;
    }

    @Override
    public UInt32 Notify(String app_name, UInt32 replaces_id, String app_icon, String summary, String body, String[] actions, Map<String, Variant<?>> hints, int timeout) {
        return null;
    }
}
