package SkyRiders.core;

import Handlers.ShaderHandler;
import MathClasses.Transform;

public class FoliageObject extends GameObject{
    
    public FoliageObject(Transform t, MeshRenderer model3D[])
    {
        super(t, model3D);
    }
    
    public void update()
    {
        
    }
    
    public void draw()
    {
        ShaderHandler.foliageShader.BindObject(this);
        
        mesh.ActiveMeshDraw();
    }
}
