/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michael
 */
public final class BackGroundManager {

    private static Map<String, Sprite> sprites;
    private static Map<String, String> spriteFiles;
    private static boolean validState = false;

    public static Sprite get(String name) {
        validateState();
        return sprites.get(name);
    }

    public static void add(String name, String path) {
        validateState();
        spriteFiles.put(name, path);
        initSprite(name, path);
    }

    public static void remove(String name) {
        validateState();
        spriteFiles.remove(name);
        sprites.remove(name);
    }

    public static void init() {
        validState = true;
        sprites = new HashMap<String, Sprite>();
        spriteFiles = new HashMap<String, String>();
    }

    private static void initSprites() {
        for (Map.Entry<String, String> entry : spriteFiles.entrySet()) {
            initSprite(entry.getKey(), entry.getValue());
        }
    }

    private static void initSprite(String name, String path) {
        sprites.put(name, new Sprite(new Texture(path)));
        
    }

    public static void load(String path) {
        sprites = new HashMap<String, Sprite>();
        System.out.println("load = " + path);

        ObjectInputStream in = null;
        try {
            System.out.println("loading");
            in = new ObjectInputStream(Gdx.files.internal(path).read());
            System.out.println("test2");
            spriteFiles = (HashMap<String, String>) in.readObject();
            initSprites();
            validState = true;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BackGroundManager.class.getName()).log(Level.SEVERE, null, ex);
            Gdx.app.exit();
        } catch (IOException ex) {
            Logger.getLogger(BackGroundManager.class.getName()).log(Level.SEVERE, null, ex);
            Gdx.app.exit();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BackGroundManager.class.getName()).log(Level.SEVERE, null, ex);
            Gdx.app.exit();
        } finally {
            try {
                if (in != null){
                    in.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(BackGroundManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void save(String path) {
        ObjectOutputStream out = null;
        try {
            validateState();
            out = new ObjectOutputStream(
                    Gdx.files.internal(path).write(false)
            );
            out.writeObject(spriteFiles);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(BackGroundManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void validateState() throws IllegalStateException {
        if (!validState) {
            throw new IllegalStateException("BackGroundManager: Call load(String path) or init() first!");
        }
    }
}
