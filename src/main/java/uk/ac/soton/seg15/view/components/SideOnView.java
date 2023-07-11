package uk.ac.soton.seg15.view.components;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.seg15.model.ColorBlindness;
import uk.ac.soton.seg15.model.Airport;
import uk.ac.soton.seg15.model.Obstacle;
import uk.ac.soton.seg15.model.Runway;
import uk.ac.soton.seg15.view.View;

public class SideOnView extends Canvas {

    private static final Logger logger = LogManager.getLogger(SideOnView.class);
    private StringProperty scenarioType = new SimpleStringProperty();
    private GraphicsContext gc;

    /** Will need to be simpleInteger properties */
    private double obstacleWidth = 100;
    private double obstacleHeight = 25;
    private double obstacleXPos;
    private final double scaleFactor;
    private double runwayLength;
    private double xScale;
    private double yScale;
    private double stripEnd;
    private double borderx;
    private double bordery;
    private double runwayStripWidth;
    private Runway curRunway;

    private Airport curAirport;
    private Obstacle curObstacle;
    private double runwayBottom;
    private int direction = 1;
    private String leftDesignator = "";
    private String rightDesignator = "";

    private double leftEnd;

    private double rightEnd;

    private View view;

    public SideOnView(double width, double height, StringProperty type, View view ){
        super(width,height);
        this.view = view;

        //Default parameters
        curRunway = view.getRunway();
        curAirport = view.getAirport();
        curObstacle = null;

        stripEnd = 60;
        borderx = 10;
        bordery = 10;

        runwayLength = curRunway.getParameters().getToda();

        xScale = (runwayLength + 2*stripEnd + 2*borderx) / width;
        yScale = (300 + bordery*2) / height;

        scaleFactor = getWidth()/runwayLength;

        //EXAMPLE OBSTACLE
        obstacleXPos = runwayLength + borderx + stripEnd;
        obstacleHeight = 0;

        //Setting up drawing
        gc = this.getGraphicsContext2D();
        gc.setFont(new Font(Font.getDefault().getName(), 16));

        buildBase();

        scenarioType.bind(type);

        scenarioType.addListener((observable, old, newVal) -> {
            buildBase();
            if(newVal == null){
                return;
            }
            switch (newVal) {
                case "Landing Over":
                    landingOver();
                    break;
                case "Landing Toward":
                    landingToward();
                    break;
                case "TakeOff Toward":
                    takeOffToward();
                    break;
                case "TakeOff Away":
                    takeOffAway();
                    break;
            }
            if(curObstacle != null){
                buildObstacle(curObstacle.getWidth()/xScale,curObstacle.getHeight()/yScale,obstacleXPos/xScale);
            }
        });


    }

    public void updateView() {
        curRunway = view.getRunway();

        runwayLength = curRunway.getParameters().getToda();
        xScale = (runwayLength + 2*stripEnd + 2*borderx) / getWidth();
        yScale = (300 + bordery*2) / getHeight();

        curObstacle = view.getObstacle();
        obstacleXPos = (runwayLength + borderx + stripEnd) - curObstacle.getDistanceFromLeftThreshold();
        obstacleHeight = curObstacle.getHeight();

        buildBase();
        buildObstacle(curObstacle.getWidth()/xScale,curObstacle.getHeight()/yScale,obstacleXPos/xScale);
        if(scenarioType.isNull().get()) return;
        switch (scenarioType.getValue()) {
            case "Landing Over":
                landingOver();
                break;
            case "Landing Toward":
                landingToward();
                break;
            case "TakeOff Toward":
                takeOffToward();
                break;
            case "TakeOff Away":
                takeOffAway();
                break;
        }

    }

    private void landingOver(){
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.setFill(Color.BLACK);
        double arrowHeadLength = 10;

        //Display Landing Direction
        drawLandingDirection(direction == 1 ? "left" : "right", "Landing Direction", arrowHeadLength);

        //REQUIRE CALCULATED VAL FOR LDA
        double lda = direction*this.curRunway.getNewParameters().getLda();
        double resa = direction*curRunway.getParameters().getResa();

        double groundY = runwayBottom + 10;
        double yOffset = 20; //offset groundY by given value

        double xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos-runwayLength)/xScale;
        var xEnd = xStart;

