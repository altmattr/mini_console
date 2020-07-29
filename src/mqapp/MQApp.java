package mqapp;

import processing.event.*;
import processing.core.*;


public class MQApp extends PApplet {

  public final static String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "" };
  
  class Pair<A,B>{
    public A fst;
    public B snd;
    public Pair(A fst, B snd){
      this.fst = fst;
      this.snd = snd;
    }
  }

  public String name(){return "";}
  public String author(){return "";}
  public String description(){return "";}


  
  
  protected void handleKeyEvent(KeyEvent event){
    if(event.getAction() == KeyEvent.PRESS){
      if (event.getKey() == 'x'){
        loadApp(new ApplicationChooser());
      } 
      // ESC kills the application no matter what I do.
    }
    super.handleKeyEvent(event);
  }

  public void loadApp(MQApp newApp){
    //nextApp = newApp;
    exit();
  }


  public void circle(float x, float y, float rad){
    ellipse(x, y, rad, rad);
  }
}