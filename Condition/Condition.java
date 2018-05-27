/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
//import static timelinefx.ConditionHandler.conditionsMap; //this works very nicely

/**
 * Each Condition object holds a Condition name and a list of sub conditions
 * The condition's boolean value is responsibility of ConditionHandler.
 * 
 * It is set to "true" at initialization via the {@link Condition#register() and it must be manually
 * unregistered via {@link Condition#unregister()} prior to deletion.
 * 
 * @author Henri Augusto
 */
public class Condition{
    SimpleStringProperty name;
    List<Condition> subs;
    
    Condition(String name){
        this.name = new SimpleStringProperty(name);        
        subs = new ArrayList<>();
        register();
    }
    
    /**
     * Recursively creates a string that represents this condition and subconditions as a tree
     * @return 
     */
    @Override
    public String toString(){
        String output = getName();
        for (Condition sub : subs) {
            output += "\n"+sub.toString(1); //implicit sub.toString()
        }
        return output;
    }
    
    private String toString(int depth){
        String output = "⌙";
        for (int i = 0; i < depth; i++) {
            output += "----";
        }
        output += "▸"+getName();
        for (Condition sub : subs) {
            output += "\n"+sub.toString(depth+1); //implicit sub.toString()
        }
        return output;
    }
    
    //======BEANS======
    SimpleStringProperty name() {
        return name;
    }

    void setName(String s) {
        name.setValue(s);
    }

    String getName() {
        return name.getValue();
    }
    
    /**
     * Adds this condition's name to the ConditionHandler.conditionsMap.
     * And does the same, recursively, for it's sub conditions
     */
    public final void register(){
        ConditionHandler.addConditionToMap(name.getValue(), true);
        for (Condition sub : subs) {
            sub.register();
        }
    }
    
    /**
     * Remove recursively this condition's name from the ConditionHandler.conditionsMap
     * along with all of it's children.
     */
    public final void unregister() {
        ConditionHandler.cleanConditionTree(this); //shouldn't this cleanConditionTree method be this condition's responsibility?
    }
    
    /**
     * recursive method that removes <b>any</b> occurrence of a given condition from this condition's subtree.
     * Example: you have A wich has a sub condition B which has a sub condition C. Calling a.removeFromSubConditions(c) will remove the C from the B subConditions's list.
     * @param condition to be removed
     * @return true if the condition was removed at least once
     */
    boolean removeFromSubConditions(Condition condition){
        if(subs.isEmpty())
            return false;
        boolean removedFromAnySub = false;
        for (Iterator<Condition> iterator = subs.iterator(); iterator.hasNext();) {
            Condition next = iterator.next();
            if(next==condition){
                iterator.remove();
                GUIMessages.displayMessage("removing sub-condition: " + condition.getName());
                Boolean b = ConditionHandler.conditionsMap.remove(condition.getName());
                if(b!=null){
                    GUIMessages.displayMessage("removing <Key,Value> form condition map: " + condition.getName());
                }
                return true;
            } else {
                boolean b = next.removeFromSubConditions(condition);
                removedFromAnySub = b ? true : removedFromAnySub;
            }
        }
        return removedFromAnySub;
    }
    
    

    /**
     * Recursive method that returns a copy object of that condition. (Needs more testing?)
     * @return copy
     */
    Condition getCopy() {
        Condition copy = new Condition(
                getName()
        );
        for (Condition sub : subs) {
            copy.subs.add( sub.getCopy() );
        }
        return copy;
    }

    
    
}
