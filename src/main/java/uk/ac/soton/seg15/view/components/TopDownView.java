package uk.ac.soton.seg15.view.components;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.seg15.model.ColorBlindness;
import uk.ac.soton.seg15.model.Airport;
import uk.ac.soton.seg15.model.Obstacle;
import uk.ac.soton.seg15.model.Runway;
import uk.ac.soton.seg15.view.View;

public class TopDownView extends Group {

    private static final Logger logger = LogManager.getLogger(TopDownView.class);
    private final GraphicsContext textGC;
    private StringProperty scenarioType = new SimpleStringProperty();
    private GraphicsContext runwayGC;

    private Runway curRunway;

    private Airport curAirport;
    private Obstacle curObstacle;

    private double xScale;
    private double yScale;
    private double runwayLength;
    private double runwayWidth;
    private double stripEnd;
    private double borderx;
    private double bordery;
    private double runwayStripWidth;
    private double obstacleXPos;
    private double obstacleHeight;
    private double obstacleLength;
    private double runwayBottom;

    private int direction = 1; //1 or -1
    private String leftDesignator = "";
    private String rightDesignator = "";

    private final double startingWidth;
    private final double startingHeight;
    private final Affine originalTransform;
    private double degreeOffset = 0;
    private boolean toggleRotation = false;

    private final View view;

    public TopDownView(double width, double height,StringProperty type, View view){
        prefWidth(width);
        prefHeight(height);
        this.view = view;

        Canvas runwayDisplay = new Canvas();
        runwayGC = runwayDisplay.getGraphicsContext2D();
        runwayDisplay.setHeight(height);
        runwayDisplay.setWidth(width);

        Canvas textDisplay = new Canvas(runwayDisplay.getWidth(), runwayDisplay.getHeight());
        textGC = textDisplay.getGraphicsContext2D();
        textGC.setFont(new Font(Font.getDefault().getName(), 16));
        textGC.setTextAlign(TextAlignment.CENTER);

        this.getChildren().addAll(runwayDisplay, textDisplay);

        runwayGC.setFont(new Font(Font.getDefault().getName(), 16));
        originalTransform = runwayGC.getTransform();


        startingWidth = runwayDisplay.getWidth();
        startingHeight = runwayDisplay.getHeight();

        scenarioType.bindBidirectional(type);

        curRunway = view.getRunway();
        curAirport = view.getAirport();
        curObstacle = null;

        stripEnd = view.getStripEnd();
        bordery = 60;
        borderx = 60;

        runwayLength = curRunway.getParameters().getToda();
        runwayWidth = 60;
        runwayStripWidth = 300;

        xScale = (runwayLength + 2*stripEnd + 2*borderx) / width;
        yScale = (300 + bordery*2) / height;

        //EXAMPLE OBSTACLE
        obstacleXPos = runwayLength + borderx + stripEnd;
        obstacleHeight = 0;

        scenarioType.addListener((observable, old, newVal) -> {
            update();
        });

        buildRunway();
        buildScale();

    }

    public void update(){
        this.clear();
        curRunway = view.getRunway();
        runwayLength = curRunway.getParameters().getToda();
        stripEnd = view.getStripEnd();
        xScale = (runwayLength + 2*stripEnd + 2*borderx) / startingWidth;
        yScale = (300 + bordery*2) / startingHeight;

        curObstacle = view.getObstacle();
        obstacleXPos = (runwayLength + borderx + stripEnd) - curObstacle.getDistanceFromLeftThreshold();
        obstacleHeight = curObstacle.getHeight();
        obstacleLength = curObstacle.getWidth();



        buildObstacle(curObstacle.getWidth(),10,obstacleXPos,
            curObstacle.getDistanceToCentreLine() + bordery + 150);
        if(scenarioType.isNull().get()) return;
        switch(scenarioType.get()){
            case "Landing Over":
                landingOver();
                break;
            case "Landing Toward":
                landingTowards();
                break;
            case "TakeOff Toward":
                takeOffToward();
                break;
            case "TakeOff Away":
                takeOffAway();
                break;
        }

    }

