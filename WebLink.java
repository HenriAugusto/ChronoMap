/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import javafx.scene.paint.Color;

/**
 * This class represents a WebLink that is meant to be contained into a {@link Event} that the app can 
 * load into a {@link Browser} object.
 * @see Event
 * @see Browser
 * @author HenriAugusto
 */
public class WebLink {
    private WebLinkType type;
    private String url;
    private String name;

    /**
     * Creates a WebLink of the desired type and given url and name
     * @param type
     * @param url
     * @param name 
     */
    public WebLink(WebLinkType type, String url, String name) {
        this.type = type;
        this.url = url;
        this.name = name;
    }
    
    public void setUrl(String url){
        this.url = url;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public WebLinkType getType(){
        return type;
    }
    
    public void setType(WebLinkType type){
        this.type = type;
    }
    
    /**
     * returns the link type for this WebLink.
     * @return type
     */
    public WebLinkType getLinkType(){
        return type;
    }
    
    /**
     * <b>TO DO: THERE IS A DEFAULT ENUM TO STRING METHOD IN ENUM. BUT I MUST CONVERT FROM STRING TO THIS IN ORDER TO PARSE THE XML FILE</b>
     * This enum specifies types of web links. It is important so the app can know if it should open the browser or not.
     * Types that does <b>not</b> open the browser: 
     *  <ul>
     *      <li>AUDIO</li>
     *      <li>OTHER_DO_NOT_OPEN_BROWSER</li>
     *  </ul>
     * For all the rest the browser is opened.
     */
    public enum WebLinkType {
        AUDIO,
        VIDEO,
        IMAGE,
        TEXT,
        GAME,
        OTHER_OPEN_BROWSER,
        OTHER_DO_NOT_OPEN_BROWSER;

        private WebLinkType() {
        }
        
        public static WebLinkType getTypeFromString(String s){
            for (WebLinkType value : WebLinkType.values()) {
                if(  value.toString().equals(s)  ){
                    return value;
                }
            }
            return OTHER_OPEN_BROWSER; //default
        }
    }
    
    public WebLink getCopy(){
        return new WebLink(getType(), getUrl(), getName());
    }
}
