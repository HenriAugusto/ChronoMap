/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author User
 */
public class EventSearcher {
    static Stage stage;
    static GridPane grid;
    static Scene scene;
    static VBox vbox;
    static ScrollPane sp;
    static TextField searchField;
    
    static void showSearchWindow(){
        if ( stage == null){
            createSearchWindow();
        }
        stage.show();
        searchField.requestFocus();
    }
    
    static void createSearchWindow(){
        stage = new Stage();
            stage.setTitle("Search Events");
        if ( grid == null ){
             grid = new GridPane();
             sp = new ScrollPane();
                sp.setStyle("-fx-background-color:transparent;"); //https://stackoverflow.com/questions/12899788/javafx-hide-scrollpane-gray-border  (hides the border)
                sp.setContent(grid);
             //sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
             //sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
             //sp.resize(400, 400);
        }
        
            
        if( vbox==null){
             vbox = new VBox();
             Label eventSearchLabel = new Label("Search for events");
                eventSearchLabel.setFont( new Font(20)  );
             searchField = new TextField();
             vbox.getChildren().addAll(eventSearchLabel, searchField, sp);
             searchField.setOnKeyPressed((event) -> {
                if(false || event.getCode().toString().equals("ENTER") ){
                    //event.consume();
                    System.out.println("timelinefx.EventSearcher.showSearchWindow()");
                    search(searchField.getText());
                }
            });
            searchField.textProperty().addListener((observable) -> {
                search(searchField.getText());
            });
        }
        vbox.setPadding( new Insets(10) );
        if( scene == null ){
            scene = new Scene(vbox, 500, 500);
        }
        stage.setScene(scene);
    }
    
    /**
     * Searches for events and display a button for each one. When you click it centers on that event and selects it
     * Must NOT be called before createSearchWindow()
     * @param s 
     */
    static void search(String s){
        clearSearchResults();
        
        int i = 0;
        int nOfCols = 3;
        s = replaceCharsWithAccents(s);
        for (Event e : ChronoMapApp.app.timeline.events) {
            String eventNameTest = replaceCharsWithAccents(  e.name.toLowerCase()  );
            if(  eventNameTest.contains(s.toLowerCase()) ){
                //System.out.println("timelinefx.EventSearcher.search() \n"+e);
                int x = i % nOfCols;
                int y = (int)((float)i/nOfCols);
                Button btn = new Button(e.name);
                btn.setOnAction((event) -> {
                    GraphicView g = ChronoMapApp.app.timeline.gview;
                    Point2D transformed = new Point2D(e.start/2+e.end/2, e.height);
                    g.centerOnPoint(transformed.getX(), transformed.getY());
                    g.centerOnPoint(e.start/2+e.end/2, e.height);
                    
                    /*   //SÓ FUNCIONAVA QUANDO ERRA ARRAYLIST
                    //DAR A SOLUÇÃO LA NO CODE REVIEW DE DELETAR A PORRA DO ROLÊ ITERANDO PRA TRÁS QUE É SUAVIS
                    for (int j = TimelineFXApp.app.timeline.selectedEvents.size()-1; j >= 0; --j) {
                        Event selectedEvent = TimelineFXApp.app.timeline.selectedEvents.get(j);
                        selectedEvent.unselect(); 
                    }*/
                    ChronoMapApp.app.timeline.clearSelectedEvents(); //does the above
                    
                    e.select();
                    ChronoMapApp.app.timeline.updateEventsIsOnView();
                    ChronoMapApp.app.draw();
                });
                grid.add(btn, x, y);
                ++i;
            }
        }
        //stage.sizeToScene();
    }

