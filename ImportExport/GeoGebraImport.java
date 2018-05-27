/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import ChronoMap.Event;
import ChronoMap.Timeline;
import ChronoMap.ChronoMapApp;

/**
 * This is a class that i've created for the solely purpose of importing data from a Timeline i had created on the GeoGebra.
 * It has no more use and will be removed in future releases
 * @author Henri Augusto
 */
@Deprecated
public class GeoGebraImport {
    
    static String chooseFile() throws IOException{
        Stage stage = new Stage();
        stage.setWidth(1500);
        stage.setHeight(700);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml", "*.xml"));
        File f = fileChooser.showOpenDialog(stage);
        return f.getCanonicalPath();
    }
    
    /**
     * Old method that i've used to read data from a timeline i've previously created in GeoGebra. 
     * See java doc comment for {@link GeoGebraImport}
     * @deprecated
     */
    static void loadFromGeogebraXML(Timeline timeline) {
        timeline.clear();
        String path = "C:\\Users\\User\\Desktop\\Juce Time Line\\Juce TimeLine\\Source\\timeline.xml";
        /*try {
            path = chooseFile();
        } catch (IOException ex) {
            Logger.getLogger(GeoGebraImport.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        //LOAD
        SAXReader reader = new SAXReader();
            //reader.setEncoding("UTF-8"); //needed?
        try {
            Document doc = reader.read(path);
            Element root = doc.getRootElement();

            Element construction = root.element("construction");
            ChronoMapApp.app.timeline.events = getEvents(construction);

        } catch (DocumentException ex) {
            //Logger.getLogger(ChronoMap.ImportExport.Export.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("ChronoMap.Import.loadFromGeogebraXML()");
        }
    }
    
    /**
     * 
     * @param construction the element construction from the ggb file, which will contain (element, command) pairs which together describe the events
     * @return 
     */
    static HashSet<Event> getEvents(Element construction){
        HashSet<Event> events = new HashSet<>();
        for (Iterator<Element> it = construction.elementIterator("command"); it.hasNext();) {
            //find the <command>
            Element cmd = it.next();
                //System.out.println();
            String eventName = cmd.element("output").attributeValue("a0");
                System.out.println("=================================\n"+"eventName = " + eventName);
            //find a <element> with same lable and type segment
            Element ggbElement;
            try {
                ggbElement = getGgbElementOfTypeSegmentWithLabel(construction, eventName);
            } catch (IsNoSegmentException ex) {
                continue; //nothing to worry
            }
            Element caption = ggbElement.element("caption");
            //if there is no custom caption just use event name
            String label = getLabel(caption, eventName);
            ConditionExpr condition = GgbConditionParser.getCondition(ggbElement);
            try{
                int start = findStart(cmd);
                int end = findEnd(cmd);
                int height = (int) (-findHeight(cmd));
                Event temp = new Event(label, start, end, height);
                events.add(temp);
                temp.color = getColor(ggbElement);
                temp.setConditionExpr(condition);
                //System.out.println("color = "+temp.color);
            } catch (NumberFormatException e){
                showErrorWindow("Bad XML formatting. Some error occurred while parsing the start, end or height strings\n"+cmd);
            } catch (MatchException e){
                //showErrorWindow("bad XML formatting. Could not find start, end or height from an event");
                //when it's thrown it prints to the console what it should
            }
            
            

            //System.out.println(""+m.group(2));
            //System.out.print
        }
        return events;
    }

    private static String getLabel(Element caption, String defaultLabel) {
        String label = caption == null ? defaultLabel : caption.attributeValue("val");
            //System.out.println("LABEL = " + label);
        return label;
    }

    private static Element getGgbElementOfTypeSegmentWithLabel(Element construction, String eventName) throws IsNoSegmentException {
        List<Node> ggbElementsList = construction.selectNodes("//element[@label='" + eventName + "' and @type='segment']");
            if (ggbElementsList.size() == 0) {
                //System.out.println("IT IS NOT A SEGMENT! => " + eventName);
                throw new IsNoSegmentException();
            }
            return (Element) ggbElementsList.get(0);
    }

    private static int findStart(Element cmd) throws NumberFormatException, MatchException {
        String startStr = match(cmd.element("input").attributeValue("a0"),"\\(-*\\d+");
        //System.out.println("start = "+startStr.substring(1));
        return Integer.parseInt(startStr.substring(1));
    }

    private static int findEnd(Element cmd) throws NumberFormatException, MatchException {
        String endStr = match(cmd.element("input").attributeValue("a1"),"\\(-*\\d+");
        //System.out.println("end = "+endStr.substring(1));
        return Integer.parseInt(endStr.substring(1));     
    }
    
    private static int findHeight(Element cmd) throws NumberFormatException, MatchException {
        String heightStr = match(cmd.element("input").attributeValue("a0"),", -*\\d*"); //a0 or 01. doesn't matter
        //System.out.println("height = "+heightStr.substring(2));
        return Integer.parseInt(heightStr.substring(2));
    }
    
    static void showErrorWindow(String error){
        //System.err.println("BUG ON loadFromGeogebraXML()!!! HOW COME IT DIDN'T FIND THE START OR END OR HEIGHT?");
        Stage stage = new Stage();
        StackPane sp = new StackPane();
        Scene scene = new Scene(sp);
        Label msg = new Label(error);
        sp.getChildren().add(msg);
        stage.setScene(scene);
        stage.show();
    }
    
    static String match(String input, String regex) throws MatchException{
        String out;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        boolean found = m.find();
        if (found) {
            out = input.substring(m.start(), m.end());
        } else {
            throw new MatchException("GeoGebraImport.match() error.\ninput = "+input+"\nregex = "+regex);
        }
        return out;
    }

    private static Color getColor(Element ggbElement) {
        //<objColor r="0" g="0" b="0" alpha="0.0"/>
        Element color = ggbElement.element("objColor");
        String rStr = color.attributeValue("r");
        String gStr = color.attributeValue("g");
        String bStr = color.attributeValue("b");
        int r, g, b;
        try{
            r = Integer.parseInt(rStr);
            g = Integer.parseInt(gStr);
            b = Integer.parseInt(bStr);
        } catch (NumberFormatException e){
            return Color.BLACK;
        }
        return new Color((double)r/255,(double)g/255,(double)b/255,1);
        
        
    }
    
    static class MatchException extends Exception{
        private MatchException(String geoGebraImportmatch_error_Dammit) {
            //showErrorWindow(geoGebraImportmatch_error_Dammit); //static method from GeoGebraImport
            //System.err.println("MathException = \n"+geoGebraImportmatch_error_Dammit);
        }
    }
        

    
    static class IsNoSegmentException extends Exception{}
}
