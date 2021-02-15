package unigame;

class BattleTextDisplay {
  String s;
  int currentCharacter = 0;
  int currentLine = 0;
  final int lineLength = 30;
  int currentOffset = 0;
  final int stringBuilderDefaultSize = lineLength*2+1;
  boolean breaking = false;
  StringBuilder sb;

  public BattleTextDisplay(String s, UniGame p3) {
    this.s = s; 
    p3.fill(0, 0, 0);
    sb = new StringBuilder(stringBuilderDefaultSize);
  }


  public void process() {
    //if we're breaking, we're no longer breaking
    //and we need to clear sb
    if (Globals.keyPressManager.getKey() == Button.A) {
      while (currentCharacter != s.length()) nextChar();
      Globals.keyPressManager.clearBuffer();
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
