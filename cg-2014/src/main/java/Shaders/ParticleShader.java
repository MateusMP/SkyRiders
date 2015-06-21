package Shaders;

import SkyRiders.core.GameObject;
import SkyRiders.core.ModelBuilder;
import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.Material;
import br.usp.icmc.vicg.gl.jwavefront.Texture;
import br.usp.icmc.vicg.gl.jwavefront.Triangle;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.util.Shader;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;

/**
 * Shader generico
 */
public class ParticleShader extends Shader {
    
    private static final int TEXTURE_DIFFUSE = 0;
    
    private int vertex_positions_handle;
    private int vertex_textures_handle;

    //control if it is a texture or material
    private int diffuseTexture_hdl;
   
    // Matrices
    private int modelMatrix_hdl;
    private int projMatrix_hdl;
    public int viewMatrix_hdl;
    
    private int matAmbientColorHandle;
    private int matDiffuseColorHandle;
    
    //
    private Matrix4 projection;
    private Matrix4 view;
    
    public ParticleShader() {
        super("particle_vertex.glsl", "particle_fragment.glsl");
    }

    @Override
    protected void RegisterAllUniformLocations() {
        
        vertex_positions_handle = super.getAttribLocation("a_position");
        vertex_textures_handle = super.getAttribLocation("a_texcoord");
        
        diffuseTexture_hdl = super.getUniformLocation("u_texture");
        
        modelMatrix_hdl = super.getUniformLocation("u_modelMatrix");
        projMatrix_hdl = super.getUniformLocation("u_projectionMatrix");
        viewMatrix_hdl = super.getUniformLocation("u_viewMatrix");
        
        matAmbientColorHandle = super.getUniformLocation("u_material.ambientColor");
        matDiffuseColorHandle = super.getUniformLocation("u_material.diffuseColor");
    }
    
    public int getVertexPositionH(){
        return vertex_positions_handle;
    }
    public int getVertexTexturesH(){
        return vertex_textures_handle;
    }
    
    /**
     * Optional direct bind
     * @param obj 
     */
    public void BindObject(GameObject obj)
    {
        LoadMaterial(obj.getMesh().getMaterial());
        
        LoadModelMatrix(obj.getTransform().getMatrix());
    }
    
    @Override
    public void fullBind(){
        super.bind();
        super.loadMatrix(projMatrix_hdl, projection);
        super.loadMatrix(viewMatrix_hdl, view);
        
        ConnectTexturesUnits();
    }
    
    /**
     * Define each texture handle to the correct texture unit.
     */
    protected void ConnectTexturesUnits(){
        super.loadInt(diffuseTexture_hdl, TEXTURE_DIFFUSE);
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
    
    public void LoadDiffuseTexture(Texture texture){
        if (texture != null){
            loadTexture(GL3.GL_TEXTURE0+TEXTURE_DIFFUSE, texture);
        } else {
        }
    }
    
    public void LoadMaterial(Material material){
        super.loadVector4f(matAmbientColorHandle, material.ambient);
        super.loadVector4f(matDiffuseColorHandle, material.diffuse);
        
        LoadDiffuseTexture(material.texture);
    }
    
    
    /**
     * 
     * @return return the generated VAO id
     */
    public int CreateTexturedParticle(Group group)
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
        group.vbo = new int[2];
        group.vbo[0] = ModelBuilder.StoreDataInAttributeListfv(this.getVertexPositionH(), 3, vertex_buffer);
        group.vbo[1] = ModelBuilder.StoreDataInAttributeListfv(this.getVertexTexturesH(), 2, texture_buffer);
        gl.glBindVertexArray(0); // Disable our Vertex Buffer Object

        return group.vao;
    }

}
