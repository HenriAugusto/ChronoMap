/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;


import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * This stage is used to edit selected events. If more than 1 event is selected it warns the user and stops accepting edits.
 * Challenge: how to detect when there were changes to selectedEvents in order to update the window??
 * @author Henri Augusto
 */
public class EditEventStageManager {
    static Stage inputStage = null;
    static Scene scene = null;
    static ConditionExpr cExpr = null;
    static boolean isConditionValid = false;
    static TextField nameField, startField, endField, heightField, conditionField;
    static Label conditionStatus = null;
    static ChangeListener conditionUpdateListener;
    static Event selected;
    private static Label createEventLabel;
    private static boolean onlyOneEventIsSelected = false;
    private static ScrollPane webLinksScrollPane;
    private static VBox webLinksVBox;
    private static List<WebLinkHBox> webLinkHBoxes = new ArrayList<>();
    private static final ColorPicker colorPicker = new ColorPicker(Color.BLACK);
       
    /**
     * Shows the Edit event window, initializing everything when necessary
     */
    static void showEventWindow(){
        if(inputStage==null){
            createEventWindow();
        } else {
            parseAgain();
        }
        inputStage.show();
    }
    
    /**
     * Initializes the Stage, scene and all the necessary Nodes for the edit event window. 
     * Also sets the inputHandler and the listener on the selectedEvents.
     */
    private static void createEventWindow(){
        inputStage = new Stage();
            inputStage.setTitle("Edit event");
            inputStage.setAlwaysOnTop(true);
        
        createEventLabel = new Label("temp label");
            createEventLabel.setFont(   new Font(20)   );
            
        Label nameLabel = new Label("Event name");
            nameField = new TextField();
        Label startLabel = new Label("start date");
            startField = new TextField();
        Label endLabel = new Label("end date");
            endField = new TextField();
        Label heightLabel = new Label("event height");
            heightField = new TextField();
        Label conditionLabel = new Label("Condition for exhibition");
            conditionField = new TextField("");
                conditionField.setMinWidth(400);
                conditionStatus = new Label("Condition status");
                conditionStatus.setMinHeight(100);
                conditionStatus.setFont(new Font(18));

        VBox vbox = new VBox(
                createEventLabel, 
                nameLabel, nameField, 
                startLabel, startField, 
                endLabel, endField,
                heightLabel, heightField
        );
        
        HBox colorHBox = new HBox();
        colorHBox.setPadding( new Insets(10) ) ;
        Label colorLabel = new Label("Color:");
            colorLabel.setFont( new Font(15) );
        colorHBox.getChildren().addAll( colorLabel, colorPicker );
        colorHBox.setSpacing(10);
        
        createWebLinkNodes();
        
        scene = new Scene(vbox);
        inputStage.setScene(scene);

        vbox.getChildren().addAll(conditionLabel, conditionField, conditionStatus, colorHBox, webLinksScrollPane);
        //Enter to confirm stuff
        Label enterToConfirm = new Label("Press Enter to confirm.");
        enterToConfirm.setFont(  new Font(17) );
        VBox enterToConfirmVBox = new VBox();
        enterToConfirmVBox.getChildren().add(enterToConfirm);
        enterToConfirmVBox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll( enterToConfirmVBox );

        setHandlers();
        //inputStage.show();
        
        updateFields();
            setListeners();//THIS MUST GO AFTER updateFields
    }
    
    static void createWebLinkNodes(){
        //initialize VBox to hold the WebLinkHBox objects
        webLinksVBox = new VBox();
        webLinksVBox.setMinHeight(100);
        //VBox to hold the (fixed) label and buttons
        VBox webLinksLabelAndButtons = new VBox();
            Button addLinkButton = new Button("Add link");
            addLinkButton.setOnAction((event) -> {
                if(onlyOneEventIsSelected){
                    WebLink link = new WebLink(WebLink.WebLinkType.OTHER_OPEN_BROWSER,"enter url","enter name");
                    CommandHandler.executeCommand( new CmdAddWebLink(selected, link) );
                    addWebLinksHBoxes();
                }
            });
            Label webLinksLabel = new Label("Web links:");
            webLinksLabel.setFont(new Font(17));
            webLinksLabelAndButtons.getChildren().addAll(webLinksLabel, addLinkButton);
            
        
        //VBox to hold both the above
        VBox labelsButtonsAndWebLinkEditFields = new VBox();
        labelsButtonsAndWebLinkEditFields.setSpacing(15);
        labelsButtonsAndWebLinkEditFields.getChildren().addAll(webLinksLabelAndButtons, webLinksVBox);
        
        //The Scroll pane to display all this
        webLinksScrollPane = new ScrollPane(labelsButtonsAndWebLinkEditFields);
        
        
    }
    
