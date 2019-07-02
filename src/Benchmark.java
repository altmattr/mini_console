import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import pfx.SizedFXApp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Benchmark extends Application {

    Canvas c;
    Vector<SizedFXApp> apps = new Vector();
    SizedFXApp app;

    boolean scaling = true;

    long[][] frameTimes = new long[100][100];

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
        apps.add(new benchmarks.Nyan(c.getGraphicsContext2D()));
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
                        log.append((vals[vals.length-1] - vals[0]) / 10000000l); log.append("\n");
                    }
                    try {FileWriter fw = new FileWriter("logs/"+app.name()+".log"); fw.write(log.toString()); fw.close();} catch (IOException e) {e.printStackTrace();}
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
        System.setProperty("javafx.animation.pulse", "30");
        launch(args);
    }
}
