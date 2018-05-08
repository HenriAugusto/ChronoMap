/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import com.sun.javafx.tk.Toolkit;
import java.util.Map;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Class with static methods to display a stage where you can toggle conditions and add/remove them;
 * @author Henri
 */
public class ConditionsWindowStageManager{
    static Stage stage = null;
    static Accordion mainAccordion = null;
    static VBox vbox = null;
    static ScrollPane rootScrollPane;
    private static Button selectAllBtn, unselectAllBtn, addTopLevelConditionBtn;
            
    static void showWindow(){
        if(stage==null){
            createWindow();
        } else {
            updateTitledPanes(); //update
        }
        stage.show();
    }
        
    static void createWindow(){
        Dbg.println("Creating ConditionsWindow Stage", Dbg.ANSI_WHITE_BACKGROUND+Dbg.ANSI_BLUE);
        //Create Stage
        stage = new Stage() ; //why i did it like this?
        stage.setMinHeight(400);
        stage.setMinWidth(400);
        stage.setTitle("Condition");
        
        //Create basic Layout
        vbox = new VBox();
            vbox.setPadding( new Insets(15) );
        rootScrollPane = new ScrollPane(vbox); //root
        Label conditionStageLabel = new Label("Choose conditions:");
            conditionStageLabel.setFont(new Font(20));
            
        //The main Accordion
        mainAccordion = new Accordion();
        
        HBox buttonsHBox = new HBox();
        //Add Buttons
        selectAllBtn = new Button("select All top level");
        unselectAllBtn = new Button("unselect All top level");
        addTopLevelConditionBtn = new Button("Add top-level condition");
        Button testBtn = new Button("test");
        
                    //buttonsHBox.getChildren().addAll( testBtn );
                    testBtn.setOnAction((event) -> {
                        Dbg.println("======Testing conditions=======", Dbg.ANSI_CYAN);
                        Dbg.println("TimelineFXApp.app.timeline.conditions.size() = " + TimelineFXApp.app.timeline.conditions.size(), Dbg.ANSI_CYAN);
                        for (Condition condition : TimelineFXApp.app.timeline.conditions) {
                            Dbg.println("Condition = " + condition, Dbg.ANSI_CYAN);
                        }
                        for (Map.Entry<String, Boolean> en : ConditionHandler.conditionsMap.entrySet()) {
                            String key = en.getKey();
                            Boolean value = en.getValue();
                            Dbg.println("(Entry, Value) = (" + key+", "+value+")", Dbg.ANSI_CYAN);
                        }
                    });
                    
        //Add everything to main VBox
        buttonsHBox.getChildren().addAll( selectAllBtn, unselectAllBtn, addTopLevelConditionBtn);

        buttonsHBox.setPadding( new Insets(30) );
        
        vbox.getChildren().addAll( conditionStageLabel, buttonsHBox, mainAccordion);

        
        //Creates the scene
        Scene scene = new Scene(rootScrollPane);
        updateTitledPanes();
        
        setEventHandlersOnMainStage();
        
        stage.setScene(scene);
    }
    
            
    
