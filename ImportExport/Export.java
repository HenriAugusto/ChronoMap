/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


/**
 *
 * @author Henri Augusto
 */
public class Export {
    
    static void saveXML(Timeline timeline) {
        Stage stage = new Stage();
        stage.setWidth(1500);
        stage.setHeight(700);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select save file for your timeline");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml", "*.xml"));
        File f = fileChooser.showSaveDialog(stage);
        String path = "";
        try {
            path = f.getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(Timeline.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        /* This is not needed because the java filechooser is smart enough to do that for us
        if( f.isFile() ){
            try {
                if ( !YesOrNoStage.getBooleanFromUser("File "+f.getName()+" already exists! Do you want to replace?") ){
                    return;
                }
            } catch (YesOrNoStage.UserCanceledException ex) {
                return;
            }
        }
        */
        //FileWriter out; //FOUND THE BUG. FileWriter constructurs assume DEFAULT ENCODINGs are OKAY
        FileWriter notUsed;
        FileOutputStream out;
        //OutputStreamWriter out;
        //https://stackoverflow.com/questions/11371154/outputstreamwriter-vs-filewriter
        try {
            //out = new FileWriter(path);
            out = new FileOutputStream(path);
            Document doc = DocumentHelper.createDocument();
                doc.setXMLEncoding("UTF-8");
            Element root = doc.addElement("timeline");
            saveSettings(timeline, root);
            saveConditions(timeline, root);
            for (Event e : timeline.events) {
                e.saveXML(root);
            }
            //doc.write(out);
            //out.close();
            
            // Pretty print the document to System.out
            OutputFormat format = OutputFormat.createPrettyPrint();
                format.setEncoding("UTF-8"); //SOLVED org.dom4j.DocumentException: invalid byte ???????
                format.setIndentSize(4);
                //GUIMessages.displayMessage("Writing UTF-8");
                GUIMessages.displayMessage("Writing "+f.getName());
            XMLWriter writer = new XMLWriter(out, format);
            //GUIMessages.displayMessage("Actual encoding: "+format.getEncoding());
            //GUIMessages.displayMessage("doc.getXMLEncoding(): "+doc.getXMLEncoding());
            //GUIMessages.displayMessage("out.getEncoding(): "+out.getEncoding());
            writer.write(doc);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Timeline.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private static void saveSettings(Timeline timeline, Element root){
        Element settings = root.addElement("settings");
        settings.addElement("name").setText(timeline.name);
        settings.addElement("minYear").setText(""+timeline.minYear);
        settings.addElement("maxYear").setText(""+timeline.maxYear);
        settings.addElement("height").setText(""+timeline.height);
    }

    private static void saveConditions(Timeline timeline, Element root) {
        Element conditions = root.addElement("conditions");
        for (Condition c : timeline.conditions) {
            addCondition(conditions,c);
        }
    }

    private static void addCondition(Element e, Condition c) {
        Element condition = e.addElement("condition");
        condition.addAttribute("name", c.getName());
        condition.addAttribute(
                    "value",
                    ""+ConditionHandler.conditionsMap.get( c.getName() )
            );
        if( c.subs.isEmpty() ){
            return;
        }
        for (Condition sub : c.subs) {
            addCondition(condition, sub);
        }
        
    }
    

}
