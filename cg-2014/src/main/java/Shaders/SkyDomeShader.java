package Shaders;

import br.usp.icmc.vicg.gl.jwavefront.Texture;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.util.Shader;

public class SkyDomeShader extends Shader{

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
    
    public void LoadSkyTexture(Texture texture){
        super.loadTexture(texture_handle, 0, texture);
    }
    
}
