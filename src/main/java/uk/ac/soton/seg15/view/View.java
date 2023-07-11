package uk.ac.soton.seg15.view;

import com.sun.javafx.image.impl.ByteIndexed;
import java.util.ArrayList;
import java.util.function.IntToDoubleFunction;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.paint.*;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.seg15.controller.Controller;
import uk.ac.soton.seg15.model.Airport;
import uk.ac.soton.seg15.model.Obstacle;
import uk.ac.soton.seg15.model.Runway;
import uk.ac.soton.seg15.view.components.ImportExport;
import uk.ac.soton.seg15.view.scenes.*;


public class View {

    private final Stage stage;
    private Controller controller;
    private ObjectProperty<Runway> runway = new SimpleObjectProperty();
    private ObjectProperty<Obstacle> obstacle = new SimpleObjectProperty();
    private DoubleProperty eba = new SimpleDoubleProperty();
    private DoubleProperty stripEnd = new SimpleDoubleProperty();
    private Airport airport;
    private BaseScene currentScene;
    private static final Logger logger = LogManager.getLogger(View.class);

    private MainScene scene;
    private String finalNoti;
    public static ObservableList notiList = FXCollections.observableArrayList();

    public View(Stage stage, Controller controller){
        this.stage = stage;
        this.controller = controller;
        switchToMenu();
    }


    /**
     * Given a scene, will set the stage to show the new scene
     *
     * @param scene the new scene to be shown
     */
    public void setScene(Scene scene) {
        if (scene instanceof MainScene) {
            this.scene = (MainScene)scene;
        }
        stage.setScene(scene);
    }

    public StackPane createNewRoot(){
        StackPane pane = new StackPane();
        pane.setMinHeight(getStageHeight());
        pane.setMinWidth(getStageWidth());
        return pane;
    }

    public void switchToMenu(){
        currentScene = new MenuScene(createNewRoot(), Color.LIGHTBLUE, this,stage);
        stage.setScene(currentScene);
        currentScene.build();
    }

    public void switchToCalc(){
        scene = new MainScene(createNewRoot(), Color.BLACK, this,runway,obstacle,stage);
        currentScene = scene;
        stage.setScene(scene);
        scene.build();
    }
    public void switchToCreate(){
        currentScene = new CreateAirportScene(createNewRoot(), Color.BLACK, this, stage);
        stage.setScene(currentScene);
        currentScene.build();
        stage.sizeToScene();
    }

    public double getWidth(){
        return stage.getWidth();
    }

    public double getHeight(){
        return stage.getHeight();
    }

    public void setRunway(ObjectProperty runway){
        this.runway.bindBidirectional(runway);
        runway.addListener((observable, oldValue, newValue) -> {
            if (scene != null) {
                scene.updateViews();
            }
        });
    }

    public void showNotification(String message) {
        logger.info("Shown Notifications: " + this.getNotiList());
        this.addToNotiList(message);
        Label label = new Label(message);
        label.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 10px;");
        label.setOpacity(0);

        javafx.stage.Popup popup = new Popup();
        popup.getContent().add(label);

        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        popup.show(stage);
        popup.setX(stage.getX() + stage.getWidth() - label.getWidth());
        popup.setY(stage.getY() + stage.getHeight() - label.getHeight());
        // updateLabelPosition(label, stage); // Set initial label position



        FadeTransition ft = new FadeTransition(Duration.millis(800), label);
        ft.setToValue(1);
        ft.setOnFinished(evt -> {
            new Thread(() -> {
                try {
                    Thread.sleep(1800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    FadeTransition ft2 = new FadeTransition(Duration.millis(2000), label);
                    ft2.setToValue(0);
                    ft2.setOnFinished(evt2 -> popup.hide());
                    ft2.play();
                });
            }).start();
        });
        ft.play();
    }

    public void setObstacle(ObjectProperty obstacle){
        this.obstacle.bindBidirectional(obstacle);
        obstacle.addListener((observable, oldValue, newValue) -> {
            if (scene != null) {
                scene.updateViews();
            }
        });
    }