    static void setHandlers(){       
       scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
           @Override
           public void handle(KeyEvent e) {
               System.out.println("Receiving key on EditEventStageManager: "+e.getCode().toString());
               if (e.getCode().toString().equals("ENTER") && isConditionValid && onlyOneEventIsSelected) {
                   //==========================================================================
                   //THIS IS THE FIX FOR THE COLORPICKER BUG         bug = first the stage is closed THEN the enter is going to the ColorPicker
                   //I SHOULD TOTALLY REPORT THIS BUG                who i know it? because if you don't close the stage on enter 
                   e.consume();                              //      (so enter opens the color picker) and you close the stage clicking on the [X] it works normally
                   //this should be a command!
                    System.out.println("WHAT DA FUCK");
                    String name = nameField.getText();
                    int start = Integer.parseInt(startField.getText());
                    int end = Integer.parseInt(endField.getText());
                    int height = Integer.parseInt(heightField.getText());
                    List<CmdEditEvent.WebLinkEditInfo> linkEditInfos = new ArrayList<>();
                    
                    for (WebLinkHBox linkHBox : webLinkHBoxes) {
                       linkEditInfos.add( 
                               new CmdEditEvent.WebLinkEditInfo(linkHBox.link, linkHBox.getName(), linkHBox.getUrl(), linkHBox.getType()) 
                       );
                   }
                    Color color = colorPicker.getValue();
                    
                    CmdEditEvent cmd = new CmdEditEvent(
                            selected, name, start, end, height, color, cExpr, linkEditInfos
                        );
                    CommandHandler.executeCommand(cmd);
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
               conditionStatus.setText("Condition Expression:\n"+cExpr.toString());
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
               
           }
       };
       //NOT USING THE HANDLE
       ChangeListenerHandle clh = new ChangeListenerHandle(conditionField.textProperty(), conditionUpdateListener); 
       conditionField.textProperty().addListener( conditionUpdateListener);
   }
   
   /**
    * set the listener and re-triggers the listener immediately. See bottom note
    */
   private static void setListeners(){
       //This is the main listener that changes <i>Event selected;</i> when the user changes it's selectiong
       TimelineFXApp.app.timeline.selectedEvents.addListener(new SetChangeListener<Event>() {
           
           @Override
           public void onChanged(SetChangeListener.Change<? extends Event> change) {
                updateFields();
           }
           
       });

       
       
   }

    /**
    * this is here just for triggering the listener so if any change happened (and a condition was deleted for ex) 
    * the text will be parsed again to show the correct status
    * note that it still doesn't updates after adding removing a condition in the conditions manager window!
    */
    static void parseAgain() {
        
        String text = conditionField.getText();
        conditionField.setText(" " + text);
        conditionField.setText(text);
        
    }

    /**
     * Updates selected and all other fields
     */
    private static void updateFields() {
        if (TimelineFXApp.app.timeline.selectedEvents.size() == 1) {
            selected = TimelineFXApp.app.timeline.selectedEvents.iterator().next();
            onlyOneEventIsSelected = true;
        } else{
            onlyOneEventIsSelected = false;
        }
        if( !onlyOneEventIsSelected ){
            createEventLabel.setText( TimelineFXApp.app.timeline.selectedEvents.isEmpty() ? "No event selected" : "More than one event selected");
            nameField.setText("");
            startField.setText("");
            endField.setText("");
            heightField.setText("");
            conditionField.setText("");
            webLinksVBox.getChildren().clear();
        } else {
            createEventLabel.setText("Editing event ("+selected.getName()+")");
            nameField.setText(selected.getName());
            startField.setText(""+selected.getStart());
            endField.setText(""+selected.getEnd());
            heightField.setText(""+selected.getHeight());
            colorPicker.setValue(selected.getColor());
            conditionField.setText("" + selected.showCondition.toString());
            addWebLinksHBoxes();
        }
        parseAgain();
    }
    
