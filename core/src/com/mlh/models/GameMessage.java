/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.models;

import java.util.Comparator;

/**
 *
 * @author michael
 */
public class GameMessage implements Comparator<GameMessage> {
    public int priority;
    public Object sender;
    public Object reciver;
    public float delay;
    public String message;
    public float data;

    @Override
    public int compare(GameMessage msg1, GameMessage msg2) {
        if (msg1.priority < msg2.priority) { return -1; }
        if (msg1.priority > msg2.priority) { return 1; }
        return 0;
    }
}
