package app.components.spectrum.painter;

import material.animation.MaterialFixedTimer;

import java.util.ArrayList;

public class SharedPainterTimer extends MaterialFixedTimer {
    public static final int FPS = 120;
    private static final int DELAY = 1000/FPS;
    private static SharedPainterTimer instance;
    private final ArrayList<SpectrumPainter> painters = new ArrayList<>(5);
    public SharedPainterTimer() {
        super(DELAY);
    }

    public static SharedPainterTimer getInstance() {
        if(instance == null)
            instance = new SharedPainterTimer();
        return instance;
    }

    public void add(SpectrumPainter painter){
        painters.add(painter);
        paintersUpdated();
    }
    public void remove(SpectrumPainter painter){
        painters.remove(painter);
        paintersUpdated();
    }

    public void removeAll(){
        painters.clear();
        paintersUpdated();
    }
    private void paintersUpdated() {
        if(!painters.isEmpty())
            start();
    }

    @Override
    public void tick(float dt) {
        for (int i = painters.size() - 1; i >= 0; i--) {
            SpectrumPainter painter = painters.get(i);
            painter.tick(dt);
        }
    }
}
