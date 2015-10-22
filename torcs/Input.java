/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package torcs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import scr.Action;
import scr.Controller;
import scr.SensorModel;

/**
 *
 * @author Jannek
 */
public class Input extends Controller implements KeyListener {

    private final HashMap<Integer, Boolean> keysDown;

    public Input() {
        keysDown = new HashMap<>();
    }
    
    private int GetKey(int keyCode)
    {
        if (keysDown.containsKey(keyCode))
            return keysDown.get(keyCode) ? 1 : 0;
        return 0;
    }
    
    @Override
    public Action control(SensorModel sensors) {
        Action action = new Action();
        action.accelerate = GetKey(KeyEvent.VK_W) + GetKey(KeyEvent.VK_UP);
        action.accelerate = GetKey(KeyEvent.VK_W) + GetKey(KeyEvent.VK_UP);
        
        
        return action;
    }

    @Override
    public void reset() {
        System.out.println("Restarting the race!");
		
    }

    @Override
    public void shutdown() {
        System.out.println("Bye bye!");		
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysDown.put(e.getKeyCode(), Boolean.TRUE);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysDown.put(e.getKeyCode(), Boolean.FALSE);
    }
    
}
