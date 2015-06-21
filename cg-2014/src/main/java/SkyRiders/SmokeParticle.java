package SkyRiders;

import Handlers.ShaderHandler;
import Handlers.TextureHandler;
import MathClasses.Transform;
import MathClasses.Vector3;
import static SkyRiders.SkyRiders.gl;
import SkyRiders.core.Camera;
import SkyRiders.core.GameRenderer;
import SkyRiders.core.Particle;
import SkyRiders.core.Quaternion;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.jwavefront.Material;
import br.usp.icmc.vicg.gl.jwavefront.Texture;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SmokeParticle extends Particle {

    static int vao = 0;
    static int vbo[];
    
    static Texture texture;
    
    public Material material;
    
    public static SmokeParticle CreateSmokeParticle(Transform t){
        
        if (vao == 0)
        {
            JWavefrontObject jw = new JWavefrontObject( new File("./Assets/graphics/sun_plane.obj") );
            try {
                jw.init(gl);
            } catch (IOException ex) {
                Logger.getLogger(SmokeParticle.class.getName()).log(Level.SEVERE, null, ex);
            }
            Group g = jw.getGroups().get(0);

            vao = ShaderHandler.particleShader.CreateTexturedParticle(g);

            texture = TextureHandler.LoadTexture("./Assets/graphics/smoke.png");
        }
        
        SmokeParticle sp = new SmokeParticle(t);
        return sp;
    }

    private SmokeParticle(Transform t) {
        super(t);
        
        material = new Material("w");
        material.diffuse = new float[]{0.6f, 0.6f, 0.6f, 1.0f};
        material.ambient = new float[]{0.6f, 0.6f, 0.6f, 0.0f};
    }

    @Override
    public int getVAO() {
        return vao;
    }
    
    
    float x = 0;
    @Override
    public void update() {
        
        x += 0.1f;
        
        material.diffuse[3] = 0.5f;
        
        Camera cam = SkyRiders.skyriders.getCurrentCamera();
        Vector3 p = transform.position.sub(cam.GetPosition());
        
        Quaternion qt = new Quaternion();
        qt.fromAxis(x, p);
        transform.rotation = qt.eulerAngles();

        transform.Invalidate();
        
    }

    @Override
    public float getRadius() {
        return 2;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public GameRenderer.RENDER_TYPE getType() {
        return GameRenderer.RENDER_TYPE.RENDER_TRANSPARENT;
    }

    @Override
    public Material getMaterial() {
        return material;
    }
    
}