        double descent;
        if (curAirport != null) {
            descent = curAirport.getDescent();
        } else {
            descent = 50;
        }
        //Display h x 50
        if(curObstacle.getName() != null){
            xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos+curObstacle.getWidth())/xScale;
            if(obstacleHeight*descent > resa) {
                xEnd = xStart - direction*((obstacleHeight * descent) / xScale);
                gc.fillText("h x " + descent + " = " + obstacleHeight*descent + "m", (xStart + xEnd)/2, groundY + gc.getFont().getSize() + yOffset);
            } else {
                xEnd = xStart - (resa / xScale);
                gc.fillText("RESA = " + Math.abs(resa) + "m", xEnd, groundY + gc.getFont().getSize()+ yOffset);
            }
            drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY + yOffset, groundY,
                arrowHeadLength, Color.BLACK);

            this.buildALS(xEnd);
            buildALSRatio(descent);

            //Display 60m
            xStart = xEnd;
            xEnd = xStart - direction*stripEnd/xScale;
            drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY, groundY,
                arrowHeadLength, ColorBlindness.daltonizeCorrect(Color.GOLD));
            gc.fillText(stripEnd + "m", direction == 1 ? xEnd : xStart, groundY + gc.getFont().getSize());
        }

        //Display LDA
        xStart = xEnd;
        xEnd = xStart - lda/xScale ;
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY + yOffset, groundY,
            arrowHeadLength, ColorBlindness.daltonizeCorrect(Color.DARKGREEN));
        gc.fillText("LDA = " + Math.abs(lda) + "m" , (xStart + xEnd)/2, groundY + gc.getFont().getSize() + yOffset);

    }

    private void landingToward(){
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.setFill(Color.BLACK);
        double arrowHeadLength = 10;

        //Display direction of landing
        drawLandingDirection(direction == 1 ? "right" : "left", "Landing Direction", arrowHeadLength);

        double lda = direction*this.curRunway.getNewParameters().getLda();
        double resa = direction*curRunway.getParameters().getResa();

        double groundY = runwayBottom + 10;
        double yOffset = 20; //offset groundY by given value

        double xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos-runwayLength)/xScale;
        var xEnd = xStart;
        if(curObstacle.getName() != null){
            //Display RESA
            xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos+curObstacle.getWidth())/xScale;
            xEnd = xStart - (resa/xScale);
            drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY + yOffset, groundY,
                arrowHeadLength, Color.BLACK);
            gc.fillText("RESA = " + Math.abs(resa) + "m", direction == 1 ? xEnd : xStart, groundY + gc.getFont().getSize() + yOffset);

            //Display 60m
            xStart = xEnd;
            xEnd = xStart - direction*(stripEnd/xScale);
            drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY, groundY,
                arrowHeadLength, ColorBlindness.daltonizeCorrect(Color.GOLD));
            gc.fillText(stripEnd + "m", direction == 1 ? xEnd : xStart, groundY + gc.getFont().getSize());

        }

        //Display LDA
        xStart = xEnd;
        xEnd = xStart - lda/xScale;
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY + yOffset, groundY,
            arrowHeadLength, ColorBlindness.daltonizeCorrect(Color.DARKGREEN));
        gc.fillText("LDA = " + Math.abs(lda) + "m" , (xStart + xEnd)/2, groundY + gc.getFont().getSize() + yOffset);

    }

    private void takeOffAway(){
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.setFill(Color.BLACK);
        double arrowHeadLength = 10;

        //Display Take-Off Direction
        drawLandingDirection(direction == 1 ? "left" : "right", "TakeOff Direction", arrowHeadLength);

        //REQUIRE VALUES FOR TORA, TODA, ASDA
        double eba = direction * view.getEBA();
        double tora = direction*this.curRunway.getNewParameters().getTora();
        double toda = direction*this.curRunway.getNewParameters().getToda();
        double asda = direction*this.curRunway.getNewParameters().getAsda();

        double groundY = runwayBottom + 10;
        double yOffset = 40; //offset groundY by given value

        //Display Engine Blast Allowance
        var xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos-runwayLength)/xScale;
        var xEnd = xStart;
        if (curObstacle.getName() != null) {
            xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos+curObstacle.getWidth())/xScale;
            xEnd = xStart - (eba/xScale);
            drawHorizontalArrow(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY, arrowHeadLength);
            gc.fillText("EBA = " + Math.abs(eba) + "m", direction == 1 ? xEnd : xStart, groundY + gc.getFont().getSize());
        }

        //Display Displaced Threshold
//        double DT = this.curRunway.getThreshold();
//        if (DT > 0) {
//            var xDTEnd = xEnd;
//            var xDTStart = xDTEnd + (DT/xScale) ;
//            double y = groundY + yOffset + 30;
//            drawHorizontalArrow(xDTEnd, xDTStart, y, arrowHeadLength);
//            gc.fillText("DT = " + DT + "m", xDTEnd, y + gc.getFont().getSize());
//        }

