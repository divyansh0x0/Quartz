package app.local.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import material.utils.Log;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class FileCacheManager {
    public static final int INITIAL_CAPACITY = 1000;
    private static FileCacheManager instance;
    private static final String CACHE_FILE_PATH = "file_cache.json";

    private final HashMap<String,File> cachedFiles;

    private FileCacheManager() {
        cachedFiles = loadCache();
        Log.info("Cache files found: " + cachedFiles.size());
    }

    public void cacheFile(File file) {
        if (!cachedFiles.containsKey(file.toString())) {
            cachedFiles.put(file.toString(),file);
            Log.cache("File cached successfully: " + file.getAbsolutePath());
        } else {
            Log.cache("File already exists in the cache: " + file.getAbsolutePath());
        }
    }

    public List<File> getCachedFiles() {
        return new ArrayList<>((cachedFiles.values()));
    }

    private HashMap<String,File> loadCache() {
        try (Reader reader = new FileReader(CACHE_FILE_PATH)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            List<String> filePaths = gson.fromJson(reader, listType);

            HashMap<String, File> files = new HashMap<>(INITIAL_CAPACITY);
            for (String filePath : filePaths) {
                File file = new File(filePath);
                files.put(file.toString(), file);
            }
            return files;
        } catch (FileNotFoundException e) {
            // Cache file does not exist yet, return an empty list
            return new HashMap<>(INITIAL_CAPACITY);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>(INITIAL_CAPACITY);
        }
    }

    public void saveCacheToStorage() {
        try (Writer writer = new FileWriter(CACHE_FILE_PATH)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(cachedFiles.keySet(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteCacheFileAsync(File file) {
        CompletableFuture.runAsync(() -> {
            deleteCacheFile(file);
        });
    }

    public void deleteCacheFile(File file) {
        Iterator<String> iterator = cachedFiles.keySet().iterator();
        while (iterator.hasNext()) {
            String cachedFile = iterator.next();
            if (cachedFile.equals(file.toString())) {
                iterator.remove();
                return;
            }
        }
        Log.cache("File not found in cache: " + file.getAbsolutePath());
    }

    public static FileCacheManager getInstance() {
        if (instance == null)
            instance = new FileCacheManager();
        return instance;
    }
}
