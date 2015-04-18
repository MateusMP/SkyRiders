/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRacers;

import SkyRacers.core.GameObject;
import SkyRacers.core.Vector3;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import jogamp.graph.math.MathFloat;

public class Airplane extends GameObject {
    
    private boolean cmd_accelerating;
    private boolean cmd_up;
    private boolean cmd_down;
    private boolean cmd_right;
    private boolean cmd_left;
    
    Vector3 velocity;
    double LRrotationTarget;
    double LRrotationCurrent;
    double UDrotationTarget;
    double UDrotationCurrent;
      
    public Airplane(JWavefrontObject mesh)
    {
        super(new Vector3(-2,3,-2), mesh);
        
        velocity = new Vector3();
        
        LRrotationTarget = 0.0;
        LRrotationCurrent = 0.0;
        UDrotationTarget = 0.0;
        UDrotationCurrent = 0.0;
    }
    
    public void CommandUP(boolean pressed)
    {
        cmd_up = pressed;
    }
    
    public void CommandDOWN(boolean pressed)
    {
        cmd_down = pressed;
    }

    public void CommandLEFT(boolean pressed)
    {
        cmd_left = pressed;
    }

    public void CommandRIGHT(boolean pressed)
    {
        cmd_right = pressed;
    }
    
    @Override
    public void update()
    {
        if (cmd_up){
            UDrotationCurrent += 0.8;
        } else if (cmd_down){
            UDrotationCurrent -= 0.8;
        }
        
        if (cmd_left){
            LRrotationTarget = -60.0f;
        } else if (cmd_right) {
            LRrotationTarget = 60.0f;
        } else {
            LRrotationTarget = 0.0f;
        }
        
        
        // Rotating Left/Right
        if ( LRrotationTarget < LRrotationCurrent )
        {
            LRrotationCurrent -= 0.8;
            if ( LRrotationTarget > LRrotationCurrent ){
                LRrotationCurrent = LRrotationTarget;
            }
        }
        
        if ( LRrotationTarget > LRrotationCurrent )
        {
            LRrotationCurrent += 0.8;
            if ( LRrotationTarget < LRrotationCurrent ){
                LRrotationCurrent = LRrotationTarget;
            }
        }
        
        // Update moving direction
        double pitchRadians = Math.toRadians(UDrotationCurrent);
        double yawRadians = Math.toRadians(LRrotationTarget);
        double sinPitch = Math.sin(pitchRadians);
        double cosPitch = Math.cos(pitchRadians);
        double sinYaw = Math.sin(yawRadians);
        double cosYaw = Math.cos(yawRadians);

        Vector3 direction = new Vector3((float)(-cosPitch * sinYaw), -(float)sinPitch, (float)(-cosPitch * cosYaw));
        
        
        System.out.println(direction.toString());
        System.out.println(UDrotationCurrent +" -> "+-Math.sin(Math.toRadians(UDrotationCurrent)));
        
        setPosition(getPosition().add(direction.div(100.0f)));
        
        // Visual
        setRotationX( (float)UDrotationCurrent );
        setRotationZ( (float)LRrotationCurrent );
    }
    
}
