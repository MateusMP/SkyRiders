/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRiders.core;

import SkyRiders.SkyRiders;
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
    private static GL3 gl;
    private static Hashtable<String, JWavefrontObject> meshes = new Hashtable<String, JWavefrontObject>();
    
    public MeshHandler(GL3 _gl) throws Exception
    {
        gl = _gl;
    }
    
    public static JWavefrontObject LoadMesh(String name, Shader shader)
    {
        JWavefrontObject mesh = meshes.get(name);
        
        if (mesh == null)
        {
            mesh = new JWavefrontObject(new File(name));
            meshes.put(name, mesh);
            PrepareMesh(mesh, shader);
        }
        
        return mesh;
    }
    
    private static void PrepareMesh(JWavefrontObject mesh, Shader shader)
    {
        try {
            //init the model
            mesh.init(gl, shader);
//             mesh.unitize();
//            mesh.dump();
        } catch (IOException ex) {
            Logger.getLogger(SkyRiders.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
