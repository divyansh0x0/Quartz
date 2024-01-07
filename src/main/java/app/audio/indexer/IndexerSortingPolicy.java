package app.audio.indexer;

import app.audio.FrostAudio;
import java.util.Comparator;

public enum IndexerSortingPolicy {
    ASCENDING{
        @Override
        Comparator<FrostAudio> getComparator(){
            return AudioFileComparators.getAscendingComparator();
        }
    },
    DESCENDING{
        @Override
        Comparator<FrostAudio> getComparator(){
            return AudioFileComparators.getDescendingComparator();
        }
    };

    abstract Comparator<FrostAudio> getComparator();

}
