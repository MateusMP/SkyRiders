/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRiders;

import MathClasses.Vector3;
import SkyRiders.core.Camera;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
    
public class StandardCamera extends KeyAdapter implements Camera {
    
    Vector3 position;
    Vector3 lookat;
    Vector3 up;
    Airplane following;
    
    Vector3 speed;
    final Vector3 offset = new Vector3(0,0,10);
    
    public StandardCamera(Vector3 pos)
    {
        speed = new Vector3();
        position = pos;
        lookat = new Vector3();
        up = new Vector3(0, 1, 0);
        
    }
    
    @Override
    public void DefineViewMatrix(Matrix4 viewMatrix)
    {
        position = position.add(speed);
        lookat = position.add(offset);
        
        viewMatrix.loadIdentity();
        viewMatrix.lookAt(position.x, position.y, position.z,
                        lookat.x, lookat.y, lookat.z,
                        0,1,0);
                       //following.top.x/1.2f, following.top.y*1.6f, following.top.z/1.2f); 
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_I:
                speed.z = -3;
                break;
            case KeyEvent.VK_K:
                speed.z = 3;
                break;
            case KeyEvent.VK_J:
                speed.x = -3;
                break;
            case KeyEvent.VK_L:
                speed.x = 3;
                break;
            case KeyEvent.VK_U:
                speed.y = 2;
                break;
            case KeyEvent.VK_N:
                speed.y = -2;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_I:
                speed.z = 0;
                break;
            case KeyEvent.VK_K:
                speed.z = 0;
                break;
            case KeyEvent.VK_J:
                speed.x = 0;
                break;
            case KeyEvent.VK_L:
                speed.x = 0;
                break;
            case KeyEvent.VK_U:
                speed.y = 0;
                break;
            case KeyEvent.VK_N:
                speed.y = 0;
                break;
        }
        
    }

   

    @Override
    public Vector3 GetPosition() {
        return position;
    }
    @Override
    public Vector3 getLookat() {
        return lookat;
    }
    @Override
    public Vector3 getUp() {
        return up;
    }

    @Override
    public Vector3 getLookatNormalized() {
        return offset;
    }
}

