import junitparams.JUnitParamsRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import uk.ac.soton.seg15.model.Calculate;
import uk.ac.soton.seg15.model.Parameters;
import uk.ac.soton.seg15.model.Runway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class RunwayTest {

  @Test(timeout = 5000)
  public void test1() {
    Parameters parameters1 = new Parameters(3902, 3902,3902,3595,240);
    Runway runway1 = new Runway(9, 306, "L", parameters1);
    Parameters parameters2 = new Parameters(3884, 3962,3884,3884,240);
    Runway runway2 = new Runway(27, 0, "R", parameters2);
    Calculate cal1 = new Calculate(runway1,12, 300, 60, -50, 3646, "TakeOff Away");
    Calculate cal2 = new Calculate(runway1,12, 300, 60, -50, 3646, "Landing Over");
    Calculate cal3 = new Calculate(runway2,12,300,60,-50,3646,"TakeOff Toward" );
    Calculate cal4 = new Calculate(runway2,12, 300, 60, -50, 3646, "Landing Toward");
    cal1.recalculate(1);
    cal2.recalculate(1);
    cal3.recalculate(1);
    cal4.recalculate(1);
    System.out.println("09L (Take Off Away, Landing Over)");
    assertEquals("09L TORA", 3346, cal1.getRunway().getNewParameters().getTora(), 0.01);
    assertEquals("09L TODA", 3346, cal1.getRunway().getNewParameters().getToda(), 0.01);
    assertEquals("09L ASDA", 3346, cal1.getRunway().getNewParameters().getAsda(), 0.01);
    assertEquals("09L LDA", 2985, cal2.getRunway().getNewParameters().getLda(), 0.01);
    System.out.println("27R (Take Off Towards, Landing Towards)");
    assertEquals("27R TORA", 2986, cal3.getRunway().getNewParameters().getTora(), 0.01);
    assertEquals("27R TODA", 2986, cal3.getRunway().getNewParameters().getToda(), 0.01);
    assertEquals("27R ASDA", 2986, cal3.getRunway().getNewParameters().getAsda(), 0.01);
    assertEquals("27R LDA", 3346, cal4.getRunway().getNewParameters().getLda(), 0.01);
    System.out.println("");
  }

  @Test(timeout = 5000)
  public void test2() {
    Parameters parameters1 = new Parameters(3902, 3902,3902,3595,240);
    Runway runway1 = new Runway(9, 306, "L", parameters1);
    Parameters parameters2 = new Parameters(3884, 3962,3884,3884,240);
    Runway runway2 = new Runway(27, 0, "R", parameters2);
    Calculate cal1 = new Calculate(runway2,20, 300, 60, 50, 3546, "TakeOff Away");
    Calculate cal2 = new Calculate(runway2,20, 300, 60, 50, 3546, "Landing Over");
    Calculate cal3 = new Calculate(runway1,20,300,60,50,3546,"TakeOff Toward" );
    Calculate cal4 = new Calculate(runway1,20, 300, 60, 50, 3546, "Landing Toward");
    cal1.recalculate(1);
    cal2.recalculate(1);
    cal3.recalculate(1);
    cal4.recalculate(1);
    System.out.println("27R (Take Off Away, Landing Over)");
    assertEquals("27R TORA", 3534, cal1.getRunway().getNewParameters().getTora(), 0.01);
    assertEquals("27R TODA", 3612, cal1.getRunway().getNewParameters().getToda(), 0.01);
    assertEquals("27R ASDA", 3534, cal1.getRunway().getNewParameters().getAsda(), 0.01);
    assertEquals("27R LDA", 2774, cal2.getRunway().getNewParameters().getLda(), 0.01);
    System.out.println("09L (Take Off Towards, Landing Towards)");
    assertEquals("09L TORA", 2792, cal3.getRunway().getNewParameters().getTora(), 0.01);
    assertEquals("09L TODA", 2792, cal3.getRunway().getNewParameters().getToda(), 0.01);
    assertEquals("09L ASDA", 2792, cal3.getRunway().getNewParameters().getAsda(), 0.01);
    assertEquals("09L LDA", 3246, cal4.getRunway().getNewParameters().getLda(), 0.01);
    System.out.println("");
  }

  @Test(timeout = 5000)
  public void test3() {
    Parameters parameters1 = new Parameters(3660, 3660,3660,3353,240);
    Runway runway1 = new Runway(9, 307, "R", parameters1);
    Parameters parameters2 = new Parameters(3660, 3660,3660,3660,240);
    Runway runway2 = new Runway(27, 0, "L", parameters2);
    Calculate cal1 = new Calculate(runway2,25, 300, 60, 500, 2853, "TakeOff Away");
    Calculate cal2 = new Calculate(runway2,25, 300, 60, 500, 2853, "Landing Over");
    Calculate cal3 = new Calculate(runway1,25,300,60,500,2853,"TakeOff Toward" );
    Calculate cal4 = new Calculate(runway1,25, 300, 60, 500, 2853, "Landing Toward");
    cal1.recalculate(1);
    cal2.recalculate(1);
    cal3.recalculate(1);
    cal4.recalculate(1);
    System.out.println("27L (Take Off Away, Landing Over)");
    assertEquals("27L TORA", 2860, cal1.getRunway().getNewParameters().getTora(), 0.01);
    assertEquals("27L TODA", 2860, cal1.getRunway().getNewParameters().getToda(), 0.01);
    assertEquals("27L ASDA", 2860, cal1.getRunway().getNewParameters().getAsda(), 0.01);
    assertEquals("27L LDA", 1850, cal2.getRunway().getNewParameters().getLda(), 0.01);
    System.out.println("9R (Take Off Towards, Landing Towards)");
    assertEquals("9R TORA", 1850, cal3.getRunway().getNewParameters().getTora(), 0.01);
    assertEquals("9R TODA", 1850, cal3.getRunway().getNewParameters().getToda(), 0.01);
    assertEquals("9R ASDA", 1850, cal3.getRunway().getNewParameters().getAsda(), 0.01);
    assertEquals("9R LDA", 2553, cal4.getRunway().getNewParameters().getLda(), 0.01);
    System.out.println("");
  }

  @Test(timeout = 5000)
  public void test4() {
    Parameters parameters1 = new Parameters(3660, 3660,3660,3353,240);
    Parameters parameters2 = new Parameters(3660, 3660,3660,3660,240);
    Runway runway1 = new Runway(27, 0, "L", parameters2);
    Runway runway2 = new Runway(9, 307, "R", parameters1);
    Calculate cal1 = new Calculate(runway2,15, 300, 60, 150, 3203, "TakeOff Away");
    Calculate cal2 = new Calculate(runway2,15, 300, 60, 150, 3203, "Landing Over");
    Calculate cal3 = new Calculate(runway1,15,300,60,150,3203,"TakeOff Toward" );
    Calculate cal4 = new Calculate(runway1,15, 300, 60, 150, 3203, "Landing Toward");
    cal1.recalculate(1);
    cal2.recalculate(1);
    cal3.recalculate(1);
    cal4.recalculate(1);
    System.out.println("27L (Take Off Towards, Landing Towards)");
    assertEquals("27L TORA", 2393, cal3.getRunway().getNewParameters().getTora(), 0.01);
    assertEquals("27L TODA", 2393, cal3.getRunway().getNewParameters().getToda(), 0.01);
    assertEquals("27L ASDA", 2393, cal3.getRunway().getNewParameters().getAsda(), 0.01);
    assertEquals("27L LDA", 2903, cal4.getRunway().getNewParameters().getLda(), 0.01);
    System.out.println("9R (Take Off Away, Landing Over)");
    assertEquals("9R TORA", 2903, cal1.getRunway().getNewParameters().getTora(), 0.01);
    assertEquals("9R TODA", 2903, cal1.getRunway().getNewParameters().getToda(), 0.01);
    assertEquals("9R ASDA", 2903, cal1.getRunway().getNewParameters().getAsda(), 0.01);
    assertEquals("9R LDA", 2393, cal2.getRunway().getNewParameters().getLda(), 0.01);
    System.out.println("");
  }

  Parameters sampleParams = new Parameters(1, 1, 1, 1);
  private Runway testRunway;
  @Rule
  public ExpectedException exception = ExpectedException.none();
  public String outpuParameters(Parameters params) {
    if (params != null)
      return "TORA: " + String.valueOf(params.getTora()) + ", TODA: "
              + String.valueOf(params.getToda()) + ", ASDA: "
              + String.valueOf(params.getAsda()) + ", LDA: "
              + String.valueOf(params.getLda());

    return null;
  }

  public boolean equalRunwayParameters(Parameters expected, Parameters actual) {
    return (expected.getTora() == actual.getTora() && expected.getToda() == actual.getToda()
            && expected.getAsda() == actual.getAsda()
            && expected.getLda() == actual.getLda());
  }

  @Test
  @junitparams.Parameters(method = "testDataForException")
  public void testException(int heading, double displacedThreshold, String position,
                            Class errorClass, String errorMsg) {
    if (errorClass != null)
      exception.expect(errorClass);
    if (errorMsg != null)
      exception.expectMessage(errorMsg);

    testRunway = new Runway(heading, displacedThreshold, position, sampleParams);
  }

  private String errorPosition = "Invalid position. Can be 'L','R','C'";
  private String errorHeading = "Heading should be in 1-36";
  private String errorThreashold = "Threashold can't be negative";

  private Object[] testDataForException() {
    return new Object[] {new Object[] {0, 307, "L", IllegalArgumentException.class, errorHeading},
            new Object[] {-1, 307, "R", IllegalArgumentException.class, errorHeading},
            new Object[] {37, 456, "N", IllegalArgumentException.class, errorHeading},
            new Object[] {1, 0, "L", null, null},
            new Object[] {10, -1, "L", IllegalArgumentException.class, errorThreashold},
            new Object[] {36, 1, "A", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "B", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "C", null, null},
            new Object[] {36, 1, "D", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "E", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "F", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "G", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "H", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "I", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "J", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "K", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "L", null, null},
            new Object[] {36, 1, "M", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "N", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "O", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "P", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "Q", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "R", null, null},
            new Object[] {36, 1, "S", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "T", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "U", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "V", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "W", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "X", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "Y", IllegalArgumentException.class, errorPosition},
            new Object[] {36, 1, "Z", IllegalArgumentException.class, errorPosition},
            new Object[] {1, 1, "L", null, null},
            new Object[] {36, 55, "C", null, null}};
  }
  
  @Test
  @junitparams.Parameters(method = "testDataForRecalculatedParameter")
  public void testRecalculatedParameter(Runway runway, double tora,
                                                     double toda, double asda, double lda, Parameters expected) {
    runway.getParameters().setTora(tora);
    runway.getParameters().setToda(toda);
    runway.getParameters().setAsda(asda);
    runway.getParameters().setLda(lda);

    assertTrue("Recalculated Parameter Test Failed." + "Expected: "
                    + outpuParameters(expected) + " Actual: "
                    + outpuParameters(runway.getParameters()),
            equalRunwayParameters(expected, runway.getParameters()));
  }

  @Test
  @junitparams.Parameters(method = "testDataForGetParameters")
  public void testGetParameters(int heading, double threshold, String position,
                                Parameters params) {
    Runway runway = new Runway(heading, threshold, position, params);
    assertEquals("getParameters() Test Failed. Expected: " + outpuParameters(params)
                    + " Actual: " + outpuParameters(runway.getParameters()), params,
            runway.getParameters());
  }

  private Calculate calc = new Calculate();
  @Test
  @junitparams.Parameters(method = "testDataForRunwayCalc")
  public void testRunwayCalc(Runway logical1, String errorMsg) {
    if (logical1 == null)
      exception.expect(IllegalArgumentException.class);

    if (errorMsg != null)
      exception.expectMessage(errorMsg);

    calc.setRunway(logical1);
    assertTrue("Runway should equal runway: ",
            calc.getRunway() == logical1);
  }

  private Object[] testDataForRecalculatedParameter() {
    return new Object[] {
            new Object[] {new Runway(1, 434, "L",
                    new Parameters(1, 1, 1, 1)), 3660, 3661, 3662, 3663,
                    new Parameters(3660, 3661, 3662, 3663)},
            new Object[] {new Runway(9, 555, "C",
                    new Parameters(3660, 3660, 3660, 3660)), 1001, 5678,
                    3344, 5454, new Parameters(1001, 5678, 3344, 5454)},
            new Object[] {new Runway(10, 856, "L",
                    new Parameters(1234, 1111, 4321, 2222)), 5621, 1321,
                    888, 4091, new Parameters(5621, 1321, 888, 4091)},
            new Object[] {new Runway(11, 74, "C",
                    new Parameters(1111, 2222, 3333, 4444)), 253, 990,
                    890, 2131, new Parameters(253, 990, 890, 2131)},};
  }

  private Object[] testDataForGetParameters() {
    return new Object[] {new Object[] {1, 78, "L", new Parameters(1, 1, 1, 1)},
            new Object[] {9, 90, "C", new Parameters(3660, 3660, 3660, 3660)},
            new Object[] {10, 789, "L", new Parameters(1234, 1111, 4321, 2222)},
            new Object[] {11, 981, "C", new Parameters(1111, 2222, 3333, 4444)},
            new Object[] {36, 475, "R", new Parameters(123, 234, 345, 567)},
            new Object[] {1,0,"C",new Parameters(1,1,1,1)},
            new Object[] {36,Double.MAX_VALUE,"C", new Parameters(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE)}};
  }

  private Object[] testDataForRunwayCalc() {
    Parameters params = new Parameters(1, 2, 3, 4);
    return new Object[] { new Object[] {new Runway(9, 111, "L", params), null},
            new Object[] {new Runway(9, 111, "L", params), null},
            new Object[]  {null, "Error. Invalid runway, cannot be null."}};
  }
}