/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRiders;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AirplaneController extends KeyAdapter {
    
    private Airplane myplane;
    
    public AirplaneController(Airplane plane)
    {
        myplane = plane;
    }
    
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_UP://gira sobre o eixo-x
                myplane.CommandUP(true);
                break;
            case KeyEvent.VK_DOWN://gira sobre o eixo-x
                myplane.CommandDOWN(true);
                break;
            case KeyEvent.VK_LEFT:
                myplane.CommandLEFT(true);
                break;
            case KeyEvent.VK_RIGHT:
                myplane.CommandRIGHT(true);
                break;
            case KeyEvent.VK_A:
                myplane.CommandAccel(true);
                break;
            case KeyEvent.VK_Z:
                myplane.CommandBrake(true);
                break;
            case KeyEvent.VK_SPACE://Desativa gravidade
                myplane.CommandGravity(true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_UP:
                myplane.CommandUP(false);
                break;
            case KeyEvent.VK_DOWN:
                myplane.CommandDOWN(false);
                break;
            case KeyEvent.VK_LEFT:
                myplane.CommandLEFT(false);
                break;
            case KeyEvent.VK_RIGHT:
                myplane.CommandRIGHT(false);
                break;
            case KeyEvent.VK_A:
                myplane.CommandAccel(false);
                break;
            case KeyEvent.VK_Z:
                myplane.CommandBrake(false);
                break;
            case KeyEvent.VK_H:
                myplane.hide = !myplane.hide;
                break;
        }
        
    }
}
