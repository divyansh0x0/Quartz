package app.audio.player.notification.dbus;

import org.freedesktop.dbus.DBusMap;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.types.UInt32;
import org.freedesktop.dbus.types.Variant;

import java.util.List;
import java.util.Map;

/**
The expected type for the Notify method is (susss as a{sv} i), which represents the following parameters:

        s: String
        u: Unsigned integer
        s: String
        s: String
        s: String
        as: Array String
        a{sv}: Dictionary (Map) of String to Variant
        i: Integer
*/

//(susss as aa{sv}i)
@DBusInterfaceName("org.freedesktop.Notifications")
interface Notifications extends DBusInterface {
    UInt32 Notify(String app_name, UInt32 replaces_id, String app_icon, String summary, String body,
                  String[] actions, Map<String, Variant<?>> hints,
                  int timeout);
}
