package SkyRiders;

import Handlers.ShaderHandler;
import Handlers.TextureHandler;
import MathClasses.Transform;
import SkyRiders.SkyRiders;
import static SkyRiders.SkyRiders.gl;
import SkyRiders.SmokeParticle;
import SkyRiders.core.GameRenderer;
import SkyRiders.core.GameRenderer.RENDER_TYPE;
import SkyRiders.core.Particle;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.jwavefront.Material;
import br.usp.icmc.vicg.gl.jwavefront.Texture;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SunParticle extends Particle {
    
    int vao;
    int vbo[];
    
    Texture texture;
    
    Material material;
    
    public SunParticle( Transform t )
    {
        super(t);
        
        JWavefrontObject jw = new JWavefrontObject( new File("./Assets/graphics/sun_plane.obj") );
        try {
            jw.init(gl);
        } catch (IOException ex) {
            Logger.getLogger(SmokeParticle.class.getName()).log(Level.SEVERE, null, ex);
        }
        Group g = jw.getGroups().get(0);

        vao = ShaderHandler.particleShader.CreateTexturedParticle(g);

        texture = TextureHandler.LoadTexture("./Assets/graphics/sun.png");
        
        material = new Material("w");
        material.diffuse = new float[]{0.6f, 0.6f,0.6f, 1};
    }

    @Override
    public int getVAO() {
        return vao;
    }

    @Override
    public void update() {
        
        transform.rotation.z += 1;
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
        return RENDER_TYPE.RENDER_TRANSPARENT;
    }

    @Override
    public Material getMaterial() {
        return material;
    }
    
}
