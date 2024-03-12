package app.local.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import material.utils.Log;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FileCacheManager {
    private static FileCacheManager instance;
    private static final String CACHE_FILE_PATH = "file_cache.json";

    private final ArrayList<String> cachedFiles;

    private FileCacheManager() {
        cachedFiles = loadCache();
        Log.info("Cache files found: " + cachedFiles.size());
    }

    public void cacheFile(File file) {
        if (!cachedFiles.contains(file.toString())) {
            cachedFiles.add(file.toString());
//            Log.cache("File cached successfully: " + file.getAbsolutePath());
        }
//        else {
//            Log.cache("File already exists in the cache: " + file.getAbsolutePath());
//        }
    }

    public List<File> getCachedFiles() {
        List<File> l= new ArrayList<>(cachedFiles.size());
        for(String str: cachedFiles){
            l.add(new File(str));
        }
        return l;
    }

    private ArrayList<String> loadCache() {
        try (Reader reader = new FileReader(CACHE_FILE_PATH)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();

            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            // Cache file does not exist yet, return an empty list
            return new ArrayList<>(0);
        }
    }

    public void saveCacheToStorage() {
        try (Writer writer = new FileWriter(CACHE_FILE_PATH)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(cachedFiles, writer);
        } catch (IOException e) {
            Log.error("Couldn't save cache: " + e);
        }
    }

    public void deleteCacheFileAsync(File file) {
        Thread.startVirtualThread(() -> {
            deleteCacheFile(file);
        });
    }

    public void deleteCacheFile(File file) {
        if (cachedFiles.remove(file.toString()))
            Log.cache("Removed file from cache: " + file.getAbsolutePath());
    }

    public static FileCacheManager getInstance() {
        if (instance == null)
            instance = new FileCacheManager();
        return instance;
    }
}
