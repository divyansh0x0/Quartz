package app.audio.search;

import app.audio.FrostAudio;
import app.audio.indexer.FrostIndexer;
import org.jetbrains.annotations.NotNull;
import material.utils.Log;

import java.util.ArrayList;
import java.util.Comparator;

// TODO Add search based on: artists, playlists, folders, genre

public class FrostSearch {
    private static FrostSearch instance;
    private static ArrayList<FrostAudio> frostAudioList = new ArrayList<>(20);

    private FrostSearch() {

    }

    public ArrayList<FrostAudio> searchString(String str) {
        Log.info("Searching: " + str);
        FrostIndexer AFI = FrostIndexer.getInstance();
        if (str == null)
            return AFI.getAllAudioFiles();
        str = str.strip();
        frostAudioList.clear();
        frostAudioList = AFI.getAllAudioFiles();

        return mergeResults(searchByTitle(str), searchByArtist(str));
    }

    private @NotNull ArrayList<FrostAudio> searchByTitle(String str) {
        ArrayList<FrostAudio> arr = new ArrayList<>();
        if (str.length() > 0) {
            for (FrostAudio frostAudio : frostAudioList) {
                try {
                    String title = frostAudio.getName();
                    if (title.matches("(?i).*" + str + ".*")) {
                        arr.add(frostAudio);
                    }
                } catch (Exception e) {
                    Log.error("Error occurred while searching: " + e.toString());
                }
            }
        }
        return arr;
    }

    private @NotNull ArrayList<FrostAudio> searchByArtist(String str) {
        ArrayList<FrostAudio> arr = new ArrayList<>();
        if (str.length() > 0) {
            for (FrostAudio frostAudio : frostAudioList) {
                try {
                    String artist = frostAudio.getArtistsConcatenated();
                    if (artist.matches("(?i).*" + str + ".*")) {
                        arr.add(frostAudio);
                    }
                } catch (Exception e) {
                    Log.error("Error occurred while searching: " + e.toString());
                }
            }
        }
        return arr;
    }

    @SafeVarargs
    private @NotNull ArrayList<FrostAudio> mergeResults(ArrayList<FrostAudio> @NotNull ... audioLists) {
        ArrayList<FrostAudio> mergedList = new ArrayList<>(audioLists.length * 20);
        for (ArrayList<FrostAudio> list : audioLists) {
            for (FrostAudio audio : list) {
                if (!mergedList.contains(audio)) {
                    mergedList.add(audio);
                }
            }
        }
        return mergedList;
    }

    public static FrostSearch getInstance() {
        if (instance == null)
            instance = new FrostSearch();
        return instance;
    }

    private Comparator<FrostAudio> getNameComparator() {
        return new Comparator<FrostAudio>() {
            @Override
            public int compare(FrostAudio o1, FrostAudio o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
    }

    private Comparator<FrostAudio> getArtistComparator() {
        return new Comparator<FrostAudio>() {
            @Override
            public int compare(FrostAudio o1, FrostAudio o2) {
                return o1.getArtistsConcatenated().compareTo(o2.getArtistsConcatenated());
            }
        };
    }
}
