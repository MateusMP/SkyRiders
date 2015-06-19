package SkyRiders.core;

import Handlers.ShaderHandler;
import MathClasses.Transform;
import MathClasses.Vector3;

public class WaterObject extends GameObject {
    
    public WaterObject(Vector3 pos, MeshRenderer model3D)
    {
        super(pos, model3D);
    }
    
    public void update()
    {
        
    }
    
    public void draw()
    {
        ShaderHandler.waterShader.BindObject(this);
        
        mesh.ActiveMeshDraw();
    }
    
    private void calculateObjectRadius() 
    {        
        objectRadius = mesh.getActiveMesh().getBoundingBox().getMaximumSphereRadius();
        
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
