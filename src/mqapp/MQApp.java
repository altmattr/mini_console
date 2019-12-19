package mqapp;

import processing.event.*;
import processing.core.*;


public class MQApp extends PApplet {
  
  final static String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "" };
  
  protected void handleKeyEvent(KeyEvent event){
    if(event.getAction() == KeyEvent.PRESS){
      if (event.getKey() == ESC){
        dispose();
        runSketch(appletArgs, new ApplicationChooser());
      } else if (event.getKey() == 'x'){
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