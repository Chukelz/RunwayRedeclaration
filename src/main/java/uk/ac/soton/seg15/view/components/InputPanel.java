package uk.ac.soton.seg15.view.components;

import com.sun.javafx.scene.control.Logging;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import uk.ac.soton.seg15.model.Parameters;
import uk.ac.soton.seg15.model.Runway;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.seg15.model.Obstacle;
import uk.ac.soton.seg15.view.View;
import uk.ac.soton.seg15.view.scenes.MainScene;
import uk.ac.soton.seg15.view.events.CalculateButtonListener;
import uk.ac.soton.seg15.model.Calculate;

public class InputPanel extends VBox {

    private static final Logger logger = LogManager.getLogger(InputPanel.class);
    private final List<Obstacle> obstacles;
    private StringProperty scenarioType = new SimpleStringProperty();
    private final BooleanProperty toggleBreakdown = new SimpleBooleanProperty(false);
    private ParameterField obstHeight;
    private ParameterField obstWidth;
    private ParameterField blastProtection;
    private ParameterField leftThreshDist;
    private ParameterField rightThreshDist;
    private ParameterField stripEnd;
    private ParameterField obsName;
    private ParameterField distcentreline;
    private Button reDeclare;
    private Button breakdown;
    private Obstacle selectedObstacle;
    private int direction = 1;

    public Runway runway;

    private View view;

    private CalculateButtonListener buttonClickedListener;
    private ComboBox<String> obstList;

    public InputPanel(View view){
        this.view = view;

        this.setSpacing(10);
        this.setPadding(new Insets(5,5,5,5));
        this.setAlignment(Pos.CENTER);

        obstacles = new ArrayList<>();
        if(Obstacle.obstacleArray() != null){
            obstacles.addAll(Arrays.asList(Obstacle.obstacleArray()));
        }


        buildCalcChoices();
        if(view.getAirport() != null) {
            if(view.getAirport().getObstacles() != null){
                obstacles.addAll(view.getAirport().getObstacles());
            }
        }

        buildObstacleChoices();
        buildInputFields();
        buildButtons();
    }

    /**
     * Builds and sets the action for the choose type of calculation
     */
    private void buildCalcChoices(){

        String calcTypes[] = {"Landing Over", "Landing Toward", "TakeOff Away", "TakeOff Toward"};

        ComboBox calcType = new ComboBox(FXCollections.observableArrayList(calcTypes));
        calcType.setPromptText("Calculation type");
        calcType.setOnAction((EventHandler<ActionEvent>) event -> {
            switch (calcType.getValue().toString()) {
                case "Landing Over":
                    view.showNotification("Landing Over...");
                    scenarioType.set("Landing Over");
                    break;
                case "Landing Toward":
                    view.showNotification("Landing Toward...");
                    scenarioType.set("Landing Toward");
                    break;
                case "TakeOff Away":
                    view.showNotification("Takeoff Away...");
                    scenarioType.set("TakeOff Away");
                    break;
                case "TakeOff Toward":
                    view.showNotification("Takeoff Toward...");
                    scenarioType.set("TakeOff Toward");
                    break;

            }
        });

        this.getChildren().add(calcType);

    }

    //this method is for the predefined obstacles
    private void buildObstacleChoices() {
        obstList = new ComboBox(FXCollections.observableList(obstacles.stream()
                                                                .map(x -> x.getName())
                                                                .collect(Collectors.toList())));
        obstList.getItems().add("Add new obstacle...");
        obstList.setMaxWidth(this.getMaxWidth());
        obstList.setPromptText("Obstacles");

        obstList.setOnAction(event -> {
            if(obstList.getValue().equals("Add new obstacle...")) {
                createNewObstacle();
                return;
            }

            var obstacle = obstacles.stream()
                    .filter(x -> obstList.getValue().equals(x.getName()))
                    .findFirst().get();
            view.setObstacle(obstacle);
            view.showNotification(obstacle.getName() + " selected");
            updateInputFields();
        });


        this.getChildren().add(obstList);

    }

    /**
     * Builds the input fields for the input panel and sets the prompt text
     */
    private void buildInputFields(){
        obstHeight = new ParameterField("Obstacle Height", "m", -Double.MAX_VALUE, Double.MAX_VALUE);

        obstWidth = new ParameterField("Obstacle Width","m", 0,Double.MAX_VALUE);

        blastProtection = new ParameterField("Blast Protection","m", 0,Double.MAX_VALUE);
        blastProtection.setText("300");

        leftThreshDist = new ParameterField("Left Threshold","m", -Double.MAX_VALUE,Double.MAX_VALUE);

        rightThreshDist = new ParameterField("Right Threshold","m", -Double.MAX_VALUE,Double.MAX_VALUE);

        stripEnd = new ParameterField("Strip End","metres", 0, Double.MAX_VALUE);
        stripEnd.setText("60");

        distcentreline = new ParameterField("Distance to Centre Line","m",-Double.MAX_VALUE,Double.MAX_VALUE);
        distcentreline.setText("0");

        obsName = new ParameterField("Obstacle name");

        this.getChildren().addAll(obstHeight, obstWidth, blastProtection, leftThreshDist, rightThreshDist, stripEnd,distcentreline,obsName);

    }

