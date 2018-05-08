/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author User
 */
public class HelpStageManager{
    static Stage stage;
    static Scene scene;
    static VBox root;
    static int currentRow = 1;
    private static Button prevPageBtn, nextPageBtn;
    private static Parent currentPage;
    private static Button firstPageBtn;
    private static Label pageLabel;
    private static ScrollPane pageScrollPane = new ScrollPane();
    private static StackPane pageStackPane = new StackPane();
    
    static void showWindow(){
        if(stage==null){
            createWindow();
        }
        stage.show();
    }
        
    static void createWindow(){
        stage = new Stage();
        root = new VBox();
            root.setPadding( new Insets(30));
            root.setSpacing(20);
            //root.setAlignment(Pos.CENTER);
        currentRow = 1;
        Text help = new Text("TimelineFX Help");
           help.setFont( new Font(20) );
        HBox btnsHBox = new HBox();
            //btnsHBox.setAlignment(Pos.CENTER);
        prevPageBtn = new Button("previous page");
        nextPageBtn = new Button("next page");
        firstPageBtn = new Button("First");
            prevPageBtn.setOnAction((event) -> {
                pageStackPane.getChildren().remove( currentPage );
                currentPage = HelpPages.previousPage();
                pageStackPane.getChildren().add( currentPage );
                    //pageScrollPane.setContent(currentPage);
                updatePageLabel();
                resize();
            });
            nextPageBtn.setOnAction((event) -> {
                pageStackPane.getChildren().remove( currentPage );
                currentPage = HelpPages.nextPage();
                pageStackPane.getChildren().add( currentPage );
                    //pageScrollPane.setContent(currentPage);
                updatePageLabel();
                resize();
            });
            firstPageBtn.setOnAction((event) -> {
                pageStackPane.getChildren().remove(currentPage);
                currentPage = HelpPages.firstPage();
                pageStackPane.getChildren().add(currentPage);
                    //pageScrollPane.setContent(currentPage);
                updatePageLabel();
                resize();
            });
        pageLabel = new Label();
            pageLabel.setFont( new Font(16) );
            HBox pageLabelHBox = new HBox(pageLabel);
            //why this doesn't work?
            //pageLabelHBox.setPadding( new Insets(0, 0, 0, 15)); 
            //pageLabelHBox.setPadding( new Insets(15));
        updatePageLabel();
        btnsHBox.getChildren().addAll( firstPageBtn, prevPageBtn, nextPageBtn, pageLabel);
        root.getChildren().addAll( help, btnsHBox);
        currentPage = HelpPages.firstPage();
        
        pageScrollPane.setContent(pageStackPane);
        
        pageStackPane.getChildren().add(currentPage);
            pageStackPane.setPadding( new Insets(9) );    
        pageScrollPane.setStyle("-fx-background-color:transparent;"); //https://stackoverflow.com/questions/12899788/javafx-hide-scrollpane-gray-border  (hides the border)
        
        root.getChildren().addAll( pageScrollPane /*currentPage*/ );
        
       

        scene = new Scene(root);
        stage.setScene(scene);
    }
    
    /**
     * Handy method to update the "Page 2/9" dialog
     */
    private static void updatePageLabel(){
        pageLabel.setText("    Page "+(HelpPages.currentPageNumber+1)+"/"+(HelpPages.NUMBER_OF_PAGES));
    }
    
    /**
     * Resizes the stage after updating the page
     */
    private static void resize(){
        stage.sizeToScene();
        Screen screen = Screen.getPrimary();
        //if the stage is bigger than a/b of the screen, resize it
        float a = 10;
        float b = 11;
        if(  stage.getX()+stage.getWidth() > a*screen.getBounds().getWidth()/b)
        {
            stage.setWidth(a*screen.getBounds().getWidth()/b-stage.getX());
        }
        if( stage.getY()+stage.getHeight() > a*screen.getBounds().getHeight()/b ){
            stage.setHeight(a*screen.getBounds().getHeight()/b-stage.getY());
        }
    }
    
   
    /*
    private static void addLine(String hotkey, String description){
        root.add( new Text(hotkey) , 0, currentRow);
        root.add( new Text(description) , 1, currentRow);
        ++currentRow;
    }
    */
    
    
}
