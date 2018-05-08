/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author Henri Augusto
 */
public class Timeline {
    //====================
    //TreeSet must also be used inside Import and GeoGebraImport
    //as those methods return an SetInstance
    //====================
    Set<Event> events = new HashSet<>();
    //Set<Event> selectedEvents = new HashSet<>();
    ObservableSet<Event> selectedEvents = FXCollections.observableSet( new HashSet() );
    int minYear = -2000;
    int maxYear = 2018;
    int height = 3000;
    int defaultCanvasWidth = maxYear-minYear;
    int defaultCanvasHeight = height;
    GraphicView gview;
    List<Condition> conditions;
    //Set<>
    
    
    Timeline(Canvas cnv){
        //gview = new GraphicView(cnv,minYear+cnv.getWidth()/2,-height/2,maxYear-cnv.getWidth()/2+100,height/2);
        gview = new GraphicView(cnv,minYear,-height/2,maxYear,height/2);
        conditions = new ArrayList<>();
    }
    
    /**
     * deprecated because we will use commands now
     * @param name
     * @param start
     * @param end
     * @param h
     * @return
     * @deprecated
     */
    @Deprecated
    Event createEvent(String name, int start, int end, int h) {
        Event out = new Event(name,start,end,h);
        events.add( out );
        return out;
    }
    
    void draw(GraphicsContext gc){
        for (Event e : events) {
            e.draw(gc);
        }
        for(int i = minYear; i<=maxYear; i+=100){
            gc.setFill(Color.BLACK);
            gc.fillText(""+i, i, 0);
        }
        //debug
        /*for(int i = -height/2; i<=height/2; i+=100){
            gc.setStroke(Color.BLACK);
            gc.strokeText(""+i, 0, i);
            gc.setStroke(Color.BLUEVIOLET);
            gc.strokeLine(minYear, i, maxYear, i);
        }*/
    }
    
    void saveXML(){
        Export.saveXML(this);
    }
    
    @Deprecated
    void loadFromGeogebraXML(){
        GeoGebraImport.loadFromGeogebraXML(this);
    }

    void loadFromFile() {        
        Import.loadFromFile(this);
    }
    
    void clear(){
        events.clear();
        selectedEvents.clear();
    }

    void moveSelectedDown(double amt){
        for (Event se : TimelineFXApp.app.timeline.selectedEvents) {
            se.height += amt;
        }
    }

    void checkConditions() {
        for (Event event : events) {
            event.checkCondition();
        }
    }
    
    void updateEventsIsOnView() {
        System.out.println("timelinefx.Timeline.updateEventsIsOnView()");
        for (Event event : events) {
            event.isOnView();
        }
    }

    void clearSelectedEvents() {
        Set<Event> selectedEventsCopy = new HashSet<>(selectedEvents);
        for (Iterator<Event> iterator = selectedEventsCopy.iterator(); iterator.hasNext();) {
            Event next = iterator.next();
            next.unselect();
        }
    }

    public void addCondition(Condition newCondition) {
        conditions.add(newCondition);
    }
    
}
