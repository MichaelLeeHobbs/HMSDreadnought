/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author michael
 */
public class Jukebox {

    private static final HashMap<String, Sound> sounds;
    private static final ArrayList<String> soundsLooping;

    static {
        sounds = new HashMap<String, Sound>();
        soundsLooping = new ArrayList<String>();
    }

    public static void load(String path, String name) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
        sounds.put(name, sound);
    }

    public static void play(String name) {
        sounds.get(name).play();
    }

    public static void loop(String name) {
        if (!soundsLooping.contains(name)) {
            sounds.get(name).loop();
            soundsLooping.add(name);
        }
    }

    public static void stop(String name) {
        sounds.get(name).stop();
        soundsLooping.remove(name);
    }

    public static void stopAll() {
        for (Sound s : sounds.values()) {
            s.stop();
        }
        soundsLooping.clear();
    }
}
