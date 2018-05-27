/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Class with static methods for displaying messages to the user
 * @author Henri
 */
public class GUIMessages {
    static List<Label> msgList = new ArrayList<>();
    static List<String> log = new ArrayList<>();
    static double defaultDuration = 10000;
    static private Stage logStage;
    
    static void displayMessage(String msg, double duration) {
        displayMessage(ChronoMapApp.app.root, msg, duration);
    }
    
    static void displayMessage(String msg) {
        displayMessage(ChronoMapApp.app.root, msg, defaultDuration);
    }
    
    static void displayMessage(Group root, String msg){
        displayMessage(root, msg, defaultDuration);
    }
    
    /**
     * This is a method used to display messages to the user. By choosing the right <i>root</i> you can control in which window you're displaying the message.
     * @param root the parent where to add the message. Usually the root of the scene.
     * @param msg the message to display
     * @param duration how long the msg will be displayed <b>(in ms)</b>
     */
    static void displayMessage(Group root, String msg, double duration){
        log.add(msg);
        messagesUp(root);
        AnchorPane anchorPane = new AnchorPane();
        Label msgLabel = new Label(msg);
            msgLabel.setFont(  new Font(20) );
            msgList.add(msgLabel);
                    //https://stackoverflow.com/questions/21074024/how-to-get-label-getwidth-in-javafx
                    FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
                    double labelWidth = fontLoader.computeStringWidth(msgLabel.getText(), msgLabel.getFont()) ;
            msgLabel.setTextFill( Color.WHITE );
            msgLabel.setBackground( new Background( new BackgroundFill( Color.DIMGRAY.darker(), new CornerRadii(7), new Insets(-5) ) ) );
            
        anchorPane.getChildren().add(msgLabel);
            AnchorPane.setTopAnchor(msgLabel, root.getScene().getHeight()-100);
            AnchorPane.setLeftAnchor(msgLabel, root.getScene().getWidth()/2-labelWidth/2);
            
            
        root.getChildren().add( anchorPane );
        javafx.animation.Timeline tl = new Timeline();
        ObservableList<KeyFrame> kf = tl.getKeyFrames();
        kf.addAll( 
                new KeyFrame( new Duration(0), new KeyValue(msgLabel.opacityProperty(), 1)),
                new KeyFrame( new Duration(duration), new KeyValue(msgLabel.opacityProperty(), 0))
        );
        tl.setOnFinished((event) -> {
            root.getChildren().remove(anchorPane);
            msgList.remove(msgLabel);
            //Dbg.println("removing msgLabel: "+msgLabel.getText());
        });
        
        //tl.setCycleCount(2);
        //tl.setAutoReverse(true);
        tl.play();
        setLogStage();
    } 

    private static void messagesUp(Group root) {
        for (Label label : msgList) {
            double current = AnchorPane.getTopAnchor(label);
            AnchorPane.setTopAnchor(label, current-50);
            
        }
    }
    
    public static void displayLogStage(){
        setLogStage();
        logStage.show();
    }

    
    static void setLogStage(){
        if(logStage==null){
            logStage = new Stage();
        }
        Label label = new Label("Message log:");
        label.setFont(new Font(20));

        ScrollPane scrollPane = new ScrollPane();
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(35));
        vbox.setSpacing(15);
        vbox.getChildren().add(label);
        scrollPane.setContent(vbox);

        for (String string : log) {
            Text t = new Text(string);
            t.setFont(new Font(15));
            vbox.getChildren().add(t);
        }

        Scene scene = new Scene(scrollPane);
        logStage.setScene(scene);
    }
}
