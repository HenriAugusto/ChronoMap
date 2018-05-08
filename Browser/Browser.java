/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 *
 * @author Henri Augusto
 */
public class Browser {
        WebView webView;
        TextField addressBar;
        VBox vbox = new VBox();//to add everything here
        ProgressBar progBar;
        
        /**
         * 
         * @param url starting url (actually it just loads google)
         * @param whereToAdd use root.getChildren()
         */
        Browser(String url, ObservableList<Node> whereToAdd){
            addressBar = new TextField(url);
            webView = new WebView();
            webView.setPrefHeight(TimelineFXApp.app.scene.getHeight()-50);
            webView.setPrefWidth(1250);
            //webView.setPrefSize(2900, 1650);
            //webView.getEngine().load("https://www.youtube.com/watch?v=49oiE8Tj1UU");
            webView.getEngine().load("https://www.google.com");
            System.out.println("work done = "+webView.getEngine().getLoadWorker().getWorkDone());
            addressBar.setAlignment(Pos.CENTER);
            progBar = new ProgressBar(1);
            addressBar.setOnKeyPressed((KeyEvent e) -> {
                    switch(e.getCode().toString()){
                        case "ENTER":
                            GUIMessages.displayMessage("address bar got enter");
                            e.consume();
                            goToUrl( addressBar.getText() );
                            break;
                        case "F5":
                            //boolean b = webView.isVisible();
                            //webView.setVisible(!b);
                            toggleVisible();
                            break;
                    }
            });
            vbox.getChildren().addAll(addressBar,webView, progBar);
            /*vbox.setOnKeyPressed((KeyEvent e) -> {
                if(e.getCode().toString().equals("F5")){
                    System.out.println("timelinefx.Browser.<init>()");
                    boolean b = webView.isVisible();
                    webView.setVisible(!b);
                }
            });
            addressBar.setOnKeyPressed((KeyEvent e) -> {
                if(e.getCode().toString().equals("F5")){
                    System.out.println("timelinefx.Browser.<init>()");
                    toggleVisible();
                }
            });
            */
            whereToAdd.add(vbox);
            addProgressListener();
        }
        
        public void setVisible(boolean b) {
            vbox.setVisible(b);
        }
        
        void toggleVisible(){
            vbox.setVisible( !vbox.isVisible() );
        }

    private void goToUrl(String url) {
        //CharSequence cs = new CharSequence("www.");
        if ( !url.contains("www.") && !url.contains("http://") && !url.contains("https://") ){
            url = "http://google.com/search?q="+url;
            GUIMessages.displayMessage("googling "+url);
        }
        //System.out.println("Loading URL: "+url);
        webView.getEngine().load(url);
    }
    
    /**
     * Opens the WebLink object in the browser. Also it displays or hide the browser depending on the link's WebLinkType
     * @param link 
     */
    public void goToLink(WebLink link) {
        String prefix;
        switch(link.getType()){
            case AUDIO:
                prefix = "Now playing:";
                break;
            case GAME:
                prefix = "Game on! Let's play:";
                break;
            case IMAGE:
                prefix = "Picture:";
                break;
            case OTHER_DO_NOT_OPEN_BROWSER:
                prefix = "Opening link silently in browser";
                break;
            case TEXT:
                prefix = "Text:";
                break;
            case VIDEO:
                prefix = "Now watching:";
                break;
            default:
            case OTHER_OPEN_BROWSER:
                prefix = "Link:";
        }
        GUIMessages.displayMessage(prefix +" "+ link.getName(), 15*1000);
        goToUrl(link.getUrl());
        if (link.getLinkType() != WebLink.WebLinkType.AUDIO
                && link.getLinkType() != WebLink.WebLinkType.OTHER_DO_NOT_OPEN_BROWSER) {
            setVisible(true);
        } else {
            setVisible(false);
        }
    }
    
    void addProgressListener() {
        // process page loading
        WebEngine webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {
                    if (newState == Worker.State.SUCCEEDED) {
                    }
                    //newState.
                    switch(newState){
                        case FAILED:
                            progBar.setProgress(-1);
                            break;
                        case READY:
                            progBar.setProgress(0);
                            break;
                        case RUNNING:
                            progBar.setProgress(0.5);
                            //progBar.set
                            ProgressIndicator test = new ProgressIndicator();
                            //test.seti
                            progBar.setProgress(progBar.INDETERMINATE_PROGRESS);
                            addressBar.setText(webEngine.getLocation());
                            break;
                        case SUCCEEDED:
                            progBar.setProgress(1);
                            //addressBar.textProperty().bind(webEngine.locationProperty());
                            addressBar.setText(webEngine.getLocation());
                            break;
                    }
                }
            }       
        );
        //webEngine.getLoadWorker().
    }

    public void setPrefDimensions(int width, int height){
        webView.setPrefHeight(height);
        webView.setPrefWidth(width);
    }
}
