package uk.ac.soton.seg15.model;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "runway")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Runway {
  private int heading;
  private String position;
  private double threshold;

  private double clearway;
  private double stopway;

  private Parameters mParameters;
  private Parameters newParameters;


  public Runway(int heading, double threshold, String position, Parameters params) {
    if (heading < 1 | heading > 36)
      throw new IllegalArgumentException("Heading should be in 1-36");
    if (threshold < 0)
      throw new IllegalArgumentException("Threashold can't be negative");
    if (!position.equals("L") && !position.equals("R") && !position.equals("C"))
      throw new IllegalArgumentException("Invalid position. Can be 'L','R','C'");

    this.heading = heading;
    this.threshold = threshold;
    this.position = position;
    this.mParameters = params;

    this.newParameters = new Parameters(mParameters.getTora(), mParameters.getToda(), mParameters.getAsda(), mParameters.getLda());

  }
  public Runway() {
  }

  @XmlElement(name = "heading")
  public int getHeading() {
    return this.heading;
  }


  public void setHeading(int heading) {
    this.heading = heading;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  @XmlElement(name = "position")
  public String getPosition() {
    return this.position;
  }

  @XmlElement(name = "threshold")
  public double getThreshold() {
    return this.threshold;
  }

  public void setThreshold(double threshold) {
    this.threshold = threshold;
  }

  @XmlElement(name = "mParameters")
  public Parameters getParameters() {
    return this.mParameters;
  }

  public void setParameters(Parameters mParameters) {
    this.mParameters = mParameters;
  }

  @XmlTransient
  public Parameters getNewParameters() {
    return this.newParameters;
  }

  public void setNewParameters(Parameters newParameters) {
    this.newParameters = newParameters;
  }

  public static Runway[] runwayArray() {
    Runway[] runways = new Runway[4];
    //this obstacle is on the centreline
    Parameters parameters2 = new Parameters(3884, 3962,3884,3884,240);
    runways[0] = new Runway(27, 0, "R", parameters2);
    Parameters parameters1 = new Parameters(3902, 3902,3902,3595,240);
    runways[1] = new Runway(9, 306, "L", parameters1);
    Parameters parameters3 = new Parameters(3660, 3660,3660,3353,240);
    runways[2] = new Runway(9, 307, "R", parameters3);
    Parameters parameters4 = new Parameters(3660, 3660,3660,3660,240);
    runways[3] = new Runway(27, 0, "L", parameters4);
    return runways;
  }

}

