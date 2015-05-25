/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathClasses;

/**
 *
 * @author Mateus
 */
public class BoundingBox {
    
    private Vector3 min;
    private Vector3 max;
    
    public BoundingBox(Vector3 min, Vector3 max){
        this.min = min;
        this.max = max;
    }
    
    /**
     * Vector containing width, height and depth
     * @return 
     */
    public Vector3 getDimension(){
        return max.sub(min);
    }
    
    /**
     * Box center in space
     * @return 
     */
    public Vector3 getCenter(){
        return max.add(min).div(2);
    }
    
    /**
     * Center position inside the box
     * @return 
     */
    public Vector3 getBoxCenter(){
        return max.sub(min).div(2);
    }
    
    /**
     * Offset from origin
     * @return 
     */
    public Vector3 getOffset(){
        return min;
    }
    
    /**
     * Grow this box to contain the received box
     * @param box 
     */
    public void expand(BoundingBox box){
        if (box.min.x < min.x) min.x = box.min.x;
        if (box.min.y < min.y) min.y = box.min.y;
        if (box.min.z < min.z) min.z = box.min.z;
        
        if (box.max.x > max.x) max.x = box.max.x;
        if (box.max.y > max.y) max.y = box.max.y;
        if (box.max.z > max.z) max.z = box.max.z;
    }
    
    /**
     * Maximum bouding sphere obtained from diagonal distance/2
     * @return 
     */
    public float getMaximumSphereRadius(){
        return max.sub(min).norm()/2.0f;
    }
    
}
