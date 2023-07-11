package uk.ac.soton.seg15.view.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class KeyComponent extends HBox {
  public KeyComponent(Color color, String caption, double width, double height){
    this.setSpacing(10);
    this.setAlignment(Pos.CENTER);

    Rectangle rect = new Rectangle(width, height);
    rect.setFill(color);

    Label text = new Label(caption);

    this.getChildren().addAll(rect, text);
  }
}
