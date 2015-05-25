package SkyRiders.core;

import Shaders.SkyDomeShader;
import Shaders.GeneralShader;
import javax.media.opengl.GL3;

public class ShaderHandler {
    
    static public GeneralShader generalShader;
    static public SkyDomeShader skyDomeShader;
    
    static public void Init(GL3 gl){
        skyDomeShader = new SkyDomeShader();
        skyDomeShader.init(gl);
        generalShader = new GeneralShader("complete_vertex.glsl", "complete_fragment.glsl");
        generalShader.init(gl);
        
    }
    
}
