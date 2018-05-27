/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

/**
 * Interface that defines Commands. Commands are general functionality of the code like <br>
 * adding {@link Event} ojects, editing event data, adding a {link @Condition} and etc.
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
