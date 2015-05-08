package SkyRacers.core;

import MathClasses.Vector3;
import br.usp.icmc.vicg.gl.jwavefront.Vertex;
import java.util.ArrayList;

public interface MeshRenderer {
    
    /**
     * 
     * @return raios em x,y,z
     */
    Vector3 getSizes();
    
    ArrayList<Vertex> getVertices();
    
    void draw();
    
}
