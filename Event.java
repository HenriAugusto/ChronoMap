/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import org.dom4j.Element;

/**
 *
 * @author Henri Augusto
 */
public class Event {
    int start,end;
    int height;
    int lineWidth = 4;
    String name;
    String description = "";
    Color color = Color.BLACK;
    ObservableList<WebLink> links = FXCollections.observableArrayList();
    int currentLink = 0;
    private boolean selected = false;
    //boolean visible = true;
    private boolean showConditionResult = true;
    private boolean isOnViewResult = true;
    ConditionExpr showCondition; 
    double z;
    boolean ongoing = false;
    
    Event(String _name, int _start, int _end, int _height){
        name = _name;
        start = _start;
        end = _end;
        height = _height;
        //links.add("https://fb.com");
    }
    
    Event(String _name, String _description, int _start, int _end, int _height){
        this(_name, _start, _end, _height);
        description = _description;
    }
    
    Event(Element xml){
        loadFromXML(xml);
    }
    
    String getName() {
        return name;
    }
    
    void setName(String name) {
        this.name = name;
    }
    
    int getStart() {
        return start;
    }

    void setStart(int start) {
        this.start = start;
    }
    
    int getEnd() {
        return end;
    }

    void setEnd(int end) {
        this.end = end;
    }
    
    int getHeight() {
        return height;
    }

    void setHeight(int height) {
        this.height = height;
    }
    
    public Color getColor(){
        return color;
    }
    
    /**
     * Sets the condition expression evaluated to determine if this event will be shown
     * @param conditionExpression 
     */
    void setConditionExpr(ConditionExpr conditionExpression){
        showCondition = conditionExpression;
    }
    
    void draw(GraphicsContext gc){
        //checkCondition(); //was here, beign calculated everytime
        if(  !checkVisibility()  ){  return;  }
        //after checkCondition!
        
        if(selected){
            Color selectedColor = Color.BLUE;
            selectedColor = selectedColor.deriveColor(1, 1, 1, 0.3);
            gc.setStroke(selectedColor);
            if (end > start) {
                gc.setLineWidth(lineWidth+5);
                gc.strokeLine(start, height, end, height);
            } else {
                gc.setFill(selectedColor);
                gc.fillArc(start-7, height-7, 14, 14, 0, 360, ArcType.CHORD);
            }
            
            gc.setLineWidth(3);
            Text t = new Text(name);
            double textWidth = t.getLayoutBounds().getWidth();
            gc.strokeText(name,start/2+end/2-textWidth/2,height-10);
        }
        
        //if line
        if (end > start){
            gc.setStroke(color);
            gc.setLineWidth(lineWidth);
            gc.strokeLine(start, height, end, height);
            if(ongoing){
                gc.setFill(color);
                double arrowWidth = 9;
                double arrowHeight = 7;
                Point2D upper = new Point2D(end, height-arrowHeight/2);
                Point2D lower = new Point2D(end, height+arrowHeight/2);
                Point2D tip = new Point2D(end+arrowWidth, height);
                gc.fillPolygon(
                        new double[] {upper.getX(),tip.getX(), lower.getX()} ,
                        new double[] {upper.getY(),tip.getY(), lower.getY()} ,
                        3
                );
            }
        } else {
        //if point
            gc.setStroke(color);
            gc.setFill(color);
            gc.fillArc(start-5, height-5, 10, 10, 0, 360, ArcType.CHORD);
        }
        
        
        if(ChronoMapApp.app.timeline.gview.getZoom() >= 0.5){
            gc.setFill(color);
            Text t = new Text(name);
            double textWidth = t.getLayoutBounds().getWidth();
            //STROKE TEXT WAS A MAJOR BOTTLENECK!
            //FILL TEXT GOT ME TO 60FPS!!!
            gc.fillText(name,start/2+end/2-textWidth/2,height-10); 
            
            //          DESCRIPTION STUFF
            if( !description.isEmpty() ){
                if(selected){
                    t = new Text(description);
                    textWidth = t.getLayoutBounds().getWidth();
                    gc.setFill(color.brighter().desaturate());
                    //From GraphicsContextAPI
                    //  Note: Canvas does not support line wrapping, therefore the text alignment Justify is identical to left aligned text.
                    gc.fillText(description, start/2+end/2-textWidth/2, height+20);
                }
            }
        }
    }
    
