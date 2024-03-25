package app.audio.player;

import app.audio.AudioData;
import app.audio.indexer.AudioDataIndexer;
import app.components.audio.AudioTile;
import material.utils.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class AudioQueue {
    private ArrayList<Runnable> queueUpdateListeners = new ArrayList<>();
    private static AudioQueue instance;
    private ArrayList<AudioData> queue = new ArrayList<>();
    private ArrayList<AudioData> unShuffledQueue = new ArrayList<>();
    private boolean isShuffled = false;
    private int currentAudioIndex = 0;
    private AudioData currentAudioData;
    private AudioData lastRandomAudioData;

    private AudioQueue() {

    }

    public static AudioQueue getInstance() {
        if (instance == null)
            instance = new AudioQueue();
        return instance;
    }

    public void shuffle() {
        if (queue.size() > 1) {
            Random random = new Random();
            if (!isShuffled) {
                unShuffledQueue.clear();
                unShuffledQueue.addAll(queue);
            }
            ArrayList<AudioData> shuffledQueue = new ArrayList<>(queue.size());
            for (int i = queue.size(); i > 0; i--) {
                int j = random.nextInt(0, queue.size());
                AudioData audioData = queue.remove(j);
                shuffledQueue.add(audioData);
            }
            queue = shuffledQueue;
            isShuffled = true;
            queueUpdated();
        }
    }

    public void unShuffle() {
        if (queue.size() > 1 && isShuffled) {
            if (unShuffledQueue != null) {
                queue.clear();
                queue.addAll(unShuffledQueue);
            }
            isShuffled = false;
            queueUpdated();
        }
    }

    public @Nullable AudioData getRandomAudio() {
        if (queue.size() > 1) {
            Random random = new Random();
            int randomInt = random.nextInt(0, queue.size());
            AudioData randomAudioData = queue.get(randomInt);
            if (lastRandomAudioData == null || randomAudioData != lastRandomAudioData) {
                lastRandomAudioData = randomAudioData;
                return randomAudioData;
            } else return getRandomAudio();
        } else {
            if (queue.isEmpty()) {
                return null;
            } else
                return queue.getFirst();
        }
    }

    public void add(AudioData audioData) {
        queue.add(audioData);
        if (unShuffledQueue == null)
            unShuffledQueue = new ArrayList<>(queue);
        else
            unShuffledQueue.add(audioData);
        queueUpdated();
    }

    public void add(AudioTile audioTile) {
        add(audioTile.getAudioData());
    }

    private void remove(AudioData audioData) {
        queue.remove(audioData);
        if (unShuffledQueue != null)
            unShuffledQueue.remove(audioData);
        queueUpdated();
    }

    public void remove(AudioTile audioTile) {
        remove(audioTile.getAudioData());
    }

    private void queueUpdated() {
        for (Runnable runnable : queueUpdateListeners) {
            runnable.run();
        }
        Log.info("NEW QUEUE: \n" + queue.toString() + "\n---- QUEUE END ----- \n\n");
    }

    private void onQueueUpdate(Runnable runnable) {
        queueUpdateListeners.add(runnable);
    }

    private boolean removeQueueUpdateListener(Runnable runnable) {
        return queueUpdateListeners.remove(runnable);
    }

    public @Nullable AudioData getNextAudio() {
        if (queue.size() > 0) {
            currentAudioIndex = Math.max(Math.min(currentAudioIndex + 1, queue.size() - 1), 0);
            AudioData temp = queue.get(currentAudioIndex);
            if (!temp.equals(currentAudioData)) {
                currentAudioData = temp;
                return temp;
            } else if (currentAudioIndex < queue.size() - 1)
                return getNextAudio();
            else
                return null;
        } else
            return null;
    }

    public @Nullable AudioData getPrevAudio() {
        if (queue.size() > 0) {
            currentAudioIndex = Math.max(Math.min(currentAudioIndex - 1, queue.size()), 0);
            AudioData temp = queue.get(currentAudioIndex);
            if (!temp.equals(currentAudioData)) {
                currentAudioData = temp;
                return temp;
            } else if (currentAudioIndex > 1)
                return getPrevAudio();
            else
                return currentAudioData;
        } else
            return null;
    }


    /**
     * Clears the queue and creates saves a new queue
     *
     * @param master        First audio
     * @param list Array list of audio that gets added after master audio
     */
    public void newQueue(AudioData master, ArrayList<AudioData> list) {
        currentAudioData = master;
        queue.clear();
        if(!list.contains(master))
            queue.add(master);
        queue.addAll(list);
        updateQueueIndex();
    }

    public void newQueueFromAudioTiles(AudioData master, ArrayList<AudioTile> allAudioTiles) {
        ArrayList<AudioData> allAudioData = new ArrayList<>(allAudioTiles.size());
        for (AudioTile audioTile : allAudioTiles) {
            allAudioData.add(audioTile.getAudioData());
        }
        newQueue(master, allAudioData);
    }
 transient String s;
    public void setActiveAudio(@NotNull AudioData audioData) {
        currentAudioData = audioData;
        if(queue.size() == 0) {
            var arr = AudioDataIndexer.getInstance().getAllAudioFiles();
            newQueue(arr.get(0), arr);
        }
        updateQueueIndex();
    }

    private void updateQueueIndex() {

        currentAudioIndex = currentAudioData != null && queue.contains(currentAudioData) ? queue.indexOf(currentAudioData) : 0;
    }
}
