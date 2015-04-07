package SkyRacers.core;

import static SkyRacers.SkyRacers.modelMatrix;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;

public class GameObject {
    
    private JWavefrontObject model;
    private Vector3 rotation;
    private Vector3 position;
    
    
    public GameObject(JWavefrontObject model3D)
    {
        model = model3D;
        rotation = new Vector3(0, 0, 0);
        position = new Vector3(0, 0, 0);
    }
    
    public void update()
    {
        
    }
    
    public void draw()
    {
        modelMatrix.loadIdentity();
        modelMatrix.translate(position.x(), position.y(), position.z());
        modelMatrix.rotate(rotation.z(), 0, 0, 1.0f);
        modelMatrix.rotate(rotation.y(), 0, 1.0f, 0);
        modelMatrix.rotate(rotation.x(), 1.0f, 0, 0);
        // modelMatrix.scale(5, 5, 5);
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
        this.rotation.xyz[0] = rotation;
    }
    public void setRotationY(float rotation) {
        this.rotation.xyz[1] = rotation;
    }
    public void setRotationZ(float rotation) {
        this.rotation.xyz[2] = rotation;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }
}
