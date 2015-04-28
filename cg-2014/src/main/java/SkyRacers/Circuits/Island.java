
package SkyRacers.Circuits;

import SkyRacers.Airplane;
import SkyRacers.AirplaneController;
import SkyRacers.SkyRacers;
import SkyRacers.core.GameObject;
import SkyRacers.core.Map;
import SkyRacers.core.MeshHandler;
import MathClasses.Vector3;
import SkyRacers.AirplaneCamera;
import SkyRacers.core.Camera;
import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.util.Shader;
import javax.media.opengl.GL3;


public class Island extends Map{
    
    private final GL3 gl;
    private final Shader shader;
    
    // Meshes
    JWavefrontObject meshPalmTree1;
    JWavefrontObject meshIsland;
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
        
        for (int j = 0; j < 25; ++j){
            for (int i = 0; i < 25; ++i){
                addObject( new GameObject(new Vector3(i*-6, 0, j*-6), meshPalmTree1) );
            }
        }
        
        meshIsland = MeshHandler.hdl().LoadMesh("./data/graphics/island.obj");
        addObject(new GameObject(new Vector3(), new Vector3(200,200,200), meshIsland));
        
        // Create player airplane and define a controller for it
        Airplane plane = new Airplane(MeshHandler.hdl().LoadMesh("./data/graphics/cartoonAriplane.obj"));
        controller = new AirplaneController(plane);
        SkyRacers.inputHandler.AddHandler(controller);
        addObject(plane);
        
        
        
        // Set camera fo the player airplane
        Camera cam = new AirplaneCamera(plane);
        SkyRacers.hdl().setCurrentCamera(cam);
        
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
