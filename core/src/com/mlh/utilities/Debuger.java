/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.utilities;

/**
 *
 * @author michael
 */
public class Debuger {

    public static boolean debugOn = false;
    public static int debugLevel = 0;

    public static void print(int level, String who, String what, String data) {
        if (debugOn && level >= debugLevel) {
            System.out.println("\"" + who + "\"" + "\"" + what + "\" > " + "\"" + data + "\"");
        }
    }
}
