package uk.ac.soton.seg15.controller;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.stage.Stage;
import uk.ac.soton.seg15.model.Airport;
import uk.ac.soton.seg15.model.Model;
import uk.ac.soton.seg15.view.View;

public class Controller {
    private View view;
    private Model model;

    public Controller(Stage stage) {

        view = new View(stage, this);

        model = new Model(this);

    }

    public void setSimpleRunwayProperty(ObjectProperty runway, ObjectProperty obstacle, String finalNoti){
        view.setRunway(runway);
        view.setObstacle(obstacle);
        view.setFinalNoti(finalNoti);

    }

    public void setDefaultParameters(DoubleProperty stripEnd, DoubleProperty eba) {
        view.setEba(eba);
        view.setStripEnd(stripEnd);
    }

    public void calculate(String action, int direction){
        model.calculate(action, direction);
    }

    public void setResa(double resa) {
        model.setRESA(resa);
    }

    public void setAsda(double asda) {
        model.setASDA(asda);
    }

    public void setStripEnd(double stripEnd) {
        model.setStripEnd(stripEnd);
    }

    public void setLda(double lda) {
        model.setLDA(lda);
    }

    public void setToda(double toda) {
        model.setTODA(toda);
    }


    public void setTora(double tora) {
        model.setTORA(tora);
    }

    public void setBlastProtect(double blastProtect) {
        model.setBlastDistance(blastProtect);
    }

    public void setLeftThresh(double leftThresh) {
        model.setlThreshDist(leftThresh);
    }

    public void setObstHeight(double obstHeight) {
        model.setObstHeight(obstHeight);
    }

    public void setRightThresh(double rightThresh) {
        model.setrThreshDist(rightThresh);
    }


    public void setThresh(double thresh) {
        model.setThresh(thresh);
    }

    public void setHeading(int heading){
        model.setHeading(heading);
    }

    public void setPosition(String position){
        model.setPosition(position);
    }
    public void setobsName(String name){
        model.setobsName(name);
    }

    public void setdistCentreLine(double dist){
        model.setdistCentreLine(dist);
    }

    public void setObstWidth(double obstWidth){
        model.setObstWidth(obstWidth);
    }

    public View getView() {return view;}

    public Model getModel() {return model;}


    public void setFinalNoti(String finalNoti) {
        model.setFinalNoti(finalNoti);
    }

    public void setBfinalNoti(String finalNoti) {
        view.setFinalNoti(finalNoti);
    }

    public void notiCollect() {
        model.notiCollect();
    }

    public void setCurAirport(Airport airport){
        model.setCurAirport(airport);
    }
}


