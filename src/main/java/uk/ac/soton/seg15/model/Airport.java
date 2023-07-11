package uk.ac.soton.seg15.model;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "airport")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Airport {
    public String name;
    public List<Runway> runways;
    public List<Obstacle> obstacles;
    public Double ascent;
    public Double descent;

    public Airport(){

    }

    public Airport(String name, List<Runway> runways, List<Obstacle> obstacles,Double ascent, Double descent){
        super();
        this.name = name;
        this.runways = runways;
        this.obstacles = obstacles;
        this.ascent = ascent;
        this.descent = descent;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    @XmlElement(name = "obstacles")
    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    @XmlElement(name = "runways")
    public List<Runway> getRunways() {
        return runways;
    }

    @XmlElement(name = "ascent")
    public Double getAscent() {
        return ascent;
    }

    @XmlElement(name = "descent")
    public Double getDescent() {
        return descent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAscent(Double ascent) {
        this.ascent = ascent;
    }

    public void setDescent(Double descent) {
        this.descent = descent;
    }

    public void setObstacles(List<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }


    public void setRunways(List<Runway> runways) {
        this.runways = runways;
    }

    public String getruns(){
        String res = "";
        for (int i = 0; i < this.runways.size(); i++){
            res = res + this.runways.get(i).getPosition() + this.runways.get(i).getHeading() ;
        }
        return res;
    }

}
