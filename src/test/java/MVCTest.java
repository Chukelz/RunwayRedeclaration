import javafx.stage.Stage;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testfx.framework.junit.ApplicationTest;
import uk.ac.soton.seg15.controller.Controller;
import uk.ac.soton.seg15.model.Model;
import uk.ac.soton.seg15.view.View;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class MVCTest extends ApplicationTest {

  private View view;
  private Model model;
  private Controller controller;

  @Override
  public void start(Stage stage){
    controller = new Controller(stage);
    view = controller.getView();
    model = controller.getModel();
  }


  private Object[] testDataForSingleParameters(){
    return new Object[]{new Object[]{60, 300, 100, 12, 500, 500, 10, "test1"},
        new Object[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
            Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, "test2"},
        new Object[]{0, 0, 0, 0, 0, 0, 0, "test3"}
    };
  }

  @Test (timeout = 5000)
  @junitparams.Parameters(method = "testDataForSingleParameters")
  public void testSingleParameterUpdate(double stripEnd, double eba, double obstWidth, double obstHeight,
      double leftThreshold, double rightThreshold, double distCentre,String obsname ){
    //StripEnd()
    view.stripEndUpdate(Double.toString(stripEnd));
    assertEquals("Strip end model value has not updated ",
        stripEnd, model.getStripEnd().get(), 0);
    assertEquals("Strip End has not been bound with model",
        stripEnd, view.getStripEnd(), 0);

    //EBA()
    view.blastProtectUpdate(Double.toString(eba));
    assertEquals("Blast Protection model value has not updated ",
        eba, model.getEba().get(), 0);
    assertEquals("Blast Protection has not been bound with model",
        eba, view.getEBA(), 0);

    //ObstWidth()
    view.obstWidthUpdate(Double.toString(obstWidth));
    assertEquals("Obstacle Width model value has not updated ",
        obstWidth, model.getCurObst().get().getWidth(), 0);
    assertEquals("Obstacle Width has not been bound with model",
        obstWidth, view.getObstacle().getWidth(), 0);

    //ObstHeight()
    view.obstHeightUpdate(Double.toString(obstHeight));
    assertEquals("Obstacle Height model value has not updated ",
        obstHeight, model.getCurObst().get().getHeight(), 0);
    assertEquals("Obstacle Height has not been bound with model",
        obstHeight, view.getObstacle().getHeight(), 0);

    //LeftThreshold()
    view.leftThreshUpdate(Double.toString(leftThreshold));
    assertEquals("Distance from Left Threshold model value has not updated ",
        leftThreshold, model.getCurObst().get().getDistanceFromLeftThreshold(), 0);
    assertEquals("Distance from Left Threshold has not been bound with model",
        leftThreshold, view.getObstacle().getDistanceFromLeftThreshold(), 0);

    //Right Threshold()
    view.rightThreshUpdate(Double.toString(rightThreshold));
    assertEquals("Distance from Right Threshold model value has not updated ",
        rightThreshold, model.getCurObst().get().getDistanceFromRightThreshold(), 0);
    assertEquals("Distance from Right Threshold has not been bound with model",
        rightThreshold, view.getObstacle().getDistanceFromRightThreshold(), 0);

    //distCentreLine
    view.distCentreLineUpdate(Double.toString(distCentre));
    assertEquals("Distance from Centre Line model value has not updated ",
        distCentre, model.getCurObst().get().getDistanceToCentreLine(), 0);
    assertEquals("Distance from Centre Line has not been bound with model",
        distCentre, view.getObstacle().getDistanceToCentreLine(), 0);

    //obsname
    view.obsNameUpdate(obsname);
    assertEquals("Obstacle Name model value has not updated ",
        obsname, model.getCurObst().get().getName());
    assertEquals("Obstacle Name has not been bound with model",
        obsname, view.getObstacle().getName());
  }


  private Object[] testDataForSingleRunwayParameters(){
    return new Object[]{new Object[]{4000,4000,4000,4000,0,240,9,"L"},
        new Object[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE,
            Double.MAX_VALUE, Double.MAX_VALUE, 36, "C"},
        new Object[]{0, 0, 0, 0, 0, 0, 1, "R"}
    };
  }

  @Test (timeout = 5000)
  @junitparams.Parameters(method = "testDataForSingleRunwayParameters")
  public void testSingleRunwayParameterUpdate(double lda, double tora, double asda, double toda,
      double threshold, double resa, int heading, String pos) {
    view.ldaUpdate(Double.toString(lda));
    assertEquals("LDA model value has not updated",
        lda, model.getCurRunway().get().getParameters().getLda(), 0);
    assertEquals("LDA has not been bound with model",
        lda, view.getRunway().getParameters().getLda(), 0);

    view.toraUpdate(Double.toString(tora));
    assertEquals("TORA model value has not updated",
        tora, model.getCurRunway().get().getParameters().getTora(), 0);
    assertEquals("TORA has not been bound with model",
        tora, view.getRunway().getParameters().getTora(), 0);

    view.asdaUpdate(Double.toString(asda));
    assertEquals("ASDA model value has not updated",
        asda, model .getCurRunway().get().getParameters().getAsda(), 0);
    assertEquals("ASDA has not been bound with model",
        asda, view.getRunway().getParameters().getAsda(), 0);

    view.todaUpdate(Double.toString(toda));
    assertEquals("TODA model value has not updated",
        toda, model.getCurRunway().get().getParameters().getToda(), 0);
    assertEquals("TODA has not been bound with model",
        toda, view.getRunway().getParameters().getToda(), 0);

    view.threshUpdate(Double.toString(threshold));
    assertEquals("Threshold model value has not updated",
        threshold, model.getCurRunway().get().getThreshold(), 0);
    assertEquals("Threshold has not been bound with model",
        threshold, view.getRunway().getThreshold(), 0);

    view.resaUpdate(Double.toString(resa));
    assertEquals("RESA model value has not updated",
        resa, model.getCurRunway().get().getParameters().getResa(), 0);
    assertEquals("RESA has not been bound with model",
        resa, view.getRunway().getParameters().getResa(), 0);

    view.headingUpdate(Integer.toString(heading));
    assertEquals("Heading model value has not updated",
        heading, model.getCurRunway().get().getHeading(), 0);
    assertEquals("Heading has not been bound with model",
        heading, view.getRunway().getHeading(), 0);

    view.positionUpdate(pos);
    assertEquals("Position model value has not updated",
        pos, model.getCurRunway().get().getPosition());
    assertEquals("Position has not been bound with model",
        pos, view.getRunway().getPosition());
  }

}
