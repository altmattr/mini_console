package unigame;

import processing.core.*; 

import java.util.LinkedList; 

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
    int maxHP, int speed, int defense, int dodge, UniGame p3) {
    this.name = name;
    this.appearance = p3.upscale(appearance);
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