package SkyRiders.core;

import MathClasses.BoundingBox;
import MathClasses.Vector3;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.jwavefront.Triangle;
import br.usp.icmc.vicg.gl.jwavefront.Vertex;
import br.usp.icmc.vicg.gl.util.Shader;
import java.util.ArrayList;

public class ObjMesh implements MeshRenderer {
    
    private Shader shader;
    private JWavefrontObject fullmesh;
    private Group g;
    private ArrayList<Vertex> vertices;
    
    public ObjMesh(JWavefrontObject obj, String group)
    {
        fullmesh = obj;
        g = obj.findGroup(group);
        
        copyVertices();
    }
    
    private void copyVertices()
    {
        if (g == null){
            vertices = fullmesh.getVertices();
            return;
        }
        
        vertices = new ArrayList<Vertex>();
        for (Triangle t : g.triangles){
            vertices.add(t.vertices[0]);
            vertices.add(t.vertices[1]);
            vertices.add(t.vertices[2]);
        }
    }
    
    @Override
    public void draw()
    {
        if (g != null){
            fullmesh.drawGroup(g);
        } else {
            fullmesh.draw();
        }
    }

    @Override
    public ArrayList<Vertex> getVertices() {
        
        return vertices;
    }

    @Override
    public int getVAO() {
        return g.vao;
    }

    @Override
    public Shader getShader() {
        return shader;
    }
    
    @Override
    public void setShader(Shader s){
        shader = s;
    }

    @Override
    public BoundingBox getBoundingBox() {
        
        if (g != null)
            return g.getBoundingBox();

        return fullmesh.getBoundingBox();
    }
    
}
