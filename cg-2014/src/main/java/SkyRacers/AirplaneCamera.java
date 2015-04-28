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
    
    Airplane following;
    
    public AirplaneCamera(Airplane plane)
    {
        following = plane;
    }
    
    @Override
    public void DefineViewMatrix(Matrix4 viewMatrix)
    {
        Vector3 direction = following.direction.mul(-2.0f);
        Vector3 from = following.getPosition().add( direction ).add( following.top.mul(-(float)following.UDrotationCurrent/30.0f) );
        from = from.add( following.top.mul(1.07f) );
        
        Vector3 lookAt = following.getPosition().add( following.direction.mul(2) );
        
        viewMatrix.loadIdentity();
        viewMatrix.lookAt(from.x, from.y, from.z,
                        lookAt.x, lookAt.y, lookAt.z,
                        0,1,0);
                       //following.top.x/1.2f, following.top.y*1.6f, following.top.z/1.2f); 
        viewMatrix.bind();
    }
}

