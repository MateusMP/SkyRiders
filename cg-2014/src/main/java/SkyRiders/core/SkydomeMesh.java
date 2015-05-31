package SkyRiders.core;

import Handlers.ShaderHandler;
import MathClasses.BoundingBox;
import static SkyRiders.SkyRiders.gl;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.Material;
import br.usp.icmc.vicg.gl.jwavefront.Texture;
import br.usp.icmc.vicg.gl.jwavefront.Triangle;
import br.usp.icmc.vicg.gl.jwavefront.Vertex;
import br.usp.icmc.vicg.gl.util.Shader;
import java.util.ArrayList;
import javax.media.opengl.GL3;

public class SkydomeMesh implements MeshRenderer {
    
    private Group g;
    private Texture texture;
    private ArrayList<Vertex> vertices;
    
    public SkydomeMesh(Group obj)
    {
        if (obj == null){
            System.exit(-1);
        }
        g = obj;
        
        texture = obj.material.texture; // Define default texture
        
        copyVertices();
    }
    
    private void copyVertices()
    {
        vertices = new ArrayList<Vertex>();
        for (Triangle t : g.triangles){
            vertices.add(t.vertices[0]);
            vertices.add(t.vertices[1]);
            vertices.add(t.vertices[2]);
        }
    }
    
    @Override
    public Texture getTexture(){
        return texture;
    }
    
    @Override
    public void draw()
    {
        if (g.triangles.isEmpty()) {
            return;
        }

        gl.glBindVertexArray(g.vao);
        gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 3 * g.triangles.size());
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
        return ShaderHandler.skyDomeShader;
    }
    
    @Override
    public void setShader(Shader s){
        return;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return g.getBoundingBox();
    }

    @Override
    public Material getMaterial() {
        return g.material;
    }
    
}
