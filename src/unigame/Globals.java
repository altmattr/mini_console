package unigame;

import processing.core.*; 

public class Globals{
    public static Player player;
    public static Attacks attacks;
    public static final String assetspath = "unigame/";
    //edit this to change the scale the game runs at. 1 is 260*160 and each increase causes pixels to become that size
    //(i.e at scale 2 each pixel is rendered as 2x2, at scale 3 each pixel is rendered at 3x3, etc.)
    public static final int scale = 4;

    public static GameStateManager gameStateManager;
    public static TextManager textManager;
    public static OverworldManager world;
    public static BattleManager battleManager;
    public static FadeManager fadeManager;
    public static KeyPressManager keyPressManager;
    public static BattleUnits battleUnits;

    public static final String battleFontPath = Globals.assetspath+"Fonts/kongtext.ttf";
    public static final String textFontPath = Globals.assetspath+"Fonts/dogicapixel.ttf";
    // public static final String playerType = "red"; // TODO: not used when there is a dialog
    public static PFont battleUIFont;
    public static PFont textDisplayFont;

}