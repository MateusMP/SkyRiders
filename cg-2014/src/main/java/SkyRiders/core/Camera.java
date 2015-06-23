package SkyRiders.core;

import MathClasses.Vector3;
import br.usp.icmc.vicg.gl.matrix.Matrix4;

public interface Camera {
    
    public void DefineViewMatrix(Matrix4 viewMatrix);
    
    public Vector3 GetPosition();
    
    public void SetPosition(Vector3 pos);
    
    public Vector3 getLookat();
    
    public Vector3 getUp();
}
