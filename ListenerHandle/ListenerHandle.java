/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

/**
 * an interface for objects that will hold an ObservableValue and a Listener. It can attach and remove the listener from that property;
 * More information:
 * https://blog.codefx.org/techniques/use-listenerhandles/
 * @author User
 */
public interface ListenerHandle {
    
    void attachListener();
    void removeListener();
}