    static void addHandlerOnCheckBox(CheckBox cb, String name){
        cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
            ConditionHandler.conditionsMap.put(name, newValue);
            TimelineFXApp.app.timeline.checkConditions();
            TimelineFXApp.app.draw();
        });
    }

    /**
     * Problem: we must update the selected property of each checkbox (just setting the conditions updates the conditions map but not the checkboxes). 
     * so this method, knowing how the structure of the results form getConditionTitledPane(), reads the content for the given accordion and updates the checkboxes.
     * @param acc Accordion containing the TitledPanes
     * @param b new boolean value
     */
    static void updateAllCheckBoxesOnConditionsAccordion(Accordion acc, boolean b){
        //BE CAUTIOUS WITH THOSE CASTS!
        //THEY DEPEND ON THE TYPES AND CREATION ORDER ON THE getConditionTitledPane() method!
        for (TitledPane titledPane : acc.getPanes()) {
            HBox graphic = (HBox) titledPane.getGraphic();
            CheckBox cb = (CheckBox) graphic.getChildren().get(0);
            cb.setSelected(b);   
        }
    }
    
    /**
     * For each top-level condition gets a ConditionTitledPane pane for it 
     * (The class ConditionTitledPane uses recursion to add panes for each sub condition)
     */
    static void updateTitledPanes(){
        if(stage==null){
            createWindow(); //here because of Import.loadConditions
        }
        mainAccordion.getPanes().clear();
        for (Condition c : TimelineFXApp.app.timeline.conditions) {
            //TitledPane tp = getConditionTitledPane(c);
            TitledPane tp = new ConditionTitledPane(c,null);
                //tp = new TitledPane(c.getName(), new Label("HAUHAEUHUEHAUEHAUHEUAHEUAHEAUHEAUHEAUHEAUHEAUHUAHE---AUEHAUEHAUEHAUHEAUHEUHAEUAHEUAHEUAHEUHAEUHUAEH") );
            mainAccordion.getPanes().add(tp);
        }
    }
    
    static void setEventHandlersOnMainStage(){
        selectAllBtn.setOnAction((event) -> {
            updateAllCheckBoxesOnConditionsAccordion(mainAccordion, true);
            TimelineFXApp.app.draw();
        });
        unselectAllBtn.setOnAction((event) -> {
            updateAllCheckBoxesOnConditionsAccordion(mainAccordion, false);
            TimelineFXApp.app.draw();
        });
        addTopLevelConditionBtn.setOnAction((event) -> {
            CommandHandler.executeCommand( new CmdAddCondition(null,null) );
        });
    }
    
    /**
     * Provides a WritableImage that is a snapshot from this GUI. Useful for
     * displaying it in the HelpPages
     *
     * @return
     */
    static public WritableImage getGuiSnapshot() {
        WritableImage image = null;
        Dbg.println("LET'S WRITE THE SNAPSHOT FOR THE CONDITIONS WINDOW", Dbg.ANSI_PURPLE);
        Condition c = null;
        boolean faking = false;
        if (TimelineFXApp.app.timeline.conditions.isEmpty()) {
            faking = true;
            c = new Condition("Composers");
            c.subs.add(new Condition("ComposersClassical"));
            c.subs.add(new Condition("ComposersRomantic"));
            c.subs.add(new Condition("ComposersLateRomantic"));
            c.subs.add(new Condition("Composers20thCentury"));
            TimelineFXApp.app.timeline.conditions.add(c);
            //CommandHandler.executeCommand(  new CmdAddCondition(c)  );
        }
        boolean hadToOpenTheStageBecauseOfEvilBug = false;
        int prevX = 0;
        int prevY = 0;
        //===========================================================================|
        //                                                                           |
        //DUE TO SOME EVIL BUG WE HAVE TO TAKE THE SNAPSHOT TWICE. DON'T ASKY ME WHY |    
        //                                                                           |
        //===========================================================================|
        
        for(int i=0;i<=1;++i){
            if (stage == null) {
                createWindow();
            } else {
                updateTitledPanes();
            }
            if(faking && !mainAccordion.getPanes().isEmpty()){ //why i have check if the panes are empty?
                mainAccordion.setExpandedPane(  mainAccordion.getPanes().get(0)  ); 
            }
            //https://stackoverflow.com/questions/30983584/how-to-get-the-size-of-a-label-before-it-is-laid-out
            //stage.getScene().getRoot().applyCss();
            //stage.getScene().getRoot().layout();
            double width = stage.getScene().getRoot().boundsInLocalProperty().getValue().getMaxX() - stage.getScene().getRoot().boundsInLocalProperty().getValue().getMinX();
            double height = stage.getScene().getRoot().boundsInLocalProperty().getValue().getHeight();
        
            //if(i==0){ //i found by trial an error that this works?
            
            //=======================================================================================================================
            //         Bug: i get a lot of CSS Warnings and the layout is wrong if i open and close the conditions window.
            //         (It Happens after closing it for the first time)
            //         After that everytime we get this page AND the conditions stage is closed it will happen
            //
            //         Solution? Open the fucking stage then close it. For that we move the window to the bottom-right of
            //         screen so it's not visible. But the taskbar might flash
            //=======================================================================================================================
            if( !stage.isShowing() ){
                prevX = (int) stage.getX();
                prevY = (int) stage.getY();
                stage.setX(Screen.getPrimary().getBounds().getWidth());
                stage.setY(Screen.getPrimary().getBounds().getHeight());
                stage.show();
                hadToOpenTheStageBecauseOfEvilBug = true;
            }
            //must be after
            rootScrollPane.applyCss();
            rootScrollPane.layout();
        
            width = rootScrollPane.boundsInLocalProperty().getValue().getMaxX() - rootScrollPane.boundsInLocalProperty().getValue().getMinX();
            height = rootScrollPane.boundsInLocalProperty().getValue().getHeight();

            debugDimensions(); //trying to solve the bug

            image = new WritableImage(
                    (int)width,
                    (int)height);
            stage.getScene().snapshot(image);
            
        }
        if(faking){
                c.unregister();
                TimelineFXApp.app.timeline.conditions.remove(c);
                updateTitledPanes();
            }
        //after the second snapshot, return
        if(hadToOpenTheStageBecauseOfEvilBug){
            stage.close();
            stage.setX(prevX);
            stage.setY(prevY);
        }
        return image;
    }

    private static void debugDimensions() {
        System.out.println("=============================================================");
        System.out.println("====Access scene directly====");
        System.out.println(stage.getScene().getWidth());
        System.out.println(stage.getScene().getHeight());
        System.out.println("====Access root directly====");
        System.out.println(+rootScrollPane.getWidth());
        System.out.println(rootScrollPane.getHeight());
        System.out.println("====layoutBoundsProperty [Indirect]====");
        System.out.println(stage.getScene().getRoot().layoutBoundsProperty().getValue().getWidth());
        System.out.println(+stage.getScene().getRoot().layoutBoundsProperty().getValue().getHeight());
        System.out.println("====layoutBoundsProperty [Direct]====");
        System.out.println(rootScrollPane.layoutBoundsProperty().getValue().getWidth());
        System.out.println(rootScrollPane.layoutBoundsProperty().getValue().getHeight());
        System.out.println("====BoundsInLocalProperty [Indirect]====");
        System.out.println((stage.getScene().getRoot().boundsInLocalProperty().getValue().getMaxX() - stage.getScene().getRoot().boundsInLocalProperty().getValue().getMinX()));
        System.out.println(stage.getScene().getRoot().boundsInLocalProperty().getValue().getHeight());
        System.out.println("====BoundsInLocalProperty [Direct]====");
        System.out.println((rootScrollPane.boundsInLocalProperty().getValue().getMaxX() - rootScrollPane.boundsInLocalProperty().getValue().getMinX()));
        System.out.println(rootScrollPane.boundsInLocalProperty().getValue().getHeight());
        System.out.println("====BoundsInParentProperty [Indirect]====");
        System.out.println((stage.getScene().getRoot().boundsInParentProperty().getValue().getMaxX() - stage.getScene().getRoot().boundsInParentProperty().getValue().getMinX()));
        System.out.println(stage.getScene().getRoot().boundsInParentProperty().getValue().getHeight());
        System.out.println("====BoundsInParentProperty [Direct]====");
        System.out.println((rootScrollPane.boundsInParentProperty().getValue().getMaxX() - rootScrollPane.boundsInParentProperty().getValue().getMinX()));
        System.out.println(rootScrollPane.boundsInParentProperty().getValue().getHeight());
        //width = stage.getScene().getRoot().boundsInLocalProperty().getValue().getMaxX() - stage.getScene().getRoot().boundsInLocalProperty().getValue().getMinX();
        //height = stage.getScene().getRoot().boundsInLocalProperty().getValue().getHeight();
    }
    
    //==========================SEE==============
    //https://stackoverflow.com/questions/17771190/javafx-2-titledpane-graphics-expansion-to-full-size
    //==========================SEE THIS OTHER TOO==============          
    //https://stackoverflow.com/questions/27100556/javafx-8-add-a-graphic-to-a-titledpane-on-the-right-side
    /**
     * This represents a titled pane that contains <i>(and is aware of it's)</i> specific structure intended to read and display a condition 
     * from the timeline and gives the user controls to toggle it's boolean value and add/remove conditions.
     */
    public static class ConditionTitledPane extends TitledPane {
        ConditionTitledPane parentTitledPane;
        CheckBox checkBox;
        Button addConditionBtn, removeConditionBtn;
        Condition c;
        Accordion accordion;

        /**
         * Specify a <b>null</b> parent if it is a top-level condition
        * @param conditionToBeUsed the condition object that it represents
        * @param parent the parent ConditionTitled object that contains is (use null if it is top-level)
        */
        public ConditionTitledPane(Condition conditionToBeUsed, ConditionTitledPane parent) {
            super();
            //Basic initialization
            this.parentTitledPane = parent;
            c = conditionToBeUsed;
            setPrefWidth(400);
            setText(c.getName());
            
            //HBox containing checkBox and AddConditionBtn
            HBox hBox = new HBox();
            checkBox = new CheckBox();
                checkBox.setSelected(ConditionHandler.conditionsMap.get(c.getName()));
                addHandlerOnCheckBox(checkBox, c.getName());
            addConditionBtn = new Button("+");
            removeConditionBtn = new Button("-");
            hBox.getChildren().addAll(checkBox, addConditionBtn, removeConditionBtn);
            setGraphic(hBox);
            
            //Position button to the right
            //Platform.runLater(() -> {
                    //setAddConditionButtonPos();
                //});
            setContentDisplay(ContentDisplay.RIGHT); //so content is displayed to the RIGHT of the label
            setEventHandlers();
            
            //If doesn't have children
            if (c.subs.isEmpty()) {   
                setCollapsible(false);
                return;
            }
            addSubConditions();
        }
        
        /**
        * If the pane's condition have subConditions:
        * 
        * <p>create an accordion and add
        * a ConditionTitledPane on that accordion for each one of it's children</p>
        */
        final void addSubConditions(){
            
            if(accordion==null){
                accordion = new Accordion();
                setContent(accordion);
            } else {
                accordion.getPanes().clear();
            }
            //add the subconditions            //more recursion, baby
            for (Condition sub : c.subs) {
                ConditionTitledPane subPane = new ConditionTitledPane(sub,this);
                subPane.setPadding(new Insets(5, 5, 5, 15));
                boolean b = accordion.getPanes().add(subPane);
                Dbg.println("Adding panes of condition ["+c.getName()+"]   ->  "+sub.getName()+"\n      success? "+b, Dbg.ANSI_GREEN);
            }         
            System.out.println("addSubConditions on condition "+c.getName()+"      --->c.subs.size() = "+c.subs.size());
            System.out.println("addSubConditions getPanes size "+c.getName()+"     --->mainAccordion.getPanes().size() = "+mainAccordion.getPanes().size());
            //setCollapsible( !c.subs.isEmpty() );
            setCollapsible(true);
            setExpanded(true);
            
        }

        @Deprecated
        void setAddConditionButtonPos() {
                Dbg.println("out.getLayoutBounds().getWidth(): " + getLayoutBounds().getWidth(), Dbg.ANSI_PURPLE);
                Dbg.println("out.getWidth(): " + getWidth(), Dbg.ANSI_PURPLE);
            double labelSize = Toolkit.getToolkit().getFontLoader().computeStringWidth(c.getName(), Font.getDefault());
                Dbg.println("label size: " + labelSize);
            double graphicTextGap = getGraphicTextGap() * 2;
                Dbg.println("graphicTextGap: " + graphicTextGap);
            double btnW = addConditionBtn.getWidth();
                Dbg.println("btnW: " + btnW);
            double labelPadding = getLabelPadding().getLeft() + getLabelPadding().getRight();
                Dbg.println("labelPadding: " + labelPadding);
            double panePadding = getPadding().getLeft();
                Dbg.println("panePadding: " + panePadding);
            double checkBoxWidth = checkBox.getWidth();
                Dbg.println("bheckBox width: " + checkBoxWidth);
            double arrowWidth = 0;
            if(!c.subs.isEmpty()){
                arrowWidth = lookup(".arrow")==null? 0 : lookup(".arrow").getLayoutBounds().getWidth();
            }
            double newX = getWidth() - btnW * 2 - labelSize - graphicTextGap - labelPadding - panePadding - checkBoxWidth - arrowWidth;
                Dbg.println("newX: " + newX);
            addConditionBtn.setTranslateX(newX);
        }

        private void setEventHandlers() {
            //ADD Btn Handler
            addConditionBtn.setOnAction((event) -> {
                CommandHandler.executeCommand(
                        new CmdAddCondition(c, this)
                );
                /*
                String name = GetStringStage.getStringFromUser("Enter new condition name:");
                GUIMessages.displayMessage(TimelineFXApp.app.root, "Adding condition "+name);
                Condition newCondition = new Condition(name, true);
                c.subs.add( newCondition );
                addSubConditions();
                */
            });
            //REMOVE Btn Handler
            removeConditionBtn.setOnAction((event) -> {
                //GUIMessages.displayMessage(TimelineFXApp.app.root, "Clicked the remove condition button on  "+c.getName()+" from the removeConditionBtn");
                CommandHandler.executeCommand( new CmdRemoveCondition(c, this) );                
            });
        }
    }
    
    
    
}
