/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timelinefx;

/**
 *
 * @author User
 */
public class MathUtils {
    static float map(float x, float range1Min, float range1Max, float range2Min, float range2Max){
        float range1Size = range1Max-range1Min;
        float range2Size = range2Max-range2Min;
        x -= range1Min; //from 0 to range1Size
        x /= range1Size; //from 0 to 1
        x *= range2Size; //from 0 to range2Size
        x += range2Min; //from range2Min to range2Max
        return x;
    }
    
    static float map(double x, double range1Min, double range1Max, double range2Min, double range2Max){
        float range1Size = (float) (range1Max - range1Min);
        float range2Size = (float) (range2Max - range2Min);
        x -= range1Min; //from 0 to range1Size
        x /= range1Size; //from 0 to 1
        x *= range2Size; //from 0 to range2Size
        x += range2Min; //from range2Min to range2Max
        return (float) x;
    }
}
