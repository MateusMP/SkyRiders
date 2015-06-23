package SkyRiders.core;

import Handlers.ShaderHandler;
import MathClasses.BoundingBox;
import MathClasses.Transform;
import MathClasses.Vector3;
import SkyRiders.SkyDome;
import static SkyRiders.SkyRiders.gl;
import br.usp.icmc.vicg.gl.jwavefront.Texture;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.Cube;
import br.usp.icmc.vicg.gl.model.Sphere;
import Shaders.Shader;
import SkyRiders.AirplaneCamera;
import SkyRiders.LensFlareParticle;
import SkyRiders.SmokeParticle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL3;

public class GameRenderer {

    public static enum RENDER_TYPE{
        RENDER_SOLID,
        RENDER_SOLID2FACES,
        RENDER_WATER,
        RENDER_TRANSPARENT,
    }
   
    private static SkyDome skyDome;
    private static final Frustum frustum = new Frustum();
    private static final HashMap<RENDER_TYPE, HashMap<Shader, ArrayList<GameObject>> > objects = new HashMap<>();
    private static final HashMap<Texture, ArrayList<Particle> > particles = new HashMap<>();
    private static Matrix4 projection, view;
    private static Camera camera;
    private static float[] lightPosition;
    private static boolean debug = true;
    public static float width;
    public static float height;
    
    public static void SetFrustum(Camera cam, Matrix4 _projection, Matrix4 _view){
        camera = cam;
        frustum.extractFromOGL(_projection, _view);
        projection = _projection;
        view = _view;
    }
    
    private static void RenderSkyDome(){
        if (skyDome == null)
            return;
        
        ShaderHandler.skyDomeShader.bind();
        ShaderHandler.skyDomeShader.LoadProjView(projection, view);
        ShaderHandler.skyDomeShader.LoadSkyTexture(skyDome.getMesh().getTexture());
        skyDome.update();
        skyDome.draw();
        ShaderHandler.skyDomeShader.unbind();
    }
    
