package unigame;

import processing.core.*;
import java.util.*;

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

  private PGraphics scene;
  final String battlePath = "Tiles/Battle/";
  final PImage bg;
    UniGame p3;



  EnumMap<StatusEffect, String> effectNames;
  public BattleManager(UniGame p3) {
      this.p3 = p3;
      scene = p3.createGraphics(240*Globals.scale,160*Globals.scale);
      bg = p3.upscale(p3.loadImage(Globals.assetspath+battlePath+"Background.png"));
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
    Globals.gameStateManager.setState(State.BATTLE);
    selectedAttack = 0;
    Globals.keyPressManager.clearBuffer();
    introFrame = 0; 
    outroFrame = 0;
    battleFinished = false;
    playerUnit.effects = new LinkedList<Effect>();
    enemy.effects = new LinkedList<Effect>();
    afterBattle = new Lambda(){
      public void activate(){
        Globals.gameStateManager.setState(State.WORLD); 
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
    p3.noStroke();
    
    //lots of magic numbers with draw element locations but hey it works
    //enemy hp bar
    int eHPSize = (int)(83.0f/enemy.maxHP*enemy.currentHP);
    scene.rect(22*Globals.scale, 36*Globals.scale, eHPSize*Globals.scale, 5*Globals.scale);


    //player hp bar
    int pHPSize = (int)(83.0f/playerUnit.maxHP*playerUnit.currentHP);
    scene.rect(150*Globals.scale, 101*Globals.scale, pHPSize*Globals.scale, 5*Globals.scale);

    scene.image(playerUnit.appearance, 40*Globals.scale, 57*Globals.scale);
    scene.image(enemy.appearance, 150*Globals.scale, 10*Globals.scale);
    scene.textFont(Globals.battleUIFont);
    scene.fill(0, 0, 0);
    scene.text(enemy.name, (63-(enemy.name.length()*4))*Globals.scale, 30*Globals.scale);
    scene.text(playerUnit.name, (191-(playerUnit.name.length()*4))*Globals.scale, 96*Globals.scale);
    scene.endDraw();
    p3.image(scene, 0, 0);
  }

  private void introDisplay() {
    display();
    p3.fill(0, 0, 0);
    p3.text(playerUnit.attack0.name, 4*Globals.scale, 130*Globals.scale);
    p3.text(playerUnit.attack1.name, 4*Globals.scale, 150*Globals.scale);
    p3.text(playerUnit.attack2.name, 124*Globals.scale, 130*Globals.scale);
    p3.text(playerUnit.attack3.name, 124*Globals.scale, 150*Globals.scale);
  }

  private void processIntro() {
    introFrame +=15;
    if (introFrame <= 255) {
      PGraphics overworldScene = Globals.world.getScene();
      p3.image(overworldScene, 0, 0);
      p3.fill(0, 0, 0, UniGame.min(introFrame, 255));
      p3.noStroke();
      p3.rect(0, 0, p3.width, p3.height);
    } else if (introFrame <= 510) {
      introDisplay();
      p3.fill(0, 0, 0, p3.max(0, 510-introFrame));
      p3.noStroke();
      p3.rect(0, 0, p3.width, p3.height);
    } else {
      currentState = BattleState.MENU;
    }
  }
  
  private void processOutro(){
    outroFrame+=15;
    if(outroFrame <= 255){
      display();
      p3.fill(0,0,0, UniGame.min(outroFrame,255));
      p3.noStroke();
      p3.rect(0,0,p3.width,p3.height);
    }else if(outroFrame <= 510){
      PGraphics overworldScene = Globals.world.getScene();
      p3.image(overworldScene, 0, 0);
      p3.fill(0, 0, 0, UniGame.max(0,510-outroFrame));
      p3.noStroke();
      p3.rect(0, 0, p3.width, p3.height);
    }else{
      afterBattle.activate(); 
    }
  }

  private void processMenu() {
    switch((int)p3.random(4)) {
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
    switch(Globals.keyPressManager.getKey()) {
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
        System.out.println("Attack selection out of bounds, how did you do this?");
        break;
      }
      currentState = BattleState.ATK;
      //If the speed is a tie, randomly select who attacks first
      playerFirst = playerUnit.speed+((int)p3.random(2)) > enemy.speed;
      currentState = BattleState.ATK;
      firstAttackDone = false;
      Globals.keyPressManager.clearBuffer();
      return;
    default:
      //who cares
    }
    Globals.keyPressManager.clearBuffer();
    //Now, let's display the moves, with the currently selected one highlighted
    if (selectedAttack == 0) {
      p3.fill(255, 255, 255);
    } else {
      p3.fill(0, 0, 0);
    }
    p3.text(playerUnit.attack0.name, 4*Globals.scale, 130*Globals.scale);
    if (selectedAttack == 1) {
      p3.fill(255, 255, 255);
    } else {
      p3.fill(0, 0, 0);
    }
    p3.text(playerUnit.attack1.name, 4*Globals.scale, 150*Globals.scale);
    if (selectedAttack == 2) {
      p3.fill(255, 255, 255);
    } else {
      p3.fill(0, 0, 0);
    }
    p3.text(playerUnit.attack2.name, 124*Globals.scale, 130*Globals.scale);

    if (selectedAttack == 3) {
      p3.fill(255, 255, 255);
    } else {
      p3.fill(0, 0, 0);
    }
    p3.text(playerUnit.attack3.name, 124*Globals.scale, 150*Globals.scale);
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
    if (user.canMiss && !atk.alwaysHit && p3.random(100) > atk.accuracy/user.accuracyDivisor) {
      displayPhase = 0;
      curDisplay = new BattleDisplay(toRemove, atk.name, true, false, false, (user==playerUnit), nextAttackState);
      currentState = BattleState.ATKDISPLAY;
      displayAttack();
      return;
    }
    if (user.canMiss && !atk.alwaysHit && p3.random(100) < target.dodge) {
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
        if (Globals.keyPressManager.getKey() == Button.A) {
          Globals.keyPressManager.clearBuffer();
          curDisplay.breaking = false;
        }
        p3.textFont(Globals.textDisplayFont);
        p3.fill(0, 0, 0);
        p3.text(battleTextDisplay.sb.toString(), 4*Globals.scale, 130*Globals.scale);
        return;
      } else {
        battleTextDisplay.process();
        p3.textFont(Globals.textDisplayFont);
        p3.fill(0, 0, 0);
        p3.text(battleTextDisplay.sb.toString(), 4*Globals.scale, 130*Globals.scale);
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
          playerUnit.currentHP = UniGame.min(playerUnit.currentHP-curDisplay.selfDamage, playerUnit.maxHP);
          break;
        } else if (curDisplay.selfDamage > 0) {
          s+="You deal "+curDisplay.selfDamage+" damage to yourself."; 
          playerUnit.currentHP = UniGame.max(playerUnit.currentHP-curDisplay.selfDamage, 0);
          break;
        }
      case 3:
        ++curDisplay.displayState;
        if (curDisplay.targetDamage != 0) {
          s+="You hit "+enemy.name+" for "+curDisplay.targetDamage+" damage!";
          enemy.currentHP = UniGame.max(enemy.currentHP-curDisplay.targetDamage, 0); 
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
          enemy.currentHP = UniGame.min(enemy.currentHP-curDisplay.selfDamage, enemy.maxHP);
          break;
        } else if (curDisplay.selfDamage > 0) {
          s+=enemy.name+" deals "+curDisplay.selfDamage+" damage to themself."; 
          enemy.currentHP = UniGame.max(enemy.currentHP-curDisplay.selfDamage, 0);
          break;
        }
      case 3:
        ++curDisplay.displayState;
        if (curDisplay.targetDamage != 0) {
          s+=enemy.name+" hits you for "+curDisplay.targetDamage+" damage!";
          playerUnit.currentHP = UniGame.max(playerUnit.currentHP-curDisplay.targetDamage, 0); 
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

    battleTextDisplay = new BattleTextDisplay(s, p3);
    curDisplay.breaking= true;
    return;
  }

  public void finishBattle() {
    battleFinished = true;
    if(playerUnit.currentHP == 0){
      battleTextDisplay = new BattleTextDisplay("You run out of stamina! "+enemy.name+" wins the battle!", p3);
    }else{
      battleTextDisplay = new BattleTextDisplay(enemy.name+" is out of stamina! You win!", p3); 
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
