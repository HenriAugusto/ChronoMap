/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This is the class responsible for holding the information of a Timeline. It holds {@link Event} and {@link Condition} objects 
 * along with a {@link GraphicView} object responsible for managing the mathematics of the drawing on the timeline's related
 * {@link Canvas} object.
 * <p>It also holds basic settings and is responsible for managing a set of the events that are currently selected.</p>
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
    int minYear = -3500;
    int maxYear = 2018;
    int height = 4000;
    String name = "";
    //int defaultCanvasWidth = maxYear-minYear;
    //int defaultCanvasHeight = height;
    GraphicView gview;
    /**  This is where the condition objects related to this timeline are stored. See also: {@link ConditionHandler} */
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
    
    /**
     * See {@link GeoGebraImport} for more information
     * @see GeoGebraImport
     * @deprecated
     */
    @Deprecated
    void loadFromGeogebraXML(){
        GeoGebraImport.loadFromGeogebraXML(this);
    }

    /**
     * Tells the {@link Import} that this timeline want to load a timeline.
     */
    void loadFromFile() {        
        Import.loadFromFile(this);
    }
    
    /**
     * clears the {@link Timeline#events} and {@link Timeline#selectedEvents} collections
     */
    void clear(){
        events.clear();
        selectedEvents.clear();
    }

     void checkConditions() {
        for (Event event : events) {
            event.checkCondition();
        }
    }
    
    void updateEventsIsOnView() {
        //System.out.println("timelinefx.Timeline.updateEventsIsOnView()");
        for (Event event : events) {
            event.updateIsOnView();
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
