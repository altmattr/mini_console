package mqapp;

import javafx.util.Pair;
import processing.event.*;
import processing.core.*;
import studentwork.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ApplicationChooser extends mqapp.MQApp {

    List<Pair<MQApp,Optional<PImage>>> apps;
    PImage defaultImage;
    int boxSize;
    int gapSize;
    int topLoc;
    int topSpot;
    int selected;
    int numRender;
    int textGapSize;
    PFont retro;

    public ApplicationChooser() {
        super();
    }

    public void setup() {
        size(displayWidth, displayHeight);

        apps = Arrays.asList(
                new Pair(new Yeet(), Optional.of(loadImage("Yeet.png"))),
                new Pair(new BlackHole(), Optional.of(loadImage("BlackHole.png"))),
                new Pair(new FarmerBill(), Optional.of(loadImage("FarmerBill.png"))),
                new Pair(new KuruCountry(), Optional.of(loadImage("KuruCounrty.png"))),
                new Pair(new GameAndWatch(), Optional.of(loadImage("GameAndWatch.png"))),
                new Pair(new Grapher(), Optional.of(loadImage("Grapher.png")))
        );

        boxSize = height / 6;
        gapSize = height / 30;
        textGapSize = height/100;
        selected = 0;
        defaultImage = loadImage("boxcarrier.png");
        recalcGlobals();
    }

    public void draw() {

        background(0);

        noFill();
        int ycoord = topLoc;
        for (int i = topSpot; i < numRender && i < apps.size(); i++) {
            if (i == selected) {
                strokeWeight(5);
                stroke(255,0,255);
                //stroke(166, 25, 46);
            } else {
                strokeWeight(1);
                stroke(55, 58, 54);
            }
            image(apps.get(i).getValue().orElse(defaultImage), gapSize+1,ycoord+1, height/6, height/6);
            //image(img, gapSize+1, ycoord+1);
            rect(gapSize, ycoord, boxSize, boxSize);
            retro = createFont("DS-DIGI.TTF", 40);
            textFont(retro);
            fill(255,0,255);
            text(apps.get(i).getKey().name(), ((gapSize * 2) + boxSize), ycoord + gapSize);
            textSize(25);
            fill(255);
            text(apps.get(i).getKey().author(), ((gapSize * 2) + boxSize), ycoord + 2*gapSize);
            textSize(35);
            text(apps.get(i).getKey().description(), ((gapSize * 2) + boxSize), ycoord + 4*gapSize);

            fill(200, 0, 0);
            textSize(150);
            textAlign(LEFT);
            text("Macquarie", 3*width/6, height/6);
            text("Classic", 3*width/6, 3*height/6);
            text("Mini", 3*width/6, 5*height/6);

            noFill();

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
