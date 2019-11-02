package benchmarks;

import javafx.scene.canvas.GraphicsContext;
import pfx.FXAppWithRunCount;
import pfx.PVector;

public class BouncingBall extends FXAppWithRunCount {
    private static final int NUM_EXTRA_BALLS_PER_RUN = 100;
    private PVector[] location, velocity, gravity;
    private int numBalls;

    public BouncingBall(GraphicsContext g, int totalNumRuns) {
        super(g, totalNumRuns);
    }

    @Override
    public String name() {
        return "Bouncing_Balls_" + NUM_EXTRA_BALLS_PER_RUN;
    }

    @Override
    public String description() {
        return "Each step is " + NUM_EXTRA_BALLS_PER_RUN + " balls.";
    }

    @Override
    public void settings() {
        size(1200, 800);
        numBalls = getRunIndex() * NUM_EXTRA_BALLS_PER_RUN;
        location = new PVector[numBalls];
        velocity = new PVector[numBalls];
        gravity = new PVector[numBalls];

        for (int i = 0; i < numBalls; i++) {
            location[i] = new PVector(100f + random(0, 4),
                    100f + random(0, 4)); // TODO: deal with these f's that should not be here
            velocity[i] = new PVector(1.5f + random(0, 1), 2.1f + random(0, 1));
            gravity[i] = new PVector(0f, 0.2f);
        }
    }

    @Override
    public void draw() {
        background(0);
        for (int i = 0; i < numBalls; i++) {
            location[i].add(velocity[i]);
            velocity[i].add(gravity[i]);

            if (location[i].x > width || location[i].x < 0) {
                velocity[i].x = velocity[i].x * -1;
            }

            if (location[i].y > height) {
                velocity[i].y = velocity[i].y * -0.95f;
                location[i].y = height;
            }

            stroke(255);
            strokeWeight(2);
            fill(127);
            ellipse(location[i].x, location[i].y, 48, 48);
        }
    }
}
