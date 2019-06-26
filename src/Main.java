import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.input.*;
import javafx.stage.*;
import pfx.FXApp;

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
            // I really should be setting up the keycode and key here.
            app.keyPressed(evt);
        });

        scene.addEventHandler(KeyEvent.KEY_TYPED, (evt) -> {
            System.out.println("the second event handler (that passes things on to the app) got run");
            System.out.println(evt);
            System.out.println(evt.getCharacter());
            if (evt.getCharacter() != null && evt.getCharacter().length() > 0) { // TODO: I can't get a check here that does what I want.  It either lets escape through or it lets nothing through!!
                app.key = evt.getCharacter().charAt(0);
                app.keyTyped();
            }
        });

        FPSChart fps = new FPSChart();
        new AnimationTimer(){
            public void handle(long currentNanoTime){
                fps.registerFrameStart(currentNanoTime);
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
                long end = System.currentTimeMillis();
                fps.registerFrameEnd(end - start);
                if (showPerformance) {
                    c.getGraphicsContext2D().save();
                    fps.draw(c.getGraphicsContext2D(), 50, 50);
                    c.getGraphicsContext2D().restore();
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
