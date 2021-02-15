package unigame;

import java.util.Map; 
import java.util.TreeMap; 
import java.util.EnumMap; 
import java.util.LinkedList; 

enum Button {
    LEFT, RIGHT, UP, DOWN, A, B, START, NONE
  }
  
  
class KeyPressManager {
  
  Map<Integer, Button> keyToButton = new TreeMap<Integer, Button>();
  EnumMap<Button, Integer> keyToInt = new EnumMap(Button.class);
  LinkedList<Button> presses = new LinkedList<Button>();
  Button lastKey;
  int lastKeyFrameCount;
  boolean held = true;
  UniGame p3;
  
  //how many frames ahead we let players buffer inputs
  final int bufferLength = 1;
 
  //Assign keys in map. We need a reverse map, from button -> key value, so that we can easily remap buttons.
  public KeyPressManager(UniGame p3) {
      this.p3 = p3;
    keyToButton.put(65, Button.LEFT); 
    keyToButton.put(68, Button.RIGHT); 
    keyToButton.put(87, Button.UP); 
    keyToButton.put(83, Button.DOWN);
    keyToButton.put(90, Button.A);
    keyToButton.put(88, Button.B); 
    keyToButton.put(10, Button.START);
    
    for(Map.Entry<Integer,Button> e : keyToButton.entrySet()){
      keyToInt.put(e.getValue(),e.getKey());
    }
    
    //We save the last key pressed, and when it was pressed
    //This is so that a player can slightly buffer inputs instead of having to wait until inputs are available
    lastKey = Button.NONE;
    lastKeyFrameCount = 0;
  }  
  
  //returns the key which keypressmanager determines was most recently pressed (within the buffer limit)
  public Button getKey(){
    if(presses.size() > 0){
      return presses.getFirst(); 
    }
    if(p3.frameCount-lastKeyFrameCount <= bufferLength){
      return lastKey; 
    }
    return Button.NONE;
  }
  
  //sets the last key pressed to be newly pressed key
  //additionally adds the key to the list of currently held keys
  public void setKey(int kc){
    lastKey = keyToButton.getOrDefault(kc,Button.NONE);
    if(lastKey != Button.NONE){
      if(!presses.contains(lastKey)){
        presses.offerFirst(lastKey); 
      }
    }
    lastKeyFrameCount = p3.frameCount;
  }
  
  //removes the key from the list of currently held keys
  public void removeKey(int kc){
    presses.remove(keyToButton.getOrDefault(kc,Button.NONE));
  }
  
  public void clearBuffer(){
   presses.clear(); 
   lastKey = Button.NONE;
  }

  
}
