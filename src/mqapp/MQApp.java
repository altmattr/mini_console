package mqapp;

import processing.event.*;
import processing.core.*;


public class MQApp extends PApplet {

  public String name(){return "";}
  public String author(){return "";}
  public String description(){return "";}


  final static String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "" };
  
  protected void handleKeyEvent(KeyEvent event){
    if(event.getAction() == KeyEvent.PRESS){
      if (event.getKey() == 'x'){
        loadApp(new ApplicationChooser());
      } else if (event.getKey() == ESC){
	    exit();
      }
    }
    super.handleKeyEvent(event);
  }

  public void loadApp(MQApp newApp){
    dispose();
    runSketch(appletArgs, newApp);
  }
}