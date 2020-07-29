package mqapp;

import java.io.*;

public class Globals{
	public static void setCurrApp(String v){try{FileWriter fw = new FileWriter("currApp.txt"); fw.write(v); fw.close();}catch(Exception e){}};
	public static void setNextApp(String v){try{FileWriter fw = new FileWriter("nextApp.txt"); fw.write(v); fw.close();}catch(Exception e){}}
	public static String getCurrApp(){try{return new BufferedReader(new FileReader("currApp.txt")).readLine();}catch(Exception e){return "mqapp.ApplicationChooser";}}
	public static String getNextApp(){try{return new BufferedReader(new FileReader("nextApp.txt")).readLine();}catch(Exception e){return "mqapp.ApplicationChooser";}}
}