    /**
     * Help me out on this one! I'm out of ideas here since in Portuguese we only use accents in
     * Vowels :) 
     * I can't even type some chars like accented j's as i know some languages have.
     * I could look in the char map for other chars but i would not know if they can be replaced by some non-accented chars 
     * so i will just leave it that way for the moment
     * 
     * <p><b>Example</b></p>
     * <p><code>replaceCharsWithAccents("Méliès")=Melies</code></p>
     * @param s
     * @return 
     */
    private static String replaceCharsWithAccents(String s) {
        String out;
        //A
        out = s.replace("á", "a");
        out = out.replace("à", "a");
        out = out.replace("ã", "a");
        out = out.replace("â", "a");
        out = out.replace("ä", "a");
        //E
        out = out.replace("é", "e");
        out = out.replace("è", "e");
        out = out.replace("ê", "e");
        out = out.replace("ë", "e");
        //I
        out = out.replace("í", "i");
        out = out.replace("ì", "i");
        out = out.replace("î", "i");
        out = out.replace("ï", "i");
        //O
        out = out.replace("ó", "o");
        out = out.replace("ò", "o");
        out = out.replace("ô", "o");
        out = out.replace("õ", "o");
        //U
        out = out.replace("ú", "u");
        out = out.replace("ù", "u");
        out = out.replace("û", "u");
        out = out.replace("ü", "u");
        //Ç
        out = out.replace("ç", "c");
        
        return out;
        
    }
    
    /**
     * Provides a WritableImage that is a snapshot from this GUI. Useful for
     * displaying it in the HelpPages
     *
     * @return
     */
    static public WritableImage getGuiSnapshot() {
        
        /*
        HashSet<Event> eventsCopy = new HashSet<>(TimelineFXApp.app.timeline.events);
        //Add some dummys
            TimelineFXApp.app.timeline.events.clear();
            TimelineFXApp.app.timeline.events.add( new Event("Arnold Schoenberg", 0, 0, 0));
            TimelineFXApp.app.timeline.events.add( new Event("Alban Berg", 0, 0, 0));
            TimelineFXApp.app.timeline.events.add( new Event("Ingmar Bergman", 0, 0, 0));
            TimelineFXApp.app.timeline.events.add( new Event("Werner Heisenberg", 0, 0, 0));
        */
        
        if (stage == null) {
            createSearchWindow();
        }
        
        searchField.setText("berg");
            clearSearchResults();//the timeline may actually contain something with berg
        grid.add( new Button("Arnold Schoenberg") , 0, 0);
        grid.add( new Button("Alban Berg") , 1, 0);
        grid.add( new Button("Ingmar Bergman") , 2, 0);
        grid.add( new Button("Werner Heisenberg") , 0, 1);
        
        

        //https://stackoverflow.com/questions/30983584/how-to-get-the-size-of-a-label-before-it-is-laid-out
        stage.getScene().getRoot().applyCss();
        stage.getScene().getRoot().layout();
        //i've discovered that accidentally! Note it needs the above lines to work!!
        double width = (stage.getScene().getRoot().boundsInLocalProperty().getValue().getMaxX() - stage.getScene().getRoot().boundsInLocalProperty().getValue().getMinX());
        double height = stage.getScene().getRoot().boundsInLocalProperty().getValue().getHeight();

        System.out.println("inputStage.getScene().getWidth() = " + stage.getScene().getWidth());
        System.out.println("inputStage.getScene().getHeight() = " + stage.getScene().getHeight());
        System.out.println("wadafuq = " + (stage.getScene().getRoot().boundsInLocalProperty().getValue().getMaxX() - stage.getScene().getRoot().boundsInLocalProperty().getValue().getMinX()));
        System.out.println("wadafuq = " + stage.getScene().getRoot().boundsInLocalProperty().getValue().getHeight());

        //System.out.println("inputStage.getScene().getRoot().getHeight() = "+inputStage.getScene().getRoot().getHeight());
        //int w = inputStage.getScene().getWidth() > 0 ? (int) inputStage.getScene().getWidth() : 400;
        //int h = inputStage.getScene().getHeight() > 0 ? (int)  inputStage.getScene().getHeight() : 400;
        WritableImage image = new WritableImage(
                (int) width,
                (int) height);
        stage.getScene().snapshot(image);
        
        
        clearSearchResults(); //remove the dummy buttons
            //TimelineFXApp.app.timeline.events = eventsCopy;
        return image;
    }
    
    public static void clearSearchResults(){
        grid.getChildren().clear();
    }
}
