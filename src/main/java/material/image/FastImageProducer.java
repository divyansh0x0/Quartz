package material.image;

import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.Hashtable;

public class FastImageProducer implements ImageProducer {
    @Override
    public void addConsumer(ImageConsumer imageConsumer) {

    }

    @Override
    public boolean isConsumer(ImageConsumer imageConsumer) {
        return false;
    }

    @Override
    public void removeConsumer(ImageConsumer imageConsumer) {

    }

    @Override
    public void startProduction(ImageConsumer imageConsumer) {

    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer imageConsumer) {

    }
}
