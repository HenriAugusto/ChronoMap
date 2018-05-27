/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import java.util.HashSet;
import java.util.Set;

/**
 * Command for creating a new event
 * @author Henri Augusto
 */
public class CmdNewEvent implements Command {
    Event newEvent;
    Timeline source;

    public CmdNewEvent(Timeline source, Event newEvent) {
        this.source = source;
        this.newEvent = newEvent;
    }

    @Override
    public void execute() {
        ChronoMapApp.app.timeline.events.add(newEvent);
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public void undo() {
        ChronoMapApp.app.timeline.events.remove(newEvent);
    }
    
}
