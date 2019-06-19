package pfx;

public class SizedFXApp extends FXApp {
    protected int SIZE;

    public SizedFXApp(javafx.scene.canvas.GraphicsContext g){
        super(g);
        SIZE = 0;
    }

    public void setSize(int newSize){
        this.SIZE = newSize;
        settings();
        setup();
    }

}
