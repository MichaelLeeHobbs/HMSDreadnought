/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.managers;

import com.mlh.interfaces.IMessageable;
import com.mlh.models.GameMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 * @author michael
 */
public class PriorityMessageQueue {
    private Comparator<GameMessage> comparator;
    private List<IMessageable> mailBoxes;
    

    public PriorityMessageQueue() {
        PriorityQueue<GameMessage> messageQueue = new PriorityQueue<GameMessage>();
        mailBoxes = new ArrayList<IMessageable>();
    }
    
    public void Update(float dt) {
        
    }
    
    public void addMailBox(IMessageable mailBox){
        mailBoxes.add(mailBox);
    }
    
    public void removeMailBox(IMessageable mailBox) {
        mailBoxes.remove(mailBox);
    }
    
}
