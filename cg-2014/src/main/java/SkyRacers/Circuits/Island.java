
package SkyRacers.Circuits;

import SkyRacers.Airplane;
import SkyRacers.AirplaneController;
import SkyRacers.SkyRacers;
import SkyRacers.core.GameObject;
import SkyRacers.core.Map;
import SkyRacers.core.MeshHandler;
import SkyRacers.core.Vector3;
import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.util.Shader;
import javax.media.opengl.GL3;


public class Island extends Map{
    
    private GL3 gl;
    private Shader shader;
    
    // Meshes
    JWavefrontObject meshPalmTree1;
    Light light;
    //
    
    AirplaneController controller;
    
    public Island(GL3 gl, Shader shader)
    {
        super();
        
        this.gl = gl;
        this.shader = shader;
        
        LoadMeshes();
        InitLights();
        
        for (int j = 0; j < 10; ++j){
            for (int i = 0; i < 10; ++i){
                addObject( new GameObject(new Vector3(i*-1.5f, 0, j*-2), meshPalmTree1) );
            }
        }
        
        Airplane plane = new Airplane(MeshHandler.hdl().LoadMesh("./data/graphics/cartoonAriplane.obj"));
        controller = new AirplaneController(plane);
        SkyRacers.inputHandler.AddHandler(controller);
        addObject(plane);
        
        /*
        plane.setRotationX(alpha);
        plane.setRotationY(beta);
        plane.draw();
        */
        
    }
    
    private void InitLights()
    {
        light = new Light();
        
        //init the light
        light.setPosition(new float[]{10, 10, 20, 1.0f});
        light.setAmbientColor(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
        light.setDiffuseColor(new float[]{0.75f, 0.75f, 0.75f, 1.0f});
        light.setSpecularColor(new float[]{0.7f, 0.7f, 0.7f, 1.0f});
        light.init(gl, shader);
        
        light.bind();
    }
    
    private void LoadMeshes()
    {
        meshPalmTree1 = MeshHandler.hdl().LoadMesh("./data/graphics/coqueiro.obj");
        
    }
    
    public void dispose()
    {
        SkyRacers.inputHandler.RemoveHandler(controller);
        meshPalmTree1.dispose();
        super.dispose();
    }
}
