/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRacers;

import MathClasses.Vector3;
import SkyRacers.core.Camera;
import br.usp.icmc.vicg.gl.matrix.Matrix4;

/**
 *
 * @author Mateus
 */
public class AirplaneCamera implements Camera {
    
    Vector3 position;
    Vector3 lookat;
    Airplane following;
    
    public AirplaneCamera(Airplane plane)
    {
        following = plane;
    }
    
    @Override
    public void DefineViewMatrix(Matrix4 viewMatrix)
    {
        Vector3 direction = following.direction.mul(-20.0f);
        
        position = following.getTransform().position.add( direction ).add( following.top.mul(-(float)following.UDrotationCurrent/30.0f) ).add( following.top.mul(10.70f) );
        lookat = following.getTransform().position.add( following.direction.mul(20) );
        
        viewMatrix.loadIdentity();
        viewMatrix.lookAt(position.x, position.y, position.z,
                        lookat.x, lookat.y, lookat.z,
                        0,1,0);
                       //following.top.x/1.2f, following.top.y*1.6f, following.top.z/1.2f); 
        viewMatrix.bind();
    }

    @Override
    public Vector3 GetPosition() {
        return position;
    }
}