    private void createNewObstacle(){
        var obstacleCreation = new NewObstacle(300, 600, view);
        logger.info("Creating");
        obstacleCreation.setOnHidden(e -> {
            obstList.setValue(obstList.getItems().get(obstList.getItems().size() - 2));
        });
    }

    /**
     * Builds and sets the actions of the recalculate button and breakdown button
     */
    private void buildButtons(){
        reDeclare = new Button("Recalculate");
        reDeclare.setOnAction(event -> {
            recalculateClick();

        });
        this.getChildren().add(reDeclare);


    }

    private void updateInputFields() {
        Obstacle obstacle = obstacles.stream()
            .filter(x -> obstList.getValue().equals(x.getName()))
            .findFirst().get();
        obstHeight.setText(Double.toString(obstacle.getHeight()));
        obstWidth.setText(Double.toString(obstacle.getWidth()));
        leftThreshDist.setText(Double.toString(obstacle.getDistanceFromLeftThreshold()));
        var rtd = view.getRunway().getParameters().getTora() - obstacle.getDistanceFromLeftThreshold();
        rightThreshDist.setText(Double.toString(rtd));
        obsName.setText(obstacle.getName());

    }
    /**
     * Gets the type of scenario
     *
     * @return the type of scenario
     */
    public StringProperty getScenarioType(){
        return scenarioType;
    }

    /**
     * Called when the recalculate button is clicked. Will send data from text boxes
     * if they are not null, to the view to send to the controller.
     */
    private void recalculateClick(){
        view.finalNotiUpdate("Values Changed: ");
        if(scenarioType.isNull().get()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Select an Scenario Type");
            alert.show();
            return;
        }

        if(obstList.getValue() == null || !obstacles.stream().anyMatch(x -> obstList.getValue().equals(x.getName()))) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Select an obstacle");
            alert.show();
            return;
        }

        if (!obstWidth.getText().isEmpty()){
            //send width update via view
            view.obstWidthUpdate(obstWidth.getText());
        }
        if (!obstHeight.getText().isEmpty()){
            //send width update via view
            view.obstHeightUpdate(obstHeight.getText());
        }
        if (!blastProtection.getText().isEmpty()){
            //send width update via view
            view.blastProtectUpdate(blastProtection.getText());
        }
        if (!leftThreshDist.getText().isEmpty()){
            //send width update via view
            view.leftThreshUpdate(leftThreshDist.getText());
        }
        if (!rightThreshDist.getText().isEmpty()){
            //send width update via view
            view.rightThreshUpdate(rightThreshDist.getText());
        }
        if (!stripEnd.getText().isEmpty()){
            //send width update via view
            view.stripEndUpdate(stripEnd.getText());
        }
        if (!obsName.getText().isEmpty()){
            //send width update via view
            view.obsNameUpdate(obsName.getText());
        }
        if (!distcentreline.getText().isEmpty()){
            //send width update via view
            view.distCentreLineUpdate(distcentreline.getText());
        }
        view.calculate(scenarioType.getValue(), direction);
        if (view.getFinalNoti() != "Values Changed: ") {
            view.showNotification(view.getFinalNoti());
        }
        buttonClicked(new ActionEvent());
    }

    /**
     * Called when breakdown button is clicked. Will send request to main scene
     * to show the breakdown of calculations.
     */
    public void setOnButtonClicked(CalculateButtonListener listener) {
        this.buttonClickedListener = listener;
    }

    private void buttonClicked(ActionEvent event) {
        if (buttonClickedListener != null) {
            buttonClickedListener.calculateButtonClicked(event);

            toggleBreakdown.set(true);
        }
    }

    public void switchDirection(int dir){direction = dir;}

    public void updateObstacleList(Obstacle obstacle){
        obstacles.add(obstacle);
        obstList.getItems().remove("Add new obstacle...");
        obstList.getItems().add(obstacle.getName());
        obstList.getItems().add("Add new obstacle...");
        obstList.setValue(obstacle.getName());

    }

    public BooleanProperty getToggleBreakdown(){return  toggleBreakdown;}

}
