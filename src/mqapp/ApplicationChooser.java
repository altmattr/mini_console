package mqapp;

import processing.event.*;
import processing.core.*;
import processing.sound.*;

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

    SoundFile music;

    float gap = 100;
    float period = 3000;
    float startAt = 500;
    int darkest = color(247, 123, 0);
    long pmillis;

    int[] colours = {color(247, 146, 2), color(247, 161, 2), color(247, 133, 5), darkest};


    public void setup() {
        size(displayWidth, displayHeight);

        apps = Arrays.asList(
                //new Pair(new MarbleLabrynth(), Optional.empty()), // TODO: waiting on the 3d fix to have this one work
                new Pair(new Tetris(), Optional.of(loadImage("application_chooser/tetris.png"))),
                new Pair(new Rocket(), Optional.of(loadImage("application_chooser/rocket.png"))),
                new Pair(new BlackHole(), Optional.of(loadImage("BlackHole.png"))),
                new Pair(new Snake(), Optional.of(loadImage("application_chooser/snake.png"))),
                new Pair(new GameAndWatch(), Optional.of(loadImage("GameAndWatch.png"))),
                new Pair(new KuruCountry(), Optional.of(loadImage("KuruCounrty.png"))),
                new Pair(new BoxCarrier(), Optional.of(loadImage("boxcarrier.png"))),
                new Pair(new Grapher(), Optional.of(loadImage("Grapher.png"))),
                new Pair(new Stacker(), Optional.of(loadImage("application_chooser/stacker.png"))),
                new Pair(new Pong(), Optional.of(loadImage("application_chooser/pongoptimised.png"))),
                new Pair(new Pandemic(), Optional.of(loadImage("application_chooser/Pandemic.png"))),
                new Pair(new Yeet(), Optional.of(loadImage("Yeet.png"))),
                new Pair(new FarmerBill(), Optional.of(loadImage("FarmerBill.png")))
        );

        boxSize = height / 6;
        gapSize = height / 30;
        textGapSize = height/100;
        selected = 0;
        topSpot = 0;
        defaultImage = loadImage("boxcarrier.png");
        uiFont = loadFont("shared/Avenir-LightOblique-28.vlw");
        largeFont = loadFont("shared/HiraMaruPro-W4-60.vlw");
        recalcGlobals();

        //music = new SoundFile(this, "application_chooser/b3.wav");
        //music.amp(0.3f);  // just some quiet background music
        //music.loop();

    }

    public void draw() {

        // eshop background
        background(darkest);
        noStroke();
        pmillis = millis() % 16000;
        for (int round = 0; round < 4; round++) {
          float leaderX = max(0, pmillis-(startAt+period*round));
          for (int i = 0; i < 5; i++) {
            float linear = (max(0, leaderX - i*gap)/width);
            double animated = (1 - pow(2, -2*linear))*1.5*width; // FIXME
            fill(colours[round]);
            if (round % 2 == 0) {
              rect(0, i*(height/5), (float)animated, height/5);
            } else {
              rect(width-(float)animated, i*(height/5), (float)animated, height/5);
            }
          }
        }

        // spiel
        fill(255);
        textSize(width/80);
        int xloc = 2*width/3;
        int yloc = height/20;
        int ygap = height/30;
        String[] txt = {"What is this?",
                         "In their first-year, computing students at",
                         "Macquarie Univeristy learn to program",
                         "by writing interacting programs.  We've",
                         "collected some ot their work here to show you."
                       };
        for(int y = 0; y< txt.length; y++){
            text(txt[y], xloc, yloc + ygap*y);
        }

        // branding
        fill(255);
        textAlign(LEFT);
        textFont(largeFont);
        text("Macquarie Classic Mini", 4*width/6 - 50, height-50);

        // apps
        int ycoord = topLoc;
        for (int i = topSpot; i < topSpot + numRender && i < apps.size(); i++) {
            if (i == selected) {
                strokeWeight(5);
                stroke(255);
                line(5, ycoord+boxSize/2, gapSize, ycoord+boxSize/2);
                //stroke(166, 25, 46);
            } else {
                strokeWeight(1);
                stroke(255);
            }
            image(apps.get(i).snd.orElse(defaultImage), gapSize+1,ycoord+1, height/6, height/6);
            noFill();
            rect(gapSize, ycoord, boxSize, boxSize);
            textFont(uiFont);
            fill(255);
            text(apps.get(i).fst.name(), ((gapSize * 2) + boxSize), ycoord + gapSize);
            text("created by " + apps.get(i).fst.author(), ((gapSize * 2) + boxSize), ycoord + 2*gapSize);
            text(apps.get(i).fst.description(), ((gapSize * 2) + boxSize), ycoord + 4*gapSize);

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
