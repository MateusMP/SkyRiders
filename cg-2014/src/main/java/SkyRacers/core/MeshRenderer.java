package SkyRacers.core;

import MathClasses.Vector3;
import br.usp.icmc.vicg.gl.jwavefront.Vertex;
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
    
    void draw();
    
}
