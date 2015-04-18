/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRacers;

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
            case KeyEvent.VK_LEFT://gira sobre o eixo-y
                myplane.CommandLEFT(true);
                break;
            case KeyEvent.VK_RIGHT://gira sobre o eixo-y
                myplane.CommandRIGHT(true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_UP://gira sobre o eixo-x
                myplane.CommandUP(false);
                break;
            case KeyEvent.VK_DOWN://gira sobre o eixo-x
                myplane.CommandDOWN(false);
                break;
            case KeyEvent.VK_LEFT://gira sobre o eixo-y
                myplane.CommandLEFT(false);
                break;
            case KeyEvent.VK_RIGHT://gira sobre o eixo-y
                myplane.CommandRIGHT(false);
                break;
        }
        
    }
}
