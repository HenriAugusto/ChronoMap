/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ChronoMap;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 *
 * @author User
 */
public class Geometry {
    static float TWO_PI = (float) (2*3.14159265);

    static double distanceBetweenPointAndLine(Vector2D point, Vector2D linePoint0, Vector2D linePoint1) {
        //the variable names on the function arguments makes it clear how to use the function
        //but now i will use shorter names
        Vector2D pt = point;
        Vector2D lp0 = linePoint0;
        Vector2D lp1 = linePoint1;
        double numerator = (lp0.y - lp1.y) * pt.x + (lp1.x - lp0.x) * pt.y + (lp0.x * lp1.y - lp1.x * lp0.y);
        double denumerator = sqrt(
                pow(lp1.x - lp0.x, 2) + pow(lp1.y - lp0.y, 2)
        );
        return abs(numerator / denumerator);
    }
    
    static float distanceBetweenPointAndSegment(Vector2D point, Vector2D segPoint1, Vector2D segPoint2){
        //Vector representing our segment
        Vector2D segment = Vector2D.sub(segPoint2, segPoint1);
        //our point relative to segPoint1
        Vector2D relativePoint = Vector2D.sub(point, segPoint1);
        //Angle of the segment
        float segmentAngle = segment.heading();
        //makes it parallel to the X axis
        segment.rotate( TWO_PI-segmentAngle ); 
        //rotates the point accordingly
        relativePoint.rotate( TWO_PI-segmentAngle );
        if( relativePoint.x < 0){
            return distanceBetweenPoints(relativePoint,segPoint1);
        } else if( relativePoint.x > segment.x){
            return distanceBetweenPoints(relativePoint,segPoint2);
        } else {
            return (float) distanceBetweenPointAndLine(point, segPoint1, segPoint2);
        }
    }
    
    boolean isPointNearSegment(Vector2D point, Vector2D linePoint0, Vector2D linePoint1, float maxDist){
    //this just rotates everything so the segment is paralell to the X axis so we can compare it in terms of only X
    if( abs( distanceBetweenPointAndLine(point, linePoint0, linePoint1) ) > maxDist ){
        return false;
    }
    Vector2D segment = Vector2D.sub(linePoint1, linePoint0);
    Vector2D relativePoint = Vector2D.sub(point, linePoint0);
    
    float segmentAngle = segment.heading();
    
    System.out.println("segmentAngle: "+segmentAngle);
    //makes it parallel to the X axis
    segment.rotate( TWO_PI-segmentAngle ); 
    //rotates it accordingly
    relativePoint.rotate( TWO_PI-segmentAngle ); 
    if( relativePoint.x > segment.x || relativePoint.x < 0){
        return false; 
    }
    return true;
}

    public static float distanceBetweenPoints(Vector2D p1, Vector2D p2) {
        return (float) abs(sqrt(pow(p2.x-p1.x,2)+pow(p2.y-p1.y,2)));
    }
    
}
