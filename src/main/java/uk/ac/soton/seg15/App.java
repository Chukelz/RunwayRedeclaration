package uk.ac.soton.seg15;


import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.seg15.controller.Controller;

public class App extends Application {

  private static final Logger logger = LogManager.getLogger(App.class);


  /**
   * Window height
   */
  private final int winHeight = 800;
  /**
   * Window width
   */
  private final int winWidth = 1400;

  private Controller controller;

  /**
   * Start app
   * @param args (not required)
   */
  public static void main(String[] args) {
    logger.info("Launching app");
    launch();
  }

  /**
   * App begins by setting up the stage and opening window
   * @param primaryStage the main window
   */
  @Override
  public void start(Stage primaryStage){
    logger.info("Opening window");

    primaryStage.setTitle("Runway Redeclaration");
    primaryStage.setMinWidth(winWidth);
    primaryStage.setMinHeight(winHeight);
//    primaryStage.setMaxWidth(winWidth);
//    primaryStage.setMaxHeight(winHeight);
//    primaryStage.setHeight(winHeight);
//    primaryStage.setWidth(winWidth);

    primaryStage.show();

    controller = new Controller(primaryStage);

  }
}
