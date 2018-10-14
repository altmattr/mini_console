import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import pfx.FXApp;

public class Main extends Application {

    Canvas c;
    FXApp app;

    boolean scaling = false;
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

        app = new studentwork.EliseMcCabe(c.getGraphicsContext2D());

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.F4) {
                System.exit(0);
            } else if (key.getCode() == KeyCode.F5){
                showPerformance = !showPerformance;
            } else if (key.getCode() == KeyCode.F12){
                scaling = ! scaling;
            }
            app.key = (char)key.getCode().impl_getCode();
            app.keyPressed();
        });

        app.settings();
        app.setup();

        FPSChart fps = new FPSChart();
        new AnimationTimer(){
            public void handle(long currentNanoTime){
                long start = System.currentTimeMillis();
                c.getGraphicsContext2D().save();
                if (scaling){
                    c.getGraphicsContext2D().scale(bounds.getWidth()/app.width, bounds.getHeight()/app.height);
                } else {
                    double xoffset = (bounds.getWidth() - app.width)/2;
                    double yoffset = (bounds.getHeight() - app.height)/2;
                    c.getGraphicsContext2D().translate(xoffset, yoffset);
                }

                app.draw();

                // show performance
                if (showPerformance) {
                    c.getGraphicsContext2D().save();
                    long end = System.currentTimeMillis();

                    fps.logFrame(16, end - start);
                    fps.draw(c.getGraphicsContext2D(), 50, 50);
                    c.getGraphicsContext2D().restore();
                }
                c.getGraphicsContext2D().restore();
            }
        }.start();

        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