    boolean checkPointNearEvent(double x, double y){
        if(end > start){
            return checkPointNearSegmentEvent(x, y);
        }
        return checkPointNearPointEvent(x, y);
    }
    
    boolean checkPointNearSegmentEvent(double x, double y){
        Vector2D p = new Vector2D(x, y);
        Vector2D startPoint = new Vector2D(start, height);
        Vector2D endPoint = new Vector2D(end, height);
        float distance = 10;
        if (Geometry.distanceBetweenPointAndSegment(p, startPoint, endPoint) <= 10) {
            return true;
        }
        return false;
    }
    
    boolean checkPointNearPointEvent(double x, double y){
        Vector2D p = new Vector2D(x, y);
        Vector2D eventPoint = new Vector2D(start, height);
        float distance = 10;
        return Geometry.distanceBetweenPoints(p, eventPoint) <= distance;
    }

    /**
     * Given an XML {@link Element} this method saves the data related to that event on this event object.
     * @param root 
     */
    void saveXML(Element root) {
        System.out.println("======================SAVING================\n"+this);
        Element event = root.addElement("event");
        event.addAttribute("name",name);
        
        
        Element date = event.addElement("date");
            date.addAttribute("start",""+start);
            date.addAttribute("end",""+end);
            date.addAttribute("ongoing",""+ongoing);
            
        Element heightElem = event.addElement("height");
            heightElem.setText(""+height);
            
        Element desc = event.addElement("description");
            desc.setText(description);
            
        Element colorElement = event.addElement("color");
            colorElement.addAttribute("red", ""+(float)color.getRed());
            colorElement.addAttribute("green", ""+(float)color.getGreen());
            colorElement.addAttribute("blue", ""+(float)color.getBlue());
            
        Element linksElement = event.addElement("links");
            for (WebLink link : links) {
                Element l = linksElement.addElement("link");
                l.setText(link.getUrl());
                l.addAttribute("type", link.getLinkType().toString());
                l.addAttribute("name", link.getName());
            }
            
        Element showConditionElement = event.addElement("showCondition");
        showCondition.addXmlElementInto(showConditionElement);
    }
    
    /**
     * Given an XML {@link Element} this method loads the data related to that event on this event object.
     * @param xml 
     */
    private void loadFromXML(Element xml){
        name = xml.attribute("name").getText();
        try{
            description = xml.element("description").getText();
            description = description.replace("\\n", "\n");
        } catch (NullPointerException e){
            //no description
        }
            //System.out.println("==========LOADING AN EVENT NAMED "+name);
        start = Integer.parseInt( xml.element("date").attribute("start").getText() );
        end = Integer.parseInt( xml.element("date").attribute("end").getText() );
        ongoing = Boolean.parseBoolean(xml.element("date").attribute("ongoing").getText() );
        height = Integer.parseInt( xml.element("height").getText() );
        float r = Float.parseFloat(xml.element("color").attribute("red").getText() );
        float g = Float.parseFloat(xml.element("color").attribute("green").getText() ); //i should put a try catch on those parsers
        float b = Float.parseFloat(xml.element("color").attribute("blue").getText() );
        color = new Color(r,g,b,1);
        Element show = xml.element("showCondition");
        showCondition = ConditionExpr.loadFromConditionElement(show);
        Element linksElem = xml.element("links");
        for (Element element : linksElem.elements()) {
            //System.out.println("adding link: "+element.getText
            String url = element.getText();
            String linkName = element.attributeValue("name");
            String type = element.attributeValue("type");
            links.add( 
                    new WebLink(   WebLink.WebLinkType.getTypeFromString(type), url, linkName   )
            );
        }
    }
    
    /**
     * Opens the next web link the browser.
     */
    void goToNextLink(){
        if(links.isEmpty() ){
            String linkName = "googling for "+getName();
            String linkUrl = "http://google.com/search?q="+getName();
            WebLink.WebLinkType linkType = WebLink.WebLinkType.OTHER_OPEN_BROWSER;
            ChronoMapApp.app.browser.goToLink( new WebLink(linkType, linkUrl, linkName));
            return;
        }
        ChronoMapApp.app.browser.goToLink(   links.get(currentLink)   );
        incrementLink();
    }

