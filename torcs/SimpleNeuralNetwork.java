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

    private NeuralNetwork.Node[] input;
    private NeuralNetwork.Node[] output;
    private NeuralNetwork network;
    
    private final double[] range;
        
    private final Input controller;
    
    public SimpleNeuralNetwork()
    {
        // forward, left, right
        
        controller = new Input();
        
        input = new NeuralNetwork.Node[20];
        output = new NeuralNetwork.Node[3];
        network = new NeuralNetwork(input, output, 1);
        
        range = new double[] {
            10, 11, 12, 14, 18, 22, 28, 40, 60, 200, 60, 40, 28, 22, 18, 14, 12, 11, 10
        };
    }
    
    
    int count = 0;
    @Override
    public Action control(SensorModel sensors) {
        
        double[] rays = sensors.getTrackEdgeSensors();
        
        for (int i = 0; i < 19; i++) {
            double norm = Math.max(0, 1 - rays[i] / range[i]);
            input[i].SetValue(null, norm);
        }
        input[19].SetValue(null, sensors.getAngleToTrackAxis());
        
        network.Update();
        
        
        
        Action action = controller.control(sensors);
        
        if (controller.isRecording)
        {
            output[0].SetDesired(action.accelerate, 0.01);
            output[1].SetDesired(Math.max(action.steering, 0), 0.01);
            output[2].SetDesired(Math.max(-action.steering, 0), 0.01);

            if (count++%50 == 0)
            {
                String txt = output[0].ReadValue() + " " + output[1].ReadValue() + " " + output[2].ReadValue() +
                        "\n" + action.accelerate + " " + Math.max(action.steering, 0) + " " + Math.max(-action.steering, 0);

                System.out.println(txt);
            }
        }
        else
        {
            action.accelerate = output[0].ReadValue();
            action.steering = output[1].ReadValue() - output[2].ReadValue();
        }
        
        return action;
    }

    @Override
    public void reset() {
        
    }

    @Override
    public void shutdown() {
        
    }
    
}
