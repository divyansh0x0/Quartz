package app.audio;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import material.utils.Log;

import java.io.File;

public class MP3Tools {

//    public static @Nullable AudioData getAudioData(String path) {
//        try {
//            File file = new File(path);
//            if (file.exists()) {
//                org.jaudiotagger.audio.AudioFile audioFile = AudioFileIO.read(file);
//                return getDataFromTag(audioFile);
//
//            }
//        }
//        catch(CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e){
//            throw new RuntimeException(e);
//        }
//        return null;
//    }

    public static @Nullable AudioHeader getAudioHeader(File f) {
        try {
            AudioFile audioFile = AudioFileIO.read(f);
            return audioFile.getAudioHeader();
        } catch (Exception e) {
            Log.error(e);
            e.printStackTrace();
            return null;
        }
    }

    public static @Nullable Tag getTag(@NotNull File f) {
        try {
            AudioFile audioFile = AudioFileIO.read(f);
            return audioFile.getTag();
        } catch (Exception e) {
            Log.error(e);
            e.printStackTrace();
            return null;
        }

//        File file = audioFile.getFile();
//        String name = null;
//        String artist = null;
//        String album = null;
//        double duration = 0;
//        byte[] artworkData = null;
//        if (t != null) {
//            name = t.getFirst(FieldKey.TITLE);
//            artist = t.getFirst(FieldKey.ARTIST);
//            album = t.getFirst(FieldKey.ALBUM);
//            duration = ((MP3AudioHeader) (audioFile.getAudioHeader())).getPreciseTrackLength();
//            Artwork artwork = t.getFirstArtwork();
//            if (artwork != null)
//                artworkData = artwork.getBinaryData();
//    }
    }
}

