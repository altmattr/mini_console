package benchmarks;

import javafx.scene.canvas.GraphicsContext;
import pfx.FXAppWithRunCount;
import pfx.PVector;

public class Expanding extends FXAppWithRunCount {
    private static final int MIN_SIZE = 100;
    private static final int MAX_SIZE = 1000;
    private static final int BALL_DIAMETER = 20;
    private static final PVector VELOCITY = new PVector(0f, 2f);
    private final int sizeStepPerRun;
    private PVector[] location;

    public Expanding(GraphicsContext g, int totalNumRuns) {
        super(g, totalNumRuns);

        sizeStepPerRun = totalNumRuns <= 1
                ? 0
                : (int) Math.round((MAX_SIZE - MIN_SIZE) / (totalNumRuns - 1.0));
    }

    @Override
    public String name() {
        return "Expanding";
    }

    @Override
    public String description() {
        return "Each step is " + sizeStepPerRun
                + "\nHow does the display size affect performance?";
    }

    @Override
    public boolean fixedSize() {
        return true;
    }

    @Override
    public void settings() {
        int size = MIN_SIZE + sizeStepPerRun * getRunIndex();
        size(size, size);

        int numRows = size / BALL_DIAMETER;
        int numBalls = numRows * numRows;
        location = new PVector[numBalls];

        for (int i = 0; i < numBalls; i++) {
            int colIdx = i % numRows;
            int rowIdx = i / numRows;
            location[i] = new PVector(
                    (float) (BALL_DIAMETER * (0.5 + colIdx)),
                    (float) (BALL_DIAMETER * (0.5 + rowIdx)));
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
            ellipse(aPVector.x, aPVector.y, BALL_DIAMETER, BALL_DIAMETER);
        }
    }
}
