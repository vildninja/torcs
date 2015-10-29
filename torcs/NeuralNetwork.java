/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package torcs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Jannek
 */
public class NeuralNetwork {
    
    public final Queue<Node> updateQueue;
    public final Queue<Node> backQueue;
    
    private final HashMap<Node, Integer> data;
    
    public class Node
    {
        public int id;
        public final HashMap<Node, Double> values = new HashMap<>();
        public final HashMap<Node, Double> weights = new HashMap<>();
        
        private boolean marked = false;
        
        public void SetValue(Node node, double value)
        {
            values.put(node, value);
            
            Mark();
        }
        
        private void Mark()
        {
            if (!marked)
            {
                marked = true;
                updateQueue.add(this);
            }
        }
        
        public void Activate()
        {
            marked = false;
            double value = ReadValue();
            for (Map.Entry<Node, Double> edge : weights.entrySet()) {
                Node node = edge.getKey();
                double weight = edge.getValue();
                node.SetValue(this, value * weight);
            }
        }
        
        public double ReadValue()
        {
            
            double value = 0;
            
            for (double v : values.values()) {
                value += v;
            }
            
            double activator = 1 / Math.pow(1 + 0.5 * Math.pow(Math.E, -value), 2);
            
            //System.out.println("Node " + id + " value = " + value + " activator = " + activator + " from " + values.size() + " children");
            
            return activator;
        }
        
        public void SetDesired(double desired, double rate)
        {
            // make sure to update marked elements
            Update();
            
            // clear backpropagation queue and start with this
            backQueue.clear();
            Backpropagate(this, desired, rate);
            while (!backQueue.isEmpty()) {
                backQueue.poll().Backpropagate(this, desired, rate);
            }
        }
        
        private void Backpropagate(Node top, double desired, double rate)
        {
            for (Node back : values.keySet()) {
                if (back == null)
                    continue;
                
                double before = desired - top.ReadValue();
                values.put(back, back.ReadValue() * (back.weights.get(this) + rate));
                Mark();
                Update();
                double after = desired - top.ReadValue();
                
                if (Math.abs(after) < Math.abs(before)) {
                    back.weights.put(this, back.weights.get(this) + rate);
                }
                else {
                    values.put(back, back.ReadValue() * (back.weights.get(this) - rate));
                    back.weights.put(this, back.weights.get(this) - rate);
                    Mark();
                    Update();
                }
                
                backQueue.add(back);
            }
        }
    }

    public NeuralNetwork(Node[] input, Node[] output, int layers) {
        updateQueue = new LinkedList<>();
        backQueue = new LinkedList<>();
        
        data = new HashMap<>();
        
        int i = 0;
        
        for (int j = 0; j < input.length; j++) {
            Node in = new Node();
            in.id = ++i;
            data.put(in, i);
            input[j] = in;
        }
        
        for (int j = 0; j < output.length; j++) {
            Node out = new Node();
            out.id = ++i;
            data.put(out, i);
            output[j] = out;
        }
        
        List<Node> last = Arrays.asList(input);
        
        for (int j = 0; j < layers; j++) {
            List<Node> layer = new LinkedList<>();
            for (int k = 0; k < last.size(); k++) {
                Node n = new Node();
                n.id = ++i;
                data.put(n, i);
                for (Node l : last) {
                    l.weights.put(n, Math.random() * 2 - 1);
                }
                layer.add(n);
            }
            
            last = layer;
        }
        
        for (int j = 0; j < output.length; j++) {
            for (Node l : last) {
                l.weights.put(output[j], Math.random() * 2 - 1);
            }
        }
        
        SaveJSON("random");
    }
    
    public void Update()
    {
        while (!updateQueue.isEmpty()) {
            Node node = updateQueue.poll();
            node.Activate();
        }
    }
        
    public void SaveJSON(String name)
    {
        JSONObject json = new JSONObject();
        JSONArray nodes = new JSONArray();
        
        json.put("name", name);
        json.put("network", nodes);
        
        for (Map.Entry<Node, Integer> set : data.entrySet()) {
            NeuralNetwork.Node node = set.getKey();
            int id = set.getValue();
            JSONObject entry = new JSONObject();
            JSONArray weights = new JSONArray();
            
            entry.put("id", id);
            entry.put("edges", weights);
            
            nodes.put(entry);
            
            for (Map.Entry<Node, Double> edge : node.weights.entrySet()) {
                NeuralNetwork.Node n = edge.getKey();
                double weight = edge.getValue();
                int nid = data.get(n);
                
                JSONObject e = new JSONObject();
                e.put("id", nid);
                e.put("w", weight);
                weights.put(e);
            }
        }
        
        System.out.println(json.toString());
    }
    
    
}
