/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mlh.game;

import com.badlogic.gdx.utils.Json;
import com.mlh.actors.Actor;
import com.mlh.models.*;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michael
 */
public class FileManager {

    public static final String ACTOR = "core/assets/data/actor/";
    public static final String ACTOR_EXT = ".act";
    public static final String ANCHOR_SPRITE = "core/assets/data/anchorSprites/";
    public static final String ANCHOR_SPRITE_EXT = ".ans";
    public static final String PROJECTILES = "core/assets/data/projectiles/";
    public static final String PROJECTILES_EXT = ".prj";
    public static final String PATHS = "core/assets/data/paths/";
    public static final String PATHS_EXT = ".pth";
    public static final String SHIPS = "core/assets/data/ships/";
    public static final String SHIPS_EXT = ".shp";
    public static final String WEAPONS = "core/assets/data/weapons/";
    public static final String WEAPONS_EXT = ".wpn";
    
    public static final String DATA = "core/assets/data/";
    public static final String FONTS = "core/assets/data/fonts/";
    public static final String PHRASES = "core/assets/data/phrases/";
    public static final String SOUNDS = "core/assets/data/sounds/";
    public static final String TEXTURES = "core/assets/data/textures/";

    private static final String SPACE = " ";
    private static final String UNDERSCORE = "_";

    private static final Json json;

    static {
        json = new Json();
    }
    public static String generatePathFileName(Object object) {
        StringBuilder sb = new StringBuilder();

        // switch using string not supported in 1.6 =(
        if (object instanceof Actor) {
            sb.append(ACTOR);
            sb.append(getName(((Actor) object).getName()));
            sb.append(ACTOR_EXT);
        }

        if (object instanceof AnchorSprite) {
            sb.append(ANCHOR_SPRITE);
            sb.append(getName(((AnchorSprite) object).getName()));
            sb.append(ANCHOR_SPRITE_EXT);
        }

        if (object instanceof Projectile) {
            sb.append(PROJECTILES);
            sb.append(getName(((Projectile) object).getName()));
            sb.append(PROJECTILES_EXT);
        }

        if (object instanceof SpaceShip) {
            sb.append(SHIPS);
            sb.append(getName(((SpaceShip) object).getName()));
            sb.append(SHIPS_EXT);
        }

        if (object instanceof Weapon) {
            sb.append(WEAPONS);
            sb.append(getName(((Weapon) object).getName()));
            sb.append(WEAPONS_EXT);
        }

        if (object instanceof Path_Old) {
            sb.append(PATHS);
            sb.append(getName(((Path_Old) object).getName()));
            sb.append(PATHS_EXT);
        }

        return sb.toString();
    }

    private static String getName(String name) {
        return name.replace(SPACE, UNDERSCORE);
    }

    public static String generatePathFileName(String path, String fileName, String ext) {
        return path.concat(fileName).concat(ext);
    }

    public static void writeJsonFile(String filePathName, Object object) {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePathName), "utf-8"));
            writer.write(json.prettyPrint(object));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
