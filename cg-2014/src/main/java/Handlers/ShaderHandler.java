package Handlers;

import Shaders.FoliageShader;
import Shaders.SkyDomeShader;
import Shaders.GeneralShader;
import Shaders.WaterShader;
import static SkyRiders.SkyRiders.gl;

public class ShaderHandler {
    
    static public GeneralShader generalShader;
    static public SkyDomeShader skyDomeShader;
    static public FoliageShader foliageShader;
    static public WaterShader waterShader;
    
    static public void Init(){
        skyDomeShader = new SkyDomeShader();
        skyDomeShader.init(gl);
        
        generalShader = new GeneralShader("complete_vertex.glsl", "complete_fragment.glsl");
        generalShader.init(gl);
        
        foliageShader = new FoliageShader("foliage_vertex.glsl", "complete_fragment.glsl");
        foliageShader.init(gl);
        
        waterShader = new WaterShader();
        waterShader.init(gl);
    }
    
}
