import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FXApp {

    protected int width;
    protected int height;

    GraphicsContext g;
    public FXApp(GraphicsContext g){
        this.g = g;
        g.setStroke(Color.BLACK);
    }
    public void settings(){}
    public void setup(){}
    public void draw(){}
    public void mousePressed(){}
    public void keyPressed(){}
    public void mouseDragged(){}
    public void mouseReleased(){}

    protected void stroke(int grey){
        g.setStroke(Color.gray(grey));
    }
    protected void stroke(int r, int g, int b){
        this.g.setStroke(Color.rgb(r,g,b, 1.0));
    }

    // environment
    // TODO

    // conversion
    // TODO

    // string functions
    // TODO

    // array functions
    // TODO

    // shape
    // TODO

    // 2d primitives
    // TODO
    protected void line(float x, float y, float x2, float y2){g.strokeLine(x, y, x2, y2);}
    protected void rect(float x, float y, float width, float height){g.rect(x,y,width,height);}

    // curves
    // TODO

    // 3d primitives
    // TODO

    // attributes
    // TODO

    // vertex
    // TODO

    // mouse

    protected void size(int w, int h){width = w;height = h;}
}
