package app.comparators;

import app.components.audio.AudioTile;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.security.InvalidParameterException;
import java.util.Comparator;

public class AudioTileComparator {
    public static final Comparator<Component> TITLE_COMPARATOR = (o1, o2) -> {
        if(o1 instanceof AudioTile tile1 && o2 instanceof AudioTile tile2){
            return tile1.getAudioData().getName().compareTo(tile2.getAudioData().getName());
        }else
            throw new InvalidParameterException("Component o1 and o2 must be " + AudioTile.class + " but was " + o1.getClass() + " and " + o2.getClass() + "respectively");
    };
    public static final Comparator<Component> ARTIST_COMPARATOR = (o1, o2) -> {
        if(o1 instanceof AudioTile tile1 && o2 instanceof AudioTile tile2){
            return tile1.getAudioData().getArtistsConcatenated().compareTo(tile2.getAudioData().getArtistsConcatenated());
        }else
            throw new InvalidParameterException("Component o1 and o2 must be " + AudioTile.class + " but was " + o1.getClass() + " and " + o2.getClass() + "respectively");
    };
    public static final Comparator<? super Component> DATE_ADDED = (o1,o2)->{
        if(o1 instanceof AudioTile tile1 && o2 instanceof AudioTile tile2){
            try {
                FileTime creationTime1 = (FileTime) Files.getAttribute(tile1.getAudioData().getFile().toPath(), "creationTime");
                FileTime creationTime2 = (FileTime) Files.getAttribute(tile2.getAudioData().getFile().toPath(), "creationTime");

                return creationTime1.compareTo(creationTime2);
            } catch (IOException e) {
                return 0; //Both files are considered equal if time creation is not there
            }

        }else
            throw new InvalidParameterException("Component o1 and o2 must be " + AudioTile.class + " but was " + o1.getClass() + " and " + o2.getClass() + "respectively");
    };
    public static Comparator<Component> DURATION_COMPARATOR = (o1, o2) -> {
        if(o1 instanceof AudioTile tile1 && o2 instanceof AudioTile tile2){
            return Double.compare(tile1.getAudioData().getDurationInMs(),tile2.getAudioData().getDurationInMs());
        }else
            throw new InvalidParameterException("Component o1 and o2 must be " + AudioTile.class + " but was " + o1.getClass() + " and " + o2.getClass() + "respectively");
    };
}
