/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author User
 */
public class Import {
    
    static void loadFromFile(Timeline timeline){
        Stage stage = new Stage();
        stage.setWidth(1500);
        stage.setHeight(700);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml", "*.xml"));
        File f = fileChooser.showOpenDialog(stage);
        if( f==null ){
            return;
        }
        //If the user have choosen a file let's proceed
        timeline.clear();
        String path = "C:\\Users\\User\\AppData\\Roaming\\Microsoft\\Windows\\Network Shortcuts\\c.xml";
        try {
            path = f.getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(Import.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
        System.out.println("LOADING FROM FILE: "+path);
            SAXReader reader = new SAXReader();
                GUIMessages.displayMessage("Reading UTF-8");
                reader.setEncoding("UTF-8"); //SOLVED org.dom4j.DocumentException: invalid byte ???????
            //reader.setEncoding("UTF-8"); //needed?
            try {
                Document doc = reader.read(path);
                Element root = doc.getRootElement();

                Element construction = root.element("construction");
                Element conditions = root.element("conditions");
                if(conditions == null){
                    System.out.println("DIDN'T FIND CONDITION");
                } else {
                    Dbg.println("Loading Conditions from element\n" + conditions.asXML(), Dbg.ANSI_PURPLE_BACKGROUND+Dbg.ANSI_BLUE);
                }
                List<Element> events = root.elements("event");
                loadEvents(timeline, events);
                
                loadConditions(timeline,conditions);
            } catch (DocumentException ex) {
                Logger.getLogger(timelinefx.Export.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("timelinefx.Import.loadFromGeogebraXML() DOCUMENT EXCEPTION!!!!!");
            }
            System.out.println("loading done!");
    }

    private static void loadEvents(Timeline timeline, List<Element> events) {
        Set<Event> temp = new HashSet();
        for (Element e : events) {
            temp.add( new Event(e) );
        }
        timeline.events = temp;
    }

    private static void loadConditions(Timeline timeline, Element conditions) {
        //System.out.println("====================loadConditions()");
        timeline.conditions.clear();
        for (Element c : conditions.elements()) {
            Condition temp = getConditionFromElement(c);
            timeline.conditions.add(temp);
        }
        /*for (Condition condition : timeline.conditions) {
        System.out.println("CONDITION = "+condition);
        }*/
        ConditionsWindowStageManager.updateTitledPanes();
    }
    
    /**
     * Given a Element object it returns the condition stored in the XML Element
     * @param e the Element Object containing the Condition's XML Information
     * @return Condition parsed from the xml data
     */
    private static Condition getConditionFromElement(Element e){
        List<Element> children = e.elements();
        if(  children.isEmpty()  ){
            //return new Condition(e.attributeValue("name"), Boolean.parseBoolean(e.attributeValue("value")) );
            Condition out = new Condition(e.attributeValue("name") );
                out.register();
            return out;        
        }
        //Condition output = new Condition(e.attributeValue("name"), Boolean.parseBoolean(e.attributeValue("value")));
        Condition out = new Condition( e.attributeValue("name") );
            out.register();
        for (Element child : children) {
            out.subs.add( getConditionFromElement(child) );
        }
        return out;
    }
    
}
