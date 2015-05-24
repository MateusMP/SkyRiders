package MathClasses;

import br.usp.icmc.vicg.gl.matrix.Matrix4;

public class Transform {
    private boolean needrebuild;
    private Matrix4 matrix;
    
    public Vector3 position;
    public Vector3 rotation;
    public Vector3 scale;
    
    public Transform(){
        position = new Vector3();
        rotation = new Vector3();
        scale = new Vector3(1,1,1);
        
        matrix = new Matrix4();
        needrebuild = true;
    }
    
    public Transform(Transform t){
        position = new Vector3(t.position);
        rotation = new Vector3(t.rotation);
        scale = new Vector3(t.scale);
        
        matrix = new Matrix4();
        needrebuild = true;
    }
    
    /**
     * Forces the matrix to be rebuilt on the next getMatrix call
     */
    public void Invalidate(){
        needrebuild = true;
    }
    
    public Matrix4 getMatrix()
    {
        if (needrebuild)
        {
            needrebuild = false;
            matrix.loadIdentity();
            matrix.translate(position.x, position.y, position.z);
            matrix.rotate(rotation.x, 1.0f, 0, 0);
            matrix.rotate(rotation.y, 0, 1.0f, 0);
            matrix.rotate(rotation.z, 0, 0, 1.0f);
            matrix.scale(scale.x, scale.y, scale.z);
        }
        
        return matrix;
    }
    
    
    public static Matrix4 CreateMatrix(Vector3 position, Vector3 rotation, Vector3 scale){
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
