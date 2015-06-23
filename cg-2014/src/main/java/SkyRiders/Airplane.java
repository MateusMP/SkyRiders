/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRiders;

import MathClasses.Transform;
import SkyRiders.core.GameObject;
import MathClasses.Vector3;
import SkyRiders.core.GameRenderer.RENDER_TYPE;
import SkyRiders.core.LODMesh;
import SkyRiders.core.Line;
import Handlers.MeshHandler;
import SkyRiders.core.MeshRenderer;
import Handlers.ShaderHandler;
import Renderers.TexturedMesh;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

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
    private boolean hasGravity = true;
    
    // Propeller
    private TexturedMesh om;
    private LODMesh lm;
    private float rotationPropellerCurrent;
    
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
    // final float MAX_UD = 70; // Direcao vertical
    final float MAX_LR = 70; // Direcao horizontal
    final float MAX_SPEED = 6;
    
    Clip propellerSound;
    
    public static Clip CreateSound(String filename)
    {
        try
        {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(filename)));
            
            return clip;
        }
        catch (Exception exc)
        {
            exc.printStackTrace(System.out);
        }
        
        return null;
    }
    
    public Airplane(Transform t, MeshRenderer mesh)
    {
        super(t, mesh);
        //propellerSound = CreateSound("Assets/propeller.wav");
        //propellerSound.setLoopPoints(0, -1);
        
        name = "Airplane";
        
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
        
        om = new TexturedMesh(MeshHandler.LoadMesh("./Assets/graphics/cartoonAriplanePropeller.obj", "Helice", ShaderHandler.generalShader), ShaderHandler.generalShader);
        lm = new LODMesh(om);
        rotationPropellerCurrent = 0;
                
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
    public void CommandGravity(boolean pressed){
        hasGravity = !hasGravity;
        velocity = new Vector3(0,0,0);
    }
    
    private void handleRotation()
    {
        if (cmd_up){
            UDrotationCurrent += pitchSpeed;
            if (UDrotationCurrent > 360)
                UDrotationCurrent -= 360;
            
            acceleration = acceleration.add( up.neg().mul(current_accel/5.0f) );
        } else if (cmd_down){
            UDrotationCurrent -= pitchSpeed;
            if (UDrotationCurrent < 0)
                UDrotationCurrent += 360;
            
            acceleration = acceleration.add( up.mul(current_accel/5.0f) );
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
               
        /*if (propellerSound.isRunning()){
            FloatControl gainControl = (FloatControl) propellerSound.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue( Math.max(Math.min(gainControl.getMaximum(), current_accel/30), 1) );
        }*/
        
        if ( current_accel < 0.01f ){
            current_accel = 0.0f;
            //propellerSound.stop();
        } //else {
            //if (!propellerSound.isRunning())
            //propellerSound.loop(Clip.LOOP_CONTINUOUSLY);
        //}

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
        
        if(hasGravity)
        {
            // Consider gravity
            acceleration = acceleration.add( GRAVITY );
        }
        // V = V + (A-F)*h
        velocity = velocity.add(acceleration.sub(friction).mul(h));
        
        //System.out.println("A: "+ acceleration + " V: "+ velocity + velocity.norm());
        
        // Propeller
        rotationPropellerCurrent += velocity.norm()*0.5f;
        
        if(rotationPropellerCurrent > 360){
            rotationPropellerCurrent -= 360;
        }
        
        // Move
        transform.position = transform.position.add(velocity.mul(h));
        transform.Invalidate();
    }
    
    @Override
    public void draw()
    {
        Matrix4 modelMatrix = new Matrix4();
        Matrix4 matrixReloaded = new Matrix4();
        // DEBUG LINE
        modelMatrix.loadIdentity();
        ShaderHandler.generalShader.LoadModelMatrix(modelMatrix);
//        modelMatrix.bind();
        Vector3 b = transform.position;
        Vector3 e = transform.position.add(forward.normalize().mul(15.0f) );
        Line l = new Line( b, e);
        l.init(SkyRiders.gl, ShaderHandler.generalShader);
        l.bind();
        l.draw();
        
        // Transform Model Matrix
        modelMatrix.loadIdentity();
        modelMatrix.translate(transform.position.x, transform.position.y, transform.position.z);
        modelMatrix.rotate(transform.rotation.y, 0, 1.0f, 0);
        modelMatrix.rotate(transform.rotation.x, 1.0f, 0, 0);

        matrixReloaded.copyFrom(modelMatrix);
        
        modelMatrix.rotate(transform.rotation.z + rotationPropellerCurrent, 0, 0, 1.0f);
        modelMatrix.scale(transform.scale.x, transform.scale.y, transform.scale.z);
        ShaderHandler.generalShader.LoadModelMatrix(modelMatrix);
        ShaderHandler.generalShader.LoadDiffuseTexture(null);
        ShaderHandler.generalShader.LoadNormalTexture(null);
        lm.ActiveMeshDraw();
        
        matrixReloaded.rotate(transform.rotation.z, 0, 0, 1.0f);
        matrixReloaded.scale(transform.scale.x, transform.scale.y, transform.scale.z);
        
        ShaderHandler.generalShader.LoadDiffuseTexture(mesh.getActiveMesh().getTexture());
        ShaderHandler.generalShader.LoadModelMatrix(matrixReloaded);
        mesh.ActiveMeshDraw();
    }

}
