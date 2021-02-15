package unigame;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

//These classes are the same, but they let the Tile constuctor differentiate between on-press behaviour and on-land behaviour.
abstract class LandBehaviour {
  public abstract void activate(Tile t, UniGame p3);
}

abstract class PressBehaviour {
  public abstract void activate(Tile t, UniGame p3);
}

class TurnToFace extends PressBehaviour {
  String path;
  Lambda l;
  public TurnToFace(String path) {
    this.path = path;
    l = new Lambda() {
      public void activate() {
      };
    };
  }

  public TurnToFace(String path, Lambda l) {
    this.path = path;
    this.l = l;
  }
  public void activate(Tile t, UniGame p3) {
    switch(Globals.world.playerDirection) {
    case UP:
      t.setAppearance(p3.loadImage(path+"_down.png"), p3);
      break;
    case DOWN:
      t.setAppearance(p3.loadImage(path+"_up.png"), p3);
      break;
    case LEFT:
      t.setAppearance(p3.loadImage(path+"_right.png"), p3);
      break;
    case RIGHT:
      t.setAppearance(p3.loadImage(path+"_left.png"), p3);
      break;
    }
    Globals.world.drawOverworld();
    l.activate();
  }
}

class Tile {
    public PImage appearance;
    public boolean isSolid;
    public boolean displayOver;
    public int offsetX;
    public int offsetY;
    public PressBehaviour pb;
    public LandBehaviour lb;
  
    //The standard tile, a visible object with no behaviour. May or may not be solid
    public Tile(PImage appearance, boolean displayOver, boolean isSolid, UniGame p3) {
      this.appearance = p3.upscale(appearance);
      this.offsetX = appearance.width-16;
      this.offsetY = appearance.height-16;
      this.isSolid = isSolid;
      this.displayOver = displayOver;
      this.pb = new PressBehaviour() {
        public void activate(Tile t, UniGame p3) {
        };
      };
      lb = new LandBehaviour() {
        public void activate(Tile t, UniGame p3) {
        };
      };
    }
  
    //An invisible solid tile
    public Tile() {
      this.appearance = new PImage(); 
      this.offsetX = -16;
      this.offsetY = -16;
      this.isSolid = true;
      this.pb = new PressBehaviour() {
        public void activate(Tile t, UniGame p3) {
        };
      };
      this.lb = new LandBehaviour() {
        public void activate(Tile t, UniGame p3) {
        };
      };
    }
  
  
    //A tile that does something when you land on it. Can not be solid
    public Tile(LandBehaviour lb) {
      this.appearance = new PImage(); 
      this.offsetX = appearance.width-16;
      this.offsetY = appearance.height-16;
      this.isSolid = false;
      this.pb = new PressBehaviour() {
        public void activate(Tile t, UniGame p3) {
        };
      };
      this.lb = lb;
    }
  
    //A tile that does something when you press it
    public Tile(PressBehaviour pb, boolean isSolid) {
      this.appearance = new PImage(); 
      this.offsetX = -16;
      this.offsetY = -16;
      this.isSolid = isSolid;
      this.lb = new LandBehaviour() {
        public void activate(Tile t, UniGame p3) {
        };
      };
      this.pb = pb;
    }
  
    //A visible tile with landing behaviour. Can not be solid
    public Tile(PImage appearance, boolean displayOver, LandBehaviour lb, UniGame p3) {
      this.appearance = p3.upscale(appearance);
      this.offsetX = appearance.width-16;
      this.offsetY = appearance.height-16;
      this.isSolid = false;
      this.displayOver = displayOver;
      this.pb = new PressBehaviour() {
        public void activate(Tile t, UniGame p3) {
        };
      };
      this.lb = lb;
    }
  
    //A visible tile with press behaviour. May or may not be solid
    public Tile(PImage appearance, boolean displayOver, PressBehaviour pb, boolean isSolid, UniGame p3) {
      this.appearance = p3.upscale(appearance);
      this.offsetX = appearance.width-16;
      this.offsetY = appearance.height-16;
      this.isSolid = isSolid;
      this.displayOver = displayOver;
      this.lb = new LandBehaviour() {
        public void activate(Tile t, UniGame p3) {
        };
      };
      this.pb = pb;
    }
  
    public void setAppearance(PImage appearance, UniGame p3){
      this.appearance = p3.upscale(appearance);
      this.offsetX = appearance.width-16;
      this.offsetY = appearance.height-16;
    }
  
    public void press(UniGame p3) {
      pb.activate(this, p3);
    }
  
    public void land(UniGame p3) {
      lb.activate(this, p3);
    }
  }
  