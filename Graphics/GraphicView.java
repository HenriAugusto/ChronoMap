/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollBar;
import javafx.scene.transform.Affine;

/**
 * Simple viewport class
 * @author Henri Augusto
 */
public class GraphicView {
    //private Point2D leftTop = new Point2D(0, 0);
    //private Point2D center = new Point2D(666, 666);
    private Point2D lastCenteredPoint = new Point2D(0, 0);
    private int translationX = 0;
    private int translationY = 0;
    private int viewX = 0;
    private int viewY = 0;
    private double zoom = 1;
    private double minZoom = 0.1;
    ScrollBar scrollBarH, scrollBarV;
    ChangeListenerHandle scrollBarHchangeListenerHandle, scrollBarVchangeListenerHandle;
    Canvas cnv;
    GraphicsContext gc;
    private Point2D minCenter,maxCenter;
    
    GraphicView(Canvas cnv, double minX, double minY, double maxX, double maxY){
        minCenter = new Point2D(minX, minY);
        maxCenter = new Point2D(maxX,maxY);
        this.cnv = cnv;
        gc = cnv.getGraphicsContext2D();

    }
        
    private void transformGraphicsContext(GraphicsContext gc) {
        //resets transform
        gc.setTransform(new Affine());
        gc.scale(zoom, zoom);
        //gc.translate(translationX*zoom, translationY*zoom);
    }
    
    public boolean isPointOnTransformedView(double x, double y){
        //convert the original to the transformed coordinatess
        //viewX = (int)( (-p.getX()) + 0.5f*cnv.getWidth()/zoom );
        //viewY = (int)( (-p.getY()) + 0.5f*cnv.getHeight()/zoom );
        Canvas cnv = ChronoMapApp.app.cnv;
        //Point2D testPoint = transformPoint(p);
        //testPoint = p;
        //THOSE COORDINATES DO NOT COUNT ZOOM???
        //Point2D leftTop = new Point2D(-viewX, -viewY);
        int leftTopX = -viewX;
        int leftTopY = -viewY;
        //System.out.println("leftTop = ("+leftTopX+", "+leftTopY+")");
        /*Point2D rightBottom = new Point2D(
                leftTopX+ cnv.getWidth() / zoom,
                leftTopY+ cnv.getHeight() / zoom
        );*/
        double rightBottomX = leftTopX+cnv.getWidth()/zoom;
        double rightBottomY = leftTopY+cnv.getHeight()/zoom;
        //System.out.println("rightBottom = ("+rightBottomX+", "+rightBottomY+")");
        if (x >= leftTopX
            && x <= rightBottomX
            && y >= leftTopY
            && y <= rightBottomY ) 
        {
                return true;
        }
        return false; 
    }
    
    /**
     * Checks if an UNTRANSFORMED point is on view.
     * @param p
     * @return true if the untransformed point is in the view
     */
    protected boolean isPointOnTransformedView(Point2D p){
        //At first it was the opposite. This method was called from isPointOnTransformedView(double x, double y) 
        //and it created a new Point2D instance everytime
        //by NOT creating this object i've manage to earn arround 4 fps
        return isPointOnTransformedView(p.getX(), p.getY());
    }
    
    /**
     * centers view on an <b>UNTRANSFORMED</b> point
     * @param gc GraphicsContext to be transformed
     * @param x x coordinate of the point
     * @param y y coordinate of the point
     */
    public void centerOnPoint(double x, double y){
        centerOnPoint(new Point2D(x,y));
    }
    
    /**
     * centers view on an <b>UNTRANSFORMED</b> point
     * @param gc GraphicsContext to be transformed
     * @param x x coordinate of the point
     * @param y y coordinate of the point
     */
    public void centerOnPoint(Point2D p){
        //System.out.println("centerOnPoint= ("+p.getX()+", "+p.getY()+")");
        lastCenteredPoint = p;
        transformGraphicsContext(gc);
        Canvas cnv = gc.getCanvas();
        //still not considering translationX
        //VIEWX AND VIEWY ARE THE VALUES OF THE TRANSLATIONS (resulting in viewing from -VIEWX to -VIEWX+CNV.WIDTH/(2*ZOOM)) FOR EXAMPLE
        viewX = (int)( (-p.getX()) + 0.5f*cnv.getWidth()/zoom );
            //viewX = -p.getX() + 0.5*cnv.getWidth()/zoom;
        viewY = (int)( (-p.getY()) + 0.5f*cnv.getHeight()/zoom );
            //viewY = -p.getY() + 0.5*cnv.getHeight()/zoom;
        //debug//System.out.println("view = ("+viewX+", "+viewY+")");
        
        //WE DON'T NEED TO MULTIPLY X AND Y COORDINATES BY ZOOM BECAUSE THE transformGraphicsContext already set the zoomed transform,
        //so they're already multiplied by zoom. Notice we divide half the canvas dimensions by zoom 
        //because that addend is relative to screen pixels and not the transform
        gc.translate(viewX,viewY);
        //System.out.println("viewX = "+viewX);
        //System.out.println("viewY = "+viewY);
        removeScrollBarListeners();
            //scrollBarH.setValue(MathUtils.map(p.getX(), 0, cnv.getWidth(), 0, 100));
            //scrollBarV.setValue(MathUtils.map(p.getY(), 0, cnv.getHeight(), 0, 100));
            scrollBarH.setValue(MathUtils.map(p.getX(), ChronoMapApp.app.timeline.minYear, ChronoMapApp.app.timeline.maxYear, 0, scrollBarH.getMax()));
            scrollBarV.setValue(MathUtils.map(p.getY(), -ChronoMapApp.app.timeline.height/2, ChronoMapApp.app.timeline.height/2, 0, scrollBarV.getMax()));
        addScrollBarListeners();
        //scrollBarH.valueProperty().
        /* SHOULD WORK
        gc.translate(
                -p.getX()*zoom + 0.5*cnv.getWidth()/zoom,
                -p.getY()*zoom + 0.5*cnv.getHeight()/zoom
        );*/
    }
    
