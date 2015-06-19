
package SkyRiders.Circuits;

import SkyRiders.core.Map;
import Handlers.ShaderHandler;
import MathClasses.Vector3;
import br.usp.icmc.vicg.gl.core.Light;
import javax.media.opengl.GL3;

public class Island extends Map{
    
    Vector3 wind;
    Light light;
    int timestamp;
    float moveFactor;
    final float moveFactorIncrement = 0.0005f;
        
    public Island(GL3 gl)
    {
        super();
        
        wind = new Vector3(1, 0, 0);
        timestamp = 0;
        
        InitLights();
    }
   
    private void InitLights()
    {
        light = new Light();
     
        //init the light
        light.setPosition(new float[]{0, 900, 0, 1.0f});
        light.setAmbientColor(new float[]{0.4f, 0.4f, 0.4f, 1.0f});
        light.setDiffuseColor(new float[]{0.9f, 0.9f, 0.9f, 1.0f});
        light.setSpecularColor(new float[]{0.7f, 0.7f, 0.7f, 1.0f});
    }
    
    @Override
    public void update()
    {
        timestamp += 1;
        
        ShaderHandler.generalShader.LoadSunLight(light);
        ShaderHandler.waterShader.LoadSunLight(light);
        
        ShaderHandler.foliageShader.LoadSunLight(light);
        ShaderHandler.foliageShader.LoadWindDirection(wind);
        ShaderHandler.foliageShader.LoadTimeStamp(timestamp);
        
        this.moveFactor+=this.moveFactorIncrement;
        if(this.moveFactor > 1)
        {
            this.moveFactor-=1;
        }
        ShaderHandler.waterShader.LoadMoveFactor(this.moveFactor);
        
        super.update();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
}
