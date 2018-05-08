/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * This is a recursive descent parser used to parse boolean expressions.
 * @author Henri Augusto
 */
public class ConditionParser {
    
    static ConditionExpr parse(String s) throws ParseException, RuntimeException{
        //debug//Dbg.println("===========================INITIALIZING PARSING========================================================= ", Dbg.ANSI_BLUE);
        //return parse(s,0);
        ConditionExpr e = parse(s.replace(" ", ""), 0);
        //debug//Dbg.println("EXPRESSION AFTER PARSING===============\n"+e.toString(), Dbg.BRIGHT_BLACK_BACKGROUND+Dbg.ANSI_WHITE);
        return e;
    }
    
    private static ConditionExpr parse(String s, int depth) throws ParseException, RuntimeException{
        //debug//Dbg.println("======", Dbg.ANSI_PURPLE_BACKGROUND+Dbg.ANSI_WHITE);
        if(depth>=15){
            throw new ParseException("Some evil error that resulted in an infinite loop");
        }
        /*debug stuff*/
        /*
                String dbg = "";
                for (int i = 0; i < depth; i++) {
                    dbg = "    " + dbg;        
                }
                System.out.println(dbg + "Parsing[" + depth + "]: " + s);
        */
        //s = s.replace(" ", "");
        //s = s.replace("\n", "");
        int currentDepth = depth;
        int maxDepth = depth;
        Deque<Integer> openPos = new ArrayDeque<>();
        ArrayDeque<Integer> closePos = new ArrayDeque<>();
        List<OperatorOccurrence> listOpAnd = new ArrayList<>();
        List<OperatorOccurrence> listOpOr = new ArrayList<>();
        List<ParenthesisExpression> listPExp = new ArrayList<>();
        for (int i = 0; i < s.length(); ++i) {
            String c = s.substring(i, i+1);
            String cOp = s.substring(i, Math.min(i+2,s.length()) ); //try to read 2 chars
            switch(c){
                case "(":
                    openPos.add(i);
                    ++currentDepth;
                    maxDepth = currentDepth > maxDepth ? currentDepth : maxDepth;
                    break;
                case ")":
                    closePos.add(i);
                    int start = openPos.getLast();
                    int end = closePos.getFirst();
                    ParenthesisExpression pe = new ParenthesisExpression(start, end, currentDepth,
                        s.substring(openPos.removeLast(), closePos.removeFirst()+1)
                    );
                    listPExp.add( pe );
                    --currentDepth;
                    break;
            }
            switch(cOp){
                case "&&":
                    listOpAnd.add(   new OperatorOccurrence(i, i+2, cOp, currentDepth)  );
                    break;
                case "||":
                    listOpOr.add(   new OperatorOccurrence(i, i+2, cOp, currentDepth)  );
                    break;
            }
            //continue to parse the rest
        }
        //======================================================================
        //Finished parsing the whole string. Get all operators on this depth
        List<OperatorOccurrence> listOpOnThisDepth = new ArrayList<>(listOpAnd);
            listOpOnThisDepth.addAll(listOpOr);
        for (Iterator<OperatorOccurrence> it = listOpOnThisDepth.iterator(); it.hasNext();) {
            OperatorOccurrence next = it.next();
            if( next.depth != depth ){   it.remove(); }
        }
        //Dbg.println("listOpOnThisDepth.size(): "+listOpOnThisDepth.size());
        //======================================================================
        //Let's decide what to do
        
        //this is the case of A when on depth 0 or 1
        if( listOpAnd.isEmpty() && listOpOr.isEmpty() && maxDepth == depth){
            //debug//Dbg.println("THIS IS A SINGLE CONDITION", Dbg.ANSI_PURPLE_BACKGROUND+Dbg.ANSI_GREEN);
            return new ConditionExpr(s);
        } else
            //this is the case of ((A)) when on depth 0 or 1
        if( listOpAnd.isEmpty() && listOpOr.isEmpty() && maxDepth != depth){
            return parse(s.substring(1, s.length()-1) , depth+1);
        } else
            //this is the case of ((A&&B)) when on depth 0 or 1
        if( listOpOnThisDepth.isEmpty() && (!listOpAnd.isEmpty() || !listOpOr.isEmpty()) ){
            //debug//Dbg.println("THIS IS A EMPTY DEPTH!    "+s+"\nLET'S DESCEND", Dbg.ANSI_PURPLE_BACKGROUND+Dbg.ANSI_GREEN);
            return parse( s.substring(1,s.length()-1 ), depth+1);
        }
        if(listOpOnThisDepth.size() == 1){
            OperatorOccurrence op = listOpOnThisDepth.get(0);
            //debug//Dbg.println("THIS IS A "+op.operator+" CONDITION", Dbg.ANSI_BLUE_BACKGROUND+Dbg.ANSI_WHITE);
            BinaryExpression npe = new BinaryExpression(s, listOpOnThisDepth.get(0), depth);
            switch(op.operator){
                default:
                    throw new RuntimeException("TA MALUCO CARA?");
                case "&&":
                    return new ConditionExpr( parse(npe.left, depth+1), parse(npe.right, depth+1) , ConditionExpr.Type.AND);
                case "||":
                    return new ConditionExpr( parse(npe.left, depth+1), parse(npe.right, depth+1) , ConditionExpr.Type.OR);
            }
        } else {
            //debug//Dbg.println("INVALID: "+s, Dbg.ANSI_RED);
            //LETS ADJUST THE AND AND OR OPERATORs on this depth
            
            if( !listOpOnThisDepth.isEmpty() ){
                List<OperatorOccurrence> ands = getOnlyOperators(listOpOnThisDepth, "&&"); 
                List<OperatorOccurrence> ors = getOnlyOperators(listOpOnThisDepth, "||");
                //there are ands to adjust. Let's read the first
                if( !ands.isEmpty() ){
                    BinaryExpression npe = new BinaryExpression(s, ands.get(0), depth);
                    s = replaceWithParenthesizedExp(s, npe);
                    return parse(s, depth + 1);//+1?
                    //there are ors to adjust. Let's read the first
                } else if( !ors.isEmpty() ){
                    BinaryExpression npe = new BinaryExpression(s, ors.get(0), depth);
                    s = replaceWithParenthesizedExp(s, npe);
                    return parse(s, depth + 1);//+1?
                    //there are unkown operators?! 
                } else {
                    throw new ParseException("We are trying to parse an unkown operator");
                }
                //if there are no operators
            }
        }
        throw new ParseException("Unkown ParseException");
    } //end of Parse
    
