package SkyRiders.core;

import Handlers.ShaderHandler;
import MathClasses.Transform;
import MathClasses.Vector3;

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
        transform.Invalidate();
        
        calculateObjectRadius();
    }
    
    public GameObject(Vector3 pos, MeshRenderer model3D)
    {
        mesh = new LODMesh(model3D);
        transform = new Transform();
        
        transform.rotation.set(0, 0, 0);
        transform.position = pos;
        transform.scale.set(1,1,1);
        transform.Invalidate();
        
        calculateObjectRadius();
    }
    
    public GameObject(Vector3 pos, Vector3 scale, MeshRenderer model3D)
    {
        mesh = new LODMesh(model3D);
        transform = new Transform();
        
        transform.rotation.set(0, 0, 0);
        transform.position = pos;
        transform.scale = scale;
        transform.Invalidate();
        
        calculateObjectRadius();
    }
    
    public void update()
    {
        
    }
    
    public void draw()
    {
        ShaderHandler.generalShader.BindObject(this);
        
        mesh.ActiveMeshDraw();
    }
    
    private void calculateObjectRadius() 
    {        
        objectRadius = mesh.getActiveMesh().getBoundingBox().getMaximumSphereRadius()*transform.scale.norm();        
//        System.out.println("RADIUS: "+objectRadius);
    }

    
    public Transform getTransform() {
        return transform;
    }
    
    public float getObjectRadius() {
        calculateObjectRadius();
        return objectRadius;
    }
    
    public GameRenderer.RENDER_TYPE getRenderType(){
        return rendermode;
    }
    
    public void setRenderType(GameRenderer.RENDER_TYPE t){
        rendermode = t;
    }
    
    public MeshRenderer getMesh(){
        return mesh.getActiveMesh();
    }
    
}
