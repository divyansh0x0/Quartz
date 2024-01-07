package app.audio.player;

import app.audio.FrostAudio;
import app.audio.indexer.FrostIndexer;
import java.util.ArrayList;
import java.util.Random;
import app.components.audio.AudioTile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import material.utils.Log;

public class FrostQueue {
    private ArrayList<Runnable> queueUpdateListeners = new ArrayList<>();
    private static FrostQueue instance;
    private ArrayList<FrostAudio> queue = new ArrayList<>();
    private ArrayList<FrostAudio> unShuffledQueue;
    private boolean isShuffled = false;
    private int currentAudioIndex = 0;
    private FrostAudio currentFrostAudio;
    private FrostAudio lastRandomFrostAudio;

    private FrostQueue() {

    }

    public static FrostQueue getInstance() {
        if (instance == null)
            instance = new FrostQueue();
        return instance;
    }

    public void shuffle() {
        if (queue.size() > 1) {
            Random random = new Random();
            if (!isShuffled) {
                unShuffledQueue = new ArrayList<>(queue);
            }
            ArrayList<FrostAudio> shuffledQueue = new ArrayList<>(queue.size());
            for (int i = queue.size(); i > 0; i--) {
                int j = random.nextInt(0, queue.size());
                FrostAudio frostAudio = queue.remove(j);
                shuffledQueue.add(frostAudio);
            }
            queue = shuffledQueue;
            isShuffled = true;
            queueUpdated();
        }
    }

    public void unShuffle() {
        if (queue.size() > 1 && isShuffled) {
            if (unShuffledQueue != null)
                queue = unShuffledQueue;
            isShuffled = false;
            queueUpdated();
        }
    }

    public @Nullable FrostAudio getRandomAudio() {
        if (queue.size() > 1) {
            Random random = new Random();
            int randomInt = random.nextInt(0, queue.size());
            FrostAudio randomFrostAudio = queue.get(randomInt);
            if (lastRandomFrostAudio == null || randomFrostAudio != lastRandomFrostAudio) {
                lastRandomFrostAudio = randomFrostAudio;
                return randomFrostAudio;
            } else return getRandomAudio();
        } else {
            if (queue.isEmpty()) {
                return null;
            } else
                return queue.get(0);
        }
    }

    public void add(FrostAudio frostAudio) {
        queue.add(frostAudio);
        if (unShuffledQueue == null)
            unShuffledQueue = new ArrayList<>(queue);
        else
            unShuffledQueue.add(frostAudio);
        queueUpdated();
    }

    public void add(AudioTile audioTile) {
        add(audioTile.getFrostAudio());
    }

    private void remove(FrostAudio frostAudio) {
        queue.remove(frostAudio);
        if (unShuffledQueue != null)
            unShuffledQueue.remove(frostAudio);
        queueUpdated();
    }

    public void remove(AudioTile audioTile) {
        remove(audioTile.getFrostAudio());
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

    public @Nullable FrostAudio getNextAudio() {
        if (queue.size() > 0) {
            currentAudioIndex = Math.max(Math.min(currentAudioIndex + 1, queue.size() - 1), 0);
            FrostAudio temp = queue.get(currentAudioIndex);
            if (!temp.equals(currentFrostAudio)) {
                currentFrostAudio = temp;
                return temp;
            } else if (currentAudioIndex < queue.size() - 1)
                return getNextAudio();
            else
                return null;
        } else
            return null;
    }

    public @Nullable FrostAudio getPrevAudio() {
        if (queue.size() > 0) {
            currentAudioIndex = Math.max(Math.min(currentAudioIndex - 1, queue.size()), 0);
            FrostAudio temp = queue.get(currentAudioIndex);
            if (!temp.equals(currentFrostAudio)) {
                currentFrostAudio = temp;
                return temp;
            } else if (currentAudioIndex > 1)
                return getPrevAudio();
            else
                return currentFrostAudio;
        } else
            return null;
    }


    /**
     * Clears the queue and creates saves a new queue
     *
     * @param master        First audio
     * @param list Array list of audio that gets added after master audio
     */
    public void newQueue(FrostAudio master, ArrayList<FrostAudio> list) {
        currentFrostAudio = master;
        queue.clear();
        if(!list.contains(master))
            queue.add(master);
        queue.addAll(list);
        updateQueueIndex();
    }

    public void newQueueFromAudioTiles(FrostAudio master, ArrayList<AudioTile> allAudioTiles) {
        ArrayList<FrostAudio> allFrostAudios = new ArrayList<>(allAudioTiles.size());
        for (AudioTile audioTile : allAudioTiles) {
            allFrostAudios.add(audioTile.getFrostAudio());
        }
        newQueue(master, allFrostAudios);
    }
 transient String s;
    public void setActiveAudio(@NotNull FrostAudio frostAudio) {
        currentFrostAudio = frostAudio;
        if(queue.size() == 0) {
            var arr = FrostIndexer.getInstance().getAllAudioFiles();
            newQueue(arr.get(0), arr);
        }
        updateQueueIndex();
    }

    private void updateQueueIndex() {

        currentAudioIndex = currentFrostAudio != null && queue.contains(currentFrostAudio) ? queue.indexOf(currentFrostAudio) : 0;
    }
}
