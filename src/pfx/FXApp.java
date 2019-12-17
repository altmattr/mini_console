package pfx;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;//
import java.util.Random;
import java.awt.MouseInfo;

public class FXApp {

    public final int CORNER = 0;
    public final int CENTER = 1;

    public int mouseX=0;
    public int mouseY=0;

    public int width;
    public int height;

    public char key;

    public int rectMode = CORNER;
    public int ellipseMode = CENTER;
    public boolean filling = true;

    public final int UP = 10;
    public final int DOWN = 11;
    public final int RIGHT = 12;
    public final int LEFT = 13;
    public final int CTRL = 14;
    public final int SHIFT = 15;
    public final int ALT = 16;

    private boolean noStroke = false;

    Random rand = new Random();

    GraphicsContext g;
    public FXApp(GraphicsContext g){
        this.g = g;
        g.setStroke(Color.BLACK);
    }
    public void settings(){}
    public void setup(){}
    public void draw(){}
    public void updateGlobals(){
        mouseX = MouseInfo.getPointerInfo().getLocation().x;
        mouseY = MouseInfo.getPointerInfo().getLocation().y;
    }
    public void demoDraw(GraphicsContext g2){
        GraphicsContext oldG = g;
        g = g2;
        settings();
        setup();
        draw();
        g = oldG;
    }
    public String description(){return "";}
    public String name(){return "anon";}
    public void mousePressed(){}
    public void keyTyped(){}
    public void keyPressed(KeyEvent evt){}
    public void mouseDragged(){}
    public void mouseReleased(){}

    public void println(int input) {System.out.println(input);}

    protected void stroke(int grey){
	noStroke = false;
        g.setStroke(Color.rgb(grey, grey, grey));
    }
    protected void stroke(int r, int g, int b){
	noStroke = false;
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


    protected void triangle (float x1, float y1, float x2, float y2, float x3, float y3){
        double[] xPoints = {x1, x2, x3};
        double[] yPoints = {y1, y2, y3};
        if(filling){
            g.fillPolygon(xPoints, yPoints, 3);
        }
        if(!noStroke) {
            g.strokePolygon(xPoints, yPoints, 3);
        }
    }


    protected void ellipse(float x, float y, float width, float height){
        float topLeftX = x - width/2;
        float topLeftY = y - width/2;

        if (filling) {
           // rect(topLeftX, topLeftY, width, height);
           g.fillOval(topLeftX, topLeftY, width, height);
        }
	if (!noStroke) {
           g.strokeOval(topLeftX,topLeftY, width, height);
	}
    }
    protected void line(float x, float y, float x2, float y2){g.strokeLine(x, y, x2, y2);}
    protected void rect(float x, float y, float width, float height){
        float topLeftX = x;
        float topLeftY = y;
        if (rectMode == CENTER){
            topLeftX = x - width/2;
            topLeftY = y - height/2;
        }
        if(filling) {
            g.fillRect(topLeftX, topLeftY, width, height);
        }
        if(!noStroke) {
            g.strokeRect(topLeftX,topLeftY,width,height);
	}
    }

    protected void square(float x, float y, float size){
	    rect(x, y, size, size);
    }

    protected void point(float x, float y){
        ellipse(x, y, 1, 1);
    }

    // curves
    // TODO

    // 3d primitives
    // TODO

    // attributes
    // TODO
    protected void strokeWeight(int w){g.setLineWidth(w);}
    protected void noStroke(){g.setLineWidth(0); noStroke = true;}

    // vertex
    // TODO

    // setting - colour
    protected void background(int grey){g.save();g.setFill(Color.rgb(grey, grey, grey));g.fillRect(0,0,width,height);g.restore();}
    protected void rectMode(int mode){rectMode = mode;}
    protected void ellipseMode(int mode){ellipseMode = mode;}
    protected void textAlign(int mode)
    {
        if (mode == 1)
        {
            g.setTextAlign(TextAlignment.CENTER);
        }
    }

    protected int random(int upperLimit) { return rand.nextInt(upperLimit); }

    protected void background(int r, int gg, int b){g.save();g.setFill(Color.rgb(r,gg,b));g.fillRect(0,0,width,height);g.restore();}
    protected void fill(int grey){
        filling = true;
        g.setFill(Color.rgb(grey, grey, grey));
    }
    protected void fill(int rr, int gg, int bb){
        filling = true;
        g.setFill(Color.rgb(rr,gg,bb));
    }

    protected void fill(int grey, int alpha){
        filling = true;
        g.setFill(Color.rgb(grey, grey, grey, alpha/100f));
    }
    protected void fill(int rr, int gg, int bb, int alpha){
        filling = true;
        g.setFill(Color.rgb(rr,gg,bb,alpha));
    }
    protected void noFill(){
        filling = false;
    }

    protected void size(int w, int h){width = w;height = h;}

    protected void text(String txt, float x, float y){
        g.setTextBaseline(VPos.TOP);
        g.fillText(txt, x,y);
    }



    // maths
    public float sin(float in){return (float)Math.sin(in);}
    public float cos(float in){return (float)Math.cos(in);}
    public float random(float lower, float upper){return (float)Math.random()*(upper - lower) + lower;}
    public float dist(float x1, float y1, float x2, float y2){
        return (float)Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
    }

    public void image(javafx.scene.image.Image img, float x, float y){
        g.drawImage(img, x, y);
    }
}
