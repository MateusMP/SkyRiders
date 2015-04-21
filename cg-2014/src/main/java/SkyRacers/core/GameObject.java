package SkyRacers.core;

import MathClasses.Vector3;
import static SkyRacers.SkyRacers.modelMatrix;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;

public class GameObject {
    
    protected JWavefrontObject model;
    protected Vector3 rotation;
    protected Vector3 position;
    protected Vector3 scale;
    
    
    public GameObject(Vector3 pos, JWavefrontObject model3D)
    {
        model = model3D;
        rotation = new Vector3(0, 0, 0);
        position = pos;
        scale = new Vector3(1,1,1);
    }
    
    public GameObject(Vector3 pos, Vector3 scale, JWavefrontObject model3D)
    {
        model = model3D;
        rotation = new Vector3(0, 0, 0);
        position = pos;
        this.scale = scale;
    }
    
    public void update()
    {
        
    }
    
    public void draw()
    {
        modelMatrix.loadIdentity();
        modelMatrix.translate(position.x(), position.y(), position.z());
        modelMatrix.rotate(rotation.x(), 1.0f, 0, 0);
        modelMatrix.rotate(rotation.y(), 0, 1.0f, 0);
        modelMatrix.rotate(rotation.z(), 0, 0, 1.0f);
        modelMatrix.scale(scale.x, scale.y, scale.z);
        modelMatrix.bind();
        
        model.draw();
    }
    
    public Vector3 getRotation() {
        return rotation;
    }

    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
    }
    
    public void setRotationX(float rotation) {
        this.rotation.x = rotation;
    }
    public void setRotationY(float rotation) {
        this.rotation.y = rotation;
    }
    public void setRotationZ(float rotation) {
        this.rotation.z = rotation;
    }
    
    public void setScale(float x, float y, float z){
        scale.x = x;
        scale.y = y;
        scale.z = z;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }
}
