
package SkyRiders.core;

import MathClasses.Transform;
import br.usp.icmc.vicg.gl.jwavefront.Material;
import br.usp.icmc.vicg.gl.jwavefront.Texture;

public abstract class Particle {
    
    public static float[] buffer_RectangleV = {
            -0.5f, 0.5f, 0,
             0.5f, 0.5f, 0,
             -0.5f, -0.5f, 0,
            0.5f,  -0.5f, 0
    };
    
    public static float[] buffer_RectangleT = {
            1,  0,
            1,  1,
            0,  1,
            0,  0
    };
    
    //
    public Transform transform;
    
    //
    
    public Particle( Transform t ){
        transform = t;
    }
    
    public abstract int getVAO();
    
    public abstract void update();
    
    public Transform getTransform(){
        return transform;
    }
    
    public abstract float getRadius();

    public abstract Texture getTexture();
    
    public abstract Material getMaterial();

    public abstract GameRenderer.RENDER_TYPE getType();
    
}
