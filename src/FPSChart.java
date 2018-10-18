import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class FPSChart {
    int len = 300;
    long[] frameHistory = new long[len];
    long[] drawHistory = new long[len];
    int records = 0;
    public void logFrame(long frame, long drawing){
        System.out.println(drawing);
        frameHistory[records] = frame;
        drawHistory[records] = drawing;
        records = (records + 1) % len;
    }

    public void draw(GraphicsContext c, double x, double y){
        c.setStroke(Color.GRAY);
        c.strokeRect(x, y, len, 20);
        c.setLineDashes(1,3);
        c.strokeLine(x,y+4,x+len,y+4); // 60fps line

        // current spot line
        c.setLineDashes(0);
        c.strokeLine(x+records, y, x+records, y+20);

        c.setStroke(Color.RED);

        //TODO: getting rid of this empty path slows the application incredibly.
        //c.beginPath();c.closePath();

        // show performance line
        for(int i = 1; i < len; i++){
            c.strokeLine(x + (i-1), y + 20-drawHistory[i-1], x + i, y + 20 - drawHistory[i]);
        }
        //c.stroke();
    }

    public int getWidth(){return len;}
}
