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

public class runwayXMLTest {
  private Schema schema;
  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
  DocumentBuilder documentBuilder = factory.newDocumentBuilder();
  Document doc = documentBuilder.parse(new File("testResources/runway.xml"));
  Element root = doc.getDocumentElement();  //Retrieve the root element

  public runwayXMLTest() throws ParserConfigurationException, IOException, SAXException {
  }


  @Before
  public void setUp() throws Exception {
    SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    File schemaFile = new File("src/main/resources/runway.xsd");
    schema = factory.newSchema(schemaFile);
  }

  //Test if runway xml file is valid against schema
  @Test
  public void runwayXMLValidTest() throws Exception {
    StreamSource xmlFile = new StreamSource(new File("testResources/runway.xml"));
    Validator validator = schema.newValidator();

    try {
      validator.validate(xmlFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //Test that elements are present in xml file
  @Test
  public void elemAttribute() throws Exception{
    NodeList node1 = root.getElementsByTagName("runway");
    assertEquals(0,node1.getLength());

    NodeList node2 = root.getElementsByTagName("heading");
    assertEquals(1,node2.getLength());

    NodeList node3 = root.getElementsByTagName("mParameters");
    assertEquals(1,node3.getLength());

    NodeList asda = root.getElementsByTagName("asda");
    assertEquals(1,asda.getLength());

    NodeList lda = root.getElementsByTagName("lda");
    assertEquals(1,lda.getLength());

    NodeList resa = root.getElementsByTagName("resa");
    assertEquals(1,resa.getLength());

    NodeList toda = root.getElementsByTagName("toda");
    assertEquals(1,toda.getLength());

    NodeList tora = root.getElementsByTagName("tora");
    assertEquals(1,tora.getLength());

    NodeList position = root.getElementsByTagName("position");
    assertEquals(1,position.getLength());

    NodeList threshold = root.getElementsByTagName("threshold");
    assertEquals(1,threshold.getLength());
  }

  //Test the number of child elements
  @Test
  public void elemTotal() throws Exception {
    NodeList nodes = root.getChildNodes();
    assertEquals(9,nodes.getLength());

  }


}
