package Shaders;

import Handlers.ShaderHandler;
import static Handlers.ShaderHandler.waterShader;
import SkyRiders.core.GameObject;
import SkyRiders.core.ModelBuilder;
import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.Material;
import br.usp.icmc.vicg.gl.jwavefront.Texture;
import br.usp.icmc.vicg.gl.jwavefront.Triangle;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.util.Shader;
import javax.media.opengl.GL3;

/**
 * Shader de agua
 */
public class WaterShader extends Shader {
    
    private static final int TEXTURE_DIFFUSE = 0;
    private static final int TEXTURE_DUDV = 1;
    
    private int vertex_positions_handle;
    private int vertex_normals_handle;
    private int vertex_textures_handle;
    
    //control if it is a texture or material
    private int is_texture_handle;
    private int diffuseTexture_hdl;
    private int dudvTexture_hdl;
    
    // Matrices
    private int modelMatrix_hdl;
    private int projMatrix_hdl;
    public int viewMatrix_hdl;
    
    // Light
    private int sunPositionHandle;
    private int sunAmbientColorHandle;
    private int sunDiffuseColorHandle;
    private int sunSpeclarColorHandle;
    
    private int matAmbientColorHandle;
    private int matDiffuseColorHandle;
    private int matSpecularColorHandle;
    private int matSpecularExponentHandle;
    private int moveFactorHandle;
    //
    private Matrix4 projection;
    private Matrix4 view;
    private Light sun;
    private float moveFactor;
    
    public WaterShader() {
        super("water_vertex.glsl", "water_fragment.glsl");
    }

    @Override
    protected void RegisterAllUniformLocations() {
        
        vertex_positions_handle = super.getAttribLocation("a_position");
        vertex_normals_handle = super.getAttribLocation("a_normal");
        vertex_textures_handle = super.getAttribLocation("a_texcoord");
        
        is_texture_handle = super.getUniformLocation("u_is_texture");
        diffuseTexture_hdl = super.getUniformLocation("u_texture");
        dudvTexture_hdl = super.getUniformLocation("u_dudv");
        
        modelMatrix_hdl = super.getUniformLocation("u_modelMatrix");
        projMatrix_hdl = super.getUniformLocation("u_projectionMatrix");
        viewMatrix_hdl = super.getUniformLocation("u_viewMatrix");
        
        sunPositionHandle = super.getUniformLocation("u_light.position");
        sunAmbientColorHandle = super.getUniformLocation("u_light.ambientColor");
        sunDiffuseColorHandle = super.getUniformLocation("u_light.diffuseColor");
        sunSpeclarColorHandle = super.getUniformLocation("u_light.specularColor");
        
        matAmbientColorHandle = super.getUniformLocation("u_material.ambientColor");
        matDiffuseColorHandle = super.getUniformLocation("u_material.diffuseColor");
        matSpecularColorHandle = super.getUniformLocation("u_material.specularColor");
        matSpecularExponentHandle = super.getUniformLocation("u_material.specularExponent");
        moveFactorHandle = super.getUniformLocation("move_factor");
        
    }
    
    public int getVertexPositionH(){
        return vertex_positions_handle;
    }
    public int getVertexNormalsH(){
        return vertex_normals_handle;
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
        LoadMoveFactor(this.moveFactor);
        LoadSunLight(sun);
        
        ConnectTexturesUnits();
        
        LoadDudvTexture(ShaderHandler.dudv_texture);
    }
    
