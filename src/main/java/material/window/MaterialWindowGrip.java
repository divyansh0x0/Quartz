package material.window;

public enum MaterialWindowGrip {
    IGNORE_CAPTION_BAR,
    CONSIDER_CAPTION_BAR;

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