//        double clearWay = curRunway.getNewParameters().getClearway();
//        var xCWStart = obstacleXPos;
//        var xCWEnd = xCWStart - (clearWay/xScale);
//        drawHorizontalArrow(xCWEnd, xCWStart, groundY + yOffset, arrowHeadLength);
//        gc.fillText("ClearWay = " + clearWay + "m", xCWEnd, groundY + yOffset + gc.getFont().getSize());

        //Display TORA
        xStart = xEnd ;
        xEnd = xStart - tora/xScale;
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY, groundY,
            arrowHeadLength,ColorBlindness.daltonizeCorrect(Color.BLUE));
        gc.fillText("TORA = " + Math.abs(tora) + "m" , (xStart + xEnd)/2, groundY + gc.getFont().getSize());

        //Display TODA
        xEnd = xStart - toda/xScale;
        groundY += yOffset;
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY, groundY-yOffset,
            arrowHeadLength, ColorBlindness.daltonizeCorrect(Color.ORANGERED));
        gc.fillText("TODA = " + Math.abs(toda) + "m" , (xStart + xEnd)/2, groundY + gc.getFont().getSize());

        //Display ASDA
        xEnd = xStart - asda/xScale;
        groundY += yOffset;
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY, groundY-yOffset,
            arrowHeadLength, ColorBlindness.daltonizeCorrect(Color.PURPLE));
        gc.fillText("ASDA = " + Math.abs(asda) + "m" , (xStart + xEnd)/2, groundY + gc.getFont().getSize());
    }

    private void takeOffToward(){
        //Set line settings
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.setFill(Color.BLACK);
        var arrowHeadLength = 10;

        //Display direction of take-off
        drawLandingDirection(direction == 1 ? "right" : "left", "TakeOff Direction", arrowHeadLength);

        double resa = direction*curRunway.getParameters().getResa();
        double tora = direction*this.curRunway.getNewParameters().getTora();
        double toda = direction*this.curRunway.getNewParameters().getToda();
        double asda = direction*this.curRunway.getNewParameters().getAsda();

        var groundY = runwayBottom + 10;
        double yOffset = 40; //offset groundY by given value

        //Display h x ascent
        double ascent;
        if(curAirport != null){
            ascent = curAirport.getAscent();
        } else {
            ascent = 50;
        }
        var xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos-runwayLength)/xScale;
        var xEnd = xStart;
        if (curObstacle.getName() != null) {
            xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos+curObstacle.getWidth())/xScale;
            if(obstacleHeight*ascent > resa) {
                xEnd = xStart - direction*((obstacleHeight * ascent) / xScale);
                gc.fillText("h x " + ascent + " = " + obstacleHeight*ascent + "m", (xStart + xEnd)/2, groundY + gc.getFont().getSize() + yOffset);
            } else {
                xEnd = xStart - (resa / xScale);
                gc.fillText("RESA = " + Math.abs(resa) + "m", direction == 1 ? xEnd : xStart, groundY + gc.getFont().getSize() + yOffset);
            }
            drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY + yOffset, groundY,
                arrowHeadLength, Color.BLACK);

            buildALS(xEnd);
            buildALSRatio(ascent);

            //Display Strip end (60 m)
            xStart = xEnd;
            xEnd = xStart - direction*(stripEnd/xScale);
            drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY, groundY,
                arrowHeadLength, ColorBlindness.daltonizeCorrect(Color.GOLD));

            gc.fillText(stripEnd + "m", xEnd, groundY + gc.getFont().getSize());

        }

        //Display TORA
        xStart = xEnd ;
        xEnd = xStart - tora/xScale;
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY, groundY,
            arrowHeadLength,ColorBlindness.daltonizeCorrect(Color.BLUE));
        gc.fillText("TORA = " + Math.abs(tora) + "m" , (xStart + xEnd)/2, groundY + gc.getFont().getSize());

        //Display TODA
        xEnd = xStart - toda/xScale;
        groundY += yOffset;
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY, groundY-yOffset,
            arrowHeadLength, ColorBlindness.daltonizeCorrect(Color.ORANGERED));
        gc.fillText("TODA = " + Math.abs(toda) + "m" , (xStart + xEnd)/2, groundY + gc.getFont().getSize());

        //Display ASDA
        xEnd = xStart - asda/xScale;
        groundY += yOffset;
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, groundY, groundY-yOffset,
            arrowHeadLength, ColorBlindness.daltonizeCorrect(Color.PURPLE));
        gc.fillText("ASDA = " + Math.abs(asda) + "m" , (xStart + xEnd)/2, groundY + gc.getFont().getSize());
    }

    protected void buildBase(){
        gc.setFill(ColorBlindness.daltonizeCorrect(Color.CORNFLOWERBLUE));
        //SKY
        gc.fillRect(0,0,getWidth(),getHeight());

        //RUNWAY
        gc.setFill(ColorBlindness.daltonizeCorrect(Color.LIGHTGRAY));
        gc.fillRect((borderx + stripEnd)/xScale, getHeight() / 2, getWidth() - ((borderx + stripEnd + stripEnd)/xScale),10);

        runwayBottom = (getHeight() / 2) + 10;
        leftEnd = (borderx + stripEnd)/xScale;
        rightEnd = leftEnd + (getWidth() - ((borderx + stripEnd + stripEnd)/xScale));

        //GRASS
        gc.setFill(ColorBlindness.daltonizeCorrect(Color.LIGHTGREEN));
        gc.fillRect(0,(getHeight() / 2) +10, getWidth(), getHeight()/2);

        buildScale();
        buildDesignators();
        buildExtraInfo();
    }

    private void buildExtraInfo(){
        var yOffset = 40;
        var arrowheadLength = 10;
        //Displaced threshold
        var xStart = (borderx + stripEnd)/xScale;
        var xEnd = xStart + view.getRunway().getThreshold()/xScale;
        double runwayY = getHeight()/2;

        if (view.getRunway().getThreshold() != 0){
            drawArrowWithDottedLines(xStart, xEnd,runwayY - yOffset, runwayY,
                arrowheadLength, Color.BLACK);

            gc.fillText("DT = " + view.getRunway().getThreshold(), xStart,
                runwayY - yOffset + gc.getFont().getSize());

        }

        //Stopway
        xStart = (borderx + stripEnd + view.getRunway().getParameters().getTora())/xScale;

        xEnd = xStart + view.getRunway().getParameters().getStopway();

        if (view.getRunway().getParameters().getStopway() != 0){
            drawArrowWithDottedLines(xStart, xEnd,runwayY - yOffset, runwayY,
                arrowheadLength, Color.BLACK);

            gc.fillText("Stopway = " + view.getRunway().getParameters().getStopway(), (xStart+xEnd)/2,
                runwayY - yOffset - gc.getFont().getSize());

        }

        yOffset += 40;
        //Clearway
        xStart = (borderx + stripEnd + view.getRunway().getParameters().getTora())/xScale;
        xEnd = xStart + view.getRunway().getParameters().getClearway();

        if (view.getRunway().getParameters().getClearway() != 0){
            drawArrowWithDottedLines(xStart, xEnd,runwayY - yOffset, runwayY,
                arrowheadLength, Color.BLACK);

            gc.fillText("Clearway = " + view.getRunway().getParameters().getClearway(), (xStart+xEnd)/2,
                runwayY - yOffset - gc.getFont().getSize());

        }

    }

    private void buildScale(){
        gc.setLineWidth(2);
        gc.setStroke(Color.WHITE);
        gc.setFill(Color.WHITE);

        double xStart = 20;
        double yStart = 20;

        //x-axis scale
        double xCompare = 200;
        drawHorizontalArrow(xStart, xStart + (xCompare/xScale), yStart, 10);
        double xOffset = 10;
        gc.fillText(xCompare+ "m", xStart + (xCompare/xScale) + xOffset, yStart);

        //y-axis scale
        double yCompare = 40;
        drawVerticalArrow(yStart, yStart + (yCompare/yScale), xStart, 10 );
        double yOffset = 10;
        gc.fillText(yCompare + "m", xStart, yStart + (yCompare/yScale) + yOffset);
    }



    protected void buildObstacle(double width, double height, double xpos){
        gc.setFill(ColorBlindness.daltonizeCorrect(Color.RED));

        gc.fillRect(xpos, runwayBottom - height - 10, width, height);

        //Display Obstacle Height
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(3);
        var arrowHeadLength = 10;
        var xStart = xpos + width + 10;
        var yStart = runwayBottom - 10 - height;
        var yEnd = runwayBottom - 10;
        gc.strokeLine(xStart, yStart + arrowHeadLength, xStart, yEnd - arrowHeadLength);

        //top arrow head
        gc.fillPolygon(new double[]{xStart - 5, xStart, xStart + 5},
            new double[]{yStart + arrowHeadLength, yStart, yStart+arrowHeadLength},
            3);

        //bottom arrow head
        gc.fillPolygon(new double[]{xStart - 5, xStart, xStart + 5},
            new double[]{yEnd- arrowHeadLength, yEnd , yEnd - arrowHeadLength},
            3);

        //Obstacle Height Text
        gc.fillText("Height = " + curObstacle.getHeight() + "m", xStart + 5, (yStart + yEnd)/2);
    }

    private void buildALS(double resaEnd){

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        gc.strokeLine(obstacleXPos/xScale, runwayBottom- (obstacleHeight/yScale) - 10, resaEnd, (runwayBottom-10) );
        gc.setLineWidth(3);
    }

    private void buildALSRatio(double ratio){
        Double d = ratio;
        gc.fillText("1:" + (d.intValue()), obstacleXPos/xScale, runwayBottom - (obstacleHeight/yScale) - 20);
    }

    private void buildDesignators() {
        double xPos = borderx/xScale + gc.getFont().getSize();
        double yPos = getHeight()/4;
        int heading = this.curRunway.getHeading();
        String position = this.curRunway.getPosition();
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, 16));
        gc.fillText(leftDesignator, xPos, yPos);
        xPos = (borderx + runwayLength)/xScale - gc.getFont().getSize();
        gc.fillText(rightDesignator, xPos, yPos);

        //Reset font settings
        gc.setFont(Font.font(Font.getDefault().getName(), 16));
    }

    /**
     * Create an arrow to show landing/takeoff direction
     * @param direction = "right" || "left"
     * @param label caption over arrow
     * @param arrowHeadLength length of arrowhead
     */
    private void drawLandingDirection(String direction, String label, double arrowHeadLength){
        double xStart = getWidth()/2;
        double yStart = getHeight()/2 - 75;
        double length = 100;
        gc.strokeLine(xStart,yStart,xStart + length, yStart);
        if (direction.equals("right")){
            gc.fillPolygon(new double[]{xStart+length,xStart+length+arrowHeadLength,xStart+length},
                new double[]{yStart - 4,yStart,yStart + 4},
                3);
        } else if (direction.equals("left")){
            gc.fillPolygon(new double[]{xStart,xStart-arrowHeadLength, xStart},
                new double[]{yStart - 4,yStart,yStart + 4},
                3);
        }
        gc.fillText(label, xStart, yStart - 4);
    }

    private void drawHorizontalArrow(double xStart, double xEnd, double yStart,double arrowHeadLength){
        gc.strokeLine(xStart + arrowHeadLength, yStart, xEnd - arrowHeadLength, yStart);

        //left arrow head
        gc.fillPolygon(new double[]{xStart + arrowHeadLength,xStart ,xStart + arrowHeadLength},
            new double[]{yStart + 5, yStart, yStart - 5},
            3);

        //right arrow head
        gc.fillPolygon(new double[]{xEnd - arrowHeadLength,xEnd ,xEnd - arrowHeadLength},
            new double[]{yStart + 5, yStart, yStart - 5},
            3);
    }

    private void drawArrowWithDottedLines(double xStart, double xEnd, double yStart, double yEnd, double arrowHeadLength, Color color){
        gc.setFill(color);
        gc.setStroke(color);

        drawHorizontalArrow(xStart,xEnd,yStart,arrowHeadLength);

        gc.setLineWidth(2);
        gc.setLineDashes(5);
        gc.strokeLine(xStart, yStart, xStart, yEnd);
        gc.strokeLine(xEnd, yStart, xEnd, yEnd);

        gc.setLineDashes(0);


    }

    private void drawVerticalArrow(double yStart, double yEnd, double xStart,double arrowHeadLength){
        gc.strokeLine(xStart, yStart + arrowHeadLength, xStart, yEnd - arrowHeadLength);

        //top arrow head
        gc.fillPolygon(new double[]{xStart - 5, xStart, xStart + 5},
            new double[]{yStart + arrowHeadLength, yStart, yStart+arrowHeadLength},
            3);

        //bottom arrow head
        gc.fillPolygon(new double[]{xStart - 5, xStart, xStart + 5},
            new double[]{yEnd- arrowHeadLength, yEnd , yEnd - arrowHeadLength},
            3);
    }

    public void switchDirection(int dir){direction = dir;}

    public void updateRunway(Runway runway) {
        this.curRunway = runway;
        updateView();
    }

    public void updateObstacle(Obstacle obstacle) {
        this.curObstacle = obstacle;
        updateView();
    }

    public void setDesignators(String left, String right){
        leftDesignator = left;
        rightDesignator = right;
    }

}
