/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;


/**
 * This is just a useful stage to get a boolean input form the user.
 * @author Henri Augusto
 */
public class YesOrNoStage {
    static boolean output; //can't be local because goes out of scope (erased from memory) after function returns
    static boolean shouldThrowException; //can't be local because goes out of scope (erased from memory) after function returns
    
    static boolean getBooleanFromUser(String messageToDisplay) throws UserCanceledException {
        Stage stage = new Stage();
        shouldThrowException = false;
        
        //create root
        VBox root = new VBox();
            root.setPadding( new Insets(10) );
        //Label
        Label label = new Label(messageToDisplay);
            label.setFont( new Font(20) );
            label.setWrapText(true);
        //TextField for getting user input
        
        Button no = new Button("No");
        Button yes = new Button("Yes");
        HBox btnHBox = new HBox( no, yes );
            btnHBox.setSpacing( 20 );
            btnHBox.setAlignment(Pos.CENTER);
        root.getChildren().addAll(label, btnHBox);
        no.setOnAction((e)->{
            output = false;
            stage.close();
        });
        yes.setOnAction((e)->{
            output = true;
            stage.close();
        });
        
        //Scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        //Enter to accept
        
        scene.setOnKeyPressed((event) -> {
            switch(event.getCode().toString()){
                /*
                case "ENTER":
                    event.consume(); //https://stackoverflow.com/questions/47797980/javafx-keyevent-triggers-twice/49039258#49039258
                    stage.close();
                    break;
                */
                case "ESCAPE":
                    stage.close();
                    shouldThrowException = true; //so cumbersone    http://www.baeldung.com/java-lambda-exceptions
                    break;
            }
        });
        
        stage.showAndWait();
        if( shouldThrowException ){
            throw new UserCanceledException();
        }
        return output;
    }
    
    static class UserCanceledException extends Exception{}
    
}
