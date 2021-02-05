package unigame;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Map; 
import java.util.TreeMap; 
import java.util.EnumMap; 
import java.util.LinkedList; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class UniGame extends mqapp.MQApp {

    static public void main(String[] passedArgs) {
        runSketch(appletArgs, new UniGame());
    }

//edit this to change the scale the game runs at. 1 is 260*160 and each increase causes pixels to become that size
//(i.e at scale 2 each pixel is rendered as 2x2, at scale 3 each pixel is rendered at 3x3, etc.)
final int scale = 7;

static final String assetspath = "unigame/";

GameStateManager gameStateManager;
TextManager textManager;
KeyPressManager keyPressManager;
OverworldManager world;
BattleManager battleManager;
BattleUnits battleUnits;
FadeManager fadeManager;
Player player;
Attacks attacks;


final String battleFontPath = assetspath+"Fonts/kongtext.ttf";
final String textFontPath = assetspath+"Fonts/dogicapixel.ttf";
final String playerType = "red";
PFont battleUIFont;
PFont textDisplayFont;


public void setup(){
  size((int)(240*scale), (int)(160*scale));
  frameRate(30);
  textDisplayFont = createFont(textFontPath,scale*8);
  battleUIFont = createFont(battleFontPath,scale*8);
  battleManager = new BattleManager();
  player = new Player("p_"+playerType);
  attacks = new Attacks();
  battleUnits = new BattleUnits();
  gameStateManager = new GameStateManager();
  keyPressManager = new KeyPressManager();
  world = new OverworldManager();
  textManager = new TextManager();
  fadeManager = new FadeManager();

 
  world = new OverworldManager();

  //When the game is done setting up, we start the opening cutscene
  cutsceneOnePartOne();
}


public void draw(){
  gameStateManager.nextState();
}

public void keyPressed(){
  keyPressManager.setKey(keyCode);
}

public void keyReleased(){
  keyPressManager.removeKey(keyCode); 
}

PGraphics lastScene;

//custom per-pixel upscaling. Processing's default upscaler is super ugly with pixel art
public PImage upscale(PImage img) {
  img.loadPixels();
  PImage outimg = createImage(img.width*scale,img.height*scale,ARGB);
  outimg.loadPixels();
  for(int i = 0; i < img.height; ++i){
    for(int j = 0; j < img.width; ++j){
      for(int k = 0; k < scale; ++k){
        for(int l = 0; l < scale; ++l){
          outimg.pixels[i*outimg.width*scale+j*scale+k*outimg.width+l] = img.pixels[i*img.width+j];
        }
      }
    }
  }
  outimg.updatePixels();
  return outimg;
}


abstract class Lambda{
  abstract public void activate();
}
class Attacks{
  //Attacks go: Name, Damage, Accuracy, Self-Damage/Healing, Effect (status and length), effectTargetSelf and alwaysHit
  Attack powerNap = new Attack("Power Nap",0,100,-50,new Effect(StatusEffect.RESTING,1),true,true);
  Attack coffeeShot = new Attack("Coffee Shot",15,100,0,new Effect(StatusEffect.CAFFEINATED,4),true,false);
  Attack selfStudy = new Attack("Self Study",0,100,0,new Effect(StatusEffect.FOCUSED,2),true,true);
  Attack persuade = new Attack("Persuade",25,80,0,new Effect(),false,false);
  
  //Ibis attacks
  Attack wingWave = new Attack("Wing Wave",10,100,0,new Effect(),false,false);
  Attack screechingSquawk = new Attack("Screeching Squawk",10,100,0,new Effect(),false,false);
  Attack rummage = new Attack("Rummage",0,70,-20,new Effect(),false,false);
  Attack snackSteal = new Attack("Snack Steal",20,80,-5,new Effect(),false,false);
  
  //Patron attacks
  Attack cheers = new Attack("Cheers",0,100,-10,new Effect(StatusEffect.DRUNK,5),true,true);
  Attack recruitmentAttempt = new Attack("MACS Recruitment Attempt",20,100,0,new Effect(),false,false);
  Attack smallTalk = new Attack("Small Talk",10,100,0,new Effect(StatusEffect.AWKWARD,1),false,false);
  Attack clumsyDance = new Attack("Clumsy Dance Move",25,80,10,new Effect(StatusEffect.RESTING,1),true,false);
  
  //HSC attacks
  Attack heavyBookThrow = new Attack("Heavy Book Throw",10,100,0,new Effect(),false,false);
  Attack portableCharger = new Attack("Portable Charger",0,100,-5,new Effect(StatusEffect.FOCUSED,1),true,true);
  Attack zoomerMeme = new Attack("Abstract Meme", 20,90,0,new Effect(),false,false);
  Attack gossip = new Attack("Gossip About "+player.name, 0, 100, 0, new Effect(StatusEffect.VULNERABLE,2),false,false);
  
  //Teacher attacks
  Attack rulesReminder = new Attack("Rules Reminder",10,100,0,new Effect(StatusEffect.AWKWARD,2),false,false);
  Attack hardHittingTruth = new Attack("Hard Hitting Truth",15,100,0,new Effect(),false,false);
  Attack armedAccusation = new Attack("Armed Accusation",30,90,0,new Effect(),false,false);
  Attack disappointedSigh = new Attack("Disappointed Sigh",10,100,-20,new Effect(),false,false);
  
  public Attacks(){};
}


class Attack {
  String name;
  Effect effect;
  boolean effectTargetSelf;
  boolean alwaysHit;
  int attack;
  int selfDamage;
  int accuracy;

  public Attack(String name, int attack, int accuracy, int selfDamage, Effect effect, boolean effectTargetSelf, boolean alwaysHit) {
    this.name = name;
    this.effect = effect;
    this.effectTargetSelf = effectTargetSelf;
    this.alwaysHit = alwaysHit;
    this.attack = attack;
    this.accuracy = accuracy;
    this.selfDamage = selfDamage;
  }
}


enum StatusEffect {
  DRUNK, CAFFEINATED, FOCUSED, GUARDING, RESTING, AWKWARD, VULNERABLE, NONE
}

class Effect {
  StatusEffect status;
  int turnsLeft;
  public Effect(StatusEffect status, int turnsLeft) {
    this.status = status;
    this.turnsLeft = turnsLeft;
  }
  public Effect() {
    this.status = StatusEffect.NONE;
    this.turnsLeft = 0;
  }
}



enum BattleState {
  INTRO, MENU, ATK, ATKDISPLAY, OVER
}


class BattleManager {
  BattleUnit playerUnit;
  BattleUnit enemy;
  BattleState currentState;
  boolean playerFirst;
  boolean firstAttackDone;
  Attack playerAttack;
  Attack enemyAttack;
  int selectedAttack = 0;
  int introFrame;
  int outroFrame;

  boolean battleFinished;
  
  Lambda afterBattle;

  private PGraphics scene = createGraphics(240*scale,160*scale);
  final String battlePath = "Tiles/Battle/";
  final PImage bg = upscale(loadImage(assetspath+battlePath+"Background.png"));




  EnumMap<StatusEffect, String> effectNames;
  public BattleManager() {
    effectNames = new EnumMap(StatusEffect.class);
    effectNames.put(StatusEffect.RESTING, "tired");
    effectNames.put(StatusEffect.DRUNK, "tipsy");
    effectNames.put(StatusEffect.CAFFEINATED, "caffeinated");
    effectNames.put(StatusEffect.FOCUSED, "focused");
    effectNames.put(StatusEffect.GUARDING, "guarding");
    effectNames.put(StatusEffect.AWKWARD, "awkward");
    effectNames.put(StatusEffect.VULNERABLE, "vulnerable");
    effectNames.put(StatusEffect.NONE, "Error: NONE does not have a name");
  }
  //Take in two BattleUnit classes
  //Start a battle
  public void battle(BattleUnit playerUnit, BattleUnit enemy) {
    this.playerUnit = playerUnit;
    this.enemy = enemy;
    playerUnit.currentHP = playerUnit.maxHP;
    enemy.currentHP = enemy.maxHP;
    playerUnit.dodge = playerUnit.baseDodge;
    enemy.dodge = enemy.baseDodge;
    playerUnit.speed = playerUnit.baseSpeed;
    enemy.speed = enemy.baseSpeed;
    playerUnit.defense = playerUnit.baseDefense;
    this.currentState = BattleState.INTRO;
    this.firstAttackDone = false;
    gameStateManager.setState(State.BATTLE);
    selectedAttack = 0;
    keyPressManager.clearBuffer();
    introFrame = 0; 
    outroFrame = 0;
    battleFinished = false;
    playerUnit.effects = new LinkedList<Effect>();
    enemy.effects = new LinkedList<Effect>();
    afterBattle = new Lambda(){
      public void activate(){
        gameStateManager.setState(State.WORLD); 
      }
    };
    
  }
  
  public void battle(BattleUnit playerUnit, BattleUnit enemy, Lambda afterBattle){
    battle(playerUnit,enemy);
    this.afterBattle = afterBattle;
  }

  public void process() {
    //display bg, chars, hpbars
    //
    switch(currentState) {
    case INTRO:
      processIntro(); 
      break;
    case MENU:
      display();
      processMenu(); 
      break;   
    case ATK:
      display();
      if (playerFirst^firstAttackDone) {
        processAttack(playerAttack, playerUnit, enemy);
      } else {
        processAttack(enemyAttack, enemy, playerUnit);
      }
      break;
    case ATKDISPLAY:
      display();
      displayAttack();
      break;
    case OVER:
      processOutro();
      break;
    }
  }

  private void display() {
    scene.beginDraw();
    scene.background(0);
    
    //background
    scene.image(bg, 0, 0);
    
    //health rectangles
    scene.fill(255, 0, 0);
    noStroke();
    
    //lots of magic numbers with draw element locations but hey it works
    //enemy hp bar
    int eHPSize = (int)(83.0f/enemy.maxHP*enemy.currentHP);
    scene.rect(22*scale, 36*scale, eHPSize*scale, 5*scale);


    //player hp bar
    int pHPSize = (int)(83.0f/playerUnit.maxHP*playerUnit.currentHP);
    scene.rect(150*scale, 101*scale, pHPSize*scale, 5*scale);

    scene.image(playerUnit.appearance, 40*scale, 57*scale);
    scene.image(enemy.appearance, 150*scale, 10*scale);
    scene.textFont(battleUIFont);
    scene.fill(0, 0, 0);
    scene.text(enemy.name, (63-(enemy.name.length()*4))*scale, 30*scale);
    scene.text(playerUnit.name, (191-(playerUnit.name.length()*4))*scale, 96*scale);
    scene.endDraw();
    image(scene, 0, 0);
  }

  private void introDisplay() {
    display();
    fill(0, 0, 0);
    text(playerUnit.attack0.name, 4*scale, 130*scale);
    text(playerUnit.attack1.name, 4*scale, 150*scale);
    text(playerUnit.attack2.name, 124*scale, 130*scale);
    text(playerUnit.attack3.name, 124*scale, 150*scale);
  }

  private void processIntro() {
    introFrame +=15;
    if (introFrame <= 255) {
      PGraphics overworldScene = world.getScene();
      image(overworldScene, 0, 0);
      fill(0, 0, 0, min(introFrame, 255));
      noStroke();
      rect(0, 0, width, height);
    } else if (introFrame <= 510) {
      introDisplay();
      fill(0, 0, 0, max(0, 510-introFrame));
      noStroke();
      rect(0, 0, width, height);
    } else {
      currentState = BattleState.MENU;
    }
  }
  
  private void processOutro(){
    outroFrame+=15;
    if(outroFrame <= 255){
      display();
      fill(0,0,0, min(outroFrame,255));
      noStroke();
      rect(0,0,width,height);
    }else if(outroFrame <= 510){
      PGraphics overworldScene = world.getScene();
      image(overworldScene, 0, 0);
      fill(0, 0, 0, max(0,510-outroFrame));
      noStroke();
      rect(0, 0, width, height);
    }else{
      afterBattle.activate(); 
    }
  }

  private void processMenu() {
    switch((int)random(4)) {
    case 0:
      enemyAttack = enemy.attack1; 
      break;
    case 1:
      enemyAttack = enemy.attack2; 
      break;
    case 2:
      enemyAttack = enemy.attack3; 
      break;
    case 3:
      enemyAttack = enemy.attack0; 
      break;
    }

    //read in the current key
    //if it's u/l/d/r, move that direction 
    //if it's A, do the currently highlighted move
    //How do we highlight moves?

    //Yes I know this is magic just trust it
    switch(keyPressManager.getKey()) {
    case UP:
    case DOWN:
      selectedAttack^=1;
      break;
    case LEFT:
    case RIGHT:
      selectedAttack^=2;
      break;
    case A:
      //Attack selected
      switch(selectedAttack) {
      case 0:
        playerAttack = playerUnit.attack0; 
        break;
      case 1:
        playerAttack = playerUnit.attack1; 
        break;
      case 2:
        playerAttack = playerUnit.attack2; 
        break;
      case 3:
        playerAttack = playerUnit.attack3; 
        break;
      default:
        //how did we get here??
        println("Attack selection out of bounds, how did you do this?");
        break;
      }
      currentState = BattleState.ATK;
      //If the speed is a tie, randomly select who attacks first
      playerFirst = playerUnit.speed+((int)random(2)) > enemy.speed;
      currentState = BattleState.ATK;
      firstAttackDone = false;
      keyPressManager.clearBuffer();
      return;
    default:
      //who cares
    }
    keyPressManager.clearBuffer();
    //Now, let's display the moves, with the currently selected one highlighted
    if (selectedAttack == 0) {
      fill(255, 255, 255);
    } else {
      fill(0, 0, 0);
    }
    text(playerUnit.attack0.name, 4*scale, 130*scale);
    if (selectedAttack == 1) {
      fill(255, 255, 255);
    } else {
      fill(0, 0, 0);
    }
    text(playerUnit.attack1.name, 4*scale, 150*scale);
    if (selectedAttack == 2) {
      fill(255, 255, 255);
    } else {
      fill(0, 0, 0);
    }
    text(playerUnit.attack2.name, 124*scale, 130*scale);

    if (selectedAttack == 3) {
      fill(255, 255, 255);
    } else {
      fill(0, 0, 0);
    }
    text(playerUnit.attack3.name, 124*scale, 150*scale);
  }


  BattleDisplay curDisplay;
  int displayPhase;


  private void processAttack(Attack atk, BattleUnit user, BattleUnit target) {
    BattleState nextAttackState = BattleState.ATK;
    if (firstAttackDone) {
      nextAttackState =  BattleState.MENU;
    } else {
      firstAttackDone = true;
    }
    LinkedList<Effect> toRemove = new LinkedList<Effect>();
    for (Effect e : user.effects) {
      e.turnsLeft = e.turnsLeft-1;
      if (e.turnsLeft == -1) {
        toRemove.add(e);
        switch(e.status) {
        case RESTING:
          user.canAttack = true;
          break;
        case DRUNK:
          user.powerMultiplier/=3;
          user.accuracyDivisor/=2;
          break;
        case CAFFEINATED:
          user.speed/=3;
          break;
        case GUARDING:
          user.defense = user.baseDefense;
          break;
        case FOCUSED:
          user.powerMultiplier/=2;
          break;
        case AWKWARD:
          user.powerDivisor/=2;
          break;
        case VULNERABLE:
          user.defense += 20;
          break;
        default:
          break;
        }
      }
    }
    user.effects.removeAll(toRemove);
    if (!user.canAttack) {
      displayPhase = 0;
      curDisplay = new BattleDisplay(toRemove, atk.name, false, false, true, (user==playerUnit), nextAttackState);
      currentState = BattleState.ATKDISPLAY;
      displayAttack();
      return;
    }
    if (user.canMiss && !atk.alwaysHit && random(100) > atk.accuracy/user.accuracyDivisor) {
      displayPhase = 0;
      curDisplay = new BattleDisplay(toRemove, atk.name, true, false, false, (user==playerUnit), nextAttackState);
      currentState = BattleState.ATKDISPLAY;
      displayAttack();
      return;
    }
    if (user.canMiss && !atk.alwaysHit && random(100) < target.dodge) {
      displayPhase = 0;
      curDisplay = new BattleDisplay(toRemove, atk.name, false, true, false, (user==playerUnit), nextAttackState);
      currentState = BattleState.ATKDISPLAY;
      displayAttack();
      return;
    }

    //Attack connects
    //First, calculate the damage

    //Self damage/healing (do not bother with defense)
    int selfDamage = atk.selfDamage;

    //Target damage/healing
    int targetDamage = atk.attack*user.powerMultiplier*(100-target.defense)/100/user.powerDivisor;

    //Then, do the effect
    BattleUnit effectTarget = target;
    if (atk.effectTargetSelf) {
      effectTarget = user;
    }
    boolean updated = false;
    for (Effect e : effectTarget.effects) {
      if (e.status == atk.effect.status) {
        e.turnsLeft = atk.effect.turnsLeft;
        updated = true;
        break;
      }
    }
    if (!updated) {
      effectTarget.effects.addLast(new Effect(atk.effect.status,atk.effect.turnsLeft));
      switch(atk.effect.status) {
      case RESTING:
        effectTarget.canAttack = false;
        break;
      case DRUNK:
        effectTarget.powerMultiplier*=3;
        effectTarget.accuracyDivisor*=2;
        break;
      case CAFFEINATED:
        effectTarget.speed*=3;
        break;
      case GUARDING:
        effectTarget.defense = 80+(effectTarget.baseDefense/5);
        break;
      case FOCUSED:
        effectTarget.powerMultiplier*=2;
        break;
      case AWKWARD:
        effectTarget.powerDivisor*=2;
        break;
      case VULNERABLE:
        effectTarget.defense+=20;
        break;
      default: 
        effectTarget.effects.removeLast();
        break;
      }
    }
    displayPhase = 0;
    curDisplay = new BattleDisplay(toRemove, atk.effect.status, atk.name, atk.effectTargetSelf, selfDamage, targetDamage, (user==playerUnit), nextAttackState);
    currentState = BattleState.ATKDISPLAY;
    displayAttack();
  }


  //Display takes in StatusOff, StatusOn, StatusTarget, SelfDamage, TargetDamage, isPlayerAttack


  //You are no longer X
  //You use *!
  //It heals you for X
  //It hits you for X damage
  //It heals Y for X
  //It hits Y for X damage
  //You become E.
  //Target becomes E.
  //You miss!
  //Y dodges!

  BattleTextDisplay battleTextDisplay;

  private void displayAttack() {
    if (curDisplay.breaking) {
      if (battleTextDisplay.breaking) {
        if (keyPressManager.getKey() == Button.A) {
          keyPressManager.clearBuffer();
          curDisplay.breaking = false;
        }
        textFont(textDisplayFont);
        fill(0, 0, 0);
        text(battleTextDisplay.sb.toString(), 4*scale, 130*scale);
        return;
      } else {
        battleTextDisplay.process();
        textFont(textDisplayFont);
        fill(0, 0, 0);
        text(battleTextDisplay.sb.toString(), 4*scale, 130*scale);
        return;
      }
    }
    if (playerUnit.currentHP == 0 || enemy.currentHP == 0) {
      if(!battleFinished){
       finishBattle(); 
      }else{
        currentState = BattleState.OVER; 
      }
      return;
    }
    String s = "";
    if (curDisplay.isPlayerAttack) {
      switch(curDisplay.displayState) {
      case 0:
        if (curDisplay.statusOff.size() > 0) {
          s+="You are no longer "+effectNames.get(curDisplay.statusOff.pop().status)+".";
          break;
        } else {
          ++curDisplay.displayState;
        }
      case 1:
        ++curDisplay.displayState;
        s+="You use "+curDisplay.attackName+".";
        break;
      case 2:
        if (curDisplay.cantAttack) {
          s+="You can't attack!";
          curDisplay.displayState = 5;
          break;
        } else if (curDisplay.missed) {
          s+="You miss!";
          curDisplay.displayState = 5;
          break;
        } else if (curDisplay.dodged) {
          s+=enemy.name+" dodges!";
          curDisplay.displayState = 5;
          break;
        }

        ++curDisplay.displayState;
        if (curDisplay.selfDamage < 0) {
          s+="You heal "+(-1*curDisplay.selfDamage)+" stamina."; 
          playerUnit.currentHP = min(playerUnit.currentHP-curDisplay.selfDamage, playerUnit.maxHP);
          break;
        } else if (curDisplay.selfDamage > 0) {
          s+="You deal "+curDisplay.selfDamage+" damage to yourself."; 
          playerUnit.currentHP = max(playerUnit.currentHP-curDisplay.selfDamage, 0);
          break;
        }
      case 3:
        ++curDisplay.displayState;
        if (curDisplay.targetDamage != 0) {
          s+="You hit "+enemy.name+" for "+curDisplay.targetDamage+" damage!";
          enemy.currentHP = max(enemy.currentHP-curDisplay.targetDamage, 0); 
          break;
        }
      case 4:
        ++curDisplay.displayState;
        //Status effect application. If none, go to 5.
        if (curDisplay.statusOn != StatusEffect.NONE) {
          if (curDisplay.statusTargetSelf) {
            s+="You become "+effectNames.get(curDisplay.statusOn)+".";
          } else {
            s+=enemy.name+" is "+effectNames.get(curDisplay.statusOn)+".";
          }
          break;
        }
      case 5:
        currentState = curDisplay.nextState;
        return;
      }
    } else {
      switch(curDisplay.displayState) {
      case 0:
        if (curDisplay.statusOff.size() > 0) {
          s+=enemy.name+" is no longer "+effectNames.get(curDisplay.statusOff.pop().status)+".";
          break;
        } else {
          ++curDisplay.displayState;
        }
      case 1:
        ++curDisplay.displayState;
        s+=enemy.name+" uses "+curDisplay.attackName+".";
        break;
      case 2:
        if (curDisplay.cantAttack) {
          s+=enemy.name+" can't attack!";
          curDisplay.displayState = 5;
          break;
        } else if (curDisplay.missed) {
          s+=enemy.name+" misses!";
          curDisplay.displayState = 5;
          break;
        } else if (curDisplay.dodged) {
          s+="You swiftly dodge!";
          curDisplay.displayState = 5;
          break;
        }
        ++curDisplay.displayState;
        if (curDisplay.selfDamage < 0) {
          s+=enemy.name+" heals "+(-1*curDisplay.selfDamage)+" stamina."; 
          enemy.currentHP = min(enemy.currentHP-curDisplay.selfDamage, enemy.maxHP);
          break;
        } else if (curDisplay.selfDamage > 0) {
          s+=enemy.name+" deals "+curDisplay.selfDamage+" damage to themself."; 
          enemy.currentHP = max(enemy.currentHP-curDisplay.selfDamage, 0);
          break;
        }
      case 3:
        ++curDisplay.displayState;
        if (curDisplay.targetDamage != 0) {
          s+=enemy.name+" hits you for "+curDisplay.targetDamage+" damage!";
          playerUnit.currentHP = max(playerUnit.currentHP-curDisplay.targetDamage, 0); 
          break;
        }
      case 4:
        ++curDisplay.displayState;
        //Status effect application. If none, go to 5.
        if (curDisplay.statusOn != StatusEffect.NONE) {
          if (curDisplay.statusTargetSelf) {
            s+=enemy.name+" is "+effectNames.get(curDisplay.statusOn)+".";
          } else {
            s+="You become "+effectNames.get(curDisplay.statusOn)+".";
          }
          break;
        }
      case 5:
        currentState = curDisplay.nextState;
        return;
      }
    }

    battleTextDisplay = new BattleTextDisplay(s);
    curDisplay.breaking= true;
    return;
  }

  public void finishBattle() {
    battleFinished = true;
    if(playerUnit.currentHP == 0){
      battleTextDisplay = new BattleTextDisplay("You run out of stamina! "+enemy.name+" wins the battle!");
    }else{
      battleTextDisplay = new BattleTextDisplay(enemy.name+" is out of stamina! You win!"); 
    }
    curDisplay.breaking = true;
    return;
  };
  //For first attack, check if it hits.
  //If it does, check if the target dodges.
  //If the target does not dodge, it connects.
  //Calculate damage and apply effects.
  //If anyone's HP < 0, other character wins
  //Then, perform the second attack, checking if possible. This section is where buffs/debuffs tick down.
  //Check if it hits.
  //If it does, check if the target dodges.
  //If the target does not dodge, it connects.
  //Calculate damage and apply effects.
  //If anyone's HP < 0, other character wins
}

class BattleDisplay {
  LinkedList<Effect> statusOff; 
  StatusEffect statusOn;
  String attackName;
  boolean statusTargetSelf;
  int selfDamage;
  int targetDamage;
  boolean isPlayerAttack;
  boolean missed;
  boolean dodged;
  boolean cantAttack;
  boolean breaking;
  int displayState;
  BattleState nextState;
  String curText;

  public BattleDisplay(LinkedList<Effect> statusOff, StatusEffect statusOn, String attackName, boolean statusTargetSelf, int selfDamage, int targetDamage, boolean isPlayerAttack, BattleState nextState) {
    this.statusOff = statusOff;
    this.statusOn = statusOn;
    this.statusTargetSelf = statusTargetSelf;
    this.selfDamage = selfDamage;
    this.targetDamage = targetDamage;
    this.isPlayerAttack = isPlayerAttack;
    this.nextState = nextState;
    this.attackName = attackName;
    missed = false;
    dodged = false;
    breaking = false;
    cantAttack = false;
    displayState = 0;
  }
  public BattleDisplay(LinkedList<Effect> statusOff, String attackName, boolean missed, boolean dodged, boolean cantAttack, boolean isPlayerAttack, BattleState nextState) {
    this.statusOff = statusOff;
    this.missed = missed;
    this.dodged = dodged;
    this.cantAttack = cantAttack;
    this.isPlayerAttack = isPlayerAttack;
    this.attackName = attackName;
    this.nextState = nextState;
    statusOn = StatusEffect.NONE;
    breaking = false;
    displayState = 0;
  }
}

class BattleTextDisplay {
  String s;
  int currentCharacter = 0;
  int currentLine = 0;
  final int lineLength = 30;
  int currentOffset = 0;
  final int stringBuilderDefaultSize = lineLength*2+1;
  boolean breaking = false;
  StringBuilder sb;

  public BattleTextDisplay(String s) {
    this.s = s; 
    fill(0, 0, 0);
    sb = new StringBuilder(stringBuilderDefaultSize);
  }


  public void process() {
    //if we're breaking, we're no longer breaking
    //and we need to clear sb
    if (keyPressManager.getKey() == Button.A) {
      while (currentCharacter != s.length()) nextChar();
      keyPressManager.clearBuffer();
    } else {
      nextChar();
    }
    if (currentCharacter == s.length()) {
      breaking = true;
    }
  }
  public void nextChar() {
    if (s.charAt(currentCharacter) == ' ') {
      int count = 0;
      //count the number of letters in the next word
      for (int i = currentCharacter+1; i<s.length() && s.charAt(i) != ' '; ++i) {
        ++count;
      }
      if (count+currentOffset+1 <= lineLength) {
        sb.append(' ');
        ++currentCharacter;
        ++currentOffset;
      } else {
        sb.append('\n');
        ++currentCharacter;
        currentOffset = 0;
      }
    } else {
      sb.append(s.charAt(currentCharacter));
      ++currentCharacter;
      ++currentOffset;
    }
  }
}


//Appearance is image
//MAXHP determines max HP
//CurrentHP determines current HP
//Speed determines who goes first, with some minor RNG involvement
//Attack determines damage
//Defense determines how much is blocked
//To calculate damage, multiply attack by (100-defense) and divide by 100
//Basically, 30 defense will block 30% of damage and 99 defense will block 99% of damage
//Accuracy determines hit percentage chance
//Dodge determines dodge percentage chance
//Both are /100. If a move has 100 accuracy, it gets through the hit phase 100% of the time
//If a character has 15 dodge, they can dodge the move that got through the hit phase 15% of the time

//Statuses: 
//Tired -> dodge goes to 0%
//Resting -> Can't attack until out of shock, max 3 turns
//Drunk -> Attack triples, accuracy is halved
//Caffinated -> Speed triples
//Guarding -> Defense changed to 80+(previousDefense/5)
//Focused -> All your attacks do double damage
//we can think of others I guess
class BattleUnits{
  BattleUnit playerBattleUnit = new BattleUnit(player.name, player.battleImage, attacks.powerNap, attacks.coffeeShot,
                                      attacks.selfStudy, attacks.persuade, 100, 100, 0, 5);
                                      
  BattleUnit hungryIbis = new BattleUnit("Hungry Ibis", loadImage(assetspath+"Characters/Battle/ibis_battle.png"), 
    attacks.wingWave, attacks.screechingSquawk, attacks.rummage, attacks.snackSteal, 
    100, 100, 0, 0);

  BattleUnit patron = new BattleUnit("UBar Patron", loadImage(assetspath+"Characters/Battle/ubarpatron_battle.png"),
    attacks.cheers, attacks.recruitmentAttempt, attacks.smallTalk, attacks.clumsyDance, 120, 100, 0, 10);
    
  BattleUnit schoolKids = new BattleUnit("HSC Student", loadImage(assetspath+"Characters/Battle/schoolkids_battle.png"),
    attacks.heavyBookThrow, attacks.portableCharger, attacks.zoomerMeme, attacks.gossip, 150,100,0,10);
    
  BattleUnit teacher = new BattleUnit("Teacher", loadImage(assetspath+"Characters/Battle/teacher_battle.png"),
    attacks.rulesReminder, attacks.hardHittingTruth, attacks.armedAccusation, attacks.disappointedSigh, 200,100,0,20);
}


class BattleUnit {
  public Attack attack0;
  public Attack attack1;
  public Attack attack2;
  public Attack attack3;

  public int maxHP;
  public int currentHP;
  public int speed;
  public int defense;
  public int dodge;

  public int baseSpeed;
  public int baseDefense; 
  public int baseDodge;
  public int powerMultiplier = 1;
  public int powerDivisor = 1;
  public int accuracyDivisor = 1;

  public boolean canAttack = true;
  public boolean canMiss = true;

  LinkedList<Effect> effects;

  PImage appearance;
  String name;
  public BattleUnit(String name, PImage appearance, Attack attack0, Attack attack1, Attack attack2, Attack attack3, 
    int maxHP, int speed, int defense, int dodge) {
    this.name = name;
    this.appearance = upscale(appearance);
    this.attack0 = attack0;
    this.attack1 = attack1;
    this.attack2 = attack2;
    this.attack3 = attack3;
    this.maxHP = maxHP;
    this.currentHP = maxHP;
    this.speed = speed;
    this.baseSpeed = speed;
    this.defense = defense;
    this.baseDefense = defense;
    this.dodge = dodge;
    this.baseDodge = dodge;
    effects = new LinkedList<Effect>();
  }
}
public void cutsceneOnePartOne() {
  textManager.printText(
    new String[]{"TEACHER: Ok everyone don’t forget, assignment 1 is due by 5pm today. I’ll be in this room until then, you can hand it in to me in person or online. It’s a fairly small task but it is important, won’t take long to do.  You can use the feedback from this task to help improve for the next one. Any questions?", 
    "KAREN: What!? This is due today!!! Oh no I haven’t started yet…"}, 
    new Lambda(){
      public void activate(){
        cutsceneOnePartTwo();
}
}
);
}

public void cutsceneOnePartTwo() {
  //Karen faces up
  //Really dirty way of doing this but we can guarantee karen is there so it works
  world.pcLab.tiles[3][7].setAppearance(loadImage(assetspath+"Characters/Karen/karen_up.png"));
  world.drawOverworld();
  world.sleepWorld(15, new Lambda() {
    public void activate() {
      cutsceneOnePartThree();
    }
  }
  );
}

public void cutsceneOnePartThree() {
  textManager.printText(
    new String[]{
    "KAREN: Hm can I get an extension?", 
    "TEACHER: After that remark, no. It should not take long at all if you watched the lectures - it’s very doable in the time remaining", 
    "KAREN: …", 
    "lectures?", 
    "TEACHER: …"
    }, 
    new Lambda(){
      public void activate(){
        cutsceneOnePartFour();
}
});
}

public void cutsceneOnePartFour() {
  world.pcLab.tiles[8][6].setAppearance(loadImage(assetspath+"Characters/Dev/dev_up.png"));
  world.drawOverworld();
  world.sleepWorld(15, new Lambda() {
    public void activate() {
      cutsceneOnePartFive();
    }
  }
  );
}

public void cutsceneOnePartFive() {
  textManager.printText(
    new String[]{
    "DEV: I don’t think I can get all the marks but I’ll do my best. This is a tough assessment…", 
    "TEACHER: That’s the spirit. Class is over, I look forward to seeing your answers."
    }, 
    new Lambda(){
      public void activate(){
        cutsceneOnePartSix();
}
});
}

public void cutsceneOnePartSix() {
  world.currentRoom.tiles[8][6].setAppearance(loadImage(assetspath+"Characters/Dev/dev_left.png"));
  world.drawOverworld();
  world.sleepWorld(15, new Lambda() {
    public void activate() {
      cutsceneOnePartSeven();
    }
  }
  );
}

public void cutsceneOnePartSeven() {
  world.pcLab.tiles[3][7].setAppearance(loadImage(assetspath+"Characters/Karen/karen_left.png"));
  world.drawOverworld();
  world.sleepWorld(15, new Lambda() {
    public void activate() {
      cutsceneOnePartEight();
    }
  }
  );
}

public void cutsceneOnePartEight() {
  textManager.printText(new String[]{
    "(I haven’t started the assignment either, I better go do that now.)"
    });
}


public void cutsceneTwoPartOne() {
  fadeManager.fade(new Lambda() {
    public void activate() {
      world.pcLab.tiles[3][7] = null;
      world.pcLab.tiles[10][4] = new Tile(loadImage(assetspath+"Characters/Karen/karen_right.png"), false, false);
      world.playerDirection = Direction.LEFT;
    }
  }
  , 
    new Lambda() {
    public void activate() {
      cutsceneTwoPartTwo();
    }
  }
  );
}

public void cutsceneTwoPartTwo() {
  textManager.printText(
    new String[]{
    "KAREN: Hey "+player.name+"! You haven't started either - perfect! Let’s go get some lunch while we figure out how to do this assignment. With the two of us working together we’ll get it done in no time at all! Plus, I haven’t seen any of the lectures yet, I’m stumped. I’ll meet you at Ubar!"
    }, 
    new Lambda(){
      public void activate(){
        cutsceneTwoPartThree();
}
});
}

public void cutsceneTwoPartThree() {
  fadeManager.fade(new Lambda() {
    public void activate() {
      world.pcLab.tiles[10][4] = null;
    }
  }
  , 
    new Lambda() {
    public void activate() {
      cutsceneTwoPartFour();
    }
  }
  );
}

public void cutsceneTwoPartFour() {
  textManager.printText(
    new String[]{
    "(She dashed off before I had the chance to answer, typical Karen…)", 
    "(I am hungry though so I should head that way anyway)"
    }, 
    new Lambda(){
      public void activate(){
        world.pcLab.tiles[11][4] = null;
  gameStateManager.setState(State.WORLD);
}
});
}


public void cutsceneIbisBattleOne() {
  textManager.printText(
    new String[]{
    "IBIS: squawwwkkkk", 
    "(He looks very hungry)"}, 
    new Lambda(){
      public void activate(){
        cutsceneIbisBattleTwo();
}
});
}

public void cutsceneIbisBattleTwo() {
  battleManager.battle(battleUnits.playerBattleUnit, battleUnits.hungryIbis, 
    new Lambda() {
    public void activate() {
      cutsceneIbisBattleThree();
    }
  }
  );
}

public void cutsceneIbisBattleThree() {
  //if player lost
  if (battleManager.playerUnit.currentHP == 0) {
    textManager.printText(
      new String[]{
      "IBIS: squawwwkkkk squawwwkkkk", 
      "(It seems happy with itself..)"}, 
      new Lambda(){
        public void activate(){
          cutsceneIbisBattleFour();
  }
}
);
}         else {
  textManager.printText(
    new String[]{
    "IBIS: squawwkkk", 
    "(It seems dejected..)"}, 
    new Lambda(){
      public void activate(){
        cutsceneIbisBattleFour();
}
});
}
}

public void cutsceneIbisBattleFour() {
  fadeManager.fade(new Lambda() {
    public void activate() {
      world.outside.tiles[12][8] = null;
      world.outside.tiles[13][7] = null;
      world.outside.tiles[13][8] = null;
      world.outside.tiles[13][9] = null;
      world.outside.tiles[14][8] = null;
      world.outside.tiles[14][9] = null;
    }
  }
  , 
    new Lambda() {
    public void activate() {
      cutsceneIbisBattleFive();
    }
  }
  );
}


public void cutsceneIbisBattleFive() {
  textManager.printText(new String[]{
    "(The ibis ran off)" 
    });
}


public void cutsceneThreePartOne() {
  fadeManager.fade(new Lambda() {
    public void activate() {
      world.ubar.tiles[world.playerX+1][world.playerY] = new Tile(loadImage(assetspath+"Characters/Karen/karen_left.png"), true, true);
    }
  }
  , 
    new Lambda() {
    public void activate() {
      cutsceneThreePartTwo();
    }
  }
  );
}

public void cutsceneThreePartTwo() {
  textManager.printText(new String[]{
    "KAREN: What took you so long? I'm so hungry. Let's go find a table." 
    }, new Lambda(){
      public void activate(){
        cutsceneThreePartThree();
}
}
);
}

public void cutsceneThreePartThree() {  
  fadeManager.fade(new Lambda() {
    public void activate() {
      world.ubar.tiles[1][8] = null;
      world.ubar.tiles[1][9] = null;
      world.ubar.tiles[world.playerX+1][world.playerY] = null;

      world.ubar.tiles[13][10] = new Tile(loadImage(assetspath+"Characters/Karen/karen_left.png"), false, 
        new TurnToFace(
        assetspath+"Characters/Karen/karen", 
        new Lambda() {
        public void activate() {
          textManager.printText(new String[]{
            "KAREN: Go get us some pizza, you can order at the bar"
            });
        };
      }
      ), true);

      world.playerX = 12;
      world.playerY = 10;
      world.playerDirection = Direction.RIGHT;
      world.adjustOffsets();
    }
  }
  , new Lambda() {
    public void activate() {
      textManager.printText(new String[]{
        "KAREN: Looks like theres some kind of society meetup, but the sooner we get food, the sooner we can get on to doing my assignment- uh I mean working together. I’ll mind the seats, go get us some pizza."
        });
    }
  }
  );
}

public void cutscenePatronBattleOne() {
  textManager.printText(
    new String[]{
    "PATRON: Soooo many options. How will I ever decide?", 
    "(The bartender looks annoyed with the disorderly ubar patron holding up the queue)", 
    "PATRON: I’ll get back to the meetup when I’ve made up my mind, don’t rush me."}, 

    new Lambda(){
      public void activate(){
        cutscenePatronBattleTwo();
}
});
}

public void cutscenePatronBattleTwo() {
  battleManager.battle(battleUnits.playerBattleUnit, battleUnits.patron, 
    new Lambda() {
    public void activate() {
      cutscenePatronBattleThree();
    }
  }
  );
}

public void cutscenePatronBattleThree() {
  switch(world.playerDirection) {
  case UP:
    world.ubar.tiles[7][3].setAppearance(loadImage(assetspath+"Characters/Patron/patron_down.png"));
    break;
  case LEFT:
    world.ubar.tiles[7][3].setAppearance(loadImage(assetspath+"Characters/Patron/patron_right.png"));
    break;    
  case RIGHT:
    world.ubar.tiles[7][3].setAppearance(loadImage(assetspath+"Characters/Patron/patron_left.png"));
    break;    
  case DOWN:
    break;
  }
  if (battleManager.playerUnit.currentHP == 0) {
    textManager.printText(
      new String[]{
      "PATRON: That was fun, I’ll think about what I want for a bit and come back"}, 
      new Lambda(){
        public void activate(){
          cutscenePatronBattleFour();
  }
}
);
}  else {
  textManager.printText(
    new String[]{
    "PATRON: Fine, I’ll come back when I figure out what I want"}, 
    new Lambda(){
      public void activate(){
        cutscenePatronBattleFour();
}
});
}
}

public void cutscenePatronBattleFour() {
  fadeManager.fade(new Lambda() {
    public void activate() {
      world.ubar.tiles[7][3] = null;
      world.ubar.tiles[13][4] = new Tile(loadImage(assetspath+"Characters/Patron/patron_down.png"), false, 
        new TurnToFace(
        assetspath+"Characters/Patron/patron", 
        new Lambda() {
        public void activate() {
          textManager.printText(new String[]{
            "PATRON: Maybe I should get the vegetarian pizza… but the margarita looks so good…"
            });
        };
      }
      ), true);

      //Making it so that talking to karen triggers the next cutscene
      world.ubar.tiles[13][10].pb = new TurnToFace(
        assetspath+"Characters/Karen/karen", 
        new Lambda() {
        public void activate() {
          cutsceneFourPartOne();
        }
      }
      );

      world.playerX = 7;
      world.playerY = 3;
      world.playerDirection = Direction.UP;
      world.adjustOffsets();
    }
  }
  , new Lambda() {
    public void activate() {
      cutscenePatronBattleFive();
    }
  }
  );
}

public void cutscenePatronBattleFive() {
  textManager.printText(new String[]{
    "BARTENDER: Thanks for helping move her along, here’s your food", 
    "(I should head back to Karen)"
    });
}

public void cutsceneFourPartOne() {
  textManager.printText(new String[]{
    "KAREN: Nice work, let’s eat!", 
    "Mmmhmm delicious.", 
    "…", 
    "Okay, let’s go somewhere more quiet, I’ll meet you at the library - don’t keep me waiting again."
    }, new Lambda(){
      public void activate(){
        cutsceneFourPartTwo();
}
});
}

public void cutsceneFourPartTwo() {
  fadeManager.fade(new Lambda() {
    public void activate() {
      world.ubar.tiles[13][10] = null;
      Tile outsideToLibrary = new Tile(new LandBehaviour() {
        public void activate(Tile t) {
          fadeManager.fade(new Lambda() {
            public void activate() {
              world.playerX = 7;
              world.playerY = 9;
              world.playerDirection = Direction.UP;
              world.currentRoom = world.library;
              world.adjustOffsets();
            }
          }
          );
        }
      }
      );
      world.outside.tiles[10][13] = outsideToLibrary;
      world.outside.tiles[11][13] = outsideToLibrary;
    }
  }
  );
}

public void cutsceneFivePartOne() {
  textManager.printText(new String[]{
    "KAREN: Hey "+player.name+" I can’t find a seat anywhere! Looks like high school students are coming here to hang out with their friends. That table at the back would be perfect, go ask if we can have it."
    }, new Lambda(){
      public void activate(){
        world.library.tiles[7][8] = null;
  gameStateManager.setState(State.WORLD);
}
});
}

public void cutsceneSchoolBattleOne() {
  textManager.printText(new String[]{
    "The high schoolers glare at you sleepily", 
    }, new Lambda(){
      public void activate(){
        cutsceneSchoolBattleTwo();
}
});
}


public void cutsceneSchoolBattleTwo() {
  battleManager.battle(battleUnits.playerBattleUnit, battleUnits.schoolKids, 
    new Lambda() {
    public void activate() {
      cutsceneSchoolBattleThree();
    }
  }
  );
}

public void cutsceneSchoolBattleThree() {
  if (battleManager.playerUnit.currentHP == 0) {
    textManager.printText(
      new String[]{
      "HIGHSCHOOLER: Heh, we’re heading off anyway"}, 
      new Lambda(){
        public void activate(){
          cutsceneSchoolBattleFour();
  }
}
);
}  else {
  textManager.printText(
    new String[]{
    "HIGHSCHOOLER: Whatever, we were heading off anyway"}, 
    new Lambda(){
      public void activate(){
        cutsceneSchoolBattleFour();
}
});
}
}

public void cutsceneSchoolBattleFour() {
  fadeManager.fade(new Lambda() {
    public void activate() {
      world.library.tiles[10][4] = null;
      world.library.tiles[13][4] = null;
      world.library.tiles[10][3] = null;
      world.library.tiles[8][8] = null;

      world.playerX = 10;
      world.playerY = 4;
      world.playerDirection = Direction.RIGHT;
      world.adjustOffsets();

      world.library.tiles[10][3] = new Tile(loadImage(assetspath+"Characters/Karen/karen_left.png"), false, true);
    }
  }
  , new Lambda() {
    public void activate() {
      cutsceneSchoolBattleFive();
    }
  }
  );
}

public void cutsceneSchoolBattleFive() {
  textManager.printText(new String[]{
    "KAREN: This is the worst - I have no idea what to do, any ideas?" 
    }, new Lambda(){
      public void activate(){
        cutsceneSchoolBattleSix();
}
});
}

public void cutsceneSchoolBattleSix() {
  world.library.tiles[10][3].setAppearance(loadImage(assetspath+"Characters/Karen/karen_down.png"));
  world.drawOverworld();
  world.sleepWorld(15, new Lambda() {
    public void activate() {
      cutsceneSchoolBattleSeven();
    }
  }
  );
}

public void cutsceneSchoolBattleSeven() {
  textManager.printText(new String[]{
    "KAREN: I mean you DID go to the lectures, what’s the harm in sharing what you have so far?" 
    }, new Lambda(){
      public void activate(){
        cutsceneSchoolBattleEight();
}
});
}

public void cutsceneSchoolBattleEight() {
  world.playerDirection = Direction.UP;
  world.drawOverworld();
  world.sleepWorld(15, new Lambda() {
    public void activate() {
      cutsceneSchoolBattleNine();
    }
  }
  );
}

public void cutsceneSchoolBattleNine() {
  textManager.printText(new String[]{
    "...", 
    "KAREN: Hey, I was busy with my friends so of course I didn’t have time to start it before today.", 
    "Argh! Just work on your own then I’ll figure something out."
    }, new Lambda(){
      public void activate(){
        cutsceneSchoolBattleTen();
}
});
}

public void cutsceneSchoolBattleTen() {
  world.playerDirection = Direction.RIGHT;
  world.library.tiles[10][3].setAppearance(loadImage(assetspath+"Characters/Karen/karen_right.png"));
  world.drawOverworld();
  world.sleepWorld(15, new Lambda() {
    public void activate() {
      cutsceneSchoolBattleEleven();
    }
  }
  );
}

public void cutsceneSchoolBattleEleven() {
  textManager.printText(new String[]{
    "(As you type up your answers you can’t help but get the feeling that Karen isn’t really looking at her own screen)" 
    }, new Lambda(){
      public void activate(){
        cutsceneSchoolBattleTwelve();
}
});
}

public void cutsceneSchoolBattleTwelve() {
  fadeManager.fade(new Lambda() {
    public void activate() {
    };
  }
  , new Lambda() {
    public void activate() {
      cutsceneSchoolBattleThirteen();
    }
  }
  );
}

public void cutsceneSchoolBattleThirteen() {
  textManager.printText(new String[]{
    "KAREN: Aha! We finished at the same time - Yay! I’m going to go hand this in right now.", 
    "Yeah sorry I can’t stick around, gotta run!"
    }, new Lambda(){
      public void activate(){
        cutsceneSchoolBattleFourteen();
}
});
}

public void cutsceneSchoolBattleFourteen() {
  fadeManager.fade(new Lambda() {
    public void activate() {
      world.library.tiles[10][3] = null;
    }
  }
  , new Lambda() {
    public void activate() {
      world.pcLab.tiles[6][3] = new Tile(loadImage(assetspath+"Characters/Teacher/teacher_down.png"), false, 
        new TurnToFace(
        assetspath+"Characters/Teacher/teacher", 
        new Lambda() {
        public void activate() {
          cutsceneTeacherBattleOne();
        };
      }
      ), true);
      textManager.printText(new String[]{
        "(I should go back to the classroom to hand this in)"
        });
    }
  }
  );
}

public void cutsceneTeacherBattleOne() {
  textManager.printText(new String[]{
    "TEACHER: All finished with the assignment? Good work."
    }, new Lambda(){
      public void activate(){
        cutsceneTeacherBattleTwo();
      }
  });
}

public void cutsceneTeacherBattleTwo() {
  fadeManager.fade(new Lambda() {
    public void activate() {
      world.pcLab.tiles[8][6] = null;
      world.pcLab.tiles[7][4] = new Tile(loadImage(assetspath+"Characters/Dev/dev_up.png"), false, true);
    }
  }
  , new Lambda() {
    public void activate() {
      cutsceneTeacherBattleThree();
    }
  }
  );
}

public void cutsceneTeacherBattleThree() {
  textManager.printText(new String[]{
    "DEV: I’m finally done, I couldn’t do every task but I’m proud of myself for powering through.", 
    "TEACHER: Well done! I’ll have your feedback uploaded by the end of the week which will help you find which areas need more work.", 
    "DEV: Cheers, see you next week."
    }, new Lambda(){
      public void activate(){
        cutsceneTeacherBattleFour();
    }
    });
}

public void cutsceneTeacherBattleFour() {
  fadeManager.fade(new Lambda() {
    public void activate() {
      world.pcLab.tiles[7][4] = null;
    }
  }
  , new Lambda() {
    public void activate() {
      cutsceneTeacherBattleFive();
    }
  }
  );
}

public void cutsceneTeacherBattleFive() {
  textManager.printText(new String[]{
    "TEACHER: Hm, okay "+player.name+", let’s put this in the system.", 
    "...", 
    "TEACHER: Oh dear... 90% plagiarism.", 
    "…"+player.name+" this is not good, not only will you not receive any marks but you also will need to be reported to the disciplinary committee. Please explain yourself"
    }, new Lambda(){
      public void activate(){
        cutsceneTeacherBattleSix();
      }
    }
);
}

public void cutsceneTeacherBattleSix() {
  battleManager.battle(battleUnits.playerBattleUnit, battleUnits.teacher, 
    new Lambda() {
    public void activate() {
      cutsceneTeacherBattleSeven();
    }
  }
  );
}

public void cutsceneTeacherBattleSeven() {
  textManager.printText(new String[]{
    "TEACHER: Okay, I see… So Karen said she would work on her own, but then used your work without acknowledgement. And now it seems you are just as surprised as me. Your honesty is appreciated. I can’t give you any marks - you need to learn to be more careful in the future. But, I won’t need to report you to the higher ups . The point of this small assignment was so that I could give you feedback that will help in the next assessment. That feedback will still be valuable to you as you did the work but Karen wont have the benefit of that. I’ll see you in class next week." 
    }, new Lambda(){
      public void activate(){
        cutsceneTeacherBattleEight();
}
});
}

public void cutsceneTeacherBattleEight() {
  world.pcLab.tiles[6][3].pb = new TurnToFace(
    assetspath+"Characters/Teacher/teacher", 
      new Lambda() {
        public void activate() {
          textManager.printText(new String[]{
            "TEACHER: See you next week" 
           });
        }
       }
     );
  gameStateManager.setState(State.WORLD);
}
class FadeManager{
  int fadeTime;
  Lambda midFade;
  Lambda afterFade;
  boolean doneMid;
  
  public FadeManager(){}
  
  public void process(){
    fadeTime +=15;
    if(fadeTime <= 255){
      PGraphics overworldScene = world.getScene();
      image(overworldScene,0,0);
      fill(0,0,0,min(fadeTime,255));
      noStroke();
      rect(0,0,width,height);
    }else if(fadeTime <= 510){
      if(!doneMid){
        doneMid = true;
        midFade.activate();
        world.drawOverworld();
      }
      PGraphics overworldScene = world.getScene();
      image(overworldScene,0,0);
      fill(0,0,0,max(510-fadeTime,0));
      noStroke();
      rect(0,0,width,height);
    }else{
      afterFade.activate();
    }
  }
  
  public void fade(Lambda midFade){
    this.midFade = midFade;
    this.afterFade = new Lambda(){
      public void activate(){
        gameStateManager.setState(State.WORLD);   
      }
    };
    this.fadeTime = 0;
    gameStateManager.setState(State.FADE);
    doneMid = false;
  }
  
  public void fade(Lambda midFade, Lambda afterFade){
    this.midFade = midFade;
    this.afterFade = afterFade;
    this.fadeTime = 0;
    gameStateManager.setState(State.FADE);
    doneMid = false;
  }
  
}

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
        world.process(); break;  
      case TEXT:
        textManager.process(); break;
      case BATTLE:
        battleManager.process(); break;
      case FADE:
        fadeManager.process(); break;
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
  
  //how many frames ahead we let players buffer inputs
  final int bufferLength = 1;
 
  //Assign keys in map. We need a reverse map, from button -> key value, so that we can easily remap buttons.
  public KeyPressManager() {
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
    if(frameCount-lastKeyFrameCount <= bufferLength){
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
    lastKeyFrameCount = frameCount;
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
class Library extends Room {

  static final String path = assetspath+"Tiles/Library/";

  private Tile wall = new Tile();
  private Tile overlay = new Tile(loadImage(path+"Overlay.png"), true, false);

  private Tile karen = new Tile(loadImage(assetspath+"Characters/Karen/karen_up.png"), false, 
    new TurnToFace(
    assetspath+"Characters/Karen/karen", 
    new Lambda() {
    public void activate() {
      textManager.printText(new String[]{
        "KAREN: That table at the back would be perfect, go ask if we can have it."
        });
    };
  }
  ), true);
  
  private Tile schoolKidOne = new Tile(loadImage(assetspath+"Characters/SchoolKids/schoolkid1_right.png"), false, 
    new TurnToFace(
    assetspath+"Characters/SchoolKids/schoolkid1", 
    new Lambda() {
    public void activate() {
      cutsceneSchoolBattleOne();
    };
  })
  , true);
  
  private Tile schoolKidTwo = new Tile(loadImage(assetspath+"Characters/SchoolKids/schoolkid2_left.png"), false, 
    new TurnToFace(
    assetspath+"Characters/SchoolKids/schoolkid2", 
    new Lambda() {
    public void activate() {
      cutsceneSchoolBattleOne();
    };
  })
  , true);
  
  private Tile schoolKidThree = new Tile(loadImage(assetspath+"Characters/SchoolKids/schoolkid3_right.png"), false, 
    new TurnToFace(
    assetspath+"Characters/SchoolKids/schoolkid3", 
    new Lambda() {
    public void activate() {
      cutsceneSchoolBattleOne();
    };
  })
  , true);
  
  private Tile karenCutscene = new Tile(new LandBehaviour(){
    public void activate(Tile t){
      cutsceneFivePartOne(); 
    }
  });
  private Tile outsideTeleport = new Tile(new LandBehaviour() {
    public void activate(Tile t) {
      fadeManager.fade(new Lambda() {
        public void activate() {
          world.playerX = 10;
          world.playerY = 12;
          world.playerDirection = Direction.UP;
          world.currentRoom = world.outside;
          world.adjustOffsets();
        }
      }
      );
    }
  }
  );
  
  private Tile npc0 = new Sign(loadImage(assetspath+"Characters/SchoolKids/schoolkid3_right.png"),"(They look too invested in their study to talk to you)");
  private Tile npc1 = new Sign(loadImage(assetspath+"Characters/SchoolKids/schoolkid3_right.png"),"(They look too invested in their study to talk to you)");
  private Tile npc2 = new Sign(loadImage(assetspath+"Characters/NPCs/npc1_left.png"),"(They’re looking at colourful statistical diagrams.)");
  private Tile npc3 = new Sign(loadImage(assetspath+"Characters/NPCs/npc2_right.png"),"(They’re taking a buzzfeed quiz, \"What kind of procrastination am I?\")");
  
  private Tile clrs = new Sign(new String[]{"You browse the bookshelf and find a book that looks interesting.", "Introduction to Algorithms by Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, and Clifford Stein", "...", "Let's put this back."});
  
  public Library() {
    super(loadImage(path+"Background.png"));

    int[][] walls = {{0,1},{1,1},{2,1},{3,1},{4,1},{5,1},{6,1},{7,1},{8,1},{9,1},{10,1},{11,1},
                    {12,1},{13,1},{14,1},{14,2},{0,4},{1,4},{14,2},{0,4},{1,4},{2,4},{3,4},{4,4},
                    {5,4},{0,7},{1,7},{2,7},{3,7},{4,7},{5,7},{11,3},{12,3},{11,4},{12,4},{14,6},
                    {14,7},{11,8},{12,8},{11,9},{12,9},{1,9},{2,9}};

    for (int[] arr : walls) {
      tiles[arr[0]][arr[1]] = wall;
    }

    tiles[tiles.length-1][tiles[0].length-1] = overlay;

    tiles[7][9] = outsideTeleport;
    tiles[8][9] = outsideTeleport;
    
    tiles[7][8] = karenCutscene;
    tiles[8][8] = karen;
    
    tiles[10][4] = schoolKidOne;
    tiles[13][4] = schoolKidTwo;
    tiles[10][3] = schoolKidThree;
    
    tiles[3][9] = npc0;
    tiles[0][9] = npc1;
    tiles[13][9] = npc2;
    tiles[10][8] = npc3;
    
    tiles[0][1] = clrs;
  }
}
class Outside extends Room {

  static final String path = assetspath+"Tiles/Outside/";

  private Tile wall = new Tile();
  private Tile overlay = new Tile(loadImage(path+"Overlay.png"), true, false);

  private Tile ibis = new Tile(loadImage(assetspath+"Characters/Ibis/ibis_left.png"), true, false);

  private Tile ibisBattle = new Tile(new LandBehaviour() {
    public void activate(Tile t) {
      cutsceneIbisBattleOne();
    }
  }
  );

  private Tile pcLabTeleport = new Tile(new LandBehaviour() {
    public void activate(Tile t) {
      fadeManager.fade(new Lambda() {
        public void activate() {
          world.playerX = 12;
          world.playerY = 4;
          world.playerDirection = Direction.LEFT;
          world.currentRoom = world.pcLab;
          world.adjustOffsets();
        }
      }
      );
    }
  }
  );

  private Tile ubarTeleportOne = new Tile(new LandBehaviour() {
    public void activate(Tile t) {
      fadeManager.fade(new Lambda() {
        public void activate() {
          world.playerX = 0;
          world.playerY = 8;
          world.playerDirection = Direction.RIGHT;
          world.currentRoom = world.ubar;
          world.adjustOffsets();
        }
      }
      );
    }
  }
  );
  private Tile ubarTeleportTwo = new Tile(new LandBehaviour() {
    public void activate(Tile t) {
      fadeManager.fade(new Lambda() {
        public void activate() {
          world.playerX = 0;
          world.playerY = 9;
          world.playerDirection = Direction.RIGHT;
          world.currentRoom = world.ubar;
          world.adjustOffsets();
        }
      }
      );
    }
  }
  );
  
  private Tile noLibraryYet = new Tile(new LandBehaviour() {
    public void activate(Tile t) {
      fadeManager.fade(new Lambda() {
        public void activate() {
          world.playerY = world.playerY -1;
          world.playerDirection = Direction.UP;
          world.adjustOffsets();
        }
      }
      , 
        new Lambda() {
        public void activate() {
          textManager.printText(new String[]{"(I should eat before going the the library, I'm hungry)"});
        }
      }
      );
    }
  }
  );

  public Outside() {
    super(loadImage(path+"Background.png"));

    int[][] walls = {{2, 4}, {3, 4}, {4, 4}, {5, 4}, {6, 4}, {7, 4}, {8, 4}, {9, 4}, 
      {12, 4}, {13, 4}, {14, 5}, {15, 6}, {15, 7}, {16, 8}, {16, 9}, 
      {16, 10}, {15, 11}, {14, 12}, {13, 13}, {12, 14}, {2, 14}, {3, 14}, 
      {4, 14}, {5, 14}, {6, 14}, {7, 14}, {8, 14}, {9, 14}, {2, 5}, {2, 6}, 
      {2, 7}, {2, 8}, {2, 9}, {2, 10}, {2, 11}, {2, 12}, {2, 13}, {10, 3}, {11, 3}, {15, 10}};
    for (int[] arr : walls) {
      tiles[arr[0]][arr[1]] = wall;
    }

    tiles[tiles.length-1][tiles[0].length-1] = overlay;

    tiles[10][4] = pcLabTeleport;
    tiles[11][4] = pcLabTeleport;

    tiles[13][8] = ibis;
    tiles[12][8] = ibisBattle;
    tiles[13][7] = ibisBattle;
    tiles[13][9] = ibisBattle;
    tiles[14][8] = ibisBattle;
    tiles[14][9] = ibisBattle;
    
    tiles[10][13] = noLibraryYet;
    tiles[11][13] = noLibraryYet;
    
    tiles[15][8] = ubarTeleportOne;
    tiles[15][9] = ubarTeleportTwo;
  }
}
//This class manages and draws the overworld

enum Direction {
  LEFT, RIGHT, UP, DOWN
}

class OverworldManager {
  public boolean locked = false;

  Room pcLab = new PCLab();
  Room outside = new Outside();
  Room ubar = new UBar();
  Room library = new Library();
  Room currentRoom = pcLab;
  Direction playerDirection = Direction.RIGHT;
  TextManager text = new TextManager();
  
  Lambda afterSleep;
  


  //Player starts the game facing right, at [5][6]
  //Rendering magic below
  int playerX = 5;
  int playerY = 6;
  int playerDrawX = (playerX)*16; 
  int playerDrawY = (playerY-1)*16;

  //player render offset based on the sprite resolution
  final int offsetX = 240/2-8;
  final int offsetY = 160/2-24;

  int worldSleep = 0;

  public OverworldManager() {
    drawOverworld();
  };

 
  int animationState = 0;

  public void process() {
    if(worldSleep == 1){
     worldSleep = 0;
     afterSleep.activate();
     return;
    }else if(worldSleep > 0){
     --worldSleep; 
    }else if (animationState%8 != 0) {
      movePlayer();
    } else {
      processInput();
    }
    drawOverworld();
  }

  private PGraphics scene = createGraphics(240*scale,160*scale);
  
  public PGraphics getScene(){
    return scene;
  }
  
  public void setScene(PGraphics scene){
    if(scene != null){
      this.scene = scene; 
    }
  }

  private void drawOverworld() {
    //draw a black background
    //scene = createGraphics(240, 160);
    scene.beginDraw();
    scene.background(0);

    //draw the current background plate, offset by the player location 
    scene.image(currentRoom.background, (offsetX-playerDrawX)*scale, (offsetY-playerDrawY)*scale);

    //iterate through active elements, drawing the tiles below the player
    for (int i = 0; i < currentRoom.tiles[0].length; i++) {
      for (int j = 0; j < currentRoom.tiles.length; ++j) {
        if (currentRoom.tiles[j][i] != null && !currentRoom.tiles[j][i].displayOver) {
          drawTile(currentRoom.tiles[j][i], j, i);
        }
        if(i == playerY && j == playerX) drawPlayer();
      }
    }
    //then draw the tiles that should display above the player
    for (int i = 0; i < currentRoom.tiles[0].length; i++) {
      for (int j = 0; j < currentRoom.tiles.length; ++j) {
        if (currentRoom.tiles[j][i] != null && currentRoom.tiles[j][i].displayOver) {
          drawTile(currentRoom.tiles[j][i], j, i);
        }
      }
    }
    scene.endDraw();
    image(scene, 0, 0);
  }


  private void drawTile(Tile t, int x, int y) {
    int tileXPos = offsetX-playerDrawX-t.offsetX+x*16;
    int tileYPos = offsetY-playerDrawY-t.offsetY+y*16;
    scene.image(t.appearance, tileXPos*scale, tileYPos*scale);
  }

  private void drawPlayer() {
    switch(playerDirection) {
    case UP: 
      scene.image(player.overworldUp[animationState/4], offsetX*scale, offsetY*scale); 
      break;
    case DOWN: 
      scene.image(player.overworldDown[animationState/4], offsetX*scale, offsetY*scale); 
      break;
    case LEFT: 
      scene.image(player.overworldLeft[animationState/4], offsetX*scale, offsetY*scale); 
      break;
    case RIGHT: 
      scene.image(player.overworldRight[animationState/4], offsetX*scale, offsetY*scale); 
      break;
    }
  }

  private void movePlayer() {
    ++animationState; 
    animationState%=16;
    switch(playerDirection) {
    case UP:
      playerDrawY-=2; 
      break;
    case DOWN:
      playerDrawY+=2; 
      break;
    case LEFT:
      playerDrawX-=2; 
      break;
    case RIGHT:
      playerDrawX+=2; 
      break;
    }
    if (animationState%8 == 0) {
      currentRoom.processLanding(playerX, playerY);
    }
  }

  private void processInput() {
    switch(keyPressManager.getKey()) {
    case UP:
      playerDirection = Direction.UP;
      if (validDirection()) {     
        --playerY;
        movePlayer();
      } 
      break;
    case DOWN:
      playerDirection = Direction.DOWN; 
      if (validDirection()) {
        ++playerY;
        movePlayer();
      } 
      break;
    case LEFT:
      playerDirection = Direction.LEFT;
      if (validDirection()) {
        --playerX;
        movePlayer();
      } 
      break;
    case RIGHT:
      playerDirection = Direction.RIGHT;
      if (validDirection()) { 
        ++playerX;
        movePlayer();
      } 
      break;
    case A:
      processInteraction(); 
      break;
    default:
      break;
    }
  }

  private boolean validDirection() {
    int candX = playerX;
    int candY = playerY;
    switch(playerDirection) {
    case UP:
      --candY; 
      break;
    case DOWN:
      ++candY; 
      break;
    case LEFT:
      --candX; 
      break;
    case RIGHT:
      ++candX; 
      break;
    }
    return currentRoom.validDirection(candX, candY);
  }

  private void processInteraction() {
    int candX = playerX;
    int candY = playerY;
    switch(playerDirection) {
    case UP:
      --candY; 
      break;
    case DOWN:
      ++candY; 
      break;
    case LEFT:
      --candX; 
      break;
    case RIGHT:
      ++candX; 
      break;
    }
    currentRoom.processInteraction(candX, candY);
  }
  
  public void sleepWorld(int worldSleep){
    gameStateManager.setState(State.WORLD);
    this.worldSleep = worldSleep;
    this.afterSleep = new Lambda(){ public void activate(){}};
  }
  
  public void sleepWorld(int worldSleep, Lambda afterSleep){
    gameStateManager.setState(State.WORLD);
    this.worldSleep = worldSleep;
    this.afterSleep = afterSleep;
  }
  
  public void adjustOffsets(){
    playerDrawX = (playerX)*16; 
    playerDrawY = (playerY-1)*16; 
  }
  
}
class PCLab extends Room {
  static final String path = assetspath+"Tiles/PCLab/";


  private Tile wall = new Tile();

  private Tile teacher = new Tile(loadImage(assetspath+"Characters/Teacher/teacher_down.png"), false, 
    new TurnToFace(
    assetspath+"Characters/Teacher/teacher", 
    new Lambda() {
    public void activate() {
      textManager.printText(new String[]{
        "TEACHER: Don’t forget to hand the assignment in when it’s finished"
        });
    };
  }
  ), true);

  private Tile karen = new Tile(loadImage(assetspath+"Characters/Karen/karen_left.png"), false, 
    new PressBehaviour() {
    public void activate(Tile t) {
      textManager.printText(new String[]{
        "KAREN: Stupid program isnt saving… I’ll catch up with you later"
        });
    }
  }
  , true);

  private Tile dev = new Tile(loadImage(assetspath+"Characters/Dev/dev_left.png"), false, 
    new TurnToFace(
    assetspath+"Characters/Dev/dev", 
    new Lambda() {
    public void activate() {
      textManager.printText(new String[]{
        "DEV: I’m really struggling, I should have asked for help earlier…"
        });
    };
  }
  ), true);
  
  private Tile bin = new Sign(new String[]{"It's just trash"});
  private Tile projector = new Sign(new String[]{"So many buttons, but none of them will solve my assignment for me"});
  private Tile karenspc = new Sign(new String[]{"She forgot to log out..."});
  private Tile cutsceneTwo = new Tile(new LandBehaviour(){
    public void activate(Tile t){
      cutsceneTwoPartOne();  
    }
  });
  
  private Tile npc3 = new Sign(loadImage(assetspath+"Characters/NPCs/npc3_right.png"),"(Looks like they're busy)");
  private Tile npc4 = new Sign(loadImage(assetspath+"Characters/NPCs/npc4_right.png"),"(Looks like they're busy)");
  
  private Tile outsideTeleport = new Tile(new LandBehaviour(){
    public void activate(Tile t){
       fadeManager.fade(new Lambda(){
         public void activate(){
           world.playerX = 10;
           world.playerY = 4;
           world.playerDirection = Direction.DOWN;
           world.currentRoom = world.outside;
           world.adjustOffsets();
         }
       });
    }
  });

  public PCLab() {
    super(loadImage(path+"Background.png"));
    //Setting up all the walls so you can't walk on tables
    int[][] wallLocations = {{0, 2}, {1, 2}, {2, 2}, {3, 2}, {4, 2}, {5, 2}, {6, 2}, {7, 2}, 
      {9, 2}, {10, 2}, {11, 2}, {12, 3}, {12, 5}, {1, 5}, {1, 6}, {1, 7}, {1, 8}, 
      {2, 5}, {2, 6}, {2, 7}, {2, 8}, {6, 5}, {6, 6}, {6, 7}, {6, 8}, {7, 5}, 
      {7, 6}, {7, 7}, {7, 8}, {11, 5}, {11, 6}, {11, 7}, {11, 8}};
    for (int[] arr : wallLocations) {
      tiles[arr[0]][arr[1]] = wall;
    }
    
    tiles[3][7] = karen;
    tiles[6][3] = teacher;
    tiles[8][6] = dev;

    tiles[8][2] = projector;
    tiles[11][3] = bin;
    tiles[2][7] = karenspc;
    tiles[11][4] = cutsceneTwo;
    tiles[12][4] = outsideTeleport;
    
    tiles[0][5] = npc3;
    tiles[10][8] = npc4;
  }
}
class Player {
  PImage[] overworldLeft;
  PImage[] overworldRight;  
  PImage[] overworldUp; 
  PImage[] overworldDown;
  PImage battleImage;
  String playerType;
  String name = "Macky"; 

  static final String path = assetspath+"Characters/Player/";
  static final String battlepath = assetspath+"Characters/Battle/";

  public Player(String playerType) {
    this.playerType = playerType;
    battleImage = loadImage(battlepath+playerType+"_battle.png"); //battleUnit constructor will automatically upscale this
    PImage[] tOverworldLeft = {loadImage(path+playerType+"_left_still.png"), loadImage(path+playerType+"_left_left.png"), loadImage(path+playerType+"_left_still.png"), loadImage(path+playerType+"_left_right.png")};   
    overworldLeft = tOverworldLeft;
    PImage[] tOverworldRight = {loadImage(path+playerType+"_right_still.png"), loadImage(path+playerType+"_right_left.png"), loadImage(path+playerType+"_right_still.png"), loadImage(path+playerType+"_right_right.png")};   
    overworldRight = tOverworldRight;
    PImage[] tOverworldUp = {loadImage(path+playerType+"_up_still.png"), loadImage(path+playerType+"_up_left.png"), loadImage(path+playerType+"_up_still.png"), loadImage(path+playerType+"_up_right.png")};   
    overworldUp = tOverworldUp;
    PImage[] tOverworldDown = {loadImage(path+playerType+"_down_still.png"), loadImage(path+playerType+"_down_left.png"), loadImage(path+playerType+"_down_still.png"), loadImage(path+playerType+"_down_right.png")};   
    overworldDown = tOverworldDown;
    
    for(int i = 0; i < overworldLeft.length; ++i){
      overworldLeft[i] = upscale(overworldLeft[i]);
      overworldRight[i] = upscale(overworldRight[i]);
      overworldUp[i] = upscale(overworldUp[i]);
      overworldDown[i] = upscale(overworldDown[i]);
    }
  }
}
class Room {
  PImage background;
  int roomWidth; 
  int roomHeight;
  Tile[][] tiles;
  public Room(PImage background){
    this.background = upscale(background);
    roomWidth = background.width/16;
    roomHeight = background.height/16;
    tiles = new Tile[roomWidth][roomHeight];
  }
  
  public void processLanding(int x, int y){
    if(tiles[x][y] != null){
      tiles[x][y].land(); 
    }
  }
  
  public void processInteraction(int x, int y){
    if(x > -1 && x < tiles.length && y > -1 && y < tiles[0].length){
      if(tiles[x][y] != null) tiles[x][y].press(); 
    }
  }
  
  public boolean validDirection(int x, int y){
    if(x > -1 && x < tiles.length && y > -1 && y < tiles[0].length){
      if(tiles[x][y] == null || !tiles[x][y].isSolid) return true; 
    }
    return false;
  }
  
  
}
class TextManager {
  final String textboxPath = assetspath+"Other/textbox.png";
  final int textX = 14*scale;
  final int textY = 133*scale;
  PImage textbox = upscale(loadImage(textboxPath));
  int currentCharacter = 0;
  int currentString = 0;
  int currentLine = 0;
  final int lineLength = 32;
  int currentOffset = 0;
  final int stringBuilderDefaultSize = lineLength*2+1;
  boolean breaking = false;
  

  StringBuilder sb;
  String[] strings;

  PImage currentDisplay;
  
  Lambda afterText;

  public TextManager() {
    textFont(textDisplayFont);
    sb = new StringBuilder(stringBuilderDefaultSize);
  }

  public void process() {
    //If we're breaking, and the input is an A, end the break and clear the input.
    //If there is no next string, change the state back to the overworld.
    if (breaking) {
      if (keyPressManager.getKey() == Button.A) {
        keyPressManager.clearBuffer();
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
      if(keyPressManager.getKey() == Button.A){
        keyPressManager.clearBuffer();
        while(!breaking){
          nextChar(); 
        }
      }else{
        nextChar(); 
      }
    }
    PGraphics scene = world.getScene();
    scene.beginDraw();
    scene.image(textbox,0,0);
    scene.fill(0,0,0);
    scene.textFont(textDisplayFont);
    scene.text(sb.toString(),textX,textY);
    scene.endDraw();
    image(scene,0,0);
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
    gameStateManager.setState(State.TEXT);
    keyPressManager.clearBuffer();
    afterText = new Lambda(){
      public void activate(){
        gameStateManager.setState(State.WORLD);
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
    gameStateManager.setState(State.TEXT);
    keyPressManager.clearBuffer();
    this.afterText = afterText;
  }
}
class Tile {
  public PImage appearance;
  public boolean isSolid;
  public boolean displayOver;
  public int offsetX;
  public int offsetY;
  private PressBehaviour pb;
  private LandBehaviour lb;

  //The standard tile, a visible object with no behaviour. May or may not be solid
  public Tile(PImage appearance, boolean displayOver, boolean isSolid) {
    this.appearance = upscale(appearance);
    this.offsetX = appearance.width-16;
    this.offsetY = appearance.height-16;
    this.isSolid = isSolid;
    this.displayOver = displayOver;
    this.pb = new PressBehaviour() {
      public void activate(Tile t) {
      };
    };
    lb = new LandBehaviour() {
      public void activate(Tile t) {
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
      public void activate(Tile t) {
      };
    };
    this.lb = new LandBehaviour() {
      public void activate(Tile t) {
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
      public void activate(Tile t) {
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
      public void activate(Tile t) {
      };
    };
    this.pb = pb;
  }

  //A visible tile with landing behaviour. Can not be solid
  public Tile(PImage appearance, boolean displayOver, LandBehaviour lb) {
    this.appearance = upscale(appearance);
    this.offsetX = appearance.width-16;
    this.offsetY = appearance.height-16;
    this.isSolid = false;
    this.displayOver = displayOver;
    this.pb = new PressBehaviour() {
      public void activate(Tile t) {
      };
    };
    this.lb = lb;
  }

  //A visible tile with press behaviour. May or may not be solid
  public Tile(PImage appearance, boolean displayOver, PressBehaviour pb, boolean isSolid) {
    this.appearance = upscale(appearance);
    this.offsetX = appearance.width-16;
    this.offsetY = appearance.height-16;
    this.isSolid = isSolid;
    this.displayOver = displayOver;
    this.lb = new LandBehaviour() {
      public void activate(Tile t) {
      };
    };
    this.pb = pb;
  }

  public void setAppearance(PImage appearance){
    this.appearance = upscale(appearance);
    this.offsetX = appearance.width-16;
    this.offsetY = appearance.height-16;
  }

  public void press() {
    pb.activate(this);
  }

  public void land() {
    lb.activate(this);
  }
}

//These classes are the same, but they let the Tile constuctor differentiate between on-press behaviour and on-land behaviour.
abstract class LandBehaviour {
  public abstract void activate(Tile t);
}

abstract class PressBehaviour {
  public abstract void activate(Tile t);
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
  public void activate(Tile t) {
    switch(world.playerDirection) {
    case UP:
      t.setAppearance(loadImage(path+"_down.png"));
      break;
    case DOWN:
      t.setAppearance(loadImage(path+"_up.png"));
      break;
    case LEFT:
      t.setAppearance(loadImage(path+"_right.png"));
      break;
    case RIGHT:
      t.setAppearance(loadImage(path+"_left.png"));
      break;
    }
    world.drawOverworld();
    l.activate();
  }
}

class Sign extends Tile{
  public Sign(final String[] text){
    super(new PressBehaviour(){
      public void activate(Tile t){
        textManager.printText(text); 
      }
    },true); 
  }
  
  public Sign(final String text){
    super(new PressBehaviour(){
      public void activate(Tile t){
        textManager.printText(new String[]{text}); 
      }
    },true);
  }
  
  public Sign(PImage appearance, final String text){
    super(appearance,false,new PressBehaviour(){
      public void activate(Tile t){
        textManager.printText(new String[]{text}); 
      }
    },true);
  }
  
  public Sign(PImage appearance, final String[] text){
    super(appearance,false,new PressBehaviour(){
      public void activate(Tile t){
        textManager.printText(text); 
      }
    },true);
  }
}
class UBar extends Room {

  static final String path = assetspath+"Tiles/UBar/";

  private Tile wall = new Tile();
  private Tile overlay = new Tile(loadImage(path+"Overlay.png"), true, false);
  

  private Tile outsideTeleportOne = new Tile(new LandBehaviour() {
    public void activate(Tile t) {
      fadeManager.fade(new Lambda() {
        public void activate() {
          world.playerX = 15;
          world.playerY = 8;
          world.playerDirection = Direction.LEFT;
          world.currentRoom = world.outside;
          world.adjustOffsets();
        }
      }
      );
    }
  }
  );
  private Tile outsideTeleportTwo = new Tile(new LandBehaviour() {
    public void activate(Tile t) {
      fadeManager.fade(new Lambda() {
        public void activate() {
          world.playerX = 15;
          world.playerY = 9;
          world.playerDirection = Direction.LEFT;
          world.currentRoom = world.outside;
          world.adjustOffsets();
        }
      }
      );
    }
  }
  );

  private Tile cutsceneThree = new Tile(new LandBehaviour() {
    public void activate(Tile t) {
      cutsceneThreePartOne();
    }
  }
  );

  private Tile patron = new Tile(loadImage(assetspath+"Characters/Patron/patron_up.png"), false, 
    new PressBehaviour() {
    public void activate(Tile t) {
      cutscenePatronBattleOne();
    }
  }
  , true);


  private Tile ubarSign = new Sign("It's the sign for the campus bar");
  private Tile poster = new Sign("There are posters for upcoming events at the bar");

  private Tile npc0 = new Tile(loadImage(assetspath+"Characters/SchoolKids/schoolkid2_right.png"), false, 
    new TurnToFace(
    assetspath+"Characters/SchoolKids/schoolkid2", 
    new Lambda() {
    public void activate() {
      textManager.printText(new String[]{
        "PATRON: I’m coming back next week for the comedy night"
        });
    };
  }
  ), true);
  
  private Tile npc1 = new Sign(loadImage(assetspath+"Characters/NPCs/npc3_up.png"),"(They're enjoying their burger.)");
  private Tile npc2 = new Sign(loadImage(assetspath+"Characters/NPCs/npc2_down.png"),"PATRON: Let’s have a game after the exam!");
  private Tile npc3 = new Sign(loadImage(assetspath+"Characters/NPCs/npc4_left.png"),"(They’re chatting about pool strategies)");
  
  private Tile npc4 = new Tile(loadImage(assetspath+"Characters/NPCs/npc3_up.png"), false, 
    new TurnToFace(
    assetspath+"Characters/NPCs/npc3", 
    new Lambda() {
    public void activate() {
      textManager.printText(new String[]{
        "MACS MEMBER: The MACS society is awesome, they have a discord channel too"
        });
    };
  }
  ), true);
  
  private Tile npc5 = new Tile(loadImage(assetspath+"Characters/NPCs/npc4_up.png"), false, 
    new TurnToFace(
    assetspath+"Characters/NPCs/npc4", 
    new Lambda() {
    public void activate() {
      textManager.printText(new String[]{
        "MACS MEMBER: I met some really cool friends in this society"
        });
    };
  }
  ), true);
  
  private Tile npc6 = new Tile(loadImage(assetspath+"Characters/NPCs/npc1_down.png"), false, 
    new TurnToFace(
    assetspath+"Characters/NPCs/npc1", 
    new Lambda() {
    public void activate() {
      textManager.printText(new String[]{
        "MACS MEMBER: ‘MACS’ stands for the Macquarie Association of Computing Students"
        });
    };
  }
  ), true);
  
  
  public UBar() {
    super(loadImage(path+"Background.png"));

    int[][] walls = {{0, 2}, {1, 2}, {2, 2}, {3, 2}, {4, 2}, {5, 2}, {6, 2}, {7, 2}, {8, 2}, {9, 2}, {10, 2}, {11, 2}, {12, 2}, 
      {13, 2}, {14, 2}, {15, 2}, {16, 2}, {17, 2}, {1, 3}, {0, 4}, {1, 5}, {2, 5}, {3, 5}, {4, 5}, {1, 6}, {2, 6}, {3, 6}, 
      {4, 6}, {0, 7}, {1, 7}, {2, 7}, {3, 7}, {4, 7}, {4, 8}, {9, 5}, {10, 5}, {9, 6}, {10, 6}, {9, 7}, {10, 7}, {9, 8}, 
      {10, 8}, {9, 9}, {10, 9}, {8, 9}, {10, 10}, {17, 3}, {17, 4}, {17, 5}, {17, 6}, {17, 7}, {17, 8}, {17, 9}, {17, 10}, 
      {17, 11}, {17, 12}, {17, 13}, {17, 14}, {16, 14}, {15, 14}, {14, 14}, {14, 15}, {13, 16}, {12, 17}, {9, 15}, {10, 15}, 
      {11, 15}, {9, 16}, {10, 16}, {11, 16}, {8, 17}, {7, 17}, {6, 16}, {6, 15}, {5, 15}, {4, 14}, {3, 14}, {2, 14}, {2, 13}, 
      {2, 12}, {1, 12}, {0, 11}, {0, 10}, {16, 5}, {16, 6}, {16, 7}, {16, 8}, {16, 9}, {4, 11}, {5, 11}, {4, 12}, {5, 12}, {6, 5}, 
      {7, 5}, {6, 6}, {7, 6}, {6, 7}, {7, 7}, {12, 5}, {13, 5}, {14, 5}, {12, 6}, {13, 6}, {14, 6}, {12, 9}, {13, 9}, {14, 9}, {12, 8}, 
      {13, 8}, {14, 8}};

    for (int[] arr : walls) {
      tiles[arr[0]][arr[1]] = wall;
    }

    tiles[tiles.length-1][tiles[0].length-1] = overlay;

    tiles[0][8] = outsideTeleportOne;
    tiles[0][9] = outsideTeleportTwo;

    tiles[1][8] = cutsceneThree;
    tiles[1][9] = cutsceneThree;
    
    tiles[7][3] = patron; 
    
    tiles[2][7] = ubarSign;
    tiles[9][9] = poster;
    tiles[3][12] = npc0;
    tiles[5][13] = npc1;
    tiles[6][14] = npc2;
    tiles[7][15] = npc3;
    tiles[13][7] = npc4;
    tiles[14][7] = npc5;
    tiles[12][4] = npc6;
  }
}

}
