/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Handlers;

import Shaders.FoliageShader;
import Shaders.GeneralShader;
import Shaders.SkyDomeShader;
import Shaders.WaterShader;
import SkyRiders.SkyRiders;
import br.usp.icmc.vicg.gl.jwavefront.Group;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.util.Shader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL3;

public class MeshHandler {
    
    private static MeshHandler instance = null;
    private static GL3 gl;
    private static final HashMap<String, JWavefrontObject> meshes = new HashMap<String, JWavefrontObject>();
    
    public MeshHandler(GL3 _gl) throws Exception
    {
        gl = _gl;
    }
    
    public static Group LoadMesh(String name, String modelName, Shader shader)
    {
        JWavefrontObject mesh = meshes.get(name);
        
        if (mesh == null)
        {
            mesh = new JWavefrontObject(new File(name));
            meshes.put(name, mesh);
            PrepareMesh(mesh, shader);
        }
        
        if (mesh.getGroups().isEmpty())
            return null;
        
        if (modelName == null){
            return mesh.getGroups().get(0);
        }
        
        return mesh.findGroup(modelName);
    }
    
    private static void PrepareMesh(JWavefrontObject mesh, Shader shader)
    {
        try {
            //init the model
            mesh.init(gl);
            
            if (shader instanceof GeneralShader)
            {
                for (Group g : mesh.getGroups()){
                    ShaderHandler.generalShader.CreateTexturedObject(g);
                }
            } else if ( shader instanceof FoliageShader ) {
                for (Group g : mesh.getGroups()){
                    ShaderHandler.foliageShader.CreateTexturedObject(g);
                }
            }
            else if (shader instanceof SkyDomeShader){
                for (Group g : mesh.getGroups()){
                    ShaderHandler.skyDomeShader.CreateSkyObject(g);
                }
            }
            else if (shader instanceof WaterShader){
                for (Group g : mesh.getGroups()){
                    ShaderHandler.waterShader.CreateTexturedObject(g);
                }
            }
            
//             mesh.unitize();
//            mesh.dump();
        } catch (IOException ex) {
            Logger.getLogger(SkyRiders.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
