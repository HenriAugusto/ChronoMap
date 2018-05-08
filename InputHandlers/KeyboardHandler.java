/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import java.util.HashSet;
import java.util.Set;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;

/**
 * Class with static methods for dealing with keyboard input on the <b>main stage</b>
 * @author Henri Augusto
 */
public class KeyboardHandler {
    
    static void keyPressed(KeyEvent e){
        String s = e.getCode().toString();
        System.out.println("timelinefx.KeyboardHandler.keyPressed() = "+s);
        switch(s){
            case "DELETE":
                CommandHandler.executeCommand( 
                        new CmdDeleteEvents(TimelineFXApp.app.timeline, TimelineFXApp.app.timeline.selectedEvents)
                );
                break;
            case "DIGIT3":
                if(e.isControlDown()){
                    GUIMessages.displayMessage("Creating 3D Visualization...");
                    ThreeDimensionalVisualizationStageManager.showWindow();
                }
                break;
            case "F1":
                if(e.isControlDown()){
                    GUIMessages.displayLogStage();
                    break;
                }
                HelpStageManager.showWindow();
                break;
            case "F10":
                GraphicsContext gc = TimelineFXApp.app.cnv.getGraphicsContext2D();
                if (e.isControlDown()) {
                    TimelineFXApp.app.timeline.gview.setZoom(gc, 1);
                } else {    
                    TimelineFXApp.app.timeline.gview.addZoom(gc, 0.1);
                }
                break;
            case "F11":
                gc = TimelineFXApp.app.cnv.getGraphicsContext2D();
                if (e.isControlDown()) {
                    TimelineFXApp.app.timeline.gview.setZoom(gc, 2);
                } else {
                    TimelineFXApp.app.timeline.gview.addZoom(gc, -0.1);
                }
                break;
            case "F":
                if(e.isControlDown()){
                    EventSearcher.showSearchWindow();
                }
                break;
            case "F5":
                TimelineFXApp.app.browser.toggleVisible();
                break;
            case "F6":
                ConditionsWindowStageManager.showWindow();
                break;
            case "F12":
                double factor = !e.isControlDown() ? 1.1 : 1/1.1;
                for (Event event : TimelineFXApp.app.timeline.events) {
                    //event.height *= factor;
                }
                break;
            case "S":
                if(e.isControlDown()){
                    System.out.println("timelinefx.KeyboardHandler.keyPressed()");
                    TimelineFXApp.app.timeline.saveXML();
                }
                break;
            case "L":
                if(e.isControlDown()){
                    System.out.println("timelinefx.KeyboardHandler.keyPressed()");
                    TimelineFXApp.app.timeline.loadFromFile();
                } else if( e.isShiftDown() ){
                    if(TimelineFXApp.app.timeline.selectedEvents.size()==1)
                        TimelineFXApp.app.timeline.selectedEvents.iterator().next().goToNextLink();
                }
                break;
            case "H":
                if (e.isControlDown()) {
                    TimelineFXApp.app.mainWindow.setFullScreen(
                            !TimelineFXApp.app.mainWindow.isFullScreen()
                    );
                }
                break;
            case "UP":
                int amt = 10;
                if( e.isAltDown() ){    amt = 30;   }
                if( e.isShiftDown() ){    amt = 1;   }
                if( e.isControlDown() ){    amt = 5;   }
                CommandHandler.executeCommand( new CmdMoveEvents(TimelineFXApp.app.timeline.selectedEvents, -amt));
                break;
            case "DOWN":
                amt = 10;
                if( e.isAltDown() ){    amt = 30;   }
                if( e.isShiftDown() ){    amt = 1;   }
                if( e.isControlDown() ){    amt = 5;   }
                CommandHandler.executeCommand( new CmdMoveEvents(TimelineFXApp.app.timeline.selectedEvents, amt));
                break;
            case "N":
                if(e.isControlDown()){
                    CreateEventStageManager.showEventWindow();
                }
                break;
            case "Z":
                if(e.isControlDown())
                    CommandHandler.undoLastCommand();
                break;
            case "Y":
                if (e.isControlDown()) {
                    CommandHandler.redoLastCommand();
                }
                break;
            case "E":
                if(e.isControlDown())
                    EditEventStageManager.showEventWindow();
                break;
        }
        TimelineFXApp.app.draw();
    }
    
    
    
}