    /**
     * For each link of the selected event create an matching <b>WebLinkHBox</b> and add it to <i>webLinksVBox</i>
     * (It is cleared before adding)
     */
    private static void addWebLinksHBoxes(){
        webLinksVBox.getChildren().clear();
        for (WebLink link : selected.links) {
            WebLinkHBox wlhb = new WebLinkHBox(link);
            webLinkHBoxes.add(wlhb);
            webLinksVBox.getChildren().add(   wlhb   );
        }
    }
    
    /**
     * Provides a WritableImage that is a snapshot from this GUI. Useful for
     * displaying it in the HelpPages
     *
     * @return
     */
    static public WritableImage getGuiSnapshot() {

        if (inputStage == null) {
            createEventWindow();
        }

        //https://stackoverflow.com/questions/30983584/how-to-get-the-size-of-a-label-before-it-is-laid-out
        inputStage.getScene().getRoot().applyCss();
        inputStage.getScene().getRoot().layout();
        //i've discovered that accidentally! Note it needs the above lines to work!!
        double width = (inputStage.getScene().getRoot().boundsInLocalProperty().getValue().getMaxX() - inputStage.getScene().getRoot().boundsInLocalProperty().getValue().getMinX());
        double height = inputStage.getScene().getRoot().boundsInLocalProperty().getValue().getHeight();

        System.out.println("inputStage.getScene().getWidth() = " + inputStage.getScene().getWidth());
        System.out.println("inputStage.getScene().getHeight() = " + inputStage.getScene().getHeight());
        System.out.println("wadafuq = " + (inputStage.getScene().getRoot().boundsInLocalProperty().getValue().getMaxX() - inputStage.getScene().getRoot().boundsInLocalProperty().getValue().getMinX()));
        System.out.println("wadafuq = " + inputStage.getScene().getRoot().boundsInLocalProperty().getValue().getHeight());

        //System.out.println("inputStage.getScene().getRoot().getHeight() = "+inputStage.getScene().getRoot().getHeight());
        //int w = inputStage.getScene().getWidth() > 0 ? (int) inputStage.getScene().getWidth() : 400;
        //int h = inputStage.getScene().getHeight() > 0 ? (int)  inputStage.getScene().getHeight() : 400;
        WritableImage image = new WritableImage(
                (int) width,
                (int) height);
        inputStage.getScene().snapshot(image);
        /* make image brighter
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                image.getPixelWriter().setColor(x, y,
                        image.getPixelReader().getColor(x, y).deriveColor(0, 0.97d, 1.03d, 1)
                );
            }
        }
         */
        return image;
    }
    
    static final class WebLinkHBox extends HBox{
        WebLink link;
        TextField nameField;
        TextField urlField;
        ChoiceBox<WebLink.WebLinkType> typeChoiceBox;
        Button removeButton = new Button("Delete");
        

        public WebLinkHBox(WebLink link) {
            this.link = link;
            setSpacing(10);
            
            Label nameLabel = new Label("Name");
            nameField = new TextField(link.getName());
            
            Label urlLabel = new Label("Url");
            urlField = new TextField(link.getUrl());
            
            Label typeLabel = new Label("Type");
            //this is how to use generics with static methods
            ObservableList<WebLink.WebLinkType> typeList = FXCollections.<WebLink.WebLinkType>observableArrayList();
            for (WebLink.WebLinkType value : WebLink.WebLinkType.values()) {
                typeList.add(value);
            }
            typeChoiceBox = new ChoiceBox<>( typeList ); //T is defined as "WebLink.WebLinkType" in the static field
            typeChoiceBox.<WebLink.WebLinkType>setValue(  link.getLinkType() );
            
            getChildren().addAll(
                    nameLabel, nameField,
                    urlLabel, urlField,
                    typeLabel, typeChoiceBox,
                    removeButton);
            
            setHgrow(nameField, Priority.ALWAYS);
            setHgrow(urlField, Priority.ALWAYS);
            
           setHandlers();
        }
        
        public String getName(){
            return nameField.getText();
        }
        
        public String getUrl() {
            return urlField.getText();
        }
        
        public WebLink.WebLinkType getType() {
            return typeChoiceBox.getValue();
        }

        /**
         * Set the handler for the delete button
         */
        private void setHandlers(){
            removeButton.setOnAction((event) -> {
                CommandHandler.executeCommand( new CmdRemoveWebLink(selected, link) );
                webLinkHBoxes.clear();
                addWebLinksHBoxes();
            });
        }
      
    }
    
}
