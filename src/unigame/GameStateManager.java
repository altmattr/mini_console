package unigame;

enum State {
  WORLD,BATTLE,TEXT,FADE
}

class GameStateManager {
  private State currentState = State.WORLD;
  boolean locked = false;

  
  public GameStateManager(){}
  
  public void nextState(){
    switch(currentState){
      case WORLD:
        Globals.world.process(); break;  
      case TEXT:
        Globals.textManager.process(); break;
      case BATTLE:
        Globals.battleManager.process(); break;
      case FADE:
        Globals.fadeManager.process(); break;
      default:
        break;
    }
  }
  
  //We need some way to set the state globally
  //In programs that need to be correct this isn't
  //a particularly good practice, but this is a game, 
  //so everything has to be a bit hacky
  public void setState(State s){
   currentState = s; 
  }
    
}
