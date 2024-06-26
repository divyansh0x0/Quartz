package app.components.spectrum.painter;

import java.awt.*;
import java.awt.geom.*;

class SloppyStroke implements Stroke {
    BasicStroke stroke;

    final float sloppiness = 4;

    public SloppyStroke(float width) {
        this.stroke = new BasicStroke(width); // Used to stroke modified shape
//        this.sloppiness = sloppiness; // How sloppy should we be?
    }

    public Shape createStrokedShape(Shape shape) {
        GeneralPath newshape = new GeneralPath(); // Start with an empty shape

        // Iterate through the specified shape, perturb its coordinates, and
        // use them to build up the new shape.
        float[] coords = new float[6];
        for (PathIterator i = shape.getPathIterator(null); !i.isDone(); i
                .next()) {
            int type = i.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_MOVETO -> {
                    perturb(coords, 2);
                    newshape.moveTo(coords[0], coords[1]);
                }
                case PathIterator.SEG_LINETO -> {
                    perturb(coords, 2);
                    newshape.lineTo(coords[0], coords[1]);
                }
                case PathIterator.SEG_QUADTO -> {
                    perturb(coords, 4);
                    newshape.quadTo(coords[0], coords[1], coords[2], coords[3]);
                }
                case PathIterator.SEG_CUBICTO -> {
                    perturb(coords, 6);
                    newshape.curveTo(coords[0], coords[1], coords[2], coords[3],
                            coords[4], coords[5]);
                }
                case PathIterator.SEG_CLOSE -> newshape.closePath();
            }
        }

        // Finally, stroke the perturbed shape and return the result
        return stroke.createStrokedShape(newshape);
    }

    // Randomly modify the specified number of coordinates, by an amount
    // specified by the sloppiness field.
    void perturb(float[] coords, int numCoords) {
        for (int i = 0; i < numCoords; i++)
            coords[i] += (float) ((Math.random() * 2 - 1.0) * sloppiness);
    }
}
