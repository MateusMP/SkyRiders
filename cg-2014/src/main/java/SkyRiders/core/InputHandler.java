/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkyRiders.core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class InputHandler extends KeyAdapter {
    
    private ArrayList<KeyAdapter> handlers;
    
    public InputHandler()
    {
        handlers = new ArrayList<KeyAdapter>();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
            break;
        }
        
        for (KeyAdapter a : handlers)
        {
            a.keyPressed(e);
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        for (KeyAdapter a : handlers)
        {
            a.keyReleased(e);
        }
    }
    
    public void AddHandler(KeyAdapter adapter)
    {
        handlers.add(adapter);
    }
    
    public void RemoveHandler(KeyAdapter adapter)
    {
        handlers.remove(adapter);
    }

}
