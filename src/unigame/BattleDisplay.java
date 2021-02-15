package unigame;

import java.util.*;

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