    public static void AddObject(GameObject obj)
    {
        obj.CalculateLOD(camera);
        
        if (obj.getMesh() == null)
            return;
        
        BoundingBox meshbox = obj.getMesh().getBoundingBox();
        
        Transform t = obj.getTransform();
        Vector3 pos = t.position.add( meshbox.getCenter().scale(t.scale) );
        
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
  
    public static void AddParticle(Particle particle)
    {
        ArrayList<Particle> ps = particles.get(particle.getTexture());
        if ( ps == null ){
            ps = new ArrayList<>();
            particles.put(particle.getTexture(), ps);
        }
        
        ps.add(particle);
    }
    
    public static void Render(Map map){
        LensFlare();        
        
        for (GameObject o : map.objects){
            AddObject(o);
        }
        
        RenderScene();
        
        RenderParticles();
        
        //projection
        //view
        //point
        
        objects.clear();
    }
    
    private static void LensFlare(){
        Vector3 dist = new Vector3();
        Vector3 center = new Vector3();
        Vector3 draw = new Vector3();
        
        Vector3 lightPos = new Vector3();
        Matrix4 flare = new Matrix4();
        
        lightPos.set(lightPosition[0], lightPosition[1], lightPosition[2]);
        
        //Vector3 toCamera = lightPos.neg().normalize();//camera.GetPosition().sub(lightPos).normalize();
        Vector3 toCamera = camera.GetPosition().sub(lightPos).normalize();
        //System.out.println("toCamera " + toCamera);
        //System.out.println("Lookat " +((AirplaneCamera) camera).getLookatNormalized());
        float abertura = toCamera.dot(((AirplaneCamera) camera).getLookatNormalized());
        //System.out.println("Abertura " + abertura);
        
        if(abertura > -0.7)
            return;
        
        flare.copyFrom(projection);
        flare.multiply(view.getMatrix());        
        lightPos = flare.Mult(lightPos);
        
        dist.x = lightPos.x;
        dist.y = lightPos.y;
        dist.z = 0;
        
        dist = dist.normalize();
        System.out.println("Dist " + dist);
        
        draw.x = dist.x * 0.4f;
        draw.y = dist.y * 0.4f;
        
        Transform transform = new Transform();
        transform.scale = new Vector3(0.5f,0.5f,0.5f);
        transform.position = draw;
        
        LensFlareParticle p = LensFlareParticle.CreateLensFlareParticle(transform, 0);        
        ShaderHandler.particleShader.LoadMaterial(p.getMaterial());
        p.getMaterial().diffuse[3] = abertura;
        GameRenderer.AddParticle(p);    
        
        draw.x = dist.x * 0.6f;
        draw.y = dist.y * 0.6f;        
        transform.scale = new Vector3(0.5f,0.5f,0.5f);
        transform.position = draw;
        
        p = LensFlareParticle.CreateLensFlareParticle(transform, 1);
        
        ShaderHandler.particleShader.LoadMaterial(p.getMaterial());
        p.getMaterial().diffuse[3] = abertura;
        GameRenderer.AddParticle(p); 
    }
    
    public static void RenderScene(){
        // --
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glCullFace(GL2.GL_BACK);
        RenderSkyDome();
        
        // --
        gl.glEnable(GL.GL_DEPTH_TEST);
        RenderLayer(objects.get(RENDER_TYPE.RENDER_SOLID));
        
        gl.glDisable(GL2.GL_CULL_FACE);
        RenderLayer(objects.get(RENDER_TYPE.RENDER_SOLID2FACES));
    
        // --
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glEnable(GL2.GL_BLEND);
//        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_COLOR);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
        
//        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
//        gl.glDepthMask(false);  // disable write to depth buffer
        RenderLayer(objects.get(RENDER_TYPE.RENDER_TRANSPARENT));
//        gl.glDepthMask(true); 
        gl.glDisable(GL2.GL_BLEND);
        
        
        // --
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glCullFace(GL2.GL_BACK);
        RenderLayer(objects.get(RENDER_TYPE.RENDER_WATER));
        
    }
    
    public static void RenderParticles()
    {
        gl.glDisable(GL2.GL_CULL_FACE);
//        gl.glEnable(GL2.GL_CULL_FACE);
//        gl.glCullFace(GL2.GL_FRONT);
        gl.glEnable(GL2.GL_BLEND);
//        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_COLOR);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
//        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        ShaderHandler.particleShader.bind();
        
        Matrix4 identity = new Matrix4();
        identity.loadIdentity();
        ShaderHandler.particleShader.LoadProjView(projection, view);
        
        for (Texture t : particles.keySet())
        {
            ShaderHandler.particleShader.LoadDiffuseTexture(t);
            
            Collections.sort(particles.get(t), new Comparator<Particle>() {

                @Override
                public int compare(Particle o1, Particle o2) {
                    return (int) (o2.getTransform().position.sub(camera.GetPosition()).norm2() - o1.getTransform().position.sub(camera.GetPosition()).norm2());
                }
            });
            
            for ( Particle p : particles.get(t) )
            {
                p.update();
                
//                Camera cam = SkyRiders.SkyRiders.skyriders.getCurrentCamera();
//                Vector3 pos = p.getTransform().position;
//                Vector3 lookAt = cam.GetPosition();
//                Matrix4 m = new Matrix4();
//                m.lookAt(pos.x, pos.y, pos.z, lookAt.x, lookAt.y, lookAt.z, 0, 1, 0);
                
                ShaderHandler.particleShader.LoadModelMatrix(p.getTransform().getMatrix());
                ShaderHandler.particleShader.LoadMaterial(p.getMaterial());
                
                gl.glBindVertexArray(p.getVAO());
                gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 3 * 2);
            }
        }
        
        ShaderHandler.particleShader.unbind();
        
        gl.glDisable(GL2.GL_BLEND);
        
        particles.clear();
    }
    
