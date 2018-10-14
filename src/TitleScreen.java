import javafx.scene.canvas.GraphicsContext;

public class TitleScreen extends FXApp {
    public TitleScreen(GraphicsContext g) {
        super(g);
    }

    public void settings(){
        size(800,800);
    }

    public void draw(){
        line(0,0, width, height);
    }
}
