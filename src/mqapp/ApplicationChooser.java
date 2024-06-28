package mqapp;

import processing.event.*;
import processing.core.*;
import processing.sound.*;

import studentwork.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ApplicationChooser extends mqapp.MQApp {

    class App{
      public String cls;
      public String name;
      public String author;
      public String description;
      public Optional<PImage> image;
      public App(String cls, String name, String author, String description, Optional<PImage> image){
        this.cls = cls;
        this.name = name;
        this.author = author;
        this.description = description;
        this.image = image;
      }
    }


    public List<App> apps;
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

    static public void main(String[] passedArgs) {
        runSketch(appletArgs, new ApplicationChooser());
    }


    public void setup() {
        size(displayWidth, displayHeight);

        apps = Arrays.asList(
                //new Pair(new MarbleLabrynth(), Optional.empty()), // TODO: waiting on the 3d fix to have this one work
                new App("studentwork.AstroSwarm",   "AstroSwarm", "By Cecilia Cannon", "Dodge asteroids, save the mini-galaxies", Optional.of(loadImage("application_chooser/astroswarm.png"))),
                new App("unigame.UniGame",          "UniGame", "By Elise McCabe and Joseph Hardman (not 1st year students :) )", "Gamify your uni-life", Optional.of(loadImage("application_chooser/unigame.png"))),
                new App("studentwork.Tetris",       "Tetris", "By Nataly Falero, Andrew, Alyssa Fedele, et.al.", "Just like you remember", Optional.of(loadImage("application_chooser/tetris.png"))),
                new App("studentwork.Rocket",       "Rocket", "Ben Talese", "Keep away from asteroids, swerve and boost!", Optional.of(loadImage("application_chooser/rocket.png"))),
                new App("studentwork.BlackHole",    "BlackHole", "Rifhad Mahbub", "Avoid black holes in your spaceship", Optional.of(loadImage("BlackHole.png"))),
                new App("studentwork.Snake",        "Snake", "Andrew Kefala", "No need for a Nokia", Optional.of(loadImage("application_chooser/snake.png"))),
                new App("studentwork.GameAndWatch", "Game and Watch", "Tanner Schineller", "A perfect clone", Optional.of(loadImage("GameAndWatch.png"))),
                new App("studentwork.KuruCountry",  "Kuru Country", "Chris Felix", "Find all the gems", Optional.of(loadImage("KuruCounrty.png"))),
                new App("studentwork.BoxCarrier",   "Box Carrier", "Elise McCabe", "A game of infinite haulage", Optional.of(loadImage("boxcarrier.png"))),
                new App("studentwork.Grapher",      "Grapher", "Sepehr Torfeh Nejad", "Make your own interactive graph", Optional.of(loadImage("Grapher.png"))),
                new App("studentwork.Stacker",      "Stacker", "Andrew Kefala", "Arcade Money Hog", Optional.of(loadImage("application_chooser/stacker.png"))),
                new App("studentwork.Pong",         "Pong", "Beau Williams", "need I say more?", Optional.of(loadImage("application_chooser/pongoptimised.png"))),
                new App("studentwork.Pandemic",     "Pandemic", "Elizabeth Cappellazzo, Cameron Baker, Quang Minh Pham, Robert Stockton, Nishal Najeeb, and Chhade Alasbar", "2020 Simulator", Optional.of(loadImage("application_chooser/Pandemic.png"))),
                new App("studentwork.Minigolf",     "Minigolf", "Mark Saba", "A game of golf... but mini", Optional.of(loadImage("application_chooser/minigolf.png"))),
                new App("studentwork.CrossyRoad",     "CrossyRoad", "Sienna Grove", "sharks instead of chickens", Optional.of(loadImage("application_chooser/minigolf.png")))
                

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

        music = new SoundFile(this, "application_chooser/b3.wav");
        music.amp(0.1f);  // just some quiet background music
        music.loop();

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
                         "by writing interactive programs.  We've",
                         "collected some of their work here to show you.",
                         "",
                         "for more information, contact",
                         "matthew.roberts@mq.edu.au"
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
            image(apps.get(i).image.orElse(defaultImage), gapSize+1,ycoord+1, height/6, height/6);
            noFill();
            rect(gapSize, ycoord, boxSize, boxSize);
            textFont(uiFont);
            fill(255);
            text(apps.get(i).name, ((gapSize * 2) + boxSize), ycoord + gapSize);
            text("created by " + apps.get(i).author, ((gapSize * 2) + boxSize), ycoord + 2*gapSize);
            text(apps.get(i).description, ((gapSize * 2) + boxSize), ycoord + 4*gapSize);

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
            Globals.setCurrApp(apps.get(selected).cls);
            Globals.setNextApp(this.getClass().getName());
            System.out.println(Globals.getCurrApp());
            System.out.println(Globals.getNextApp());

            exit();
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
