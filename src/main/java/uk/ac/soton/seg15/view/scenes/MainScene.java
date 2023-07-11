package uk.ac.soton.seg15.view.scenes;

import java.util.NoSuchElementException;
import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.text.Text;

import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.seg15.App;
import uk.ac.soton.seg15.model.Calculate;
import uk.ac.soton.seg15.model.ColorBlindness;
import uk.ac.soton.seg15.model.Obstacle;
import uk.ac.soton.seg15.model.Runway;
import uk.ac.soton.seg15.view.View;
import uk.ac.soton.seg15.view.components.*;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class MainScene extends BaseScene{

    private InputPanel inputPanel;
    private BorderPane bp;
    private final BooleanProperty toggleBreakdown = new SimpleBooleanProperty(false);

    private StringProperty calcType = new SimpleStringProperty();

    private static final Logger logger = LogManager.getLogger(MainScene.class);

    private RunwaySettingsPanel settingsPanel;
    private ImportExport ie;
    private Notis notis;

    private StackPane viewStackPane;
    protected OutputPanelbd outputPanelbd;

    private ObjectProperty<Runway> curRunway = new SimpleObjectProperty<>();

    private ObjectProperty<Obstacle> curObstacle = new SimpleObjectProperty<>();

    private SideOnView sideView;
    private TopDownView topView;

    private BorderPane viewBP;

    private BorderPane top;

    private ScrollPane scrollInput;
    private int direction = 1;

    /**
     * true if individual view, false if simultaneously
     */
    private Boolean individualView;

    /**
     * True if on side view, false if on top down
     */
    private Boolean sideViewBool;

    private ComboBox viewType;
    private CompassView compassView;
    private TextField field;
    private ComboBox<String> runwayCB;
    private ObservableList<Runway> runwayList;

    private SimpleDoubleProperty stageWidth = new SimpleDoubleProperty();

    private SimpleDoubleProperty stageHeight = new SimpleDoubleProperty();


    public MainScene(StackPane root, Color color, View view, ObjectProperty runway, ObjectProperty obstacle, Stage stage) {
        super(root,color, view, stage);

            individualView = false;
            sideViewBool = true;

            bp = new BorderPane();
            root.getChildren().add(bp);

            curRunway.bindBidirectional(runway);
            curObstacle.bindBidirectional(obstacle);

        //stage.minHeightProperty().bind(this.heightProperty());
        //stage.minWidthProperty().bind(this.widthProperty());

        stageWidth.bind(view.getStage().widthProperty());
        stageHeight.bind(view.getStage().heightProperty());

        stageWidth.addListener(newVal ->build());
        stageHeight.addListener(newVal ->build());

    }

    /**
     * Screen sections as fractions (width height):
     * Left(1/5,8/10)
     * Centre(4/5,8/10)
     * Top(1,2/10)
     */
    @Override
    public void build(){
        //Top side: Back, Display settings, Import/Export,Runway settings
        buildSettingsPanel();
        buildImpExpPanel();
        buildnotipanel();
        buildTopHBox();

        //Left side: Input and output
        inputPanel = new InputPanel(view);
        inputPanel.getToggleBreakdown().bindBidirectional(toggleBreakdown);
        scrollInput = new ScrollPane(inputPanel);
        scrollInput.setPrefWidth(stageWidth.getValue()/5);
        scrollInput.setPrefHeight((stageHeight.getValue()*11)/12);
        logger.info(scrollInput.getHeight());
        bp.setLeft(scrollInput);

        outputPanelbd = new OutputPanelbd(view);
        outputPanelbd.getToggleBreakdown().bindBidirectional(toggleBreakdown);

        toggleBreakdown.addListener(x -> toggleBreakdownCalculations());

        //Centre: Display
        buildViewPanel();

        //Listeners and event handlers
        curRunway.addListener(((observable, oldValue, newValue) -> {
            view.calculate(calcType.get(), direction);
            newValues();
            updateViews();
        } ));

        curObstacle.addListener(((observable, oldValue, newValue) -> {
            view.calculate(calcType.get(), direction);
            newValues();
            updateViews();
        } ));

        inputPanel.setOnButtonClicked(event -> {
            newValues();
            updateViews();
        });

        calcType.bindBidirectional(inputPanel.getScenarioType());
        calcType.addListener((observableValue, s, t1) -> {
            view.calculate(calcType.get(), direction);
            updateViews();
        });

        settingsPanel.setOnButtonClicked(event -> {
            view.calculate(calcType.get(), direction);
            newValues();
            updateViews();
        });

        /**
        if(!runwayCB.getItems().isEmpty()) {
            runwayCB.setValue(runwayCB.getItems().get(0));
            runwayCB.getOnAction().handle(new ActionEvent());
        }*/
    }


    private void newValues() {
//        logger.info("toggle breakdown: " + toggleBreakdown.get());
        if(toggleBreakdown.get()) {
            this.outputPanelbd.updateValues(calcType.get());
            bp.setLeft(outputPanelbd);
        } else {
            bp.setLeft(scrollInput);}
    }

    /**
     * Initialises the Settings panel
     */
    private void buildSettingsPanel(){
        settingsPanel = new RunwaySettingsPanel(view);
        settingsPanel.setPadding(new Insets(5,5,5,5));

        HBox.setHgrow(settingsPanel,Priority.ALWAYS);
    }

    private void buildViewPanel(){
        var width = (stageWidth.getValue()*4)/5;
        var height= (stageHeight.getValue()*11)/12;
        viewStackPane = new StackPane();
        bp.setCenter(viewStackPane);

        //---Display accessories---
        //Colour keys
        KeyBox keyBox = new KeyBox(200, 10);

        //Compass
        compassView = new CompassView(width/10, width/10, view.getRunway().getHeading() * 10);

        //---Main View---
        viewBP = new BorderPane();
        viewStackPane.getChildren().add(viewBP);
        BackgroundFill fill = new BackgroundFill(ColorBlindness.daltonizeCorrect(Color.LIGHTGREEN), CornerRadii.EMPTY, new Insets(0,0,0,0));
        viewBP.setBackground(new Background(fill));

        if(individualView == false){
            sideView = new SideOnView(width,height/2,inputPanel.getScenarioType(),view);
            topView = new TopDownView(width,height/2,inputPanel.getScenarioType(),view);

            viewBP.setTop(topView);
            viewBP.setBottom(sideView);

            BorderPane.setMargin(topView, new Insets(0,0,10,0));
            StackPane.setAlignment(compassView, Pos.CENTER_RIGHT);

        } else {
            if(sideViewBool == true){
                sideView = new SideOnView(width,height,inputPanel.getScenarioType(),view);
                viewBP.setTop(sideView);
                BorderPane.setMargin(sideView, new Insets(0,0,10,0));
            } else {
                topView = new TopDownView(width,height,inputPanel.getScenarioType(),view);
                viewBP.setTop(topView);
                BorderPane.setMargin(topView, new Insets(0,0,10,0));
            }
            StackPane.setAlignment(compassView, Pos.BOTTOM_RIGHT);
        }


        if(calcType.get() != null){
            view.calculate(calcType.get(), direction);
            newValues();
            updateViews();
        }

        viewStackPane.getChildren().addAll(keyBox, compassView);
        StackPane.setAlignment(keyBox, Pos.TOP_RIGHT);

    }

    private void buildImpExpPanel(){
        ie = new ImportExport(view);
        ie.setPadding(new Insets(5,5,5,5));
        HBox.setHgrow(ie,Priority.ALWAYS);

    }

    private void buildnotipanel(){
        notis = new Notis(view);
        notis.setPadding(new Insets(5,5,5,5));
        HBox.setHgrow(notis,Priority.ALWAYS);
    }

    /**
     * Builds the topHBox that stores the back button, the choose runway button and the runway settings button.
     */
    private void buildTopHBox(){
        Button backButton = new Button("<-");
        backButton.setOnAction(x -> goBack());

        //Runway manipulation
        HBox runways = new HBox();
        runways.setAlignment(Pos.CENTER);
        runways.setSpacing(5);

        Button notii = new Button("Noti History");
        notii.setOnAction(event -> toggleNotiSettings());
        runways.getChildren().addAll(notii);

        //Importing and Exporting
        Button impexp = new Button("Import and Export");
        impexp.setOnAction(event -> toggleImpSettings());
        runways.getChildren().addAll(impexp);

        //Runway ComboBox
        runwayList = FXCollections.observableList(view.getAirport() == null ?
                Arrays.stream(Runway.runwayArray()).collect(Collectors.toList()) :
                view.getAirport().getRunways());

        runwayCB = new ComboBox<>(FXCollections.observableList(runwayList.stream()
            .map(x -> x.getHeading() + x.getPosition())
            .collect(Collectors.toList()))
        );
        runwayCB.getItems().add("Add new runway...");
        runwayCB.setPromptText("Predefined Runways");
        runwayCB.setOnAction(e -> handleRunwaySelection());

        runways.getChildren().add(runwayCB);

        //Runway Settings Button
        Button settings = new Button("Runway Settings");
        settings.setOnAction(event -> toggleRunwaySettings());
        runways.getChildren().addAll(settings);


        //Display Settings
        HBox displaySettings = new HBox();
        displaySettings.setAlignment(Pos.CENTER);
        displaySettings.setSpacing(5);
        //Rotating to compass
        CheckBox rotateTopDown = new CheckBox("Rotate to Compass");
        rotateTopDown.setOnAction(event -> {
            topView.toggleCompassRotation();
            topView.rotateToCompass(view.getRunway().getHeading());
            updateViews();
        });

        //Switch Scenario Direction Button
        Button switchDirection = new Button("Switch Direction");
        switchDirection.setOnAction(event -> {
            view.showNotification("Direction is switched");
            direction = -direction;
            inputPanel.switchDirection(direction);
            topView.switchScenarioDirection(direction);
            sideView.switchDirection(direction);
            view.calculate(calcType.get(), direction);
            newValues();
            updateViews();
        });



        //Display isolation

        String[] viewTypes = {"Side-On","Top-Down","Simultaneous"};
        viewType = new ComboBox(FXCollections.observableArrayList(viewTypes));
        viewType.getSelectionModel().select(2);
        individualView = false;


        viewType.setOnAction((EventHandler<ActionEvent>) event -> {
            switch (viewType.getValue().toString()) {
                case "Side-On":
                    individualView = true;
                    sideViewBool = true;
                    rotateTopDown.setDisable(true);
                    break;
                case "Top-Down":
                    individualView = true;
                    sideViewBool = false;
                    rotateTopDown.setDisable(false);
                    break;
                case "Simultaneous":
                    individualView = false;
                    rotateTopDown.setDisable(false);
                    break;
            }
            direction = 1;
            rotateTopDown.setSelected(false);
            buildViewPanel();
            bp.setCenter(viewStackPane);
        });

        displaySettings.getChildren().addAll(rotateTopDown, viewType, switchDirection);

        //Top pane configurations
        top = new BorderPane();
        top.setPadding(new Insets(5,5,5,5));

        top.setPrefHeight(root.getHeight()*1/12);

        Text airportName = new Text();
        airportName.getStyleClass().add(".airport-name");
        if(view.getAirport() != null){
            airportName.setText(view.getAirport().getName());
        } else {
            airportName.setText("Test Airport");
        }

        HBox leftHbox = new HBox();
        leftHbox.setSpacing(20);
        leftHbox.setAlignment(Pos.CENTER);
        leftHbox.setPadding(new Insets(5,5,5,5));
        leftHbox.getChildren().addAll(backButton, airportName);

        top.setLeft(leftHbox);
        top.setAlignment(leftHbox, Pos.CENTER);
        top.setRight(runways);
        top.setAlignment(runways, Pos.CENTER);
        top.setCenter(displaySettings);

        bp.setTop(top);

    }

    private void handleRunwaySelection(){
        if(runwayCB.getValue() == null || runwayCB.getValue().isEmpty()) return;
        if (runwayCB.getValue().equals("Add new runway...")){
            createNewRunway();
            return;
        }

        Runway val = runwayList.stream()
                            .filter(runway -> runwayCB.getValue().equals(runway.getHeading() + runway.getPosition()))
                            .findFirst().get();
        view.asdaUpdate(Double.toString(val.getParameters().getAsda()));
        view.todaUpdate(Double.toString(val.getParameters().getToda()));
        view.toraUpdate(Double.toString(val.getParameters().getTora()));
        view.ldaUpdate(Double.toString(val.getParameters().getLda()));
        view.resaUpdate(Double.toString(val.getParameters().getResa()));
        view.headingUpdate(Integer.toString(val.getHeading()));
        view.positionUpdate(val.getPosition());
        view.threshUpdate(Double.toString(val.getThreshold()));
        view.showNotification(val.getHeading() + val.getPosition() + " selected");
        logger.info("Parameters: " + curRunway.get().getNewParameters());

        int curRunwayHeading = curRunway.get().getHeading();
        int reciprocalHeading = curRunwayHeading <= 18 ? curRunwayHeading + 18 : curRunwayHeading - 18;
        String reciprocalPos = "";
        switch(curRunway.get().getPosition()){
            case "L":
                reciprocalPos = "R";
                break;
            case "C":
                reciprocalPos = "C";
                break;
            case "R":
                reciprocalPos = "L";
                break;
        }
        String finalReciprocalPos = reciprocalPos;
        String curRunwayDesignator = curRunwayHeading + "\n" + curRunway.get().getPosition();
        try {
            Runway reciprocal = runwayCB.getItems().stream()
                .map(x -> runwayList.stream()
                    .filter(runway -> x.equals(runway.getHeading() + runway.getPosition()))
                    .findFirst().get()
                )
                .filter(i -> reciprocalHeading == i.getHeading() && finalReciprocalPos.equals(
                    i.getPosition()))
                .findFirst().get();

            String reciprocalDesignator = reciprocalHeading + "\n" + reciprocalPos;
            if(curRunway.get().getHeading() <= reciprocal.getHeading()) {
                topView.setDesignators(curRunwayDesignator, reciprocalDesignator);
                topView.setDegreeOffset(0);
                sideView.setDesignators(curRunwayDesignator, reciprocalDesignator);
                direction = 1;
            } else {
                topView.setDesignators(reciprocalDesignator, curRunwayDesignator);
                topView.setDegreeOffset(180);
                sideView.setDesignators(reciprocalDesignator, curRunwayDesignator);
            }
        } catch (NoSuchElementException exception) {
            topView.setDesignators(curRunwayDesignator, "");
            sideView.setDesignators(curRunwayDesignator, "");
        }
        topView.switchScenarioDirection(direction);
        sideView.switchDirection(direction);
        settingsPanel.updateFields();
        newValues();
        updateViews();
    }

    private void createNewRunway(){
        var runwayCreation = new NewRunway(300, 500, view);
        runwayCreation.setOnHidden(e -> {
            runwayCB.setValue(runwayCB.getItems().get(runwayCB.getItems().size() - 2));
        });
    }

    /**
     * Switches the scene to the menu scene
     */
    private void goBack(){
        view.switchToMenu();
    }

    /**
     * Toggles whether the runway settings are being displayed or not
     */
    private void toggleRunwaySettings(){

        logger.info("toggle runway settings");

        if(viewStackPane.getChildren().contains(settingsPanel)){
            viewStackPane.getChildren().remove(settingsPanel);
        } else {
            StackPane.setAlignment(settingsPanel,Pos.TOP_RIGHT);
            viewStackPane.getChildren().add(settingsPanel);
            if(viewStackPane.getChildren().contains(ie)){
                viewStackPane.getChildren().remove(ie);
            }
            if(viewStackPane.getChildren().contains(notis)){
                viewStackPane.getChildren().remove(notis);
            }
        }

    }

    private void toggleImpSettings(){

        logger.info("toggle import export settings");

        if(viewStackPane.getChildren().contains(ie)){
            viewStackPane.getChildren().remove(ie);
        } else {
            StackPane.setAlignment(ie,Pos.TOP_RIGHT);
            viewStackPane.getChildren().add(ie);
            if(viewStackPane.getChildren().contains(settingsPanel)){
                viewStackPane.getChildren().remove(settingsPanel);}
            if(viewStackPane.getChildren().contains(notis)){
                viewStackPane.getChildren().remove(notis);
            }
        }

    }

    private void toggleNotiSettings(){

        logger.info("toggle noti settings");
        notis.update();

        if(viewStackPane.getChildren().contains(notis)){
            viewStackPane.getChildren().remove(notis);
        } else {
            StackPane.setAlignment(notis,Pos.TOP_RIGHT);
            viewStackPane.getChildren().add(notis);
            if(viewStackPane.getChildren().contains(settingsPanel)){
                viewStackPane.getChildren().remove(settingsPanel);}
            if(viewStackPane.getChildren().contains(ie)){
                viewStackPane.getChildren().remove(ie);}
        }

    }

    public void toggleBreakdownCalculations(){
        if(toggleBreakdown.get()) {
            this.outputPanelbd.updateValues(calcType.get());
            bp.setLeft(outputPanelbd);
        } else {
            bp.setLeft(scrollInput);}
    }

    public void updateViews(){
        sideView.updateView();
        topView.update();
        compassView.update(view.getRunway().getHeading());

    }


    public void updateRunwayList(Runway runway){
        var val = runway.getHeading() + runway.getPosition();
        runwayList.add(runway);
        runwayCB.getItems().remove("Add new runway...");
        runwayCB.getItems().add(val);
        runwayCB.getItems().add("Add new runway...");
        runwayCB.setValue(val);
    }

    public void updateObstacleList(Obstacle obstacle) {
        inputPanel.updateObstacleList(obstacle);
    }


}
