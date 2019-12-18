package mqapp;

import processing.core.*;

public class Scaler {
  MQApp parent;
  public Scaler(MQApp parent){
    this.parent = parent;
    parent.registerMethod("draw", this);
    parent.registerMethod("pre", this);
  }

  public void pre(){
    System.out.println("pre run");
    System.out.println(parent.width);
    System.out.println(parent.height);
  }

  public void draw(){
    System.out.println("draw run");
  }
}