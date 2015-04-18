/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRacers.core;

import SkyRacers.SkyRacers;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.util.Shader;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL3;

/**
 *
 * @author Mateus
 */
public class MeshHandler {
    
    private static MeshHandler instance = null;
    private GL3 gl;
    private Shader shader;
    private Hashtable<String, JWavefrontObject> meshes = new Hashtable<String, JWavefrontObject>();
    
    public MeshHandler(GL3 gl, Shader shader) throws Exception
    {
        this.gl = gl;
        this.shader = shader;
        if (instance == null){
            instance = this;
        }
        else
        {
            throw new Exception("ERROR: Just one MeshHandler should be created!");
        }
    }
    
    public static MeshHandler hdl()
    {
        return instance;
    }
    
    public JWavefrontObject LoadMesh(String name)
    {
        JWavefrontObject mesh = meshes.get(name);
        
        if (mesh == null)
        {
            mesh = new JWavefrontObject(new File(name));
            PrepareMesh(mesh);
        } 
        
        return mesh;
    }
    
    private void PrepareMesh(JWavefrontObject mesh)
    {
        try {
            //init the model
            mesh.init(gl, shader);
            mesh.unitize();
            mesh.dump();
        } catch (IOException ex) {
            Logger.getLogger(SkyRacers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
