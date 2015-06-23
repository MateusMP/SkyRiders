package SkyRiders.core;

import SkyRiders.Profiler;
import java.util.ArrayList;


public class LODMesh {
    
    static public boolean USE_LOD = true;
    
    private final ArrayList<MeshRenderer> meshes;
    private int activeModel;
    
    private static final int LOD_LEVEL_DIST = 1000;    // Every X units, decrease 1 level of detail
    private static final int MAX_RENDER_DIST = 7000;
    
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
    
    public void CalculateLOD(float dist)
    {
        if (USE_LOD)
        {
            if (dist >= MAX_RENDER_DIST){
                activeModel = -1;
            } else {
                activeModel = (int) dist/LOD_LEVEL_DIST;
                if (activeModel >= meshes.size())
                    activeModel = meshes.size()-1;
            }
        } else {
            activeModel = 0;
        }
        
//        if (activeModel > 0)
//        System.out.println("LOD LVL: "+activeModel+ " - "+dist);
    }
    
    public void ActiveMeshDraw(){
        
        if (activeModel < 0)
            return;
        
        MeshRenderer w = meshes.get(activeModel);
        
        w.draw();
        Profiler.AddRenderingObject(w);
    }
   
    public MeshRenderer getActiveMesh(){        
        if (activeModel == -1)
            return null;
        
        return meshes.get(activeModel);
    }
}
