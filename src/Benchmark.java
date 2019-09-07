import benchmarks.CircleSize;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import pfx.SizedFXApp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

public class Benchmark extends Application {
    @Override
    public void start(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Canvas canvas = setupStage(stage, bounds);

        new AnimationTimerImpl(
                canvas,
                bounds,
                apps(canvas.getGraphicsContext2D()))
                .start();

        stage.show();
    }

    private Vector<SizedFXApp> apps(GraphicsContext graphicsContext) {
        Vector<SizedFXApp> apps = new Vector<>();
        //apps.add(new benchmarks.BouncingBall(c.getGraphicsContext2D()));
        apps.add(new CircleSize(graphicsContext));
        return apps;
    }

    private Canvas setupStage(Stage stage, Rectangle2D bounds) {
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        Group root = new Group();
        Scene scene = new Scene(root);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (evt) -> {
            if (evt.getCode() == KeyCode.F4) { // added kill switch for Raspberry Pi
                Platform.exit();
                System.exit(0);
            }
        });

        stage.setScene(scene);
        stage.setFullScreen(true);
        Canvas canvas = new Canvas(bounds.getWidth(), bounds.getHeight());
        root.getChildren().add(canvas);
        return canvas;
    }

    private static class RunStats {
        final double[] averageFrameMs;
        final double[] size;

        private RunStats(double[] averageFrameMs, double[] size) {
            this.averageFrameMs = averageFrameMs;
            this.size = size;
        }
    }

    private static class AnimationTimerImpl extends AnimationTimer {
        private static final boolean SCALING = true;
        private static final int NUM_FRAMES_PER_RUN = 100;
        private static final int NUM_RUNS = 10;
        private final StringBuilder log = new StringBuilder();
        private final long[][] frameTimes = new long[NUM_RUNS][NUM_FRAMES_PER_RUN];
        private final Rectangle2D bounds;
        private final Vector<SizedFXApp> pendingApps;
        private final Canvas canvas;
        private SizedFXApp runningApp;
        private int frameIndex = 0;
        private int runIndex = 0;

        AnimationTimerImpl(Canvas canvas, Rectangle2D bounds, Vector<SizedFXApp> apps) {
            this.canvas = canvas;
            this.bounds = bounds;
            pendingApps = apps;
            restartWithNextApp();
        }

        @Override
        public void handle(long aCurrentNanoTime) {
            frameTimes[runIndex][frameIndex] = aCurrentNanoTime;
            drawFrame();
            frameIndex++;

            if (frameIndex == NUM_FRAMES_PER_RUN) {
                runIndex++;
                frameIndex = 0;
                runningApp.setSize(runIndex);
                runningApp.settings();
                runningApp.setup();
            }

            if (runIndex == NUM_RUNS) {
                writeResults();

                if (pendingApps.isEmpty()) {
                    System.exit(0);
                } else {
                    restartWithNextApp();
                }
            }
        }

        private void restartWithNextApp() {
            runningApp = pendingApps.remove(0);

            String appDetails = String.format("%s\n%s\n", runningApp.name(), runningApp.description());
            System.out.print(appDetails);
            log.append(appDetails);

            frameIndex = 0;
            runIndex = 0;
            runningApp.setSize(0);

        }

        private RunStats runStats() {
            RunStats runStats = new RunStats(
                    new double[NUM_RUNS],
                    new double[NUM_RUNS]);

            for (int i = 0; i < NUM_RUNS; i++) {
                long[] timesForRun = frameTimes[i];
                long totalRunTimeNs = timesForRun[timesForRun.length - 1] - timesForRun[0];
                runStats.averageFrameMs[i] = totalRunTimeNs / (NUM_FRAMES_PER_RUN * 1e6);
                runStats.size[i] = i + 1;
            }
            return runStats;
        }

        private void writeResults() {
            RunStats runStats = runStats();

            for (double avgFrameMs : runStats.averageFrameMs) {
                log.append(avgFrameMs);
                log.append("\n");
            }

            try {
                writeRessultsFile(runStats);
                writeChart(runStats);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void writeRessultsFile(RunStats runStats) throws
                IOException {
            FileWriter fw = new FileWriter("logs/" + runningApp.name() + ".log");
            fw.write(log.toString());
            fw.close();
            System.out.println(Arrays.toString(runStats.averageFrameMs));
        }

        private void writeChart(RunStats runStats) throws
                IOException {
            XYChart chart = new XYChartBuilder()
                    .width(800)
                    .height(600)
                    .title("Frame Calculation Time (BB)")
                    .xAxisTitle("size")
                    .yAxisTitle("ms")
                    .build();

            chart.getStyler().setYAxisMin(0d);
            chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            chart.getStyler().setLegendVisible(false);
            chart.getStyler().setMarkerSize(8);
            chart.addSeries("ms per Frame", runStats.size, runStats.averageFrameMs);

            BitmapEncoder.saveBitmap(chart, "./logs/FCT_Bouncing_Balls", BitmapEncoder.BitmapFormat.PNG);
        }

        private void drawFrame() {
            canvas.getGraphicsContext2D().save();
            if (SCALING) {
                canvas.getGraphicsContext2D()
                        .scale(bounds.getWidth() / runningApp.width, bounds.getHeight() / runningApp.height);
            } else {
                double xOffset = (bounds.getWidth() - runningApp.width) / 2;
                double yOffset = (bounds.getHeight() - runningApp.height) / 2;
                canvas.getGraphicsContext2D().translate(xOffset, yOffset);
            }

            runningApp.draw();
            canvas.getGraphicsContext2D().restore();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
