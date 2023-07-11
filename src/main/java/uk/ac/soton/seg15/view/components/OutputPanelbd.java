package uk.ac.soton.seg15.view.components;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;

import javafx.scene.text.Font;

import javafx.geometry.Insets;
import javafx.scene.control.Button;

import javafx.scene.layout.VBox;

import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.seg15.model.Obstacle;
import uk.ac.soton.seg15.model.Runway;
import uk.ac.soton.seg15.view.View;


public class OutputPanelbd extends VBox{
    private final Logger logger = LogManager.getLogger(OutputPanelbd.class);
    private View view;
    private Text TORA;
    private Text TORACalc;
    private Text TORAOriginal;
    private Text ASDA;
    private Text ASDACalc;
    private Text ASDAOriginal;
    private Text TODA;
    private Text TODACalc;
    private Text TODAOriginal;
    private Text LDA;
    private Text LDACalc;
    private Text LDAOriginal;
    private TableView<RunwayParam> table;
    private TableColumn <RunwayParam, String> column;
    private TableColumn <RunwayParam, Double> column2;
    private TableColumn <RunwayParam, Double> column3;


    private BooleanProperty toggleBreakdown = new SimpleBooleanProperty();

    public OutputPanelbd(View view){
        this.view = view;
        this.setSpacing(30);
        this.setPadding(new Insets(5,5,5,5));
        this.setMaxWidth(200);
        Button back = new Button("Back to Input Panel");
        back.setOnAction(x -> toggleBreakdown.set(false));
        table = new TableView<>();


        this.getChildren().addAll(back, table);

        calculationOutput();
    }

    private void calculationOutput(){
        this.TORACalc = new Text("TORA Calculation: \n");
        this.ASDACalc = new Text("ASDA Calculation: \n");
        this.TODACalc = new Text("TODA Calculation: \n");
        this.LDACalc = new Text("LDA Calculation: \n");


        var showNewStage = new Button("Display breakdown");
        showNewStage.setOnAction(e -> {
            display(TORACalc, ASDACalc, TODACalc, LDACalc);
        });
        this.getChildren().add(showNewStage);
    }

    private void display(Text toraCalc, Text asdaCalc, Text todaCalc, Text ldaCalc) {
        var stage = new Stage();
        var root = new BorderPane();
        var scene = new Scene(root, 550, 200);


        TextFlow outputBreakdown = new TextFlow();
        outputBreakdown.setTabSize(100);
        outputBreakdown.getChildren().addAll(toraCalc, asdaCalc, todaCalc, ldaCalc);

        stage.setScene(scene);
        root.getChildren().add(outputBreakdown);
        stage.show();
    }

