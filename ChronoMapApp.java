/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
 //WHY STATIC?????????????
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * This is the main app Class
 * @author Henri Augusto
 */
public class ChronoMapApp extends Application {
    static ChronoMapApp app;
    Canvas cnv;
    Scene scene;
    Timeline timeline;
    //ScrollPane scrollPane;
    StackPane stackPane; //Group root children
    Browser browser;
    Stage mainWindow;
    ChangeListenerHandle resizeWListenerHandle, resizeHListenerHandle, resizeMaximizedListenerHandle;
    Group root;
            
    @Override
    public void start(Stage primaryStage) {
        app = this;
        mainWindow = primaryStage;
            //primaryStage.initStyle(StageStyle.UNDECORATED);
        System.out.println("mainWindow dimensions = ("+mainWindow.getWidth()+", "+mainWindow.getHeight()+")");
        
        
        
        
        root = new Group();  //NOT resized automatically when stage changes
            
            
        //scene = new Scene(root,1500,700);
        
        //StackPane root = new StackPane(); //needs line 74 root.getChildren().add(cnv);
        //GridPane root = new GridPane();
        //BorderPane root = new BorderPane();
        
        scene = new Scene(root);
        Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
                
        cnv = new Canvas(bounds.getWidth()-50,bounds.getHeight()-300);
        
            //VARIABLE LINES DEPENDING ON WHICH ROOT U USE
            //Group root NEEDS THOSE LINES
            stackPane = new StackPane(cnv); 
            root.getChildren().add(stackPane);
            root.getChildren().add(cnv);
        timeline = new Timeline(cnv);
        
        //scrollPane = new ScrollPane(cnv); //was cnv//            scrollPane.setHvalue(0.5);//            scrollPane.setVvalue(0.5);
        
                    
        
        ScrollBar scrollBarH = new ScrollBar();
        ScrollBar scrollBarV = new ScrollBar();
                  scrollBarV.setOrientation(Orientation.VERTICAL);
            //scrollBarH.setLayoutY(scene.getHeight()-scrollBarH.getHeight());
            //scrollBarH.setLayoutY(scene.getHeight()-scrollBarH.getHeight());
            //scrollBarV.setMaxHeight(0);
            //scrollBarH.setMaxHeight(20); //sampled value //era 20
            //minimun sizes
            scrollBarH.setMinHeight(25);
            scrollBarV.setMinWidth(25);
            //Layout properties
                    scrollBarH.setLayoutY(scene.getHeight()-scrollBarH.getMinHeight());
                    scrollBarV.setLayoutX(scene.getWidth()-scrollBarV.getMinWidth());
                    //scrollBarH.setLayoutY(primaryStage.getHeight()-scrollBarH.getMinHeight());
                    //scrollBarV.setLayoutX(primaryStage.getWidth()-scrollBarV.getMinWidth());
            //pref width and height
            scrollBarH.setPrefWidth(scene.getWidth()-scrollBarV.getMinWidth());
            scrollBarV.setPrefHeight(scene.getHeight()-scrollBarH.getMinHeight());
            //visible amounts
            scrollBarH.setVisibleAmount(33);
            scrollBarV.setVisibleAmount(33);
        timeline.gview.setScrollBars(scrollBarH, scrollBarV);
        scrollBarH.setValue(50);
        scrollBarV.setValue(50);
        
                            
            
        
        root.getChildren().addAll(scrollBarH,scrollBarV);
        
        
        
        primaryStage.setTitle("ChronoMap");
        primaryStage.setScene(scene);
        try{
            //primaryStage.getIcons().add( new Image("Icons/Icon.png"));
            Image applicationIcon = new Image(getClass().getResourceAsStream("Icons/Icon.png"));
            primaryStage.getIcons().add(applicationIcon);
        } catch(Exception e){
            System.err.println("Could not load Icon!");
            Dbg.println(e.toString(), Dbg.ANSI_YELLOW);
        }
        primaryStage.show();
        
        //After the primary stage so it starts with the correct size
        browser = new Browser("http://www.google.com",root.getChildren());
        browser.setVisible(false);
        
        repositionScrollBars();
        //I use it mainly for debugging
        AnimationTimer drawTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                app.draw();
            }
        };
        //drawTimer.start();
        setEventHandlers();
                //primaryStage.setMaximized(true);
        timeline.gview.centerOnPoint(0,0);
        //startFrameRateCounter();
        root.getChildren().add(frameRatelabel);
        GUIMessages.displayMessage("Press F1 for help",10000);
        app.draw();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    void draw(){
        GraphicsContext gc = cnv.getGraphicsContext2D();
        gc.save();
            gc.setTransform(new Affine());
            gc.clearRect(0, 0, cnv.getWidth(), cnv.getHeight());
        gc.restore();
        //timeline.gview.transformGraphicsContext(gc);
        
        
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.strokeLine(timeline.minYear,0,timeline.maxYear,0); //X AXIS
        gc.strokeLine(0, timeline.height,0,-timeline.height); //Y AXIS
        // 3) draw the rest
        timeline.draw(gc);
    }
    
    void setEventHandlers(){
        //Keyboard and Mouse Handlers
        scene.setOnKeyPressed(KeyboardHandler::keyPressed);
        scene.setOnMousePressed(MouseHandler::mousePressed);
            scene.setOnMouseDragged(MouseHandler::mouseDragged);
        scene.setOnMouseReleased(MouseHandler::mouseReleased);
        
        //RESIZE HANDLERS
        //DIDN'T SPECIFIED THE TEMPLATE BECAUSE IT WOULD BE <dOUBLE> FOR THE WIDTH AND HEIGHT PROPERTIES BUT <Boolean> for the maximized
        ChangeListener resizeChangeListener = new ChangeListener(){
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                repositionScrollBars();
                browser.webView.setPrefHeight(scene.getHeight()-50);
                draw();
                
            }
        };
        //resizeWListenerHandle = new ChangeListenerHandle(mainWindow.widthProperty(), resizeChangeListener);
                    resizeWListenerHandle = new ChangeListenerHandle(scene.widthProperty(), resizeChangeListener); //DA FRACKING SOLUTION
            resizeWListenerHandle.attachListener();
        //resizeHListenerHandle = new ChangeListenerHandle(mainWindow.heightProperty(), resizeChangeListener);
                    resizeHListenerHandle = new ChangeListenerHandle(scene.heightProperty(), resizeChangeListener); //DA FRACKING SOLUTION
            resizeHListenerHandle.attachListener();
        //resizeMaximizedListenerHandle = new ChangeListenerHandle(mainWindow.maximizedProperty(), resizeChangeListener);
            //resizeMaximizedListenerHandle.attachListener();
    }
    
    void repositionScrollBars(){
        
        
        Insets insets = stageInsets(mainWindow, scene);     
                                                            
        cnv.setWidth(mainWindow.getWidth());
        cnv.setHeight(mainWindow.getHeight());
        ScrollBar scrollBarH = timeline.gview.scrollBarH;
        ScrollBar scrollBarV = timeline.gview.scrollBarV;                 //REAL SOLUTION ON LINE 188!!!!!!!!!!!!!!!!!!!!!
        double w = scene.getWidth();
        double h = scene.getHeight();
            w = mainWindow.getWidth()-insets.getRight();                
            h = mainWindow.getHeight()-insets.getBottom();
        scrollBarH.setLayoutY(h - scrollBarH.getMinHeight()-insets.getTop() );
            //scrollBarH.setTranslateY(h - scrollBarH.getMinHeight()-insets.getTop() );
        scrollBarV.setLayoutX(w - scrollBarV.getMinWidth()-insets.getLeft() );   //DUPLICATE CODE
            //scrollBarV.setTranslateX(w - scrollBarV.getMinWidth()-insets.getLeft() );   //DUPLICATE CODE
        scrollBarH.setPrefWidth(w - scrollBarV.getMinWidth()-insets.getLeft());
        scrollBarV.setPrefHeight(h - scrollBarH.getMinHeight()-insets.getTop());
        
        
    }

    
    private final long[] frameTimes = new long[10];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false ;
    Label frameRatelabel = new Label();
    
    //HOW DOES THAT WORK? I MEAN, I UNDERSTAND THE MATH
    //BUT HOW IT IS AWARE OF THE DRAW METHOD ON MY ANIMATION TIMER?
    
    void startFrameRateCounter(){
        AnimationTimer frameRateMeter = new AnimationTimer() {

            @Override
            public void handle(long now) {
                long oldFrameTime = frameTimes[frameTimeIndex] ;
                frameTimes[frameTimeIndex] = now ;
                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
                if (frameTimeIndex == 0) {
                    arrayFilled = true ;
                }
                if (arrayFilled) {
                    long elapsedNanos = now - oldFrameTime ;
                    long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
                    double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
                    frameRatelabel.setText(String.format("Current frame rate: %.3f", frameRate));
                }
            }
        };
        frameRateMeter.start();
    }
    
    //https://stackoverflow.com/questions/26711474/javafx-8-stage-insets-window-decoration-thickness
    //varies when decreasing stage size??
    Insets stageInsets(Stage stage, Scene scene){
        double top = scene.getY();
        double left = scene.getX();
        double right = stage.getWidth()-scene.getWidth()-scene.getX();
        double bottom = stage.getHeight()-scene.getHeight()-scene.getY();
        Insets out = new Insets(top, right, bottom, left);
        return out;
    }


    
}

