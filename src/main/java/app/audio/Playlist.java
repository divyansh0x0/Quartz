package app.audio;

import org.jetbrains.annotations.NotNull;

public class Playlist implements Comparable<Playlist> {
    private String name;
    private byte[] thumbnail;
    private String dateCreated;
    private boolean isPinned;

    public Playlist(String name, byte[] thumbnail, String dateCreated, boolean isPinned) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.dateCreated = dateCreated;
        this.isPinned = isPinned;
    }

    public String getName() {
        return name;
    }

    public Playlist setName(String name) {
        this.name = name;
        return this;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public Playlist setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public Playlist setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public Playlist setPinned(boolean pinned) {
        isPinned = pinned;
        return this;
    }

    @Override
    public int compareTo(@NotNull Playlist o) {
        return name.compareTo(o.name);
    }
}
