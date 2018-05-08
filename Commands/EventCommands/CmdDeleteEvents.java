/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Henri Augusto
 */
public class CmdDeleteEvents implements Command {
    Set<Event> events;// = new HashSet<>();
    Timeline source;

    public CmdDeleteEvents(Timeline source, Set<Event> events) {
        this.source = source;
        this.events = new HashSet<>(events);
    }

    

    @Override
    public void execute() {
        //Set<Event> l = new HashSet(TimelineFXApp.app.timeline.selectedEvents);
        for (Event event : events) {
            TimelineFXApp.app.timeline.events.remove(event);
        }
        TimelineFXApp.app.timeline.selectedEvents.clear();
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public void undo() {
        for (Event event : events) {
            TimelineFXApp.app.timeline.events.add(event);
            TimelineFXApp.app.timeline.selectedEvents.add(event);
        }
        
    }
    
}
