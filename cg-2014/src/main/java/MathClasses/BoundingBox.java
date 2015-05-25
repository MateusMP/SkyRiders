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
    
    public Vector3 getDimension(){
        return max.sub(min);
    }
    
    public Vector3 getCenter(){
        return max.sub(min).div(2);
    }
    
    public Vector3 getOffset(){
        return min;
    }
    
    public void expand(BoundingBox box){
        if (box.min.x < min.x) min.x = box.min.x;
        if (box.min.y < min.y) min.y = box.min.y;
        if (box.min.z < min.z) min.z = box.min.z;
        
        if (box.max.x > max.x) max.x = box.max.x;
        if (box.max.y > max.y) max.y = box.max.y;
        if (box.max.z > max.z) max.z = box.max.z;
    }
    
    public float getMaximumSphereRadius(){
        return max.sub(min).norm()/2.0f;
    }
    
}
