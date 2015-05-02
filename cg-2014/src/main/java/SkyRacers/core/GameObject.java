package SkyRacers.core;

import MathClasses.Transform;
import MathClasses.Vector3;
import static SkyRacers.SkyRacers.modelMatrix;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;

public class GameObject {
    
    protected Transform transform;
    protected LODMesh mesh;
    
    public GameObject(Vector3 pos, JWavefrontObject model3D)
    {
        mesh = new LODMesh(model3D);
        transform = new Transform();
        
        transform.rotation.set(0, 0, 0);
        transform.position = pos;
        transform.scale.set(1,1,1);
    }
    
    public GameObject(Vector3 pos, Vector3 scale, JWavefrontObject model3D)
    {
        mesh = new LODMesh(model3D);
        transform = new Transform();
        
        transform.rotation.set(0, 0, 0);
        transform.position = pos;
        transform.scale = scale;
    }
    
    public void update()
    {
        
    }
    
    public void draw()
    {
        modelMatrix.loadIdentity();
        modelMatrix.translate(transform.position.x, transform.position.y, transform.position.z);
        modelMatrix.rotate(transform.rotation.x, 1.0f, 0, 0);
        modelMatrix.rotate(transform.rotation.y, 0, 1.0f, 0);
        modelMatrix.rotate(transform.rotation.z, 0, 0, 1.0f);
        modelMatrix.scale(transform.scale.x, transform.scale.y, transform.scale.z);
        modelMatrix.bind();
        
        mesh.ActiveMeshDraw();
    }
    
    public Transform getTransform() {
        return transform;
    }
}
