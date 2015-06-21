package Handlers;

import Shaders.FoliageShader;
import Shaders.SkyDomeShader;
import Shaders.GeneralShader;
import Shaders.ParticleShader;
import Shaders.WaterShader;
import static SkyRiders.SkyRiders.gl;
import br.usp.icmc.vicg.gl.jwavefront.Texture;

public class ShaderHandler {
    
    static public GeneralShader generalShader;
    static public SkyDomeShader skyDomeShader;
    static public FoliageShader foliageShader;
    static public WaterShader waterShader;
    static public ParticleShader particleShader;
    
    static public Texture dudv_texture;
    
    static public void Init(){
        skyDomeShader = new SkyDomeShader();
        skyDomeShader.init(gl);
        
        generalShader = new GeneralShader("complete_vertex.glsl", "complete_fragment.glsl");
        generalShader.init(gl);
        
        foliageShader = new FoliageShader("foliage_vertex.glsl", "complete_fragment.glsl");
        foliageShader.init(gl);
        
        waterShader = new WaterShader();
        waterShader.init(gl);
        
        dudv_texture = TextureHandler.LoadTexture("./Assets/graphics/water_dudv.png");
        
        particleShader = new ParticleShader();
        particleShader.init(gl);
    }
    
}
