/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Command to remove a Condition from the the conditions inside the main Timeline object (along with it's
 * associated (key,value) pair into the {@link ConditionHandler#conditionsMap}.
 * @see ConditionHandler
 * @see Timeline#conditions
 * @author Henri Augusto
 */
public class CmdRemoveCondition implements Command{
    //Fields for toplevel stuff
    boolean wasTopLevel;
    Condition conditionToBeRemoved;
    Condition copy;
    int indexOfTopLevel;
    //Fields for deleting subconditions
    Set<Condition> topLevelContainingConditionSet = new HashSet<>();
    Set<Condition> topLevelContainingConditionCopySet = new HashSet<>();
    Map<String,Boolean> mapDiff = new HashMap<>(); //map containing the (K,V) pairs of the removed conditions so they can be re-added later.
    ConditionsWindowStageManager.ConditionTitledPane conditionTitledPane;

    /**
     * Initialize the command with the condition to be removed. That also includes each one of it's sub conditions.
     * @param conditionToBeRemoved condition to be removed
     */
    public CmdRemoveCondition(Condition conditionToBeRemoved, ConditionsWindowStageManager.ConditionTitledPane conditionTitledPane){
        this.conditionToBeRemoved = conditionToBeRemoved;
        this.copy = conditionToBeRemoved.getCopy();
        this.conditionTitledPane = conditionTitledPane;
    }
    
    @Override
    public void execute() {
        removePanes();
        List<Condition> conditions = ChronoMapApp.app.timeline.conditions;
        //if it is a top-level condition
        for (Iterator<Condition> it = conditions.iterator(); it.hasNext();) {
            Condition next = it.next();
            if (next == conditionToBeRemoved) {
                mapDiff = ConditionHandler.cleanConditionTree(next); //removed conditions
                indexOfTopLevel=conditions.indexOf(conditionToBeRemoved);
                it.remove();
                wasTopLevel = true;
                GUIMessages.displayMessage("Removing top-level condition "+conditionToBeRemoved.getName()+" from the timeline condition list");
                next.unregister();
                return;
            }
        }
        //if it's not a top-level condition we must descend the tree
        //Let's call removeFromSubConditions in every top-level condition
        for (Iterator<Condition> it = conditions.iterator(); it.hasNext();) {
            Condition currentCondition = it.next();
            boolean b = currentCondition.removeFromSubConditions(conditionToBeRemoved);
            if(b){
                topLevelContainingConditionSet.add(currentCondition);
                wasTopLevel = false;
                conditionToBeRemoved.unregister();
            }
        }
        //ConditionsWindowStageManager.createWindow(); //can't do that because the accordions would be closed on reconstruction
        
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public void undo() {
        addPanes();
        //put the map entries again (reading from mapDiff)
        for (Iterator<Map.Entry<String, Boolean>> iterator = mapDiff.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String,Boolean> next = iterator.next();
            ConditionHandler.conditionsMap.put(next.getKey(), next.getValue());
        }
        if(wasTopLevel){
            try{
                ChronoMapApp.app.timeline.conditions.add(indexOfTopLevel,copy); //copy otherwise subconditions would not be restored!
                
            } catch (IndexOutOfBoundsException ex){
                GUIMessages.displayMessage("Could not add the copy again on the same index!");
                ChronoMapApp.app.timeline.conditions.add(copy);
            }
            copy.register();
        }
        for (Condition condition : topLevelContainingConditionSet) {
            ChronoMapApp.app.timeline.conditions.remove(condition);
            condition.unregister(); //idk what i'm doing
        }
        for (Condition condition : topLevelContainingConditionCopySet) {
            ChronoMapApp.app.timeline.conditions.add(condition);
            condition.register(); //idk what i'm doing
        }
        //ConditionsWindowStageManager.createWindow(); //can't do that because the accordions would be closed on reconstruction
    }
    
    /**
     * variable that holds the previous index of the conditionTitledPane on the ConditionsWindowStageManager
     * accordion so it can be re added on the same order
     */
    private int indexOfRemoval;
    
    /**
     * Called on <i>execute()</i> this method remove the ConditionTitledPanes from the ConditionsWindowStageManager
     */
    private void removePanes(){
        
        if (conditionTitledPane.parentTitledPane != null) {
            indexOfRemoval = conditionTitledPane.parentTitledPane.accordion.getPanes().indexOf(conditionTitledPane);
            conditionTitledPane.parentTitledPane.accordion.getPanes().remove(conditionTitledPane);
        } else {
            indexOfRemoval = ConditionsWindowStageManager.mainAccordion.getPanes().indexOf(conditionTitledPane);
            ConditionsWindowStageManager.mainAccordion.getPanes().remove(conditionTitledPane);
        }
    }
    
    /**
     * Called on <i>undo()</i> this method re-adds the ConditionTitledPanes
     * to the ConditionsWindowStageManer
     */
    private void addPanes() {
        if (conditionTitledPane.parentTitledPane != null) {
            conditionTitledPane.parentTitledPane.accordion.getPanes().add(indexOfRemoval, conditionTitledPane);
        } else {
            ConditionsWindowStageManager.mainAccordion.getPanes().add(indexOfRemoval, conditionTitledPane);
        }
    }
    
}
