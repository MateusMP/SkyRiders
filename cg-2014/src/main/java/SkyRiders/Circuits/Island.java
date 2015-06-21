
package SkyRiders.Circuits;

import SkyRiders.core.Map;
import Handlers.ShaderHandler;
import MathClasses.Transform;
import MathClasses.Vector3;
import SkyRiders.SmokeParticle;
import SkyRiders.core.GameRenderer;
import SkyRiders.SunParticle;
import br.usp.icmc.vicg.gl.core.Light;
import java.util.ArrayList;
import javax.media.opengl.GL3;

public class Island extends Map{
    
    Vector3 wind;
    Light light;
    int timestamp;
    float moveFactor;
    final float moveFactorIncrement = 0.0005f;
    
    SunParticle sunParticle;
    
    public Island(GL3 gl)
    {
        super();
        
        wind = new Vector3(1, 0, 0);
        timestamp = 0;
        
        InitLights();
        
        Transform t = new Transform();
        t.position.x = -38*100;
        t.position.y = 13.5f*10;
        t.position.z = 26*100;
        t.scale = t.scale.mul(5000);
        t.Invalidate();
        
        
//        sunObj = new SunParticle( t );

        sunParticle = new SunParticle(t);
        GameRenderer.AddParticle(sunParticle);
        
        ArrayList<SmokeParticle> particles = new ArrayList<SmokeParticle>();
        for (int i = 0; i < 10; ++i){
            for (int j = 0; j < 10; ++j){
                Transform transform = new Transform();
                transform.scale = new Vector3(30, 30, 30);
                transform.position = new Vector3( 5 + 50*i , 30, 10 + 50*j);
                SmokeParticle p = SmokeParticle.CreateSmokeParticle( transform );
                particles.add(p);
                
                GameRenderer.AddParticle(p);
            }
        }
        
        
    }
   
    private void InitLights()
    {
        light = new Light();
     
        //init the light
        light.setPosition(new float[]{-38*100, 13.5f*100, -26*100, 1.0f});
        light.setAmbientColor(new float[]{0.6f, 0.6f, 0.6f, 1.0f});
        light.setDiffuseColor(new float[]{0.9f, 0.9f, 0.9f, 1.0f});
        light.setSpecularColor(new float[]{0.7f, 0.7f, 0.7f, 1.0f});
    }
    
    @Override
    public void update()
    {
//        sunobj.getTransform().rotation.y += 0.2f;
//        sunobj.getTransform().rotation.x -= 0.1f;
//        sunobj.getTransform().Invalidate();
        
        timestamp += 1;
        
        ShaderHandler.generalShader.LoadSunLight(light);
        ShaderHandler.waterShader.LoadSunLight(light);
        
        ShaderHandler.foliageShader.LoadSunLight(light);
        ShaderHandler.foliageShader.LoadWindDirection(wind);
        ShaderHandler.foliageShader.LoadTimeStamp(timestamp);
        
        this.moveFactor += this.moveFactorIncrement;
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
