import java.awt.*;
import processing.core.*; 


public class MQApp extends PApplet {
  
  final static String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "studentwork.Yeet" };
  
  protected void handleKeyEvent(KeyEvent event){
    System.out.println("key event captured by mqapp");
    if(event.getAction() == KeyEvent.PRESS){
      if (key == ESC){
        System.out.println("and it is esc");
        dispose();
        runSketch(appletArgs, new ApplicationChooser());
      } else if (key == VK_F4){
	exit();
      }
    }
  }
}