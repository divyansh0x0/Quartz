package app.audio.search;

import app.audio.AudioData;
import app.audio.indexer.AudioDataIndexer;
import app.components.listeners.SearchCompletedListener;
import app.local.cache.FileCacheManager;
import app.settings.StartupSettings;
import material.utils.Log;
import material.utils.OsInfo;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SystemSearch {
    private static final ArrayList<SearchCompletedListener> searchCompletedListeners = new ArrayList<>();
    private static SystemSearch instance;
    private static FileSystemView FSV = FileSystemView.getFileSystemView();
    private int SEARCH_DEPTH = StartupSettings.SEARCH_DEPTH;
    private boolean isSearchOnceComplete = false;
    private boolean isSearching = false;
    private boolean isBackgroundSearchRunning = false;
    private Thread searchThread;
    private final int ThreadCount = StartupSettings.PARALLEL_THREAD_COUNT;

    private SystemSearch() {
//        forceSearch();
        Runtime.getRuntime().addShutdownHook(Thread.ofVirtual().unstarted(() -> {
            if (searchThread != null && searchThread.isAlive())
                searchThread.interrupt();
        }));
    }

    public void forceSearch() {
        if (isSearching) {
            searchThread.interrupt();
            searchThread = null;
            isSearching = false;
        }
        Log.warn("Starting system search");
        searchThread = Thread.startVirtualThread(() -> this.beginSearch(true));
    }

    private void beginSearch() {
        this.beginSearch(false);
    }

    private void beginSearch(boolean doBackgroundSearchIfCacheLoaded) {
        isSearching = true;
//        boolean isCacheLoaded = cacheLoaded();
//        if (isCacheLoaded) {
//            Log.info("CACHE LOADED");
//            searchCompleted();
//        }

//        if (doBackgroundSearchIfCacheLoaded) {
        Log.info("Beginning background search");
//            isBackgroundSearchRunning = true;
        switch (OsInfo.getOsType()) {
            case LINUX, MAC -> rootFSSearch();
            case WINDOWS -> windowsFSSearch();
//            }
        }
    }

    /**
     * Loads cache and checks if all files were loaded
     *
     * @return if all files in the cache were loaded
     */
    private synchronized boolean cacheLoaded() {
        List<File> files = FileCacheManager.getInstance().getCachedFiles();
        if (files.isEmpty()) {
            return false;
        }

        int validFiles = saveDataAsync(files);
        return !files.isEmpty() && validFiles == files.size();
    }


    private void rootFSSearch() {
        Log.warn("Linux/Mac File System detected");
        File root = FSV.getHomeDirectory();
        AudioFileVisitor fileVisitor = new AudioFileVisitor();
        try {
            Files.walkFileTree(root.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), SEARCH_DEPTH, fileVisitor);
            List<File> arr = fileVisitor.getAudioFileArrayList();
            saveDataAsync(arr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        searchCompleted();
    }


    private void windowsFSSearch() {
        Log.warn("Windows File System detected");
        File[] roots = File.listRoots();
        for (File root : roots) {
            try {
                AudioFileVisitor fileVisitor = new AudioFileVisitor();
                Files.walkFileTree(root.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), SEARCH_DEPTH, fileVisitor);
                List<File> arr = fileVisitor.getAudioFileArrayList();
                Log.success(arr.size() + " mp3 files found in " + root);
                saveDataAsync(arr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        searchCompleted();
    }

    private synchronized void saveData(List<File> files) {
        for (File file : files) {
            AudioDataIndexer.getInstance().addAudioFile(new AudioData(file));
//            FileCacheManager.getInstance().cacheFile(file);
        }
//        FileCacheManager.getInstance().saveCacheToStorage();
    }

    private int saveDataAsync(List<File> files) {
        try {
            if (files.isEmpty())
                return 0;
            AtomicInteger savedFilesNumber = new AtomicInteger();
            long t1 = System.nanoTime();
            Thread[] threads = new Thread[ThreadCount];
            int stackSize = (int) Math.ceil((double) files.size() / ThreadCount);
            for (int i = 0; i < threads.length; i++) {
                int begin = i * stackSize;
                int end = Math.min(begin + stackSize, files.size());
                threads[i] = Thread.ofVirtual().name("Audio data extractor thread " + i).start(() -> {
                    for (int j = begin; j < end; j++) {
                        File file = files.get(j);
                        if (file.exists() && AudioData.isValidAudio(file.toPath())) {
                            AudioData audioData = new AudioData(file);
                            AudioDataIndexer.getInstance().addAudioFile(audioData);
                            savedFilesNumber.incrementAndGet();
//                        FileCacheManager.getInstance().cacheFile(file);
                        } else {
//                        FileCacheManager.getInstance().deleteCacheFile(file);
                            Log.error(file + " is not valid or doesn't exist");
                        }
                    }
                });
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Log.error(thread.getName() + " was interrupted during cache loading");
                }
            }
            long t2 = System.nanoTime();
            Log.success("Time taken to hot cache " + savedFilesNumber + " out of " + files.size() + " files: " + ((t2 - t1) / 0.000_0001) + "ms");
//        FileCacheManager.getInstance().saveCacheToStorage();
            return savedFilesNumber.get();
        }catch (Exception e){
            Log.error("error occurred while hot caching searched files: "  + e);
            e.printStackTrace();
            return 0;
        }
    }


    public static SystemSearch getInstance() {
        if (instance == null)
            instance = new SystemSearch();
        return instance;
    }


    public void onSearchComplete(SearchCompletedListener listener) {
        if (!searchCompletedListeners.contains(listener))
            searchCompletedListeners.add(listener);
    }

    private synchronized void searchCompleted() {
        Log.success("SEARCH COMPLETED!");
        AudioDataIndexer.getInstance().indexAndSortAudioFiles();

        isSearchOnceComplete = true;
        isSearching = false;
        isBackgroundSearchRunning = false;
        for (SearchCompletedListener l : searchCompletedListeners) {
            l.searchComplete();
        }
        AudioDataIndexer.getInstance().callIndexUpdated();
    }

    public boolean isSearchOnceComplete() {
        return isSearchOnceComplete;
    }

    public boolean isBackgroundSearchRunning() {
        return isBackgroundSearchRunning;
    }
}
