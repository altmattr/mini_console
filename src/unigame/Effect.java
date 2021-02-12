package unigame;

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
