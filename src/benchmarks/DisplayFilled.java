package benchmarks;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import pfx.FXAppWithRunCount;
import pfx.PVector;

import java.util.function.Consumer;

public class DisplayFilled extends FXAppWithRunCount {
    private static final int SIZE = 1000;
    private static final int DIAMETER = 50;
    private static final PVector VELOCITY = new PVector(0f, 2f);
    private final Consumer<PVector> drawImage;
    private final Display display;
    private PVector[] location;

    public enum Display {
        CIRCLE,
        CIRCLE_ALPHA,
        IMAGE,
        TEXT
    }

    public DisplayFilled(GraphicsContext g, int totalNumRuns, Display display) {
        super(g, totalNumRuns);
        this.display = display;
        drawImage = drawImage(display);
    }

    private Consumer<PVector> drawImage(Display display) {
        switch (display) {
            case CIRCLE:
                fill(127);
                return location -> ellipse(location.x, location.y, DIAMETER, DIAMETER);
            case CIRCLE_ALPHA:
                fill(127, 50);
                return location -> ellipse(location.x, location.y, DIAMETER, DIAMETER);
            case IMAGE:
                Image image = new Image("nyan_50.png");
                return location -> image(image, location.x, location.y);
            case TEXT:
                return location -> text("ABC", location.x, location.y);
        }
        throw new RuntimeException(display.name() + " not handled");
    }

    @Override
    public String name() {
        return "DisplayFilled_" + display.name();
    }

    @Override
    public String description() {
        return "Fill the entire display - no change from step to step";
    }

    @Override
    public boolean fixedSize() {
        return true;
    }

    @Override
    public void settings() {
        size(SIZE, SIZE);

        int numRows = SIZE / DIAMETER;
        location = new PVector[numRows * numRows];

        int locationIdx = 0;
        for (int rowIdx = 0; rowIdx < numRows; rowIdx++) {
            for (int colIdx = 0; colIdx < numRows; colIdx++) {
                location[locationIdx++] = new PVector(
                        (float) (DIAMETER * (0.5 + colIdx)),
                        (float) (DIAMETER * (0.5 + rowIdx)));
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
            drawImage.accept(aPVector);
        }
    }
}
