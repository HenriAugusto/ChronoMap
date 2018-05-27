/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;

/**
 * Command for moving events. <br>
 * Note that {@link CommandHandler} usually merge consecutive CmdMoveEvents.
 * @author Henri Augusto
 */
public class CmdMoveEvents implements Command {
    Set<Event> events;
    int yMove;

    public CmdMoveEvents(Set<Event> events, int yMove) {
        this.events = new HashSet(events);
        this.yMove = yMove;
    }


    @Override
    public void execute() {
        for (Iterator<Event> it = events.iterator(); it.hasNext();) {
            Event next = it.next();
            next.setHeight(next.getHeight() + yMove);
            next.updateIsOnView();
        }
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public void undo() {
        for (Iterator<Event> it = events.iterator(); it.hasNext();) {
            Event next = it.next();
            next.setHeight(next.getHeight() - yMove);
            next.updateIsOnView();
        }
    }
    
}
