package SkyRiders;

import Handlers.ShaderHandler;
import Handlers.TextureHandler;
import MathClasses.Transform;
import static SkyRiders.SkyRiders.gl;
import SkyRiders.core.GameRenderer;
import SkyRiders.core.Particle;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.jwavefront.Material;
import br.usp.icmc.vicg.gl.jwavefront.Texture;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matt
 */
public class LensFlareParticle  extends Particle{
    
    static int vao = 0;
    static int vbo[];
    
    public Texture texture;
    
    public Material material;
    
    public static LensFlareParticle CreateLensFlareParticle(Transform t, int i){
        
        LensFlareParticle sp = new LensFlareParticle(t);
        
        if (vao == 0)
        {
            JWavefrontObject jw = new JWavefrontObject( new File("./Assets/graphics/sun_plane.obj") );
            try {
                jw.init(gl);
            } catch (IOException ex) {
                Logger.getLogger(LensFlareParticle.class.getName()).log(Level.SEVERE, null, ex);
            }
            Group g = jw.getGroups().get(0);

            vao = ShaderHandler.particleShader.CreateTexturedParticle(g);
        }
        
        switch(i){
            case 0:
                sp.texture = TextureHandler.LoadTexture("./Assets/graphics/lensFlare1.png");
                break;
            case 1:
                sp.texture = TextureHandler.LoadTexture("./Assets/graphics/sun.png");
                break;
        }
        
        return sp;
    }

    private LensFlareParticle(Transform t) {
        super(t);
        
        material = new Material("w");
        material.diffuse = new float[]{0.6f, 0.6f, 0.6f, 1.0f};
        material.ambient = new float[]{0.6f, 0.6f, 0.6f, 0.0f};
    }

    @Override
    public int getVAO() {
        return vao;
    }
    
    @Override
    public void update() {
        
        
        material.diffuse[3] = 0.5f;
        

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
