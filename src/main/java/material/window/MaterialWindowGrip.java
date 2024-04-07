package material.window;

public enum MaterialWindowGrip {
    /**
     * Use custom bounds
     */
    CUSTOM,
    /**
     * Uses the whole window as GRIP for moving
     */
    FULL_WINDOW,
    /**
     * Has same bounds as the caption bar
     */
    EXCLUDE_CAPTION_BAR_WIDTH;

    public int height = 20;
    public int x;
    public int y;
    public int width=-1;
    public MaterialWindowGrip setGripHeight(int height){
        this.height = height;
        return this;
    }

    public MaterialWindowGrip setGripWidth(int width) {
        this.width = width;
        return this;
    }
}
