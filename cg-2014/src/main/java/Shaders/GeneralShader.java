package Shaders;

import SkyRiders.core.GameObject;
import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.jwavefront.Material;
import br.usp.icmc.vicg.gl.jwavefront.Texture;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.util.Shader;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;

/**
 * Shader generico
 */
public class GeneralShader extends Shader {
    
    private static final int TEXTURE_DIFFUSE = 0;
    
    private int vertex_positions_handle;
    private int vertex_normals_handle;
    private int vertex_textures_handle;

    //control if it is a texture or material
    private int is_texture_handle;
    private int diffuseTexture_hdl;
   
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
    
    //
    private Matrix4 projection;
    private Matrix4 view;
    private Light sun;
    
    public GeneralShader(String vertex, String fragment) {
        super(vertex, fragment);
    }

    @Override
    protected void RegisterAllUniformLocations() {
        
        vertex_positions_handle = super.getAttribLocation("a_position");
        vertex_normals_handle = super.getAttribLocation("a_normal");
        vertex_textures_handle = super.getAttribLocation("a_texcoord");
        
        is_texture_handle = super.getUniformLocation("u_is_texture");
        diffuseTexture_hdl = super.getUniformLocation("u_texture");
        
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
        LoadDiffuseTexture(obj.getMesh().getTexture());
        
        LoadModelMatrix(obj.getTransform().getMatrix());
    }
    
    @Override
    public void fullBind(){
        super.bind();
        super.loadMatrix(projMatrix_hdl, projection);
        super.loadMatrix(viewMatrix_hdl, view);
        LoadSunLight(sun);
        
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
    
    public void LoadMaterial(Material material){
        super.loadVector4f(matAmbientColorHandle, material.ambient);
        super.loadVector4f(matDiffuseColorHandle, material.diffuse);
        super.loadVector4f(matSpecularColorHandle, material.specular);
        super.loadFloat(matSpecularExponentHandle, material.shininess);
    }

}
