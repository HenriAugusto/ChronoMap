/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class contains helper methods to manage the conditions in a {@link  Timeline}.
 * It stores the Boolean value of each condition in a map for easy access 
 * (So we don't have to recursively iterate on the conditions for each {@link Event} when determining if it's going to be displayed or not)
 * @see Timeline#conditions
 * @author Henri Augusto
 */
public class ConditionHandler {
    //static ConditionHandler handler = new ConditionHandler();
    static Map<String,Boolean> conditionsMap = new TreeMap();
    
    public static void addConditionToMap(String s, Boolean b){
        conditionsMap.put(s, b);
    }
    
    private static void setAll(boolean b) {
        for (Iterator<String> it = conditionsMap.keySet().iterator(); it.hasNext();) {
            String next = it.next();
            conditionsMap.put(next, b);
        }
    }

    static void selectAll() {
        setAll(true);
    }

    static void unselectAll() {
        setAll(false);
    }
    
    /**
     * Calls <b>conditionsMap.remove(getName())</b> on each condition on the sub
     * tree. Also clear the subs list. (Clearing the sub list is necessary? Or
     * when you remove one form the tree all of this children are garbage
     * collected in a top-down fashion?)
     *
     * @return a Map containing pairs of the <i>getName()</i> result of every
     * removed condition and it's current boolean state
     */
    static Map<String, Boolean> cleanConditionTree(Condition c) {
        //Map<String, Boolean> conditionsMap = ConditionHandler.conditionsMap;
        Map<String, Boolean> result = new HashMap<>();
        boolean removed = false;
        //if this condition still has a <key,value> pain in the conditions Map
        if (conditionsMap.keySet().contains( c.getName()) ) {
            result.put(c.getName(), conditionsMap.get(c.getName()));
            conditionsMap.remove(c.getName());
            removed = true;
            //GUIMessages.displayMessage("removing <Key,Value> for: " + c.getName());
        }
        //it there are no subs, return
        if (c.subs.isEmpty()) {
            return result;
        }
        //it there ARE subs, 
        for (Condition sub : c.subs) {
            Map<String, Boolean> subResult = cleanConditionTree(sub);
            result.putAll(subResult);
        }
        c.subs.clear();
        return result;
    }
    
}
