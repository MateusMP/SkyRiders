package Renderers;

import MathClasses.BoundingBox;
import SkyRiders.core.MeshRenderer;
import static SkyRiders.SkyRiders.gl;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.Material;
import br.usp.icmc.vicg.gl.jwavefront.Texture;
import br.usp.icmc.vicg.gl.jwavefront.Triangle;
import br.usp.icmc.vicg.gl.jwavefront.Vertex;
import Shaders.Shader;
import java.util.ArrayList;
import javax.media.opengl.GL3;

public class TexturedMesh implements MeshRenderer {
    
    private Group g;
    private Shader shader;
    private Texture texture;
    private Material material;
    private ArrayList<Vertex> vertices;
    
    public TexturedMesh(Group obj, Shader s)
    {
        g = obj;
        shader = s;
        
        texture = obj.material.texture; // Define default texture
        material = obj.material;
        
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
    public Material getMaterial(){
        return material;
    }
    
    @Override
    public void draw()
    {
        if (g.triangles.isEmpty()) {
            return;
        }

//        if (g.material != null) {
//            material.setAmbientColor(g.material.ambient);
//            material.setDiffuseColor(g.material.diffuse);
//            material.setSpecularColor(g.material.specular);
//            material.setSpecularExponent(g.material.shininess);
//            material.bind();
//        }

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
        return shader;
    }
    
    @Override
    public void setShader(Shader s){
        shader = s;
//        material.init(gl, shader);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return g.getBoundingBox();
    }
    
}
