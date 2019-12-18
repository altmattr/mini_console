package mqapp;

import javafx.util.Pair;
import processing.event.*;
import processing.core.*;
import studentwork.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ApplicationChooser extends mqapp.MQApp {

    List<Pair<MQApp,Optional<String>>> apps;
    String defaultImage;
    int boxSize;
    int gapSize;
    int topLoc;
    int topSpot;
    int selected;
    int numRender;

    public ApplicationChooser() {
        super();

        apps = Arrays.asList(
                new Pair(new Yeet(), Optional.empty()),
                new Pair(new BlackHole(), Optional.empty()),
                new Pair(new FarmerBill(), Optional.empty())
        );
    }

    public void setup() {
        size(displayWidth, displayHeight);
        boxSize = height / 6;
        gapSize = height / 30;
        selected = 0;
        defaultImage = "boxcarrier.png";
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
            //Image img = new Image(apps.get(i).getValue().orElse(defaultImage), boxSize-2, boxSize-2, true, false);
            //image(img, gapSize+1, ycoord+1);
            rect(gapSize, ycoord, boxSize, boxSize);
            //text(apps.get(i).getKey().name() + "\n\n" + apps.get(i).getKey().description(), gapSize * 2 + boxSize, ycoord);
            ycoord = ycoord + boxSize + gapSize;
        }
    }


    public void keyPressed() {

        if (keyCode == UP) {
            selected = (selected - 1);
                if (selected < 0)
                    selected = apps.size() - 1;
                recalcGlobals();
                }
        if (keyCode == DOWN) {
                selected = (selected + 1) % apps.size();
                recalcGlobals();
            }
        if (key == ENTER) {
            System.out.println("made it");
            loadApp(apps.get(selected).getKey());
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
