/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.AmbientLight;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.transform.Rotate;

/**
 * Currently only supports one window
 * @author User
 */
public class ThreeDimensionalVisualizationStageManager {
    static Stage stage;
    static Scene scene;
    static Group root;
    static Group subSceneRoot;
    static PerspectiveCamera camera;
    static SubScene subScene;
    static boolean cached = true;
    static CacheHint cacheHint = CacheHint.DEFAULT;

    static void showWindow() {
        if (stage == null || true) {
            createWindow();
        }
        stage.show();
        stage.requestFocus();
    }
    
    private static void createWindow(){
        stage = new Stage();
        root = new Group();
            root.setCache(cached); //https://stackoverflow.com/questions/28753724/javafx-2d-text-with-background-in-3d-scene
            root.setCacheHint(cacheHint);
        scene = new Scene(root, 1644, 700, true, SceneAntialiasing.BALANCED);
        //scene = new Scene(root);
        //scene.getAntiAliasing
        //Initialize the camera
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
            //camera.setNearClip(500); //so font doesn't look blurry
        camera.setFarClip(10000);
        camera.setTranslateZ(-3000);

        //scene.setCamera(camera);
        //Light
        AmbientLight light = new AmbientLight(Color.LIME);
        //light.setTranslateX(1950);
        //light.setTranslateY(100);
        //root.getChildren().add(light);
        //creates the SubScene and add the camera to it!
        subSceneRoot = new Group();
            subSceneRoot.setCache(cached);
            subSceneRoot.setCacheHint(cacheHint);
            
        subScene = new SubScene(subSceneRoot, 1800, 700, true, SceneAntialiasing.BALANCED);
            subScene.setCache(cached);
            subScene.setCacheHint(cacheHint);
            root.getChildren().add(subScene); //MUST BE AFTE/R THE LAST LINE
        subScene.setFill(Color.WHITE);
        subScene.setCamera(camera);
        Font font = new Font(300);
        font = Font.font("Arial", FontPosture.REGULAR, 100);
        
        int oi  = 0;
        for (Event e : TimelineFXApp.app.timeline.events) {
            subSceneRoot.getChildren().add( e.get3DShape() );
            
            //Create the text and take a snapshot!
            Text text = new Text(e.name);
                text.setFont(font);
                text.setFill(e.color);
            WritableImage img = text.snapshot(/*ssp*/null, null);
            makeWhiteTransparent(img);
            
            text.getTransforms().add( new Translate(e.start / 2 + e.end / 2 , e.height) );
            text.setTranslateZ(e.z); //THE SET TRANSLATE Z MUST BE SET HERE OTHERWISE THE SNAPSHOT WOULD NOT CAPTURE THE TEXT FOR ANY Z != 0
            text.getTransforms().add( new Translate(- text.getLayoutBounds().getWidth()/2, -text.getLayoutBounds().getHeight()) );
            text.setCache(cached); //https://stackoverflow.com/questions/28753724/javafx-2d-text-with-background-in-3d-scene
            text.setCacheHint(cacheHint);
            int t = 0;

            PhongMaterial mat;// = new PhongMaterial(Color.BLUE, img, null, img, img);
            mat = new PhongMaterial();
            mat.setDiffuseMap(img);
            //mat.setDiffuseColor( new Color(1, 1, 1, 0.2) ); //ALPHA <1 IS IMPORTANT
            
            //Scale the textsnapshot and put it on a ImageView
            double scale = 1.0d/8.0d;

            ImageView imgV = new ImageView(img);
                
                imgV.setTranslateX(e.start / 2 + e.end / 2 -img.getWidth()/2-(e.end-e.start)/2);
                double h = e.height; //same as event
                    //the text is centered the same way as if it was not scaled!
                    h -= img.getHeight()/2; //center the text Y on the cylinder
                    h -= img.getHeight()*scale; //just put the text over the cylinder
                    h -= img.getHeight()*scale/4; //add 1/4 of label height of gap
                imgV.setTranslateY( h );
                
                //imgV.getTransforms().add( new Scale(scale, scale) ); //doesn't work
                imgV.setScaleX(scale); //works
                imgV.setScaleY(scale);//works 
                imgV.setTranslateZ(e.z);
                
                subSceneRoot.getChildren().add(imgV);
        }
        addReferences();
        stage.setScene(scene);
       setEventHandlers();
    }
    
