package app.audio.indexer;

import app.audio.AudioData;
import java.util.Comparator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class AudioFileComparators {
    @Contract(value = " -> new", pure = true)
    public static @NotNull Comparator<AudioData> getAscendingComparator() {
        return Comparator.comparing(AudioData::getName);
    }
    public static @NotNull Comparator<AudioData> getDescendingComparator() {
        return (o1, o2) -> o2.getName().compareTo(o1.getName());
    }
}
