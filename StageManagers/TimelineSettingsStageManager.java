/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * This stage is used to edit Timeline settings such as min/max date and min/max display hight
 * @author Henri Augusto
 */
public class TimelineSettingsStageManager {
    static Stage settingsStage = null;
    static Scene scene = null;
    static TextField nameField, minYearField, maxYearField, setHeightField;
    
       
    /**
     * Shows the Timeline settings window, initializing everything when necessary
     */
    static void showEventWindow(){
        if(settingsStage==null){
            createEventWindow();
        }
        updateFields();
        settingsStage.show();
    }
    
    /**
     * Initializes the Stage, scene and all the necessary Nodes for the timeline settings window. 
     */
    private static void createEventWindow(){
        settingsStage = new Stage();
            settingsStage.setTitle("Edit timeline settings");
            settingsStage.setAlwaysOnTop(true);
        VBox root = new VBox();
            root.setPadding( new Insets(10) );
        scene = new Scene(root);
        settingsStage.setScene(scene);
            
        Label stageTitle = new Label("Edit Timeline Settings");
            stageTitle.setFont( new Font(20) );
        root.getChildren().add( stageTitle );
        
        /* Name */
        Label setTimelineNameLabel = new Label("Set timeline name");
        nameField = new TextField();
            root.getChildren().add( setTimelineNameLabel );
            root.getChildren().add( nameField );
        /* Min Year*/
        Label minYearLabel = new Label("Minimum year to be displayed");
        minYearField = new TextField();
            root.getChildren().add( minYearLabel );
            root.getChildren().add( minYearField );
        /* Max Year*/
        Label maxYearLabel = new Label("Maximum year to be displayed");
        maxYearField = new TextField();
            root.getChildren().add( maxYearLabel );
            root.getChildren().add( maxYearField );
        /* Height Year*/
        Label setHeightLabel = new Label("Timeline height");
        setHeightField = new TextField();
            root.getChildren().add( setHeightLabel );
            root.getChildren().add( setHeightField );
        
        
        
        Label enterToConfirm = new Label("Press Enter to confirm.");
        enterToConfirm.setFont(new Font(17));
        VBox enterToConfirmVBox = new VBox();
        enterToConfirmVBox.getChildren().add(enterToConfirm);
        enterToConfirmVBox.setAlignment(Pos.CENTER);
        root.getChildren().addAll(enterToConfirmVBox);
        setEventHandlers();
    }
    
    static void setEventHandlers(){
        settingsStage.addEventHandler(KeyEvent.KEY_PRESSED,
                (e) -> {
                  switch(e.getCode()){
                      case ENTER:
                          commitChanges();
                          break;
                  }
                }
        );
    }

    private static void commitChanges() {
        try{
            Integer newMinYear = Integer.parseInt( minYearField.getText() );
            Integer newMaxYear = Integer.parseInt( maxYearField.getText() );
            Integer newHeight = Integer.parseInt( setHeightField.getText() );
            ChronoMapApp.app.timeline.name = nameField.getText();
            ChronoMapApp.app.timeline.minYear = newMinYear;
            ChronoMapApp.app.timeline.maxYear = newMaxYear;
            ChronoMapApp.app.timeline.height = newHeight;
            settingsStage.hide();
            ChronoMapApp.app.timeline.gview.setMinCenter( newMinYear,-newHeight/2 );
            ChronoMapApp.app.timeline.gview.setMaxCenter( newMaxYear,newHeight/2);
            ChronoMapApp.app.draw();
        } catch (NumberFormatException nfex){
            return;
        } catch (Exception ex){
            System.err.println("Some unkown exception was thron in TimelineSettingsStagemanager.commitChanges()");
        }
        
    }
    
    static void updateFields(){
        int minYear = ChronoMapApp.app.timeline.minYear;
        int maxYear = ChronoMapApp.app.timeline.maxYear;
        int height = ChronoMapApp.app.timeline.height;
        String name = ChronoMapApp.app.timeline.name;
        nameField.setText(""+name);
        minYearField.setText(""+minYear);
        maxYearField.setText(""+maxYear);
        setHeightField.setText(""+height);
        
    }
    
}

