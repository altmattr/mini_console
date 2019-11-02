package benchmarks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import pfx.FXAppWithRunCount;
import pfx.PVector;

public class Nyan extends FXAppWithRunCount {
    PVector[] location, velocity, gravity;
    Image nyan;

    public Nyan(GraphicsContext g, int totalNumRuns) {
        super(g, totalNumRuns);
    }

    @Override
    public String name() {
        return "NyanCat";
    }

    @Override
    public String description() {
        return "Now the music is stuck in your head.";
    }

    @Override
    public void settings() {
        size(1200, 800);
        location = new PVector[getRunIndex()];
        velocity = new PVector[getRunIndex()];
        gravity = new PVector[getRunIndex()];

        nyan = new Image("nyan.png");
        for (int i = 0; i < getRunIndex(); i++) {
            location[i] = new PVector(
                    600 + random(-600, 600),
                    400 + random(-400, 400));
            velocity[i] = new PVector(
                    random(-4, 4),
                    random(0, 1));
            gravity[i] = new PVector(0, 0.2f);
        }
    }

    @Override
    public void draw() {
        background(0);
        for (int i = 0; i < getRunIndex(); i++) {
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
            image(nyan, location[i].x, location[i].y);
        }
    }
}
