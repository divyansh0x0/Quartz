package app.audio.player.notification.dbus;

import org.freedesktop.dbus.Container;
import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.annotations.Position;
import org.freedesktop.dbus.types.Variant;


public class Dictionary extends Struct {
    @Position(0)
    String str;
    @Position(1)
    Variant<?> v;

    public Dictionary(String str, Variant<?> v) {
        this.str = str;
        this.v = v;
    }
}
