/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRacers;

import SkyRacers.core.GameObject;
import MathClasses.Vector3;
import static SkyRacers.SkyRacers.modelMatrix;
import SkyRacers.core.Line;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.matrix.Matrix4;

public class Airplane extends GameObject {
    
    private boolean cmd_up;
    private boolean cmd_down;
    private boolean cmd_right;
    private boolean cmd_left;
    private boolean cmd_accel;
    private boolean cmd_brake;
    
    Vector3 brake_speed;
    Vector3 velocity;
    float speed;
    float accel;
    float brakespeed;
    Vector3 direction;  // forward direction
    Vector3 top;
    final Vector3 UP = new Vector3(0,1,0);
    final Vector3 FORWARD = new Vector3(0,0,1);
    
    float LRrotationTarget;
    float LRrotationCurrent;
    float UDrotationCurrent;
    float roationXZ;
    float rollSpeed;
    float pitchSpeed;
    
    final float MAX_UD = 70; // Direcao vertical
    final float MAX_LR = 70; // Direcao horizontal
    
    public Airplane(JWavefrontObject mesh)
    {
        super(new Vector3(-5,3,-5), mesh);
        
        cmd_up = false;
        cmd_down = false;
        cmd_right = false;
        cmd_left = false;
        cmd_accel = false;
        cmd_brake = false;
        
        accel = 0.005f;
        speed = 0.0f;
        brakespeed = 0.002f;
        velocity = new Vector3();
        
        LRrotationTarget = 0.0f;
        LRrotationCurrent = 0.0f;
        UDrotationCurrent = 0.0f;
        pitchSpeed = 0.7f;
        rollSpeed = 1.3f;
        
        direction = FORWARD;
        roationXZ = 0;
        
        transform.scale.x = transform.scale.y = transform.scale.z = 0.5f;
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
    
    private float handleAcceleration()
    {
        float fabsUD = (float)Math.abs(UDrotationCurrent);
        
//        System.out.println("Amount: "+(accel/((fabsUD+MAX_LR*10)/MAX_LR)));
        if ( cmd_accel ){
            if ( UDrotationCurrent <= 0){
                speed += (float) (accel/((fabsUD+MAX_LR)/MAX_LR));
            } else {
                speed += (float) (accel*((MAX_LR+UDrotationCurrent/3.0f)/MAX_LR));
            }
        } else if (cmd_brake) {
            speed -= brakespeed;
        } else {
            speed -= brakespeed/2;
        }
        
        if (speed > 2)
            speed = 2;
        else if (speed < 0)
            speed = 0;
        
        return speed;
        
        /*if ( cmd_accel ){
            if ( UDrotationCurrent <= 0){
                velocity = velocity.add(new Vector3(accel, (float)((float)accel*Math.abs(UDrotationCurrent/MAX_LR)), accel));
            } else {
                velocity = velocity.add( new Vector3(accel, (float) (accel*((MAX_LR+UDrotationCurrent/3.0f)/MAX_LR)), accel));
            }
        } else if (cmd_brake) {
            if ( velocity.x*velocity.x+velocity.y*velocity.y > 0 ){
                velocity = velocity.sub(brake_speed);
            }
        }*/
    }
    
    @Override
    public void update()
    {
        handleRotation();
        float speed = handleAcceleration();
                
        Matrix4 mx = new Matrix4();
        mx.loadIdentity();
        
        // Visual
        transform.rotation.x =(float)UDrotationCurrent; // pitch
        transform.rotation.y = (float)roationXZ; // yaw
        transform.rotation.z = (float)LRrotationCurrent*1.2f; // roll
        
        // Calcular rotacao para movimento
        mx.rotate(-LRrotationCurrent, 0, 0, 1.0f);
        mx.rotate(-transform.rotation.x, 1.0f, 0, 0);
        mx.rotate(-transform.rotation.y, 0, 1.0f, 0);
        direction = mx.Mult( FORWARD );
        top = mx.Mult( UP );
        
        float vy = velocity.y;
        velocity = direction.mul(speed/10.0f).add( new Vector3(0, -0.016f, 0) );
        if (vy < 0){
            velocity.y += vy/1.5;
        }
        
//        System.out.println(speed + " - " + velocity);
        
        // Move
        transform.position = transform.position.add(velocity);
                
    }
    
    @Override
    public void draw()
    {
        modelMatrix.loadIdentity();
        modelMatrix.bind();
        Vector3 b = transform.position;
        Vector3 e = transform.position.add( direction.normalize().mul(15.0f) );
        Line l = new Line( b, e);
        l.init(SkyRacers.hdl().gl, SkyRacers.hdl().shader);
        l.bind();
        l.draw();
        
//        modelMatrix.loadIdentity();
//        modelMatrix.bind();
//        b = getPosition().clone();
//        e = getPosition().add( top.normalize().mul(1.0f) );
//        l = new Line( b, e);
//        l.init(SkyRacers.hdl().gl, SkyRacers.hdl().shader);
//        l.bind();
//        l.draw();
        
        modelMatrix.loadIdentity();
        modelMatrix.translate(transform.position.x, transform.position.y, transform.position.z);
        modelMatrix.rotate(transform.rotation.y, 0, 1.0f, 0);
        modelMatrix.rotate(transform.rotation.x, 1.0f, 0, 0);
        modelMatrix.rotate(transform.rotation.z, 0, 0, 1.0f);
        modelMatrix.scale(transform.scale.x, transform.scale.y, transform.scale.z);
        modelMatrix.bind();
        
        mesh.ActiveMeshDraw();
    }
}
