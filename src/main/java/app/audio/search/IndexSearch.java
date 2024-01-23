package app.audio.search;

import app.audio.AudioData;
import app.audio.indexer.AudioDataIndexer;
import org.jetbrains.annotations.NotNull;
import material.utils.Log;

import java.util.ArrayList;
import java.util.Comparator;

// TODO Add search based on: artists, playlists, folders, genre

public class IndexSearch {
    private static IndexSearch instance;
    private static ArrayList<AudioData> audioDataList = new ArrayList<>(20);

    private IndexSearch() {

    }

    public ArrayList<AudioData> searchString(String str) {
        Log.info("Searching: " + str);
        AudioDataIndexer ADI = AudioDataIndexer.getInstance();
        if (str == null)
            return ADI.getAllAudioFiles();
        str = str.strip();
        audioDataList.clear();
        audioDataList = ADI.getAllAudioFiles();

        return mergeResults(searchByTitle(str), searchByArtist(str));
    }

    private @NotNull ArrayList<AudioData> searchByTitle(String str) {
        ArrayList<AudioData> arr = new ArrayList<>();
        if (str.length() > 0) {
            for (AudioData audioData : audioDataList) {
                try {
                    String title = audioData.getName();
                    if (title.matches("(?i).*" + str + ".*")) {
                        arr.add(audioData);
                    }
                } catch (Exception e) {
                    Log.error("Error occurred while searching: " + e.toString());
                }
            }
        }
        return arr;
    }

    private @NotNull ArrayList<AudioData> searchByArtist(String str) {
        ArrayList<AudioData> arr = new ArrayList<>();
        if (str.length() > 0) {
            for (AudioData audioData : audioDataList) {
                try {
                    String artist = audioData.getArtistsConcatenated();
                    if (artist.matches("(?i).*" + str + ".*")) {
                        arr.add(audioData);
                    }
                } catch (Exception e) {
                    Log.error("Error occurred while searching: " + e.toString());
                }
            }
        }
        return arr;
    }

    @SafeVarargs
    private @NotNull ArrayList<AudioData> mergeResults(ArrayList<AudioData> @NotNull ... audioLists) {
        ArrayList<AudioData> mergedList = new ArrayList<>(audioLists.length * 20);
        for (ArrayList<AudioData> list : audioLists) {
            for (AudioData audio : list) {
                if (!mergedList.contains(audio)) {
                    mergedList.add(audio);
                }
            }
        }
        return mergedList;
    }

    public static IndexSearch getInstance() {
        if (instance == null)
            instance = new IndexSearch();
        return instance;
    }

    private Comparator<AudioData> getNameComparator() {
        return new Comparator<AudioData>() {
            @Override
            public int compare(AudioData o1, AudioData o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
    }

    private Comparator<AudioData> getArtistComparator() {
        return new Comparator<AudioData>() {
            @Override
            public int compare(AudioData o1, AudioData o2) {
                return o1.getArtistsConcatenated().compareTo(o2.getArtistsConcatenated());
            }
        };
    }
}
