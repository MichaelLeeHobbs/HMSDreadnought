/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mlh.managers;

import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author michael
 */
public class GameKeys {
    private static final boolean[] keys;              // state of keys
    private static final boolean[] pkeys;             // previous state of keys
    
    private static final int NUM_KEYS = 8;
    public static final int UP = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int RIGHT = 3;
    public static final int ENTER = 4;
    public static final int ESCAPE = 5;
    public static final int SPACE = 6;
    public static final int SHIFT = 7;
    private static float AXIS_X = 0.0f;
    private static float AXIS_Y = 0.0f;
    public static float AXIS_2 = -1.0f;
    public static boolean isTurning = false;
    public static boolean xTurned = false;
    public static boolean yTurned = false;
    
    public static float getAXIS_X() {
        return AXIS_X;
    }
    public static void setAXIS_X(float AXIS_X) {
        GameKeys.AXIS_X = AXIS_X;
    }
    public static float getAXIS_Y() {
        return AXIS_Y;
    }
    public static void setAXIS_Y(float AXIS_Y) {
        GameKeys.AXIS_Y = AXIS_Y;
    }

    public static Vector2 getHeading(){
        return new Vector2(AXIS_X, AXIS_Y);
    }
    
    public static boolean isHeadingHome() {
        return !xTurned && !yTurned;
    }
    
    static {
        keys = new boolean[NUM_KEYS];
        pkeys = new boolean[NUM_KEYS];
    }
    
    public static void update() {
        System.arraycopy(keys, 0, pkeys, 0, NUM_KEYS);
    }
    
    public static void setKey(int k, boolean b) {
        keys[k] = b;
    }
    
    public static boolean isDown(int k) {
        return keys[k];
    }
    
    public static boolean isPressed(int k) {
        return keys[k] && !pkeys[k];
    }
}
