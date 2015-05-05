package SkyRacers.core;

import MathClasses.Vector3;
import br.usp.icmc.vicg.gl.matrix.Matrix4;

public interface Camera {
    
    public void DefineViewMatrix(Matrix4 viewMatrix);
    
    public void DefineProjectionMatrix(Matrix4 projectionMatrix, float angle, float aspect, float nearDistance, float farDistance);
    
    public Vector3 GetPosition();
    
    public float getAngle();

    public float getAspect();

    public float getNearDistance();

    public float getFarDistance();
    
    public Vector3 getLookat();
    
    public Vector3 getUp();
}