    /**
     * Updates the values of each label when values are available
     */
    public TableView<RunwayParam> updateValues(String scenarioType) {

        Runway runway = view.getRunway();
        Obstacle obstacle = view.getObstacle();



        if(scenarioType.equals("Landing Over")){
            this.TORACalc.setText("");
            this.ASDACalc.setText("");
            this.TODACalc.setText("");
            if (runway.getParameters().getResa() > (obstacle.getHeight() * 50)) {
                if (runway.getParameters().getResa() + 60 < 300) {
                    this.LDACalc.setText("LDA = LDA - Distance from Threshold - Blast Protection \n" + runway.getParameters().getLda() + "-" + obstacle.getDistanceFromLeftThreshold() + "- 300 -" + "=" + runway.getNewParameters().getLda());
                } else {
                    this.LDACalc.setText("LDA = LDA - Distance from Threshold - RESA - StripEnd \n" + runway.getParameters().getLda() + "-" + obstacle.getDistanceFromLeftThreshold() + "- 60 - " + runway.getParameters().getResa() + "=" + runway.getNewParameters().getLda());
                }

            } else {
                if ((obstacle.getHeight() * 50) + 60 < 300) {
                    this.LDACalc.setText("LDA = LDA - Distance from Threshold - Blast Protection \n" + runway.getParameters().getLda() + "-" + obstacle.getDistanceFromLeftThreshold() + "- 300 -" + "=" + runway.getNewParameters().getLda());
                } else {
                    this.LDACalc.setText("LDA = LDA - Distance from Threshold - StripEnd - Slope Calculation \n" + runway.getParameters().getLda() + "-" + obstacle.getDistanceFromLeftThreshold() + "- 60 - " + (obstacle.getHeight() * 50) + "=" + runway.getNewParameters().getLda());
                }
            }

        } else if (scenarioType.equals("Landing Toward")){
            this.TORACalc.setText("");
            this.ASDACalc.setText("");
            this.TODACalc.setText("");
            this.LDACalc.setText("LDA = Distance from Threshold - RESA - StripEnd \nLDA = " + obstacle.getDistanceFromRightThreshold() + " - " + runway.getParameters().getResa() + " - 60 = " + runway.getNewParameters().getLda() + "\n");

        } else if (scenarioType.equals("TakeOff Toward")){
            if (runway.getParameters().getResa() > (obstacle.getHeight() * 50)) {
                this.TORACalc.setText("TORA = Displaced Threshold + Distance from Threshold - RESA - StripEnd \nTORA = " + runway.getThreshold() + " + " + obstacle.getDistanceFromRightThreshold() + " - " + runway.getParameters().getResa() + " - 60 = " + runway.getNewParameters().getTora() + "\n");
            }
            else{
                this.TORACalc.setText("TORA = Displaced Threshold + Distance from Threshold - Slope Calculation - StripEnd \nTORA = " + runway.getThreshold() + " + " + obstacle.getDistanceFromRightThreshold() + " - " + (obstacle.getHeight() * 50) + " - 60 = " + runway.getNewParameters().getTora() + "\n");
            }
            this.ASDACalc.setText("ASDA = (R) TORA \nASDA = " + runway.getNewParameters().getTora() + "\n");
            this.TODACalc.setText("TODA = (R) TORA \nTODA = " + runway.getNewParameters().getTora() + "\n");
            this.LDACalc.setText("");

        } else if (scenarioType.equals(("TakeOff Away"))){
            if (runway.getThreshold() != 0.0) {
                this.TORACalc.setText("TORA = TORA - Blast Protection - Distance from Threshold - Displaced threshold \nTORA = " + runway.getParameters().getTora() + " - 300 - " + obstacle.getDistanceFromLeftThreshold() + " - " + runway.getThreshold() + " = " + runway.getNewParameters().getTora() + "\n");
            }
            else{
                this.TORACalc.setText("TORA = TORA - RESA - StripEnd - Distance from Threshold \n TORA = " + runway.getParameters().getTora() + "-" + runway.getParameters().getResa() + "- 60 - " + obstacle.getDistanceFromLeftThreshold() + "=" + runway.getNewParameters().getTora() + "\n");
            }
            this.TODACalc.setText("TODA = (R) TORA + Clearway \nTODA = " + runway.getNewParameters().getTora() + " + " + runway.getParameters().getClearway() + " = " + runway.getNewParameters().getToda() + "\n");
            this.ASDACalc.setText("ASDA = (R) TORA + Stopway \nASDA = " + runway.getNewParameters().getTora() + " + " + runway.getParameters().getStopway() + " = " + runway.getNewParameters().getAsda() + "\n");
            this.LDACalc.setText("");

        }
        table.setPrefHeight(125);


        column = new TableColumn<>("Name");
        column.setCellValueFactory(new PropertyValueFactory<>("name"));
        column.setPrefWidth(62);

        column2 = new TableColumn<>("OriginalValues");
        column2.setCellValueFactory(new PropertyValueFactory<>("originalVal"));
        column2.setPrefWidth(62);

        column3 = new TableColumn<>("RecalculatedValues");
        column3.setCellValueFactory(new PropertyValueFactory<>("recalcVal"));
        column3.setPrefWidth(62);

        table.getColumns().clear();
        table.getColumns().addAll(column, column2, column3);

        if (scenarioType.equals("Landing Over") || scenarioType.equals("Landing Toward")){
            ObservableList<RunwayParam> data = FXCollections.observableArrayList(
                    new RunwayParam("ASDA", runway.getParameters().getAsda(), runway.getParameters().getAsda()),
                    new RunwayParam("TORA", runway.getParameters().getTora(), runway.getParameters().getTora()),
                    new RunwayParam("TODA", runway.getParameters().getToda(), runway.getParameters().getToda()),
                    new RunwayParam("LDA", runway.getParameters().getLda(), runway.getNewParameters().getLda()));
            table.setItems(data);
        }
        if (scenarioType.equals("TakeOff Away") || scenarioType.equals("TakeOff Toward")){
            ObservableList<RunwayParam> data = FXCollections.observableArrayList(
                    new RunwayParam("ASDA", runway.getParameters().getAsda(), runway.getNewParameters().getAsda()),
                    new RunwayParam("TORA", runway.getParameters().getTora(), runway.getNewParameters().getTora()),
                    new RunwayParam("TODA", runway.getParameters().getToda(), runway.getNewParameters().getToda()),
                    new RunwayParam("LDA", runway.getParameters().getLda(), runway.getParameters().getLda()));
            table.setItems(data);
        }



        logger.info(runway.getParameters().getAsda());
        return table;
    }

    public BooleanProperty getToggleBreakdown(){return toggleBreakdown;}
}
