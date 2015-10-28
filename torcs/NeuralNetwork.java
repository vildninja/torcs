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
            
            return 1 / Math.pow(1 + 0.5 * Math.pow(Math.E, value), 2);
        }
        
        public void Backpropagate(Node node, double towards)
        {
            if (weights.containsKey(node))
            {
                weights.put(node, weights.get(node) + towards);
            }
        }
    }

    public NeuralNetwork(Node[] input, Node[] output, int layers) {
        this.updateQueue = new LinkedList<>();
        
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
