package Shaders;

import SkyRiders.core.ModelBuilder;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.Texture;
import br.usp.icmc.vicg.gl.jwavefront.Triangle;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;

public class SkyDomeShader extends Shader{
    
    private static final int SKYDOME_DIFFUSE = 0;

    private int vertex_positions_handle;
    private int vertex_textures_handle;

    private int texture_handle;
   
    // Matrices
    private int modelMatrix_hdl;
    private int projMatrix_hdl;
    public int viewMatrix_hdl;
    
    //
    private Matrix4 projection;
    private Matrix4 view;

    public SkyDomeShader() {
        super("SkyDomeVertex.glsl", "SkyDomeFragment.glsl");
    }
    
    @Override
    protected void RegisterAllUniformLocations() {
                
        vertex_positions_handle = super.getAttribLocation("a_position");
        vertex_textures_handle = super.getAttribLocation("a_texcoord");
        
        texture_handle = super.getUniformLocation("u_texture");
        
        modelMatrix_hdl = super.getUniformLocation("u_modelMatrix");
        projMatrix_hdl = super.getUniformLocation("u_projectionMatrix");
        viewMatrix_hdl = super.getUniformLocation("u_viewMatrix");
    }
    
    public int getVertexPositionH(){
        return vertex_positions_handle;
    }
    public int getVertexTexturesH(){
        return vertex_textures_handle;
    }

    @Override
    public void fullBind() {
        super.bind();
        super.loadMatrix(projMatrix_hdl, projection);
        super.loadMatrix(viewMatrix_hdl, view);
        
        ConnectTexturesUnits();
    }
    
    /**
     * Define each texture handle to the correct texture unit.
     */
    protected void ConnectTexturesUnits(){
        super.loadInt(texture_handle, SKYDOME_DIFFUSE);
    }
    
    public void LoadSkyTexture(Texture texture){
        super.loadTexture(GL3.GL_TEXTURE0+SKYDOME_DIFFUSE, texture);
    }
    
    public void LoadProjView(Matrix4 proj, Matrix4 view){
        this.projection = proj;
        this.view = view;
        
        super.loadMatrix(projMatrix_hdl, proj);
        super.loadMatrix(viewMatrix_hdl, view);
    }
    
    public void LoadModelMatrix(Matrix4 model){
        super.loadMatrix(modelMatrix_hdl, model);
    }
    
    
    public int CreateSkyObject(Group group)
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
        group.vbo[0] = ModelBuilder.StoreDataInAttributeListfv(this.getVertexPositionH(), 3, vertex_buffer);
        group.vbo[1] = ModelBuilder.StoreDataInAttributeListfv(this.getVertexTexturesH(), 2, texture_buffer);
        gl.glBindVertexArray(0); // Disable our Vertex Buffer Object

        return group.vao;
    }
}
