public class PVector {
    float x;
    float y;

    public PVector(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void add(PVector other){
        this.x = this.x + other.x;
        this.y = this.y + other.y;
    }
}
