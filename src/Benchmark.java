import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.input.*;
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

    private static final int NUM_FRAMES_PER_RUN = 100;
    private static final int NUM_RUNS = 10;
    private final Vector<SizedFXApp> apps = new Vector();
    private final StringBuilder log = new StringBuilder();
    private Canvas canvas;
    private SizedFXApp app;

    private final boolean scaling = true;

    //long[] avgFrameMs;


    @Override
    public void start(Stage stage) throws
            Exception {

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        Group root = new Group();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        canvas = new Canvas(bounds.getWidth(), bounds.getHeight());
        root.getChildren().add(canvas);

        //apps.add(new benchmarks.BouncingBall(c.getGraphicsContext2D()));
        apps.add(new benchmarks.Nyan(canvas.getGraphicsContext2D()));
        app = apps.remove(0);
        log.append(app.name());
        log.append("\n");
        log.append(app.description());
        log.append("\n");

        app.setSize(0);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (evt) -> {
            if (evt.getCode() == KeyCode.F4) { // added kill switch for Raspberry Pi
                Platform.exit();
                System.exit(0);
            }
        });

        new AnimationTimer() {
            private final long[][] frameTimes = new long[NUM_RUNS][NUM_FRAMES_PER_RUN];

            int frameIndex = 0;
            int runIndex = 0;

            @Override
            public void handle(long aCurrentNanoTime) {
                frameTimes[runIndex][frameIndex] = aCurrentNanoTime;

                canvas.getGraphicsContext2D().save();
                if (scaling) {
                    canvas.getGraphicsContext2D().scale(bounds.getWidth() / app.width, bounds.getHeight() / app.height);
                } else {
                    double xoffset = (bounds.getWidth() - app.width) / 2;
                    double yoffset = (bounds.getHeight() - app.height) / 2;
                    canvas.getGraphicsContext2D().translate(xoffset, yoffset);
                }

                app.draw();
                canvas.getGraphicsContext2D().restore();

                frameIndex++;

                if (frameIndex == NUM_FRAMES_PER_RUN) {
                    runIndex++;
                    frameIndex = 0;
                    app.setSize(runIndex);
                    app.settings();
                    app.setup();
                }

                if (runIndex == NUM_RUNS) {
                    System.out.println("Terminating");

                    double[] runAvgFrameMs = new double[NUM_RUNS];
                    double[] runSize = new double[NUM_RUNS];
                    for (int i = 0; i < NUM_RUNS; i++) {
                        long[] timesForRun = frameTimes[i];
                        long totalRunTimeNs = timesForRun[timesForRun.length - 1] - timesForRun[0];
                        double avgFrameGapMs = totalRunTimeNs / (NUM_FRAMES_PER_RUN * 1e6);
                        log.append(avgFrameGapMs);
                        log.append("\n");

                        runSize[i] = i+1;
                        runAvgFrameMs[i] = avgFrameGapMs;
                        runIndex++;
                    }

                    try {
                        FileWriter fw = new FileWriter("logs/" + app.name() + ".log");
                        fw.write(log.toString());
                        fw.close();
                        System.out.println(Arrays.toString(runAvgFrameMs));

                        //Chart Code

                        XYChart chart = new XYChartBuilder().width(800).height(600).title("Frame Calculation Time (BB)")
                                .xAxisTitle("size").yAxisTitle("ms").build();
                        chart.getStyler().setYAxisMin(0d);
                        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
                        chart.getStyler().setLegendVisible(false);
                        chart.getStyler().setMarkerSize(8);
                        chart.addSeries("ms per Frame", runSize, runAvgFrameMs);
                        //Chart Code

                        BitmapEncoder.saveBitmap(chart, "./logs/FCT_Bouncing_Balls", BitmapEncoder.BitmapFormat.PNG);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (apps.size() == 0) {
                        System.exit(0);
                    } else {
                        app = apps.remove(0);
                        System.out.println(app.name());
                        System.out.println(app.description());
                        frameIndex = 0;
                        runIndex = 0;
                        app.setSize(0);
                    }
                }
            }
        }.start();

        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
