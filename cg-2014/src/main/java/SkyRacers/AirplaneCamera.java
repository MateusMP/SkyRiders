package SkyRacers;

import MathClasses.Vector3;
import SkyRacers.core.Camera;
import br.usp.icmc.vicg.gl.matrix.Matrix4;

public class AirplaneCamera implements Camera {
    
    Vector3 position;
    Vector3 lookat;
    Vector3 up;
    Airplane following;
    private float angle;
    private float aspect;
    private float nearDistance;
    private float farDistance;
    
    public AirplaneCamera(Airplane plane)
    {
        following = plane;
    }
    
    @Override
    public void DefineViewMatrix(Matrix4 viewMatrix)
    {
        Vector3 offset = following.forward.mul(-20.0f);
        
        position = following.getTransform().position.add( offset.add(following.up.mul(-(float)following.UDrotationCurrent/20.0f).add( following.up.mul(-Math.abs(following.LRrotationCurrent)/3)) ) );
        position = position.add(following.up.mul(15));
        
        //this.position = following.getTransform().position.add( direction ).add( following.top.mul(-(float)following.UDrotationCurrent/30.0f) ).add( following.top.mul(10.70f) );
        this.lookat = following.getTransform().position.add( following.forward.mul(30) );
        this.up = new Vector3(0, 1, 0);
        
        viewMatrix.loadIdentity();
        viewMatrix.lookAt(position.x, position.y, position.z,
                        lookat.x, lookat.y, lookat.z,
                        up.x,up.y,up.z);
                       //following.top.x/1.2f, following.top.y*1.6f, following.top.z/1.2f); 
        viewMatrix.bind();
    }
    
    public void DefineProjectionMatrix(Matrix4 projectionMatrix, float angle, float aspect, float nearDistance, float farDistance)
    {
        this.angle = angle;
        this.aspect = aspect;
        this.nearDistance = nearDistance;
        this.farDistance = farDistance;
        projectionMatrix.loadIdentity();
        projectionMatrix.perspective(angle, aspect, nearDistance, farDistance);
        projectionMatrix.bind();
        
    }

    @Override
    public Vector3 GetPosition() {
        return position;
    }
    @Override
    public float getAngle() {
        return angle;
    }
    @Override
    public float getAspect() {
        return aspect;
    }
    @Override
    public float getNearDistance() {
        return nearDistance;
    }
    @Override
    public float getFarDistance() {
        return farDistance;
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

