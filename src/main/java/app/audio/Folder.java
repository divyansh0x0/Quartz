package app.audio;

import material.utils.Log;
import material.utils.StringUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Folder {
    private final ArrayList<AudioData> audioData = new ArrayList<>();
    private final String name;
    private final String path;
    private BufferedImage defaultArtistImage;
    private BufferedImage lastBufferedImage;
    private final int imgResolution = 524;


    public Folder(String folderPath) {
        this.name = StringUtils.getFileName(folderPath);
        this.path = folderPath;
        Log.info("Folder created path:"+path + "|name: " + name);
    }

    public ArrayList<AudioData> getAudioDatas() {
//        Log.info(name +":\n" + audioData);
        return audioData;//I know its modifiable so don't modify it plzz
    }

    public void setAudioDatas(ArrayList<AudioData> audioData) {
        this.audioData.clear();
        this.audioData.addAll(audioData.stream().filter((audio)->name.equals(audio.getFile().getParent())).toList());
    }

    public void addAudioData(AudioData audioData) {
        if(!this.audioData.contains(audioData) && path.equals(audioData.getFolderPath()))
            this.audioData.add(audioData);
    }

    public void removeAudioData(AudioData audioData) {
        this.audioData.remove(audioData);
    }

    public void removeAllAudioDatas() {
        audioData.clear();
    }

    public String getName() {
        return name;
    }
    public String getPath() {
        return path;
    }

//    public @Nullable BufferedImage getArtistImage() {
//        try {
//            if (defaultArtistImage == null) {
//                if (lastBufferedImage == null) {
//                    int numOfImages = audioData.size() >= 4 ? 4 : 1;
//                    Image[] images = new Image[numOfImages];
//                    for (int i = 0; i < images.length; i++) {
//                        images[i] = audioData.get(i).getArtwork();
//                    }
//
//                    lastBufferedImage = GraphicsUtils.imageMerger(ArtworkManager.getInstance().getArtworkSize().getWidth(),images);
//                }
//                return lastBufferedImage;
//            }
//            return defaultArtistImage;
//        } catch (Exception e) {
//            Log.error("Error in artwork of artist (" + name + "):" + e);
//            return null;
//        }
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist artist)) return false;
        return this.getName().equalsIgnoreCase(artist.getName());
    }

    @Override
    public int hashCode() {
        int result = getAudioDatas().hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Folder: " + name + "| path:" + path;
    }
}
