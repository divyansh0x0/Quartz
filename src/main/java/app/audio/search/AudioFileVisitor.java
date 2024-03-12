package app.audio.search;

import app.audio.AudioData;
import app.audio.indexer.AudioDataIndexer;
import app.settings.StartupSettings;
import material.utils.Log;
import material.utils.OsUtils;
import material.utils.enums.OsType;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AudioFileVisitor implements FileVisitor<Path> {
    private final ArrayList<File> AUDIO_FILE_PATHS = new ArrayList<>(20);
    private final List<String> ignorePaths = StartupSettings.EXCLUDED_FOLDERS;

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        File file = dir.toFile();
        if (OsUtils.getOsType() == OsType.WINDOWS) {
            if (ignorePaths.contains(file.getName().toLowerCase(Locale.ROOT))) {
//                Log.warn("Skipping system file " + file);
                return FileVisitResult.SKIP_SUBTREE;
            }
            DosFileAttributeView view = Files.getFileAttributeView(dir, DosFileAttributeView.class);
            if (dir.getNameCount() != 0 && view != null && view.readAttributes().isHidden()) {
//                Log.warn("Skipping hidden file " + file);
                return FileVisitResult.SKIP_SUBTREE;
            }
        }
        if (file.getName().startsWith(".")) {
//                Log.warn("Skipping " + file);
            return FileVisitResult.SKIP_SUBTREE;
        }

//        if (file.isHidden())
//            return FileVisitResult.SKIP_SIBLINGS;
//        Log.info("[ Visiting directory: " + dir.toAbsolutePath() + " ]");
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        try {
            if (AudioData.isValidAudio(path)) {
                addAudio(path);
            }
        } catch (Exception e) {
            Log.error(e);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if (exc != null)
            Log.error("File visit failed: " + exc);
        return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc != null)
            Log.error("Post visit failed: " + exc);
        return FileVisitResult.CONTINUE;
    }

    private void addAudio(Path path) {
        if (path == null)
            return;

        if (AudioDataIndexer.getInstance().isFileLoaded(path.toString()) && AudioData.isValidAudio(path))
            Log.warn("File already loaded: " + path);
        File file = path.toFile();
        AUDIO_FILE_PATHS.add(file);
    }

    public List<File> getAudioFileArrayList() {
        return AUDIO_FILE_PATHS;
    }
}