    public String getFinalNoti() {
        return finalNoti;
    }

    public Runway getRunway(){
        return runway.get();
    }

    public Obstacle getObstacle(){ return obstacle.get();}

    public void notiCollect(){
        controller.notiCollect();
    }

    public void calculate(String calcType, int direction){
        try {
            controller.calculate(calcType, direction);
        } catch (IllegalArgumentException e) {
            showNotification(e.getMessage() + " Try switching direction");
        }
    }

    public Airport getAirport() {
        return airport;
    }

    public void setEba(DoubleProperty eba) {
        this.eba.bindBidirectional(eba);
    }

    public Double getEBA() {return eba.get();}

    public void setStripEnd(DoubleProperty stripEnd) { this.stripEnd.bindBidirectional(stripEnd);}


    public double getStripEnd() { return stripEnd.get();}

    public double getStageHeight(){
        return stage.getHeight();
    }

    public double getStageWidth(){
        return stage.getWidth();
    }

    public Stage getStage() { return stage;}

    public void obstWidthUpdate(String obstWidth){
        this.controller.setObstWidth(Double.parseDouble(obstWidth));
    }

    public void obstHeightUpdate(String obstHeight){
        this.controller.setObstHeight(Double.parseDouble(obstHeight));
    }

    public void blastProtectUpdate(String blastProtect){
        this.controller.setBlastProtect(Double.parseDouble(blastProtect));
    }

    public void leftThreshUpdate(String leftThresh){
        this.controller.setLeftThresh(Double.parseDouble(leftThresh));
    }

    public void rightThreshUpdate(String rightThresh){
        this.controller.setRightThresh(Double.parseDouble(rightThresh));
    }

    public void stripEndUpdate(String stripEnd){
        this.controller.setStripEnd(Double.parseDouble(stripEnd));
    }

    public void ldaUpdate(String lda){
        this.controller.setLda(Double.parseDouble(lda));
    }

    public void asdaUpdate(String asda){
        this.controller.setAsda(Double.parseDouble(asda));
    }

    public void toraUpdate(String tora){
        this.controller.setTora(Double.parseDouble(tora));
    }
    public void todaUpdate(String toda){
        this.controller.setToda(Double.parseDouble(toda));
    }

    public void threshUpdate(String threshold){
        this.controller.setThresh(Double.parseDouble(threshold));
    }

    public void resaUpdate(String resa){
        this.controller.setResa(Double.parseDouble(resa));
    }

    public void headingUpdate(String heading){
        this.controller.setHeading(Integer.parseInt(heading));
    }

    public void positionUpdate(String position){
        this.controller.setPosition(position);
    }

    public void obsNameUpdate(String name){
        this.controller.setobsName(name);
    }

    public void distCentreLineUpdate(String dcl){
        this.controller.setdistCentreLine(Double.parseDouble(dcl));
    }

    public void addRunway(Runway runway){
        if(getAirport() != null) {
            if(getAirport().getRunways() == null) {
                var list = new ArrayList<Runway>();
                list.add(runway);
                getAirport().setRunways(list);
            }
            else getAirport().getRunways().add(runway);
        }
        scene.updateRunwayList(runway);
    }

    public void addObstacle(Obstacle obstacle){
        if(getAirport() != null) {
            if(getAirport().getObstacles() == null) {
                var list = new ArrayList<Obstacle>();
                list.add(obstacle);
                getAirport().setObstacles(list);
            }
            else getAirport().getObstacles().add(obstacle);
        }
        scene.updateObstacleList(obstacle);
    }

    public void finalNotiUpdate(String finalNoti){
        this.controller.setFinalNoti(finalNoti);
    }

    public void setObstacle(Obstacle obstacle){
        this.obstacle.set(obstacle);
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
        controller.setCurAirport(airport);
    }

    public void setFinalNoti(String finalNoti) {
        this.finalNoti = finalNoti;
    }

    public ObservableList<String> getNotiList() {
        return notiList;
    }

    public void addToNotiList(String noti){
        this.notiList.add(noti);
    }
}
