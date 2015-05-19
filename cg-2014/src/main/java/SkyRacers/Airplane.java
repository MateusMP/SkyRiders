/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRacers;

import MathClasses.Transform;
import SkyRacers.core.GameObject;
import MathClasses.Vector3;
import static SkyRacers.SkyRacers.modelMatrix;
import SkyRacers.core.GameRenderer.RENDER_TYPE;
import SkyRacers.core.LODMesh;
import SkyRacers.core.Line;
import SkyRacers.core.MeshHandler;
import SkyRacers.core.MeshRenderer;
import SkyRacers.core.ObjMesh;
import br.usp.icmc.vicg.gl.matrix.Matrix4;

public class Airplane extends GameObject {
    
    public final Vector3 UP = new Vector3(0,1,0);
    public final Vector3 FORWARD = new Vector3(0,0,1);
    public final Vector3 RIGHT = new Vector3(1,0,0);
    public final Vector3 GRAVITY = new Vector3(0, -9.81f, 0);
    
    // Comands
    private boolean cmd_up;
    private boolean cmd_down;
    private boolean cmd_right;
    private boolean cmd_left;
    private boolean cmd_accel;
    private boolean cmd_brake;
    
    // Propeller
    private ObjMesh om;
    private LODMesh lm;
    private float rotationCurrent;
    private float rotationMax;
    
    // Movement
    Vector3 acceleration;
    Vector3 velocity;
    Vector3 friction;
    
    private float current_accel;
    private final float accelMax_normal;
    private final float air_friction;
    
    public Vector3 up;
    public Vector3 forward;
    public Vector3 right;
    
    private final Vector3 initialRot;
    
    // Rotation
    float LRrotationTarget;
    float LRrotationCurrent;
    float UDrotationCurrent;
    float roationXZ;
    float rollSpeed;
    float pitchSpeed;
    
    // Limits
    final float MAX_UD = 70; // Direcao vertical
    final float MAX_LR = 70; // Direcao horizontal
    final float MAX_SPEED = 6;
    
    public Airplane(Transform t, MeshRenderer mesh)
    {
        super(t, mesh);
        
        cmd_up = false;
        cmd_down = false;
        cmd_right = false;
        cmd_left = false;
        cmd_accel = false;
        cmd_brake = false;
        
        acceleration = new Vector3();
        velocity = new Vector3();
        friction = new Vector3();
        
        accelMax_normal = 100.0f;
        current_accel = 0;
        air_friction = 0.03f;
        
        up = UP;
        forward = FORWARD;
        right = RIGHT;
        
        initialRot = t.rotation.clone();
        
        om = new ObjMesh(MeshHandler.hdl().LoadMesh("./Assets/graphics/cartoonAriplanePropeller.obj"), "");
        lm = new LODMesh(om);
                
        LRrotationTarget = 0.0f;
        LRrotationCurrent = 0.0f;
        UDrotationCurrent = 0.0f;
        pitchSpeed = 0.7f;
        rollSpeed = 1.1f;
        
        roationXZ = 0;
        
        rendermode = RENDER_TYPE.RENDER_SOLID;
    }
    
    public void CommandUP(boolean pressed){
        cmd_up = pressed;
    }
    public void CommandDOWN(boolean pressed){
        cmd_down = pressed;
    }
    public void CommandLEFT(boolean pressed){
        cmd_left = pressed;
    }
    public void CommandRIGHT(boolean pressed){
        cmd_right = pressed;
    }
    public void CommandAccel(boolean pressed){
        cmd_accel = pressed;
    }
    public void CommandBrake(boolean pressed){
        cmd_brake = pressed;
    }
    
