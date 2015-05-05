
package SkyRacers.Circuits;

import SkyRacers.Airplane;
import SkyRacers.AirplaneController;
import SkyRacers.SkyRacers;
import SkyRacers.core.Map;
import SkyRacers.core.MeshHandler;
import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.util.Shader;
import javax.media.opengl.GL3;


public class Island extends Map{
    
    private final GL3 gl;
    private final Shader shader;
    
    // Meshes
    //JWavefrontObject meshPalmTree1;
    //JWavefrontObject meshIsland;
    Light light;
    //
    float x, y;
        
    public Island(GL3 gl, Shader shader)
    {
        super();
        
        this.gl = gl;
        this.shader = shader;
        
        x = 0;
        y = 0;
        
        LoadMeshes();
        InitLights();
        
        /*for (int j = 0; j < 25; ++j){
            for (int i = 0; i < 25; ++i){
                addObject( new GameObject(new Vector3(i*-6, 0, j*-6), meshPalmTree1) );
            }
        }
        
        meshIsland = MeshHandler.hdl().LoadMesh("./data/graphics/island.obj");
        addObject(new GameObject(new Vector3(), new Vector3(150,150,150), meshIsland));*/
        
    }
    
    private void InitLights()
    {
        light = new Light();
        
        //init the light
        light.setPosition(new float[]{0, 20, 0, 1.0f});
        light.setAmbientColor(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
        light.setDiffuseColor(new float[]{0.75f, 0.75f, 0.75f, 1.0f});
        light.setSpecularColor(new float[]{0.7f, 0.7f, 0.7f, 1.0f});
        light.init(gl, shader);
        
        light.bind();
    }
    
    private void LoadMeshes()
    {
        // meshPalmTree1 = MeshHandler.hdl().LoadMesh("./Assets/graphics/coqueiro.obj");
        
    }
    
    public void dispose()
    {
        // meshPalmTree1.dispose();
        super.dispose();
    }
    
    @Override
    public void draw()
    {
        double rad = x * 3.14159 / 180.0;
        
        y = (float) (Math.sin( rad )*500);
        x += 0.5;
        
        light.setPosition(new float[]{0.0f, 60, y, 0.0f});
        light.bind();
        
        super.draw();
    }
}
