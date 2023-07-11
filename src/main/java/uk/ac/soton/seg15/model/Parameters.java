package uk.ac.soton.seg15.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mParameters")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Parameters {
  private double tora;
  private double toda;
  private double lda;
  private double asda;
  private double resa;
  private double clearway;
  private double stopway;

  public Parameters(double tora, double toda, double asda, double lda) {
    super();
    this.toda = toda;
    this.tora = tora;
    this.asda = asda;
    this.lda = lda;
    this.resa = 240;

    setClearway();
    setStopway();
  }

  public Parameters(){

  }

  public Parameters(double tora, double toda, double asda, double lda, double resa) {
    super();
    this.toda = toda;
    this.tora = tora;
    this.asda = asda;
    this.lda = lda;
    this.resa = resa;

  }
  @XmlElement(name = "tora")
  public double getTora() {
    return tora;
  }

  public double getClearway() {
    return clearway;
  }

  public double getStopway() {
    return stopway;
  }

  public void setClearway() {
    this.clearway = this.toda - this.asda;
  }

  public void setStopway() {
    this.stopway = this.asda - this.tora;
  }

  public void setTora(double tora) {
    this.tora = tora;
  }

  @XmlElement(name = "toda")
  public double getToda() {
    return toda;
  }

  public void setToda(double toda) {
    this.toda = toda;
  }

  @XmlElement(name = "asda")
  public double getAsda() {
    return asda;
  }

  @XmlElement(name = "lda")
  public double getLda() {
    return lda;
  }

  public void setAsda(double asda) {
    this.asda = asda;
  }

  public void setLda(double lda) {
    this.lda = lda;
  }

  @XmlElement(name = "resa")
  public double getResa() {
    return this.resa;
  }

  public void setResa(double resa) {
    this.resa = resa;
  }
}
