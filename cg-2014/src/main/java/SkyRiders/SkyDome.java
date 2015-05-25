
package SkyRiders;

import MathClasses.Transform;
import SkyRiders.core.Camera;
import SkyRiders.core.GameObject;
import SkyRiders.core.LODMesh;
import SkyRiders.core.MeshHandler;
import SkyRiders.core.MeshRenderer;
import SkyRiders.core.ObjMesh;
import SkyRiders.core.ShaderHandler;

public class SkyDome extends GameObject{

    Camera cam;
    
    public SkyDome(Camera _cam, ObjMesh objM) {
        super(_cam.GetPosition(), objM);
        this.cam = _cam;
        transform.scale.x = 100;
        transform.scale.y = 100;
        transform.scale.z = 100;
    }
    
    @Override
    public void update()
    {
        transform.position = cam.GetPosition();
        transform.Invalidate();
    }
    @Override
    public void draw()
    {
        ShaderHandler.skyDomeShader.LoadModelMatrix(transform.getMatrix());
        mesh.ActiveMeshDraw();
    }
}
