/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

/**
 * Class with static utility methods to get the help pages for the help Stage
 * @author Henri Augusto
 */
public class HelpPages {
    static int currentPageNumber = 0;
    /**
     * Considering there is only 4 methods who are not intended to get a page (as those returned by getPage(i) 
     * this variable provides a dynamic, smart way to keep track of the number of pages everytime you add a new one
     * without having to change it automatically.
     * 
     * The 4 methods are: <code>firstPage(), getPage(), nextPage() and previousPage() </code>
     */
    static final int NUMBER_OF_PAGES = HelpPages.class.getDeclaredMethods().length-4; //there are only 4 methods that doesn't relate to getting a page
    
    public static Parent firstPage(){
        currentPageNumber = 0;
        return getPage(0);
    }
    
    public static Parent nextPage(){
        ++currentPageNumber;
        currentPageNumber = currentPageNumber > NUMBER_OF_PAGES-1 ? NUMBER_OF_PAGES-1 : currentPageNumber;
        return getPage(currentPageNumber);
    }
    
    public static Parent previousPage() {
        --currentPageNumber;
        currentPageNumber = currentPageNumber < 0 ? 0 : currentPageNumber;
        return getPage(currentPageNumber);
    }
    
    public static Parent getPage(int i){
        Parent parent;
        int compare = 0;
        if( i==compare++ ){
                parent = commandListPage();
        } else if ( i==compare++ ) {
            parent = eventsBasicsPage();
        } else if ( i==compare++ ) {
            parent = addingEventsPage();
        } else if ( i==compare++ ) {
            parent = editingEventsPage();
        } else if ( i==compare++ ) {
            parent = searchingEventsPage();
        } else if ( i==compare++ ) {
            parent = conditionsPage();
        } else if ( i==compare++ ) {
            parent = conditionExpressionsPage();
        } else if ( i==compare++ ) {
            parent = saveLoadTimelinePage();
        } else if ( i==compare++ ) {
            parent = linksAndBrowserPage();
        } else{
            return getPage(NUMBER_OF_PAGES-1);
        }
        return parent;
    }
    
    private static Parent commandListPage(){
        
        class HelpGridPane extends GridPane {
            public int currentRow = 0;
            public GridPane addLine(String hotkey, String description) {
                add(new Text(hotkey), 0, currentRow);
                add(new Text(description), 1, currentRow);
                ++currentRow;
                return this;
            }
        }

        HelpGridPane helpGridPane = new HelpGridPane();
            helpGridPane.setHgap(10);
            helpGridPane.setVgap(10);
        helpGridPane.addLine("F1", "show this help window");
        helpGridPane.addLine("F5", "Show hide browser");
        helpGridPane.addLine("F6", "Change conditions");
        helpGridPane.addLine("Ctrl + L", "Load an xml file");
        helpGridPane.addLine("Ctrl + S", "Save an xml file");
        helpGridPane.addLine("Ctrl + N", "Create an event");
        helpGridPane.addLine("Ctrl + E", "Edit an event");
        helpGridPane.addLine("Ctrl + F", "Search events");
        helpGridPane.addLine("Shift + L", "Go to next link on the selected event");
        helpGridPane.addLine("Ctrl + H", "Toggle fullscreen");
        helpGridPane.addLine("Ctrl + Z", "Undo");
        helpGridPane.addLine("Ctrl + Y", "Redo");
        helpGridPane.addLine("Ctrl+F1", "show message log");
        helpGridPane.addLine("F10/F11", "Zoom in/out");
        helpGridPane.addLine("Ctrl+F10", "Reset zoom");
        
        Label label = new Label("Hotkeys overview");
            label.setFont( new Font(20) );
        VBox vbox = new VBox();
            vbox.getChildren().addAll( label, helpGridPane);
            vbox.setSpacing( 15 );
        ScrollPane sp = new ScrollPane(vbox);
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            sp.setPannable(true);
            sp.setStyle("-fx-background-color:transparent;"); //https://stackoverflow.com/questions/12899788/javafx-hide-scrollpane-gray-border  (hides the border)
            
        return sp;
    }
    
