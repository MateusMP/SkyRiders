package SkyRacers.core;

import MathClasses.Transform;
import MathClasses.Vector3;
import static SkyRacers.SkyRacers.modelMatrix;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.jwavefront.Vertex;

public class GameObject {
    
    protected Transform transform;
    protected LODMesh mesh;
    protected float objectRadius;

    
    public GameObject(Transform t, JWavefrontObject model3D)
    {
        mesh = new LODMesh(model3D);
        transform = t;
        
        calculateObjectRadius();
    }
    
    public GameObject(Vector3 pos, JWavefrontObject model3D)
    {
        mesh = new LODMesh(model3D);
        transform = new Transform();
        
        transform.rotation.set(0, 0, 0);
        transform.position = pos;
        transform.scale.set(1,1,1);
        
        calculateObjectRadius();
    }
    
    public GameObject(Vector3 pos, Vector3 scale, JWavefrontObject model3D)
    {
        mesh = new LODMesh(model3D);
        transform = new Transform();
        
        transform.rotation.set(0, 0, 0);
        transform.position = pos;
        transform.scale = scale;
        
        calculateObjectRadius();
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
    
    private void calculateObjectRadius() 
    {
        this.objectRadius = 0.0f;
        float auxDist;
        for(Vertex vertex : this.mesh.getActiveMesh().getVertices())
        {
            Vector3 point = new Vector3(vertex.x, vertex.y, vertex.z);
            auxDist = this.transform.position.calcAbsolDistance(point);
            if( auxDist > this.objectRadius )
                this.objectRadius = auxDist;
        }
    }
    
    public Transform getTransform() {
        return transform;
    }
    
    public float getObjectRadius() {
        return objectRadius;
    }
    
}
