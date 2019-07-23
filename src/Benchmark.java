import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
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

    Canvas c;
    Vector<SizedFXApp> apps = new Vector();
    SizedFXApp app;

    boolean scaling = true;

    static int frames = 100;
    static int repeatTimes = 10;

    int frameIndex = 0;

    //long[] avgFrameMs;
    double[] xAvgFrames = new double[repeatTimes];
    double[] yAvgFrames = new double[repeatTimes];

    long[][] frameTimes = new long[repeatTimes][frames]; // new long[timesToRepeat][frameAnimationLength]

    StringBuilder log = new StringBuilder();

    @Override
    public void start(Stage stage) throws Exception{

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        Group root = new Group();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        c = new Canvas(bounds.getWidth(), bounds.getHeight());
        root.getChildren().add(c);

        //apps.add(new benchmarks.BouncingBall(c.getGraphicsContext2D()));
        apps.add(new benchmarks.BouncingBall(c.getGraphicsContext2D()));
        app = apps.remove(0);
        log.append(app.name()); log.append("\n");
        log.append(app.description()); log.append("\n");

        app.setSize(0);

        new AnimationTimer(){
            int repeat = 0;
            int size = 0;
            public void handle(long currentNanoTime){
                frameTimes[size][repeat] = currentNanoTime;

                c.getGraphicsContext2D().save();
                if (scaling) {
                    c.getGraphicsContext2D().scale(bounds.getWidth() / app.width, bounds.getHeight() / app.height);
                } else {
                    double xoffset = (bounds.getWidth() - app.width) / 2;
                    double yoffset = (bounds.getHeight() - app.height) / 2;
                    c.getGraphicsContext2D().translate(xoffset, yoffset);
                }

                app.draw();
                c.getGraphicsContext2D().restore();

                repeat++;

                if (repeat == frameTimes[size].length){
                    size++;
                    repeat = 0;
                    app.setSize(size);
                    app.settings();
                    app.setup();
                }
                if (size == frameTimes.length){
                    for(long[] vals: frameTimes){
                        long avgFrameGap = (vals[vals.length-1] - vals[0]) / (frames * 1000000l);
                        log.append(avgFrameGap); log.append("\n");

                        xAvgFrames[frameIndex] = frameIndex;
                        yAvgFrames[frameIndex] = avgFrameGap;
                        frameIndex++;
                    }
                    try {
                        FileWriter fw = new FileWriter("logs/"+app.name()+".log" );
                        fw.write(log.toString()); fw.close();
                        System.out.println(Arrays.toString(yAvgFrames));

                        //Chart Code

                        XYChart chart = new XYChartBuilder().width(800).height(600).title("Frame Calculation Time (BB)")
                                .xAxisTitle("size").yAxisTitle("ms").build();
                        chart.getStyler().setYAxisMin(0d);
                        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
                        chart.getStyler().setLegendVisible(false);
                        chart.getStyler().setMarkerSize(8);
                        chart.addSeries("ms per Frame", xAvgFrames, yAvgFrames);
                        //Chart Code

                        BitmapEncoder.saveBitmap(chart, "./logs/FCT_Bouncing_Balls", BitmapEncoder.BitmapFormat.PNG);
                    } catch (IOException e) {e.printStackTrace();}


                    if (apps.size() == 0) {
                        System.exit(0);
                    }
                    else {
                        app = apps.remove(0);
                        System.out.println(app.name());
                        System.out.println(app.description());
                        repeat = 0;
                        size = 0;
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