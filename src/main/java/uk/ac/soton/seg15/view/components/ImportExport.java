package uk.ac.soton.seg15.view.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

public class ImportExport extends VBox{
    private static final Logger logger = LogManager.getLogger(ImportExport.class);
    private View view;
    private Button importRunway;
    private Button importObstacle;
    private Button exportRunway;
    private Button exportObstacle;
    private Button exportAirport;

    public ImportExport(View view){
        this.view = view;

        this.setSpacing(15);
        this.setPadding(new Insets(5,5,5,5));

        this.setMaxSize(175,200);

        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        buildButton();
    }
    
    public void buildButton(){
        importObstacle = new Button("Import Obstacle");
        importObstacle.setOnAction(event -> importObstacle());
        this.getChildren().add(importObstacle);

        importRunway = new Button("Import Runway");
        importRunway.setOnAction(event -> importRunway());
        this.getChildren().add(importRunway);

        exportObstacle = new Button("Export Obstacle");
        exportObstacle.setOnAction(event -> {
            try {
                exportObstacle();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        });
        this.getChildren().add(exportObstacle);

        exportRunway = new Button("Export Runway");
        exportRunway.setOnAction(event -> {
            try {
                exportRunway();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        });
        this.getChildren().add(exportRunway);

        exportAirport = new Button("Export Airport");
        exportAirport.setOnAction(event -> {
            try{
                exportAirport();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        });
        this.getChildren().add(exportAirport);
    }

    private void exportRunway() throws JAXBException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save XML");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        File f = fileChooser.showSaveDialog(view.getStage());
        String path = f.getAbsolutePath();

        JAXBContext contextObj = JAXBContext.newInstance(Runway.class);

        Marshaller marshallerObj = contextObj.createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        Runway run = view.getRunway();
        logger.info("Exported Runway " + run.getPosition());
        marshallerObj.marshal(run, new File(path));

        view.showNotification("Runway Imported");


    }

    private void exportObstacle() throws JAXBException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save XML");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML", "*.xml"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        File f = fileChooser.showSaveDialog(view.getStage());
        String path = f.getAbsolutePath();

        JAXBContext contextObj = JAXBContext.newInstance(Obstacle.class);

        Marshaller marshallerObj = contextObj.createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        Obstacle obs = view.getObstacle();
        logger.info("Exported Obstacle Height " + obs.getHeight());
        marshallerObj.marshal(obs, new File(path));
        view.showNotification("Obstacle Exported");
    }

    private void exportAirport() throws JAXBException{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save XML");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("XML", "*.xml"),
            new FileChooser.ExtensionFilter("All Files", "*.*"));

        File f = fileChooser.showSaveDialog(view.getStage());
        String path = f.getAbsolutePath();

        JAXBContext contextObj = JAXBContext.newInstance(Airport.class);

        Marshaller marshallerObj = contextObj.createMarshaller();
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        Airport obs = view.getAirport();
        marshallerObj.marshal(obs, new File(path));
        view.showNotification("Airport Exported");
    }

    private void importRunway() {
        FileChooser chooser = new FileChooser();
        File selectedFile = chooser.showOpenDialog(view.getStage());
        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(".xml")) {
                new Alert(Alert.AlertType.NONE, "The File should be in XML format", ButtonType.OK).showAndWait();
            }
            else {
                if (!validateXMLSchema("/runway.xsd",selectedFile.getAbsolutePath())){
                    return;
                }
                try {
                    JAXBContext jaxbContext = JAXBContext.newInstance(Runway.class);

                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                    Runway run = (Runway) jaxbUnmarshaller.unmarshal(selectedFile);

                    logger.info("Imported Runway " + run.getParameters().getAsda());
                    logger.info("Imported Runway " + run.getPosition());

                    view.addRunway(run);
                    view.asdaUpdate(Double.toString(run.getParameters().getAsda()));
                    view.toraUpdate(Double.toString(run.getParameters().getTora()));
                    view.todaUpdate(Double.toString(run.getParameters().getToda()));
                    view.resaUpdate(Double.toString(run.getParameters().getResa()));
                    view.ldaUpdate(Double.toString(run.getParameters().getLda()));
                    view.threshUpdate(Double.toString((run.getThreshold())));
                    view.headingUpdate(Integer.toString(run.getHeading()));
                    view.positionUpdate(run.getPosition());
                    view.showNotification("Runway Imported");

                } catch (JAXBException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    private void importObstacle() {
        FileChooser chooser = new FileChooser();
        File selectedFile = chooser.showOpenDialog(view.getStage());
        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(".xml")) {
                new Alert(Alert.AlertType.NONE, "The File should be in XML format", ButtonType.OK).showAndWait();
            } else {
                if (!validateXMLSchema("/obstacle.xsd",selectedFile.getAbsolutePath())){
                    return;
                }
                try {
                    JAXBContext jaxbContext = JAXBContext.newInstance(Obstacle.class);

                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                    Obstacle obs = (Obstacle) jaxbUnmarshaller.unmarshal(selectedFile);

                    logger.info("Imported Obstacle " + obs.getHeight());

                    view.addObstacle(obs);
                    view.obstHeightUpdate(Double.toString(obs.getHeight()));
                    view.obstWidthUpdate(Double.toString(obs.getWidth()));
                    view.leftThreshUpdate(Double.toString(obs.getDistanceFromLeftThreshold()));
                    view.rightThreshUpdate(Double.toString(obs.getDistanceFromRightThreshold()));
                    view.showNotification("Obstacle Imported");


                } catch (JAXBException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean validateXMLSchema(String xsdPath, String xmlPath){

        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(ImportExport.class.getResource(xsdPath));
            Validator validator = ((Schema) schema).newValidator();

            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (IOException | SAXException e) {
            new Alert(Alert.AlertType.NONE, "The File should be of the schema " + e.getMessage(), ButtonType.OK).showAndWait();
            return false;
        }
        return true;
    }


}
