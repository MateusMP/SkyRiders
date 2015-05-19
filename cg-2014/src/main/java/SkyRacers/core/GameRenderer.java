package SkyRacers.core;

import MathClasses.Transform;
import SkyRacers.SkyRacers;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.Sphere;
import br.usp.icmc.vicg.gl.util.Shader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import javax.media.opengl.GL2;
import javax.media.opengl.GL3;

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
        GL3 gl = SkyRacers.hdl().gl;
        
        for (GameObject o : map.objects){
            AddObject(o);
        }
        
        // --
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glCullFace(GL2.GL_BACK);
        RenderLayer(objects.get(RENDER_TYPE.RENDER_SOLID));
    
        // --
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glDepthMask(false);  // disable write to depth buffer
        RenderLayer(objects.get(RENDER_TYPE.RENDER_TRANSPARENT));
        gl.glDepthMask(true); 
        
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
                s.fullBind();
                
                for ( GameObject o : ls_objs ){
                    
                    o.draw();
                    DrawBoundingSphere(o);
                }
                
                s.unbind();
            }
        }
    }
    
    private static void DrawBoundingSphere(GameObject o)
    {
        float radius = o.getObjectRadius();
        Transform t = o.getTransform();
        Sphere sp = new Sphere(radius);
        sp.init(SkyRacers.hdl().gl, ShaderHandler.generalShader);
        sp.bind();
        ShaderHandler.generalShader.LoadModelMatrix(t.getMatrix());
        sp.draw();
        sp.dispose();
    }
}
