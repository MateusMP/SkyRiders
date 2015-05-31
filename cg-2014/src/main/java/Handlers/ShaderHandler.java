package Handlers;

import MathClasses.Vector3;
import Shaders.FoliageShader;
import Shaders.SkyDomeShader;
import Shaders.GeneralShader;
import static SkyRiders.SkyRiders.gl;
import SkyRiders.core.ModelBuilder;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.Triangle;
import java.util.Vector;

public class ShaderHandler {
    
    static public GeneralShader generalShader;
    static public SkyDomeShader skyDomeShader;
    static public FoliageShader foliageShader;
    
    static public void Init(){
        skyDomeShader = new SkyDomeShader();
        skyDomeShader.init(gl);
        
        generalShader = new GeneralShader("complete_vertex.glsl", "complete_fragment.glsl");
        generalShader.init(gl);
        
        foliageShader = new FoliageShader("foliage_vertex.glsl", "complete_fragment.glsl");
        foliageShader.init(gl);
    }
    
}
