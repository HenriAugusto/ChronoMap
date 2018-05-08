/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Henri Augusto
 */
public class CreateEventStageManager {
    static Stage inputStage = null;
    static Scene scene = null;
    static ConditionExpr cExpr = null;
    static boolean isConditionValid = false;
    static TextField nameField, startField, endField, heightField, conditionField;
    static Label conditionStatus = null;
    static ChangeListener conditionUpdateListener;
       
    static void showEventWindow(){
        if(inputStage==null){
            createEventWindow();
        } else {
            parseAgain();
        }
        inputStage.show();
    }
    
    /**
     * This method initializes everything on the stage. Note that it does NOT displays the stage. For that use showEventWindow()
     */
    static void createEventWindow(){
        inputStage = new Stage();
        inputStage.setTitle("Create event");
        
        Label createEventLabel = new Label("Create event:");
            createEventLabel.setFont(   new Font(20)   );
            
        Label nameLabel = new Label("Event name");
            nameField = new TextField("test");

        Label startLabel = new Label("start date");
            startField = new TextField();

        Label endLabel = new Label("end date");
            endField = new TextField();
            
        Label heightLabel = new Label("event height");
            heightField = new TextField();

        VBox vbox = new VBox(
                createEventLabel, 
                nameLabel, nameField, 
                startLabel, startField, 
                endLabel, endField,
                heightLabel, heightField
        );
        
        scene = new Scene(vbox);
        inputStage.setScene(scene);
        
        Label conditionLabel = new Label("Condition for exhibition");
        conditionField = new TextField("");
        
        conditionField.setMinWidth(600);
        conditionStatus = new Label("Condition status");
            conditionStatus.setMinHeight(100);
            conditionStatus.setFont( new Font(18));
            
        
        vbox.getChildren().addAll(conditionLabel, conditionField, conditionStatus);
        Text text = new Text(
                "Travam:\n"+
                "(A||B||C&&A)\n"+
                "(A(B))\n"+
                "(A && B) && (C || D || J) && C\n"+
                "NÃO Travam:\n"+
                "( A && (A && B) && (C || D || (J&&Q)) || (M && (J || A) ) || (C && W ) )"
        );
                //vbox.getChildren().addAll(text);
        setHandlers();
        //inputStage.show();
    }
    
   static void setHandlers(){
       scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent e) {
               System.out.println(e.getCode().toString() + "");
               if (e.getCode().toString().equals("ENTER") && isConditionValid) {
                   e.consume();//needed because of some bug https://stackoverflow.com/questions/47797980/javafx-keyevent-triggers-twice
                       String name = nameField.getText();
                       int start = Integer.parseInt(startField.getText());
                       int end = Integer.parseInt(endField.getText());
                       int height = Integer.parseInt(heightField.getText());
                       //Event event = TimelineFXApp.app.timeline.createEvent(name, start, end, (int) (Math.random() * 1500 - 750));
                       Event event = new Event(name, start, end, height);
                       event.setConditionExpr(cExpr);
                       CommandHandler.executeCommand( new CmdNewEvent(TimelineFXApp.app.timeline, event) );
                   inputStage.close();
               }
               if (e.getCode().toString().equals("ESCAPE")) {
                   inputStage.close();
               }
           }
       });
       conditionUpdateListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
           isConditionValid = false;
           try {
               cExpr = ConditionParser.parse(conditionField.getText());
               boolean isValid = false;
               cExpr.validate();
               conditionStatus.setText(cExpr.toString());
               conditionStatus.setTextFill(Color.LIMEGREEN);
               isConditionValid = true;
           } catch (ConditionParser.ParseException ex) {
               conditionStatus.setText("parsing error:\n" + ex.getMessage());
               conditionStatus.setTextFill(Color.RED);
               isConditionValid = false;
           } catch (ConditionExpr.NonExistentConditionException necex) {
               conditionStatus.setText("Contains non-existent condition:\n" + necex.getMessage());
               conditionStatus.setTextFill(Color.DARKRED);
               isConditionValid = false;
               //Catch anything else
           } catch (RuntimeException rex) {
               conditionStatus.setText("Unkown parse error:\n" + rex.toString()/*+"\n"+rex.getLocalizedMessage()*/);
               conditionStatus.setTextFill(Color.RED);
               isConditionValid = false;
               //rex.printStackTrace();
           }
       };
       //NOT USING THE HANDLE
       ChangeListenerHandle clh = new ChangeListenerHandle(conditionField.textProperty(), conditionUpdateListener); 
       conditionField.textProperty().addListener( conditionUpdateListener);
   }

    static void parseAgain() {
        //this is here just for triggering the listener
        //so if any change happened (and a condition was deleted for ex) 
        //the text will be parsed again to depict the correct status
        String text = conditionField.getText();
        conditionField.setText(" " + text);
        conditionField.setText(text);
        //note that i still doesn't update after adding removing a condition in the conditions manager window!
    }

    /**
     * Provides a WritableImage that is a snapshot from this GUI. Useful for displaying it in the HelpPages
     * @return 
     */
    static public WritableImage getGuiSnapshot() {
        
        if (inputStage == null){
            createEventWindow();
        }
        
        //https://stackoverflow.com/questions/30983584/how-to-get-the-size-of-a-label-before-it-is-laid-out
        inputStage.getScene().getRoot().applyCss();
        inputStage.getScene().getRoot().layout(); 
        //i've discovered that accidentally! Note it needs the above lines to work!!
        double width = (inputStage.getScene().getRoot().boundsInLocalProperty().getValue().getMaxX()-inputStage.getScene().getRoot().boundsInLocalProperty().getValue().getMinX());
        double height = inputStage.getScene().getRoot().boundsInLocalProperty().getValue().getHeight();
        
        System.out.println("inputStage.getScene().getWidth() = "+inputStage.getScene().getWidth());
        System.out.println("inputStage.getScene().getHeight() = "+inputStage.getScene().getHeight());
        System.out.println("wadafuq = "+(inputStage.getScene().getRoot().boundsInLocalProperty().getValue().getMaxX()-inputStage.getScene().getRoot().boundsInLocalProperty().getValue().getMinX()));
        System.out.println("wadafuq = "+inputStage.getScene().getRoot().boundsInLocalProperty().getValue().getHeight());
        
        //System.out.println("inputStage.getScene().getRoot().getHeight() = "+inputStage.getScene().getRoot().getHeight());
        //int w = inputStage.getScene().getWidth() > 0 ? (int) inputStage.getScene().getWidth() : 400;
        //int h = inputStage.getScene().getHeight() > 0 ? (int)  inputStage.getScene().getHeight() : 400;
        WritableImage image = new WritableImage(
                (int)width,
                (int)height);
        inputStage.getScene().snapshot(image);
        return image;
    }
    
}
