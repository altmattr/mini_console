package unigame;

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