    private static void RenderLayer( HashMap<Shader, ArrayList<GameObject>> layer )
    {
        if (layer == null)
            return;
        
        Set<Shader> set = layer.keySet();
        
        for (Shader s : set)
        {
            if (s == null){
                System.out.println("!!!!!! NULL SHADER");
                continue;
            }
            
            ArrayList<GameObject> ls_objs = layer.get(s);
            
            if (ls_objs != null)
            {
                s.fullBind();
                
                for ( GameObject o : ls_objs ){
//                        System.out.println(o.name);
                    o.draw();
                }
                
                s.unbind();
                
                ShaderHandler.generalShader.bind();
                ShaderHandler.generalShader.LoadProjView(projection, view);
                ShaderHandler.generalShader.LoadNormalTexture(null);
                ShaderHandler.generalShader.LoadDiffuseTexture(null);
                for ( GameObject o : ls_objs ){
                        //System.out.println(o.name);
                    if (o.getRenderType() == RENDER_TYPE.RENDER_SOLID)
                        DrawBoundingSphere(o);
                }
                ShaderHandler.generalShader.unbind();
            }
        }
    }
    
    private static void DrawBoundingSphere(GameObject o)
    {
        float radius = o.getObjectRadius();
        //System.out.println(radius);
        BoundingBox meshbox = o.getMesh().getBoundingBox();
//        System.out.println("RADIUS: "+o.getMesh().getBoundingBox().getMaximumSphereRadius());
//        System.out.println("SCALE: "+radius);
//        Vector3 offset = meshbox.getOffset();
        Transform objtrans = o.getTransform();
        
//        System.out.println(radius + " " + meshbox.getDimension() + "center" + meshbox.getCenter() + " off "+offset);
        
        Transform w = new Transform();
        w.position = objtrans.position;//.add( meshbox.getOffset().scale(objtrans.scale) );
        
//        w.scale.x = meshbox.getDimension().x;
//        w.scale.y = meshbox.getDimension().y;
//        w.scale.z = meshbox.getDimension().z;
//        w.scale = w.scale.scale(objtrans.scale);
//        
//        Cube c = new Cube();
//        c.init(gl, ShaderHandler.generalShader);
//        c.bind();
//        ShaderHandler.generalShader.LoadModelMatrix(w.getMatrix());
//        c.draw();
//        c.dispose();
//        
//        w.position = objtrans.position; //.add( o.getMesh().getBoundingBox().getCenter().scale(objtrans.scale) );
//        w.scale = new Vector3(1,1,1);
//        w.Invalidate();
        
//        Sphere sp = new Sphere(radius);
//        sp.init(gl, ShaderHandler.generalShader);
//        sp.bind();
//        ShaderHandler.generalShader.LoadModelMatrix(w.getMatrix());
//        sp.draw();
//        sp.dispose();
//        
        Line ln = new Line(new Vector3(), new Vector3(lightPosition[0],lightPosition[1],lightPosition[2]));
        ln.init(gl, ShaderHandler.generalShader);
        ln.bind();
        ShaderHandler.generalShader.LoadModelMatrix( new Matrix4() );
        ln.draw();
        ln.dispose();
//        
//        ln = new Line(new Vector3(), new Vector3(-1,0,0).mul(radius));
//        ln.init(gl, ShaderHandler.generalShader);
//        ln.bind();
//        ln.draw();
//        ln.dispose();
//        
//        ln = new Line(new Vector3(), new Vector3(0,1,0).mul(radius));
//        ln.init(gl, ShaderHandler.generalShader);
//        ln.bind();
//        ln.draw();
//        ln.dispose();
//        
//        ln = new Line(new Vector3(), new Vector3(0,-1,0).mul(radius));
//        ln.init(gl, ShaderHandler.generalShader);
//        ln.bind();
//        ln.draw();
//        ln.dispose();
//        
//        ln = new Line(new Vector3(), new Vector3(0,0,-1).mul(radius));
//        ln.init(gl, ShaderHandler.generalShader);
//        ln.bind();
//        ln.draw();
//        ln.dispose();
//        
//        ln = new Line(new Vector3(), new Vector3(0,0,1).mul(radius));
//        ln.init(gl, ShaderHandler.generalShader);
//        ln.bind();
//        ln.draw();
//        ln.dispose();
    }
    
    public static void setSkyDome(SkyDome dome)
    {
        skyDome = dome;
    }
    
    public static void setLightPos(float[] light){
        lightPosition = light;
    }
    
    public static void setScreenSize(float w, float h){
        width = w;
        height = h;
    }
}
