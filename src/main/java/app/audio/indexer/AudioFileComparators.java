package app.audio.indexer;

import app.audio.FrostAudio;
import java.util.Comparator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class AudioFileComparators {
    @Contract(value = " -> new", pure = true)
    public static @NotNull Comparator<FrostAudio> getAscendingComparator() {
        return Comparator.comparing(FrostAudio::getName);
    }
    public static @NotNull Comparator<FrostAudio> getDescendingComparator() {
        return (o1, o2) -> o2.getName().compareTo(o1.getName());
    }
}
