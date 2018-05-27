/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

/**
 * Command for editing events
 * @author Henri Augusto
 */
public class CmdEditEvent implements Command{
    int newStart, oldStart;
    int newEnd, oldEnd;
    int newHeight, oldHeight;
    boolean newOngoing, oldOngoing;
    String newName, oldName;
    String newDescription, oldDescription;
    String newCondExpr, oldCondExpr;
    Color newColor, oldColor;
    Event event;
    List<WebLinkEditInfo> linksEditInfos;

    /**
     * Initializes the command with the new parameters
     * @param event
     * @param newName
     * @param newStart
     * @param newEnd
     * @param newHeight
     * @param newDescription
     * @param newColor 
     * @param newCondExpr 
     * @param linksEditInfos 
     */
    public CmdEditEvent(Event event, String newName, 
                        int newStart, int newEnd, int newHeight,
                        String newDescription,
                        Color newColor, ConditionExpr newCondExpr,
                        boolean newOngoing,
                        List<WebLinkEditInfo> linksEditInfos) {
        this.event = event;
        
        this.newName = newName;
            oldName=event.getName();
        this.newStart = newStart;
            oldStart = event.start;
        this.newEnd = newEnd;
            oldEnd = event.end;
        this.newHeight = newHeight;
            oldHeight = event.height;
        this.newDescription = newDescription;
            oldDescription = event.getDescription();
        this.newOngoing = newOngoing;
            oldOngoing = event.isOngoing();
        this.newCondExpr = newCondExpr.toString();
            oldCondExpr =  event.showCondition.toString() ;
            
        this.newColor = newColor;
            oldColor = event.getColor();
        this.linksEditInfos = linksEditInfos;
    }
    
    @Override
    public void execute() {
        event.setName(newName);
        event.setStart(newStart);
        event.setEnd(newEnd);
        event.setHeight(newHeight);
        event.setDescription(newDescription);
        event.setColor(newColor);
        event.setOngoing(newOngoing);
        setEventConditionExpr(newCondExpr);
        for (WebLinkEditInfo linkEditInfo : linksEditInfos) {
            linkEditInfo.execute();
        }
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public void undo() {
        event.setName(oldName);
        event.setStart(oldStart);
        event.setEnd(oldEnd);
        event.setHeight(oldHeight);
        event.setDescription(oldDescription);
        event.setColor(oldColor);
        event.setOngoing(oldOngoing);
        setEventConditionExpr(oldCondExpr);
        for (WebLinkEditInfo linkEditInfo : linksEditInfos) {
            linkEditInfo.undo();
        }
        
    }
    
    /**
     * This method calls ConditionParser to parse the conditionString that was stored.
     * <i>(Is storing the condition really easier than storing copies of the ConditionExpr?)</i>
     * @param conditionString 
     */
    private void setEventConditionExpr(String conditionString){
        try {
            event.setConditionExpr(  ConditionParser.parse(conditionString) );
        } catch (ConditionParser.ParseException | RuntimeException ex) {
            Logger.getLogger(CmdEditEvent.class.getName()).log(Level.SEVERE, null, ex);
            Dbg.println("This should never ever happen because that condition was valid to begin with. The validation is ensured in event creation time.",
                    Dbg.ANSI_RED + Dbg.ANSI_BLUE_BACKGROUND);
            Dbg.println("Still it might be invalid due to users editing the XML files directly.",
                    Dbg.ANSI_GREEN + Dbg.ANSI_BLUE_BACKGROUND);
        }
    }
    
    public static class WebLinkEditInfo{
        WebLink link;
        String oldName, newName;
        String oldUrl, newUrl;
        WebLink.WebLinkType oldType, newType;

        public WebLinkEditInfo(WebLink link, String newName, String newUrl, WebLink.WebLinkType newType) {
            this.link = link;
            this.newName = newName;
            this.newUrl = newUrl;
            this.newType = newType;
            oldName = link.getName();
            oldUrl = link.getUrl();
            oldType = link.getLinkType();
        }

        private void execute() {
            link.setName( newName );
            link.setUrl( newUrl );
            link.setType( newType );
        }

        private void undo() {
            link.setName( oldName );
            link.setUrl( oldUrl );
            link.setType( oldType );
        }
        
        
    }
    
}
