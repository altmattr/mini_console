import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pfx.FXApp;
import pfx.PVector;

public class GridChooser extends Application
{
    FXApp homeScreen;
    FXApp currentApp;
    Canvas c;

    boolean scaling = true;
    boolean showPerformance;

    PVector screenSize;
    //Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

    @Override
    public void start(Stage stage) throws Exception
    {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        screenSize = new PVector((float)(bounds.getWidth()), (float)(bounds.getHeight()));
        //screenSize = new PVector((float)(screenDim.getWidth()), (float)(screenDim.getHeight()));

        stage.setWidth(screenSize.x);
        stage.setHeight(screenSize.y);

        Group root = new Group();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        c = new Canvas(screenSize.x, screenSize.y);
        root.getChildren().add(c);

        homeScreen = new Applications(c.getGraphicsContext2D(), (FXApp a) -> {
            c.getGraphicsContext2D().save(); // fix issue #2:
            currentApp = a;
            currentApp.settings();
            currentApp.setup();
        });

        currentApp = homeScreen;
        currentApp.settings();
        currentApp.setup();


        /*
        homeScreen = new Applications(c.getGraphicsContext2D(), (FXApp a) -> {currentApp = a; currentApp.settings(); currentApp.setup();});

        c.getGraphicsContext2D().save(); // fix issue #2

        currentApp = homeScreen;
        currentApp.settings();
        currentApp.setup();
         */

        scene.addEventHandler(KeyEvent.KEY_PRESSED, (evt) ->
        {
            if (evt.getCode() == KeyCode.F4)
            { System.exit(0); }
            else if (evt.getCode() == KeyCode.F5)
            { showPerformance = !showPerformance; }
            else if (evt.getCode() == KeyCode.F12)
            { scaling = ! scaling; }
            else if (evt.getCode() == KeyCode.ESCAPE)
            {
                c.getGraphicsContext2D().restore(); // fix issue #2
                currentApp = homeScreen;
            }
            // I really should be setting up the keycode and key here.
            currentApp.keyPressed(evt);
        });

        scene.addEventHandler(KeyEvent.KEY_TYPED, (evt) -> {
            System.out.println("the second event handler (that passes things on to the app) got run");
            System.out.println(evt);
            System.out.println(evt.getCharacter());
            if (evt.getCharacter() != null && evt.getCharacter().length() > 0)
            { // TODO: I can't get a check here that does what I want.  It either lets escape through or it lets nothing through!!
                currentApp.key = evt.getCharacter().charAt(0);
                currentApp.keyTyped();
            }
        });

        FPSChart fps = new FPSChart();
        new AnimationTimer() // Draws the actual app.
        {
            public void handle(long currentNanoTime)
            {
                fps.registerFrameStart(currentNanoTime);
                long start = System.currentTimeMillis();
                c.getGraphicsContext2D().save();

                if (scaling) {
                    c.getGraphicsContext2D().scale(screenSize.x / currentApp.width, screenSize.y / currentApp.height);
                } else {
                    //double xoffset = (screenSize.x - currentApp.width) / 2;
                    double yoffset = (screenSize.y - currentApp.height) / 2;
                    c.getGraphicsContext2D().translate(yoffset, yoffset);
                }

                currentApp.draw();
                c.getGraphicsContext2D().restore();

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

    public static void main(String[] args) { launch(args); }
}
