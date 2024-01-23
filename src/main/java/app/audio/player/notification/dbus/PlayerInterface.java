package app.audio.player.notification.dbus;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;

/**
 * [<a href="https://specifications.freedesktop.org/mpris-spec/latest/Player_Interface.html">...</a>]
 * <p>
 * ## Properties
 * - PlaybackStatus	s (Playback_Status) 	Read only
 * - LoopStatus		s (Loop_Status)	        Read/Write	(optional)
 * - Rate		    d (Playback_Rate)	    Read/Write
 * - Shuffle		b	                    Read/Write	(optional)
 * - Metadata		a{sv} (Metadata_Map)	Read only
 * - Volume		    d (Volume)	            Read/Write
 * - Position		x (Time_In_Us)	        Read only
 * - MinimumRate	d (Playback_Rate)	    Read only
 * - MaximumRate	d (Playback_Rate)	    Read only
 * - CanGoNext		b	                    Read only
 * - CanGoPrevious	b	                    Read only
 * - CanPlay		b	                    Read only
 * - CanPause		b	                    Read only
 * - CanSeek		b	                    Read only
 * - CanControl		b	                    Read only
 */
@DBusInterfaceName("org.mpris.MediaPlayer2.Aphrodite")
public interface PlayerInterface extends DBusInterface {
    public static String BUS_NAME = "org.mpris.MediaPlayer2.Aphrodite";
    public static String PATH = "/org/mpris/MediaPlayer2/Aphrodite";  void Next();

    void Previous();

    void Pause();

    void PlayPause();

    void Play();

    void Stop();

    void Seek(long x);

    void OpenUri(String uri);
    enum LoopStatus {
        None, Track, Playlist
    }
    public class Seeked {
        public Seeked(DBusPath path, long position){

        }
    }
}





