package uk.ac.soton.seg15.view.components;

public class RunwayParam {
  private String name;
  private double originalVal;
  private double recalcVal;

  public RunwayParam(String name, double originalVal, double recalcVal){
    this.name = name;
    this.originalVal = originalVal;
    this.recalcVal = recalcVal;
  }

  public String getName() {
    return name;
  }

  public double getOriginalVal() {
    return originalVal;
  }

  public double getRecalcVal() {
    return recalcVal;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOriginalVal(double originalVal) {
    this.originalVal = originalVal;
  }

  public void setRecalcVal(double recalcVal) {
    this.recalcVal = recalcVal;
  }

}
