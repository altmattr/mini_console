package examples;

import javafx.scene.canvas.GraphicsContext;

public class ScalingChecker extends pfx.FXApp {
    public ScalingChecker(GraphicsContext g) {
        super(g);
    }

    public void settings(){
        size(960,555);
    }

    public void draw(){
        background(0);
        fill(100);
        stroke(255);
        rect(5,5,950,545);
    }
}
