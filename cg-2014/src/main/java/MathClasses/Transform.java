package MathClasses;

import br.usp.icmc.vicg.gl.matrix.Matrix4;

public class Transform {
    
    public Vector3 position;
    public Vector3 rotation;
    public Vector3 scale;
    
    public Transform(){
        position = new Vector3();
        rotation = new Vector3();
        scale = new Vector3();
    }
    
    public Matrix4 createMatrix(){
        Matrix4 m = new Matrix4();
        
        m.loadIdentity();
        m.translate(position.x, position.y, position.z);
        m.rotate(rotation.x, 1.0f, 0, 0);
        m.rotate(rotation.y, 0, 1.0f, 0);
        m.rotate(rotation.z, 0, 0, 1.0f);
        m.scale(scale.x, scale.y, scale.z);
        
        return m;
    }
        
}
