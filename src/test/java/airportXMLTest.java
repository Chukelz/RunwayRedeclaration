import static junit.framework.TestCase.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.soton.seg15.model.Calculate;
import uk.ac.soton.seg15.model.Obstacle;
import uk.ac.soton.seg15.model.Parameters;
import uk.ac.soton.seg15.model.Runway;

public class airportXMLTest {
  private Schema schema;
  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  DocumentBuilder documentBuilder = factory.newDocumentBuilder();
  Document doc = documentBuilder.parse(new File("testResources/airport.xml"));
  Element root = doc.getDocumentElement();  //Retrieve the root element

  public airportXMLTest() throws ParserConfigurationException, IOException, SAXException {
  }


  @Before
  public void setUp() throws Exception {
    SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    File schemaFile = new File("src/main/resources/airport.xsd");
    schema = factory.newSchema(schemaFile);
  }

  //Test if runway xml file is valid against schema
  @Test
  public void airportXMLValidTest() throws Exception {
    StreamSource xmlFile = new StreamSource(new File("testResources/airport.xml"));
    Validator validator = schema.newValidator();

    try {
      validator.validate(xmlFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //Test that elements are present in xml file
  @Test
  public void elemAttribute() throws Exception {
    NodeList ascent = root.getElementsByTagName("ascent");
    assertEquals(1, ascent.getLength());

    NodeList descent = root.getElementsByTagName("descent");
    assertEquals(1, descent.getLength());

    List<Runway> runwayList = new ArrayList<>();
    NodeList runways = root.getElementsByTagName("runways");
    for (int i = 0; i < runways.getLength(); i++) {
      Node node = runways.item(i);
      NodeList runway = node.getChildNodes();
      int heading = -1;
      double threshold = -1;
      String position = "";
      float toda = -1.0f;
      float tora = -1.0f;
      float asda = -1.0f;
      float lda = -1.0f;
      float resa = -1.0f;
      for (int k = 0; k < runway.getLength(); k++) {
        if (runway.item(k).getNodeName().equals("heading")) {
          heading = Integer.parseInt(runway.item(k).getTextContent());
        } else if (runway.item(k).getNodeName().equals("threshold")) {
          threshold = Double.parseDouble(runway.item(k).getTextContent());
        } else if (runway.item(k).getNodeName().equals("position")) {
          position = runway.item(k).getTextContent();
        } else if (runway.item(k).getNodeName().equals("mParameters")) {
          NodeList parameters = runway.item(k).getChildNodes();
          assertEquals(11, parameters.getLength());
          for (int j = 0; j < parameters.getLength(); j++) {
            if (parameters.item(j).getNodeName().equals("toda")) {
              toda = Float.parseFloat(parameters.item(j).getTextContent());
            } else if (parameters.item(j).getNodeName().equals("tora")) {
              tora = Float.parseFloat(parameters.item(j).getTextContent());
            } else if (parameters.item(j).getNodeName().equals("asda")) {
              asda = Float.parseFloat(parameters.item(j).getTextContent());
            } else if (parameters.item(j).getNodeName().equals("lda")) {
              lda = Float.parseFloat(parameters.item(j).getTextContent());
            } else if (parameters.item(j).getNodeName().equals("resa")) {
              resa = Float.parseFloat(parameters.item(j).getTextContent());
            }
          }
        }
      }
      Parameters parameters1 = new Parameters(tora, toda, asda, lda, resa);
      Runway runway1 = new Runway(heading, threshold, position, parameters1);
      runwayList.add(runway1);
    }

    NodeList obstacles = root.getElementsByTagName("obstacles");
    Obstacle obstacle1 = new Obstacle();
    for (int i = 0; i < obstacles.getLength(); i++) {
      Node node = obstacles.item(i);
      NodeList obstacle = node.getChildNodes();
      double distancefromLeftThreshold = -1.0;
      double distancefromRightThreshold = -1.0;
      double distancetoCentreLine = -1.0;
      double height = -1.0;
      double width = -1.0;
      String name = "";
      for (int k = 0; k < obstacle.getLength(); k++) {
        if (obstacle.item(k).getNodeName().equals("distancefromLeftThreshold")) {
          distancefromLeftThreshold = Double.parseDouble(obstacle.item(k).getTextContent());
        } else if (obstacle.item(k).getNodeName().equals("distancefromRightThreshold")) {
          distancefromRightThreshold = Double.parseDouble(obstacle.item(k).getTextContent());
        } else if (obstacle.item(k).getNodeName().equals("distancetoCentreLine")) {
          distancetoCentreLine = Double.parseDouble(obstacle.item(k).getTextContent());
        } else if (obstacle.item(k).getNodeName().equals("height")) {
          height = Double.parseDouble(obstacle.item(k).getTextContent());
        } else if (obstacle.item(k).getNodeName().equals("width")) {
          width = Double.parseDouble(obstacle.item(k).getTextContent());
        } else if (obstacle.item(k).getNodeName().equals("name")) {
          name = obstacle.item(k).getTextContent();
        }
      }
      obstacle1.setDistanceFromLeftThreshold(distancefromLeftThreshold);
      obstacle1.setDistanceFromRightThreshold(distancefromRightThreshold);
      obstacle1.setDistanceToCentreLine(distancetoCentreLine);
      obstacle1.setHeight(height);
      obstacle1.setWidth(width);
      obstacle1.setName(name);
    }

    for (Runway runway : runwayList) {
      Calculate cal1 = new Calculate(runway, obstacle1.getHeight(), 300, 60, obstacle1.getDistanceFromLeftThreshold(), obstacle1.getDistanceFromRightThreshold(), "TakeOff Away");
      Calculate cal2 = new Calculate(runway, obstacle1.getHeight(), 300, 60, obstacle1.getDistanceFromLeftThreshold(), obstacle1.getDistanceFromRightThreshold(), "Landing Over");
      cal1.recalculate(1);
      cal2.recalculate(1);
    }
  }
}
