package benchmarks;

import javafx.scene.canvas.GraphicsContext;
import pfx.FXAppWithRunCount;
import pfx.PVector;

public class DisplayFilling extends FXAppWithRunCount {
    private static final int SIZE = 1000;
    private static final PVector VELOCITY = new PVector(0f, 2f);
    private final float ballDiameter;
    private PVector[] location;

    public DisplayFilling(GraphicsContext g, int totalNumRuns) {
        super(g, totalNumRuns);

        ballDiameter = SIZE / (2f * (totalNumRuns - 1));
    }

    @Override
    public String name() {
        return "Filling";
    }

    @Override
    public String description() {
        return "Each step is one extra column"
                + "\nHow does the fraction drawn affect performance?";
    }

    @Override
    public boolean fixedSize() {
        return true;
    }

    @Override
    public void settings() {
        size(SIZE, SIZE);

        location = new PVector[
                2 * getTotalNumRuns() *
                        2 * getRunIndex()];

        int locationIdx = 0;
        for (int rowIdx = 0; rowIdx < 2 * getTotalNumRuns(); rowIdx++) {
            for (int colIdx = 0; colIdx < 2 * getRunIndex(); colIdx++) {
                location[locationIdx++] = new PVector(
                        (float) (ballDiameter * (0.5 + colIdx)),
                        (float) (ballDiameter * (0.5 + rowIdx)));
            }
        }
    }

    @Override
    public void draw() {
        background(0);
        stroke(255);
        strokeWeight(2);
        fill(127);

        for (PVector aPVector : location) {
            aPVector.add(VELOCITY);

            if (aPVector.x > width) {
                aPVector.x -= width;
            }

            if (aPVector.y > height) {
                aPVector.y -= height;
            }
            ellipse(aPVector.x, aPVector.y, ballDiameter, ballDiameter);
        }
    }
}
