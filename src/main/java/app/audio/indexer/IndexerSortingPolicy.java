package app.audio.indexer;

import app.audio.AudioData;

import java.util.Comparator;

public enum IndexerSortingPolicy {
    ASCENDING{
        @Override
        Comparator<AudioData> getComparator(){
            return AudioFileComparators.getAscendingComparator();
        }
    },
    DESCENDING{
        @Override
        Comparator<AudioData> getComparator(){
            return AudioFileComparators.getDescendingComparator();
        }
    };

    abstract Comparator<AudioData> getComparator();

}
