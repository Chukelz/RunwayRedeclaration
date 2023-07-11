package uk.ac.soton.seg15.view.scenes;

import java.io.File;
import java.io.IOException;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.seg15.model.Airport;
import uk.ac.soton.seg15.model.Obstacle;
import uk.ac.soton.seg15.model.Parameters;
import uk.ac.soton.seg15.model.Runway;
import uk.ac.soton.seg15.view.View;
import uk.ac.soton.seg15.view.components.ParameterField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.jar.JarEntry;

/**
 * Manual creation of an airport, input necessary airport information
 * such as runways and predefined obstacles.
 */
public class CreateAirportScene extends BaseScene {

    private static Logger logger = LogManager.getLogger(CreateAirportScene.class);
    private ParameterField name;

    private ParameterField angDescent;
    private ParameterField angAscent;
    private ParameterField runwayName;
    private ComboBox runwayPos;

    private ParameterField tora;
    private ParameterField toda;
    private ParameterField asda;
    private ParameterField lda;
    private ParameterField thresh;
    private ParameterField RESA;
    private ComboBox<Runway> runways;

    private ComboBox<Obstacle> obstacles;
    private ObservableList<Runway> observableRunways = FXCollections.observableArrayList(new ArrayList<Runway>());

    private SimpleListProperty<Runway> runwaysList = new SimpleListProperty<Runway>(observableRunways);

    private ObservableList<Obstacle> observableObst = FXCollections.observableArrayList(new ArrayList<Obstacle>());
    private SimpleListProperty<Obstacle> obstList = new SimpleListProperty<Obstacle>(observableObst);

    private  ParameterField obstName;

    private ParameterField obstHeight;

    private ParameterField obstWidth;

    private ParameterField distFromCentre;

    private ParameterField leftThresh;

    private ParameterField rightThresh;

    private Button addRunway;

    private Button removeRunway;

    private Button addObst;

    private Button removeObst;

    private Button createAirport;


    public CreateAirportScene(StackPane root, Color color, View view, Stage stage) {
        super(root, color, view, stage);

        stage.minHeightProperty().bind(this.heightProperty());
        stage.minWidthProperty().bind(this.widthProperty());

    }

