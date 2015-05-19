package SkyRiders.core;

import br.usp.icmc.vicg.gl.util.GeneralShader;
import javax.media.opengl.GL3;

public class ShaderHandler {
    
    static public GeneralShader generalShader;
    
    static public void Init(GL3 gl){
        generalShader = new GeneralShader("complete_vertex.glsl", "complete_fragment.glsl");
        generalShader.init(gl);
        
    }
    
}
