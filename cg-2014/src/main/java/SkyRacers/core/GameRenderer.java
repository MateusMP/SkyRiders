package SkyRacers.core;

import SkyRacers.SkyRacers;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.util.GeneralShader;
import br.usp.icmc.vicg.gl.util.Shader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import javax.media.opengl.GL2;

public class GameRenderer {
    
    public static enum RENDER_TYPE{
        RENDER_SOLID,
        RENDER_WATER,
        RENDER_TRANSPARENT,
    }
    
    private static final Frustum frustum = new Frustum();
    private static final HashMap<RENDER_TYPE, HashMap<Shader, ArrayList<GameObject>> > objects = new HashMap<>();
    
    public static void SetFrustum(Matrix4 projection, Matrix4 view){
        frustum.extractFromOGL(projection, view);
    }
    
    public static void AddObject(GameObject obj)
    {
        if ( frustum.sphereIntersects(obj.getTransform().position.x, obj.getTransform().position.y, 
                                       obj.getTransform().position.z, obj.getObjectRadius()) == Frustum.Result.Miss )
            return;
        
        
        RENDER_TYPE layer_id = obj.getRenderType();
        Shader shader_id = obj.mesh.getActiveMesh().getShader();
        
        HashMap<Shader, ArrayList<GameObject>> layer = objects.get(layer_id);
        if ( layer == null ) // Criar nova layer
        {
            layer = new HashMap<Shader, ArrayList<GameObject>>();
            objects.put(layer_id, layer);
        }
        
        ArrayList<GameObject> lista = layer.get(shader_id);
        if (lista == null) // Criar nova lista
        {
            lista = new ArrayList<>();
            layer.put(shader_id, lista);
        }
        lista.add(obj);
    }
    
    public static void Render(Map map){
        
        for (GameObject o : map.objects){
            AddObject(o);
        }

        // --
        SkyRacers.hdl().gl.glEnable(GL2.GL_CULL_FACE);
        RenderLayer(objects.get(RENDER_TYPE.RENDER_SOLID));
        
        // --
        SkyRacers.hdl().gl.glDisable(GL2.GL_CULL_FACE);
        RenderLayer(objects.get(RENDER_TYPE.RENDER_TRANSPARENT));

        // --
        RenderLayer(objects.get(RENDER_TYPE.RENDER_WATER));
        
        objects.clear();
    }
    
    private static void RenderLayer( HashMap<Shader, ArrayList<GameObject>> layer )
    {
        if (layer == null)
            return;
        
        Set<Shader> set = layer.keySet();
        
        for (Shader s : set){
            ArrayList<GameObject> ls_objs = layer.get(s);
            
            if (ls_objs != null)
            {
//                s.fullbind();
                
                for ( GameObject o : ls_objs ){
                    
//                    GeneralShader gs = (GeneralShader) s;
//                    gs.LoadModelMatrix( o.getTransform().createMatrix() );
                    
                    o.draw();
                }
                
//                s.unbind();
            }
        }
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
