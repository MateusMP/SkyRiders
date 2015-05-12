package SkyRacers.core;

import MathClasses.Transform;
import MathClasses.Vector3;
import static SkyRacers.SkyRacers.modelMatrix;

public class GameObject {
    
    protected GameRenderer.RENDER_TYPE rendermode;
    protected Transform transform;
    protected LODMesh mesh;
    protected float objectRadius;
    
    public String name;

    public GameObject(Transform t, MeshRenderer model3D)
    {
        name = "_unnamed_";
        
        mesh = new LODMesh(model3D);
        transform = t;
                
        calculateObjectRadius();
    }
    
    public GameObject(Vector3 pos, MeshRenderer model3D)
    {
        mesh = new LODMesh(model3D);
        transform = new Transform();
        
        transform.rotation.set(0, 0, 0);
        transform.position = pos;
        transform.scale.set(1,1,1);
        
        calculateObjectRadius();
    }
    
    public GameObject(Vector3 pos, Vector3 scale, MeshRenderer model3D)
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
        Vector3 sizes = mesh.getActiveMesh().getSizes();
        sizes.x *= transform.scale.x;
        sizes.y *= transform.scale.y;
        sizes.z *= transform.scale.z;
        objectRadius = sizes.norm();
        
        System.out.println("RADIUS: "+objectRadius);
    }

    
    public Transform getTransform() {
        return transform;
    }
    
    public float getObjectRadius() {
        return objectRadius;
    }
    
    public GameRenderer.RENDER_TYPE getRenderType(){
        return rendermode;
    }
    
    public void setRenderType(GameRenderer.RENDER_TYPE t){
        rendermode = t;
    }
    
}
