
package SkyRiders;

import SkyRiders.core.Camera;
import SkyRiders.core.GameObject;
import SkyRiders.core.SkydomeMesh;
import Handlers.ShaderHandler;

public class SkyDome extends GameObject{

    Camera cam;
    
    public SkyDome(Camera _cam, SkydomeMesh objM) {
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
        ShaderHandler.skyDomeShader.LoadSkyTexture(mesh.getActiveMesh().getTexture());
        ShaderHandler.skyDomeShader.LoadModelMatrix(transform.getMatrix());
        mesh.ActiveMeshDraw();
    }
}
