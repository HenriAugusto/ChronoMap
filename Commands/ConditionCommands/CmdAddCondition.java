/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Command to Add a Condition from the the conditions inside the main Timeline object (along with it's associated (key,value) pair into the conditionsMap.
 * @author Henri Augusto
 */
public class CmdAddCondition implements Command{
    private Condition parentCondition;
    private final Condition newCondition;
    private ConditionsWindowStageManager.ConditionTitledPane parentConditionTitledPane;
    private ConditionsWindowStageManager.ConditionTitledPane conditionTitledPane;

    /**
     * Initialize the command to create an condition asking the user for the condition's name.
     * 
     * @param parentCondition use <i>null</i> if this is a top-level condition
     * @param parentConditionTitledPane the parent's conditionTitledPane where the condition will be added. Use <i>null</i> if this is a top-level condition
     * 
     * @throws RuntimeException if only parentCondition or only if parentConditionTitledPane are null
     */
    public CmdAddCondition(Condition parentCondition, ConditionsWindowStageManager.ConditionTitledPane parentConditionTitledPane){
        this.parentConditionTitledPane = parentConditionTitledPane;
        String name = GetStringStage.getStringFromUser("Enter new condition name:");
        GUIMessages.displayMessage(TimelineFXApp.app.root, "Adding condition " + name);
        newCondition = new Condition(name);
        if(parentCondition!=null){
            this.parentCondition = parentCondition;
        }
        if(parentCondition == null || parentConditionTitledPane == null){
            if(parentCondition != null || parentConditionTitledPane != null){
                throw new RuntimeException("If you specify a parentCondition or a parentConditionTitledPane you must specify both. It doesn't make sense to only one of them to be null");
            }
        }
    }
    
    /**
     * Initialize the command with a previously created condition
     *
     * @param newCondition the previously created condition
     */
    protected CmdAddCondition(Condition newCondition) {
        GUIMessages.displayMessage(TimelineFXApp.app.root, "Adding condition " + newCondition.getName());
        this.newCondition = newCondition;
        parentCondition = null;
    }
    
    @Override
    public void execute() {
        newCondition.register(); //must be here
        //If it has a parent, add the condition to the parent's sub conditions and updates it
        if (parentCondition != null){
            parentCondition.subs.add(newCondition);
            //badDesign//parentConditionTitledPane.addSubConditions();
            //badDesign//parentConditionTitledPane.setCollapsible(true);
            //badDesign//parentConditionTitledPane.setExpanded(true);
            //badDesign//Dbg.println("IS THIS SHIT COLLAPSIBLE? "+parentConditionTitledPane.collapsibleProperty().getValue(), Dbg.ANSI_YELLOW);
        //It it is a toplevel condition
        } else {
            TimelineFXApp.app.timeline.addCondition(newCondition);
            //Add a new ConditionTitledPane
            //badDesign//conditionTitledPane = new ConditionsWindowStageManager.ConditionTitledPane(newCondition, null);
            //badDesign//ConditionsWindowStageManager.mainAccordion.getPanes().add( conditionTitledPane );
        }
        ConditionsWindowStageManager.updateTitledPanes();
    }

    @Override
    public void undo() {
        newCondition.unregister(); //must be here
        //If it is a sub condition
        if (parentCondition != null){
            parentCondition.subs.remove(newCondition);
            //badDesign//parentConditionTitledPane.addSubConditions();
        //If it is a top-level condition
        } else {
            TimelineFXApp.app.timeline.conditions.remove(newCondition);
            //badDesign//ConditionsWindowStageManager.mainAccordion.getPanes().remove(conditionTitledPane);
        }
        ConditionsWindowStageManager.updateTitledPanes();
    }
    
    @Override
    public boolean isUndoable() {
        return true;
    }
      
}
