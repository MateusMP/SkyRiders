package Shaders;

import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.util.Shader;

public class SkyDomeShader extends Shader{

    private static final int DIFFUSE_ID = 0;
    
    private int vertex_positions_handle;
    private int vertex_normals_handle;
    private int vertex_textures_handle;

    private int is_texture_handle;
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
        vertex_normals_handle = super.getAttribLocation("a_normal");
        vertex_textures_handle = super.getAttribLocation("a_texcoord");
        
        is_texture_handle = super.getUniformLocation("u_is_texture");
        texture_handle = super.getUniformLocation("u_texture");
        
        modelMatrix_hdl = super.getUniformLocation("u_modelMatrix");
        projMatrix_hdl = super.getUniformLocation("u_projectionMatrix");
        viewMatrix_hdl = super.getUniformLocation("u_viewMatrix");
        
        System.out.println("a_pos"+vertex_positions_handle);
        System.out.println("a_norm"+vertex_normals_handle);
        System.out.println("a_text"+vertex_textures_handle);
        System.out.println("is_text"+is_texture_handle);
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
    
}