    private static Parent eventsBasicsPage(){
        VBox parent = new VBox();
            parent.setSpacing(6);
        Label pageTitle = new Label("Events");
            pageTitle.setFont( new Font(20) );
        Text description = new Text("Each object on the timeline is an event. Events 5 basic properties:\n");
        Text eventProperties = new Text("Name\n"
                + "Start (year)\n"
                + "End (year)\n"
                + "Height\n"
                + "Condition\n");
            eventProperties.setFont( new Font(15) );
            HBox eventPropertiesHBox = new HBox(eventProperties);
                eventPropertiesHBox.setAlignment(Pos.CENTER);
                eventPropertiesHBox.setPadding( Insets.EMPTY );
        Text description2 = new Text("Events are straight segments (end>height) or circles (end=height).\n"
            + "For example you might add segments representing people and circles\n"
                + "representing one-time events");
        Canvas cnv = new Canvas(400,400);
        GraphicsContext gc = cnv.getGraphicsContext2D();
            //paint bkg
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, cnv.getWidth(), cnv.getHeight());
            //paint the axis
            double xAxisY = cnv.getHeight()/3;
            double yAxisX = cnv.getWidth()/3;
            gc.setFill(Color.BLACK);
            gc.strokeLine(0, xAxisY, cnv.getWidth(), xAxisY); //x Axis
            gc.strokeLine(yAxisX, 0, yAxisX, cnv.getHeight());  //y Axis
            for(int i=-400 ; i<=cnv.getWidth(); i += 100){
                gc.fillText(""+i, yAxisX+i+7, xAxisY-8);
            }
            //some person
            gc.fillText("Person", 200/2+272/2-30,90);
            gc.strokeLine(200, 100, 275, 100); 
            //some person's government
            gc.fillText("Person's government", 250 / 2 + 335 / 2 - 30, 260);
            gc.strokeLine(250, 270, 335, 270);
            //some war
            gc.fillText("Some war", 70 / 2 + 120 / 2 - 30, 280);
            gc.strokeLine(70, 290, 120, 290);
            //some invention
            gc.fillText("An invention", 300,190);
            gc.fillArc(300, 200, 10, 10, 0, 360, ArcType.CHORD);
            //some date of publication
            gc.fillText("Publication of some book", 195, 310);
            gc.fillArc(195, 320, 10, 10, 0, 320, ArcType.CHORD);
        cnv.setEffect( new DropShadow(7, Color.BLACK));
            parent.getChildren().addAll(pageTitle,description,eventPropertiesHBox,description2,cnv);
        return parent;
    }
    
    private static Parent addingEventsPage() {
        VBox parent = new VBox();
            parent.setSpacing(20);
        Label pageTitle = new Label("Adding");
        pageTitle.setFont(new Font(20));
        Text description = new Text("Press ctrl+n to create a new event\n"
                + "here is an screenshot of the create event window:");
        WritableImage image = CreateEventStageManager.getGuiSnapshot();
        
        ImageView imgView = new ImageView(image);
            //imgView.setEffect( new PerspectiveTransform(0, 0, 400, 0, 500, 400, 0, 500));
            imgView.setEffect( new DropShadow(20, Color.BLACK));
        Text description2 = new Text("please notice that currently the Edit Event window has more information than the create event window");
        parent.getChildren().addAll(pageTitle, description, imgView, description2);
        return parent;
    }
    
    private static Parent editingEventsPage() {
        VBox parent = new VBox();
        parent.setSpacing(20);
        Label pageTitle = new Label("Editing");
        pageTitle.setFont(new Font(20));
        Text description = new Text("Press ctrl+e to edit an event.\n"
                + "here is an screenshot of the edit event window:");
        WritableImage image = EditEventStageManager.getGuiSnapshot();

        ImageView imgView = new ImageView(image);
        imgView.setEffect(new DropShadow(20, Color.BLACK));
        Text description2 = new Text("please notice that currently the Edit Event window has more information than the create event window");
        parent.getChildren().addAll(pageTitle, description, imgView, description2);
        return parent;
    }
    
    private static Parent searchingEventsPage() {
        VBox parent = new VBox();
        parent.setSpacing(20);
        Label pageTitle = new Label("Searching");
        pageTitle.setFont(new Font(20));
        Text description = new Text("Press ctrl+F to edit an event.\n"
                + "here is an screenshot of the edit event window:");
        WritableImage image = EventSearcher.getGuiSnapshot();

        ImageView imgView = new ImageView(image);
        imgView.setEffect(new DropShadow(20, Color.BLACK));
        Text description2 = new Text("please notice that currently the Edit Event window has more information than the create event window");
        parent.getChildren().addAll(pageTitle, description, imgView, description2);
        return parent;
    }
    
    private static Parent conditionsPage() {
        VBox parent = new VBox();
            parent.setSpacing(20);
        Label pageTitle = new Label("Conditions");
        pageTitle.setFont(new Font(20));
        Text description = new Text("You can create conditions to control which events you want to display at any given time. \n"
                + "For that you can press F6. Below there is an screenshot of the window");
        
        WritableImage image = ConditionsWindowStageManager.getGuiSnapshot();
            //Due to some evil bug we have to call this method twice because the first time it's called the image dimensions
            //return are wrong!
            //image = ConditionsWindowStageManager.getGuiSnapshot(); 

        ImageView imgView = new ImageView(image);
            imgView.setEffect(new DropShadow(20, Color.BLACK));
        
        Text description2 = new Text("Notice you can nest conditions. For example: create a condition Inventions an then a sub condition MusicalInstrumentsInvention");
        parent.getChildren().addAll(pageTitle, description, imgView, description2);
        return parent;
    }
    
    private static Parent conditionExpressionsPage() {
        VBox parent = new VBox();
        parent.setSpacing(10);
        Label pageTitle = new Label("Condition Expressions");
        pageTitle.setFont(new Font(20));
        Text description = new Text(
                "When you're creating an event you will be able to define a Condition Expression that\n"
                + "will be evaluated to determine whether or not the event must be displayed.\n"
                + "The operators are && ('and' operator) and || ('or' operator)");
        Text syntax = new Text(
                "Syntax examples:\n"
                + "A && B\n"
                + "A || B\n"
                + "(A || B) && C");
                syntax.setFont( new Font(15) );
        Text realExamples = new Text(
                "The examples read, in order: A and B, A or B, (A or B) and C.\n"
                + "Please note that the && operator is evaluated first so:\n"
                + "A || B && C    is equal to    A || (B && C).\n"
                + "Some examples:");
        Text beethoven = new Text(
                 "Beethoven could have:\n"
                + "        'Composers && (ComposersClassical || ComposersRomantic)'");
        Text bertrand = new Text("Bertrand Russell could have:\n"
                + "        'Philosophers || Mathematicians'");
        beethoven.setFont(new Font(15));
        bertrand.setFont(new Font(15));
        parent.getChildren().addAll(pageTitle, description, syntax, realExamples, beethoven, bertrand);
        return parent;
    }

    private static Parent linksAndBrowserPage() {
        //https://stackoverflow.com/questions/35611176/how-can-i-resize-a-javafx-image
        class ImageScaler{
            public WritableImage scale(Image source, int targetWidth, int targetHeight, boolean preserveRatio) {
                ImageView imageView = new ImageView(source);
                imageView.setPreserveRatio(preserveRatio);
                imageView.setFitWidth(targetWidth);
                imageView.setFitHeight(targetHeight);
                return imageView.snapshot(null, null);
            }
        }
        ImageScaler imgScaler = new ImageScaler();
        VBox parent = new VBox();
        parent.setSpacing(20);
        Label pageTitle = new Label("Links and browser");
        pageTitle.setFont(new Font(20));
        Text description = new Text("Press Shift+L to open previously-defined links related to the selected event. They will open into an\n"
                + "integrated browser. If no link was defined it will just google the event name.\n\n"
                + "Press F5 to open or close the browser at any time.\nBelow is a example of how the browser window will look like");
        parent.getChildren().addAll(pageTitle, description);
        //Browser browser = new Browser("http://www.google.com", parent.getChildren());
        //browser.setPrefDimensions(500, 500);
        /*WritableImage img = new WritableImage(1000,100);
        img = imgScaler.scale(img, 250, 250, true);
        SnapshotParameters sp = new SnapshotParameters();
        TimelineFXApp.app.browser.vbox.snapshot( sp , img);*/
        
        WebView webView = new WebView();
            webView.setPrefHeight(400);
            webView.setPrefWidth(400);
            //webView.setPrefSize(2900, 1650);
            //webView.getEngine().load("https://www.youtube.com/watch?v=49oiE8Tj1UU");
            webView.getEngine().load("https://www.google.com");
        parent.getChildren().addAll( webView );
        return parent;
    }
    
    private static Parent saveLoadTimelinePage() {
        VBox parent = new VBox();
            parent.setSpacing(10);
        Label pageTitle = new Label("Saving and loading");
        pageTitle.setFont(new Font(20));
        Text description = new Text("Press ctrl+S to save your timeline and ctrl+L to load a previously saved one.\n");
        Text description2 = new Text("The save file will store the following information:\n"
                + "- All information of every event\n"
                + "- All information related to the timeline's conditions\n"
                /*+ "- Timeline's current settings"*/);
        description.setFont( new Font(14) );
        description2.setFont( new Font(14) );
        parent.getChildren().addAll(pageTitle, description, description2);
        String example = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "\n"
                + "<timeline>\n"
                + "    <conditions>\n"
                + "        <condition name=\"Composers\" value=\"true\">\n"
                + "            <condition name=\"ComposersClassical\" value=\"true\"/>\n"
                + "            <condition name=\"ComposersRomantic\" value=\"true\"/>\n"
                + "        </condition>\n"
                + "    </conditions>\n"
                + "    <event name=\"Ludwig van Beethoven\">\n"
                + "        <date start=\"1770\" end=\"1827\" ongoing=\"false\"/>\n"
                + "        <height>-100</height>\n"
                + "        <description></description>\n"
                + "        <color red=\"0.0\" green=\"0.0\" blue=\"0.0\"/>\n"
                + "        <links/>\n"
                + "        <showCondition>\n"
                + "            <AND>\n"
                + "                <SINGLE>Composers</SINGLE>\n"
                + "                <OR>\n"
                + "                    <SINGLE>ComposersClassical</SINGLE>\n"
                + "                    <SINGLE>ComposersRomantic</SINGLE>\n"
                + "                </OR>\n"
                + "            </AND>\n"
                + "        </showCondition>\n"
                + "    </event>\n"
                + "</timeline>";
        return parent;
    }
    
}
