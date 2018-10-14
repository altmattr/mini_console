import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.canvas.*;
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

        double dpi = Screen.getPrimary().getDpi();
        Group root = new Group();
        stage.setScene(new Scene(root));
        c = new Canvas(bounds.getWidth(), bounds.getHeight());

        root.getChildren().add(c);

        app = new TitleScreen(c.getGraphicsContext2D());

        app.settings();
        app.setup();

        new AnimationTimer(){
            public void handle(long currentNanoTime){
                app.draw();
            }
        }.start();

        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
