/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.Element;

/**
 *
 * @author User
 */
public class GgbConditionParser {
    
    static String getConditionExpressionInParenthesis(String in){
        String output;
        //Pattern p = Pattern.compile("\\(\\w+ (∧|∨) \\w+\\)");
        Pattern p = Pattern.compile("\\(\\w+ (∧|∨) \\w+\\)",Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = p.matcher(in);
        boolean found = m.find();
        if (found) {
            output = in.substring(m.start() + 1, m.end() - 1); //remove parenthesis
            //System.out.println("CONDITION IN PARENTHESIS = ["+output+"]");
            return output;
        } else {
            //System.out.println("didn't have CONDITION IN PARENTHESIS = "+in+"");
            return "";
        }
    }

    static String getOperator(String in) {
        String output;
        Pattern p = Pattern.compile("(∧|∨)");
        Matcher m = p.matcher(in);
        boolean found = m.find();
        //System.out.println("found = " + found);
        if (found) {
            output = in.substring(m.start(), m.end()); //remove parenthesis
            return output;
        } else {
            System.out.println("====DID NOT FIND OPERATOR IN "+in);
            return "";
        }
    }

    static ConditionExpr getExp(String in) {
        //System.out.println("=========parsing expression that was on parenthesis = "+in);
        ConditionExpr output;
        String operator = GgbConditionParser.getOperator(in);
            //System.out.println("timelinefx.GeoGebraImport.getCondition() = OPERATOR = ["+operator+"]");
        Pattern p = Pattern.compile("(\\w+)",Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = p.matcher(in);
        int i = 0;
        String[] names = new String[2];
        while(m.find()){
            //System.out.println("MATCH = "+in.substring(m.start(), m.end()));
            names[i] = in.substring(m.start(), m.end());            
            ++i;
        }
        //System.out.println("MATCH COUNT = "+i);
        
        ConditionExpr c1, c2;
        c1 = new ConditionExpr(names[0]);
        c2 = new ConditionExpr(names[1]);
        switch(operator){
            case "∧":
                return new ConditionExpr(c1, c2, ConditionExpr.Type.AND);
            case "∨":
                return new ConditionExpr(c1, c2, ConditionExpr.Type.OR);
            default:
                System.err.println("WRONG OPERATOR!!!! = "+operator);
                throw new RuntimeException("WRONG OPERATOR!!!!");
        }
    }
    
    /**
     * rename after to getCondition
     */
    static String getStuffOutOfParenthesis(String in){
        Pattern p = Pattern.compile("\\w+ (∧|∨) \\(", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = p.matcher(in);
        int i = 0;
        if (m.find()) {
            //System.out.println("MATCH = "+in.substring(m.start(), m.end()));
            return in.substring(m.start(), m.end());
        } else {
            throw new RuntimeException("COULD NOT GET SUTFF OUT OF PARENTHESIS");
        }
        
    }

    static boolean isOuterExpr(String in) {
        //System.out.println("IS OUTER EXPR?????????   "+in);
        Pattern p = Pattern.compile("(∧|∨)");
        Matcher m = p.matcher(in);
        boolean out =  m.find();
        return out;
    }

    static ConditionExpr getCondition(Element ggbElement) {
        //==============get condition string (showObject)==========
        Element cond = ggbElement.element("condition");
        String showObject = cond == null ? "empty condition" : cond.attributeValue("showObject");
        //System.out.println("============================ showObject = "+showObject);
        //==============Parse parethesis group=============
        String conditionExpression = GgbConditionParser.getConditionExpressionInParenthesis(showObject);
        //RETURN OR PARSE STUFF IN PARENTHESIS?
        if (conditionExpression.equals("")) {
            if (GgbConditionParser.isOuterExpr(showObject)) {
                return GgbConditionParser.getExp("(" + showObject + ")");
            }
            return new ConditionExpr(showObject); //SINGLE
        }
        String outerCond = GgbConditionParser.getStuffOutOfParenthesis(showObject);
        String outerOperator = GgbConditionParser.getOperator(outerCond);
        // now we can remove the operator part
        outerCond = outerCond.substring(0, outerCond.length() - 4);
        ConditionExpr expr = GgbConditionParser.getExp(conditionExpression);
        ConditionExpr outer = new ConditionExpr(outerCond);
        ConditionExpr.Type type;
        switch (outerOperator) {
            case "∧":
                type = ConditionExpr.Type.AND;
                break;
            case "∨":
                type = ConditionExpr.Type.OR;
                break;
            default:
                System.out.println("============WRONG OPERATOR = " + outerOperator);
                throw new RuntimeException("WRONG OUTER OPERATOR");
        }
        System.out.println("CREATING FROM COMBINED EXPRESSION = outer + expr");
        System.out.println("        outer = "+outer);
        System.out.println("        expr = "+expr);
        ConditionExpr fullCondition = new ConditionExpr(outer, expr, type);
        return fullCondition;
    }
    
}
