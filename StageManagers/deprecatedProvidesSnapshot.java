
package ChronoMap;

import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 *
 * @author Henri Augusto
/
public abstract class providesSnapshot {
    static Stage stage = null;
    
    
    //This initializes the stage
    static abstract void createWindow();
    
    //
     * Provides a WritableImage that is a snapshot from this GUI. Useful for
     * displaying it in the HelpPages
     *
     * @return
     //
    static public WritableImage getGuiSnapshot() {

        if (stage == null) {
            createWindow();
        }
        stage.getScene().getRoot().layout();
        System.out.println("inputStage.getScene().getWidth() = " + stage.getScene().getWidth());
        System.out.println("inputStage.getScene().getWidth() = " + stage.getScene().getHeight());
        int w = stage.getScene().getWidth() > 0 ? (int) stage.getScene().getWidth() : 500;
        int h = stage.getScene().getHeight() > 0 ? (int) stage.getScene().getHeight() : 500;
        WritableImage image = new WritableImage(
                500,
                500);
        inputStage.getScene().snapshot(image);
        return image;
    }   
}
*/