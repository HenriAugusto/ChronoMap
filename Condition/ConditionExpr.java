/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*Net Beans bugs when refactoring a line like this

ConditionExp c1, c2;


result: 

ConditionExpr c1;
ConditionExp c2;
*/
package timelinefx;

import javafx.beans.property.SimpleStringProperty;
import org.dom4j.Element;

/**
 * 
 * @author Henri Augusto
 */
public class ConditionExpr {
    ConditionExpr c1;
    ConditionExpr c2;
    Type type; 
    SimpleStringProperty name;
    
    /**
     * Creates a 'AND' or 'OR' type ConditionExpr
     * @param c1 condition 1
     * @param c2 condition 2
     * @param type ConditionExp.Type = AND, OR
     */
    ConditionExpr(ConditionExpr c1, ConditionExpr c2, Type type) {
        this.c1 = c1; 
        this.c2 = c2;      //https://stackoverflow.com/questions/7187799/why-default-constructor-is-required-in-a-parent-class-if-it-has-an-argument-ed-c
        this.type = type;
        //System.out.println(""+this);
        String op;
        switch(type){
            default:
            case AND:
                op = "&&" ;
                break;
            case OR:
                op = "||" ;
                break;
            case SINGLE:
                throw new RuntimeException("WHAT THE FUCK IS THIS SINGLE TYPE DOING HERE?");
        }
        //System.out.println("*Conditions expression \n       "+c1.getName()+" "+op+" "+c2.getName());
        //System.out.println("*Conditions expression \n       "+c1+" "+op+" "+c2);
    }

  
    /**
     * Create a <i>SINGLE</i> type condition of the given name
     * @param name 
     */
    ConditionExpr(String name) {
        this.name = new SimpleStringProperty(name);
        this.type = Type.SINGLE;
    }

    //@Override
    public boolean eval() {
        switch(type){
            case AND:
                return c1.eval() && c2.eval();
            case OR:
                return c1.eval() || c2.eval();
            case SINGLE:
                Boolean b = ConditionHandler.conditionsMap.get(getName());
                if(b==null){
                    //System.out.println("there was no registered condition for me "+getName()+"");
                    return true;
                }
                //System.out.println("evaluating condition "+getName());
                //System.out.println("        ====>"+b);
                return b;
            default:
                System.err.println("ConditionExpr eval() = have wrong enumeration");
                return true;
        }
    }
    
    enum Type{
        SINGLE,
        AND,
        OR;
    }
    
    /**
     * Returns a string representing that condition.
     * This strings is parseable by ConditionParser to return an equivalent ConditionExpr object.
     * @return parseable string
     */
    @Override
    public String toString() {
        String operator;
        switch (type) {
            default:
            case AND:
                operator = "&&";
                break;
            case OR:
                operator = "||";
                break;
            case SINGLE:
                return getName();
        }
        return "( " + c1 + " " + operator + " " + c2 + " )";
    }
    
    //======BEANS======
    SimpleStringProperty name() {
        return name;
    }

    void setName(String s) {
        name.setValue(s);
    }

    String getName() {
        //return name.getValue() == null ? "VAI TOMAR NO CU" : name.getValue();
        return name.getValue();
    }
    

    
    void validate() throws NonExistentConditionException{
        //<dbg>Dbg.println("Validating!:"+toString(), Dbg.ANSI_YELLOW_BACKGROUND);
        //<dbg>String op = "Or: ";
        switch(type){
            case SINGLE:
                //<dbg>Dbg.println("Single: "+toString(), Dbg.ANSI_GREEN_BACKGROUND);
                boolean exists = ConditionHandler.conditionsMap.containsKey(getName());
                if (!exists) {
                    throw new NonExistentConditionException( getName() );
                }
                //<dbg>Dbg.println("should i get here??: "+toString(), Dbg.ANSI_YELLOW_BACKGROUND);
                break;
            case AND:
                //op = "And: ";
            case OR:
                //<dbg>Dbg.println(op+toString(), Dbg.ANSI_GREEN_BACKGROUND);
                String invalids = null;
                try {
                    c1.validate();    
                } catch (NonExistentConditionException nece) {
                    //invalids = invalids == null ? nece.getMessage() : invalids+", "+nece.getMessage();
                    invalids = nece.getMessage();
                    //<dbg>Dbg.println("=======================================================INVALIDS================:"+invalids, Dbg.ANSI_RED_BACKGROUND);
                }
                try {
                    c2.validate();
                } catch (NonExistentConditionException nece) {
                    invalids = invalids == null ? nece.getMessage() : invalids+", "+nece.getMessage();
                    //<dbg>Dbg.println("=======================================================INVALIDS================:"+invalids, Dbg.ANSI_RED_BACKGROUND);
                }
                if (invalids == null){
                    //<dbg>Dbg.println("=======================================================VALID========== =D ======:"+toString(), Dbg.ANSI_BLUE_BACKGROUND);
                    return;
                }
                throw new NonExistentConditionException(invalids);
        }
    }
    
    void addXmlElementInto(Element e){
        String ename = e.getName();
        System.out.println("NAME = "+ename);
        switch(type){
            case SINGLE:
                //System.out.println("TYPE = SINGLE");
                Element cExpr = e.addElement("SINGLE");
                //cExpr.addAttribute("type", "single");
                cExpr.setText(getName());
                break;
            case AND:
                //System.out.println("TYPE = AND");
                cExpr = e.addElement("AND");
                //cExpr.addAttribute("type", "and");
                c1.addXmlElementInto(cExpr);
                c2.addXmlElementInto(cExpr);
                break;
            case OR:
                //System.out.println("TYPE = OR");
                cExpr = e.addElement("OR");
                //cExpr.addAttribute("type", "OR");
                c1.addXmlElementInto(cExpr);
                c2.addXmlElementInto(cExpr);
                break;
        }
    }
    
    /**
     * Sexy recursive method to read the conditions tree form the xml
     * @param the <showCondition> element (and recursively it's children)
     * @return ConditionExpr based
     */
    static ConditionExpr loadFromConditionElement(Element show) {

        if (show.getName().equals("showCondition")) { //just for the head of the list!
            if(show.elements().size() != 0){
                return loadFromConditionElement(show.elements().get(0));
            } else {
             return new ConditionExpr("empty condition");   
            }
        } else if (show.getName().equals("SINGLE")) {
            return new ConditionExpr(show.getText());
        } else if (show.getName().equals("AND")) {
            ConditionExpr c1 = loadFromConditionElement(show.elements().get(0));
            ConditionExpr c2 = loadFromConditionElement(show.elements().get(1));
            return new ConditionExpr(c1, c2, ConditionExpr.Type.AND);
        } else if (show.getName().equals("OR")) {
            ConditionExpr c1 = loadFromConditionElement(show.elements().get(0));
            ConditionExpr c2 = loadFromConditionElement(show.elements().get(1));
            return new ConditionExpr(c1, c2, ConditionExpr.Type.OR);
        } else {
            throw new RuntimeException("LOAD FROM CONDITION ELEMENT ERROR");
        }
    }
    
    public boolean containsCondition(Condition c){
        switch(type){
            case SINGLE:
                return getName().equals(c.getName());
            case AND:
            case OR:
                return c1.containsCondition(c) || c2.containsCondition(c);
        }
        throw new RuntimeException("Wrong type on ConditionExpr");
    }
    
    class NonExistentConditionException extends Exception{

        public NonExistentConditionException(String s) {
            super(s);
        }
        
    }
    
}
