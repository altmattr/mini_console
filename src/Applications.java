import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;
import pfx.FXApp;
import pfx.PVector;
import studentwork.BoxCarrier;
import studentwork.GameOfLife;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Applications extends FXApp
{
    List<Pair<FXApp,Optional<Image>>> apps;
    Consumer<FXApp> appResetter;

    Image defaultImage;

    PVector screenSize, selected, appNumSize;
    int appSize = 200, appSpacing = 30, appToLaunch;
    Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

    int numOfRows; //How many full rows are displayed
    int lastRowCol; //How many columns are used on the final row

    public Applications(GraphicsContext g, Consumer<FXApp> appResetter) {
        super(g);
        this.appResetter = appResetter;

        apps = Arrays.asList(
                new Pair(new BoxCarrier(g), Optional.empty()),
                new Pair(new GameOfLife(g), Optional.empty()),
                new Pair(new examples.BouncingBall(g), Optional.empty()),
                new Pair(new examples.SineWave(g), Optional.empty()),
                new Pair(new examples.ScalingChecker(g), Optional.empty()),
                new Pair(new examples.AdditiveWave(g), Optional.empty()),
                new Pair(new examples.Array2D(g), Optional.empty())
        );
    }

    @Override
    public void settings()
    {
        defaultImage = new Image("boxcarrier.png", appSize-2, appSize-2, true, false);
        screenSize = new PVector((float)(screenDim.getWidth()), (float)(screenDim.getHeight()));
        size((int)screenSize.x,(int)screenSize.y);
        appNumSize = new PVector(6,3);
        selected = new PVector(0,0);
    }

    public void setup() { }

    public void draw()
    {
        background(10, 10, 10);
        strokeWeight(3);

        appToLaunch = (int)((selected.y * appNumSize.x) + selected.x);

        textAlign(CENTER);

        for (int x = 0; x < appNumSize.x; x++)
        {
            for (int y = 0; y < appNumSize.y; y++)
            {
                if (selected.x == x && selected.y == y)
                {
                    stroke(255,0,0);
                }
                else
                {
                    stroke(255);
                }
                if ((y * ((int)appNumSize.x) + x) < apps.size()) {

                    image(apps.get(x).getValue().orElse(defaultImage),
                            (appSpacing / 2) + x * (appSize + appSpacing) + ((screenSize.x) - (appNumSize.x * (appSize + appSpacing))) / 2 + 1,
                            (appSpacing / 2) + y * (appSize + appSpacing * 2) + ((screenSize.y)
                                    - (appNumSize.y * (appSize + appSpacing * 2)))/2 + 1
                    );

                    noFill();
                    rect(
                        (appSpacing/2)+x*(appSize+appSpacing)+((screenSize.x)-(appNumSize.x*(appSize+appSpacing)))/2,
                        (appSpacing/2)+y*(appSize+appSpacing*2)+((screenSize.y)-(appNumSize.y*(appSize+appSpacing*2)))/2,
                        appSize+0,
                        appSize+0
                    );

                    fill(255);
                    text(
                            apps.get((y * ((int) appNumSize.x) + x)).getKey().name() + "",
                            (appSpacing / 2) + x * (appSize + appSpacing) + ((screenSize.x) - (appNumSize.x * (appSize + appSpacing))) / 2 + (appSize/2),
                            (appSpacing / 2) + y * (appSize + appSpacing * 2) + ((screenSize.y)
                                    - (appNumSize.y * (appSize + appSpacing * 2)))/2+ (appSize+(appSpacing/2))
                    );
                }
            }
        }
    }

    public void keyPressed(KeyEvent evt)
    {


        println(numOfRows);

        numOfRows = Math.round(apps.size()/appNumSize.x);
        lastRowCol = (int) (apps.size()-((numOfRows*appNumSize.x)));

        if (evt.getCode() == KeyCode.UP)
        {
            selected.y--;

            if (selected.x < lastRowCol)
            { if (selected.y < 0) { selected.y = numOfRows; } }
            else
            { if (selected.y < 0) { selected.y = numOfRows-1; } }

            //if (selected.y < 0) { selected.y = appNumSize.y-1; }
        }
        else if (evt.getCode() == KeyCode.DOWN)
        {
            selected.y++;

            if (selected.x < lastRowCol)
            { if (selected.y > numOfRows) { selected.y = 0; } }
            else
            { if (selected.y > numOfRows-1) { selected.y = 0; } }

            //if (selected.y > appNumSize.y-1) { selected.y = 0; }
        }
        else if (evt.getCode() == KeyCode.LEFT)
        {
            selected.x--;

            if (selected.y == numOfRows)
            { if (selected.x < 0) { selected.x = lastRowCol-1; } }
            else
            { if (selected.x < 0) { selected.x = appNumSize.x-1; } }
        }
        else if (evt.getCode() == KeyCode.RIGHT)
        {
            selected.x++;

            if (selected.y == numOfRows)
            { if (selected.x > lastRowCol-1) { selected.x = 0; } }
            else
            { if (selected.x > appNumSize.x-1) { selected.x = 0; } }
        }
        else if (evt.getCode() == KeyCode.ENTER)
        { appResetter.accept(apps.get(appToLaunch).getKey()); }
    }
}
