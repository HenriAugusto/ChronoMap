/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;


/**
 * This is just a useful stage to get a String input form the user.
 * @author Henri Augusto
 */
public class GetStringStage {
    static boolean shouldThrowException = false; //can't be local because goes out of scope (memory)
    
    static String getStringFromUser(String messageToDisplay) throws UserCanceledException{
        Stage stage = new Stage();
        shouldThrowException = false;
        
        //create root
        VBox root = new VBox();
            root.setPadding( new Insets(10) );
        //Label
        Label label = new Label(messageToDisplay);
        label.setFont( new Font(20) );
        //TextField for getting user input
        TextField tf = new TextField("type here");
        root.getChildren().addAll(label, tf);
        
        //Scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        //Enter to accept
        scene.setOnKeyPressed((event) -> {
            switch(event.getCode().toString()){
                case "ENTER":
                    event.consume(); //https://stackoverflow.com/questions/47797980/javafx-keyevent-triggers-twice/49039258#49039258
                    stage.close();
                    break;
                case "ESCAPE":
                    stage.close();
                    shouldThrowException = true;
                    break;
            }
        });
        
        stage.showAndWait();
        if (shouldThrowException) {
            throw new UserCanceledException();
        }
        String out = tf.getText() == null ? "[empty]" : tf.getText(); 
        return out;
    }
    
    static class UserCanceledException extends Exception{}
    
}
