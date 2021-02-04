import processing.core.*;

public class Main {
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "", ""};
    //String[] appletArgs = new String[] {""};
    PApplet.runSketch(appletArgs, new mqapp.ApplicationChooser());
  }

}
