package uk.ac.soton.seg15.model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.beans.property.*;
import uk.ac.soton.seg15.App;
import uk.ac.soton.seg15.controller.Controller;

import java.util.Objects;

public class Model {

    private static final Logger logger = LogManager.getLogger(Model.class);
    private Controller controller;
    private ObjectProperty<Runway> curRunway;
    private Airport curAirport;
    private ObjectProperty<Obstacle> curObst;
    private DoubleProperty eba = new SimpleDoubleProperty(300);
    private DoubleProperty stripEnd = new SimpleDoubleProperty(60);
    private String finalNoti;

    public Model (Controller controller){
        this.controller = controller;

        //TEMPORARY RUNWAY FOR CALCULATIONS
        Parameters param = new Parameters(3902,3902,3902,3595,240);
        curRunway = new SimpleObjectProperty(new Runway(27,60,"L",param));
        curObst = new SimpleObjectProperty(new Obstacle());

        logger.info("finalNoti" + this.finalNoti);
        controller.setSimpleRunwayProperty(curRunway,curObst,this.finalNoti);
        controller.setDefaultParameters(stripEnd, eba);

    }

    public void calculate(String action, int direction){
        logger.info("current obstacle = " + curObst.get().getName());
        if(curObst.get().getName() == null)
            return;

        Calculate calculate;

        if(curAirport == null){
            calculate = new Calculate(curRunway.get(), curObst.get().getHeight(),eba.get(),stripEnd.get(),curObst.get().getDistanceFromLeftThreshold(),curObst.get().getDistanceFromRightThreshold(),action);
        } else {
            calculate = new Calculate(curRunway.get(), curObst.get().getHeight(),eba.get(),stripEnd.get(),curObst.get().getDistanceFromLeftThreshold(),curObst.get().getDistanceFromRightThreshold(),action,curAirport.getAscent(),curAirport.getDescent());
        }

        calculate.recalculate(direction);
        curRunway.set(calculate.getRunway());
        logger.info("finalNoti" + this.finalNoti);
        controller.setSimpleRunwayProperty(curRunway, curObst,this.finalNoti);
    }

    public void setStripEnd (double stripEnd){
        DoubleProperty old = this.stripEnd;
        this.stripEnd.set(stripEnd);
        logger.info("new and old" + old + stripEnd);
        if (old.doubleValue() != stripEnd){
            logger.info("new and old" + old + stripEnd);
            this.finalNoti = this.finalNoti + " Strip End";
        }
    }

    public void setBlastDistance (double blastDistance){
        DoubleProperty old = this.eba;
        this.eba.set(blastDistance);
        if (old.doubleValue() != blastDistance){
            finalNoti += " Blast Distance ";
        }

    }

    public void setTORA(double tora){
        double old = curRunway.get().getParameters().getTora();
        curRunway.get().getParameters().setTora(tora);
        if (old != tora){
            finalNoti += " TORA";
        }
    }

    public void setTODA(double toda){
        double old = curRunway.get().getParameters().getToda();
        curRunway.get().getParameters().setToda(toda);
        if (old != toda){
            finalNoti += " TODA";
        }
    }

    public void setASDA(double asda){
        double old = curRunway.get().getParameters().getAsda();
        curRunway.get().getParameters().setAsda(asda);
        if (old != asda){
            finalNoti += " ASDA";
        }
    }

    public void setLDA(double lda){
        double old = curRunway.get().getParameters().getLda();
        curRunway.get().getParameters().setLda(lda);
        if (old != lda){
            finalNoti += " LDA";
        }
    }

    public void setRESA(double resa){
        double old = curRunway.get().getParameters().getResa();
        curRunway.get().getParameters().setResa(resa);
        if (old != resa){
            finalNoti += " RESA";
        }
    }

    public void setThresh(double thresh){
        double old = curRunway.get().getThreshold();
        curRunway.get().setThreshold(thresh);
        logger.info("new and old" + old + thresh);
        if (old != thresh){
            finalNoti += " Threshold";
        }
    }

    public void setObstHeight(double obstHeight){
        double old = curObst.get().getHeight();
        curObst.get().setHeight(obstHeight);
        if (old != obstHeight){
            finalNoti += " Obstacle Height";
        }
    }

    public void setObstWidth(double obstWidth){
        double old = curObst.get().getWidth();
        curObst.get().setWidth(obstWidth);
        if (old != obstWidth){
            finalNoti += " Obstacle Width";
        }
    }

    public void setHeading(int heading){
        double old = curRunway.get().getHeading();
        curRunway.get().setHeading(heading);
        logger.info("new and old" + old + heading);
        if (old != heading){
            finalNoti += " Heading";
        }
    }

    public void setPosition(String position){
        String old = curRunway.get().getPosition();
        curRunway.get().setPosition(position);
        if (old != position){
            finalNoti += " Position";
        }
    }

    public void setobsName(String name){
        String old = curObst.get().getName();
        curObst.get().setName(name);
        if (!Objects.equals(old, name)){
            finalNoti += " Obstacle Name";
        }
    }

    public void setdistCentreLine(double dist){
        double old = curObst.get().getDistanceToCentreLine();
        curObst.get().setDistanceToCentreLine(dist);
        if (old != dist){
            finalNoti += " Distance to Centre Line";
        }
    }

    public void setlThreshDist(double lThreshDist){
        double old = curObst.get().getDistanceFromLeftThreshold();
        curObst.get().setDistanceFromLeftThreshold(lThreshDist);
        if (old != lThreshDist){
            finalNoti += " Distance to Left Threshold";
        }
    }

    public void setrThreshDist(double rThreshDist){
        double old = curObst.get().getDistanceFromRightThreshold();
        curObst.get().setDistanceFromRightThreshold(rThreshDist);
        if (old != rThreshDist){
            finalNoti += " Distance to Right Threshold";
        }
    }

    public ObjectProperty<Runway> getCurRunway(){return curRunway;}

    public ObjectProperty<Obstacle> getCurObst() {return curObst;}

    public DoubleProperty getEba() {return eba;}

    public DoubleProperty getStripEnd() {return stripEnd;}


    public void setFinalNoti(String finalNoti) {
        this.finalNoti = finalNoti;
    }

    public void notiCollect() {
        controller.setSimpleRunwayProperty(curRunway, curObst,this.finalNoti);
    }

    public void setCurAirport(Airport airport){
        this.curAirport = airport;
    }
}
