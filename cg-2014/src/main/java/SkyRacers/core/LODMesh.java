package SkyRacers.core;

import MathClasses.Vector3;
import SkyRacers.SkyRacers;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import java.util.ArrayList;


public class LODMesh {
    
    private final ArrayList<MeshRenderer> meshes;
    private int activeModel;
    
    private static final int LOD_LEVEL_DIST = 1000;    // Every X units, decrease 1 level of detail
    private static final int MAX_RENDER_DIST = 10000;
    
    /**
     * @param lods Modelos de diferentes resolucoes. 0 = melhor.
     */
    public LODMesh( MeshRenderer lods[] )
    {
        meshes = new ArrayList<MeshRenderer>();
        for (int i = 0; i < lods.length; ++i){
            meshes.add(lods[i]);
        }
        
        activeModel = 0;
    }
    
    public LODMesh( MeshRenderer lods )
    {
        meshes = new ArrayList<MeshRenderer>();
        meshes.add(lods);
        
        activeModel = 0;
    }
    
    public void CalculateLOD(Camera cam)
    {
        Vector3 myworldpos = null;
        float dist = cam.GetPosition().dot( myworldpos );
        
        if (dist >= MAX_RENDER_DIST){
            activeModel = -1;
        } else {
            activeModel = (int) dist/LOD_LEVEL_DIST;
            if (activeModel > meshes.size())
                activeModel = meshes.size()-1;
        }
    }
    
    public void ActiveMeshDraw(){
        if (activeModel < 0)
            return;
        
        MeshRenderer w = meshes.get(activeModel);
        w.draw();
        SkyRacers.AddRenderingObject(w);
    }
   
    public MeshRenderer getActiveMesh(){
        return meshes.get(activeModel);
    }
}
