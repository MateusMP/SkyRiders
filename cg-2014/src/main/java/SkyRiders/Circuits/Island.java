
package SkyRiders.Circuits;

import SkyRiders.core.Map;
import Handlers.ShaderHandler;
import br.usp.icmc.vicg.gl.core.Light;
import javax.media.opengl.GL3;

public class Island extends Map{
    
    Light light;
    float x, y;
        
    public Island(GL3 gl)
    {
        super();
        
        x = 0;
        y = 0;
        
        InitLights();
    }
   
    private void InitLights()
    {
        light = new Light();
     
        //init the light
        light.setPosition(new float[]{0, 90, 0, 1.0f});
        light.setAmbientColor(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
        light.setDiffuseColor(new float[]{0.75f, 0.75f, 0.75f, 1.0f});
        light.setSpecularColor(new float[]{0.7f, 0.7f, 0.7f, 1.0f});
    }
    
    @Override
    public void update()
    {
        double rad = x * 3.14159 / 180.0;
        
        y = (float) (Math.sin( rad )*400);
        x += 0.5;
        
        light.setPosition(new float[]{0.0f, 90, y, 0.0f});
        ShaderHandler.generalShader.LoadSunLight(light);
        
        super.update();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
}
