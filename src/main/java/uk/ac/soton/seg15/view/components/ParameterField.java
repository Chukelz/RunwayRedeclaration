package uk.ac.soton.seg15.view.components;

import java.awt.event.ActionEvent;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Custom component that creates a text field with input constraints
 * and preset prompts.
 */
public class ParameterField extends VBox {

  private static final Logger logger = LogManager.getLogger(ParameterField.class);
  private double lowerBound;
  private double upperBound;
  private TextField parameter = new TextField();
  private Label name = new Label();
  private Label unit = new Label();
  private double value = 0.0;

  /**
   * Create a parameter text field which only accepts numbers between
   * the upper and lower bound
   * @param name
   * @param unit
   * @param lowerBound
   * @param upperBound
   */
  public ParameterField(String name, String unit, double lowerBound, double upperBound) {
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;

    parameter.setPromptText("Enter the " + name);
    parameter.setOnKeyTyped(x -> checkInput());
    parameter.setOnAction(x -> checkInput());

    this.name.setText(name + ":");
    this.unit.setText(unit);

    HBox hbox = new HBox();
    hbox.getChildren().addAll(parameter, this.unit);
    getChildren().addAll(this.name, hbox);

  }

  public ParameterField(String name) {
    parameter.setFont(Font.font(Font.getDefault().getName(), 16));
    parameter.setPromptText("Enter the " + name);

    this.name.setText(name + ":");
    this.unit.setText("");

    HBox hbox = new HBox();
    hbox.getChildren().addAll(parameter, this.unit);
    getChildren().addAll(this.name, hbox);
  }


  private void checkInput(){
    try {
      if (getText().isEmpty()) {
        value = 0.0;
        return;
      }

      if(!parameter.getText().matches("-|(-?[0-9]{1,13}(\\.[0-9]*)?)"))
        throw new Exception("Not a double");

      double input;
      if (parameter.getText().equals("-"))
        input = 0;
      else
        input = Double.parseDouble(parameter.getText());
      if (!(input >= lowerBound && input <= upperBound))
        throw new Exception("Not within range");

      value = input;
    } catch (Exception e) {
      parameter.deletePreviousChar();
      logger.info(e.getMessage());
      Alert alert = new Alert(AlertType.WARNING, "Input a value between " + lowerBound + " and "+ upperBound);
      alert.show();
    }
  }

  public String getText() {return parameter.getText();}

  public void setText(String text) {parameter.setText(text);}

  public double getValue() {return value;}

  public String getName(){return name.getText();}

  public StringProperty getTextProperty() {return parameter.textProperty();}
}
