/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package torcs;

import scr.Action;
import scr.Controller;
import scr.SensorModel;

/**
 *
 * @author Jannek
 */
public class SimpleNeuralNetwork extends Controller {

    private final double[][] weights;
    private final double[] range;
    private final double[] output;
    private final double[] controls;
    
    //private NeuralNetwork.Node[] input;
    //private NeuralNetwork.Node[] output;
    //private NeuralNetwork network;
        
    private final Input input;
    
    public SimpleNeuralNetwork()
    {
        // forward, left, right
        controls = new double[3];
        output = new double[3];
        
        weights = new double[][] {
                {2, 1, 1, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 1, 1, 2},
                {0, 0, 0, 0, 0, 0, 0, 0, 0,  0, 1, 1, 1, 1, 2, 2, 2, 3, 3},
                {3, 3, 2, 2, 2, 1, 1, 1, 1,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        
        range = new double[] {
            10, 11, 12, 14, 18, 22, 28, 40, 60, 200, 60, 40, 28, 22, 18, 14, 12, 11, 10
        };
        
        input = new Input();
        
        //NeuralNetwork.Node[] input = new NeuralNetwork.Node[20];
        //NeuralNetwork.Node[] output = new NeuralNetwork.Node[3];
        //NeuralNetwork network = new NeuralNetwork(input, output, 1);
        
    }
    
    
    int count = 0;
    @Override
    public Action control(SensorModel sensors) {
        
        double[] rays = sensors.getTrackEdgeSensors();
        
        
        for (int i = 0; i < controls.length; i++) {
            output[i] = 0;
        }
        
        for (int i = 0; i < 19; i++) {
            double norm = Math.max(0, 1 - rays[i] / range[i]);
            for (int j = 0; j < controls.length; j++) {
                output[j] += norm * weights[j][i];
            }
        }
        
        for (int i = 0; i < controls.length; i++) {
            controls[i] = output[i];
        }
        
        
        if (count++%10 == 0)
        {
            String txt = controls[0] + " " + controls[1] + " " + controls[2] +
                    "\n" + rays[0] + " " + rays[3] + " " +
                    rays[9] + " " + rays[15] + " " + rays[18];

            System.out.println(txt);
        }
        
        
        Action action = new Action();
        action.gear = 2;
        action.accelerate = controls[0];
        action.steering = controls[1] - controls[2];
        return action;
    }

    @Override
    public void reset() {
        
    }

    @Override
    public void shutdown() {
        
    }
    
}
