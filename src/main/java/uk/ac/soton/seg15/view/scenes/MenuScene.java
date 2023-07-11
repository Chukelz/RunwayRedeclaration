package uk.ac.soton.seg15.view.scenes;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.seg15.model.Airport;
import uk.ac.soton.seg15.model.ColorBlindness;
import uk.ac.soton.seg15.model.ColorStyleType;
import uk.ac.soton.seg15.model.Obstacle;
import uk.ac.soton.seg15.model.Runway;
import uk.ac.soton.seg15.view.View;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;

import static javafx.scene.Node.getClassCssMetaData;
import static uk.ac.soton.seg15.view.components.ImportExport.validateXMLSchema;

public class MenuScene extends BaseScene{

    Logger logger = LogManager.getLogger(MenuScene.class);

    private ObservableList<Airport> observableAirports = FXCollections.observableArrayList(new ArrayList<Airport>());

    private SimpleListProperty<Airport> airportList = new SimpleListProperty<Airport>(observableAirports);

    public MenuScene(StackPane root, Color color, View view, Stage stage) {
        super(root,color, view,stage);

        BorderPane menuPane = new BorderPane();
        BackgroundFill bf = new BackgroundFill(ColorBlindness.daltonizeCorrect(Color.LIGHTBLUE), CornerRadii.EMPTY, Insets.EMPTY);
        menuPane.setBackground(new Background(bf));
        root.getChildren().add(menuPane);

        VBox menuBox = new VBox();
        menuBox.setAlignment(Pos.TOP_CENTER);
        menuBox.setPadding(new Insets(100));
        menuBox.setSpacing(25);
        menuPane.setCenter(menuBox);
        BorderPane.setAlignment(menuBox, Pos.TOP_CENTER);

        Button testAirport = new Button("Test Airport");
        testAirport.getStyleClass().remove(testAirport.getStyleClass().size()-1,testAirport.getStyleClass().size());
        testAirport.getStyleClass().add("menu-button");
        testAirport.setOnAction(x -> view.switchToCalc());

        Button createAirport = new Button ("Create Airport");
        createAirport.getStyleClass().remove(createAirport.getStyleClass().size()-1,createAirport.getStyleClass().size());
        createAirport.getStyleClass().add("menu-button");
        createAirport.setOnAction(x -> view.switchToCreate());


        //Get "saved airports" from folder and add to the drop down
        File airports = new File(System.getProperty("user.dir") + File.separator + "airports" + File.separator);
        ComboBox<Airport> previousAirport = new ComboBox<>(airportList);
        File[] files;
        if(airports.exists() && airports.isDirectory()) {
            logger.info("Found airports file");
            files = airports.listFiles();
            for(File file : files){
                try{
                    JAXBContext jaxbContex = JAXBContext.newInstance(Airport.class);
                    Unmarshaller jaxbUnmarshall = jaxbContex.createUnmarshaller();
                    Airport airport = (Airport) jaxbUnmarshall.unmarshal(file);
                    airportList.add(airport);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        }

        Callback<ListView<Airport>, ListCell<Airport>> airportFactory = lv -> new ListCell<Airport>() {
            @Override
            protected void updateItem(Airport item, boolean empty){
                super.updateItem(item,empty);
                setText(empty ? "" : item.getName());
            }
        };

        previousAirport.setCellFactory(airportFactory);
        previousAirport.setButtonCell(airportFactory.call(null));

        previousAirport.getStyleClass().remove(previousAirport.getStyleClass().size()-1,previousAirport.getStyleClass().size());
        previousAirport.getStyleClass().add("menu-combo");
        previousAirport.setPromptText("Previous Airports");
        previousAirport.setOnAction(event -> {
            view.setAirport(previousAirport.getValue());

            logger.info("Imported Runways " + previousAirport.getValue().getruns());
            logger.info("Imported Airport " + view.getAirport().getName());
            logger.info("Imported Airport " + previousAirport.getValue().getDescent());
            view.switchToCalc();
        });


        String colorStyleTypes[] = {"Normal", "Protanopia", "Deuteranopia", "Tritanopia"};
        ComboBox comboBox = new ComboBox(FXCollections.observableArrayList(colorStyleTypes));
        comboBox.getStyleClass().remove(comboBox.getStyleClass().size() -1,comboBox.getStyleClass().size());
        comboBox.getStyleClass().add("menu-combo");
        comboBox.setPromptText("Color vision deficiency setting");
        comboBox.setOnAction((EventHandler<ActionEvent>) event -> {
            switch (comboBox.getValue().toString()) {
                case "Normal":
                    ColorBlindness.colorType = ColorStyleType.NORMAL;
                    break;
                case "Protanopia":
                    ColorBlindness.colorType = ColorStyleType.Protanopia;
                    break;
                case "Deuteranopia":
                    ColorBlindness.colorType = ColorStyleType.Deuteranopia;
                    break;
                case "Tritanopia":
                    ColorBlindness.colorType = ColorStyleType.Tritanopia;
                    break;
            }
        });




        Button importAirport = new Button("Import new Airport");
        importAirport.getStyleClass().remove(importAirport.getStyleClass().size()-1,importAirport.getStyleClass().size());
        importAirport.getStyleClass().add("menu-button");
        importAirport.setOnAction(x -> impair());

        menuBox.getChildren().addAll(testAirport,createAirport,previousAirport, importAirport, comboBox);

    }

    @Override
    public void build(){};

    private void impair() {
        FileChooser chooser = new FileChooser();
        File selectedFile = chooser.showOpenDialog(view.getStage());
        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(".xml")) {
                new Alert(Alert.AlertType.NONE, "The File should be in XML format", ButtonType.OK).showAndWait();
            } else {
                logger.info(getClass().getResource("/airport.xsd"));
                String path = getClass().getResource("/airport.xsd").getPath();
                logger.info(path);
                if (!validateXMLSchema("/airport.xsd",selectedFile.getAbsolutePath())){
                    return;
                }
                try {
                    JAXBContext jaxbContex = JAXBContext.newInstance(Airport.class);
                    Unmarshaller jaxbUnmarshall = jaxbContex.createUnmarshaller();
                    Airport airport = (Airport) jaxbUnmarshall.unmarshal(selectedFile);
                    view.setAirport(airport);

                    logger.info("Imported Runways " + airport.getruns());
                    logger.info("Imported Airport " + view.getAirport().getName());
                    logger.info("Imported Airport " + airport.getDescent());
                    view.switchToCalc();
                    new Alert(Alert.AlertType.NONE, airport.getName() + " imported succesfully!", ButtonType.OK).showAndWait();

                } catch (JAXBException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