    @Override
    public void build() {

        GridPane gp = new GridPane();
        gp.setPadding(new Insets(5,5,5,5));
        gp.setHgap(10);
        gp.setVgap(10);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPrefWidth(100);
        gp.getColumnConstraints().addAll(column3,column2,column1,column3);

        gp.setPadding(new Insets(5,5,5,5));

        //back to menu button
        Button back = new Button("<- Back");
        back.setOnAction(x -> view.switchToMenu());
        back.setAlignment(Pos.CENTER_RIGHT);
        gp.add(back,0,0);

        name = new ParameterField("Airport Name");

        HBox angs = new HBox();
        angs.setPadding(new Insets(5,5,5,5));
        angs.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, new Insets(0,0,0,0))));
        angDescent = new ParameterField("Min angle of descent (1m:_m)","",1,Double.MAX_VALUE);
        angAscent = new ParameterField("Min angle of ascent (1m:_m)","",1,Double.MAX_VALUE);
        angs.getChildren().addAll(name,angDescent,angAscent);
        angs.setHgrow(angAscent,Priority.ALWAYS);
        angs.setHgrow(angDescent,Priority.ALWAYS);
        angs.setHgrow(name,Priority.ALWAYS);

        gp.add(angs,1,1,2,1);

        //Create runways vBox
        VBox createRunways = new VBox();
        createRunways.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, new Insets(0,0,0,0))));
        createRunways.setPadding(new Insets(5,5,5,5));
        createRunways.setSpacing(20);

        HBox nameCreator = new HBox();
        nameCreator.setSpacing(20);
        nameCreator.setPadding(new Insets(5,5,5,5));
        nameCreator.setAlignment(Pos.BOTTOM_LEFT);
        runwayName = new ParameterField("Runway Heading","",0,36);
        String[] pos = {"L","C","R"};
        runwayPos = new ComboBox<String>(FXCollections.observableArrayList(pos));
        nameCreator.getChildren().addAll(runwayName,runwayPos);


        tora = new ParameterField("TORA","m", 0, Double.MAX_VALUE);
        toda = new ParameterField("TODA","m", 0, Double.MAX_VALUE);
        asda = new ParameterField("ASDA","m", 0, Double.MAX_VALUE);
        lda = new ParameterField("LDA","m", 0, Double.MAX_VALUE);
        thresh = new ParameterField("Threshold\ndisplacement","m", 0, Double.MAX_VALUE);
        RESA = new ParameterField("RESA","m", 0, Double.MAX_VALUE);

        HBox runwaysBox = new HBox();
        runwaysBox.setPadding(new Insets(5,5,5,5));
        runwaysBox.setSpacing(20);

        runways = new ComboBox<>(runwaysList);
        runways.setPromptText("Select created runways");

        Callback<ListView<Runway>, ListCell<Runway>> runwayFactory = lv -> new ListCell<Runway>() {
            @Override
            protected void updateItem(Runway item, boolean empty){
                super.updateItem(item,empty);
                setText(empty ? "" : item.getHeading() + item.getPosition());
            }
        };

        runways.setCellFactory(runwayFactory);
        runways.setButtonCell(runwayFactory.call(null));

        runways.setOnAction(event -> {
            if(runways.getValue() != null){
                runwayName.setText(Integer.toString(runways.getValue().getHeading()));
                runwayPos.getSelectionModel().select(runways.getValue().getPosition());
                toda.setText(Double.toString(runways.getValue().getParameters().getToda()));
                tora.setText(Double.toString(runways.getValue().getParameters().getTora()));
                asda.setText(Double.toString(runways.getValue().getParameters().getAsda()));
                lda.setText(Double.toString(runways.getValue().getParameters().getLda()));
                thresh.setText(Double.toString(runways.getValue().getThreshold()));
                RESA.setText(Double.toString(runways.getValue().getParameters().getResa()));
            }
        });


        addRunway = new Button("+");
        addRunway.setOnAction(x -> addRunway());

        removeRunway = new Button("-");
        removeRunway.setOnAction(x -> removeRunway());
        runwaysBox.getChildren().addAll(runways,addRunway,removeRunway);

        createRunways.getChildren().addAll(runwaysBox,nameCreator,tora,toda,asda,lda,thresh,RESA);

        gp.add(createRunways,1,2);

        //Create obstacles vbox
        VBox createObst = new VBox();
        createObst.setSpacing(20);
        createObst.setPadding(new Insets(5,5,5,5));
        createObst.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, new Insets(0,0,0,0))));

        obstName = new ParameterField("Obstacle Name");
        obstHeight = new ParameterField("Obstacle height","m",-1,Double.MAX_VALUE);
        obstWidth = new ParameterField("Obstacle width","m",-1,Double.MAX_VALUE);
        distFromCentre = new ParameterField("Distance to centreline","m",-1,Double.MAX_VALUE);
        rightThresh = new ParameterField("Distance from right threshold","m",Double.MIN_VALUE,Double.MAX_VALUE);
        leftThresh = new ParameterField("Distance from left threshold", "m", Double.MIN_VALUE,Double.MAX_VALUE);


        HBox obstBox = new HBox();
        obstBox.setPadding(new Insets(5,5,5,5));
        obstBox.setSpacing(20);

        obstacles = new ComboBox<>(obstList);
        obstacles.setPromptText("Select created obstacles");

        Callback<ListView<Obstacle>, ListCell<Obstacle>> obstFactory = lv -> new ListCell<Obstacle>() {
            @Override
            protected void updateItem(Obstacle item, boolean empty){
                super.updateItem(item,empty);
                setText(empty ? "" : item.getName());
            }
        };

        obstacles.setCellFactory(obstFactory);
        obstacles.setButtonCell(obstFactory.call(null));

        obstacles.setOnAction(event -> {
            if(obstacles.getValue() != null){
                obstName.setText(obstacles.getValue().getName());
                obstWidth.setText(Double.toString(obstacles.getValue().getWidth()));
                obstHeight.setText(Double.toString(obstacles.getValue().getHeight()));
                distFromCentre.setText(Double.toString(obstacles.getValue().getDistanceToCentreLine()));
                leftThresh.setText(Double.toString(obstacles.getValue().getDistanceFromLeftThreshold()));
                rightThresh.setText(Double.toString(obstacles.getValue().getDistanceFromRightThreshold()));
            }
        });


        addObst = new Button("+");
        addObst.setOnAction(x -> addObst());

        removeObst = new Button("-");
        removeObst.setOnAction(x -> removeObst());
        obstBox.getChildren().addAll(obstacles,addObst,removeObst);

        createObst.getChildren().addAll(obstBox,obstName,obstHeight,obstWidth,distFromCentre,leftThresh,rightThresh);

        gp.add(createObst,2,2);

        createAirport = new Button("Create Airport");
        createAirport.setOnAction(e -> createAirport());
        createAirport.getStyleClass().remove(createAirport.getStyleClass().size()-1,createAirport.getStyleClass().size());
        createAirport.getStyleClass().add("menu-button");
        gp.add(createAirport,2,3);

        root.getChildren().add(gp);
    }

    private void addRunway(){
        //Add runway to observable list
        if(runwayName.getText().isEmpty()){
            //notification
            view.showNotification("Need to input runway heading");
        }else if(runwayPos.getSelectionModel().isEmpty()){
            //notification
            view.showNotification("Need to choose runway position");
        }else if(tora.getText().isEmpty()){
            //notification
            view.showNotification("Need to enter the TORA");
        }else if(toda.getText().isEmpty()){
            //notification
            view.showNotification("Need to enter the TODA");
        }else if(asda.getText().isEmpty()){
            //notification
            view.showNotification("Need to enter the ASDA");
        }else if(lda.getText().isEmpty()){
            //notification
            view.showNotification("Need to enter the LDA");
        }else if(thresh.getText().isEmpty()){
            //notification
            view.showNotification("Need to enter the threshold displacement");
        }else if(RESA.getText().isEmpty()){
            //notification
            view.showNotification("Need to enter the RESA");
        }else{
            Runway r = runwayAlreadyExists((int)runwayName.getValue(),runwayPos.getValue().toString());
            if(r != null && obstacles.getValue() != null && runways.getSelectionModel().getSelectedItem().equals(r)){
                runwaysList.remove(r);
                Runway runway = new Runway((int) runwayName.getValue(),thresh.getValue(),runwayPos.getValue().toString(),new Parameters(tora.getValue(), toda.getValue(), asda.getValue(), lda.getValue()));
                runwaysList.add(runway);
                clearRunwayText();
            } else if(r != null){
                view.showNotification("Runway already exists! Please select runway from drop down if you want to update it");
            }else {
                //add!
                Runway runway = new Runway((int) runwayName.getValue(),thresh.getValue(),runwayPos.getValue().toString(),new Parameters(tora.getValue(), toda.getValue(), asda.getValue(), lda.getValue()));
                runwaysList.add(runway);
                clearRunwayText();
            }

        }

    }

    private void removeRunway(){
        //Remove runway from observable list
        if(runways.getValue() == null){
            //notification
            view.showNotification("Need to choose a runway to remove it");
        } else {
            runwaysList.remove(runways.getValue());
            clearRunwayText();
        }

    }

    private void addObst(){
        if(obstName.getText().isEmpty()){
            view.showNotification("Need to enter an obstacle name");
        }else if (obstWidth.getText().isEmpty()){
            view.showNotification("Need to enter an obstacle width");
        }else if (obstHeight.getText().isEmpty()){
            view.showNotification("Need to enter an obstacle height");
        }else if (distFromCentre.getText().isEmpty()){
            view.showNotification("Need to enter a distance from centre line");
        }else if (leftThresh.getText().isEmpty()){
            view.showNotification("Need to enter a distance from left threshold");
        }else if (obstWidth.getText().isEmpty()){
            view.showNotification("Need to enter a distance from right threshold");
        } else {
            Obstacle o = obstAlreadyExists(obstName.getText());
            if(o != null && obstacles.getValue() != null && obstacles.getValue().equals(o)){
                obstList.remove(o);
                Obstacle obs = new Obstacle(obstName.getText(),obstHeight.getValue(),obstWidth.getValue(),distFromCentre.getValue(),leftThresh.getValue(),rightThresh.getValue());
                obstList.add(obs);
                clearObstText();
                view.showNotification("Obstacle " + obstName.getText() + " updated");
            }else if(o != null){
                view.showNotification("Obstacle already exists! Please select obstacle from drop down if you want to update it");
            }else{
                Obstacle obs = new Obstacle(obstName.getText(),obstHeight.getValue(),obstWidth.getValue(),distFromCentre.getValue(),leftThresh.getValue(),rightThresh.getValue());
                obstList.add(obs);
                clearObstText();
            }
            
        }

    }

    private void removeObst(){
        //Remove runway from observable list
        if(obstacles.getSelectionModel().getSelectedItem() == null){
            //notification
            view.showNotification("Need to choose an obstacle to remove it");
        } else {
            obstList.remove(obstacles.getSelectionModel().getSelectedItem());
            clearObstText();
        }

    }

    private void createAirport(){
        if(runwaysList.size() < 1){
            view.showNotification("Airport must have at least 1 runway");
            return;
        } else if(name.getText().isEmpty()){
            view.showNotification("Must give airport a name");
            return;
        } else if(angDescent.getText().isEmpty()){
            view.showNotification("Must give a min angle of descent");
            return;
        } else if(angAscent.getText().isEmpty()){
            view.showNotification("Must give a min angle of ascent");
            return;
        }

        logger.info("Creating Airport");
        Airport newAirport = new Airport(name.getText(), runwaysList, obstList, angAscent.getValue(), angDescent.getValue());

        //Get directory
        File airports = new File(System.getProperty("user.dir") + File.separator + "airports" + File.separator);
        if(!airports.exists()) {
            try{
                airports.mkdir();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        //Create filename
        int filecount = 1;
        String filename = name.getText();
        for (var airport : airports.listFiles()) {
            if(airport.getName().equals(filename + ".xml")){
                filename = name.getText() + filecount;
            } else if(airport.getName().equals(filename + ".xml")){
                filecount++;
                filename = name.getText() + filecount;
            }
        }

        //Create file
        File file = new File(airports, filename + ".xml");
        try{
            file.createNewFile();
            JAXBContext contextObj = JAXBContext.newInstance(Airport.class);

            Marshaller marshallerObj = contextObj.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshallerObj.marshal(newAirport, file);
            view.showNotification("Added new airport to: " + airports.getAbsolutePath());
        } catch (Exception e){
            e.printStackTrace();
        }

        //Switch to calc scene
        view.setAirport(newAirport);
        view.switchToCalc();
    }

    private void clearRunwayText(){
        runwayName.setText("");
        runwayPos.getSelectionModel().clearSelection();
        tora.setText("");
        toda.setText("");
        asda.setText("");
        lda.setText("");
        thresh.setText("");
        RESA.setText("");
    }

    private void clearObstText(){
        obstName.setText("");
        obstHeight.setText("");
        obstWidth.setText("");
        distFromCentre.setText("");
        leftThresh.setText("");
        rightThresh.setText("");
    }

    private Runway runwayAlreadyExists(int heading,String pos){
        Iterator<Runway> it = runwaysList.iterator();
        while(it.hasNext()){
            Runway r = it.next();
            if(r.getPosition().equals(pos) && r.getHeading() == heading){
                return r;
            }
        }
        return null;
    }

    private Obstacle obstAlreadyExists(String name){
        Iterator<Obstacle> it = obstList.iterator();
        while(it.hasNext()){
            Obstacle o = it.next();
            if(o.getName().equals(name)){
                return o;
            }
        }
        return null;

    }
}


