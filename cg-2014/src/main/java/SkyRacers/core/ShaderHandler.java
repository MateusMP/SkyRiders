package SkyRacers.core;

import br.usp.icmc.vicg.gl.util.GeneralShader;
import javax.media.opengl.GL3;

public class ShaderHandler {
    
    static private GL3 gl;
    static public GeneralShader generalShader;
    
    static public void Init(GL3 gl){
        generalShader = new GeneralShader("complete_vertex.glsl", "complete_fragment.glsl");
        generalShader.init(gl);
        
    }
    
}
