package SkyRiders.core;

import MathClasses.Vector3;
import br.usp.icmc.vicg.gl.jwavefront.Vertex;
import br.usp.icmc.vicg.gl.util.Shader;
import java.util.ArrayList;

public interface MeshRenderer {
    
    /**
     * @return raios em x,y,z
     */
    Vector3 getSizes();
    
    /**
     * 
     * @return Return a vector of vertices
     */
    ArrayList<Vertex> getVertices();
    
    /**
     * @return Return meshes VAO id
     */
    int getVAO();
    
    /**
     * Shader used to draw this object
     */
    Shader getShader();
    
    /**
     * Set shader used to draw
     * @param s 
     */
    void setShader(Shader s);
    
    void draw();
    
}