    private void takeOffAway(){
        runwayGC.setFill(Color.BLACK);
        runwayGC.setStroke(Color.BLACK);
        runwayGC.setLineWidth(3);
        double arrowHeadLength = 10;

        drawLandingDirection(direction == 1 ? "left" : "right", "Take-Off Away", arrowHeadLength);


        double eba = direction * view.getEBA();
        double tora = direction * curRunway.getNewParameters().getTora();
        double toda = direction * curRunway.getNewParameters().getToda();
        double asda = direction * curRunway.getNewParameters().getAsda();

        double runwayY = (bordery + 150 + 0.5*runwayWidth)/yScale;
        double yOffset = 40;

        double xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos-runwayLength)/xScale;
        double xEnd = xStart;
        if(curObstacle.getName() != null){
            //Display Engine Blast Allowance
            xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos+obstacleLength)/xScale;
            xEnd = (obstacleXPos - eba)/xScale;
            drawHorizontalArrow(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY, arrowHeadLength);
            drawText("EBA = " + Math.abs(eba) + "m", direction == 1 ? xEnd : xStart, runwayY, Color.BLACK);
        }

        //Display TORA
        xStart = xEnd;
        xEnd = xStart - (tora/xScale);
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY, runwayY,
            arrowHeadLength, ColorBlindness.daltonizeCorrect(Color.BLUE));
        drawText("TORA = " + Math.abs(tora) + "m" , (xStart + xEnd)/2, runwayY, ColorBlindness.daltonizeCorrect(Color.BLUE));

        //Display TODA
        xEnd = xStart - (toda/xScale);
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY + yOffset, runwayY,
            arrowHeadLength,ColorBlindness.daltonizeCorrect(Color.ORANGERED));
        drawText("TODA = " + Math.abs(toda) + "m" , (xStart + xEnd)/2, runwayY + yOffset, ColorBlindness.daltonizeCorrect(Color.ORANGERED));
        yOffset += 40;
        //Display ASDA
        xEnd = xStart - (asda/xScale);
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY + yOffset, runwayY,
            arrowHeadLength,ColorBlindness.daltonizeCorrect(Color.PURPLE));
        drawText("ASDA = " + Math.abs(asda) + "m" , (xStart + xEnd)/2, runwayY + yOffset, ColorBlindness.daltonizeCorrect(Color.PURPLE));

    }

    private void takeOffToward(){
        //Set line settings
        runwayGC.setFill(Color.BLACK);
        runwayGC.setStroke(Color.BLACK);
        runwayGC.setLineWidth(3);
        var arrowHeadLength = 10;


        double resa = direction*curRunway.getParameters().getResa(); //OR Slope calc
        double tora = direction*curRunway.getNewParameters().getTora();
        double toda = direction*curRunway.getNewParameters().getToda();
        double asda = direction*curRunway.getNewParameters().getAsda();

        double runwayY = (bordery + 150 + 0.5*runwayWidth)/yScale;
        double yOffset = 40;

        //Display direction of take-off
        drawLandingDirection(direction == 1 ? "right" : "left", "TakeOff Direction", arrowHeadLength);

        double xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos-runwayLength)/xScale;
        double xEnd = xStart; //For Resa

        double ascent;
        if(curAirport != null){
            ascent = curAirport.getAscent();
        }else{
            ascent = 50;
        }

        if(curObstacle.getName() != null){
            xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos+obstacleLength)/xScale;
            //Display RESA || Slope Calc
            if(obstacleHeight*50 > curRunway.getParameters().getResa()) {
                xEnd = xStart - (direction*(obstacleHeight * 50) / xScale);
                drawText("h x 50 = " + obstacleHeight*50 + "m", (xStart + xEnd)/2, runwayY + yOffset, Color.BLACK);
            } else {
                xEnd = xStart - (resa / xScale);
                drawText("RESA = " + Math.abs(resa) + "m", xEnd, runwayY + yOffset, Color.BLACK);
            }
            drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY + yOffset, runwayY,
                arrowHeadLength,Color.BLACK);

            //Display Strip End
            runwayGC.setFill(ColorBlindness.daltonizeCorrect(Color.GOLD));
            runwayGC.setStroke(ColorBlindness.daltonizeCorrect(Color.GOLD));
            xStart = xEnd;
            xEnd = xStart - direction*(stripEnd/xScale);
            drawHorizontalArrow(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY, arrowHeadLength);
            drawText(stripEnd + "m", xEnd, runwayY, ColorBlindness.daltonizeCorrect(Color.GOLD));
        }

        //Display TORA
        xStart = xEnd;
        xEnd = xStart - (tora/xScale);
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY, runwayY,
            arrowHeadLength, ColorBlindness.daltonizeCorrect(Color.BLUE));
        drawText("TORA = " + Math.abs(tora) + "m" , (xStart + xEnd)/2, runwayY, ColorBlindness.daltonizeCorrect(Color.BLUE));

        //Display TODA
        xEnd = xStart - (toda/xScale);
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY + yOffset, runwayY,
            arrowHeadLength,ColorBlindness.daltonizeCorrect(Color.ORANGERED));
        drawText("TODA = " + Math.abs(toda) + "m" , (xStart + xEnd)/2, runwayY + yOffset, ColorBlindness.daltonizeCorrect(Color.ORANGERED));
        yOffset += 40;
        //Display ASDA
        xEnd = xStart - (asda/xScale);
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY + yOffset, runwayY,
            arrowHeadLength,ColorBlindness.daltonizeCorrect(Color.PURPLE));
        drawText("ASDA = " + Math.abs(asda) + "m" , (xStart + xEnd)/2, runwayY + yOffset, ColorBlindness.daltonizeCorrect(Color.PURPLE));

    }

    private void landingTowards(){
        //Set line settings
        runwayGC.setFill(Color.BLACK);
        runwayGC.setStroke(Color.BLACK);
        runwayGC.setLineWidth(3);
        var arrowHeadLength = 10;

        double resa = direction*curRunway.getParameters().getResa();
        double lda = direction*curRunway.getNewParameters().getLda();

        double runwayY = (bordery + 150 + 0.5*runwayWidth)/yScale;
        double yOffset = 40;

        //Display direction of Landing
        drawLandingDirection(direction == 1 ? "right" : "left", "Landing Direction", arrowHeadLength);

        double xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos-runwayLength)/xScale;
        double xEnd = xStart;
        if(curObstacle.getName() != null) {
            //Display RESA
            xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos+obstacleLength)/xScale;
            xEnd = (obstacleXPos - resa) / xScale;
            drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY + yOffset, runwayY,
                arrowHeadLength,Color.BLACK);
            drawText("RESA = " + Math.abs(resa) + "m", direction == 1 ? xEnd : xStart, runwayY + yOffset, Color.BLACK);

            //Display 60m
            runwayGC.setFill(ColorBlindness.daltonizeCorrect(Color.GOLD));
            runwayGC.setStroke(ColorBlindness.daltonizeCorrect(Color.GOLD));
            xStart = xEnd;
            xEnd = xEnd - direction*(stripEnd/xScale);
            drawHorizontalArrow(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY, arrowHeadLength);
            drawText(stripEnd + "m", xEnd, runwayY, ColorBlindness.daltonizeCorrect(Color.GOLD));

        }

        //Display LDA
        xStart = xEnd;
        xEnd = xEnd - (lda/xScale);
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY + yOffset,runwayY,
            arrowHeadLength,ColorBlindness.daltonizeCorrect(Color.DARKGREEN));
        drawText("LDA = " + Math.abs(lda) + "m", (xStart + xEnd)/2, runwayY + yOffset, ColorBlindness.daltonizeCorrect(Color.DARKGREEN));

    }

    private void landingOver(){
        //Set line settings
        runwayGC.setFill(Color.BLACK);
        runwayGC.setStroke(Color.BLACK);
        runwayGC.setLineWidth(3);
        var arrowHeadLength = 10;


        double resa = direction*curRunway.getParameters().getResa();
        double lda = direction*curRunway.getNewParameters().getLda();

        double runwayY = (bordery + 150 + 0.5*runwayWidth)/yScale;
        double yOffset = 40;

        //Display direction of Landing
        drawLandingDirection(direction == 1 ? "left" : "right", "Landing Direction", arrowHeadLength);

        double xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos-runwayLength)/xScale;
        double xEnd = xStart;

        double descent;
        if(curAirport != null){
            descent = curAirport.getDescent();
        }else{
            descent = 50;
        }

        //Display RESA or slope calc??
        if(curObstacle.getName() != null){
            xStart = direction == 1 ? obstacleXPos/xScale : (obstacleXPos+obstacleLength)/xScale;
            if(obstacleHeight*50 > curRunway.getParameters().getResa()) {
                xEnd = xStart - (direction*(obstacleHeight * 50) / xScale);
                drawText("h x 50 = " + obstacleHeight*50 + "m", (xStart + xEnd)/2, runwayY + yOffset, Color.BLACK);
            } else {
                xEnd = xStart - (resa / xScale);
                drawText("RESA = " + Math.abs(resa) + "m", (xStart+xEnd)/2, runwayY + yOffset, Color.BLACK);
            }
            drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY + yOffset, runwayY,
                arrowHeadLength,Color.BLACK);

            //Display 60m
            runwayGC.setFill(ColorBlindness.daltonizeCorrect(Color.GOLD));
            runwayGC.setStroke(ColorBlindness.daltonizeCorrect(Color.GOLD));
            xStart = xEnd;
            xEnd = xEnd - direction*(stripEnd/xScale);
            drawHorizontalArrow(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY, arrowHeadLength);
            drawText(stripEnd + "m", xEnd, runwayY, ColorBlindness.daltonizeCorrect(Color.GOLD));

        }

        //Display LDA
        xStart = xEnd;
        xEnd = xEnd - (lda/xScale);
        drawArrowWithDottedLines(direction == 1 ? xEnd : xStart, direction == 1 ? xStart : xEnd, runwayY + yOffset, runwayY,
            arrowHeadLength,ColorBlindness.daltonizeCorrect(Color.DARKGREEN));
        drawText("LDA = " + Math.abs(lda) + "m", (xStart + xEnd)/2, runwayY + yOffset, ColorBlindness.daltonizeCorrect(Color.DARKGREEN));

    }

    private void buildRunway(){
        runwayGC.setFill(ColorBlindness.daltonizeCorrect(Color.LIGHTGREEN));
        runwayGC.fillRect(0,0,startingWidth,startingHeight);

        //RUNWAY STRIP - if obstacle in this area, need to redeclare
        runwayGC.setFill(ColorBlindness.daltonizeCorrect(Color.MEDIUMPURPLE));
        double runwayStripx = borderx / xScale;
        double runwayStripy = bordery / yScale;
        runwayGC.fillRect(runwayStripx,runwayStripy, ((runwayLength + (stripEnd*2)) /xScale), (runwayStripWidth/yScale));

        //CLEARED AND GRADED AREA
        runwayGC.setFill(ColorBlindness.daltonizeCorrect(Color.CORNFLOWERBLUE));
        double clearedx = runwayStripx;
        double clearedy = runwayStripy + (75 / yScale);
        runwayGC.fillRect(clearedx, clearedy, ((runwayLength + (stripEnd*2)) /xScale), (150/yScale));

        //top trapezium
        var trapXCoords = new double[] {runwayStripx + ((60 + 150)/ xScale), runwayStripx + ((60 + 300)/xScale),runwayStripx +((runwayLength + 60 - 300)/xScale),runwayStripx +((runwayLength + 60 - 150)/xScale)};
        var trapYCoords = new double[] {clearedy, clearedy - (30/yScale),clearedy - (30/yScale),clearedy};
        runwayGC.fillPolygon(trapXCoords, trapYCoords,4);

        //bottom trapezium
        clearedy = clearedy + (150/yScale);
        trapXCoords = new double[] {runwayStripx + ((60 + 150)/ xScale), runwayStripx + ((60 + 300)/xScale),runwayStripx +((runwayLength + 60 - 300)/xScale),runwayStripx +((runwayLength + 60 - 150)/xScale)};
        trapYCoords = new double[] {clearedy, clearedy + (30/yScale),clearedy + (30/yScale),clearedy};
        runwayGC.fillPolygon(trapXCoords, trapYCoords,4);

        //RUNWAY
        runwayGC.setFill(ColorBlindness.daltonizeCorrect(Color.GRAY));
        double runwayx = runwayStripx + (stripEnd /xScale);
        double runwayy = runwayStripy + ((150 - (runwayWidth/2))/yScale);
        runwayBottom = runwayy + (60 /yScale);
        runwayGC.fillRect(runwayx,runwayy,(runwayLength/xScale),runwayWidth/yScale);

        //CENTRELINE
        runwayGC.setStroke(Color.WHITE);
        runwayGC.setLineWidth(3);
        runwayy = runwayy + ((runwayWidth/2)/yScale);
        double curX = runwayx;
        while(curX < (runwayx + (runwayLength/xScale))){
            runwayGC.strokeLine(curX,runwayy,(curX + 10),runwayy);
            curX = curX + 20;
        }

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
        double runwayY = (bordery + 150 - runwayWidth/2)/yScale;

        if (view.getRunway().getThreshold() != 0){
            drawArrowWithDottedLines(xStart, xEnd,runwayY - yOffset, runwayY,
                arrowheadLength, Color.BLACK);

            drawText("DT = " + view.getRunway().getThreshold(), (xStart+xEnd)/2,
                runwayY - yOffset - textGC.getFont().getSize(),Color.BLACK);

        }


        xStart = (borderx + stripEnd + view.getRunway().getParameters().getTora())/xScale;

        xEnd = xStart + view.getRunway().getParameters().getStopway();

        if (view.getRunway().getParameters().getStopway() != 0){
            drawArrowWithDottedLines(xStart, xEnd,runwayY - yOffset, runwayY,
                arrowheadLength, Color.BLACK);

            drawText("Stopway = " + view.getRunway().getParameters().getStopway(), (xStart+xEnd)/2,
                runwayY - yOffset - textGC.getFont().getSize(),Color.BLACK);

        }

        yOffset += 40;

        xStart = (borderx + stripEnd + view.getRunway().getParameters().getTora())/xScale;
        xEnd = xStart + view.getRunway().getParameters().getClearway();

        if (view.getRunway().getParameters().getClearway() != 0){
            drawArrowWithDottedLines(xStart, xEnd,runwayY - yOffset, runwayY,
                arrowheadLength, Color.BLACK);

            drawText("Clearway = " + view.getRunway().getParameters().getClearway(), (xStart+xEnd)/2,
                runwayY - yOffset - textGC.getFont().getSize(),Color.BLACK);

        }

    }

    private void buildObstacle(double length, double width, double xcoord, double ycoord){
        runwayGC.setFill(ColorBlindness.daltonizeCorrect(Color.RED));
        runwayGC.fillRect(xcoord/xScale,ycoord/yScale,(length/xScale),width/yScale);

    }

    private void buildScale(){
        runwayGC.setStroke(Color.WHITE);
        runwayGC.setFill(Color.WHITE);
        runwayGC.setLineWidth(2);

        double xStart = 20;
        double yStart = 20;

        //x-axis scale
        double xCompare = 200;
        drawHorizontalArrow(xStart, xStart + (xCompare/xScale), yStart, 10);
        double xOffset = textGC.getFont().getSize();
//        runwayGC.fillText(xCompare+ "m", xStart + (xCompare/xScale) + xOffset, yStart);
        drawText(xCompare+ "m", xStart + (xCompare/xScale) + xOffset, yStart, Color.WHITE);

        //y-axis scale
        double yCompare = 40;
        drawVerticalArrow(yStart, yStart + (yCompare/yScale), xStart, 10 );
//        runwayGC.fillText(yCompare + "m", xStart, yStart + (yCompare/yScale) + yOffset);
        drawText(yCompare + "m", xStart + xOffset, yStart + (yCompare/yScale), Color.WHITE);
    }

    private void buildDesignators() {
        double xOffset = (runwayLength/15) / xScale;
        double yOffset = (runwayWidth/2) / yScale;
        double xPos = (borderx + stripEnd)/xScale;
        double yPos = startingHeight/2;
        drawText(leftDesignator, xPos + xOffset, isUpsideDown() ? yPos + yOffset - 2*textGC.getFont().getSize() : yPos - yOffset, Color.WHITE);
        xPos = (borderx + stripEnd + runwayLength) / xScale;
        drawText(rightDesignator, xPos - xOffset, isUpsideDown()? yPos + yOffset - 2*textGC.getFont().getSize() : yPos - yOffset, Color.WHITE);

    }

    /**
     * Create an arrow to show landing/takeoff direction
     * @param direction = "right" || "left"
     * @param label caption over arrow
     * @param arrowHeadLength length of arrowhead
     */
    private void drawLandingDirection(String direction, String label, double arrowHeadLength){
        double length = 100;
        double xStart = startingWidth/2 - length/2;
        double yStart = 50;
        runwayGC.strokeLine(xStart,yStart,xStart + length, yStart);
        if (direction.equals("right")){
            runwayGC.fillPolygon(new double[]{xStart+length,xStart+length+arrowHeadLength,xStart+length},
                new double[]{yStart - 4,yStart,yStart + 4},
                3);
        } else if (direction.equals("left")){
            runwayGC.fillPolygon(new double[]{xStart,xStart-arrowHeadLength, xStart},
                new double[]{yStart - 4,yStart,yStart + 4},
                3);
        }
        yStart = isUpsideDown() ? yStart + 4 - textGC.getFont().getSize() : yStart-4-textGC.getFont().getSize();
        drawText(label, xStart + length/2, yStart, Color.BLACK);
    }

    private void drawHorizontalArrow(double xStart, double xEnd, double yStart,double arrowHeadLength){
        runwayGC.strokeLine(xStart + arrowHeadLength, yStart, xEnd - arrowHeadLength, yStart);

        //left arrow head
        runwayGC.fillPolygon(new double[]{xStart + arrowHeadLength,xStart ,xStart + arrowHeadLength},
                new double[]{yStart + 5, yStart, yStart - 5},
                3);

        //right arrow head
        runwayGC.fillPolygon(new double[]{xEnd - arrowHeadLength,xEnd ,xEnd - arrowHeadLength},
                new double[]{yStart + 5, yStart, yStart - 5},
                3);
    }

    private void drawVerticalArrow(double yStart, double yEnd, double xStart,double arrowHeadLength){
        runwayGC.strokeLine(xStart, yStart + arrowHeadLength, xStart, yEnd - arrowHeadLength);

        //top arrow head
        runwayGC.fillPolygon(new double[]{xStart - 5, xStart, xStart + 5},
            new double[]{yStart + arrowHeadLength, yStart, yStart+arrowHeadLength},
            3);

        //bottom arrow head
        runwayGC.fillPolygon(new double[]{xStart - 5, xStart, xStart + 5},
            new double[]{yEnd- arrowHeadLength, yEnd , yEnd - arrowHeadLength},
            3);
    }

    private void drawArrowWithDottedLines(double xStart, double xEnd, double yStart, double yEnd,double arrowHeadLength, Color color){
        runwayGC.setFill(color);
        runwayGC.setStroke(color);

        drawHorizontalArrow(xStart,xEnd,yStart,arrowHeadLength);

        runwayGC.setLineWidth(2);
        runwayGC.setLineDashes(5);
        runwayGC.strokeLine(xStart, yStart, xStart, yEnd);
        runwayGC.strokeLine(xEnd, yStart, xEnd, yEnd);

        runwayGC.setLineDashes(0);

    }

    private void drawText(String text, double x, double y, Color fillColour){
        textGC.setFill(fillColour);
        var tempx = isUpsideDown() ? startingWidth - x : x;
        var tempy =  isUpsideDown() ? startingHeight - y - textGC.getFont().getSize(): y+textGC.getFont().getSize();
        textGC.fillText(text, tempx, tempy);
    }

    private boolean isUpsideDown(){
        double degree = view.getRunway().getHeading()*10 - 90 + degreeOffset;
        var cond1 = degree > 90 && degree < 270; //normal runway
        var cond2 = degree > 90+360 && degree < 270+360; //reciprocal runway
        return (cond1||cond2) && toggleRotation;
    }

    private void clear(){
        runwayGC.clearRect(0,0,startingWidth,startingHeight);
        textGC.clearRect(0,0,startingWidth,startingHeight);
        this.buildRunway();
    }

    public void rotateToCompass(int compassHeading){
        runwayGC.clearRect(0,0,startingWidth,startingHeight);
        runwayGC.setTransform(originalTransform);
        textGC.clearRect(0,0, startingWidth, startingHeight);
        textGC.setTransform(originalTransform);
        if(!toggleRotation) return;
        logger.info("Rotate");
        //circle eqn
        compassHeading *= 10;
        double degree = compassHeading - 90 + degreeOffset;

        var radius = startingWidth-startingHeight;
        var y = startingWidth - Math.abs(radius * Math.cos(Math.toRadians(degree)));
        var width = startingWidth - Math.sqrt(Math.pow(radius, 2) - Math.pow(startingWidth - y,2));

        var originalCentreX = startingWidth/2;
        var originalCentreY = startingHeight/2;

        runwayGC.transform(new Affine(new Rotate(degree, originalCentreX, originalCentreY)));
        runwayGC.transform(new Affine(new Scale(width/startingWidth, 1, originalCentreX, originalCentreY)));
//        runwayGC.transform(new Affine(new Scale(width/startingWidth, y/startingHeight, originalCentreX, originalCentreY)));

        //Rotating text
        var scalex = isUpsideDown() ? -width/startingWidth : width/startingWidth;
        var scaley = isUpsideDown() ? -1 :1;
        textGC.transform(new Affine(new Rotate(degree, originalCentreX, originalCentreY)));
        textGC.transform(new Affine(new Scale(scalex, scaley, originalCentreX, originalCentreY)));


    }

    public void switchScenarioDirection(int dir){
        direction = dir;
    }

    public void setDesignators(String left, String right) {
        leftDesignator = left;
        rightDesignator = right;
    }

    public void setDegreeOffset(double offset){
        degreeOffset = offset;
    }

    public void toggleCompassRotation(){
        toggleRotation = !toggleRotation;
    }




}