    static int nOfOccurrences(String source, String checking){
        //System.out.println("Checking number of occurrences of "+checking+" inside of "+source);
        int out = 0;
        for (int i = 0; i < source.length(); i++) {
            int dest = Math.min(i+checking.length(), source.length());
            out = source.substring(i, dest).equals(checking) ? out+1 : out;
        }
        //System.out.println("   result: "+out);
        return out;
    }

    private static String replaceWithParenthesizedExp(String s, BinaryExpression npe) {
        //debug//Dbg.println("replacing "+s,Dbg.ANSI_CYAN);
        //debug//Dbg.println("        expression "+npe.expr,Dbg.ANSI_CYAN);
        String out = s.substring(0, npe.start)+"("+npe.expr+")"+s.substring(npe.end);
        //debug//Dbg.println("replaced! "+out,Dbg.ANSI_CYAN);
        return out;
    }
    
    static class OperatorOccurrence{
        int start;
        int end;
        String operator;
        int depth;

        public OperatorOccurrence(int start, int end, String operator, int depth) {
            this.start = start;
            this.end = end;
            this.operator = operator;
            this.depth = depth;
            //debug//String dbg = "Operator Occurrence["+depth+"]: " + operator;
            //debug//dbg = appendDepthIndent(dbg, depth);
            //debug//System.out.println(dbg);
        }

        @Override
        public String toString(){
            return "OperatorOccurrence: "+operator+"\n"+
            "    start,end: "+start+", "+end+"\n"+
            "    depth: "+depth;
        }
    }
    
    /**
     * this class is used just for Debugging purposes to print what is in parethesized expressions
     */
    static class ParenthesisExpression{
        int start, end, depth;
        String expr;

        public ParenthesisExpression(int start, int end, int depth, String expr) {
            this.start = start;
            this.end = end;
            this.depth = depth;
            this.expr = expr;
            /*
            String dbg =  "ParenthesisExpression["+depth+"]: " + expr;
            dbg = appendDepthIndent(dbg, depth);
            System.out.println(dbg);
            */
            //System.out.println("    start,end: " + start + ", " + end);
            //System.out.println("    depth: " + depth);
        }
       
    }
    
    static class BinaryExpression{
        String expr, operator;
        int depth;
        int start,end;
        String left, right;

        public BinaryExpression(String expr, String operator, int depth) {
            this.expr = expr;
            this.operator = operator;
            this.depth = depth;
        }

