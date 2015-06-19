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
}
