package SkyRiders.core;

import MathClasses.BoundingBox;
import MathClasses.Vector3;
import br.usp.icmc.vicg.gl.jwavefront.Vertex;
import br.usp.icmc.vicg.gl.util.Shader;
import java.util.ArrayList;

public interface MeshRenderer {
        
    /**
     * @return 
     */
    public BoundingBox getBoundingBox();
    /**
     * 
     * @return Return a vector of vertices
     */
    public ArrayList<Vertex> getVertices();
    
    /**
     * @return Return meshes VAO id
     */
    public int getVAO();
    
    /**
     * Shader used to draw this object
     */
    public Shader getShader();
    
    /**
     * Set shader used to draw
     * @param s 
     */
    public void setShader(Shader s);
    
    public void draw();
    
}
