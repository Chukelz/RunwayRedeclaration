package uk.ac.soton.seg15.view.components;

import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.seg15.model.Obstacle;
import uk.ac.soton.seg15.model.Parameters;
import uk.ac.soton.seg15.model.Runway;
import uk.ac.soton.seg15.view.View;

/**
 * A window that is displayed when the user wants to add a new obstacle to the calculation
 */
public class NewObstacle extends Stage {
  private static Logger logger = LogManager.getLogger(NewObstacle.class);
  private final StackPane root;
  private final Scene scene;
  private View view;
  private VBox main;

  public NewObstacle(double width, double height, View view) {
    this.view = view;
    setResizable(false);
    initOwner(view.getStage());
    initStyle(StageStyle.UNDECORATED);
    sizeToScene();

    root = new StackPane();
    scene = new Scene(root, width, height);

    scene.getStylesheets().add(getClass().getResource("/app.css").toExternalForm());
    setScene(scene);
    build();
    this.show();
  }

  private void build(){
    main = new VBox();
    root.getChildren().add(main);

    main.setSpacing(5);
    main.setAlignment(Pos.CENTER);
    main.setPadding(new Insets(5,5,5,5));

    var obstHeight = new ParameterField("Obstacle Height", "m", -Double.MAX_VALUE, Double.MAX_VALUE);

    var obstWidth = new ParameterField("Obstacle Width","m", 0,Double.MAX_VALUE);

    var blastProtection = new ParameterField("Blast Protection","m", 0,Double.MAX_VALUE);
    blastProtection.setText("300");

    var leftThreshDist = new ParameterField("Left Threshold","m", -Double.MAX_VALUE,Double.MAX_VALUE);

    var rightThreshDist = new ParameterField("Right Threshold","m", -Double.MAX_VALUE,Double.MAX_VALUE);

    var stripEnd = new ParameterField("Strip End","m", 0, Double.MAX_VALUE);
    stripEnd.setText("60");

    var distcentreline = new ParameterField("Distance to Centre Line","m",-Double.MAX_VALUE,Double.MAX_VALUE);
    distcentreline.setText("0");

    var obsName = new ParameterField("Obstacle name");

    main.getChildren().addAll(obsName, obstHeight, obstWidth, blastProtection, leftThreshDist, rightThreshDist, stripEnd,distcentreline);

    HBox buttonsBox = new HBox();
    buttonsBox.setAlignment(Pos.CENTER);
    buttonsBox.setSpacing(10);
    var create = new Button("Create");
    var cancel = new Button("Cancel");
    buttonsBox.getChildren().addAll(create,cancel);
    main.getChildren().addAll(buttonsBox);

    create.setOnAction(e -> {
      if(!check()) return;
      Obstacle obstacle = new Obstacle(obsName.getText(), obstHeight.getValue(), obstWidth.getValue(), distcentreline.getValue(), leftThreshDist.getValue(),
          rightThreshDist.getValue());
        view.addObstacle(obstacle);
        view.obstWidthUpdate(obstWidth.getText());
        view.obstHeightUpdate(obstHeight.getText());
        view.blastProtectUpdate(blastProtection.getText());
        view.leftThreshUpdate(leftThreshDist.getText());
        view.rightThreshUpdate(rightThreshDist.getText());
        view.stripEndUpdate(stripEnd.getText());
        view.obsNameUpdate(obsName.getText());
        view.distCentreLineUpdate(distcentreline.getText());
        close();
    });

    Alert alert = new Alert(AlertType.CONFIRMATION, "Cancelling will remove changes. \nAre you sure you want to cancel?");
    cancel.setOnAction(e -> {
      alert.showAndWait()
          .filter(response -> response == ButtonType.OK)
          .ifPresent(response -> close());
    });
  }

  /**
   * input handling
   * @return
   */
  private boolean check(){
    boolean check = true;
    for (var node: main.getChildren()){
      if (node instanceof ParameterField){
        var field = (ParameterField) node;
        if (field.getText().isEmpty()) {
          check = false;
          view.showNotification("Enter value for: " + field.getName());
        }
      }
    }

    return check;
  }


}