    /**
     * Define each texture handle to the correct texture unit.
     */
    protected void ConnectTexturesUnits(){
        super.loadInt(diffuseTexture_hdl, TEXTURE_DIFFUSE);
        super.loadInt(dudvTexture_hdl, TEXTURE_DUDV);
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
    
    public void LoadSunLight(Light l){
        sun = l;
        
        if (l != null){
            super.loadVector4f(sunPositionHandle, l.getPosition());
            super.loadVector4f(sunAmbientColorHandle, l.getAmbientColor());
            super.loadVector4f(sunDiffuseColorHandle, l.getDiffuseColor());
            super.loadVector4f(sunSpeclarColorHandle, l.getSpecularColor());
        }
    }
    
    public void LoadDiffuseTexture(Texture texture){
        if (texture != null){
            loadBoolean(is_texture_handle, true);
            loadTexture(GL3.GL_TEXTURE0+TEXTURE_DIFFUSE, texture);
        } else {
            loadBoolean(is_texture_handle, false);
        }
    }
    
    public void LoadDudvTexture(Texture texture){
        if (texture != null){
            loadTexture(GL3.GL_TEXTURE0+TEXTURE_DUDV, texture);
        }
    }
    
    public void LoadMaterial(Material material){
        super.loadVector4f(matAmbientColorHandle, material.ambient);
        super.loadVector4f(matDiffuseColorHandle, material.diffuse);
        super.loadVector4f(matSpecularColorHandle, material.specular);
        super.loadFloat(matSpecularExponentHandle, material.shininess);
        
        LoadDiffuseTexture(material.texture);
    }
    
    public void LoadMoveFactor(float factor)
    {
        super.loadFloat(this.moveFactorHandle, factor);
        this.moveFactor = factor;
    }
    
    /**
     * 
     * @return return the generated VAO id
     */
    public int CreateTexturedObject(Group group)
    {
        if (group.triangles.isEmpty()) {
          return -1;
        }

        float[] vertex_buffer = new float[9 * group.triangles.size()];
        float[] normal_buffer = new float[9 * group.triangles.size()];
        float[] texture_buffer = new float[6 * group.triangles.size()];
        float[] tangent_buffer = new float[9 * group.triangles.size()];

        for (int j = 0; j < group.triangles.size(); j++) {
          Triangle triangle = group.triangles.get(j);

          for (int k = 0; k < 3; k++) {
            vertex_buffer[(9 * j) + (3 * k)] = triangle.vertices[k].x;
            vertex_buffer[(9 * j) + (3 * k) + 1] = triangle.vertices[k].y;
            vertex_buffer[(9 * j) + (3 * k) + 2] = triangle.vertices[k].z;

            normal_buffer[(9 * j) + (3 * k)] = triangle.vertex_normals[k].x;
            normal_buffer[(9 * j) + (3 * k) + 1] = triangle.vertex_normals[k].y;
            normal_buffer[(9 * j) + (3 * k) + 2] = triangle.vertex_normals[k].z;
            
            tangent_buffer[(9 * j) + (3 * k)] = triangle.tangents[k].x;
            tangent_buffer[(9 * j) + (3 * k) + 1] = triangle.tangents[k].y;
            tangent_buffer[(9 * j) + (3 * k) + 2] = triangle.tangents[k].z;

            if (triangle.vertex_tex_coords[k] != null) {
              texture_buffer[(6 * j) + (2 * k)] = triangle.vertex_tex_coords[k].u;
              texture_buffer[(6 * j) + (2 * k) + 1] = triangle.vertex_tex_coords[k].v;
            }
          }
        }
        
        group.vao = ModelBuilder.CreateVAO();
        group.vbo = new int[4];
        group.vbo[0] = ModelBuilder.StoreDataInAttributeListfv(this.getVertexPositionH(), 3, vertex_buffer);
        group.vbo[1] = ModelBuilder.StoreDataInAttributeListfv(this.getVertexNormalsH(), 3, normal_buffer);
        if (group.material.texture != null) {
            group.vbo[2] = ModelBuilder.StoreDataInAttributeListfv(this.getVertexTexturesH(), 2, texture_buffer);
        }
        gl.glBindVertexArray(0); // Disable our Vertex Buffer Object

        return group.vao;
    }

    /**
     * 
     */
    public int CreateTile()
    {
        float[] vertex_buffer = new float[]{
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f};

        float[] normal_buffer = new float[]{
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f};

        float[] texture_buffer = new float[]{
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f};
        int VAO = ModelBuilder.CreateVAO();
        int VBO[] = new int[3];
        
        VBO[0] = ModelBuilder.StoreDataInAttributeListfv(this.getVertexPositionH(), 3, vertex_buffer);
        VBO[1] = ModelBuilder.StoreDataInAttributeListfv(this.getVertexNormalsH(), 3, normal_buffer);
        VBO[2] = ModelBuilder.StoreDataInAttributeListfv(this.getVertexTexturesH(), 2, texture_buffer);

        gl.glBindVertexArray(0);
        return VAO;
    }
}