    Point2D transformPoint(Point2D p){
        return new Point2D(
                p.getX()*zoom,
                p.getY()*zoom);
    }
    
    Point2D untransformPointOnView(Point2D p) {
        //System.out.println("======timelinefx.GraphicView.untransformPointOnView()=====");
        //System.out.println("p = ("+p.getX()+", "+p.getY()+")");
        /* this is transform
        gc.scale(zoom, zoom);
        gc.translate(translationX*zoom, translationY*zoom);
        gc.translate(
                -p.getX() + 0.5*cnv.getWidth()/zoom,
                -p.getY() + 0.5*cnv.getHeight()/zoom
        );
        */
        Point2D output = new Point2D(p.getX(),p.getY());
        output = output.multiply(1/zoom);
        output = output.add( -viewX, -viewY);
        //output = output.add(-translationX*zoom,-translationY*zoom);
        //System.out.println("zoom = "+zoom);

        //System.out.println("Unstransformed point = ("+output.getX()+", "+output.getY()+")");
        return output;
    }
    

    void addZoom(GraphicsContext gc, double d) {
        zoom += d;
        zoom = zoom <= 0 ? minZoom : zoom;
        //bad design
        centerOnPoint(lastCenteredPoint);    
        ChronoMapApp.app.timeline.updateEventsIsOnView();
    }
    
    void setZoom(GraphicsContext gc, double newZoom){
        zoom = newZoom;
        zoom = zoom <= 0 ? minZoom : zoom;
        centerOnPoint(lastCenteredPoint);
        ChronoMapApp.app.timeline.updateEventsIsOnView();
    }
    
    void initScrollBarHandles(){
        ChangeListener hCL = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scrollH(newValue.doubleValue());
            }
        };
        scrollBarHchangeListenerHandle = new ChangeListenerHandle(scrollBarH.valueProperty(), hCL);
        ChangeListener vCL = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scrollV(newValue.doubleValue());
            }
        };
        scrollBarVchangeListenerHandle = new ChangeListenerHandle(scrollBarV.valueProperty(), vCL);
    }
    
    void addScrollBarListeners() {
        scrollBarHchangeListenerHandle.attachListener();
        scrollBarVchangeListenerHandle.attachListener();
    }
    
    void removeScrollBarListeners(){
        scrollBarHchangeListenerHandle.removeListener();
        scrollBarVchangeListenerHandle.removeListener();
    }
    
    /**
     * use transformGraphicsContext(GraphicsCOntext gc) first
     * @param d 
     */
    private void scrollH(double d){
        //double minBounds = -2000;
        //double yy = minCenter.getY();
        //minCenter = new Point2D(0,yy);
        //minCenter = minCenter.add(minBounds+0.5*TimelineFXApp.app.cnv.getWidth()/zoom, 0);
        //System.out.println(" ===-"+minCenter.getX());
        //System.out.println("timelinefx.GraphicView.scrollH()    "+(minBounds+0.5*TimelineFXApp.app.cnv.getWidth()/zoom));
        double y = MathUtils.map(scrollBarV.getValue(), 0, scrollBarV.getMax(), minCenter.getY(), maxCenter.getY());
        double x = MathUtils.map(d, 0,scrollBarH.getMax(), minCenter.getX(), maxCenter.getX());
        centerOnPoint(x, y);
        ChronoMapApp.app.timeline.updateEventsIsOnView();
        ChronoMapApp.app.draw();
    }
    
    /**
     * use transformGraphicsContext(GraphicsCOntext gc) first
     * @param d 
     */
    private void scrollV(double d){
        double x = MathUtils.map(scrollBarH.getValue(), 0, scrollBarH.getMax(), minCenter.getX(), maxCenter.getX());
        double y = MathUtils.map(d, 0, scrollBarV.getMax(), minCenter.getY(), maxCenter.getY());
        centerOnPoint(x, y);
        ChronoMapApp.app.timeline.updateEventsIsOnView();
        ChronoMapApp.app.draw();
    }
    
    protected void setScrollBars(ScrollBar scrollBarH, ScrollBar scrollBarV){
        this.scrollBarH = scrollBarH;
        this.scrollBarV = scrollBarV;
        this.scrollBarH.setMax(1000/4);
        this.scrollBarV.setMax(1000/4);
        //setScrollBarsMaximumValues(gc);
        initScrollBarHandles();
        addScrollBarListeners();
    }
    
    public void setMinCenter(float minX, float minY){
        minCenter = new Point2D(minX, minY);
    }
    
    public void setMaxCenter(float maxX, float maxY){
        maxCenter = new Point2D(maxX, maxY);
    }

    public double getZoom() {
        return zoom;
    }

    /**
     * 
     */
    //public boolean isSegmentOnView(){
       
    //}
    
}
