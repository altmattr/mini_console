import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.stage.*;

public class Main extends Application {

    Canvas c;
    FXApp app;

    public char key;
    public int mouseX;
    public int mouseY;

    public void setup(){
    }

    public void draw(){
        GraphicsContext g = c.getGraphicsContext2D();
        g.setFill(Color.BLUE);
        g.fillRect(75,75,100,100);
    }

    public void mousePressed(){

    }

    public void keyPressed(){

    }

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
        c = new Canvas(bounds.getWidth(), bounds.getHeight());

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.F4) {
                System.exit(0);
            }
        });

        root.getChildren().add(c);

        app = new BouncingBall(c.getGraphicsContext2D());

        app.settings();
        app.setup();

        FPSChart fps = new FPSChart();
        new AnimationTimer(){
            public void handle(long currentNanoTime){
                long start = System.currentTimeMillis();

                app.draw();

                // show performance
                c.getGraphicsContext2D().save();
                long end = System.currentTimeMillis();

                fps.logFrame(16, end - start);
                fps.draw(c.getGraphicsContext2D(), 50, 50);
                c.getGraphicsContext2D().restore();
            }
        }.start();

        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
