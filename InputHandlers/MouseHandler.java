/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Class with static methods used to handle mouse input on the main Stage
 * @author Henri Augusto
 */
public class MouseHandler {
    static boolean selecting = false;
    static Point2D selectStart;
    
    
    static void mousePressed(MouseEvent e){
        if( e.isPrimaryButtonDown() ){
            System.out.println("timelinefx.MouseHandler.mousePressed()");
            Point2D mouse = new Point2D(e.getX(), e.getY());
            mouse = ChronoMapApp.app.timeline.gview.untransformPointOnView(mouse);
            //System.out.println("IS MOUSE ON VIEW? (should be true) ="+TimelineFXApp.app.timeline.gview.isPointOnTransformedView(mouse));
            for (Event event : ChronoMapApp.app.timeline.events) {
                if(   event.checkPointNearEvent(mouse.getX(), mouse.getY())   ){
                    event.select();
                } else {
                    event.unselect();
                }
            
            }
        } else if( e.isSecondaryButtonDown() ){
            selecting = true;
            GraphicView gv = ChronoMapApp.app.timeline.gview;
            Point2D mouse = new Point2D(e.getX(), e.getY() );
            selectStart = gv.untransformPointOnView( mouse );
        }
        ChronoMapApp.app.draw();
    }
    
    static void mouseDragged(MouseEvent e){
        
        if( e.isSecondaryButtonDown() && selecting ){
                ChronoMapApp.app.draw();
                GraphicsContext gc = ChronoMapApp.app.cnv.getGraphicsContext2D();
                GraphicView gv = ChronoMapApp.app.timeline.gview;
                //what is faster? stroking a rect or filling a rect then clearing a smaller one?
                Point2D mouse = new Point2D(e.getX(), e.getY() );
                mouse = gv.untransformPointOnView(mouse);
                //gc.setTransform( new Affine() );
                gc.setLineWidth(10);
                gc.setStroke(Color.CRIMSON);
                gc.setFill(Color.CRIMSON);
                //gc.strokeRect(selectStart.getX(), selectStart.getY(), mouse.getX(), mouse.getY());
                Point2D leftTop = new Point2D( 
                        Math.min(selectStart.getX(), mouse.getX()) ,
                        Math.min(selectStart.getY(), mouse.getY())
                );
                Point2D rightBottom = new Point2D(
                    Math.max(selectStart.getX(), mouse.getX()),
                    Math.max(selectStart.getY(), mouse.getY())
                );
                double w = rightBottom.getX()-leftTop.getX();
                double h = rightBottom.getY()-leftTop.getY();
                gc.setLineWidth(2);
                gc.strokeRect(leftTop.getX(), leftTop.getY(), w, h);
                //gc.fillRect(50, 50, 150, 150);
        } 
    }
    
    static void mouseReleased(MouseEvent e){
        if(selecting){
            selecting = false;
            ChronoMapApp.app.draw();
            GraphicView gv = ChronoMapApp.app.timeline.gview;
            Point2D mouse = new Point2D(e.getX(), e.getY());
            mouse = gv.untransformPointOnView(mouse);
            Point2D leftTop = new Point2D(
                    Math.min(selectStart.getX(), mouse.getX()),
                    Math.min(selectStart.getY(), mouse.getY())
            );
            Point2D rightBottom = new Point2D(
                    Math.max(selectStart.getX(), mouse.getX()),
                    Math.max(selectStart.getY(), mouse.getY())
            );
            double w = rightBottom.getX() - leftTop.getX();
            double h = rightBottom.getY() - leftTop.getY();
            Rectangle2D r = new Rectangle2D(leftTop.getX(), leftTop.getY(), w, h);
            ChronoMapApp.app.timeline.clearSelectedEvents();
            for (Event event : ChronoMapApp.app.timeline.events) {
                event.setSelected( r.contains(event.start/2+event.end/2, event.height)  );
            }
            ChronoMapApp.app.draw();
        }
    }
    
}
