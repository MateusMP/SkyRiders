package Handlers;

import Shaders.FoliageShader;
import Shaders.SkyDomeShader;
import Shaders.GeneralShader;
import static SkyRiders.SkyRiders.gl;
import SkyRiders.core.ModelBuilder;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.Triangle;

public class ShaderHandler {
    
    static public GeneralShader generalShader;
    static public SkyDomeShader skyDomeShader;
    static public FoliageShader foliageShader;
    
    static public void Init(){
        skyDomeShader = new SkyDomeShader();
        skyDomeShader.init(gl);
        
        generalShader = new GeneralShader("complete_vertex.glsl", "complete_fragment.glsl");
        generalShader.init(gl);
        
        foliageShader = new FoliageShader("foliage_vertex.glsl", "complete_fragment.glsl");
        foliageShader.init(gl);
    }
    
    /**
     * USED BY GENERAL SHADER
     * 
     * @return return the generated VAO id
     */
    static public int CreateTexturedObject(Group group)
    {
        if (group.triangles.isEmpty()) {
          return -1;
        }

        float[] vertex_buffer = new float[9 * group.triangles.size()];
        float[] normal_buffer = new float[9 * group.triangles.size()];
        float[] texture_buffer = new float[6 * group.triangles.size()];

        for (int j = 0; j < group.triangles.size(); j++) {
          Triangle triangle = group.triangles.get(j);

          for (int k = 0; k < 3; k++) {
            vertex_buffer[(9 * j) + (3 * k)] = triangle.vertices[k].x;
            vertex_buffer[(9 * j) + (3 * k) + 1] = triangle.vertices[k].y;
            vertex_buffer[(9 * j) + (3 * k) + 2] = triangle.vertices[k].z;

            normal_buffer[(9 * j) + (3 * k)] = triangle.vertex_normals[k].x;
            normal_buffer[(9 * j) + (3 * k) + 1] = triangle.vertex_normals[k].y;
            normal_buffer[(9 * j) + (3 * k) + 2] = triangle.vertex_normals[k].z;

            if (triangle.vertex_tex_coords[k] != null) {
              texture_buffer[(6 * j) + (2 * k)] = triangle.vertex_tex_coords[k].u;
              texture_buffer[(6 * j) + (2 * k) + 1] = triangle.vertex_tex_coords[k].v;
            }
          }
        }

        group.vao = ModelBuilder.CreateVAO();
        group.vbo = new int[3];
        group.vbo[0] = ModelBuilder.StoreDataInAttributeListfv(generalShader.getVertexPositionH(), 3, vertex_buffer);
        group.vbo[1] = ModelBuilder.StoreDataInAttributeListfv(generalShader.getVertexNormalsH(), 3, normal_buffer);
        if (group.material.texture != null) {
            group.vbo[2] = ModelBuilder.StoreDataInAttributeListfv(generalShader.getVertexTexturesH(), 2, texture_buffer);
        }
        gl.glBindVertexArray(0); // Disable our Vertex Buffer Object

        return group.vao;
    }
    
    
    static public int CreateSkyObject(Group group)
    {
        if (group.triangles.isEmpty()) {
          return -1;
        }

        float[] vertex_buffer = new float[9 * group.triangles.size()];
        float[] texture_buffer = new float[6 * group.triangles.size()];

        for (int j = 0; j < group.triangles.size(); j++) {
          Triangle triangle = group.triangles.get(j);

          for (int k = 0; k < 3; k++) {
            vertex_buffer[(9 * j) + (3 * k)] = triangle.vertices[k].x;
            vertex_buffer[(9 * j) + (3 * k) + 1] = triangle.vertices[k].y;
            vertex_buffer[(9 * j) + (3 * k) + 2] = triangle.vertices[k].z;

            if (triangle.vertex_tex_coords[k] != null) {
              texture_buffer[(6 * j) + (2 * k)] = triangle.vertex_tex_coords[k].u;
              texture_buffer[(6 * j) + (2 * k) + 1] = triangle.vertex_tex_coords[k].v;
            }
          }
        }

        group.vao = ModelBuilder.CreateVAO();
        group.vbo = new int[2];
        group.vbo[0] = ModelBuilder.StoreDataInAttributeListfv(skyDomeShader.getVertexPositionH(), 3, vertex_buffer);
        group.vbo[1] = ModelBuilder.StoreDataInAttributeListfv(skyDomeShader.getVertexTexturesH(), 2, texture_buffer);
        gl.glBindVertexArray(0); // Disable our Vertex Buffer Object

        return group.vao;
    }
}
