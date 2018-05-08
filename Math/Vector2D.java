package timelinefx;
        
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;

class Vector2D{
    float x,y;

    Vector2D(){}
    Vector2D(float _x, float _y){x=_x;y=_y;}
    Vector2D(double _x, double _y){
        x=(float)_x;
        y=(float)_y;
    }
    
    void set(float _x, float _y){x=_x;y=_y;}
    void set(double _x, double _y){x=(float)_x;y=(float)_y;}

    void mult(double m) {
        x *= m;
        y *= m;
    }

    void add(Vector2D delta) {
        x += delta.x;
        y += delta.y;
    }
    
    //from Processing's PVector
    void rotate(float theta){
        float temp = x;
        // Might need to check for rounding errors like with angleBetween function?
        x = (float) (x * cos(theta) - y * sin(theta));
        y = (float) (temp * sin(theta) + y * cos(theta));
    }
    
    //from Processing's PVector
    float heading() {
        float angle = (float) Math.atan2(y, x);
        return angle;
    }
    
    static Vector2D sub(Vector2D v1, Vector2D v2){
        return new Vector2D(v1.x-v2.x,v1.y-v2.y);
    }
    
    //returns a Vector2D of the coordinates of the leftTop point of a rectangle
    //takes input two diagonal vertex of the rectangle
    static Vector2D getRectLeftTop(Vector2D a, Vector2D b){
        Vector2D output = new Vector2D();
        output.x = min(a.x,b.x);
        output.y = min(a.y,b.y);
       return output;
    }
    
    //returns a Vector2D of the coordinates of the rightBottom point of a rectangle
    //takes input two diagonal vertex of the rectangle
    static Vector2D getRectRightBottom(Vector2D a, Vector2D b) {
        Vector2D output = new Vector2D();
        output.x = max(a.x, b.x);
        output.y = max(a.y, b.y);
        return output;
    }
    
}