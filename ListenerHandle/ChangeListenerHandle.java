/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author User
 */
public class ChangeListenerHandle implements ListenerHandle{
    ObservableValue ov;
    ChangeListener cl;
    
    ChangeListenerHandle(ObservableValue ov, ChangeListener cl){
        this.ov = ov;
        this.cl = cl;
    }

    @Override
    public void attachListener() {
        ov.addListener(cl);
    }

    @Override
    public void removeListener() {
        ov.removeListener(cl);
    }
    
}
