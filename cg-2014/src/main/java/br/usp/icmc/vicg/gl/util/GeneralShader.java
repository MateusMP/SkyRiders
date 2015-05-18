package br.usp.icmc.vicg.gl.util;

import br.usp.icmc.vicg.gl.matrix.Matrix4;

/**
 * Shader generico
 */
public class GeneralShader extends Shader {
    
    private int vertex_positions_handle;
    private int vertex_normals_handle;
    private int vertex_textures_handle;

    //control if it is a texture or material
    private int is_texture_handle;
    private int texture_handle;
    
    // Matrices
    private int modelMatrix_hdl;
    private int projMatrix_hdl;
    public int viewMatrix_hdl;
    
    private Matrix4 projMatrix;
    private Matrix4 viewMatrix;

    public GeneralShader(String vertex, String fragment) {
        super(vertex, fragment);
        
        projMatrix = new Matrix4();
        viewMatrix = new Matrix4();
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
    }
    
    public void LoadProjView(Matrix4 proj, Matrix4 view){
        projMatrix.copyFrom(proj);
        viewMatrix.copyFrom(view);
        
        super.loadMatrix(projMatrix_hdl, proj);
        super.loadMatrix(viewMatrix_hdl, view);
    }
    
    public void LoadModelMatrix(Matrix4 model){
        super.loadMatrix(modelMatrix_hdl, model);
    }

    @Override
    public void fullbind() {
        super.bind();

//        for (int i = 0; i < 16; ++i)
//            System.out.print(viewMatrix.getMatrix()[i]+", ");
//        System.out.println();
        
        super.loadMatrix(projMatrix_hdl, projMatrix);
        super.loadMatrix(viewMatrix_hdl, viewMatrix);
    }
    
}
