package uk.ac.soton.seg15.view.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import uk.ac.soton.seg15.model.Airport;
import uk.ac.soton.seg15.model.Calculate;
import uk.ac.soton.seg15.model.Obstacle;
import uk.ac.soton.seg15.model.Runway;
import uk.ac.soton.seg15.view.View;
import uk.ac.soton.seg15.view.scenes.MainScene;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class Notis extends VBox{
    private static final Logger logger = LogManager.getLogger(Notis.class);
    private View view;
    private Button importRunway;
    private Button importObstacle;
    private Button exportRunway;
    private Button exportObstacle;
    private Button exportAirport;


    public Notis(View view){
        this.view = view;

        this.setSpacing(15);
        this.setPadding(new Insets(5,5,5,5));

        this.setMaxSize(175,200);

        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        buildButton();
    }

    public void buildButton(){
        logger.info(view.getNotiList());
        TilePane pane = new TilePane();
        ListView<String> list = new ListView<String>();
        list.setItems(view.getNotiList());
        pane.getChildren().add(list);
        //for (String s : view.getNotiList()){
          //  Label label = new Label(s);
            //pane.getChildren().add(label);
        //}
        this.getChildren().add(pane);
    }

    public void update(){
        //buildButton();
    }


}

