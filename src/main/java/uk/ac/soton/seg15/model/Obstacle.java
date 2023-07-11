package uk.ac.soton.seg15.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "obstacle")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Obstacle {

  private String name;
  private double height;
  private double width;
  private double distanceToCentreLine;
  private double distanceFromLeftThreshold;
  private double distanceFromRightThreshold;

  public Obstacle(){

  }

  public Obstacle (String name, double height, double width, double distanceToCentreLine, double distanceFromLeftThreshold, double distanceFromRightThreshold) {
      super();
      this.name = name;
      this.height = height;
      this.width = width;
      this.distanceToCentreLine = distanceToCentreLine;
      this.distanceFromLeftThreshold = distanceFromLeftThreshold;
      this.distanceFromRightThreshold = distanceFromRightThreshold;
    }



  @XmlElement(name = "distancetoCentreLine")
  public double getDistanceToCentreLine() {
    return distanceToCentreLine;
  }

  public void setDistanceToCentreLine(double distanceToCentreLine) {
    this.distanceToCentreLine = distanceToCentreLine;
  }
  @XmlElement(name = "distancefromLeftThreshold")
  public double getDistanceFromLeftThreshold() {
    return distanceFromLeftThreshold;
  }

  public void setDistanceFromLeftThreshold(double distanceFromLeftThreshold) {
    this.distanceFromLeftThreshold = distanceFromLeftThreshold;
  }
  @XmlElement(name = "distancefromRightThreshold")
  public double getDistanceFromRightThreshold() {
    return distanceFromRightThreshold;
  }

  public void setDistanceFromRightThreshold(double distanceFromRightThreshold) {
    this.distanceFromRightThreshold = distanceFromRightThreshold;
  }
  @XmlElement(name = "height")
  public double getHeight() {return height;}
  public void setHeight(double height) {this.height = height;}

  @XmlElement(name = "width")
  public double getWidth() {return width;}
  public void setWidth(double width) {this.width = width;}


  @XmlElement(name = "name")
  public String getName() {return name;}
  public void setName(String name){this.name = name;}


  //Array of predefined obstacles
  public static Obstacle[] obstacleArray() {
   Obstacle[] obstacles = new Obstacle[4];
    //this obstacle is on the centreline

   obstacles[0] = new Obstacle("Pothole", 0,5, 0,50, 3646);
   obstacles[1] = new Obstacle("Aeroplane", 25,40,20,500, 2853);
   obstacles[2] = new Obstacle("Broken part from aeroplane", 20,15, 20,3546, 50);
   obstacles[3] = new Obstacle("Personal Aircraft", 15,10, 60,3203, 150);

    return obstacles;
  }


  
}
