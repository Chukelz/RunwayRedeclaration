package uk.ac.soton.seg15.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.seg15.model.ColorBlindness;

public class KeyBox extends VBox {
  private static final Logger logger = LogManager.getLogger(KeyBox.class);
  private final KeyComponent lda;
  private final KeyComponent tora;
  private final KeyComponent toda;
  private final KeyComponent asda;
  private final KeyComponent stripEnd;
  private final KeyComponent obstacle;
  private Boolean showKey = false;

  private HBox hbox;

  public KeyBox(double width, double height){
    this.setSpacing(10);
    this.setAlignment(Pos.CENTER);
    this.setPadding(new Insets(10,10,10,10));
    this.getStyleClass().add("keyBox");
    this.setMaxHeight(height);
    this.setMaxWidth(width);
    this.setCursor(Cursor.HAND);

    Button dragabble = new Button();
    //dragabble.getStylesheets().add("keyButton");
    ImageView img = new ImageView(new Image("draggable.png"));
    img.setFitHeight(20);
    img.setPreserveRatio(true);
    dragabble.setGraphic(img);

    Button toggle = new Button("Show Key");
    toggle.getStyleClass().add("keyButton");
    toggle.setOnMouseClicked(event -> {
      if(showKey)
        toggle.setText("Show Key");
      else
        toggle.setText("Hide Key");

      toggleVisibility();
      showKey = !showKey;

    });

    lda = new KeyComponent(ColorBlindness.daltonizeCorrect(Color.DARKGREEN), "LDA", 10, 10);
    tora = new KeyComponent(ColorBlindness.daltonizeCorrect(Color.BLUE), "TORA", 10, 10);
    toda = new KeyComponent(ColorBlindness.daltonizeCorrect(Color.ORANGERED), "TODA", 10, 10);
    asda = new KeyComponent(ColorBlindness.daltonizeCorrect(Color.PURPLE), "ASDA", 10, 10);
    stripEnd = new KeyComponent(ColorBlindness.daltonizeCorrect(Color.GOLD), "Strip End", 10, 10);
    obstacle = new KeyComponent(ColorBlindness.daltonizeCorrect(Color.RED), "Obstacle", 10, 10);

    hbox = new HBox(dragabble,toggle);
    hbox.setPadding(new Insets(5,5,5,5));
    hbox.setSpacing(20);

    this.getChildren().addAll(hbox);

    dragabble.setOnMouseDragged(event -> {
      setTranslateX(this.getTranslateX() + event.getX() - 10);
      setTranslateY(this.getTranslateY() + event.getY() - 10);
      setCursor(Cursor.CLOSED_HAND);
    });
    dragabble.setOnMouseReleased(event -> setCursor(Cursor.HAND));

  }

  private void toggleVisibility() {
    if(showKey)
      getChildren().remove(1, getChildren().size());
    else
      getChildren().addAll(lda, tora, toda, asda, stripEnd, obstacle);
  }
}
