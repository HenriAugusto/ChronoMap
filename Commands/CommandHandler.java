/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Henri Augusto
 */
public class CommandHandler {
    private static List<Command> undoableCmdHistory = new ArrayList<>();
    private static List<Command> redoCmdHistory = new ArrayList<>();
    
    static void executeCommand(Command c){
        if(c.isUndoable())
            undoableCmdHistory.add(c);
        c.execute();
        mergeCommands();
    }

    static void undoLastCommand() {
        if( undoableCmdHistory.isEmpty() )
            return;
        Command c = undoableCmdHistory.get( undoableCmdHistory.size()-1 );
        undoableCmdHistory.remove(c);
        c.undo();
        redoCmdHistory.add(c);
        GUIMessages.displayMessage("Undoing last command");
    }

    static void redoLastCommand() {
        if( redoCmdHistory.isEmpty() )
            return;
        Command c = redoCmdHistory.get( redoCmdHistory.size()-1 );
        redoCmdHistory.remove(c);
        c.execute();
        undoableCmdHistory.add(c);
        GUIMessages.displayMessage("Redoing last command");
    }
    
    private static void mergeCommands(){
        if(undoableCmdHistory.size()<=1){
            //GUIMessages.displayMessage("0 or 1 events");
            return;
        }
        for (int i = undoableCmdHistory.size()-2; i >= 0; --i) {
            Command last = undoableCmdHistory.get(i+1);
            Command penultimate = undoableCmdHistory.get(i);
            if( last.getClass()==CmdMoveEvents.class && penultimate.getClass()==CmdMoveEvents.class  ){
                CmdMoveEvents lastCast = (CmdMoveEvents) last;
                CmdMoveEvents penultimateCast = (CmdMoveEvents) penultimate;
                if(lastCast.events.equals(penultimateCast.events)){
                    //GUIMessages.displayMessage("merging!");
                    undoableCmdHistory.remove(last);
                    penultimateCast.yMove += lastCast.yMove;
                } else {
                    //GUIMessages.displayMessage("different events!");
                }
            }
            
        }
    }
}
