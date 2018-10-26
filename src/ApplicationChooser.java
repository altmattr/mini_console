import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;
import pfx.FXApp;
import studentwork.BoxCarrier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ApplicationChooser extends FXApp {

    List<Pair<FXApp,Optional<Image>>> apps;
    Image defaultImage;
    int boxSize;
    int gapSize;
    int topLoc;
    int topSpot;
    int selected;
    int numRender;
    Consumer<FXApp> appResetter;

    public ApplicationChooser(GraphicsContext g, Consumer<FXApp> appResetter) {
        super(g);
        this.appResetter = appResetter;

        apps = Arrays.asList(
                new Pair(new BoxCarrier(g), Optional.empty()),
                new Pair(new examples.BouncingBall(g), Optional.empty()),
                new Pair(new examples.SineWave(g), Optional.empty()),
                new Pair(new examples.ScalingChecker(g), Optional.empty()),
                new Pair(new examples.AdditiveWave(g), Optional.empty()),
                new Pair(new examples.Array2D(g), Optional.empty())
        );
    }

    @Override
    public void settings() {
        size(1920, 1080);
    }

    public void setup() {
        boxSize = height / 6;
        gapSize = height / 30;
        selected = 0;
        defaultImage = new Image("boxcarrier.png", boxSize-2, boxSize-2, true, false);
        recalcGlobals();
    }

    public void draw() {
        background(214, 210, 196);

        noFill();
        int ycoord = topLoc;
        for (int i = topSpot; i < numRender && i < apps.size(); i++) {
            if (i == selected) {
                stroke(166, 25, 46);
            } else {
                stroke(55, 58, 54);
            }
            image(apps.get(i).getValue().orElse(defaultImage), gapSize+1, ycoord+1);
            rect(gapSize, ycoord, boxSize, boxSize);
            text(apps.get(i).getKey().name() + "\n\n" + apps.get(i).getKey().description(), gapSize * 2 + boxSize, ycoord);
            ycoord = ycoord + boxSize + gapSize;
        }
    }

    public void keyPressed(KeyEvent evt) {
        System.out.println(evt.getCode());
        if (evt.getCode() == KeyCode.UP) {
            selected = (selected - 1);
            if (selected < 0)
                selected = apps.size() - 1;
            recalcGlobals();
        } else if (evt.getCode() == KeyCode.DOWN) {
            selected = (selected + 1) % apps.size();
            recalcGlobals();
        } else if (evt.getCode() == KeyCode.ENTER){
            appResetter.accept(apps.get(selected).getKey());
        }
    }

    private void recalcGlobals() {
        switch (selected) {
            case 0:
                topSpot = 0;
                topLoc = gapSize;
                numRender = 6;
                break;
            default:
                topSpot = selected - 1;
                topLoc = -1 * boxSize / 3;
                numRender = 7;
                break;
        }
    }
}
