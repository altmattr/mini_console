import processing.core.*;

public class Main {
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", ""};
    PApplet.runSketch(appletArgs, new studentwork.Yeet());
  }

}
