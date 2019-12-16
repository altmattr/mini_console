import javafx.stage.Screen;

public class AskAboutScreen extends javafx.application.Application {
  public static void main(String[] args){
	  launch(args);
  }

  public void start(javafx.stage.Stage stage) throws Exception {
	  System.out.println(Screen.getPrimary().getVisualBounds());
	  System.exit(0);
  }
}