        public BinaryExpression(String originalString, OperatorOccurrence opOcc, int depth) throws ParseException {
            this.depth = depth;
            //debug//Dbg.println("Constructing NonParenthesisExpression from "+originalString);
            //debug//Dbg.println(""+opOcc);
            expr = getLeftSide(originalString, opOcc)+opOcc.operator+getRightSide(originalString, opOcc);
            String dbgExpr = Dbg.ANSI_BLUE+left+Dbg.ANSI_BLACK+opOcc.operator+Dbg.ANSI_PURPLE+right+Dbg.ANSI_RESET;
            //debug//Dbg.println("NonParenthesisExpression: "+dbgExpr);
        }
        
        private String getLeftSide(String originalString, OperatorOccurrence opOcc) throws ParseException{
            //debug//Dbg.println("getting LeftSide of "+originalString);
            left = null;
            int currentDepth = 0; //so we can get stuff inside parenthesis stuff
            for (int i = opOcc.start; i >= 1; --i) {
                String c = originalString.substring(i-1, i);
                //debug//Dbg.println("c: "+c,Dbg.ANSI_BLUE_BACKGROUND+Dbg.ANSI_WHITE);
                switch(c){
                    case "(":
                        --currentDepth;
                        break;
                    case ")":
                        ++currentDepth;
                        break;
                }
                //for all conditions you must check if currentDepth==0 otherwise you're going to exit in the middle of an expression
                if( currentDepth==0 && (c.equals("(") || i==1 )){
                    left = originalString.substring(i-1, opOcc.start);
                    start = i-1;
                    break;
                } else
                if( currentDepth==0 && ( c.equals("|") || c.equals("&"))  ){
                    left = originalString.substring(i, opOcc.start);
                    start = i;
                    break;
                }
            }
            if(left==null){
                throw new ParseException("There was an error trying to parse the left side of \"" + originalString + "\"\nOperator["+opOcc.depth+"] was: "+opOcc.operator+". (start, end) =  )"+opOcc.start+", "+end+")");
            }
            //debug//Dbg.println("result "+left);
            return left;
        }

        private String getRightSide(String originalString, OperatorOccurrence opOcc) throws ParseException {
            //debug//Dbg.println("getting RightSide of " + originalString);
            right = null;
            int currentDepth = 0; //so we can get stuff inside parenthesis stuff
            for (int i = opOcc.end; i <= originalString.length()-1; ++i) {
                String c = originalString.substring(i, i+1);
                //debug//Dbg.println("c: " + c, Dbg.ANSI_PURPLE_BACKGROUND+Dbg.ANSI_WHITE);
                switch (c) {
                    case "(":
                        ++currentDepth;
                        break;
                    case ")":
                        --currentDepth;
                        break;
                }
                //for all conditions you must check if currentDepth==0 otherwise you're going to exit in the middle of an expression
                if (   ( i == originalString.length()-1 || c.equals(")") ) && currentDepth == 0) {
                    right = originalString.substring(opOcc.end, i+1);
                    end = i+1;
                    break;
                }
                if (   currentDepth == 0 && ( c.equals("|") || c.equals("&"))   ) {
                    right = originalString.substring(opOcc.end, i);
                    end = i;
                    break;
                }
            }
            if (right == null) {
                throw new ParseException("There was an error trying to parse the right side of \"" + originalString + "\"\nOperator["+opOcc.depth+"] was: "+opOcc.operator+". (start, end) =  ("+opOcc.start+", "+end+")");
            }
            //debug//Dbg.println("result " + right);
            return right;
        }
        
        
    }

    
    
    
    
    static String appendDepthIndent(String s, int depth){
        for (int i = 0; i < depth; i++) {
            s = "        "+s;
        }
        return s;
    }
    
    /**
     * gets an list with any kind of operator and returns a list with only operators of type "&&"
     * @return a list with only operators of type "&&"
     */
    static List<OperatorOccurrence> getOnlyOperators(List<OperatorOccurrence> source, String op){
        List<OperatorOccurrence> out = new ArrayList<>(source);
        for (Iterator<OperatorOccurrence> it = out.iterator(); it.hasNext();) {
            OperatorOccurrence next = it.next();
            if ( !next.operator.equals(op)) {
                it.remove();
            }
        }
        return out;
    }
    
    
    
    static class ParseException extends Exception{

        public ParseException(String s) {
            super(s);
        }
        
    }
}
