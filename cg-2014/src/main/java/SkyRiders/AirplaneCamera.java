package SkyRiders;

import MathClasses.Vector3;
import SkyRiders.core.Camera;
import br.usp.icmc.vicg.gl.matrix.Matrix4;

public class AirplaneCamera implements Camera {
    
    Vector3 position;
    Vector3 lookat_dest;
    Vector3 lookat;
    Vector3 up;
    Airplane following;
    private Vector3 position_dest;
   
    public AirplaneCamera(Airplane plane)
    {
        following = plane;
        lookat = new Vector3(0,0,0);
        position = plane.getTransform().position.clone();
        up = new Vector3(0, 1, 0);
    }
    
    @Override
    public void DefineViewMatrix(Matrix4 viewMatrix)
    {
        // Offset behind the airplane
        Vector3 offset = following.forward.mul(-18.0f).add(following.up.mul(2));
        
        // Camera position behind the plane
        position_dest = following.getTransform().position.add( offset );
        
        // Look at plane direction
        lookat = following.getTransform().position.add( following.forward.mul(20) );
        
        // Slowly move to final position
        Vector3 diff = position_dest.sub(position);
        position = position.add( diff.mul( 0.02f * diff.norm()/3.0f ) );
        
        // Set view matrix
        viewMatrix.loadIdentity();
        viewMatrix.lookAt( position.x, position.y, position.z,
                           lookat.x, lookat.y, lookat.z,
                           up.x, up.y, up.z);
        //viewMatrix.bind();
    }
    

    @Override
    public Vector3 GetPosition() {
        return position;
    }
    @Override
    public void SetPosition(Vector3 pos){
        position = pos;
    }
    @Override
    public Vector3 getLookat() {
        return lookat;
    }
    @Override
    public Vector3 getUp() {
        return up;
    }
    
}

