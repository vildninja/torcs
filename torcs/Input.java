/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package torcs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import javax.swing.JFrame;
import scr.Action;
import scr.Controller;
import scr.SensorModel;

/**
 *
 * @author Jannek
 */
public class Input extends Controller implements KeyListener {

    private final HashMap<Integer, Boolean> keysDown;
    private JFrame frame;
    
    private int gear = 1;

    public Input() {
        keysDown = new HashMap<>();
        
        frame = new JFrame("TORCS INPUT");
        frame.addKeyListener(this);
        frame.setVisible(true);
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
        action.brake = GetKey(KeyEvent.VK_S) + GetKey(KeyEvent.VK_DOWN);
        
        action.steering = GetKey(KeyEvent.VK_A) + GetKey(KeyEvent.VK_LEFT)
                - GetKey(KeyEvent.VK_D) - GetKey(KeyEvent.VK_RIGHT);
        
        action.gear = gear;
        
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
    public void keyTyped(KeyEvent e)
    {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysDown.put(e.getKeyCode(), Boolean.TRUE);
        
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_N:
                gear = Math.max(-1, gear - 1);
                break;
            case KeyEvent.VK_M:
                gear = Math.min(6, gear + 1);
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysDown.put(e.getKeyCode(), Boolean.FALSE);
    }
    
}
