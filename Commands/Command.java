/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

/**
 *
 * @author Henri Augusto
 */
public interface Command {
    void execute();
    boolean isUndoable();
    
    default void undo(){ 
        if(!isUndoable()){
            throw new RuntimeException("Calling undo() in a non-undoable command");
        }
    };
}
