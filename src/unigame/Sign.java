package unigame;

import processing.core.*; 

class Sign extends Tile{
  public Sign(final String[] text, UniGame p3){
    super(new PressBehaviour(){
      public void activate(Tile t, UniGame p3){
        Globals.textManager.printText(text); 
      }
    },true); 
  }
  
  public Sign(final String text, UniGame p3){
    super(new PressBehaviour(){
      public void activate(Tile t, UniGame p3){
        Globals.textManager.printText(new String[]{text}); 
      }
    },true);
  }
  
  public Sign(PImage appearance, final String text, UniGame p3){
    super(appearance,false,new PressBehaviour(){
      public void activate(Tile t, UniGame p3){
        Globals.textManager.printText(new String[]{text}); 
      }
    },true, p3);
  }
  
  public Sign(PImage appearance, final String[] text, UniGame p3){
    super(appearance,false,new PressBehaviour(){
      public void activate(Tile t, UniGame p3){
        Globals.textManager.printText(text); 
      }
    },true, p3);
  }
}
