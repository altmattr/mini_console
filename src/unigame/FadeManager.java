package unigame;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

class FadeManager{
  int fadeTime;
  Lambda midFade;
  Lambda afterFade;
  boolean doneMid;
  UniGame p3;
  
  public FadeManager(UniGame p3){
      this.p3 = p3;
  }
  
  public void process(){
    fadeTime +=15;
    if(fadeTime <= 255){
      PGraphics overworldScene = Globals.world.getScene();
      p3.image(overworldScene,0,0);
      p3.fill(0,0,0,UniGame.min(fadeTime,255));
      p3.noStroke();
      p3.rect(0,0,p3.width,p3.height);
    }else if(fadeTime <= 510){
      if(!doneMid){
        doneMid = true;
        midFade.activate();
        Globals.world.drawOverworld();
      }
      PGraphics overworldScene = Globals.world.getScene();
      p3.image(overworldScene,0,0);
      p3.fill(0,0,0,UniGame.max(510-fadeTime,0));
      p3.noStroke();
      p3.rect(0,0,p3.width,p3.height);
    }else{
      afterFade.activate();
    }
  }
  
  public void fade(Lambda midFade){
    this.midFade = midFade;
    this.afterFade = new Lambda(){
      public void activate(){
        Globals.gameStateManager.setState(State.WORLD);   
      }
    };
    this.fadeTime = 0;
    Globals.gameStateManager.setState(State.FADE);
    doneMid = false;
  }
  
  public void fade(Lambda midFade, Lambda afterFade){
    this.midFade = midFade;
    this.afterFade = afterFade;
    this.fadeTime = 0;
    Globals.gameStateManager.setState(State.FADE);
    doneMid = false;
  }
  
}
