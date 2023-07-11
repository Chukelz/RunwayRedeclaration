import static junit.framework.TestCase.assertEquals;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class obsXMLTest {
  private Schema schema;
  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  DocumentBuilder documentBuilder = factory.newDocumentBuilder();
  Document doc = documentBuilder.parse(new File("testResources/obstacle.xml"));
  Element root = doc.getDocumentElement();  //Retrieve the root element

  public obsXMLTest() throws IOException, SAXException, ParserConfigurationException {
  }


  @Before
  public void setUp() throws Exception {
    SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    File schemaFile = new File("src/main/resources/obstacle.xsd");
    schema = factory.newSchema(schemaFile);
  }


  //Test if the xml file is valid against schema
  @Test
  public void obstacleXMLValidTest() throws Exception {
    StreamSource xmlFile = new StreamSource(new File("testResources/obstacle.xml"));
    Validator validator = schema.newValidator();

    try {
      validator.validate(xmlFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //Test that specific elements are present in xml file
  @Test
  public void elemAttribute() throws Exception{
    //Test that the element exists
    NodeList node1 = root.getElementsByTagName("obstacle");
    assertEquals(0,node1.getLength());

    NodeList node2 = root.getElementsByTagName("distancefromLeftThreshold");
    assertEquals(1,node2.getLength());

    NodeList node3 = root.getElementsByTagName("distancefromRightThreshold");
    assertEquals(1,node3.getLength());

    NodeList node4 = root.getElementsByTagName("distancetoCentreLine");
    assertEquals(1,node4.getLength());

    NodeList node5 = root.getElementsByTagName("height");
    assertEquals(1,node5.getLength());

    NodeList node7 = root.getElementsByTagName("name");
    assertEquals(1,node7.getLength());

    NodeList node8 = root.getElementsByTagName("width");
    assertEquals(1,node8.getLength());
  }

  //Test the number of child elements
  @Test
  public void elemTotal() throws Exception {
    NodeList nodes = root.getChildNodes();
    assertEquals(13,nodes.getLength());

  }
}
