package pfx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class FXApp {

    public final int CORNER = 0;
    public final int CENTER = 1;

    public int width;
    public int height;

    public char key;

    public int rectMode = CORNER;

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
        g.setStroke(Color.rgb(grey, grey, grey));
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
    protected void ellipse(float x, float y, float width, float height){
        float topLeftX = x - width/2;
        float topLeftY = y - width/2;

        g.fillOval(topLeftX,topLeftY,width, height);
        g.strokeOval(topLeftX,topLeftY, width, height);
    }
    protected void line(float x, float y, float x2, float y2){g.strokeLine(x, y, x2, y2);}
    protected void rect(float x, float y, float width, float height){
        float topLeftX = x;
        float topLeftY = y;
        if (rectMode == CENTER){
            topLeftX = x - width/2;
            topLeftY = y - height/2;
        }
        g.fillRect(topLeftX,topLeftY,width,height);
        g.strokeRect(topLeftX,topLeftY,width,height);
    }

    // curves
    // TODO

    // 3d primitives
    // TODO

    // attributes
    // TODO
    protected void strokeWeight(int w){g.setLineWidth(w);}
    protected void noStroke(){g.setLineWidth(0);}

    // vertex
    // TODO

    // setting - colour
    protected void background(int grey){g.save();g.setFill(Color.rgb(grey, grey, grey));g.fillRect(0,0,width,height);g.restore();}
    protected void rectMode(int mode){rectMode = mode;}
    protected void background(int r, int gg, int b){g.save();g.setFill(Color.rgb(r,gg,b));g.fillRect(0,0,width,height);g.restore();}
    protected void fill(int grey){g.setFill(Color.rgb(grey, grey, grey));}
    protected void fill(int rr, int gg, int bb){g.setFill(Color.rgb(rr,gg,bb));}

    protected void size(int w, int h){width = w;height = h;}

    // maths
    public float sin(float in){return (float)Math.sin(in);}
    public float cos(float in){return (float)Math.cos(in);}
    public float random(float lower, float upper){return (float)Math.random()*(upper - lower) + lower;}
}
