package pfx;

import javafx.scene.canvas.GraphicsContext;

public class FXAppWithRunCount extends FXApp {
    private final int totalNumRuns;
    private int runIndex = 0;

    public FXAppWithRunCount(GraphicsContext g, int totalNumRuns) {
        super(g);
        this.totalNumRuns = totalNumRuns;

        if (this.totalNumRuns <= 0) {
            throw new RuntimeException("Expecting positive totalNumRuns");
        }
    }

    public void setRunIndex(int runIndex) {
        if (runIndex < 0 || runIndex >= totalNumRuns || runIndex < this.runIndex) {
            throw new RuntimeException("Invalid run index: " + runIndex);
        }

        this.runIndex = runIndex;
        settings();
        setup();
    }

    public int getTotalNumRuns() {
        return totalNumRuns;
    }

    protected int getRunIndex() {
        return runIndex;
    }

    public boolean fixedSize() {
        return false;
    }
}