    private void handleRotation()
    {
        if (cmd_up){
            UDrotationCurrent += pitchSpeed;
            if (UDrotationCurrent > MAX_UD)
                UDrotationCurrent = MAX_UD;
        } else if (cmd_down){
            UDrotationCurrent -= pitchSpeed;
            if (UDrotationCurrent < -MAX_UD)
                UDrotationCurrent = -MAX_UD;
            
            acceleration = acceleration.add( up.mul(current_accel/10.0f) );
        }
       
        if (cmd_left){
            LRrotationTarget = -MAX_LR;
            roationXZ += rollSpeed;
        } else if (cmd_right) {
            LRrotationTarget = MAX_LR;
            roationXZ -= rollSpeed;
        } else {
            LRrotationTarget = 0.0f;
        }        
        
        // Rotating Left/Right
        if ( LRrotationTarget < LRrotationCurrent )
        {
            LRrotationCurrent -= rollSpeed;
            if ( LRrotationTarget > LRrotationCurrent ){
                LRrotationCurrent = LRrotationTarget;
            }
        }
        else if ( LRrotationTarget > LRrotationCurrent )
        {
            LRrotationCurrent += rollSpeed;
            if ( LRrotationTarget < LRrotationCurrent ){
                LRrotationCurrent = LRrotationTarget;
            }
        }
        
    }
    
    private void handleAcceleration()
    {
        if (cmd_accel){
            if (current_accel < accelMax_normal)
                current_accel += 1;            
        } else if (cmd_brake){
            current_accel /= 1.3;
        } else {
            current_accel /= 1.1;
        }
               
        if ( current_accel < 0.01f )
            current_accel = 0.0f;

        friction = velocity.mul(velocity.norm() * air_friction);
        acceleration = acceleration.add(forward.mul(current_accel));
    }
    
    
    @Override
    public void update()
    {
    final float h = 0.016f;
    
        friction.set(0, 0, 0);
        acceleration.set(0, 0, 0);
        
        handleRotation();
        handleAcceleration();
       
        // Matrix to calculate new rotation
        Matrix4 mx = new Matrix4();
        mx.loadIdentity();
        
        // Update transform rotation values
        transform.rotation.x = (float)UDrotationCurrent     +initialRot.x; // pitch
        transform.rotation.y = (float)roationXZ/2.0f        +initialRot.y; // yaw
        transform.rotation.z = (float)LRrotationCurrent*1.1f+initialRot.z; // roll
        
        // Movement rotation
        mx.rotate(-LRrotationCurrent, 0, 0, 1.0f);
        mx.rotate(-UDrotationCurrent, 1.0f, 0, 0);
        mx.rotate(-roationXZ/2.0f, 0, 1.0f, 0);
        forward = mx.Mult( FORWARD );
        up = mx.Mult( UP );
        
        // Consider gravity
        acceleration = acceleration.add( GRAVITY );
        
        // V = V + (A-F)*h
        velocity = velocity.add(acceleration.sub(friction).mul(h));
        
        //System.out.println("A: "+ acceleration + " V: "+ velocity + velocity.norm());
        
        // Move
        transform.position = transform.position.add(velocity.mul(h));
    }
    
    @Override
    public void draw()
    {
        // DEBUG LINE
        modelMatrix.loadIdentity();
        modelMatrix.bind();
        Vector3 b = transform.position;
        Vector3 e = transform.position.add(forward.normalize().mul(15.0f) );
        Line l = new Line( b, e);
        l.init(SkyRacers.hdl().gl, SkyRacers.hdl().shader);
        l.bind();
        l.draw();
        
        // Transform Model Matrix
        modelMatrix.loadIdentity();
        modelMatrix.translate(transform.position.x, transform.position.y, transform.position.z);
        modelMatrix.rotate(transform.rotation.y, 0, 1.0f, 0);
        modelMatrix.rotate(transform.rotation.x, 1.0f, 0, 0);
        modelMatrix.rotate(transform.rotation.z, 0, 0, 1.0f);
        modelMatrix.scale(transform.scale.x, transform.scale.y, transform.scale.z);
        modelMatrix.bind();
        
        lm.ActiveMeshDraw();
        mesh.ActiveMeshDraw();
    }

}
