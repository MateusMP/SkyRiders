package SkyRacers.core;

import SkyRacers.SkyRacers;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import java.util.ArrayList;
import java.util.HashMap;
import javax.media.opengl.GL2;

public class GameRenderer {
    
    public static enum RENDER_TYPE{
        RENDER_SOLID,
        RENDER_WATER,
        RENDER_TRANSPARENT,
    }
    
    private static final Frustum frustum = new Frustum();
    private static final HashMap<RENDER_TYPE, ArrayList<GameObject>> objects = new HashMap<>();
    
    public static void SetFrustum(Matrix4 projection, Matrix4 view){
        frustum.extractFromOGL(projection, view);
    }
    
    public static void AddObject(GameObject obj)
    {
        if ( frustum.sphereIntersects(obj.getTransform().position.x, obj.getTransform().position.y, 
                                       obj.getTransform().position.z, obj.getObjectRadius()) == Frustum.Result.Miss )
            return;
        
        
        ArrayList<GameObject> lista = objects.get(obj.getRenderType());
        
        if (lista == null) // Criar nova lista
        {
            lista = new ArrayList<>();
            lista.add(obj);
            objects.put(obj.getRenderType(), lista);
        } else {
            lista.add(obj);
        }
    }
    
    public static void Render(Map map){
        
        for (GameObject o : map.objects){
            AddObject(o);
        }

        // --
        SkyRacers.hdl().gl.glEnable(GL2.GL_CULL_FACE);
        ArrayList<GameObject> solids = objects.get(RENDER_TYPE.RENDER_SOLID);
        if (solids != null)
            for (GameObject o : solids){
                o.draw();
            }
        
        // --
        SkyRacers.hdl().gl.glDisable(GL2.GL_CULL_FACE);
        ArrayList<GameObject> transparents = objects.get(RENDER_TYPE.RENDER_TRANSPARENT);
        if (transparents != null)
            for (GameObject o : transparents){
                o.draw();
            }

        // --
        ArrayList<GameObject> waters = objects.get(RENDER_TYPE.RENDER_WATER);
        if (waters != null)
            for (GameObject o : waters){
                o.draw();
            }
        
        objects.clear();
    }
    
    
            // DEBUG
//            float radius = go.getObjectRadius();
//            Transform t = go.getTransform();
//            Sphere sp = new Sphere(radius);
//            sp.init(SkyRacers.hdl().gl, SkyRacers.hdl().shader);
//            sp.bind();
//            SkyRacers.modelMatrix.loadIdentity();
//            SkyRacers.modelMatrix.translate(t.position.x, t.position.y, t.position.z);
//            sp.draw();
}
