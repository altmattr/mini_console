import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import pfx.FXApp;

import java.util.*;

public class Main extends Application {

    Canvas c;
    FXApp app;
    FXApp defaultApp;

    boolean scaling = true;
    boolean showPerformance;

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

	defaultApp = new ApplicationChooser(c.getGraphicsContext2D(), (FXApp a) -> {app = a; app.settings(); app.setup();});

        app = defaultApp;
        app.settings();
        app.setup();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (evt) -> {
            if (evt.getCode() == KeyCode.F4) {
                System.exit(0);
            } else if (evt.getCode() == KeyCode.F5){
                showPerformance = !showPerformance;
            } else if (evt.getCode() == KeyCode.F12){
                scaling = ! scaling;
            } else if (evt.getCode() == KeyCode.ESCAPE){
                app = defaultApp;
            }
            app.keyPressed(evt);
        });

        scene.addEventHandler(KeyEvent.KEY_TYPED, (evt) -> {
            app.key = evt.getCharacter().charAt(0);
            app.keyTyped();
        });

        FPSChart fps = new FPSChart();
        new AnimationTimer(){
            public void handle(long currentNanoTime){
                long start = System.currentTimeMillis();
                c.getGraphicsContext2D().save();
                if (scaling) {
                    c.getGraphicsContext2D().scale(bounds.getWidth() / app.width, bounds.getHeight() / app.height);
                } else {
                    double xoffset = (bounds.getWidth() - app.width) / 2;
                    double yoffset = (bounds.getHeight() - app.height) / 2;
                    c.getGraphicsContext2D().translate(xoffset, yoffset);
                }

                c.getGraphicsContext2D().save();
                app.draw();
                c.getGraphicsContext2D().restore();

                c.getGraphicsContext2D().restore();

                // show performance
                if (showPerformance) {
                    c.getGraphicsContext2D().save();
                    long end = System.currentTimeMillis();

                    fps.logFrame(16, end - start);
                    fps.draw(c.getGraphicsContext2D(), 50, 50);
                    c.getGraphicsContext2D().restore();
                }
            }
        }.start();

        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
