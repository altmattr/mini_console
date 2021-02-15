package unigame;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 


class TextManager {
  final String textboxPath = Globals.assetspath+"Other/textbox.png";
  final int textX = 14*Globals.scale;
  final int textY = 133*Globals.scale;
  PImage textbox;
  int currentCharacter = 0;
  int currentString = 0;
  int currentLine = 0;
  final int lineLength = 32;
  int currentOffset = 0;
  final int stringBuilderDefaultSize = lineLength*2+1;
  boolean breaking = false;
  UniGame p3;
  

  StringBuilder sb;
  String[] strings;
  PImage currentDisplay;
  
  Lambda afterText;

  public TextManager(UniGame p3) {
    this.p3 = p3;
    textbox = p3.upscale(p3.loadImage(textboxPath));
    p3.textFont(Globals.textDisplayFont);
    sb = new StringBuilder(stringBuilderDefaultSize);
  }

  public void process() {
    //If we're breaking, and the input is an A, end the break and clear the input.
    //If there is no next string, change the state back to the overworld.
    if (breaking) {
      if (Globals.keyPressManager.getKey() == Button.A) {
        Globals.keyPressManager.clearBuffer();
        if (currentString == strings.length-1 && currentCharacter == strings[currentString].length()) {
          afterText.activate();
          return;
        } else {
          //Clear the last string
          sb.setLength(0);

          //If we have exhausted all the characters in the current string
          //we must go to the next string
          if (currentCharacter >= strings[currentString].length()) {
            ++currentString;
            currentCharacter = 0;
          }
          breaking = false;
          currentLine = 0;
          currentOffset = 0;
        }
      }
    }else{
      //If we're not breaking, and they press A
      //Finish the current block, and clear the input
      if(Globals.keyPressManager.getKey() == Button.A){
        Globals.keyPressManager.clearBuffer();
        while(!breaking){
          nextChar(); 
        }
      }else{
        nextChar(); 
      }
    }
    PGraphics scene = Globals.world.getScene();
    scene.beginDraw();
    scene.image(textbox,0,0);
    scene.fill(0,0,0);
    scene.textFont(Globals.textDisplayFont);
    scene.text(sb.toString(),textX,textY);
    scene.endDraw();
    p3.image(scene,0,0);
  }

  private void nextChar() {
    //If the current string is complete, create a break between strings.
    if (currentCharacter >= strings[currentString].length()) {
      breaking = true;
      return;
    }
    //If the current character is a space, try to move on to the next word
    if (strings[currentString].charAt(currentCharacter) == ' ') {
      //count stores the length of the next word plus the space
      int count = 1;
      for (int i = currentCharacter+1; i < strings[currentString].length(); ++i) {
        if (strings[currentString].charAt(i) == ' ') break;
        ++count;
      }
      ++currentCharacter;
      //if the next word+count fits on to the current line, simply continue
      if (count+sb.length()-currentOffset <= lineLength) {
        sb.append(' ');
      } else {
        //it can not fit on to the line, so we must either go to the next line
        //or create a break
        if (currentLine == 1) {
          breaking = true;
        }else{
          sb.append("\n"); 
          currentLine = 1;
          currentOffset = sb.length();
        }
      }
    } else {
      sb.append(strings[currentString].charAt(currentCharacter++));
    }
  }


  public void printText(String[] strings) {
    this.strings = strings;
    this.currentCharacter = 0;
    this.currentString = 0;
    this.currentLine = 0;
    this.currentOffset = 0;
    this.breaking = false;
    this.sb.setLength(0);
    Globals.gameStateManager.setState(State.TEXT);
    Globals.keyPressManager.clearBuffer();
    afterText = new Lambda(){
      public void activate(){
        Globals.gameStateManager.setState(State.WORLD);
      } 
    };
  }
  
    public void printText(String[] strings, Lambda afterText) {
    this.strings = strings;
    this.currentCharacter = 0;
    this.currentString = 0;
    this.currentLine = 0;
    this.currentOffset = 0;
    this.breaking = false;
    this.sb.setLength(0);
    Globals.gameStateManager.setState(State.TEXT);
    Globals.keyPressManager.clearBuffer();
    this.afterText = afterText;
  }
}