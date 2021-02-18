package unigame;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Map; 
import java.util.TreeMap; 
import java.util.EnumMap; 
import java.util.LinkedList; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class UniGame extends mqapp.MQApp {

    static public void main(String[] passedArgs) {
        runSketch(appletArgs, new UniGame());
    }


public void setup(){
  size((int)(240*Globals.scale), (int)(160*Globals.scale));
  frameRate(30);
  Globals.textDisplayFont = createFont(Globals.textFontPath,Globals.scale*8);
  Globals.battleUIFont = createFont(Globals.battleFontPath,Globals.scale*8);
  Globals.battleManager = new BattleManager(this);
  Globals.player = new Player("p_"+Globals.playerType, this);
  Globals.attacks = new Attacks();
  Globals.battleUnits = new BattleUnits(this);
  Globals.gameStateManager = new GameStateManager();
  Globals.keyPressManager = new KeyPressManager(this);
  Globals.textManager = new TextManager(this);
  Globals.fadeManager = new FadeManager(this);
  Globals.world = new OverworldManager(this);

// testing push 
  //When the game is done setting up, we start the opening cutscene
  cutsceneOnePartZero();
}


public void draw(){
  Globals.gameStateManager.nextState();
}

public void keyPressed(){
  Globals.keyPressManager.setKey(keyCode);
}

public void keyReleased(){
  Globals.keyPressManager.removeKey(keyCode); 
}

PGraphics lastScene;

//custom per-pixel upscaling. Processing's default upscaler is super ugly with pixel art
public PImage upscale(PImage img) {
  img.loadPixels();
  PImage outimg = createImage(img.width*Globals.scale,img.height*Globals.scale,ARGB);
  outimg.loadPixels();
  for(int i = 0; i < img.height; ++i){
    for(int j = 0; j < img.width; ++j){
      for(int k = 0; k < Globals.scale; ++k){
        for(int l = 0; l < Globals.scale; ++l){
          outimg.pixels[i*outimg.width*Globals.scale+j*Globals.scale+k*outimg.width+l] = img.pixels[i*img.width+j];
        }
      }
    }
  }
  outimg.updatePixels();
  return outimg;
}



public void cutsceneOnePartZero() {
  Globals.textManager.printText(
    new String[]{"Controls:                        P: Kills game   Z:Click "}, 
    new Lambda(){
      public void activate(){
        cutsceneOnePartOne();
}
}
);
}


public void cutsceneOnePartOne() {
  Globals.textManager.printText(
    new String[]{"TEACHER: Listen up everyone don't forget, assignment 1 is due by 5pm today. I'll be in this room until then, you can hand it in to me in person or online. It's a fairly small task but it is important, won't take long to do.  You can use the feedback from this task to help improve for the next one. Any questions?", 
    "KAREN: What!? This is due today!!! Oh no I haven't started yet..."}, 
    new Lambda(){
      public void activate(){
        cutsceneOnePartTwo();
}
}
);
}




public void cutsceneOnePartTwo() {
  //Karen faces up
  //Really dirty way of doing this but we can guarantee karen is there so it works
  Globals.world.pcLab.tiles[3][7].setAppearance(loadImage(Globals.assetspath+"Characters/Karen/karen_up.png"), this);
  Globals.world.drawOverworld();
  Globals.world.sleepWorld(15, new Lambda() {
    public void activate() {
      cutsceneOnePartThree();
    }
  }
  );
}

public void cutsceneOnePartThree() {
  Globals.textManager.printText(
    new String[]{
    "KAREN: Hm can I get an extension?", 
    "TEACHER: After that remark, no. It should not take long at all if you watched the lectures - it's very doable in the time remaining", 
    "KAREN: ...", 
    "lectures?", 
    "TEACHER: ..."
    }, 
    new Lambda(){
      public void activate(){
        cutsceneOnePartFour();
}
});
}

public void cutsceneOnePartFour() {
  Globals.world.pcLab.tiles[8][6].setAppearance(loadImage(Globals.assetspath+"Characters/Dev/dev_up.png"), this);
  Globals.world.drawOverworld();
  Globals.world.sleepWorld(15, new Lambda() {
    public void activate() {
      cutsceneOnePartFive();
    }
  }
  );
}

public void cutsceneOnePartFive() {
  Globals.textManager.printText(
    new String[]{
    "DEV: I don't think I can get all the marks but I'll do my best. This is a tough assessment...", 
    "TEACHER: That's the spirit. Class is over, I look forward to seeing your answers."
    }, 
    new Lambda(){
      public void activate(){
        cutsceneOnePartSix();
}
});
}

public void cutsceneOnePartSix() {
  Globals.world.currentRoom.tiles[8][6].setAppearance(loadImage(Globals.assetspath+"Characters/Dev/dev_left.png"), this);
  Globals.world.drawOverworld();
  Globals.world.sleepWorld(15, new Lambda() {
    public void activate() {
      cutsceneOnePartSeven();
    }
  }
  );
}

public void cutsceneOnePartSeven() {
  Globals.world.pcLab.tiles[3][7].setAppearance(loadImage(Globals.assetspath+"Characters/Karen/karen_left.png"), this);
  Globals.world.drawOverworld();
  Globals.world.sleepWorld(15, new Lambda() {
    public void activate() {
      cutsceneOnePartEight();
    }
  }
  );
}


public void cutsceneOnePartEight() {
  Globals.textManager.printText(new String[]{
    "(I haven't started the assignment either, I better go do that now.)"
    });
}








}
