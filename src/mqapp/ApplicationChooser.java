package mqapp;

import processing.event.*;
import processing.core.*;
import studentwork.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ApplicationChooser extends mqapp.MQApp {

    public List<Pair<MQApp,Optional<PImage>>> apps;
    public PImage defaultImage;
    public int boxSize;
    public int gapSize;
    public int topLoc;
    public int topSpot;
    public int selected;
    public int numRender;
    public int textGapSize;
    public PFont uiFont, largeFont;

    public void setup() {
        size(displayWidth, displayHeight);

        apps = Arrays.asList(
                new Pair(new BoxCarrier(), Optional.of(loadImage("boxcarrier.png"))),
                new Pair(new GameAndWatch(), Optional.of(loadImage("GameAndWatch.png"))),
                new Pair(new GameOfLife(), Optional.of(loadImage("GameOfLife_485.png"))),
                new Pair(new Yeet(), Optional.of(loadImage("Yeet.png"))),
                new Pair(new BlackHole(), Optional.of(loadImage("BlackHole.png"))),
                new Pair(new FarmerBill(), Optional.of(loadImage("FarmerBill.png"))),
                new Pair(new KuruCountry(), Optional.of(loadImage("KuruCounrty.png"))),
                new Pair(new Grapher(), Optional.of(loadImage("Grapher.png"))),
                new Pair(new examples.AdditiveWave(), Optional.of(loadImage("AdditiveWave.png")))
        );

        boxSize = height / 6;
        gapSize = height / 30;
        textGapSize = height/100;
        selected = 0;
        defaultImage = loadImage("boxcarrier.png");
        uiFont = loadFont("Avenir-LightOblique-28.vlw");
        largeFont = loadFont("Avenir-LightOblique-78.vlw");
        recalcGlobals();
    }

    public void draw() {

        background(0);

        int ycoord = topLoc;
        for (int i = topSpot; i < topSpot + numRender && i < apps.size(); i++) {
            if (i == selected) {
                strokeWeight(5);
                stroke(255,0,255);
                //stroke(166, 25, 46);
            } else {
                strokeWeight(1);
                stroke(55, 58, 54);
            }
            image(apps.get(i).snd.orElse(defaultImage), gapSize+1,ycoord+1, height/6, height/6);
            noFill();
            rect(gapSize, ycoord, boxSize, boxSize);
            textFont(uiFont);
            fill(255,0,255);
            text(apps.get(i).fst.name(), ((gapSize * 2) + boxSize), ycoord + gapSize);
            fill(255);
            text("created by " + apps.get(i).fst.author(), ((gapSize * 2) + boxSize), ycoord + 2*gapSize);
            text(apps.get(i).fst.description(), ((gapSize * 2) + boxSize), ycoord + 4*gapSize);

            ycoord = ycoord + boxSize + gapSize;
        }
        fill(200, 0, 0);
        textAlign(LEFT);
        textFont(largeFont);
        text("Macquarie", 3*width/6, height/6);
        text("Classic", 3*width/6, 3*height/6);
        text("Mini", 3*width/6, 5*height/6);

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
            loadApp(apps.get(selected).fst);
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
