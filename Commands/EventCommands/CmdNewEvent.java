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
public class CmdNewEvent implements Command {
    Event newEvent;
    Timeline source;

    public CmdNewEvent(Timeline source, Event newEvent) {
        this.source = source;
        this.newEvent = newEvent;
    }

    @Override
    public void execute() {
        TimelineFXApp.app.timeline.events.add(newEvent);
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public void undo() {
        TimelineFXApp.app.timeline.events.remove(newEvent);
    }
    
}
