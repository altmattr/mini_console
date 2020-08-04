package mqapp;

import processing.sound.*;
import processing.core.*; 
import java.io.*;

public class Main {

  static public void main(String[] passedArgs) {

  	ProcessBuilder pb = new ProcessBuilder().inheritIO();
  	String[] libs = {"./lib/core.jar"
  					,"./lib/sound.jar"
  					,"./lib/jsyn-20171016.jar"
            ,"./lib/controlP5.jar"
  	                };
  	String cp = "";
  	for (String s: libs){
  		cp = cp + (new File(s).getPath());
  		cp = cp + File.pathSeparator;
  	}
  	cp = cp + (new File("./src").getPath());

    Globals.setCurrApp("mqapp.ApplicationChooser");
    while(Globals.getCurrApp() != ""){
      pb.command("java", "-cp", cp, Globals.getCurrApp());
      Globals.setCurrApp(Globals.getNextApp());
      Globals.setNextApp("");
  
      System.out.println(pb.command());
      Process p = null;
      try {
  	    p = pb.start();
      	p.waitFor();
      } catch (Exception e){}
      p.destroy();
    }




  }
}