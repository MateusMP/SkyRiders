package SkyRiders.core;

import MathClasses.BoundingBox;
import MathClasses.Transform;
import MathClasses.Vector3;
import SkyRiders.SkyRiders;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.Cube;
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
        BoundingBox meshbox = obj.getMesh().getBoundingBox();
        
        Transform t = obj.getTransform();
        Vector3 pos = t.position.add( meshbox.getCenter().add(meshbox.getOffset()).scale(t.scale) );
        
        if ( frustum.sphereIntersects( pos.x, pos.y, pos.z, obj.getObjectRadius()) == Frustum.Result.Miss )
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
        GL3 gl = SkyRiders.hdl().gl;
        
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
//        gl.glDepthMask(false);  // disable write to depth buffer
        RenderLayer(objects.get(RENDER_TYPE.RENDER_TRANSPARENT));
//        gl.glDepthMask(true); 
        
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
                    
//                    if (o.getRenderType() == RENDER_TYPE.RENDER_SOLID){
//                        System.out.println(o.name);
                        o.draw();
                        DrawBoundingSphere(o);
//                    }
                }
                
                s.unbind();
            }
        }
    }
    
    private static void DrawBoundingSphere(GameObject o)
    {
        BoundingBox meshbox = o.getMesh().getBoundingBox();
        float radius = o.getObjectRadius();
//        Vector3 offset = meshbox.getOffset();
        Transform objtrans = o.getTransform();
        
//        System.out.println(radius + " " + meshbox.getDimension() + "center" + meshbox.getCenter() + " off "+offset);
        
        /*Transform w = new Transform();
        w.position = objtrans.position.add( meshbox.getCenter().add(meshbox.getOffset()).scale(objtrans.scale) );
        w.scale.x = meshbox.getDimension().x;
        w.scale.y = meshbox.getDimension().y;
        w.scale.z = meshbox.getDimension().z;
        w.scale = w.scale.scale(objtrans.scale);
        
        Cube c = new Cube();
        c.init(SkyRiders.hdl().gl, ShaderHandler.generalShader);
        c.bind();
        ShaderHandler.generalShader.LoadModelMatrix(w.getMatrix());
        c.draw();
        c.dispose();*/
        
        Transform w = new Transform();
        w.position = objtrans.position.add( meshbox.getCenter().add(meshbox.getOffset()).scale(objtrans.scale) );
        w.scale = w.scale.scale(objtrans.scale);
                
        Sphere sp = new Sphere(radius);
        sp.init(SkyRiders.hdl().gl, ShaderHandler.generalShader);
        sp.bind();
        ShaderHandler.generalShader.LoadModelMatrix(w.getMatrix());
        sp.draw();
        sp.dispose();
    }
}