    static void setEventHandlers() {
        scene.setOnMouseDragged((event) -> {
            //camera.setRotate(event.getX());
            double translateX = (event.getSceneX() - 0) / stage.getWidth();
            int size = TimelineFXApp.app.timeline.maxYear-TimelineFXApp.app.timeline.minYear;
            translateX = translateX * size + (+TimelineFXApp.app.timeline.minYear);
            double translateY = (event.getSceneY() - 0) / stage.getHeight();
            translateY = translateY * TimelineFXApp.app.timeline.height - TimelineFXApp.app.timeline.height/2;
            double tz;
            if(!camera.getTransforms().isEmpty()){
                tz = camera.getTransforms().get(0).getTz();
            } else {
                tz = camera.getTranslateZ();
            }
            camera.getTransforms().clear();
            camera.getTransforms().addAll(
                    new Translate(translateX, translateY, tz)
                );
            System.out.println("EVENT + "+event.getX()+", "+event.getY()+")");
            System.out.println("translate = (" + translateX + ", " + translateY + ", "+tz+")");
            System.out.println("STAGE = ("+stage.getWidth()+", "+stage.getHeight()+")");
        });
        //KEY PRESSED
        scene.setOnKeyPressed((event) -> {
            switch (event.getCode().toString()) {
                case "F10":
                    double amt = event.isControlDown() ? 100 : 30;
                    camera.setTranslateZ(camera.getTranslateZ() + amt);
                    break;
                case "F11":
                    amt = event.isControlDown() ? -100 : -30;
                    camera.setTranslateZ(camera.getTranslateZ() + amt);
                    break;
                case "R":
                    for (Iterator<Event> iterator = TimelineFXApp.app.timeline.events.iterator(); iterator.hasNext();) {
                        Event e = iterator.next();
                        double tz = camera.getTranslateZ();
                        camera.getTransforms().clear();
                        camera.getTransforms().addAll(
                                new Translate(e.start/2+e.end/2, e.height, tz)
                        );
                        return;
                    }
                    break;
            }
        });
    }
    
    static void saveImage(Image img){
        BufferedImage bImage = SwingFXUtils.fromFXImage(img, null);
        try {
            File file = new File("C:/JavaFX/test"+(int)(Math.random()*100)+".png");
            ImageIO.write(bImage, "png", file);
            System.err.println("SAVED");
        } catch (IOException ex) {
            Logger.getLogger(ThreeDimensionalVisualizationStageManager.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("FODEU");
            System.err.println("FODEU");
            System.err.println("FODEU");
        }
    }
    
    static BufferedImage invertImage(BufferedImage img){
        short[] data = new short[256];
        for (short i = 0; i < 256; i++) {
            data[i] = (short) (255 - i);
        }
        BufferedImage dstImage = null;
        LookupTable lookupTable = new ShortLookupTable(0, data);
        LookupOp op = new LookupOp(lookupTable, null);
        dstImage = op.filter(img, null);
        return dstImage;
    }

    private static void makeWhiteTransparent(WritableImage img) {
        for (int x = 0; x < img.getWidth(); ++x) {
                for (int y = 0; y < img.getHeight(); ++y) {
                    Color c = img.getPixelReader().getColor(x, y);
                    if(c.getRed()>=0.8 && c.getGreen()>=0.8 && c.getBlue()>=0.8 ){
                        c = new Color( 0, 0, 0, 0); //just a transparent color
                        //c = new Color( 1, 1, 1, 0.3); //just a 70% transparent color //doesn't work. you can see through the text other texs but not 3d objects
                        //c = new Color( 0.1, 1, 0, 1); //just a transparent color
                    }
                    img.getPixelWriter().setColor(x, y, c);
                }
            }
    }
    
    private static void addReferences(){
        if(true){
            return;
        }
        int delta = 100;
        for (int i = TimelineFXApp.app.timeline.minYear; i < TimelineFXApp.app.timeline.maxYear; i += delta) {
            if(true){break;}
            Box plane = new Box(1, TimelineFXApp.app.timeline.height, 1000);
            plane.setTranslateX(i);
            PhongMaterial mat = new PhongMaterial(Color.DARKORCHID.deriveColor(0, 1, 1, 0.3));
            mat.setSpecularPower(128);
            plane.setMaterial(mat);
            subSceneRoot.getChildren().add(plane);
        }
        PhongMaterial mat = new PhongMaterial(Color.PURPLE);
        double size = 2;
        int deltaY = 100;
        int deltaZ = 100;
        for (int x = TimelineFXApp.app.timeline.minYear; x < TimelineFXApp.app.timeline.maxYear; x += delta) {
            for (int y = -TimelineFXApp.app.timeline.height/2; y < TimelineFXApp.app.timeline.height/2; y += deltaY) {
                //Box b = new Box(size, size, 1000);
                    Cylinder b = new Cylinder(size, 1000);                    
                b.setMaterial( new PhongMaterial(Color.CHARTREUSE) );
                    b.setRotationAxis(Rotate.X_AXIS);
                    b.setRotate(90);
                b.setTranslateX(x);
                b.setTranslateY(y);
                    
                //b.setTranslateZ(b.getDepth()/2);*/
                subSceneRoot.getChildren().add(b);
                if(x==0 && false){
                    for (int z = 0; z < 1000; z+=deltaZ) {
                        Box b2 = new Box(TimelineFXApp.app.timeline.maxYear-TimelineFXApp.app.timeline.minYear, size, size);
                        b2.setMaterial(mat);
                        b2.setTranslateY(y);
                        b2.setTranslateZ(z);
                        subSceneRoot.getChildren().add(b2);
                    }
                }
                if (y == 0) {
                    for (int z = 0; z < 1000; z += deltaZ) {
                        Box b2 = new Box(size , TimelineFXApp.app.timeline.height, size);
                        b2.setMaterial(mat);
                        b2.setTranslateX(x);
                        b2.setTranslateZ(z);
                        subSceneRoot.getChildren().add(b2);
                    }
                }
            }
        }
    }
    
}
