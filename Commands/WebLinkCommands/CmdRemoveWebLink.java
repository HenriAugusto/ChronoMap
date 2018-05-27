/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

/**
 * Command for removing a {@link WebLink} into an {@link Event}
 * @author Henri Augusto
 */
public class CmdRemoveWebLink implements Command {
    Event event;
    WebLink link;

    public CmdRemoveWebLink(Event event, WebLink link) {
        this.event = event;
        this.link = link;
    }
    
    

    @Override
    public void execute() {
        event.links.remove(link);
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public void undo() {
        event.links.add(link);
    }
    
}
