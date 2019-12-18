
public class Chooser extends MQApp {
    int boxSize;
    int gapSize;
    int topLoc;
    int topSpot;
    int selected;
    int numRender;

   

    @Override
    public void settings() {
        size(1920, 1080);
    }

    public void setup() {
        boxSize = height / 6;
        gapSize = height / 30;
        selected = 0;
    }

    public void draw() {
        background(214, 210, 196);

        noFill();
        int ycoord = topLoc;
        for (int i = topSpot; i < numRender && i < 2; i++) {
            if (i == selected) {
                stroke(166, 25, 46);
            } else {
                stroke(55, 58, 54);
            }
            rect(gapSize, ycoord, boxSize, boxSize);

            ycoord = ycoord + boxSize + gapSize;
        }
    }

}