    /**
     * Utility method to increment the currentLink variable and warping around when necessary
     */
    private void incrementLink() {
        ++currentLink;
        if(currentLink>=links.size()){
            currentLink = 0;
        }
    }

    
    
    @Override
    public String toString(){
        //return "======================\n   eventName: " + name +"\n   condition: " + showCondition;
        return "eventName: " + name +"\n   condition: " + showCondition;
    }
    
    /**
     * Checks if this event is visible which is a result of BOTH
     * <ul>
     *  <li>it's ConditionExpr evaluating to true </li>
     *  <li>it lies on the visible portion of the Canvas drawing area</li>
     * </ul>
     * @return showConditionResult && isOnViewResult
     */
    boolean checkVisibility(){
        return showConditionResult && isOnViewResult;
    }
    
    /**
     * Sexy recursive method to descend the condition tree in the XML
     * <p>It then sets the boolean variable {@link Event#showConditionResult} to the value returned
     * by the evaluation of it's {@link ConditionExpr}</p>
     */
    void checkCondition() {
        //System.out.println("=======Checking condition on event\n"+this);
        showConditionResult = showCondition.eval();
        if( !showConditionResult ){
            unselect();
        }
    }
    
    /**
     * Updates the {@link Event#isOnViewResult} on this Event
     */
    void updateIsOnView(){
        if(ChronoMapApp.app.timeline.gview.isPointOnTransformedView(start, height) && ChronoMapApp.app.timeline.gview.isPointOnTransformedView(end, height) ){
            //System.out.println("===="+name+"====\ntimelinefx.Event.isOnView() = TRUE");
            isOnViewResult = true; 
        } else {
            //System.out.println("timelinefx.Event.isOnView() = FALSE");
            isOnViewResult = false;
        }
    }
    
    /**
     * Returns the Event's {@link Event#isOnViewResult}
     */
    boolean isOnView(){
        return isOnViewResult;
    }
    
    void setSelected(boolean b) {
        if(b && !selected){
            select();
        } else if(!b && selected) {
            unselect();
        }
    }

    /**
     * This method makes the Event selected and adds it to {@link ChronoMapApp#app#timeline#selectedEvents}.
     */
    void select() {
        if(selected || !showConditionResult ){return;}
        selected = true;
        ChronoMapApp.app.timeline.selectedEvents.add(this);
    }

    /**
     * This method makes the Event unselected and removes it from
     * {@link ChronoMapApp#app#timeline#selectedEvents}.
     */
    void unselect() {
        if(!selected){return;}
        selected = false;
        ChronoMapApp.app.timeline.selectedEvents.remove(this);
    }
    
    boolean isSelected(){
       return selected; 
    }
    
    /**
     * Experimental method used by the experimental {@link ThreeDimensionalVisualizationStageManager}
     * @return Shape3D for that event
     */
    Shape3D get3DShape(){
        Shape3D out;
        if(end-start==0){
            out = new Sphere(5);
        } else {
            out = new Cylinder(10, end-start);
            out.setRotate(90);
            out = new Box(end-start, 10, 10);
        }
        
        
        out.setLayoutX(start); //also works
        out.setLayoutY(height); //also works
        //out.setTranslateX(start);
        //out.setTranslateY(height);
        int deltaZ = 200;
        int rWidth = 200;
        z = 0+Math.random()*rWidth-rWidth/2;
        for (Condition condition : ChronoMapApp.app.timeline.conditions) {
            if(showCondition.containsCondition(condition)){
                break;
            }
            z += deltaZ;
        }
        out.setTranslateZ( z );
        Color c = color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0 ? Color.hsb(Math.random()*360, 0.5d, 0.3d) : color;
        out.setMaterial(   new PhongMaterial( c )   );
        out.setDrawMode(DrawMode.FILL);
        return out;
    }

    /**
     * Sets the color used to display this event.
     * @param color 
     */
    void setColor(Color color) {
        this.color = color;
    }

    /**
     * Returns if this event is ongoing (like an war still happening or an alive person)
     * @return 
     */
    boolean isOngoing() {
        return ongoing;
    }

    /**
     * Sets if this event is ongoing (like an war still happening or an alive person)
     * @param isEventOngoing 
     */
    void setOngoing(boolean isEventOngoing) {
        this.ongoing = isEventOngoing;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    
}
