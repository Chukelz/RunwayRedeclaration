package uk.ac.soton.seg15.view.components;

import java.text.MessageFormat;
import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.seg15.model.ColorBlindness;

/**
 * The Compass that displays the angle of the runway in the world
 */
public class CompassView extends Group {

  private static final Logger logger = LogManager.getLogger(CompassView.class);
  private final GraphicsContext backgroundGC;
  private final GraphicsContext arrowGC;
  private final double width;
  private final double height;
  private final Affine originalArrowTransform;
  private final Canvas toggleCanvas;
  private boolean toggle = true;


  public CompassView(double width, double height, double startingDegree){
    this.setOpacity(0.75);
    prefHeight(height);
    var btnWidth = 50;
    prefWidth(width + btnWidth);
    this.width = width;
    this.height = height;
    var bp = new BorderPane();

    //Compass toggle
    toggleCanvas = new Canvas(btnWidth, height);
    toggleCanvas.getGraphicsContext2D().strokeLine(0,0, toggleCanvas.getWidth(), toggleCanvas.getHeight()/2);
    toggleCanvas.getGraphicsContext2D().strokeLine(0,toggleCanvas.getHeight(), toggleCanvas.getWidth(), toggleCanvas.getHeight()/2);
    toggleCanvas.setScaleX(0.5);
    toggleCanvas.setScaleY(0.5);

    TranslateTransition tt = new TranslateTransition(Duration.millis(1000), this);
    tt.setOnFinished(event -> toggleCanvas.setDisable(false));
    toggleCanvas.setOnMouseClicked(event -> {
      toggleCanvas.setDisable(true);
      if(this.toggle){
        tt.setByX(width);
        tt.play();
      } else {
        tt.setByX(-width);
        tt.play();
      }
      this.toggle = !this.toggle;
      changeToggleText();
    });
    bp.setLeft(toggleCanvas);

    //Canvas setup
    var pane = new StackPane();
    Canvas background = new Canvas(width, height);
    Canvas arrow = new Canvas(width, height);
    backgroundGC = background.getGraphicsContext2D();
    arrowGC = arrow.getGraphicsContext2D();
    originalArrowTransform = arrowGC.getTransform();
    pane.getChildren().addAll(background, arrow);

    bp.setCenter(pane);
    this.getChildren().add(bp);

    build(startingDegree);
  }

  private void build(double degree){
    arrowGC.setTransform(originalArrowTransform);
    arrowGC.transform(new Affine(new Rotate(degree, width/2, height/2)));

    arrowGC.clearRect(0, 0, width, height);
    backgroundGC.clearRect(0, 0, width, height);


    backgroundGC.setTextAlign(TextAlignment.CENTER);
    backgroundGC.setFont(new Font(Font.getDefault().getName(), 16));
    //Drawing Compass layout
    backgroundGC.setFill(ColorBlindness.daltonizeCorrect(Color.SEASHELL));
    backgroundGC.fillArc(0,0,width, height, 0, 360, ArcType.ROUND);
    backgroundGC.setFill(ColorBlindness.daltonizeCorrect(Color.DARKGRAY));
    backgroundGC.fillText("N", width/2, backgroundGC.getFont().getSize());
    backgroundGC.fillText("E", width - backgroundGC.getFont().getSize()/2, height/2);
    backgroundGC.fillText("S", width/2 , height - backgroundGC.getFont().getSize()/2);
    backgroundGC.fillText("W", backgroundGC.getFont().getSize()/2, height/2);

    //Drawing arrow
    arrowGC.setFill(ColorBlindness.daltonizeCorrect(Color.DARKGRAY));
    var yOffset = backgroundGC.getFont().getSize();
    var xStart = width/2;
    var yStart = yOffset;
    var yEnd = height - yOffset;
    var arrowHeadLength = 10;

    arrowGC.strokeLine(xStart, yStart + arrowHeadLength, xStart, yEnd - arrowHeadLength);

    //top arrow head
    arrowGC.fillPolygon(new double[]{xStart - 5, xStart, xStart + 5},
        new double[]{yStart + arrowHeadLength, yStart, yStart+arrowHeadLength},
        3);

  }

  public void update(int compassHeading){
    build(compassHeading*10);
  }

  private void changeToggleText(){
    var gc = toggleCanvas.getGraphicsContext2D();
    gc.clearRect(0,0, toggleCanvas.getWidth(), toggleCanvas.getHeight());
    if(toggle){
      gc.strokeLine(0,0, toggleCanvas.getWidth(), toggleCanvas.getHeight()/2);
      gc.strokeLine(0,toggleCanvas.getHeight(), toggleCanvas.getWidth(), toggleCanvas.getHeight()/2);
    } else {
      gc.strokeLine(0,toggleCanvas.getHeight()/2, toggleCanvas.getWidth(), 0);
      gc.strokeLine(0,toggleCanvas.getHeight()/2, toggleCanvas.getWidth(), toggleCanvas.getHeight());
    }
  }




}
